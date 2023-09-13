/*
 * TODO
 *  Remove old tests/code
 *  Add YML configurability
 *    Infected/Survivor Buffs
 *  Make menu give correct loadout
 *  Scoreboard styling
 *  Add support for spaces in names
 *  Add support for color in names/lore
 *  Implement respawn timer
 *  Custom guns/fix accuracy
 *  Make loadout list prettier
 *  Implement multiverse
 *    Must change playerhandler
 *    Add world checks to every command
 *  Teleport players on game end after a countdown
 *    Announce winners
 *  Implement economy
 *  Implement scores
 *  Figure out why it kicks for spam for no reason
 *  Add command to manually set role
 *  Add command to manually start/end matches
 *  Have scoreboard showing the entire time in the server/world/match
 *
 *
 *  Move code to fresh repo lol
 *
 * */

package mctest.minecraft_test.roles;

import mctest.minecraft_test.Minecraft_Test;
import mctest.minecraft_test.util.ConfigUtil;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
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
    private int time = Integer.MIN_VALUE;
    private int maxPl;
    private int minPl;
    private int waitTime;
    private int gameTime;
    private int respawnTime;

    private int numStartInf;

    public void setPlaying(Boolean playing) {
        this.playing = playing;
    }
    public Boolean getPlaying() {
        return this.playing;
    }
    public void setTimer(int amount) {
        this.time = amount;
    }
    public int getTimer() {
        return this.time;
    }
    public HashMap<UUID, String> getStatusMap(){
        return this.statusMap;
    }

    public SurvivalPlayer(Minecraft_Test plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);

        plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
            // if timer is set, start counting down
            if (this.getTimer() > 0) {
                Bukkit.getLogger().info("Timer: " + this.getTimer());
                setTimer(this.getTimer()-1);
            }

            if (!this.getPlaying()) {
                // Send countdown time
                if (this.getTimer() != Integer.MIN_VALUE) {
                    statusMap.forEach((key, value) ->  Bukkit.getPlayer(key).sendMessage("Timer: " + this.getTimer()));
                }
                // if minimum amount of players have joined, start timer
                if (statusMap.size() == this.getMinPl() &&  this.getTimer() == Integer.MIN_VALUE) {
                    Bukkit.getLogger().info("Min amount of players joined: Timer Started!");
                    setTimer(getWaitTime());
                }
                statusMap.forEach((key, value) -> this.waitBoard(Objects.requireNonNull(Bukkit.getPlayer(key))));
                // if max amount of players have joined or if the timer has hit 0, start the game
                if (statusMap.size() == this.getMaxPl() || this.getTimer() == 0) {
                    Bukkit.getLogger().info("Game Starting");
                    statusMap.forEach((key, value) ->  Bukkit.getPlayer(key).sendMessage("Game starting"));
                    gameInit();
                    this.setTimer(Integer.MIN_VALUE);
                }
            }

            // start the game
            if (this.getPlaying()) {
                if (this.getTimer() == Integer.MIN_VALUE) {
                    this.setTimer(getGameTime());
                }
                this.setInfectedCnt();
                this.setSurvivorCnt();
                statusMap.forEach((key, value) -> this.setBoard(Objects.requireNonNull(Bukkit.getPlayer(key))));
                Bukkit.getLogger().info("Game in session");

                // TODO
                //  infected win
                // if everyone is infected, infected won
//                if (this.getInfectedCnt() == statusMap.size()) {
//                    statusMap.forEach((key, value) ->  Bukkit.getPlayer(key).sendMessage("INFECTED WON!"));
//                    this.endGame();
//                }

                // if timer runs out, survivors won
                if (this.getTimer() == 0) {
                    statusMap.forEach((key, value) ->  {
                        Bukkit.getPlayer(key).sendMessage("SURVIVORS WON!");
                        if (Objects.equals(value, "survivor")) {
                            Bukkit.getPlayer(key).sendMessage("YOU WON!");
                            //TODO Anything else you want winners to get
                        }
                    });
                    this.endGame();
                }
            }

        }, 0L, 20L);
    }

    public void gameInit() {
        //TODO Check if enough people are playing,
        // Set players as infected or survivor depending on amount playing,
        // Start timer,
        // Set infected/survivor counts: possibly add unassigned role,
        // Set Scoreboards (might not need to do here),
        // Start game
        // THIS MIGHT ALL NEED TO BE IN SurvivalPlayer ABOVE ^^^^
        try{
            Bukkit.getLogger().info("NUM START INF: " + this.getNumStartInf());
            Random rand = new Random();

            Set<Integer> infectedSet = new HashSet<>();
            for (int i = 0; i < this.getNumStartInf(); i++) {
                infectedSet.add(rand.nextInt(this.getNumStartInf()));
            }

            int it = 0;
            for (Map.Entry<UUID, String> entry : statusMap.entrySet()) {
                Bukkit.getLogger().info("cnt: " + it + " " + infectedSet.contains(it));
                entry.setValue((infectedSet.contains(it++)) ? "infected" : "survivor");
                this.setBoard(Objects.requireNonNull(Bukkit.getPlayer(entry.getKey())));
                this.setRole(Objects.requireNonNull(Bukkit.getPlayer(entry.getKey())));
            }

            Bukkit.getLogger().info(statusMap.toString());

            this.setPlaying(true);

        } catch (Exception e){
            Bukkit.getLogger().info("Something went wrong trying to initialize the game.");
            e.printStackTrace();
        }
    }

    public void setRole(Player player) {
        if (Objects.equals(statusMap.get(player.getUniqueId()), "infected")) {
            ConfigUtil con = new ConfigUtil(Minecraft_Test.getPlugin(Minecraft_Test.class), "Infected.yml");
            int health = con.getConfig().getInt("health");
            float speed = Float.parseFloat(con.getConfig().get("speed").toString().replaceAll("[\\[\\],]",""));

            this.setAttributes(player, speed, health, health);
            player.sendMessage(ChatColor.translateAlternateColorCodes ('&', "&cYou are infected!"));
        } else if (Objects.equals(statusMap.get(player.getUniqueId()), "survivor")) {
            ConfigUtil con = new ConfigUtil(Minecraft_Test.getPlugin(Minecraft_Test.class), "Survivor.yml");
            int health = con.getConfig().getInt("health");
            float speed = Float.parseFloat(con.getConfig().get("speed").toString().replaceAll("[\\[\\],]",""));

            this.setAttributes(player, speed, health, health);
            player.sendMessage(ChatColor.translateAlternateColorCodes ('&', "&cYou are a survivor!"));
        }
        Bukkit.dispatchCommand(player, "m");
        //this.setBoard(player);
    }
    private void setAttributes(Player player, Float speed, int maxHealth, int health) {
        player.setWalkSpeed(speed);
        player.setMaxHealth(maxHealth);
        player.setHealth(health);
    }

    //TODO delete setInfection and setSurvivor
    public void setInfection(Player player) {
        Bukkit.getLogger().info(player.getName() + " has been infected!");
        statusMap.put(player.getUniqueId(), "infected");

        this.setAttributes(player, .2f, 20, 20);

        Bukkit.dispatchCommand(player, "m");
        this.setBoard(player);
        player.sendMessage("YOU ARE INFECTED!");
    }
    public Location infectSpawn(Player player) {
        world = player.getLocation().getWorld();

        return new Location(world, 22.5, 67, -36, 0f, 0f);
    }

    public void setSurvivor(Player player) {
        Bukkit.getLogger().info(player.getName() + " is a survivor!");
        statusMap.put(player.getUniqueId(), "survivor");
        //statusMap.forEach((key, value) -> Bukkit.getLogger().info(key + " " + value));

        this.setAttributes(player, .2f, 20, 20);

        Bukkit.dispatchCommand(player, "m");
        this.setBoard(player);
        player.sendMessage("YOU ARE A SURVIVOR!");
    }

    public void setNotPlaying(Player player) {
        Bukkit.getLogger().info(player.getName() + " is no longer playing!");

        Inventory inv = player.getInventory();
        inv.clear();
        player.setFoodLevel(20);
        this.setAttributes(player, .2f, 20, 20);

        this.removeBoard(player);
        statusMap.remove(player.getUniqueId());
        player.sendMessage("No longer playing");
        //TODO Store players' items and give them back
    }

    public void setUnassigned(Player player) {
        Bukkit.getLogger().info("Set as unassigned");
        statusMap.forEach((key, value) -> Bukkit.getLogger().info(key + " " + value));
        statusMap.put(player.getUniqueId(), "unassigned");
    }
    public void endGame() {
        Bukkit.getLogger().info("1");
        this.setTimer(Integer.MIN_VALUE);
        Bukkit.getLogger().info("2");
        Iterator<Map.Entry<UUID, String>> it = statusMap.entrySet().iterator();
        Bukkit.getLogger().info("3");
        while (it.hasNext()) {
            Bukkit.getLogger().info("4");
            Map.Entry<UUID, String> entry = it.next();

            Bukkit.getPlayer(entry.getKey()).getInventory().clear();
            Bukkit.getPlayer(entry.getKey()).getInventory().setHelmet(null);
            Bukkit.getPlayer(entry.getKey()).getInventory().setChestplate(null);
            Bukkit.getPlayer(entry.getKey()).getInventory().setLeggings(null);
            Bukkit.getPlayer(entry.getKey()).getInventory().setBoots(null);

            Bukkit.getLogger().info(Bukkit.getPlayer(entry.getKey()).getName() + " successfully exited the game");
            Bukkit.getPlayer(entry.getKey()).sendMessage("The game has ended.");
            if (entry.getKey() == null) {
                it.remove();
            } else {
                Bukkit.getLogger().info("5");
                this.setNotPlaying(Objects.requireNonNull(Bukkit.getPlayer(entry.getKey())));
                Bukkit.getLogger().info("6");
            }
        }
        this.setPlaying(false);
        statusMap.forEach((key, value) -> Bukkit.getLogger().info(key + " " + value));
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
        statusMap.put(player.getUniqueId(), "infected");
        this.setRole(player);
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

    /**
     *
     * @param event
     * Cancels friendly fire
     */
    @EventHandler
    private void onPlayerAttack(EntityDamageByEntityEvent event) {
        Entity attacker = event.getDamager();
        Entity damaged = event.getEntity();

        //Makes sure both are playing the game, else return
        if (!statusMap.containsKey(attacker.getUniqueId()) && !statusMap.containsKey(damaged.getUniqueId())) {
            return;
        }

        //If survivor and hit by arrow, cancel damage
        if (Objects.equals(statusMap.get(damaged.getUniqueId()), "survivor") && event.getCause() == EntityDamageEvent.DamageCause.PROJECTILE) {
            event.setCancelled(true);
        }
        // if both are on the same team or if a survivor is hit by a projectile: cancel the attack
        else if (Objects.equals(statusMap.get(attacker.getUniqueId()), statusMap.get(damaged.getUniqueId()))) {
            event.setCancelled(true);
        }
    }
    //TODO Possibly use this
//    @EventHandler
//    private void onFoodDepletion(FoodLevelChangeEvent event) {
//        if (statusMap.containsKey(event.getEntity().getUniqueId())) {
//            event.setCancelled(true);
//        }
//    }

    /**
     * Setters/Getters for config stuff
     */

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

    private void setWaitTime(){
        this.waitTime = Integer.parseInt(Minecraft_Test.getPlugin(Minecraft_Test.class).getConfig().get("wait-timer").toString().replaceAll("[\\[\\],]",""));
    }
    private int getWaitTime(){
        setWaitTime();
        return this.waitTime;
    }

    // Match length
    private void setGameTime(){
        this.gameTime = Integer.parseInt(Minecraft_Test.getPlugin(Minecraft_Test.class).getConfig().get("match-length").toString().replaceAll("[\\[\\],]",""));
    }
    private int getGameTime(){
        setGameTime();
        return this.gameTime;
    }

    private void setMaxPl(){
        this.maxPl = Integer.parseInt(Minecraft_Test.getPlugin(Minecraft_Test.class).getConfig().get("max-players").toString().replaceAll("[\\[\\],]",""));
    }
    private int getMaxPl(){
        setMaxPl();
        return this.maxPl;
    }

    private void setMinPl(){
        this.minPl = Integer.parseInt(Minecraft_Test.getPlugin(Minecraft_Test.class).getConfig().get("min-players").toString().replaceAll("[\\[\\],]",""));
    }
    private int getMinPl(){
        setMinPl();
        return this.minPl;
    }

    private void setNumStartInf(){
        this.numStartInf = Integer.parseInt(Minecraft_Test.getPlugin(Minecraft_Test.class).getConfig().get("num-starting-infected").toString().replaceAll("[\\[\\],]",""));
    }
    private int getNumStartInf(){
        setNumStartInf();
        return this.numStartInf;
    }

    private void setRespawnTime(){
        this.respawnTime = Integer.parseInt(Minecraft_Test.getPlugin(Minecraft_Test.class).getConfig().get("respawn-timer").toString().replaceAll("[\\[\\],]",""));
    }
    private int getRespawnTime(){
        setRespawnTime();
        return this.respawnTime;
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

        Score newLine1 = objective.getScore("");
        newLine1.setScore(5);
        String minutes = String.valueOf(this.getTimer() / 60);
        String seconds = ((this.getTimer()%60 < 10) ? "0" : "") + this.getTimer()%60 ;
        Score timer = objective.getScore("Time left: " + minutes + ":" + seconds);
        timer.setScore(4);
        Score newLine2 = objective.getScore("");
        newLine2.setScore(3);

        Score survivorScore = objective.getScore(ChatColor.GREEN + "Survivors: " + this.getSurvivorCnt());
        survivorScore.setScore(2);
        Score infectedScore = objective.getScore(ChatColor.RED + "Infected:   " + this.getInfectedCnt());
        infectedScore.setScore(1);

        player.setScoreboard(scoreboard);
    }
    private void waitBoard(Player player) {

        if (!statusMap.containsKey(player.getUniqueId())) {
            return;
        }
        ScoreboardManager manager1 = Bukkit.getScoreboardManager();
        Scoreboard scoreboard = manager1.getNewScoreboard();

        Objective objective = scoreboard.registerNewObjective("Game Status", "dummy");
        objective.setDisplayName(ChatColor.GOLD + "Waiting on Players");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        Score newLine1 = objective.getScore("");
        newLine1.setScore(5);
        if (statusMap.size() == getMinPl()) {
            String minutes = String.valueOf(this.getTimer() / 60);
            String seconds = ((this.getTimer()%60 < 10) ? "0" : "") + this.getTimer()%60 ;
            Score timer = objective.getScore("Time left: " + minutes + ":" + seconds);
            timer.setScore(4);
        } else {
            Score waiting = objective.getScore("Waiting for at least " + (this.getMinPl() - statusMap.size()) + " more player(s).");
            waiting.setScore(4);
        }
        Score amount = objective.getScore("Players: " + statusMap.size() + " / " + this.getMaxPl());
        amount.setScore(3);
        Score min = objective.getScore("Minimum required to start: " + this.getMinPl());
        min.setScore(2);

        player.setScoreboard(scoreboard);
    }
    
    private void removeBoard(Player player) {
        if (!statusMap.containsKey(player.getUniqueId())) {
            return;
        }
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        Scoreboard scoreboard = manager.getNewScoreboard();
        player.setScoreboard(scoreboard);

    }
}