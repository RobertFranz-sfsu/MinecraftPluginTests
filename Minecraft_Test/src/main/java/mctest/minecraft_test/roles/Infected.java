package mctest.minecraft_test.roles;

import mctest.minecraft_test.Minecraft_Test;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class Infected implements Listener {
//    private Boolean inf = false; //if player is infected
////    private World world;
//
//    public Infected(Minecraft_Test plugin) {
//        Bukkit.getPluginManager().registerEvents(this, plugin);
//    }
//
//    public void setInfection(Player player) {
//        Bukkit.getLogger().info("Infection called from class");
//
//
//        if (!this.getI()) {
//            Bukkit.getLogger().info(player + " has been infected!");
//
////            world = player.getLocation().getWorld();
////
////            Location infSpawn = new Location(world, 22.5, 67, -36, 0f, 0f);
////            player.teleport(infSpawn);
//
//            player.setGlowing(true);
//            player.setWalkSpeed(.6f);
//            player.setHealth(1);
//            player.setFoodLevel(1);
//            Inventory inv = player.getInventory();
//            inv.clear();
//
//            ItemStack weapon = new ItemStack(Material.NETHERITE_SWORD, 1);
//            getItem(weapon, "&9Infected Claw", "&9Infect the uninfected!");
//            inv.setItem(0, weapon);
//            player.sendMessage("YOU ARE INFECTED!");
//
//        } else {
//            Bukkit.getLogger().info(player + " is no longer infected!");
//            player.setGlowing(false);
//            Inventory inv = player.getInventory();
//            inv.clear();
//            player.setHealth(20);
//            player.setFoodLevel(20);
//            player.setWalkSpeed(.2f);
//            player.sendMessage("No longer infected");
//        }
//        this.setI();
//    }
//    @EventHandler
//    public void onInfectedDeath(PlayerDeathEvent event) {
//        Player player = event.getEntity();
//        Bukkit.getLogger().info("infected player:  " + player + "  has died:  " + getI());
//        this.setInfection(player);
//    }
//    @EventHandler
//    public void onPlayerDisconnect(PlayerQuitEvent event) {
//        Player player = event.getPlayer();
//        Bukkit.getLogger().info("infected player:  " + player + "  has disconected:  " + getI());
//        if (!this.getI()){
//            this.setInfection(player);
//        }
//    }
//    public void setI() {
//        this.inf = !this.inf;
//
//    }
//    public boolean getI() {
//        return inf;
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
