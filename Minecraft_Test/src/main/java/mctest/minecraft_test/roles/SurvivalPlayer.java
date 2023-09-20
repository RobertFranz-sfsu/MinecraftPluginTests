/*
 * TODO
 *   S:
 *     Add YML configurability
 *       Infected/Survivor effects
 *     Make loadout list prettier
 *     Scoreboard styling
 *     Teleport players on game end after a countdown
 *     Redo config spawns
 *          world name (which is map name):
 *              multiple spawns, perm to view if applicable
 *   R:
 *     Infected join
 *       optional argument to specify which queue they want to join for multiple maps
 *       if no argument, then send them to any queue with people
 *       if no queue with people send to random map
 *     Command to manually set role
 *     Concurrent matches
 *
 *  Save inventory beforehand and give it back later
 *  Add option for loadouts to have prices attached to them
 *  Implement allowed worlds
 *    Must change playerhandler
 *    Announce winners
 *  Implement scores
 *    Save to player data so save as a file with the UUID as the file name (similar to essentials)
 *  Implement PAPI
 *  Multiple spawn locations
 *  Fix /spawn to /ispawn
 *  Change map in queue broadcast to map name which can be set in a config
 *  Add queue selector menu
 *    Add optional sub perms for viewable lobbies
 *  Add lobby selector menu
 *    Add optional sub perms for viewable lobbies
 *
 *  Remove old tests/code
 *  Move code to fresh repo lol
 *
 * */

package mctest.minecraft_test.roles;

import mctest.minecraft_test.Minecraft_Test;
import mctest.minecraft_test.util.ConfigUtil;
import mctest.minecraft_test.util.CountdownTimer;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.scoreboard.*;

import java.util.*;

public class SurvivalPlayer implements Listener{
    ConfigUtil surConfig = new ConfigUtil(Minecraft_Test.getPlugin(Minecraft_Test.class), "Survivor.yml");
    ConfigUtil infConfig = new ConfigUtil(Minecraft_Test.getPlugin(Minecraft_Test.class), "Infected.yml");
    private final HashMap<UUID, String> statusMap = new HashMap<>();
    private final HashMap<UUID, String> healthMap = new HashMap<>();
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
    private boolean scalingInf;
    private int infRatio;
    private double infMoneyReward;
    private double surMoneyReward;
    private List<String> infCommandRewards;
    private List<String> surCommandRewards;

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
    private Minecraft_Test plugin;

    public SurvivalPlayer(Minecraft_Test plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
        this.plugin = plugin;

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
                        // TODO
                        //  Change this to map name

                        String current = "";
                        //String current = games.getGameMap().getWorldName();

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
                statusMap.forEach((key, value) -> {
                    if (Objects.equals(healthMap.get(key), "alive")) {
                        this.setBoard(Objects.requireNonNull(Bukkit.getPlayer(key)));
                    }

                });
                //Bukkit.getLogger().info("Game in session");

                // TODO
                //  infected win
                //  If everyone is infected, infected won
//                if (this.getInfectedCnt() == statusMap.size()) {
//                    statusMap.forEach((key, value) ->  Bukkit.getPlayer(key).sendMessage("INFECTED WON!"));
//                    this.endGame("infected");
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
                    this.endGame("survivor");
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
            ArrayList<Integer> playerList = new ArrayList<>();
            for (int i = 0; i < statusMap.size(); i++) {
                playerList.add(i);
            }

            List pList;

            Collections.shuffle(playerList);
            if(isScalingInf() && getInfRatio() <= statusMap.size()){
                Bukkit.getLogger().info("RATIO: " + getInfRatio());
                pList = playerList.subList(0, getInfRatio());
            }else{
                Bukkit.getLogger().info("STARTING INF: " + getNumStartInf());
                pList = playerList.subList(0, getNumStartInf());
            }

            int iter = 0;
            for (Map.Entry<UUID, String> entry : statusMap.entrySet()) {
                entry.setValue((pList.contains(iter++)) ? "infected" : "survivor");
                this.setBoard(Objects.requireNonNull(Bukkit.getPlayer(entry.getKey())));
                this.setRole(Objects.requireNonNull(Bukkit.getPlayer(entry.getKey())));
            }

            Bukkit.getLogger().info(statusMap.toString());

            this.setPlaying(true);

            for (Map.Entry<UUID, String> entry : statusMap.entrySet()) {
                if(entry.getKey() != null){
                    Bukkit.dispatchCommand(Bukkit.getPlayer(entry.getKey()), "m");
                }
            }
        } catch (Exception e){
            Bukkit.getLogger().warning("Something went wrong trying to initialize the game.");
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

            player.sendMessage(ChatColor.translateAlternateColorCodes ('&', "&bYou are a &asurvivor&b!"));

            player.teleport(this.getSurSpawn());
        }
//        Bukkit.dispatchCommand(player, "m");
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

    public void removeEffects(Player player){
        ArrayList<PotionEffect> pe = new ArrayList<>(player.getActivePotionEffects());

        for(PotionEffect x : pe){
            player.removePotionEffect(x.getType());
        }
    }
    public void setNotPlaying(Player player) {
        Bukkit.getLogger().info(player.getName() + " is no longer playing!");

        player.setFoodLevel(20);
        this.setAttributes(player, .2f, 20, 20);

        this.removeBoard(player);
        statusMap.remove(player.getUniqueId());
        healthMap.remove(player.getUniqueId());
        removeEffects(player);
        player.sendMessage("No longer playing");
        //TODO Store players' items and give them back
    }

    public void setUnassigned(Player player) {
        if (getAllowedWorlds().contains(player.getWorld().getName())) {
            try {
                statusMap.forEach((key, value) -> Bukkit.getLogger().info(key + " " + value));
                statusMap.put(player.getUniqueId(), "unassigned");
                this.removeEffects(player);
            } catch (Exception e) {
                Bukkit.getLogger().warning("Something went wrong.");
                e.printStackTrace();
            }

            // TODO
            //  Why is this out here?? maybe move health map?
            statusMap.forEach((key, value) -> Bukkit.getLogger().info(key + " " + value));
            statusMap.put(player.getUniqueId(), "unassigned");
            healthMap.put(player.getUniqueId(), "alive");
        }
    }

    public void endGame() {
        try{
            Iterator<Map.Entry<UUID, String>> it = statusMap.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<UUID, String> entry = it.next();

                if (entry.getKey() == null) {
                    it.remove();
                } else {
                    Bukkit.getPlayer(entry.getKey()).getInventory().clear();
                    Bukkit.getPlayer(entry.getKey()).getInventory().setHelmet(null);
                    Bukkit.getPlayer(entry.getKey()).getInventory().setChestplate(null);
                    Bukkit.getPlayer(entry.getKey()).getInventory().setLeggings(null);
                    Bukkit.getPlayer(entry.getKey()).getInventory().setBoots(null);

                    Inventory inv = Bukkit.getPlayer(entry.getKey()).getInventory();
                    inv.clear();

                    removeEffects(Bukkit.getPlayer(entry.getKey()));

                    Bukkit.getPlayer(entry.getKey()).teleport(getDefaultSpawn());

                    this.setNotPlaying(Objects.requireNonNull(Bukkit.getPlayer(entry.getKey())));

                    Bukkit.getLogger().info(Bukkit.getPlayer(entry.getKey()).getName() + " successfully exited the game");
                    Bukkit.getPlayer(entry.getKey()).sendMessage("The game has ended.");
                }
            }

            this.setPlaying(false);
            this.setTimer(Integer.MIN_VALUE);
        }catch (Exception e){
            Bukkit.getLogger().warning("Something went wrong.");
            e.printStackTrace();
        }
//        this.setPlaying(false);
//        this.setTimer(Integer.MIN_VALUE);

    }

    public void endGame(String winner) {
        try{
            Iterator<Map.Entry<UUID, String>> it = statusMap.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<UUID, String> entry = it.next();

                if (entry.getKey() == null) {
                    it.remove();
                } else {
                    Bukkit.getPlayer(entry.getKey()).getInventory().clear();
                    Bukkit.getPlayer(entry.getKey()).getInventory().setHelmet(null);
                    Bukkit.getPlayer(entry.getKey()).getInventory().setChestplate(null);
                    Bukkit.getPlayer(entry.getKey()).getInventory().setLeggings(null);
                    Bukkit.getPlayer(entry.getKey()).getInventory().setBoots(null);

                    Inventory inv = Bukkit.getPlayer(entry.getKey()).getInventory();
                    inv.clear();

                    removeEffects(Bukkit.getPlayer(entry.getKey()));

                    Bukkit.getPlayer(entry.getKey()).teleport(getDefaultSpawn());

                    if(entry.getValue().equals(winner)){
                        giveRewards(Bukkit.getPlayer(entry.getKey()), winner);
                    }

                    this.setNotPlaying(Objects.requireNonNull(Bukkit.getPlayer(entry.getKey())));

                    Bukkit.getLogger().info(Bukkit.getPlayer(entry.getKey()).getName() + " successfully exited the game");
                    Bukkit.getPlayer(entry.getKey()).sendMessage("The game has ended.");
                }
            }

            this.setPlaying(false);
            this.setTimer(Integer.MIN_VALUE);
        }catch (Exception e){
            Bukkit.getLogger().warning("Something went wrong.");
            e.printStackTrace();
        }
//        this.setPlaying(false);
//        this.setTimer(Integer.MIN_VALUE);

    }

    public void giveRewards(Player player, String winner){
        double reward;

        if(winner.equals("survivor")){
            reward = getSurMoneyReward();
        }else if(winner.equals("infected")){
            reward = getInfMoneyReward();
        }else{
            reward = 0;
        }

        if(Minecraft_Test.getPlugin(Minecraft_Test.class).getEcon() != null){
            player.sendMessage(ChatColor.translateAlternateColorCodes ('&', "&fYou have been awarded &a" +
                    Minecraft_Test.getPlugin(Minecraft_Test.class).getConfig().getString("currency-symbol") + reward + "&f for winning!"));
            Minecraft_Test.getPlugin(Minecraft_Test.class).getEcon().depositPlayer(player.getName(), reward);
        }

        try{
            if(winner.equals("survivor") && !getSurCommandRewards().isEmpty()){
                for(String x : getSurCommandRewards()){
                    Bukkit.getLogger().info(x.replaceAll("PLAYER_NAME", Bukkit.getPlayer(player.getUniqueId()).getName()));
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), x.replaceAll("PLAYER_NAME", Bukkit.getPlayer(player.getUniqueId()).getName()));
                }
            }else if(winner.equals("infected") && !getInfCommandRewards().isEmpty()){
                for(String x : getInfCommandRewards()){
                    Bukkit.getLogger().info(x.replaceAll("PLAYER_NAME", Bukkit.getPlayer(player.getUniqueId()).getName()));
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), x.replaceAll("PLAYER_NAME", Bukkit.getPlayer(player.getUniqueId()).getName()));
                }
            }
        }catch (Exception e){
            Bukkit.getLogger().warning("Something went wrong.");
            e.printStackTrace();
        }
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
        Player player = (Player) damaged;

        // If shooter and target are on same team, cancel damage
        if(event.getCause().equals(EntityDamageEvent.DamageCause.PROJECTILE)){
            ProjectileSource attack = ((Projectile) event.getDamager()).getShooter();
            Entity victim = event.getEntity();

            if (!statusMap.containsKey(((Player)attack).getUniqueId()) && !statusMap.containsKey(victim.getUniqueId())) {
                return;
            }

            if((attack instanceof Player) && (victim instanceof Player)){
//                Bukkit.getLogger().severe("Attack: " + statusMap.get(((Player) attack).getUniqueId()));
//                Bukkit.getLogger().severe("Victim: " + statusMap.get(victim.getUniqueId()));

                if(Objects.equals(statusMap.get(((Player) attack).getUniqueId()), statusMap.get(victim.getUniqueId()))){
                    Bukkit.getLogger().severe("3");
                    event.setCancelled(true);
                }
            }
        }
        // If on same team, cancel damage
        else if (Objects.equals(statusMap.get(attacker.getUniqueId()), statusMap.get(damaged.getUniqueId()))) {
            event.setCancelled(true);
        }

        else if(event.getDamage() >= player.getHealth()){
//            Bukkit.getLogger().severe("Attacker: " + attacker + ", " + statusMap.get(attacker.getUniqueId()));
//            Bukkit.getLogger().severe("Damaged: " + damaged + ", " + statusMap.get(damaged.getUniqueId()));

            event.setCancelled(true);

            Bukkit.getLogger().info("Player:  " + player.getName() + "  has died ");
            if(Objects.equals(statusMap.get(player.getUniqueId()).toLowerCase(), "survivor")){
                statusMap.put(player.getUniqueId(), "infected");
            }

            this.removeEffects(player);
            this.setRole(player);
            //player.teleport(getInfSpawn());
            //healthMap.put(player.getUniqueId(), "dead");

            new CountdownTimer(this.plugin, this.getRespawnTime(),
                    // What happens at the start
                    () -> {
                        healthMap.put(player.getUniqueId(), "dead");
                        player.setWalkSpeed(0);
                    },
                    // What happens at the end
                    () -> {
                        if (this.getPlaying()) {
                            healthMap.put(player.getUniqueId(), "alive");
                            player.setWalkSpeed(this.getInfSpeed());
                        }
                    },
                    // What happens during each tick
                    (t) -> {
                        if (this.getPlaying()) {
                            this.respawnBoard(player, t.getSecondsLeft());
                        }
                    }).scheduleTimer();

//                new DelayedTask(() -> {
//                    healthMap.put(player.getUniqueId(), "alive");
//                    player.setWalkSpeed(this.getInfSpeed());
//                }, 20L * this.getRespawnTime());
        }
    }

    @EventHandler
    private void disableMovement(PlayerMoveEvent event) {
        if (!statusMap.containsKey(event.getPlayer().getUniqueId())) {
            return;
        }
        if (Objects.equals(healthMap.get(event.getPlayer().getUniqueId()), "dead")) {
            if (event.getTo().getY() > event.getFrom().getY()) {
                event.setCancelled(true);
            }
        }
    }

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

    private void setScalingInf(){
        this.scalingInf = Minecraft_Test.getPlugin(Minecraft_Test.class).getConfig().getBoolean("use-scaling-infected");
    }
    private boolean isScalingInf(){
        setScalingInf();
        return this.scalingInf;
    }

    private void setInfMoneyReward(){
        this.infMoneyReward = infConfig.getConfig().getDouble("inf-money-reward");
    }
    private double getInfMoneyReward(){
        setInfMoneyReward();
        return this.infMoneyReward;
    }

    private void setSurMoneyReward(){
        this.surMoneyReward = surConfig.getConfig().getDouble("sur-money-reward");
    }
    private double getSurMoneyReward(){
        setSurMoneyReward();
        return this.surMoneyReward;
    }

    private void setInfRatio(){
        String str = Minecraft_Test.getPlugin(Minecraft_Test.class).getConfig().getString("infected-ratio");
        String[] ratio = str.split("-");

        if(Minecraft_Test.getPlugin(Minecraft_Test.class).getConfig().getBoolean("ratio-rounds-up")){
            Bukkit.getLogger().info("ROUNDING UP");
            this.infRatio = (int) Math.ceil((Double.parseDouble(ratio[1])/Double.parseDouble(ratio[0])));
        }else{
            Bukkit.getLogger().info("ROUNDING DOWN");
            this.infRatio = (int) Math.floor((Double.parseDouble(ratio[1])/Double.parseDouble(ratio[0])));
        }
    }
    private int getInfRatio(){
        setInfRatio();
        return infRatio;
    }

    private void setInfCommandRewards(){
        infCommandRewards = infConfig.getConfig().getStringList("inf-win-commands");
    }
    private List<String> getInfCommandRewards(){
        setInfCommandRewards();
        return this.infCommandRewards;
    }

    private void setSurCommandRewards(){
        surCommandRewards = surConfig.getConfig().getStringList("sur-win-commands");
    }
    private List<String> getSurCommandRewards(){
        setSurCommandRewards();
        return this.surCommandRewards;
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
        newLine1.setScore(6);

//        if (Objects.equals(healthMap.get(player.getUniqueId()), "dead")) {
//            String ellipsis = ".";
//            ellipsis =  new String(new char[3-(this.getTimer() % 4)]).replace("\0", ellipsis);
//            Score wait = objective.getScore("Waiting to respawn" + ellipsis);
//            wait.setScore(5);
//        }


        String minutes = String.valueOf(this.getTimer() / 60);
        String seconds = ((this.getTimer()%60 < 10) ? "0" : "") + this.getTimer()%60 ;
        Score timer = objective.getScore("Time left in game: " + minutes + ":" + seconds);
        timer.setScore(4);
        Score newLine2 = objective.getScore("");
        newLine2.setScore(3);

        Score survivorScore = objective.getScore(ChatColor.GREEN + "Survivors: " + this.getSurvivorCnt());
        survivorScore.setScore(2);
        Score infectedScore = objective.getScore(ChatColor.RED + "Infected:   " + this.getInfectedCnt());
        infectedScore.setScore(1);

        player.setScoreboard(scoreboard);
    }

    private  void respawnBoard(Player player, int sec) {
        if (!statusMap.containsKey(player.getUniqueId())) {
            return;
        }
        if (!statusMap.containsKey(player.getUniqueId())) {
            return;
        }
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        Scoreboard scoreboard = manager.getNewScoreboard();

        Objective objective = scoreboard.registerNewObjective("Game Status", "dummy");
        objective.setDisplayName(ChatColor.GOLD + "Survival Status");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        Score newLine1 = objective.getScore("");
        newLine1.setScore(6);

        String ellipsis = ".";
        ellipsis =  new String(new char[3-(this.getTimer() % 4)]).replace("\0", ellipsis);
        Score wait = objective.getScore("Respawning in: " + sec + ellipsis);
        wait.setScore(5);

        String minutes = String.valueOf(this.getTimer() / 60);
        String seconds = ((this.getTimer()%60 < 10) ? "0" : "") + this.getTimer()%60 ;
        Score timer = objective.getScore("Time left in game: " + minutes + ":" + seconds);
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