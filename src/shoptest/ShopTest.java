package shoptest;
import java.text.NumberFormat;
import java.text.ParseException;
import net.canarymod.plugin.Plugin;
import net.canarymod.logger.Logman;
import net.canarymod.Canary;
import net.canarymod.commandsys.*;
import net.canarymod.chat.MessageReceiver;
import net.canarymod.api.nbt.CompoundTag;
import net.canarymod.api.entity.living.humanoid.Player;
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
    String priceStr = price.replaceAll("[^\\d.]+", "");
    NumberFormat nf = NumberFormat.getInstance();
    double p;
    try {
      p = nf.parse(priceStr).doubleValue();
    } catch(ParseException e) {
      return;
    }


    // Show shop
    Player me = event.getPlayer();

    CanaryObjectFactory of = new CanaryObjectFactory();
    Inventory ci = of.newCustomStorageInventory(1);

    ci.setInventoryName("SHOP");

    fillSlot(action, p, ci, it, item, 0, 1);
    fillSlot(action, p, ci, it, item, 1, 2);
    fillSlot(action, p, ci, it, item, 2, 4);
    fillSlot(action, p, ci, it, item, 3, 8);
    fillSlot(action, p, ci, it, item, 4, 16);
    fillSlot(action, p, ci, it, item, 5, 24);
    fillSlot(action, p, ci, it, item, 6, 32);
    fillSlot(action, p, ci, it, item, 7, 48);
    fillSlot(action, p, ci, it, item, 8, 64);

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
    if (i == null) {
      return;
    }

    CompoundTag itemMeta = i.getMetaTag();

    if (!itemMeta.containsKey("shoptype")) {
      // Clicked item in own inventory, return
      return;
    }
    if (!itemMeta.containsKey("price")) {
      return;
    }


    String shopType = itemMeta.getString("shoptype");
    if (!shopType.equals("BUY") && !shopType.equals("SELL")) {
      return; // The shoptype metatag was set to something weird.
    }

    Inventory playerInv = me.getInventory();

    double price = itemMeta.getDouble("price");

    if (shopType.equals("BUY")) {
      playerInv.addItem(i.getId(), i.getAmount());
    } else {
      if (playerInv.hasItemStack(i.getId(), i.getAmount())) {
        playerInv.decreaseItemStackSize(i.getId(), i.getAmount());
      } else {
        // Message player that they don't have enough
      }
    }
  }

  private void fillSlot(String action, double price, Inventory inv, ItemType it, String item, int slot, int count) {

    ItemFactory itemf = Canary.factory().getItemFactory();
    Item i = itemf.newItem(it, 0, count);
    CompoundTag tag = i.getMetaTag();
    tag.put("shoptype", action.toUpperCase());
    tag.put("price", price);

    String name = item.toUpperCase();

    String lore0 = String.format("%s %dx %s", action, count, name);
    String lore1 = String.format("Item: %s", name);
    String lore2 = String.format("Amount: %d", count);
    String lore3 = String.format("Price: $%.2f", price * count);

    i.setLore(lore0, lore1, lore2, lore3, "Click to purchase this item");
    inv.setSlot(slot, i);
  }
}
