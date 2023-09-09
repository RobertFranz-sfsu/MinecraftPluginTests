/*
 * TODO
 *  Remove old tests/code
 *  Add YML configurability
 *    Infected/Survivor Buffs
 *  Init game
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
 *  Figure out why it kicks for spam for no reason
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
                //Send countdown time
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
                    //Bukkit.getLogger().info("Setting game time: " + gameTime);
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
//            PlayerHandler pl = new PlayerHandler(Minecraft_Test.getPlugin(Minecraft_Test.class));
//            ArrayList<UUID> chosen = new ArrayList<>();
//
//            //Randomly select number of players from list to be infected by index num
//            Bukkit.getLogger().info("Min: " + (int)Minecraft_Test.getPlugin(Minecraft_Test.class).getConfig().get("min-players"));
//            for(int i = 0; i < (int)Minecraft_Test.getPlugin(Minecraft_Test.class).getConfig().get("num-starting-infected"); i++){
//                chosen.add(pl.getPlayers().get((int)(Math.random() *
//                        (int)Minecraft_Test.getPlugin(Minecraft_Test.class).getConfig().get("num-starting-infected"))));
//            }
//
//            // Populate the map and assign roles
//            for(UUID p : pl.getPlayers()){
//                if(chosen.contains(p) && p != null){
//                    statusMap.put(p, "infected");
//                    setInfection(Bukkit.getPlayer(p));
//                }else if(p != null){
//                    statusMap.put(p, "survivor");
//                    setSurvivor(Bukkit.getPlayer(p));
//                }
//            }
            Random rand = new Random();

            Set<Integer> infectedSet = new HashSet<>();
            for (int i = 0; i < this.getNumStartInf(); i++) {
                infectedSet.add(rand.nextInt(statusMap.size()));
            }

            int it = 0;
            for (Map.Entry<UUID, String> entry : statusMap.entrySet()) {
                Bukkit.getLogger().info("cnt: " + it + " " + infectedSet.contains(it));
                entry.setValue((infectedSet.contains(it++)) ? "infected" : "survivor");
                this.setBoard(Objects.requireNonNull(Bukkit.getPlayer(entry.getKey())));
                this.setRole(Objects.requireNonNull(Bukkit.getPlayer(entry.getKey())));
            }

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

//            player.sendMessage("Health: " + con.getConfig().getInt("health"));
//            Bukkit.getLogger().info("Health: " + con.getConfig().getValues(false));
//            player.sendMessage("Speed: " + speed);

//            this.setAttributes(player, .6f, 4, 4);
            this.setAttributes(player, speed, health, health);
            player.sendMessage(ChatColor.translateAlternateColorCodes ('&', "&cYou are infected!"));
        } else if (Objects.equals(statusMap.get(player.getUniqueId()), "survivor")) {
            ConfigUtil con = new ConfigUtil(Minecraft_Test.getPlugin(Minecraft_Test.class), "Survivor.yml");
            int health = con.getConfig().getInt("health");
            float speed = Float.parseFloat(con.getConfig().get("speed").toString().replaceAll("[\\[\\],]",""));
//
//            player.sendMessage("Health: " + health);
//            player.sendMessage("Speed: " + speed);

//            this.setAttributes(player, .2f, 20, 20);
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
        //statusMap.forEach((key, value) -> Bukkit.getLogger().info(key + " " + value));

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
    private void endGame() {
        this.setTimer(Integer.MIN_VALUE);
        Iterator<Map.Entry<UUID, String>> it = statusMap.entrySet().iterator();
        while (it.hasNext()) {
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
                this.setNotPlaying(Objects.requireNonNull(Bukkit.getPlayer(entry.getKey())));
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
//            Bukkit.getLogger().info("*****PROJECTILE FRIENDLY FIRE*****");
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
//    public ItemStack giveWeapons(String role){
//
//        Map<String, Object> config = Minecraft_Test.getPlugin(Minecraft_Test.class).getConfig().getConfigurationSection("loadouts").getValues(true);
//
//        if(role.equals("infected")){
//            ItemStack weapon = new ItemStack(Material.matchMaterial(config.get("infected.item").toString()), Integer.valueOf(config.get("infected.itemAmount").toString().replaceAll("[\\[\\],]","")));
//            getItem(weapon, config.get("infected.itemName").toString().replaceAll("[\\[\\],]",""), config.get("infected.lore").toString().replaceAll("[\\[\\],]",""));
//
//            if((config.get("infected.enchantment") != null) && (config.get("infected.enchantmentLevel") != null)){
//                ItemMeta meta = weapon.getItemMeta();
//                meta.addEnchant(Enchantment.getByName(config.get("infected.enchantment").toString().replaceAll("[\\[\\],]","")),
//                        Integer.valueOf(config.get("infected.enchantmentLevel").toString().replaceAll("[\\[\\],]","")), true);
//                weapon.setItemMeta(meta);
//            }
//
//            return weapon;
//        }else if(role.equals("survivor")){
//            ItemStack weapon = new ItemStack(Material.matchMaterial(config.get("survivor.item").toString()), Integer.valueOf(config.get("survivor.itemAmount").toString().replaceAll("[\\[\\],]","")));
//            getItem(weapon, config.get("survivor.itemName").toString().replaceAll("[\\[\\],]",""),
//                    config.get("survivor.lore").toString().replaceAll("[\\[\\],]",""));
//            if((config.get("survivor.enchantment") != null) && (config.get("survivor.enchantmentLevel") != null)){
//                ItemMeta meta = weapon.getItemMeta();
//                meta.addEnchant(Enchantment.getByName(config.get("survivor.enchantment").toString().replaceAll("[\\[\\],]","")),
//                        Integer.valueOf(config.get("survivor.enchantmentLevel").toString().replaceAll("[\\[\\],]","")), true);
//                weapon.setItemMeta(meta);
//            }
//
//            return weapon;
//        }else{
//            return null;
//        }
//    }
//    public ItemStack infectedWeapons() {
//        Minecraft_Test pl = new Minecraft_Test();
//        Map<String, Object> test2 = pl.getConfig().getConfigurationSection("loadouts").getValues(true);
//
//        ItemStack weapon = new ItemStack(Material.IRON_SWORD, 1);
//        getItem(weapon, "&9Infected Claw", "&9Infect the uninfected!");
//        return weapon;
//    }
//    public ItemStack survivorWeapons() {
//        ItemStack weapon = new ItemStack(Material.BOW, 1);
//        getItem(weapon, "&9Infected Slayer", "&8Kill the infected!");
//        ItemMeta meta = weapon.getItemMeta();
//        meta.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
//        weapon.setItemMeta(meta);
//
//        return weapon;
//    }
//    public ItemStack silverArrow() {
//        ItemStack weapon = new ItemStack(Material.ARROW, 1);
//        getItem(weapon, "&9Silver Arrows", "&7Shiny");
//        ItemMeta meta = weapon.getItemMeta();
//        meta.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
//        weapon.setItemMeta(meta);
//
//        return weapon;
//    }

//    private ItemStack getItem(ItemStack item, String name, String ... lore) {
//        ItemMeta meta = item.getItemMeta();
//
//        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
//
//        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
//        meta.addEnchant(Enchantment.ARROW_DAMAGE, 1, true);
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

    /**
     * Setters/Getters for config stuff
     */
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