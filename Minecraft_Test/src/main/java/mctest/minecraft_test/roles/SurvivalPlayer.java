/*
 * TODO
 *   S:
 *   R:
 *
 *  Implement scores
 *    Save to player data so save as a file with the UUID as the file name (similar to essentials)
 *      Games won as survivor
 *      Games won as infected
 *      Kills as survivor/infected
 *      Number of games played
 *      Top players
 *  Implement PAPI
 *  Fix /spawn to /ispawn
 *  Change /menu to /imenu
 *  Add player stats to last row of menu
 *  Fix end command
 *  Command to add specific games
 *  Implement switching loadout switch
 *
 *  Remove old tests/code
 *  Move code to fresh repo lol
 *  Add version checker
 *
 * */

package mctest.minecraft_test.roles;

import mctest.minecraft_test.Minecraft_Test;
import mctest.minecraft_test.util.ConfigUtil;
import mctest.minecraft_test.util.CountdownTimer;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.scoreboard.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@SuppressWarnings({"rawtypes", "FieldMayBeFinal", "CallToPrintStackTrace", "deprecation"})
public class SurvivalPlayer implements Listener {
    ConfigUtil surConfig = new ConfigUtil(Minecraft_Test.getPlugin(Minecraft_Test.class), "Survivor.yml");
    ConfigUtil infConfig = new ConfigUtil(Minecraft_Test.getPlugin(Minecraft_Test.class), "Infected.yml");
    private final ConcurrentHashMap<UUID, String> statusMap = new ConcurrentHashMap<>();
    private final HashMap<UUID, String> healthMap = new HashMap<>();
    public HashMap<UUID, String> previousWorlds = new HashMap<>();
    private HashMap<UUID, ItemStack[]> previousInventory = new HashMap<>();
    public HashMap<UUID, Integer> survivorKills = new HashMap<>();
    public HashMap<UUID, Integer> infectedKills = new HashMap<>();

    private int infectedCnt = 0;
    private int survivorCnt = 0;
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
    private boolean scalingInf;
    private boolean playing = false;
    private int infRatio;
    private double infMoneyReward;
    private double surMoneyReward;
    private List<String> infCommandRewards;
    private List<String> surCommandRewards;
    private List<String> allowedWorlds;
    private List<String> lobbies;
    private Minecraft_Test plugin;
    private String currentWorld;
    private Integer gameID;
    private boolean createNew = true;
    private int endTime;
    public Integer getGameID() {
        return this.gameID;
    }

    public void setPlaying(Boolean playing) {
        this.playing = playing;
    }

    public boolean getPlaying() {
        return this.playing;
    }

    public void setTimer(int amount) {
        this.time = amount;
    }

    public int getTimer() {
        return this.time;
    }

    public ConcurrentHashMap<UUID, String> getStatusMap() {
        return this.statusMap;
    }


//    public HashMap<UUID, String> getStatusMap() {
//        return this.statusMap;
//    }

    public void addPreviousWorld(UUID player, String world){
        previousWorlds.put(player, world);
    }

    public SurvivalPlayer(Minecraft_Test plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
        this.plugin = plugin;
        this.setValues();

        this.gameID = plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, () -> {

            // if timer is set, start counting down
            if (this.getTimer() > 0) {
                if (this.getTimer() % 5 == 0) {
                    String str = (this.getPlaying() ? "Game " : "Queue ");
                    Bukkit.getLogger().info(str + "Timer: " + this.getTimer());
                }
                setTimer(this.getTimer() - 1);
            }

            if (!this.getPlaying()) {
                // if minimum amount of players have joined, start timer
                if (statusMap.size() == this.getMinPl() && this.getTimer() == Integer.MIN_VALUE) {
                    List<String> val = plugin.getConfig().getStringList("lobby-worlds");
                    Bukkit.getLogger().info("Min amount of players joined: Timer Started!");
                    setTimer(getWaitTime());

                    // Print message that queue has begun in specified world as long as broadcast is enabled in config
                    if (plugin.getConfig().getBoolean("queue-start-broadcast-enabled")) {
                        // TODO
                        //  Change this to map name

                        String current = this.getCurrentWorld();

                        String msg = ChatColor.translateAlternateColorCodes('&', "&aAn &cInfected &aqueue has begun in &c" + current + "&a!");

                        for (String w : val) {
                            if (Bukkit.getWorld(w) != null) {
                                Objects.requireNonNull(Bukkit.getWorld(w)).getPlayers().forEach(player -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                                        "tellraw " + player.getUniqueId() + " {" +
                                                "\"text\": \"" + msg + "\"," +
                                                "\"hoverEvent\": {" +
                                                "\"action\": \"show_text\"," +
                                                "\"value\": \"Shift click to copy join command to chat\"" +
                                                "}," +
                                                "\"insertion\": \"" + "/infected join " + current + "\"" +
                                                "}"));
                            }
                        }
                    }
                }

                statusMap.forEach((key, value) -> this.waitBoard(Objects.requireNonNull(Bukkit.getPlayer(key))));
                // if max amount of players have joined or if the timer has hit 0, start the game
                if (statusMap.size() == this.getMaxPl() || this.getTimer() == 0) {
                    Bukkit.getLogger().info("Game Starting");
                    statusMap.forEach((key, value) -> Objects.requireNonNull(Bukkit.getPlayer(key)).sendMessage("Game starting"));
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

                //  If everyone is infected, infected won
                if ((this.getInfectedCnt() == statusMap.size()) && createNew) {
                    CountdownTimer time = new CountdownTimer(this.plugin, this.getEndTime(),
                            // What happens at the start
                            () -> {
                                statusMap.forEach((key, value) -> {
                                    Objects.requireNonNull(Bukkit.getPlayer(key)).sendMessage(
                                            ChatColor.translateAlternateColorCodes('&', "&cInfected won!")
                                    );
                                    if (Objects.equals(value, "infected")) {
                                        Objects.requireNonNull(Bukkit.getPlayer(key)).sendMessage(
                                                ChatColor.translateAlternateColorCodes('&', "&c&lYou won!")
                                        );
                                        //TODO Anything else you want winners to get
                                    }
                                });
                            },
                            // What happens at the end
                            () -> {
                                this.endGame("infected");
                            },
                            // What happens during each tick
                            (t) -> {
                                Objects.requireNonNull(Bukkit.getWorld(currentWorld)).getPlayers().forEach(player ->
                                        player.sendMessage("Returning in: " + t.getSecondsLeft()));
                            });

                    time.scheduleTimer();
                    createNew = false;
                }

                // If timer runs out, survivors won
                if ((this.getTimer() == 0) && createNew) {
                    CountdownTimer time = new CountdownTimer(this.plugin, this.getEndTime(),
                            // What happens at the start
                            () -> {
                                statusMap.forEach((key, value) -> {
                                    Objects.requireNonNull(Bukkit.getPlayer(key)).sendMessage(
                                            ChatColor.translateAlternateColorCodes('&', "&aSurvivors won!")
                                    );
                                    if (Objects.equals(value, "survivor")) {
                                        Objects.requireNonNull(Bukkit.getPlayer(key)).sendMessage(
                                                ChatColor.translateAlternateColorCodes('&', "&2&lYou won!")
                                        );
                                        //TODO Anything else you want winners to get
                                    }
                                });
                            },
                            // What happens at the end
                            () -> {
                                this.endGame("survivor");
                            },
                            // What happens during each tick
                            (t) -> {
                                Objects.requireNonNull(Bukkit.getWorld(currentWorld)).getPlayers().forEach(player ->
                                        player.sendMessage("Returning in: " + t.getSecondsLeft()));
                            });

                    time.scheduleTimer();
                    createNew = false;
                }

                // Game ended by admin
                if (this.getTimer() == -42) {
                    CountdownTimer time = new CountdownTimer(this.plugin, this.getEndTime(),
                            // What happens at the start
                            () -> {
                                statusMap.forEach((key, value) -> {
                                    Objects.requireNonNull(Bukkit.getPlayer(key)).sendMessage("Game has been manually ended.");
                                });
                            },
                            // What happens at the end
                            () -> {
                                this.endGame();
                            },
                            // What happens during each tick
                            (t) -> {
                                Objects.requireNonNull(Bukkit.getWorld(currentWorld)).getPlayers().forEach(player ->
                                        player.sendMessage("Returning in: " + t.getSecondsLeft()));
                            });

                    time.scheduleTimer();
                    createNew = false;
                }

                if (!this.getPlaying() && !statusMap.isEmpty()){

                }
            }
        }, 0L, 20L);
    }

    public void gameInit() {
        //TODO
        // Set players as infected or survivor depending on amount playing,
        createNew = true;

        try {
            ArrayList<Integer> playerList = new ArrayList<>();
            for (int i = 0; i < statusMap.size(); i++) {
                playerList.add(i);
            }

            Bukkit.getLogger().severe("BEFORE");
            for(Map.Entry<UUID, String> entry : statusMap.entrySet()){
                Bukkit.getLogger().severe("PLAYER: " + Bukkit.getPlayer(entry.getKey()) + ", STATUS: " + entry.getValue());
            }

            List pList;

            Collections.shuffle(playerList);
            if (isScalingInf() && getInfRatio() <= statusMap.size()) {
                Bukkit.getLogger().info("RATIO: " + getInfRatio());
                pList = playerList.subList(0, getInfRatio());
            } else {
                Bukkit.getLogger().info("STARTING INF: " + getNumStartInf());
                pList = playerList.subList(0, getNumStartInf());
            }

            int iter = 0;

            for (Map.Entry<UUID, String> entry : statusMap.entrySet()) {
                entry.setValue((pList.contains(iter++)) ? "infected" : "survivor");
                this.saveInventory(Objects.requireNonNull(Bukkit.getPlayer(entry.getKey())));
                this.clearInventory(Objects.requireNonNull(Bukkit.getPlayer(entry.getKey())));
                this.setBoard(Objects.requireNonNull(Bukkit.getPlayer(entry.getKey())));
                this.setRole(Objects.requireNonNull(Bukkit.getPlayer(entry.getKey())));

                if(entry.getValue().equalsIgnoreCase("infected")){
                    infectedKills.put(entry.getKey(), 0);
                }else if(entry.getValue().equalsIgnoreCase("survivor")){
                    survivorKills.put(entry.getKey(), 0);
                }
            }

            Bukkit.getLogger().info(statusMap.toString());

            this.setPlaying(true);

            Bukkit.getLogger().severe("AFTER");
            for(Map.Entry<UUID, String> entry : statusMap.entrySet()){
                Bukkit.getLogger().severe("PLAYER: " + Bukkit.getPlayer(entry.getKey()) + ", STATUS: " + entry.getValue());
            }

            for (Map.Entry<UUID, String> entry : statusMap.entrySet()) {
                if (entry.getKey() != null) {
                    Bukkit.dispatchCommand(Objects.requireNonNull(Bukkit.getPlayer(entry.getKey())), "m");
                }
            }
        } catch (Exception e) {
            Bukkit.getLogger().warning("Something went wrong trying to initialize the game.");
            e.printStackTrace();
        }
    }

    public void setRole(Player player) {
        if (Objects.equals(statusMap.get(player.getUniqueId()), "infected")) {
            this.setAttributes(player, this.getInfSpeed(), this.getInfHealth(), this.getInfHealth());

            if (!Objects.equals(infConfig.getConfig().get("effects"), null)) {
                setEffects(player);
            } else {
                Bukkit.getLogger().info("No infected effects to apply.");
            }

            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&bYou are &cinfected&b!"));
//            infected.addPlayer(player);

            player.teleport(this.getInfSpawn());
//            if(plugin.getConfig().getBoolean("hide-nametags")){
//                this.hideNames(player);
//            }
        } else if (Objects.equals(statusMap.get(player.getUniqueId()), "survivor")) {
            this.setAttributes(player, this.getSurSpeed(), this.getSurHealth(), this.getSurHealth());

            if (!Objects.equals(surConfig.getConfig().get("effects"), null)) {
                setEffects(player);
            } else {
                Bukkit.getLogger().info("No survivor effects to apply.");
            }

            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&bYou are a &asurvivor&b!"));
//            survivors.addPlayer(player);

            player.teleport(this.getSurSpawn());
//            if(plugin.getConfig().getBoolean("hide-nametags")){
//                this.hideNames(player);
//            }
        }
    }

    private void setAttributes(Player player, Float speed, int maxHealth, int health) {
        player.setWalkSpeed(speed);
        if(plugin.getIs18()){
            player.setMaxHealth(maxHealth);
        }else{
            Objects.requireNonNull(player.getAttribute(Attribute.GENERIC_MAX_HEALTH)).setBaseValue(maxHealth);
        }
        player.setHealth(health);
    }

    private void setEffects(Player player) {
        if (Objects.equals(statusMap.get(player.getUniqueId()), "infected")) {
            for (String x : Objects.requireNonNull(infConfig.getConfig().getConfigurationSection("effects")).getKeys(false)) {
                String path = ("effects." + x);
                boolean force = false;

                int duration;
                if (Objects.requireNonNull(infConfig.getConfig().getString((path + ".duration"))).equalsIgnoreCase("INFINITE")) {
                    duration = Integer.MAX_VALUE;
                    force = true;
                } else {
                    duration = infConfig.getConfig().getInt(path + ".duration");
                }

                if(plugin.getIs18()){
                    player.addPotionEffect(new PotionEffect(Objects.requireNonNull(PotionEffectType.getByName(x)),
                                    duration,
                                    infConfig.getConfig().getInt(path + ".level")),
                            force);
                }else{
                    player.addPotionEffect(new PotionEffect(Objects.requireNonNull(PotionEffectType.getByName(x)),
                                    duration,
                                    infConfig.getConfig().getInt(path + ".level")));
                }
            }
        } else if (Objects.equals(statusMap.get(player.getUniqueId()), "survivor")) {
            for (String x : Objects.requireNonNull(surConfig.getConfig().getConfigurationSection("effects")).getKeys(false)) {
                String path = ("effects." + x);
                boolean force = false;

                int duration;
                if (Objects.requireNonNull(surConfig.getConfig().getString((path + ".duration"))).equalsIgnoreCase("INFINITE")) {
                    duration = Integer.MAX_VALUE;
                    force = true;
                } else {
                    duration = surConfig.getConfig().getInt(path + ".duration");
                }

                if(plugin.getIs18()){
                    player.addPotionEffect(new PotionEffect(Objects.requireNonNull(PotionEffectType.getByName(x)),
                                    duration,
                                    surConfig.getConfig().getInt(path + ".level")),
                            force);
                }else{
                    player.addPotionEffect(new PotionEffect(Objects.requireNonNull(PotionEffectType.getByName(x)),
                            duration,
                            infConfig.getConfig().getInt(path + ".level")));
                }
            }
        }
    }

    public void removeEffects(Player player) {
        ArrayList<PotionEffect> pe = new ArrayList<>(player.getActivePotionEffects());

        for (PotionEffect x : pe) {
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

//        if(statusMap.get(player.getUniqueId()).equalsIgnoreCase("infected")){
//            infected.removePlayer(player);
//        }else if(statusMap.get(player.getUniqueId()).equalsIgnoreCase("survivor")){
//            survivors.removePlayer(player);
//        }

//        if(plugin.getConfig().getBoolean("hide-nametags")){
//            this.showNames(player);
//        }

        if (Objects.requireNonNull(previousInventory).containsKey(player.getUniqueId())) {
            previousInventory.remove(player.getUniqueId());
        }

        removeEffects(player);
        player.sendMessage("No longer playing");
        //TODO Store players' items and give them back
    }

    public void setUnassigned(Player player) {
        plugin.getGameIDMap().put(player.getUniqueId(), this.getGameID());
        if (getAllowedWorlds().contains(player.getWorld().getName())) {
            try {
                statusMap.forEach((key, value) -> Bukkit.getLogger().info(key + " " + value));
                statusMap.put(player.getUniqueId(), "unassigned");
                healthMap.put(player.getUniqueId(), "alive");
                previousWorlds.put(player.getUniqueId(), player.getWorld().getName());

//                if(plugin.getConfig().getBoolean("hide-nametags")){
//                    this.showNames(player);
//                }

                this.removeEffects(player);
            } catch (Exception e) {
                Bukkit.getLogger().warning("Something went wrong.");
                e.printStackTrace();
            }
        }
    }

    public void endGame() {
        try {
            Iterator<Map.Entry<UUID, String>> it = statusMap.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<UUID, String> entry = it.next();

                if (entry.getKey() == null) {
                    it.remove();
                } else {
                    clearInventory(Objects.requireNonNull(Bukkit.getPlayer(entry.getKey())));

                    removeEffects(Objects.requireNonNull(Bukkit.getPlayer(entry.getKey())));

                    if(plugin.doKeepScore()){
                        this.setScores(entry.getKey());
                    }

                    if (this.getLobbies().isEmpty()) {
                        Bukkit.getLogger().severe("There are no lobbies set!");
                    }

                    if (!previousWorlds.keySet().isEmpty()) {
                        String world = (this.getLobbies().contains(previousWorlds.get(entry.getKey())) ? previousWorlds.get(entry.getKey()) : this.getLobbies().get(0));
                        Objects.requireNonNull(Bukkit.getPlayer(entry.getKey())).teleport(this.getDefaultSpawn(world));
                    } else {
                        Objects.requireNonNull(Bukkit.getPlayer(entry.getKey())).teleport(this.getDefaultSpawn(this.getLobbies().get(0)));
                    }

                    this.giveInventory(Objects.requireNonNull(Bukkit.getPlayer(entry.getKey())));

                    this.setNotPlaying(Objects.requireNonNull(Bukkit.getPlayer(entry.getKey())));

                    Bukkit.getLogger().info(Objects.requireNonNull(Bukkit.getPlayer(entry.getKey())).getName() + " successfully exited the game");
                    Objects.requireNonNull(Bukkit.getPlayer(entry.getKey())).sendMessage("The game has ended.");
                }
            }

            infectedKills.clear();
            survivorKills.clear();
            previousWorlds.clear();

            this.setTimer(Integer.MIN_VALUE);
            this.setPlaying(false);
        } catch (Exception e) {
            Bukkit.getLogger().severe("Something went wrong trying to end the game.");
            Bukkit.getLogger().severe("Get cause:");
            e.getCause();

            Bukkit.getLogger().severe("Stack Trace:");
            e.printStackTrace();
        }
    }

    public void endGame(String winner) {
        try {
            Iterator<Map.Entry<UUID, String>> it = statusMap.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<UUID, String> entry = it.next();

                if (entry.getKey() == null) {
                    it.remove();
                } else {
                    clearInventory(Objects.requireNonNull(Bukkit.getPlayer(entry.getKey())));

                    removeEffects(Objects.requireNonNull(Bukkit.getPlayer(entry.getKey())));

                    if(plugin.doKeepScore()){
                        this.setScores(entry.getKey());
                    }

                    if (this.getLobbies().isEmpty()) {
                        Bukkit.getLogger().severe("There are no lobbies set!");
                    }

                    if (!previousWorlds.keySet().isEmpty()) {
                        String world = (this.getLobbies().contains(previousWorlds.get(entry.getKey())) ? previousWorlds.get(entry.getKey()) : this.getLobbies().get(0));
                        Objects.requireNonNull(Bukkit.getPlayer(entry.getKey())).teleport(getDefaultSpawn(world));
                    } else {
                        Objects.requireNonNull(Bukkit.getPlayer(entry.getKey())).teleport(getDefaultSpawn(this.getLobbies().get(0)));
                    }

                    this.giveInventory(Objects.requireNonNull(Bukkit.getPlayer(entry.getKey())));

                    if (entry.getValue().equals(winner)) {
                        giveRewards(Bukkit.getPlayer(entry.getKey()), winner);
                        if(plugin.doKeepScore()){
                            this.setGamesWon(entry.getKey(), winner);
                        }
                    }

                    this.setNotPlaying(Objects.requireNonNull(Bukkit.getPlayer(entry.getKey())));

                    Bukkit.getLogger().info(Objects.requireNonNull(Bukkit.getPlayer(entry.getKey())).getName() + " successfully exited the game");
                    Objects.requireNonNull(Bukkit.getPlayer(entry.getKey())).sendMessage("The game has ended.");
                }
            }

            this.setTimer(Integer.MIN_VALUE);
            this.setPlaying(false);
        } catch (Exception e) {
            Bukkit.getLogger().severe("Something went wrong trying to end the game.");
            Bukkit.getLogger().severe("Get cause:");
            e.getCause();

            Bukkit.getLogger().severe("Stack Trace:");
            e.printStackTrace();
        }
    }

    public void clearInventory(Player player) {
        player.getInventory().clear();
        player.getInventory().setHelmet(null);
        player.getInventory().setChestplate(null);
        player.getInventory().setLeggings(null);
        player.getInventory().setBoots(null);

        Inventory inv = player.getInventory();
        inv.clear();
    }

    public void saveInventory(Player player) {
        int count = player.getInventory().getSize() + 4;
        ItemStack[] inv = new ItemStack[count];

        for (int i = 0; i < player.getInventory().getSize(); i++) {
            inv[i] = player.getInventory().getItem(i);
        }

        if (player.getInventory().getHelmet() != null) {
            inv[count - 4] = player.getInventory().getHelmet();
        }

        if (player.getInventory().getChestplate() != null) {
            inv[count - 3] = player.getInventory().getChestplate();
        }

        if (player.getInventory().getLeggings() != null) {
            inv[count - 2] = player.getInventory().getLeggings();
        }

        if (player.getInventory().getBoots() != null) {
            inv[count - 1] = player.getInventory().getBoots();
        }

        previousInventory.put(player.getUniqueId(), inv.clone());
    }


    public void giveInventory(Player player){
        ItemStack[] inv = previousInventory.get(player.getUniqueId());
        int count = player.getInventory().getSize() + 4;

        for(int i = 0 ; i < count ; i++) {
            if(i < (count-4)){
                player.getInventory().setItem(i, inv[i]);
            } else if(i == (count - 4)){
                player.getInventory().setHelmet(inv[i]);
            }else if(i == (count - 3)){
                player.getInventory().setChestplate(inv[i]);
            }else if(i == (count - 2)){
                player.getInventory().setLeggings(inv[i]);
            }else if(i == (count- 1)){
                player.getInventory().setBoots(inv[i]);
            }
        }
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

        if(plugin.getEcon() != null){
            player.sendMessage(ChatColor.translateAlternateColorCodes ('&', "&fYou have been awarded &a" +
                    plugin.getConfig().getString("currency-symbol") + reward + "&f for winning!"));
            plugin.getEcon().depositPlayer(player, reward);
        }

        try{
            if(winner.equals("survivor") && !getSurCommandRewards().isEmpty()){
                for(String x : getSurCommandRewards()){
                    Bukkit.getLogger().info(x.replaceAll("PLAYER_NAME", Objects.requireNonNull(Bukkit.getPlayer(player.getUniqueId())).getName()));
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), x.replaceAll("PLAYER_NAME", Objects.requireNonNull(Bukkit.getPlayer(player.getUniqueId())).getName()));
                }
            }else if(winner.equals("infected") && !getInfCommandRewards().isEmpty()){
                for(String x : getInfCommandRewards()){
                    Bukkit.getLogger().info(x.replaceAll("PLAYER_NAME", Objects.requireNonNull(Bukkit.getPlayer(player.getUniqueId())).getName()));
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), x.replaceAll("PLAYER_NAME", Objects.requireNonNull(Bukkit.getPlayer(player.getUniqueId())).getName()));
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
     * onPlayerDeath makes sure players' items don't drop on the ground.
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
    private void onPlayerDamage(EntityDamageByEntityEvent event) {
        if(getPlaying()){
            try {
                Entity attacker = event.getDamager();
                Entity damaged = event.getEntity();
                Player player = (Player) damaged;

                // If shooter and target are on same team, cancel damage
                if((event.getCause().equals(EntityDamageEvent.DamageCause.PROJECTILE))){
                    ProjectileSource attack = ((Projectile) event.getDamager()).getShooter();
                    Entity victim = event.getEntity();

                    Bukkit.getLogger().severe("PLAYER: " + player.getHealth() + ", DAMAGE: " + event.getDamage());

                    if (!statusMap.containsKey(Objects.requireNonNull(((Player)attack)).getUniqueId()) && !statusMap.containsKey(victim.getUniqueId())) {
                        return;
                    }

                    if((victim instanceof Player)){
                        if(Objects.equals(statusMap.get(((Player) attack).getUniqueId()), statusMap.get(victim.getUniqueId()))){
                            event.setCancelled(true);
                        }else if(event.getDamage() >= player.getHealth()){
                            event.setCancelled(true);

                            if(plugin.doKeepScore()){
                                Player killer = (Player) attacker;

                                if(plugin.doInfectedKills() && Objects.equals(statusMap.get(killer.getUniqueId()), "infected")){
                                    int k = infectedKills.get(killer.getUniqueId()) + 1;
                                    infectedKills.put(killer.getUniqueId(), k);
                                }

                                if(plugin.doSurvivorKills() && Objects.equals(statusMap.get(killer.getUniqueId()), "survivor")){
                                    int k = survivorKills.get(killer.getUniqueId()) + 1;
                                    survivorKills.put(killer.getUniqueId(), k);
                                }
                            }

                            Bukkit.getLogger().info("Player:  " + player.getName() + "  has died ");
                            if(Objects.equals(statusMap.get(player.getUniqueId()).toLowerCase(), "survivor")){
                                statusMap.put(player.getUniqueId(), "infected");
                                this.clearInventory(player);
                            }

                            this.removeEffects(player);
                            this.setRole(player);

                            player.teleport(this.getInfSpawn());

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
                        }
                    }
                }
                // If on same team, cancel damage
                else if (Objects.equals(statusMap.get(attacker.getUniqueId()), statusMap.get(damaged.getUniqueId())) && this.getPlaying()) {
                    event.setCancelled(true);
                }

                else if(event.getDamage() >= player.getHealth()){
                    event.setCancelled(true);

                    if(plugin.doKeepScore()){
                        Player killer = (Player) attacker;

                        if(plugin.doInfectedKills() && Objects.equals(statusMap.get(killer.getUniqueId()), "infected")){
                            int k = infectedKills.get(killer.getUniqueId()) + 1;
                            infectedKills.put(killer.getUniqueId(), k);
                        }

                        if(plugin.doSurvivorKills() && Objects.equals(statusMap.get(killer.getUniqueId()), "survivor")){
                            int k = survivorKills.get(killer.getUniqueId()) + 1;
                            survivorKills.put(killer.getUniqueId(), k);
                        }
                    }

                    Bukkit.getLogger().info("Player:  " + player.getName() + "  has died ");
                    if(Objects.equals(statusMap.get(player.getUniqueId()).toLowerCase(), "survivor")){
                        statusMap.put(player.getUniqueId(), "infected");
                        this.clearInventory(player);
                    }

                    this.removeEffects(player);
                    this.setRole(player);

                    player.teleport(this.getInfSpawn());

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
                }
            } catch (Exception e) {
//            Bukkit.getLogger().info("Mob attacking mob");
            }
        }
    }

    @EventHandler
    private void disableMovement(PlayerMoveEvent event) {
        if (!statusMap.containsKey(event.getPlayer().getUniqueId())) {
            return;
        }
        if (Objects.equals(healthMap.get(event.getPlayer().getUniqueId()), "dead")) {
            if (Objects.requireNonNull(event.getTo()).getY() > event.getFrom().getY()) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    private void inventoryOpen(InventoryOpenEvent event){
        if(getPlaying()){
            Player player = (Player) event.getPlayer();
            setEffects(player);
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
        this.waitTime = plugin.getConfig().getInt("wait-timer");
    }
    private int getWaitTime(){
//        this.setWaitTime();
        return this.waitTime;
    }

    // Match length
    private void setGameTime(){
        this.gameTime = plugin.getConfig().getInt("match-length");
    }
    private int getGameTime(){
//        this.setGameTime();
        return this.gameTime;
    }

    private void setMaxPl(){
        this.maxPl = plugin.getConfig().getInt("max-players");
    }
    public int getMaxPl(){
//        this.setMaxPl();
        return this.maxPl;
    }

    private void setMinPl(){
        this.minPl = plugin.getConfig().getInt("min-players");
    }
    private int getMinPl(){
//        this.setMinPl();
        return this.minPl;
    }

    private void setNumStartInf(){
        this.numStartInf = plugin.getConfig().getInt("num-starting-infected");
    }
    private int getNumStartInf(){
//        this.setNumStartInf();
        return this.numStartInf;
    }

    private void setRespawnTime(){
        this.respawnTime = plugin.getConfig().getInt("respawn-timer");
    }
    private int getRespawnTime(){
        this.setRespawnTime();
        return this.respawnTime;
    }

    private void setValues(){
        this.setNumStartInf();
        this.setMinPl();
        this.setMaxPl();
        this.setGameTime();
        this.setWaitTime();
    }

    public void reloadConfigs(){
        surConfig = new ConfigUtil(plugin, "Survivor.yml");
        infConfig = new ConfigUtil(plugin, "Infected.yml");

        this.setValues();
    }

    private void setGameSpawn(String type){
        String world = null;
        String path = "spawns.";
        List<String> labels = new ArrayList<>();
        List chosen;

        int check = 0;
        for(UUID x : statusMap.keySet()){
            if(!Objects.equals(x, null)){
                world = Objects.requireNonNull(Bukkit.getPlayer(x)).getWorld().getName();
            }

            if((check != 0) && (Objects.equals(Objects.requireNonNull(Bukkit.getPlayer(x)).getWorld().getName(), world))){
                break;
            }

            check++;
        }

        if(Objects.equals(world, null)){
            Bukkit.getLogger().warning("Something went wrong trying to get the world.");
            return;
        }

        path += world;

        if(type.equals("survivor")){
            for(String x : Objects.requireNonNull(surConfig.getConfig().getConfigurationSection(path)).getKeys(false)){
                if(x != null){
                    labels.add(x);
                }
            }
        }else if(type.equals("infected")){
            for(String x : Objects.requireNonNull(infConfig.getConfig().getConfigurationSection(path)).getKeys(false)){
                if(x != null){
                    labels.add(x);
                }
            }
        }

        if(!labels.isEmpty()){
            Collections.shuffle(labels);
            chosen = labels.subList(0, 1);
            path += ("." + chosen.get(0));
        }else{
            Bukkit.getLogger().warning("Something went wrong trying to get the list of spawns in world " + world);
            return;
        }

        if(type.equalsIgnoreCase("survivor")){
            this.surSpawn = new Location(
                    Bukkit.getWorld(world),
                    surConfig.getConfig().getDouble(path + ".x"),
                    surConfig.getConfig().getDouble(path + ".y"),
                    surConfig.getConfig().getDouble(path + ".z"),
                    (float) surConfig.getConfig().getDouble(path + ".yaw"),
                    (float) surConfig.getConfig().getDouble(path + ".pitch")
            );
        }else{
            this.infSpawn = new Location(
                    Bukkit.getWorld(world),
                    infConfig.getConfig().getDouble(path + ".x"),
                    infConfig.getConfig().getDouble(path + ".y"),
                    infConfig.getConfig().getDouble(path + ".z"),
                    (float) infConfig.getConfig().getDouble(path + ".yaw"),
                    (float) infConfig.getConfig().getDouble(path + ".pitch")
            );
        }
    }

    public Location getInfSpawn(){
        this.setGameSpawn("infected");
        return this.infSpawn;
    }

    public Location getSurSpawn(){
        this.setGameSpawn("survivor");
        return this.surSpawn;
    }

    // TODO
    //  get previous lobby or send to random lobby if none exists
    //  to get previous lobby see if theres an onteleport then see if you can get last location
    //  then check if the world is a lobby and if not dont set anything
    //  create a new variable for lastlobby initialized to null
    private void setDefaultSpawn(String x){
        String path = ("default-spawns." + x);

        this.defaultSpawn = new Location(
            Bukkit.getWorld(x),
                plugin.getConfig().getDouble(path + ".x"),
                plugin.getConfig().getDouble(path + ".y"),
                plugin.getConfig().getDouble(path + ".z"),
            (float) plugin.getConfig().getDouble(path + ".pitch"),
            (float) plugin.getConfig().getDouble(path + ".yaw")
        );
    }

    public Location getDefaultSpawn(String x){
        setDefaultSpawn(x);
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
        this.allowedWorlds = plugin.getConfig().getStringList("allowed-worlds");
    }
    private List<String> getAllowedWorlds(){
        setAllowedWorlds();
        return this.allowedWorlds;
    }

    private void setLobbies(){
        this.lobbies = plugin.getConfig().getStringList("lobby-worlds");
    }
    public List<String> getLobbies(){
        setLobbies();
        return this.lobbies;
    }

    private void setScalingInf(){
        this.scalingInf = plugin.getConfig().getBoolean("use-scaling-infected");
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
        String str = plugin.getConfig().getString("infected-ratio");
        String[] ratio = Objects.requireNonNull(str).split("-");

        if(plugin.getConfig().getBoolean("ratio-rounds-up")){
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

    public void setCurrentWorld(String world){
        this.currentWorld = world;
    }

    public String getCurrentWorld(){
        return this.currentWorld;
    }

    /**
     * Scoreboards
     * setBoard is the in-game scoreboard.
     * waitBoard is the scoreboard for while in game lobby.
     * removeBoard removes the current scoreboard.
     */
//    ScoreboardManager manager = Bukkit.getScoreboardManager();
//    Scoreboard scoreboard = Objects.requireNonNull(manager).getNewScoreboard();

    private void setBoard(Player player) {
        if (!statusMap.containsKey(player.getUniqueId())) {
            return;
        }
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        Scoreboard scoreboard = Objects.requireNonNull(manager).getNewScoreboard();

        Objective objective;
        if(plugin.getIs18()){
            objective = scoreboard.registerNewObjective("Game Status", "dummy");
            objective.setDisplayName(ChatColor.GOLD + "Survival Status");
        }else{
            objective = scoreboard.registerNewObjective("Game Status", Criteria.DUMMY, ChatColor.GOLD + "Survival Status");
        }

        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        Score newLine1 = objective.getScore("");
        newLine1.setScore(6);

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
        Scoreboard scoreboard = Objects.requireNonNull(manager).getNewScoreboard();

        Objective objective;
        if(plugin.getIs18()){
            objective = scoreboard.registerNewObjective("Game Status", "dummy");
            objective.setDisplayName(ChatColor.GOLD + "Survival Status");
        }else{
            objective = scoreboard.registerNewObjective("Game Status", Criteria.DUMMY, ChatColor.GOLD + "Survival Status");
        }

//        Bukkit.getLogger().severe("2 INFECTED: " + infected.getPlayers());
//        Bukkit.getLogger().severe("2 SURVIVORS: " + survivors.getPlayers());

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
        Scoreboard scoreboard = Objects.requireNonNull(manager1).getNewScoreboard();

        Objective objective;
        if(plugin.getIs18()){
            objective = scoreboard.registerNewObjective("Game Status", "dummy");
            objective.setDisplayName(ChatColor.GOLD + "Waiting on players");
        }else{
            objective = scoreboard.registerNewObjective("Game Status", Criteria.DUMMY, ChatColor.GOLD + "Waiting on players");
        }
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
        Scoreboard scoreboard = Objects.requireNonNull(manager).getNewScoreboard();
//        infected.unregister();
//        survivors.unregister();
        player.setScoreboard(scoreboard);

    }

    private void setEndTime(){
        this.endTime = plugin.getConfig().getInt("end-time");
    }


    public int getEndTime(){
        setEndTime();
        return this.endTime;
    }

    /**
     * Handling Scores
     */

    private void setScores(UUID player){
        ConfigUtil con = new ConfigUtil(plugin, "/Scores/" + player + ".yml");

        if(plugin.doSurvivorKills() && survivorKills.containsKey(player)){
            int kills = con.getConfig().getInt("survivor-kills");
            kills += survivorKills.get(player);
            con.getConfig().set("survivor-kills", kills);
        }

        if(plugin.doInfectedKills() && infectedKills.containsKey(player)){
            int kills = con.getConfig().getInt("infected-kills");
            kills += infectedKills.get(player);
            con.getConfig().set("infected-kills", kills);
        }

        if(plugin.doGamesPlayed()){
            int games = con.getConfig().getInt("games-played");
            games++;
            con.getConfig().set("games-played", games);
        }

        con.save();
    }

    private void setGamesWon(UUID player, String team){
        ConfigUtil con = new ConfigUtil(plugin, "/Scores/" + player + ".yml");

        if(plugin.doInfectedGamesWon() && team.equalsIgnoreCase("infected")){
            int wins = con.getConfig().getInt("infected-wins");
            wins++;
            con.getConfig().set("infected-wins", wins);
        }

        if(plugin.doSurvivorGamesWon()  && team.equalsIgnoreCase("survivor")){
            int wins = con.getConfig().getInt("survivor-wins");
            wins++;
            con.getConfig().set("survivor-wins", wins);
        }

        con.save();
    }

    /**
     * Hiding usernames
     */

//    public void hideNames(Player player){
//        ArmorStand as = (ArmorStand) player.getWorld().spawnEntity(player.getLocation(), EntityType.ARMOR_STAND);
//        as.setVisible(false);
//        as.setMetadata("nametag", new FixedMetadataValue(plugin, true));
////        as.setBasePlate(false);
//        as.setMarker(true);
//        if(plugin.getIs18()){
//            player.setPassenger(as);
//        }else{
//            player.addPassenger(as);
//        }
//    }
//
//    public void showNames(Player player){
//        if(plugin.getIs18()){
//            Entity entity = player.getPassenger();
//            if(Objects.requireNonNull(entity).hasMetadata("nametag")){
//                entity.remove();
//            }
//        }else{
//            List<Entity> entities = player.getPassengers();
//            for(Entity x : entities){
//                if(Objects.requireNonNull(x).hasMetadata("nametag")){
//                    x.remove();
//                }
//            }
//        }
//    }
}