package mctest.minecraft_test.roles;

import mctest.minecraft_test.Minecraft_Test;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SurvivalPlayer implements Listener{
    private Boolean inf = false; //if player is infected
    private  Boolean sur = false; //if player is survivor
//    private World world;

    public SurvivalPlayer(Minecraft_Test plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public void setRole(Player player, String role) {
        Bukkit.getLogger().info("Infection called from class");

        if (Objects.equals(role, "infected")) {
            Bukkit.getLogger().info(player + " has been infected!");
            this.setInfect(true);
            this.setSurv(false);

//            world = player.getLocation().getWorld();
//
//            Location infSpawn = new Location(world, 22.5, 67, -36, 0f, 0f);
//            player.teleport(infSpawn);

            player.setGlowing(true);
            player.setWalkSpeed(.6f);
            player.setHealth(1);
            player.setFoodLevel(1);
            Inventory inv = player.getInventory();
            inv.clear();

            ItemStack weapon = new ItemStack(Material.NETHERITE_SWORD, 1);
            getItem(weapon, "&9Infected Claw", "&9Infect the uninfected!");
            inv.setItem(0, weapon);
            player.sendMessage("YOU ARE INFECTED!");

        } else if (Objects.equals(role, "survivor")) {
            Bukkit.getLogger().info(player + " is a survivor!");
            this.setInfect(false);
            this.setSurv(true);

//            world = player.getLocation().getWorld();
//
//            Location sSpawn = new Location(world, 22.5, 67, 37, -180f, -1f);
//            player.teleport(sSpawn);

            player.setGlowing(false);
            player.setWalkSpeed(.2f);
            Inventory inv = player.getInventory();
            inv.clear();
            player.setHealth(20);
            player.setFoodLevel(20);
            ItemStack weapon = new ItemStack(Material.TRIDENT, 1);
            getItem(weapon, "&8Infected Slayer", "&8Kill the infected!");
            inv.setItem(0, weapon);
            player.sendMessage("YOU ARE A SURVIVOR!");
        } else if (Objects.equals(role, "N/A")) {
            Bukkit.getLogger().info(player + " is no longer playing!");
            this.setInfect(false);
            this.setSurv(false);

            player.setGlowing(false);
            Inventory inv = player.getInventory();
            inv.clear();
            player.setHealth(20);
            player.setFoodLevel(20);
            player.setWalkSpeed(.2f);
            player.sendMessage("No longer playing");
        }

    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        Bukkit.getLogger().info("Player:  " + player + "  has died:  ");
        if (this.getSurv()) {
            this.setSurv(false);
            this.setInfect(true);
            this.setRole(player, "infected");
        } else if (this.getInfect()) {
            //TODO
        }

    }

    public void setInfect(Boolean bool) {
        this.inf = bool;
    }
    public boolean getInfect() {
        return inf;
    }

    public void setSurv(Boolean bool) {
        this.sur = bool;
    }
    public boolean getSurv() {
        return sur;
    }
    @EventHandler
    public void onPlayerDisconnect(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        Bukkit.getLogger().info("Player:  " + player + "  has disconnected");
        this.setRole(player, "N/A");

    }

//    @EventHandler
//    public void onPlayerAttack(EntityDamageByEntityEvent event) {
//        Entity attacker = event.getDamager();
//        Entity damaged = event.getEntity();
//        if (attacker instanceof Player && damaged instanceof Player) {
//
//        }
//    }

    private ItemStack getItem(ItemStack item, String name, String ... lore) {
        ItemMeta meta = item.getItemMeta();

        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));

        List<String> lores = new ArrayList<>();
        for(String s : lore) {
            lores.add(ChatColor.translateAlternateColorCodes('&', s));
        }
        meta.setLore(lores);
        item.setItemMeta(meta);

        return item;
    }
}
