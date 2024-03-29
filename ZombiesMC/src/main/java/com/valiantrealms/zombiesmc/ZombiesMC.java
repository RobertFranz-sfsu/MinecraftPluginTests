package com.valiantrealms.zombiesmc;

import com.valiantrealms.zombiesmc.commands.Unlock;
import com.valiantrealms.zombiesmc.commands.ZMC;
import com.valiantrealms.zombiesmc.commands.ZmcSub.Reload;
import com.valiantrealms.zombiesmc.util.*;
import com.valiantrealms.zombiesmc.util.skills.*;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class ZombiesMC extends JavaPlugin {

    // Info
    private HashMap<String, Integer> breakableBlocks = new HashMap<>();
    private ConcurrentHashMap<UUID, PlayerProfile> players = new ConcurrentHashMap<>();
    private ConcurrentHashMap<UUID, Double[]> xp = new ConcurrentHashMap<>();

    // Checks
    private boolean isCustomSkillSettings;

    // Sub Commands
    private Reload reload;
    private PlayerLoader loader;
    private PlayerHandler playerHandler;
    private BlockListener blockListener;

    // Skills
    private Experience experience;
    private Strength strength;
    private Ranged ranged;
    private Stealth stealth;
    private Husbandry husbandry;
    private Lockpicking lockpicking;
    private Cooking cooking;
    private Farming farming;
    private Salvage salvage;

    // Configs
    ConfigUtil skillSettings;
    ConfigUtil playerSettings;
    ConfigUtil blockValuesConfig;
    ConfigUtil husbandryValues;
    ConfigUtil farmingValues;
    ConfigUtil customSkillSettings;
    ConfigUtil playerExperience;
    ConfigUtil playerXPSettings;

    // Other plugins
    Vault econ;
    boolean hasProtocolLib;

    @Override
    public void onEnable() {
        Bukkit.getLogger().info("Enabling ZombiesMC");

        //Configs
        this.saveDefaultConfig();

        saveResource("PlayerInfo" + System.getProperty("file.separator") + "Experience.yml", false);
        saveResource("BlockValues.yml", false);
        saveResource("DefaultSkillSettings.yml", false);
        saveResource("HusbandryValues.yml", false);
        saveResource("FarmingValues.yml", false);
        saveResource("CustomSkillSettings.yml", false);
        saveResource("SkillExperienceSettings.yml", false);

        this.setConfigs();
        this.setEcon();

        // Economy
        if (!econ.setupEconomy() ) {
            getLogger().warning("Vault not found! Economy features will not work");
        }else{
            getLogger().info("Setting up the economy!");
            econ.setupPermissions();
            econ.setupChat();
        }
        // Enabling commands
        Objects.requireNonNull(getCommand("zmc")).setExecutor(new ZMC(this));
        Objects.requireNonNull(getCommand("unlock")).setExecutor(new Unlock(this));
        this.setSubCommands();

        new DelayedTask(this);

        // Enabling utils
        this.setSkills();

        // Saving breakable blocks
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        this.savePlayerData();
        Bukkit.getServer().getScheduler().cancelTask(867530942); // Canceling save process
        Bukkit.getLogger().info("Disabling ZombiesMC");
    }

    /**
     * Main Stuff
     */
    public ConcurrentHashMap<UUID, PlayerProfile> getPlayers() { return this.players; }

    public void savePlayerData(UUID id){
        if(!this.players.isEmpty()){
            this.players.get(id).save();
        }else{
            Bukkit.getLogger().info("Nothing to save!");
        }
    }

    public void savePlayerData(){
        Bukkit.getLogger().info("Attempting to save all player data...");

        if(!this.players.isEmpty()){
            this.players.forEach((key, value) -> value.save());
            Bukkit.getLogger().info("Done saving all player data!");
        }else{
            Bukkit.getLogger().info("Nothing to save!");
        }
    }

    public void register(UUID id){
        this.getPlayers().get(id).register(id);
    }

    public HashMap<String, Integer> getBreakableBlocks() { return this.breakableBlocks; }
    public ConcurrentHashMap<UUID, Double[]> getXp() { return this.xp; }

    /**
     * Config Files
     */
    public void saveConfigs() {
        this.skillSettings.save();
        this.playerSettings.save();
        this.blockValuesConfig.save();
        this.husbandryValues.save();
        this.farmingValues.save();
        this.customSkillSettings.save();
        this.playerExperience.save();
        this.playerXPSettings.save();

        this.setConfigs();
    }

    public void setConfigs(){
        this.skillSettings = new ConfigUtil(this, "DefaultSkillSettings.yml");
        this.playerSettings = new ConfigUtil(this, System.getProperty("file.separator") + "PlayerInfo" + System.getProperty("file.separator") + "DefaultPlayerSettings.yml");
        this.blockValuesConfig = new ConfigUtil(this, "BlockValues.yml");
        this.husbandryValues = new ConfigUtil(this, "HusbandryValues.yml");
        this.farmingValues = new ConfigUtil(this, "FarmingValues.yml");
        this.customSkillSettings = new ConfigUtil(this, "CustomSkillSettings.yml");
        this.playerExperience = new ConfigUtil(this, "PlayerInfo" + System.getProperty("file.separator") + "Experience.yml");
        this.playerXPSettings = new ConfigUtil(this, "SkillExperienceSettings.yml");

        this.isCustomSkillSettings = this.skillSettings.getConfig().getBoolean("custom-perms");
    }

    public ConfigUtil getSkillSettings() { return this.skillSettings; }
    public ConfigUtil getPlayerSettings() { return this.playerSettings; }
    public ConfigUtil getBlockValuesConfig() { return this.blockValuesConfig; }
    public ConfigUtil getHusbandryValues() { return this.husbandryValues; }
    public ConfigUtil getFarmingValues() { return this.farmingValues; }
    public ConfigUtil getCustomSkillSettings() { return this.customSkillSettings; }
    public ConfigUtil getPlayerExperience() { return this.playerExperience; }
    public ConfigUtil getPlayerXPSettings() { return this.playerXPSettings; }

    /**
     * Sub-Commands & Skills
     */
    private void setSubCommands(){
        this.reload = new Reload(this);
        this.loader = new PlayerLoader(this);
        this.playerHandler = new PlayerHandler(this);
        this.blockListener = new BlockListener(this);
    }

    private void setSkills(){
        this.experience = new Experience(this);
        this.strength = new Strength(this);
        this.ranged = new Ranged(this);
        this.stealth = new Stealth(this);
        this.husbandry = new Husbandry(this);
        this.farming = new Farming(this);
        this.cooking = new Cooking(this);
        this.lockpicking = new Lockpicking(this);
        this.salvage = new Salvage(this);
    }

    public Reload getReload(){ return this.reload; }
    public Strength getStrength(){ return this.strength; }
    public PlayerLoader getLoader() { return this.loader; }
    public PlayerHandler getPlayerHandler() { return this.playerHandler; }
    public BlockListener getBlockListener() { return this.blockListener; }
    public Experience getExperience() { return this.experience; }
    public Ranged getRanged() { return this.ranged; }
    public Stealth getStealth() { return this.stealth; }
    public Husbandry getHusbandry() { return this.husbandry; }
    public Farming getFarming() { return this.farming; }
    public Cooking getCooking() { return this.cooking; }
    public Lockpicking getLockpicking() { return this.lockpicking; }
    public Salvage getSalvage() { return this.salvage; }

    /**
     * Checks
     */
    public boolean isCustomSkillSettings() { return this.isCustomSkillSettings; }

    /**
     * ECONOMY STUFF
     */
    public void setEcon() { this.econ = new Vault(this); }

    public static ZombiesMC getInstance() {
        return getPlugin(ZombiesMC.class);
    }
}
