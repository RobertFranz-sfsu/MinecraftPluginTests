package mctest.minecraft_test.handlers;
import mctest.minecraft_test.Minecraft_Test;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

public class TorchHandler implements Listener {
    public TorchHandler(Minecraft_Test plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }
    /**
     * Priorities:
     * Lowest
     * Low
     * Normal
     * High
     * Highest
     * ---Monitor
     * */

    @EventHandler(priority = EventPriority.LOW)
    public void onTorchPlace_Low(BlockPlaceEvent event) {
//        if (event.getBlock().getType() == Material.TORCH || event.getBlock().getType() == Material.WALL_TORCH) {
//            event.getBlock().setType(Material.DIAMOND_BLOCK);
//
//            // event.setCancelled(true);
//        }
    }
    @EventHandler
    public void onTorchPlace_Normal(BlockPlaceEvent event) {
        Block block = event.getBlock();

        if (block.getType() != Material.TORCH) {
            return;
        }

        Bukkit.getLogger().info("Torch was placed!");
    }
}
