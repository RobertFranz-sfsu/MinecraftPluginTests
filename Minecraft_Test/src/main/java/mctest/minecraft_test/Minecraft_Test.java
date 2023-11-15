package mctest.minecraft_test;

import mctest.minecraft_test.commands.*;
import mctest.minecraft_test.handlers.PlayerHandler;
import mctest.minecraft_test.roles.GamesList;
import mctest.minecraft_test.util.ConfigUtil;
import mctest.minecraft_test.util.DelayedTask;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class Minecraft_Test extends JavaPlugin {
    private static Economy econ = null;
    private static Permission perms = null;
    private static Chat chat = null;
    public FileConfiguration getDefaultConfig(){
        return this.getConfig();
    }

    private final ConcurrentHashMap<String, Integer[]> statsMap = new ConcurrentHashMap<>();
    private HashMap<UUID, Integer> gameIDMap = new HashMap<>();
    public HashMap<UUID, Integer> getGameIDMap() {
        return this.gameIDMap;
    }
    public void setGameIDMap(HashMap<UUID, Integer> map) {
        this.gameIDMap = map;
    }
    private ConfigUtil loadoutCon = new ConfigUtil(this, "Loadouts.yml");

    // Checks for things
    private boolean is18;
    private boolean doKeepScore;
    private boolean loadoutPrices;
    private boolean survivorGamesWon;
    private boolean infectedGamesWon;
    private boolean survivorKills;
    private boolean infectedKills;
    private boolean gamesPlayed;

    @Override
    public void onEnable() {
        // Plugin startup logic
        Bukkit.getLogger().info("Server Started");
        this.saveDefaultConfig();
        this.setIs18();
        this.setLoadoutCon();
        this.setDoKeepScore();

        saveResource("Scores" + System.getProperty("file.separator") + "ScoresConfig.yml", false);

        if(this.doKeepScore){
            this.setScoreOptions();
            this.initStatMap();
        }

        // Economy
        if (!setupEconomy() ) {
            getLogger().warning("Vault not found! Economy features will not work");
        }else{
            getLogger().info("Setting up the economy!");
            setupPermissions();
            setupChat();
        }

        // Create these yml files and don't replace if present already
        saveResource("Infected.yml", false);
        saveResource("Survivor.yml", false);
        saveResource("Loadouts.yml", false);

        this.setLoadoutPrices();

        GamesList g = new GamesList(this);

        Objects.requireNonNull(getCommand("menu")).setExecutor(new Menu(this, g));
        Objects.requireNonNull(getCommand("spawn")).setExecutor(new Spawn(g));
        Objects.requireNonNull(getCommand("loadout")).setExecutor(new Loadout(this));
        Objects.requireNonNull(getCommand("reload")).setExecutor(new Reload(g));
        Objects.requireNonNull(getCommand("infected")).setExecutor(new Infected(this, g));

        new PlayerHandler(this);
        new DelayedTask(this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        Bukkit.getLogger().info("Shutting Down");
    }

    public void setLoadoutCon(){
        this.loadoutCon = new ConfigUtil(this, "Loadouts.yml");
    }

    public ConfigUtil getLoadoutCon(){
        return this.loadoutCon;
    }

    /**
     * Version check
     */
    public boolean getIs18(){ return this.is18; }

    private void setIs18(){
        is18 = Bukkit.getVersion().contains("1.8");
    }

    /**
     * ECONOMY STUFF
     */
    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }

    private boolean setupChat() {
        RegisteredServiceProvider<Chat> rsp = getServer().getServicesManager().getRegistration(Chat.class);
        chat = rsp.getProvider();
        return chat != null;
    }

    private boolean setupPermissions() {
        RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
        perms = rsp.getProvider();
        return perms != null;
    }

    public Economy getEcon(){
        return econ;
    }


    /**
     * Config stuff
     */
    public void setLoadoutPrices() {
        this.loadoutPrices = getDefaultConfig().getBoolean("loadout-prices") && (!Objects.equals(this.getEcon(), null));
    }

    public boolean isLoadoutPrices(){ return this.loadoutPrices; }

    public void setDoKeepScore(){
        this.doKeepScore = getDefaultConfig().getBoolean("keep-score");
    }
    public void setScoreOptions(){
        ConfigUtil scoreCon = new ConfigUtil(this, "Scores/ScoresConfig.yml");
        this.survivorGamesWon = scoreCon.getConfig().getBoolean("survivor-games-won");
        this.infectedGamesWon = scoreCon.getConfig().getBoolean("infected-games-won");
        this.survivorKills = scoreCon.getConfig().getBoolean("survivor-kills");
        this.infectedKills = scoreCon.getConfig().getBoolean("infected-kills");
        this.gamesPlayed = scoreCon.getConfig().getBoolean("games-played");
    }

    public boolean doKeepScore(){ return this.doKeepScore; }
    public boolean doSurvivorGamesWon(){ return this.survivorGamesWon; }
    public boolean doInfectedGamesWon(){ return this.infectedGamesWon; }
    public boolean doSurvivorKills(){ return this.survivorKills; }
    public boolean doInfectedKills(){ return this.infectedKills; }
    public boolean doGamesPlayed(){ return this.gamesPlayed; }
    private void initStatMap() {
        File dir = new File(this.getDataFolder().getPath() + System.getProperty("file.separator") + "Scores" + System.getProperty("file.separator"));
        File[] dirList = dir.listFiles();

        if (dirList != null) {
            for (File child : dirList) {
                if (child.getName().equals("ScoresConfig.yml")) {
                    continue;
                }
                String newPath = System.getProperty("file.separator") + "Scores"  + System.getProperty("file.separator") + child.getName();
                ConfigUtil s = new ConfigUtil(this, newPath);
//                String name = s.getConfig().getString("username");
                String name = child.getName().substring(0, child.getName().length() - 4);
                int played = s.getConfig().getInt("games-played");
                int infKills = s.getConfig().getInt("infected-kills");
                int infWins = s.getConfig().getInt("infected-wins");
                int surKills = s.getConfig().getInt("survivor-kills");
                int surWins = s.getConfig().getInt("survivor-wins");

                this.statsMap.put(name, new Integer[] {played, infKills, infWins, surKills, surWins});
            }
            Bukkit.getLogger().info("Printing Values");
            this.statsMap.forEach((key, value) -> Bukkit.getLogger().info(key + "  " + Arrays.toString(value)));

        }
    }
    public ConcurrentHashMap<String, Integer[]> getStatsMap() {
        return this.statsMap;
    }
}
