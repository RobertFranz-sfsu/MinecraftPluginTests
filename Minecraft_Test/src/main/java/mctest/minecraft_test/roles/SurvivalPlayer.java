package mctest.minecraft_test.roles;

import mctest.minecraft_test.Minecraft_Test;
import mctest.minecraft_test.util.DelayedTask;
import org.bukkit.*;
//import org.bukkit.attribute.Attribute;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scoreboard.*;

import java.util.*;

public class SurvivalPlayer implements Listener{
    private World world;
    private final HashMap<UUID, String> statusMap = new HashMap<>();
    private int infectedCnt = 0;
    private int survivorCnt = 0;
    private Boolean playing = false;

    public void setPlaying(Boolean playing) {
        this.playing = playing;
    }
    public Boolean getPlaying() {
        return this.playing;
    }

    public SurvivalPlayer(Minecraft_Test plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);

        plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
            if (this.getPlaying()) {
                this.setInfectedCnt();
                this.setSurvivorCnt();
                statusMap.forEach((key, value) -> this.setBoard(Bukkit.getPlayer(key)));
                statusMap.forEach((key, value) -> Bukkit.getLogger().info(key + " " + value));
                Bukkit.getLogger().info("Survivors: " + this.getSurvivorCnt());
                int test = this.getSurvivorCnt() + this.getInfectedCnt();
                Bukkit.getLogger().info(test + " == " + 0 + " : " + (test == 0));

                if (this.getInfectedCnt() == statusMap.size()) {
                    this.endGame();
                }
            }

        }, 0L, 50L);
    }

    public void gameInit() {
        //TODO Check if enough people are playing,
        // Set players as infected or survivor,
        // Start timer,
        // Set infected/survivor counts: possibly add unassigned role,
        // Set Scoreboards,
        // Start game
    }

    public void setInfection(Player player) {
        Bukkit.getLogger().info("Size: " + statusMap.size());
        Bukkit.getLogger().info(player.getName() + " has been infected!");
        statusMap.put(player.getUniqueId(), "infected");
        statusMap.forEach((key, value) -> Bukkit.getLogger().info(key + " " + value));

//            world = player.getLocation().getWorld();
//
//            Location infSpawn = new Location(world, 22.5, 67, -36, 0f, 0f);
//            player.teleport(infectSpawn());

        //player.setWalkSpeed(.6f);
        //player.setMaxHealth(4);
        this.setSpeed(player, .6f);
        this.setMaxHealth(player, 4);
        this.setHealth(player, 4);
        //player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(4);

        Inventory inv = player.getInventory();
        inv.clear();
        inv.setItem(0, claw());
        this.setBoard(player);
        player.sendMessage("YOU ARE INFECTED!");
    }
    public Location infectSpawn(Player player) {
        world = player.getLocation().getWorld();

        return new Location(world, 22.5, 67, -36, 0f, 0f);
    }

    public void setSurvivor(Player player) {
        Bukkit.getLogger().info(player.getName() + " is a survivor!");
        setPlaying(true);
        statusMap.put(player.getUniqueId(), "survivor");
        statusMap.forEach((key, value) -> Bukkit.getLogger().info(key + " " + value));

//        world = player.getLocation().getWorld();
//        Location sSpawn = new Location(world, 22.5, 67, 37, -180f, -1f);
//        player.teleport(sSpawn);

//        player.setWalkSpeed(.2f);
//        player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(20);
//        player.setMaxHealth(20);
//        player.setHealth(20);
        this.setSpeed(player, .2f);
        this.setMaxHealth(player, 20);
        this.setHealth(player, 20);

        Inventory inv = player.getInventory();
        inv.clear();
        inv.setItem(0, magicBow());
        inv.setItem(9, silverArrow());
        this.setBoard(player);

        player.sendMessage("YOU ARE A SURVIVOR!");
    }

    public void setNotPlaying(Player player) {
        Bukkit.getLogger().info(player.getName() + " is no longer playing!");
        statusMap.remove(player.getUniqueId());

        Inventory inv = player.getInventory();
        inv.clear();
        //player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(20);
//        player.setMaxHealth(20);
//        player.setHealth(20);
//        player.setWalkSpeed(.2f);
        player.setFoodLevel(20);
        this.setSpeed(player, .2f);
        this.setMaxHealth(player, 20);
        this.setHealth(player, 20);
        this.removeBoard(player);
        player.sendMessage("No longer playing");
        //TODO Store players' items and give them back
    }
    private void setSpeed(Player player, Float speed) {
        player.setWalkSpeed(speed);
    }
    private Float getSpeed(Player player) {
        return player.getWalkSpeed();
    }
    private void setMaxHealth(Player player, int health) {
        player.setMaxHealth(health);
    }
    private Double getMaxHealth(Player player) {
        return player.getMaxHealth();
    }
    private void setHealth(Player player, int health) {
        player.setHealth(health);
    }
    private Double getHealth(Player player) {
        return player.getHealth();
    }
    @EventHandler
    private void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        if (!statusMap.containsKey(player.getUniqueId())) {
            return;
        }
        Bukkit.getLogger().info("Player:  " + player.getName() + "  has died ");
        event.getDrops().clear();
    }
    @EventHandler
    private void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        if (!statusMap.containsKey(player.getUniqueId())) {
            return;
        }
        Bukkit.getLogger().info(player.getName() + " set as infected");
        this.setInfection(player);

        //TODO Figure out where and when to check how many left.
        // Note: currently if everyone is infected and no one respawns game never ends.
        // Or if the last person never respawns game doesnt end either
        this.setInfectedCnt();
        this.setSurvivorCnt();
        Bukkit.getLogger().info("Survivors: " + this.getSurvivorCnt() + "  Infected: " + this.getInfectedCnt());
//        if (Collections.frequency(statusMap.values(), "survivor") == 0) {
//            this.endGame();
//        }
    }
    @EventHandler
    private void onPlayerDisconnect(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (!statusMap.containsKey(player.getUniqueId())) {
            return;
        }
        Bukkit.getLogger().info("Player:  " + player.getName() + "  has disconnected");
        this.setNotPlaying(player);
    }
    private void endGame() {
        //Bukkit.getLogger().info("No more survivors!!!");
        new DelayedTask(() -> {
            //https://stackoverflow.com/questions/2351331/iterating-over-and-deleting-from-hashtable-in-java
            Iterator<Map.Entry<UUID, String>> it = statusMap.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<UUID, String> entry = it.next();
                Bukkit.getLogger().info(Bukkit.getPlayer(entry.getKey()).getName() + "  successfully exited the game");
                Bukkit.getPlayer(entry.getKey()).sendMessage("The game has ended.");
                if (entry.getKey() == null) {
                    it.remove();
                } else {
                    this.setNotPlaying(Bukkit.getPlayer(entry.getKey()));
                }

            }

            setPlaying(false);
            //       statusMap.forEach((key, value) -> {
            //          this.setNotPlaying(Bukkit.getPlayer(key));
            //          Bukkit.getLogger().info(Bukkit.getPlayer(key) + " No longer playing.");
            //       });

        }, 10 * 10);
        statusMap.forEach((key, value) -> Bukkit.getLogger().info(key + " " + value));
    }

    /**
     *
     * @param event
     * Cancels friendly fire
     */
    @EventHandler
    private void onPlayerAttack(EntityDamageByEntityEvent event) {
        Entity attacker = event.getDamager();
        Entity damaged = event.getEntity();
//        //TODO TEST CODE WORKS: REMOVE SOON
//        if (event.getCause() == EntityDamageEvent.DamageCause.PROJECTILE) {
//            Bukkit.getLogger().info("Projectile hit!");
//            if (damaged.getType() == EntityType.ZOMBIE_VILLAGER) {
//                event.setCancelled(true);
//            }
//        }
        //Makes sure both are playing the game, else return
        if (!statusMap.containsKey(attacker.getUniqueId()) && !statusMap.containsKey(damaged.getUniqueId())) {
            return;
        }
        
        //If survivor and hit by arrow, cancel damage
        if (Objects.equals(statusMap.get(damaged.getUniqueId()), "survivor") && event.getCause() == EntityDamageEvent.DamageCause.PROJECTILE) {
            Bukkit.getLogger().info("*****PROJECTILE FRIENDLY FIRE*****");
            event.setCancelled(true);
        }

        if (attacker instanceof Player && damaged instanceof Player) {
            // Bukkit.getLogger().info("****Entity damaged by entity event called****");
            Bukkit.getLogger().info("Attacker: " + statusMap.get(attacker.getUniqueId()) + "  Damaged: " + statusMap.get(damaged.getUniqueId()));

            // if both are on the same team or if a survivor is hit by a projectile: cancel the attack
            if (Objects.equals(statusMap.get(attacker.getUniqueId()), statusMap.get(damaged.getUniqueId()))) {
                Bukkit.getLogger().info("*****FRIENDLY FIRE*****");
                event.setCancelled(true);
            }
        }
    }

    public void setSurvivorCnt() {
        this.survivorCnt = Collections.frequency(statusMap.values(), "survivor");
    }
    public int getSurvivorCnt() {
        return this.survivorCnt;
    }
    public void setInfectedCnt() {
        this.infectedCnt = Collections.frequency(statusMap.values(), "infected");
    }
    public int getInfectedCnt() {
        return this.infectedCnt;
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
    /**
     * Scoreboard
     */
    private void setBoard(Player player) {
        if (!statusMap.containsKey(player.getUniqueId())) {
            return;
        }
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        Scoreboard scoreboard = manager.getNewScoreboard();

        Objective objective = scoreboard.registerNewObjective("Game Status", "dummy");
        objective.setDisplayName(ChatColor.GOLD + "Survival Status");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        //TODO can possibly move these to where/when the game initializes so they arent called everytime the scoreboard is called
        this.setSurvivorCnt();
        this.setInfectedCnt();

        Score newLine = objective.getScore("");
        newLine.setScore(3);
        Score infectedScore = objective.getScore(ChatColor.GREEN + "Infected:  " + this.getInfectedCnt());
        infectedScore.setScore(2);
        Score survivorScore = objective.getScore(ChatColor.AQUA + "Survivors: " + this.getSurvivorCnt());
        survivorScore.setScore(1);

        player.setScoreboard(scoreboard);
    }
    private void removeBoard(Player player) {
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        Scoreboard scoreboard = manager.getNewScoreboard();
        player.setScoreboard(scoreboard);

    }
}
