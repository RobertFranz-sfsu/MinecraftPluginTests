/*
 * TODO
 *   S:
 *      Add version checker
 *   R:
 *      Fix /spawn to /ispawn
 *      Change /menu to /imenu
 *
 *  Fix end command
 *  Command to add specific games
 *  Implement switching loadout switch
 *
 *  Remove old tests/code
 *  Move code to fresh repo lol
 *
 *  MySQL
 *  PAPI
 *
 * */

package mctest.minecraft_test.roles;

import mctest.minecraft_test.Minecraft_Test;
import mctest.minecraft_test.listeners.GameListener;
import mctest.minecraft_test.util.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@SuppressWarnings({"rawtypes", "FieldMayBeFinal", "CallToPrintStackTrace", "deprecation"})
public class SurvivalPlayer implements Listener {
    ConfigUtil surConfig = new ConfigUtil(Minecraft_Test.getPlugin(Minecraft_Test.class), "Survivor.yml");
    ConfigUtil infConfig = new ConfigUtil(Minecraft_Test.getPlugin(Minecraft_Test.class), "Infected.yml");
    private final ConcurrentHashMap<UUID, String> statusMap = new ConcurrentHashMap<>();
    private final HashMap<UUID, String> healthMap = new HashMap<>();
    public HashMap<UUID, String> previousWorlds = new HashMap<>();

    public HashMap<UUID, Integer> survivorKills = new HashMap<>();

    public HashMap<UUID, Integer> getSurvivorKills() {
        return this.survivorKills;
    }

    public HashMap<UUID, Integer> infectedKills = new HashMap<>();

    public HashMap<UUID, Integer> getInfectedKills() {
        return this.infectedKills;
    }

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
    private Scoreboards scoreboard;
    private PlayerRoles role;
    private Scores scores;
    private InventoryUtil invUtil;

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

    public HashMap<UUID, String> gethealthMap() {
        return this.healthMap;
    }


    //    public HashMap<UUID, String> getStatusMap() {
//        return this.statusMap;
//    }

    public void addPreviousWorld(UUID player, String world) {
        previousWorlds.put(player, world);
    }

    public SurvivalPlayer(Minecraft_Test plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
        this.plugin = plugin;
        this.setValues();
        scoreboard = new Scoreboards(plugin, this);
        role = new PlayerRoles(plugin);
        scores = new Scores(plugin, this);
        invUtil = new InventoryUtil(plugin);

        new GameListener(plugin, this, scoreboard, role);

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
                                        "tellraw " + player.getName() + " {" +
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

                statusMap.forEach((key, value) -> scoreboard.waitBoard(Objects.requireNonNull(Bukkit.getPlayer(key))));
                // if max amount of players have joined or if the timer has hit 0, start the game
                if (statusMap.size() == this.getMaxPl() || this.getTimer() == 0) {
                    if (this.getInfSpawn() == null || this.getSurSpawn() == null || this.getDefaultSpawn(currentWorld) == null) {
                        Bukkit.getLogger().info("Spawns are not set up");
                        this.setTimer(Integer.MIN_VALUE);
                    } else {
                        Bukkit.getLogger().info("Game Starting");
                        statusMap.forEach((key, value) -> Objects.requireNonNull(Bukkit.getPlayer(key)).sendMessage("Game starting"));
                        this.gameInit();
                        this.setTimer(Integer.MIN_VALUE);
                    }
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
                        scoreboard.setBoard(Objects.requireNonNull(Bukkit.getPlayer(key)));
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

                if (!this.getPlaying() && !statusMap.isEmpty()) {

                }
            }
        }, 0L, 20L);
    }

    public void gameInit() {
        createNew = true;

        try {
            ArrayList<Integer> playerList = new ArrayList<>();
            for (int i = 0; i < statusMap.size(); i++) {
                playerList.add(i);
            }

            Bukkit.getLogger().severe("BEFORE");
            for (Map.Entry<UUID, String> entry : statusMap.entrySet()) {
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
                invUtil.saveInventory(Objects.requireNonNull(Bukkit.getPlayer(entry.getKey())));
                invUtil.clearInventory(Objects.requireNonNull(Bukkit.getPlayer(entry.getKey())));
                scoreboard.setBoard(Objects.requireNonNull(Bukkit.getPlayer(entry.getKey())));
                role.setRole(Objects.requireNonNull(Bukkit.getPlayer(entry.getKey())), this);

                if (entry.getValue().equalsIgnoreCase("infected")) {
                    infectedKills.put(entry.getKey(), 0);
                } else if (entry.getValue().equalsIgnoreCase("survivor")) {
                    survivorKills.put(entry.getKey(), 0);
                }
            }

            Bukkit.getLogger().info(statusMap.toString());

            this.setPlaying(true);

            Bukkit.getLogger().severe("AFTER");
            for (Map.Entry<UUID, String> entry : statusMap.entrySet()) {
                Bukkit.getLogger().severe("PLAYER: " + Bukkit.getPlayer(entry.getKey()) + ", STATUS: " + entry.getValue());
            }

            for (Map.Entry<UUID, String> entry : statusMap.entrySet()) {
                if (entry.getKey() != null) {
                    plugin.getIsPlayingSet().add(entry.getKey());
                    Bukkit.dispatchCommand(Objects.requireNonNull(Bukkit.getPlayer(entry.getKey())), "m");
                }
            }
        } catch (Exception e) {
            Bukkit.getLogger().warning("Something went wrong trying to initialize the game.");
            e.printStackTrace();
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
                    invUtil.clearInventory(Objects.requireNonNull(Bukkit.getPlayer(entry.getKey())));

                    role.removeEffects(Objects.requireNonNull(Bukkit.getPlayer(entry.getKey())));

                    if (plugin.doKeepScore()) {
                        scores.setScores(entry.getKey());
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

                    invUtil.giveInventory(Objects.requireNonNull(Bukkit.getPlayer(entry.getKey())));

                    role.setNotPlaying(Objects.requireNonNull(Bukkit.getPlayer(entry.getKey())), this, invUtil);

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
                    invUtil.clearInventory(Objects.requireNonNull(Bukkit.getPlayer(entry.getKey())));

                    role.removeEffects(Objects.requireNonNull(Bukkit.getPlayer(entry.getKey())));

                    if (plugin.doKeepScore()) {
                        scores.setScores(entry.getKey());
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

                    invUtil.giveInventory(Objects.requireNonNull(Bukkit.getPlayer(entry.getKey())));

                    if (entry.getValue().equals(winner)) {
                        scores.giveRewards(Bukkit.getPlayer(entry.getKey()), winner);
                        if (plugin.doKeepScore()) {
                            scores.setGamesWon(entry.getKey(), winner);
                        }
                    }

                    role.setNotPlaying(Objects.requireNonNull(Bukkit.getPlayer(entry.getKey())), this, invUtil);

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

    private void setWaitTime() {
        this.waitTime = plugin.getConfig().getInt("wait-timer");
    }

    private int getWaitTime() {
//        this.setWaitTime();
        return this.waitTime;
    }

    // Match length
    private void setGameTime() {
        this.gameTime = plugin.getConfig().getInt("match-length");
    }

    private int getGameTime() {
//        this.setGameTime();
        return this.gameTime;
    }

    private void setMaxPl() {
        this.maxPl = plugin.getConfig().getInt("max-players");
    }

    public int getMaxPl() {
//        this.setMaxPl();
        return this.maxPl;
    }

    private void setMinPl() {
        this.minPl = plugin.getConfig().getInt("min-players");
    }

    public int getMinPl() {
//        this.setMinPl();
        return this.minPl;
    }

    private void setNumStartInf() {
        this.numStartInf = plugin.getConfig().getInt("num-starting-infected");
    }

    private int getNumStartInf() {
//        this.setNumStartInf();
        return this.numStartInf;
    }

    private void setRespawnTime() {
        this.respawnTime = plugin.getConfig().getInt("respawn-timer");
    }

    public int getRespawnTime() {
        this.setRespawnTime();
        return this.respawnTime;
    }

    private void setValues() {
        this.setNumStartInf();
        this.setMinPl();
        this.setMaxPl();
        this.setGameTime();
        this.setWaitTime();
    }

    public void reloadConfigs() {
        surConfig = new ConfigUtil(plugin, "Survivor.yml");
        infConfig = new ConfigUtil(plugin, "Infected.yml");

        this.setValues();
    }

    private void setGameSpawn(String type) {
        String world = null;
        String path = "spawns.";
        List<String> labels = new ArrayList<>();
        List chosen;

        int check = 0;
        for (UUID x : statusMap.keySet()) {
            if (!Objects.equals(x, null)) {
                world = Objects.requireNonNull(Bukkit.getPlayer(x)).getWorld().getName();
            }

            if ((check != 0) && (Objects.equals(Objects.requireNonNull(Bukkit.getPlayer(x)).getWorld().getName(), world))) {
                break;
            }

            check++;
        }

        if (Objects.equals(world, null)) {
            Bukkit.getLogger().warning("Something went wrong trying to get the world.");
            return;
        }

        path += world;

        if (type.equals("survivor")) {
            for (String x : Objects.requireNonNull(surConfig.getConfig().getConfigurationSection(path)).getKeys(false)) {
                if (x != null) {
                    labels.add(x);
                }
            }
        } else if (type.equals("infected")) {
            for (String x : Objects.requireNonNull(infConfig.getConfig().getConfigurationSection(path)).getKeys(false)) {
                if (x != null) {
                    labels.add(x);
                }
            }
        }

        if (!labels.isEmpty()) {
            Collections.shuffle(labels);
            chosen = labels.subList(0, 1);
            path += ("." + chosen.get(0));
        } else {
            Bukkit.getLogger().warning("Something went wrong trying to get the list of spawns in world " + world);
            return;
        }

        if (type.equalsIgnoreCase("survivor")) {
            this.surSpawn = new Location(
                    Bukkit.getWorld(world),
                    surConfig.getConfig().getDouble(path + ".x"),
                    surConfig.getConfig().getDouble(path + ".y"),
                    surConfig.getConfig().getDouble(path + ".z"),
                    (float) surConfig.getConfig().getDouble(path + ".yaw"),
                    (float) surConfig.getConfig().getDouble(path + ".pitch")
            );
        } else {
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

    public Location getInfSpawn() {
        this.setGameSpawn("infected");
        return this.infSpawn;
    }

    public Location getSurSpawn() {
        this.setGameSpawn("survivor");
        return this.surSpawn;
    }

    // TODO
    //  get previous lobby or send to random lobby if none exists
    //  to get previous lobby see if theres an onteleport then see if you can get last location
    //  then check if the world is a lobby and if not dont set anything
    //  create a new variable for lastlobby initialized to null
    private void setDefaultSpawn(String x) {
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

    public Location getDefaultSpawn(String x) {
        setDefaultSpawn(x);
        return this.defaultSpawn;
    }

    private void setSurHealth() {
        this.surHealth = surConfig.getConfig().getInt("health");
    }

    public int getSurHealth() {
        setSurHealth();
        return this.surHealth;
    }

    private void setSurSpeed() {
        this.surSpeed = (float) surConfig.getConfig().getDouble("speed");
    }

    public float getSurSpeed() {
        setSurSpeed();
        return this.surSpeed;
    }

    private void setInfHealth() {
        this.infHealth = infConfig.getConfig().getInt("health");
    }

    public int getInfHealth() {
        setInfHealth();
        return this.infHealth;
    }

    private void setInfSpeed() {
        this.infSpeed = (float) infConfig.getConfig().getDouble("speed");
    }

    public float getInfSpeed() {
        setInfSpeed();
        return this.infSpeed;
    }

    private void setAllowedWorlds() {
        this.allowedWorlds = plugin.getConfig().getStringList("allowed-worlds");
    }

    public List<String> getAllowedWorlds() {
        setAllowedWorlds();
        return this.allowedWorlds;
    }

    private void setLobbies() {
        this.lobbies = plugin.getConfig().getStringList("lobby-worlds");
    }

    public List<String> getLobbies() {
        setLobbies();
        return this.lobbies;
    }

    private void setScalingInf() {
        this.scalingInf = plugin.getConfig().getBoolean("use-scaling-infected");
    }

    private boolean isScalingInf() {
        setScalingInf();
        return this.scalingInf;
    }

    private void setInfMoneyReward() {
        this.infMoneyReward = infConfig.getConfig().getDouble("inf-money-reward");
    }

    public double getInfMoneyReward() {
        setInfMoneyReward();
        return this.infMoneyReward;
    }

    private void setSurMoneyReward() {
        this.surMoneyReward = surConfig.getConfig().getDouble("sur-money-reward");
    }

    public double getSurMoneyReward() {
        setSurMoneyReward();
        return this.surMoneyReward;
    }

    private void setInfRatio() {
        String str = plugin.getConfig().getString("infected-ratio");
        String[] ratio = Objects.requireNonNull(str).split("-");

        if (plugin.getConfig().getBoolean("ratio-rounds-up")) {
            Bukkit.getLogger().info("ROUNDING UP");
            this.infRatio = (int) Math.ceil((Double.parseDouble(ratio[1]) / Double.parseDouble(ratio[0])));
        } else {
            Bukkit.getLogger().info("ROUNDING DOWN");
            this.infRatio = (int) Math.floor((Double.parseDouble(ratio[1]) / Double.parseDouble(ratio[0])));
        }
    }

    private int getInfRatio() {
        setInfRatio();
        return infRatio;
    }

    private void setInfCommandRewards() {
        infCommandRewards = infConfig.getConfig().getStringList("inf-win-commands");
    }

    public List<String> getInfCommandRewards() {
        setInfCommandRewards();
        return this.infCommandRewards;
    }

    private void setSurCommandRewards() {
        surCommandRewards = surConfig.getConfig().getStringList("sur-win-commands");
    }

    public List<String> getSurCommandRewards() {
        setSurCommandRewards();
        return this.surCommandRewards;
    }

    public void setCurrentWorld(String world) {
        this.currentWorld = world;
    }

    public String getCurrentWorld() {
        return this.currentWorld;
    }

    private void setEndTime() {
        this.endTime = plugin.getConfig().getInt("end-time");
    }

    public int getEndTime() {
        setEndTime();
        return this.endTime;
    }


    /**
     * Inventory util for getting previous inventories
     * */

    public InventoryUtil getInvUtil() {
        return this.invUtil;
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