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
import com.pragprog.ahmine.ez.EZPlugin;
import net.canarymod.api.factory.CanaryObjectFactory;

public class ShopTest extends EZPlugin {
  
  @Command(aliases = { "shoptest" },
            description = "shoptest plugin",
            permissions = { "*" },
            toolTip = "/shoptest")
  public void shoptestCommand(MessageReceiver caller, String[] parameters) {
    if (caller instanceof Player) { 
      Player me = (Player)caller;

      CanaryObjectFactory of = new CanaryObjectFactory();
      Inventory ci = of.newCustomStorageInventory(2);
      ci.setInventoryName("WELCOME TO THE SHOP!!!");
      me.openInventory(ci);
    }
  }
}
