package mctest.minecraft_test.handlers;

import mctest.minecraft_test.Minecraft_Test;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class PlayerHandler implements Listener {
    public PlayerHandler(Minecraft_Test plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        ItemStack dia = new ItemStack(Material.DIAMOND,33);
        ItemStack tor = new ItemStack(Material.TORCH, 3);
        ItemStack test = new ItemStack(Material.GRASS, 1);
        Inventory inv = player.getInventory();

        ItemMeta meta = test.getItemMeta();
        meta.setDisplayName("Secret Item");
        test.setItemMeta(meta);


        inv.setItem(0, tor);
        inv.setItem(1, test);
        inv.addItem(dia);
    }
}
