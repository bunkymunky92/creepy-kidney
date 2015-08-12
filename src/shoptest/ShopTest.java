package shoptest;
import net.canarymod.plugin.Plugin;
import net.canarymod.logger.Logman;
import net.canarymod.Canary;
import net.canarymod.commandsys.*;
import net.canarymod.chat.MessageReceiver;
import net.canarymod.api.entity.living.humanoid.Player;
import net.canarymod.api.world.World;
import net.canarymod.api.world.position.Location;
import net.canarymod.api.world.blocks.BlockType;
import net.canarymod.api.inventory.Inventory;
import net.canarymod.api.inventory.ItemType;
import net.canarymod.hook.HookHandler;
import net.canarymod.hook.player.InventoryHook;
import net.canarymod.hook.player.SlotClickHook;
import net.canarymod.plugin.PluginListener;
import com.pragprog.ahmine.ez.EZPlugin;
import net.canarymod.api.factory.CanaryObjectFactory;

public class ShopTest extends EZPlugin implements PluginListener {

  @Override
  public boolean enable() {
    Canary.hooks().registerListener(this, this);
    return super.enable();
  }
  
  @Command(aliases = { "shoptest" },
            description = "shoptest plugin",
            permissions = { "*" },
            toolTip = "/shoptest")
  public void shoptestCommand(MessageReceiver caller, String[] parameters) {
    if (caller instanceof Player) { 
      Player me = (Player)caller;

      CanaryObjectFactory of = new CanaryObjectFactory();
      Inventory ci = of.newCustomStorageInventory(1);
      ci.setInventoryName("WELCOME TO THE SHOP!!!");
      ci.setSlot(ItemType.Arrow, 23, 2);
      me.openInventory(ci);
    }
  }

  @HookHandler
  public void onInventory(InventoryHook event) {
    Canary.instance().getServer().broadcastMessage("got an inventory event!");
  }

  @HookHandler
  public void onSlotClick(SlotClickHook event) {
    Canary.instance().getServer().broadcastMessage("got a slot click event!");
    event.setCanceled();
  }
}
