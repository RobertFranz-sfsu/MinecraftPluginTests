/*
 * TODO
 *   S:
 *     Add YML configurability
 *       Infected/Survivor effects
 *     Make loadout list prettier
 *     Scoreboard styling
 *     Teleport players on game end after a countdown
 *     Implement permissions for sub-commands
 *     Set which permissions should be given by default
 *   R:
 *     Implement respawn timer
 *     Infected join
 *       optional argument to specify which queue they want to join for multiple maps
 *       if no argument, then send them to any queue with people
 *       if no queue with people send to random map
 *     Command to manually set role
 *
 *
 *  Implement allowed worlds
 *    Must change playerhandler
 *    Add world checks to every command
 *    Announce winners
 *  Implement economy
 *  Implement scores
 *    Save to player data so save as a file with the UUID as the file name (similar to essentials)
 *
 *  Implement per world timer
 *      Add command to set wait time. If world is specified set that otherwise set for world they're in
 *  Implement PAPI
 *  Multiple spawn locations
 *  Fix /spawn to /ispawn
 *  Scaling infected amount (?)
 *  Change map in queue broadcast to map name which can be set in a config
 *
 *  Remove old tests/code
 *  Move code to fresh repo lol
 *
 * */

package mctest.minecraft_test.roles;

import mctest.minecraft_test.Minecraft_Test;
import mctest.minecraft_test.util.ConfigUtil;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scoreboard.*;

import java.util.*;

public class SurvivalPlayer implements Listener{
    ConfigUtil surConfig = new ConfigUtil(Minecraft_Test.getPlugin(Minecraft_Test.class), "Survivor.yml");
    ConfigUtil infConfig = new ConfigUtil(Minecraft_Test.getPlugin(Minecraft_Test.class), "Infected.yml");
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
    private Location infSpawn;
    private Location surSpawn;
    private Location defaultSpawn;
    private int surHealth;
    private float surSpeed;
    private int infHealth;
    private float infSpeed;
    private List<String> allowedWorlds;

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
                if (this.getTimer() % 5 == 0) {
                    String str = (this.getPlaying() ? "Game " : "Queue ");
                    Bukkit.getLogger().info(str + "Timer: " + this.getTimer());
                }
                setTimer(this.getTimer()-1);
            }

            if (!this.getPlaying()) {
                // Send countdown time
                if (this.getTimer() != Integer.MIN_VALUE) {
                    //statusMap.forEach((key, value) ->  Bukkit.getPlayer(key).sendMessage("Timer: " + this.getTimer()));
                }
                // if minimum amount of players have joined, start timer
                if (statusMap.size() == this.getMinPl() &&  this.getTimer() == Integer.MIN_VALUE) {
                    List<String> val = Minecraft_Test.getPlugin(Minecraft_Test.class).getConfig().getStringList("lobby-worlds");
                    Bukkit.getLogger().info("Min amount of players joined: Timer Started!");
                    setTimer(getWaitTime());

                    // Print message that queue has begun in specified world as long as broadcast is enabled in config
                    if(Minecraft_Test.getPlugin(Minecraft_Test.class).getConfig().getBoolean("queue-start-broadcast-enabled")){
                        String current = "";

                        // TODO
                        //  Change this to map name
                        for(UUID i : statusMap.keySet()){
                            if((Bukkit.getPlayer(i) != null) && !val.contains(Bukkit.getPlayer(i).getWorld().getName())){
                                current = Bukkit.getPlayer(i).getWorld().getName();
                                break;
                            }
                        }

                        String msg = ChatColor.translateAlternateColorCodes ('&', "&aAn &cInfected &aqueue has begun in " + current + "!");

                        for(String w : val){
                            if(Bukkit.getWorld(w) != null){
                                Bukkit.getWorld(w).getPlayers().forEach(player -> player.sendMessage(msg));
                            }
                        }
                    }
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
                //  If everyone is infected, infected won
//                if (this.getInfectedCnt() == statusMap.size()) {
//                    statusMap.forEach((key, value) ->  Bukkit.getPlayer(key).sendMessage("INFECTED WON!"));
//                    this.endGame();
//                }

                // If timer runs out, survivors won
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

                // Game ended by admin
                if (this.getTimer() == -42){
                    statusMap.forEach((key, value) ->  {
                        Bukkit.getPlayer(key).sendMessage("Game has been manually ended.");
                    });
                    this.endGame();
                }
            }
        }, 0L, 20L);
    }

    public void gameInit() {
        //TODO
        // Set players as infected or survivor depending on amount playing,

        try{
            Bukkit.getLogger().info("NUM START INF: " + this.getNumStartInf());

            ArrayList<Integer> playerList = new ArrayList<>();
            for (int i = 0; i < statusMap.size(); i++) {
                playerList.add(i);
            }

            Collections.shuffle(playerList);
            List pList = playerList.subList(0, getNumStartInf());
            int iter = 0;
            for (Map.Entry<UUID, String> entry : statusMap.entrySet()) {
                entry.setValue((pList.contains(iter++)) ? "infected" : "survivor");
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
            this.setAttributes(player, this.getInfSpeed(), this.getInfHealth(), this.getInfHealth());

            if(!Objects.equals(infConfig.getConfig().get("effects"), null)){
                setEffects(player);
            }else{
                Bukkit.getLogger().info("No infected effects to apply.");
            }

            player.sendMessage(ChatColor.translateAlternateColorCodes ('&', "&bYou are &cinfected&b!"));

            player.teleport(this.getInfSpawn());
        } else if (Objects.equals(statusMap.get(player.getUniqueId()), "survivor")) {
            this.setAttributes(player, this.getSurSpeed(), this.getSurHealth(), this.getSurHealth());

            if(!Objects.equals(surConfig.getConfig().get("effects"), null)){
                setEffects(player);
            }else{
                Bukkit.getLogger().info("No survivor effects to apply.");
            }

            player.sendMessage(ChatColor.translateAlternateColorCodes ('&', "&cYou are a survivor!"));

            player.teleport(this.getSurSpawn());
        }
        Bukkit.dispatchCommand(player, "m");
        //this.setBoard(player);
    }
    private void setAttributes(Player player, Float speed, int maxHealth, int health) {
        player.setWalkSpeed(speed);
        player.setMaxHealth(maxHealth);
        player.setHealth(health);
    }

    private void setEffects(Player player){
        if (Objects.equals(statusMap.get(player.getUniqueId()), "infected")) {

        } else if (Objects.equals(statusMap.get(player.getUniqueId()), "survivor")) {

        }
    }

    private void removeEffects(Player player){
        ArrayList<PotionEffect> pe = new ArrayList<>(player.getActivePotionEffects());

        for(PotionEffect x : pe){
            player.removePotionEffect(x.getType());
        }
    }
    public void setNotPlaying(Player player) {
        Bukkit.getLogger().info(player.getName() + " is no longer playing!");

        Inventory inv = player.getInventory();
        inv.clear();
        player.setFoodLevel(20);
        this.setAttributes(player, .2f, 20, 20);

        this.removeBoard(player);
        statusMap.remove(player.getUniqueId());
        removeEffects(player);
        player.sendMessage("No longer playing");
        //TODO Store players' items and give them back
    }

    public void setUnassigned(Player player) {
        if(getAllowedWorlds().contains(player.getWorld().getName())){
            statusMap.forEach((key, value) -> Bukkit.getLogger().info(key + " " + value));
            statusMap.put(player.getUniqueId(), "unassigned");
        } else {
            player.sendMessage("You can't do that here.");
        }
    }

    public void endGame() {
        Iterator<Map.Entry<UUID, String>> it = statusMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<UUID, String> entry = it.next();

            if(Bukkit.getPlayer(entry.getKey()) != null){
                Bukkit.getPlayer(entry.getKey()).getInventory().clear();
                Bukkit.getPlayer(entry.getKey()).getInventory().setHelmet(null);
                Bukkit.getPlayer(entry.getKey()).getInventory().setChestplate(null);
                Bukkit.getPlayer(entry.getKey()).getInventory().setLeggings(null);
                Bukkit.getPlayer(entry.getKey()).getInventory().setBoots(null);

                Inventory inv = Bukkit.getPlayer(entry.getKey()).getInventory();
                inv.clear();

                removeEffects(Bukkit.getPlayer(entry.getKey()));
            }

            Bukkit.getLogger().info(Bukkit.getPlayer(entry.getKey()).getName() + " successfully exited the game");
            Bukkit.getPlayer(entry.getKey()).sendMessage("The game has ended.");
            if (entry.getKey() == null) {
                it.remove();
            } else {
                this.setNotPlaying(Objects.requireNonNull(Bukkit.getPlayer(entry.getKey())));
            }
        }
        this.setPlaying(false);
        this.setTimer(Integer.MIN_VALUE);
    }

    /**
     * Event Handlers
     * @param event
     *
     * onPlayerDeath makes sure players' items dont drop on the ground.
     * onPlayerRespawn sets players who have died to infected.
     * onPlayerDisconnect removes players who have disconnected from the game.
     * onPlayerAttack cancels friendly fire.
     */
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

    @EventHandler
    private void onPlayerAttack(EntityDamageByEntityEvent event) {
        Entity attacker = event.getDamager();
        Entity damaged = event.getEntity();

        //Makes sure both are playing the game, else return
        if (!statusMap.containsKey(attacker.getUniqueId()) && !statusMap.containsKey(damaged.getUniqueId())) {
            return;
        }

        //If survivor and hit by projectile: cancel the damage
        if (Objects.equals(statusMap.get(damaged.getUniqueId()), "survivor") && event.getCause() == EntityDamageEvent.DamageCause.PROJECTILE) {
            event.setCancelled(true);
        }

        // if both are on the same team: cancel the attack
        else if (Objects.equals(statusMap.get(attacker.getUniqueId()), statusMap.get(damaged.getUniqueId()))) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    private void onDamage(EntityDamageEvent event){
        if(event.getEntity() instanceof Player){
            Player player = (Player) event.getEntity();
            //Makes sure both are playing the game, else return
            if (!statusMap.containsKey(player.getUniqueId())) {
                return;
            }
            if(event.getDamage() >= player.getHealth()){
                event.setCancelled(true);
                if (!statusMap.containsKey(player.getUniqueId())) {
                    return;
                }

                Bukkit.getLogger().info("Player:  " + player.getName() + "  has died ");
                if(Objects.equals(statusMap.get(player.getUniqueId()).toLowerCase(), "survivor")){
                    statusMap.put(player.getUniqueId(), "infected");
                }

                this.removeEffects(player);
                this.setRole(player);
                player.teleport(getInfSpawn());


            }
        }
    }

    //TODO Possibly use this
//    @EventHandler
//    private void onFoodDepletion(FoodLevelChangeEvent event) {
//        if (statusMap.containsKey(event.getEntity().getUniqueId())) {
//            event.setCancelled(true);
//            //event.getEntity().setFoodLevel(20);
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
        this.waitTime = Minecraft_Test.getPlugin(Minecraft_Test.class).getConfig().getInt("wait-timer");
    }
    private int getWaitTime(){
        this.setWaitTime();
        return this.waitTime;
    }

    // Match length
    private void setGameTime(){
        this.gameTime = Minecraft_Test.getPlugin(Minecraft_Test.class).getConfig().getInt("match-length");
    }
    private int getGameTime(){
        this.setGameTime();
        return this.gameTime;
    }

    private void setMaxPl(){
        this.maxPl = Minecraft_Test.getPlugin(Minecraft_Test.class).getConfig().getInt("max-players");
    }
    private int getMaxPl(){
        this.setMaxPl();
        return this.maxPl;
    }

    private void setMinPl(){
        this.minPl = Minecraft_Test.getPlugin(Minecraft_Test.class).getConfig().getInt("min-players");
    }
    private int getMinPl(){
        this.setMinPl();
        return this.minPl;
    }

    private void setNumStartInf(){
        this.numStartInf = Minecraft_Test.getPlugin(Minecraft_Test.class).getConfig().getInt("num-starting-infected");
    }
    private int getNumStartInf(){
        this.setNumStartInf();
        return this.numStartInf;
    }

    private void setRespawnTime(){
        this.respawnTime = Minecraft_Test.getPlugin(Minecraft_Test.class).getConfig().getInt("respawn-timer");
    }
    private int getRespawnTime(){
        this.setRespawnTime();
        return this.respawnTime;
    }

    public void reloadConfigs(){
        surConfig = new ConfigUtil(Minecraft_Test.getPlugin(Minecraft_Test.class), "Survivor.yml");
        infConfig = new ConfigUtil(Minecraft_Test.getPlugin(Minecraft_Test.class), "Infected.yml");
    }

    private void setInfSpawn(){
        infSpawn = new Location(
            Bukkit.getWorld(infConfig.getConfig().getString("spawn.world")),
            infConfig.getConfig().getDouble("spawn.x"),
            infConfig.getConfig().getDouble("spawn.y"),
            infConfig.getConfig().getDouble("spawn.z"),
            (float) infConfig.getConfig().getDouble("spawn.yaw"),
            (float) infConfig.getConfig().getDouble("spawn.pitch")
        );
    }
    public Location getInfSpawn(){
        setInfSpawn();
        return this.infSpawn;
    }

    private void setSurSpawn() {
            surSpawn = new Location(
            Bukkit.getWorld(surConfig.getConfig().getString("spawn.world")),
            surConfig.getConfig().getDouble("spawn.x"),
            surConfig.getConfig().getDouble("spawn.y"),
            surConfig.getConfig().getDouble("spawn.z"),
            (float) surConfig.getConfig().getDouble("spawn.yaw"),
            (float) surConfig.getConfig().getDouble("spawn.pitch")
        );
    }
    public Location getSurSpawn(){
        setSurSpawn();
        return this.surSpawn;
    }

    private void setDefaultSpawn(){
        defaultSpawn = new Location(
            Bukkit.getWorld(Minecraft_Test.getPlugin(Minecraft_Test.class).getConfig().getString("default-spawn.world")),
            Minecraft_Test.getPlugin(Minecraft_Test.class).getConfig().getDouble("default-spawn.x"),
            Minecraft_Test.getPlugin(Minecraft_Test.class).getConfig().getDouble("default-spawn.y"),
            Minecraft_Test.getPlugin(Minecraft_Test.class).getConfig().getDouble("default-spawn.z"),
            (float) Minecraft_Test.getPlugin(Minecraft_Test.class).getConfig().getDouble("default-spawn.pitch"),
            (float) Minecraft_Test.getPlugin(Minecraft_Test.class).getConfig().getDouble("default-spawn.yaw")
        );
    }
    public Location getDefaultSpawn(){
        setDefaultSpawn();
        return this.defaultSpawn;
    }

    private void setSurHealth(){
        this.surHealth = surConfig.getConfig().getInt("health");
    }
    public int getSurHealth(){
        setSurHealth();
        return this.surHealth;
    }

    private void setSurSpeed(){
        this.surSpeed = (float) surConfig.getConfig().getDouble("speed");
    }
    public float getSurSpeed(){
        setSurSpeed();
        return this.surSpeed;
    }

    private void setInfHealth(){
        this.infHealth = infConfig.getConfig().getInt("health");
    }
    public int getInfHealth(){
        setInfHealth();
        return this.infHealth;
    }

    private void setInfSpeed(){
        this.infSpeed = (float) infConfig.getConfig().getDouble("speed");
    }
    public float getInfSpeed() {
        setInfSpeed();
        return this.infSpeed;
    }

    private void setAllowedWorlds(){
        this.allowedWorlds = Minecraft_Test.getPlugin(Minecraft_Test.class).getConfig().getStringList("allowed-worlds");
    }
    private List<String> getAllowedWorlds(){
        setAllowedWorlds();
        return this.allowedWorlds;
    }

    /**
     * Scoreboards
     * setBoard is the in-game scoreboard.
     * waitBoard is the scoreboard for while in game lobby.
     * removeBoard removes the current scoreboard.
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
        if (statusMap.size() >= getMinPl()) {
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