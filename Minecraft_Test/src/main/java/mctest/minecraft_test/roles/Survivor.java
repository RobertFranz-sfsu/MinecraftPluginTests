package mctest.minecraft_test.roles;

import mctest.minecraft_test.Minecraft_Test;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class Survivor implements Listener {
//    private  Boolean sur = false; //if player is survivor
////    private World world;
//
//    public Survivor(Minecraft_Test plugin) {
//        Bukkit.getPluginManager().registerEvents(this, plugin);
//    }
//
//    public void setSurvivor(Player player) {
//
//        if (!this.getSur()) {
//            Bukkit.getLogger().info(player + " is a survivor!");
//
////            world = player.getLocation().getWorld();
////
////            Location sSpawn = new Location(world, 22.5, 67, 37, -180f, -1f);
////            player.teleport(sSpawn);
//
//            Inventory inv = player.getInventory();
//            inv.clear();
//            player.setHealth(20);
//            player.setFoodLevel(20);
//            ItemStack weapon = new ItemStack(Material.TRIDENT, 1);
//            getItem(weapon, "&8Infected Slayer", "&8Kill the infected!");
//            inv.setItem(0, weapon);
//            player.sendMessage("YOU ARE A SURVIVOR!");
//
//        } else {
//            Bukkit.getLogger().info(player + " is no longer a survivor!");
//            Inventory inv = player.getInventory();
//            inv.clear();
//            player.setHealth(20);
//            player.setFoodLevel(20);
//            player.sendMessage("No longer Survivor");
//        }
//        this.setSur();
//    }
//
//    public void setSur() {
//        this.sur = !this.sur;
//    }
//    public boolean getSur() {
//        return sur;
//    }
//    @EventHandler
//    public void onSurvivorDeath(PlayerDeathEvent event) {
//        Player player = event.getEntity();
//        Bukkit.getLogger().info("Survivor player:  " + player + "  has died:  " + getSur());
//        this.setSurvivor(player);
//    }
//    @EventHandler
//    public void onPlayerDisconnect(PlayerQuitEvent event) {
//        Player player = event.getPlayer();
//        Bukkit.getLogger().info("Survivor player:  " + player + "  has disconected:  " + getSur());
//        if (!this.getSur()){
//            this.setSurvivor(player);
//        }
//    }
//
//    @EventHandler
//    public void onPlayerAttack(EntityDamageByEntityEvent event) {
//        Entity attacker = event.getDamager();
//        Entity damaged = event.getEntity();
//        if (attacker instanceof Player && damaged instanceof Player) {
//
//        }
//    }
//
//    private ItemStack getItem(ItemStack item, String name, String ... lore) {
//        ItemMeta meta = item.getItemMeta();
//
//        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
//
//        List<String> lores = new ArrayList<>();
//        for(String s : lore) {
//            lores.add(ChatColor.translateAlternateColorCodes('&', s));
//        }
//        meta.setLore(lores);
//        item.setItemMeta(meta);
//
//        return item;
//    }
}
