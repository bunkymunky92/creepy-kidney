package shoptest;
import net.canarymod.plugin.Plugin;
import net.canarymod.logger.Logman;
import net.canarymod.Canary;
import net.canarymod.commandsys.*;
import net.canarymod.chat.MessageReceiver;
import net.canarymod.api.entity.living.humanoid.Player;
import net.canarymod.api.world.World;
import net.canarymod.api.world.blocks.BlockType;
import net.canarymod.api.world.blocks.Sign;
import net.canarymod.api.world.blocks.Block;
import net.canarymod.api.inventory.Inventory;
import net.canarymod.api.inventory.Item;
import net.canarymod.api.inventory.ItemType;
import net.canarymod.api.chat.ChatComponent;
import net.canarymod.hook.HookHandler;
import net.canarymod.hook.player.SlotClickHook;
import net.canarymod.hook.player.BlockRightClickHook;
import net.canarymod.plugin.PluginListener;
import com.pragprog.ahmine.ez.EZPlugin;
import net.canarymod.api.factory.CanaryObjectFactory;
import net.canarymod.api.factory.ItemFactory;

public class ShopTest extends EZPlugin implements PluginListener {

  @Override
  public boolean enable() {
    Canary.hooks().registerListener(this, this);
    return super.enable();
  }

  @HookHandler
  public void onSignRightClick(BlockRightClickHook event) {
    BlockRightClickHook h = (BlockRightClickHook)event;

    Block b = h.getBlockClicked();
    if (b.getType() != BlockType.StandingSign) {
      return;
    }

    Sign s = (Sign)b.getTileEntity();

    ChatComponent l0 = s.getComponentOnLine(0);
    String firstLine = l0.getText();

    // Shop sign lines
    // [Shop]
    // (Buy|Sell)
    // Item
    // $5/ea
    
    if (!firstLine.equals("[Shop]")) {
      return;
    }

    String action = s.getComponentOnLine(1).getText();
    if (!action.equals("Buy") && !action.equals("Sell")) {
      return; // Not correct sign format
    }

    // Convert to machine name
    String item = s.getComponentOnLine(2).getText();
    String machineName = "minecraft:" + item.toLowerCase();
    ItemType it = ItemType.fromString(machineName);
    if (it == null) {
      // Invalid item type
      return;
    }

    String price = s.getComponentOnLine(3).getText();
    // Check price validity


    // Show shop
    Player me = event.getPlayer();

    CanaryObjectFactory of = new CanaryObjectFactory();
    Inventory ci = of.newCustomStorageInventory(1);
    ci.setInventoryName("SHOP");

    fillSlot(ci, it, item, 0, 1);
    fillSlot(ci, it, item, 1, 2);
    fillSlot(ci, it, item, 2, 4);
    fillSlot(ci, it, item, 3, 8);
    fillSlot(ci, it, item, 4, 16);
    fillSlot(ci, it, item, 5, 24);
    fillSlot(ci, it, item, 6, 32);
    fillSlot(ci, it, item, 7, 48);
    fillSlot(ci, it, item, 8, 64);

    me.openInventory(ci);
  }

  @HookHandler
  public void onSlotClick(SlotClickHook event) {
    Player me = event.getPlayer();
    SlotClickHook h = (SlotClickHook)event;
    Inventory inv = h.getInventory();

    if (inv.getInventoryName() != "SHOP") {
      return;
    }

    event.setCanceled();

    Item i = h.getItem();

    Inventory playerInv = me.getInventory();

    // TODO determine if this shop is buy or sell
    playerInv.addItem(i.getId(), i.getAmount());
  }

  private void fillSlot(Inventory inv, ItemType it, String item, int slot, int count) {
    ItemFactory itemf = Canary.factory().getItemFactory();
    Item i = itemf.newItem(it, 0, count);
    String name = item.toUpperCase();

    String lore0 = String.format("Buy %dx %s", count, name);
    String lore1 = String.format("Item: %s", name);
    String lore2 = String.format("Amount: %d", count);
    String lore3 = String.format("Price: $???");

    i.setLore(lore0, lore1, lore2, lore3, "Click to purchase this item");
    inv.setSlot(slot, i);
  }

}
