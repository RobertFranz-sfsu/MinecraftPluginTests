package mctest.minecraft_test.roles;

import mctest.minecraft_test.Minecraft_Test;
import org.bukkit.*;
//import org.bukkit.attribute.Attribute;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class SurvivalPlayer implements Listener{
    private Player gamer;
    private World world;
    public void setPlayer(Player player) {
        this.gamer = player;
    }
    public Player getPlayer() {
        return gamer;
    }

    //TODO Create a hashmap.  Use uid instead of name and set status
    private HashMap<UUID, String> statusMap = new HashMap<>();

    public SurvivalPlayer(Minecraft_Test plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }


    public void setInfection(Player player) {
        Bukkit.getLogger().info(player.getName() + " has been infected!");
        statusMap.put(player.getUniqueId(), "infected");
        statusMap.forEach((key, value) -> Bukkit.getLogger().info(key + " " + value));


//            world = player.getLocation().getWorld();
//
//            Location infSpawn = new Location(world, 22.5, 67, -36, 0f, 0f);
//            player.teleport(infectSpawn());

        player.setWalkSpeed(.6f);
        player.setMaxHealth(4);
        //player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(4);

        Inventory inv = player.getInventory();
        inv.clear();
        inv.setItem(0, claw());

        player.sendMessage("YOU ARE INFECTED!");
    }
    public Location infectSpawn() {
        world = getPlayer().getLocation().getWorld();

        return new Location(world, 22.5, 67, -36, 0f, 0f);
    }

    public void setSurvivor(Player player) {
        Bukkit.getLogger().info(player.getName() + " is a survivor!");
        statusMap.put(player.getUniqueId(), "survivor");
        statusMap.forEach((key, value) -> Bukkit.getLogger().info(key + " " + value));

//            world = player.getLocation().getWorld();
//
//            Location sSpawn = new Location(world, 22.5, 67, 37, -180f, -1f);
//            player.teleport(sSpawn);

        player.setWalkSpeed(.2f);
        //player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(20);
        player.setMaxHealth(20);
        player.setHealth(20);

        Inventory inv = player.getInventory();
        inv.clear();
        inv.setItem(0, magicBow());
        inv.setItem(9, silverArrow());

        player.sendMessage("YOU ARE A SURVIVOR!");
    }

    public void setNotPlaying(Player player) {
        Bukkit.getLogger().info(player.getName() + " is no longer playing!");
        statusMap.remove(player.getUniqueId());

        Inventory inv = player.getInventory();
        inv.clear();
        //player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(20);
        player.setMaxHealth(20);
        player.setHealth(20);
        player.setFoodLevel(20);
        player.setWalkSpeed(.2f);
        player.sendMessage("No longer playing");
        //TODO Store players' items and give them back
    }
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        if (!statusMap.containsKey(player.getUniqueId())) {
            return;
        }
        Bukkit.getLogger().info("Player:  " + player.getName() + "  has died ");
        event.getDrops().clear();
    }
    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        if (!statusMap.containsKey(player.getUniqueId())) {
            return;
        }
        if (Objects.equals(statusMap.get(player.getUniqueId()), "survivor")) {
            Bukkit.getLogger().info("**********SURVIVOR***********");
            this.setInfection(player);
            //event.setRespawnLocation(infectSpawn());

        } else if (Objects.equals(statusMap.get(player.getUniqueId()), "infected")) {
            //TODO
            Bukkit.getLogger().info("**********INFECTED***********");
            this.setInfection(player);
            //event.setRespawnLocation(infectSpawn());
        }
    }
    @EventHandler
    public void onPlayerDisconnect(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (!statusMap.containsKey(player.getUniqueId())) {
            return;
        }
        Bukkit.getLogger().info("Player:  " + player.getName() + "  has disconnected");
        this.setNotPlaying(player);
    }

    /**
     *
     * @param event
     * Cancels friendly fire
     */
    @EventHandler
    public void onPlayerAttack(EntityDamageByEntityEvent event) {
        Entity attacker = event.getDamager();
        Entity damaged = event.getEntity();
        if (!statusMap.containsKey(attacker.getUniqueId()) && !statusMap.containsKey(damaged.getUniqueId())) {
            return;
        }

        //TEST CODE
        if (attacker instanceof Player) {
            Bukkit.getLogger().info("***Player attack***");

        }
        //END TEST
        if (attacker instanceof Player && damaged instanceof Player) {
            //        Bukkit.getLogger().info("****Entity damaged by entity event called****");

            Bukkit.getLogger().info("***Passed check***");
           // Bukkit.getLogger().info(this.getPlayer().getName() + " survivor:  " + getSurvivalStatus() + "   infected:  " + getInfectedStatus());
            Bukkit.getLogger().info("Attacker: " + statusMap.get(attacker.getUniqueId()) + "  Damaged: " + statusMap.get(damaged.getUniqueId()));

            if (Objects.equals(statusMap.get(attacker.getUniqueId()), statusMap.get(damaged.getUniqueId()))) {
                Bukkit.getLogger().info("*****FRIENDLY FIRE*****");
                event.setCancelled(true);
            }
        }
    }

    /**
     * Items and Inventories
     *
     */
    public ItemStack claw() {
        ItemStack weapon = new ItemStack(Material.IRON_SWORD, 1);
        getItem(weapon, "&9Infected Claw", "&9Infect the uninfected!");
        return weapon;
    }
    public ItemStack magicBow() {
        ItemStack weapon = new ItemStack(Material.BOW, 1);
        getItem(weapon, "&9Infected Slayer", "&8Kill the infected!");
        ItemMeta meta = weapon.getItemMeta();
        meta.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
        weapon.setItemMeta(meta);

        return weapon;
    }
    public ItemStack silverArrow() {
        ItemStack weapon = new ItemStack(Material.ARROW, 1);
        getItem(weapon, "&9Silver Arrows", "&7Shiny");
        ItemMeta meta = weapon.getItemMeta();
        meta.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
        weapon.setItemMeta(meta);

        return weapon;
    }

    private ItemStack getItem(ItemStack item, String name, String ... lore) {
        ItemMeta meta = item.getItemMeta();

        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
//        meta.setUnbreakable(true);

        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.addEnchant(Enchantment.ARROW_DAMAGE, 1, true);

        List<String> lores = new ArrayList<>();
        for(String s : lore) {
            lores.add(ChatColor.translateAlternateColorCodes('&', s));
        }
        meta.setLore(lores);
        item.setItemMeta(meta);

        return item;
    }
}
