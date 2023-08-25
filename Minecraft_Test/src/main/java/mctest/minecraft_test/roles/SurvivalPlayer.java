package mctest.minecraft_test.roles;

import mctest.minecraft_test.Minecraft_Test;
import org.bukkit.*;
//import org.bukkit.attribute.Attribute;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SurvivalPlayer implements Listener{
    private Boolean inf = false; //if player is infected
    private Boolean sur = false; //if player is survivor
    private Player gamer;
    private World world;
    public void setPlayer(Player player) {
        this.gamer = player;
    }
    public Player getPlayer() {
        return gamer;
    }

    //TODO Create a hashmap.  Use uid instead of name and set status

    public SurvivalPlayer(Minecraft_Test plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public void setRole(Player player, String role) {
        setPlayer(player);
        if (Objects.equals(role, "infected")) {
            setInfection(player);
        } else if (Objects.equals(role, "survivor")) {
            setSurvivor(player);
        } else if (Objects.equals(role, "N/A")) {
            setNotPlaying(player);
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        Bukkit.getLogger().info("Player:  " + player.getName() + "  has died ");
        if (this.getSurvivalStatus()) {
            Bukkit.getLogger().info("**********SURVIVOR***********");
            Bukkit.getLogger().info(player.getName() + " survivor:  " + getSurvivalStatus() + "   infected:  " + getInfectedStatus());
            Inventory inv = player.getInventory();
            inv.clear();
//            this.setSurvivalStatus(false);
//            this.setInfectedStatus(true);
//            this.setRole(player, "infected");
            Bukkit.getLogger().info(player.getName() + " survivor:  " + getSurvivalStatus() + "   infected:  " + getInfectedStatus());

        } else if (this.getInfectedStatus()) {
            //TODO
            Bukkit.getLogger().info("**********INFECTED***********");
            //event.setKeepInventory(true);
            List<ItemStack> items = event.getDrops();
            for (ItemStack i : items) {
                Bukkit.getLogger().info("Dropping: " + i.toString());
                event.getDrops().remove(i);
            }
        }

    }
    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        Bukkit.getLogger().info("Player:  " + player.getName() + "  has died ");
        if (this.getSurvivalStatus()) {
            Bukkit.getLogger().info("**********SURVIVOR***********");
            Bukkit.getLogger().info(player.getName() + " survivor:  " + getSurvivalStatus() + "   infected:  " + getInfectedStatus());
            Inventory inv = player.getInventory();
            inv.clear();
            //event.setRespawnLocation(infectSpawn());
            this.setSurvivalStatus(false);
            this.setInfectedStatus(true);
            this.setRole(player, "infected");
            Bukkit.getLogger().info(player.getName() + " survivor:  " + getSurvivalStatus() + "   infected:  " + getInfectedStatus());

        } else if (this.getInfectedStatus()) {
            //TODO
            Bukkit.getLogger().info("**********INFECTED***********");
            this.setRole(player, "infected");
            //event.setRespawnLocation(infectSpawn());
            Bukkit.getLogger().info(player.getName() + " survivor:  " + getSurvivalStatus() + "   infected:  " + getInfectedStatus());
        }

    }
    public void setInfection(Player player) {
        Bukkit.getLogger().info(player.getName() + " has been infected!");
        this.setInfectedStatus(true);
        this.setSurvivalStatus(false);

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

    public void setInfectedStatus(Boolean bool) {
        this.inf = bool;
    }
    public boolean getInfectedStatus() {
        return inf;
    }

    public void setSurvivor(Player player) {
        Bukkit.getLogger().info(player.getName() + " is a survivor!");
        this.setInfectedStatus(false);
        this.setSurvivalStatus(true);

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
        inv.setItem(10, silverArrow());

        player.sendMessage("YOU ARE A SURVIVOR!");
    }
    public void setSurvivalStatus(Boolean bool) {
        this.sur = bool;
    }
    public boolean getSurvivalStatus() {
        return sur;
    }
    public void setNotPlaying(Player player) {
        Bukkit.getLogger().info(player.getName() + " is no longer playing!");
        this.setInfectedStatus(false);
        this.setSurvivalStatus(false);

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
    public void onPlayerDisconnect(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        Bukkit.getLogger().info("Player:  " + player.getName() + "  has disconnected");
        this.setRole(player, "N/A");
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

        if (attacker instanceof Player && damaged instanceof Player) {
            Bukkit.getLogger().info("Player: " + this.getPlayer().getName() + "  attacker:  " + ((Player)attacker).getName() + "   damaged:  " + ((Player)damaged).getName());
            Bukkit.getLogger().info(this.getPlayer().getName() + " survivor:  " + getSurvivalStatus() + "   infected:  " + getInfectedStatus());
            if (this.getInfectedStatus()) {
                if (((Player) damaged).getItemInHand().equals((claw()))) {
                    event.setCancelled(true);
                }
            } else if (this.getSurvivalStatus()) {
                if (((Player) damaged).getItemInHand().equals((magicBow()))) {
                    event.setCancelled(true);
                }
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
