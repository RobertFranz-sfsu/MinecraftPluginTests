package com.valiantrealms.zombiesmc;

import com.valiantrealms.zombiesmc.commands.ZMC;
import com.valiantrealms.zombiesmc.commands.ZmcSub.Reload;
import com.valiantrealms.zombiesmc.util.BlockListener;
import com.valiantrealms.zombiesmc.util.ConfigUtil;
import com.valiantrealms.zombiesmc.util.PlayerHandler;
import com.valiantrealms.zombiesmc.util.Vault;
import com.valiantrealms.zombiesmc.util.skills.Husbandry;
import com.valiantrealms.zombiesmc.util.skills.Ranged;
import com.valiantrealms.zombiesmc.util.skills.Stealth;
import com.valiantrealms.zombiesmc.util.skills.Strength;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class ZombiesMC extends JavaPlugin {

    // Info
    private static HashMap<String, Integer> breakableBlocks = new HashMap<>();
    private ConcurrentHashMap<UUID, PlayerProfile> players = new ConcurrentHashMap<>();

    // Sub Commands
    private Reload reload;
    private PlayerLoader loader;
    private PlayerHandler playerHandler;
    private BlockListener blockListener;

    // Skills
    private Strength strength;
    private Ranged ranged;
    private Stealth stealth;
    private Husbandry husbandry;

    // Configs
    ConfigUtil skillSettings;
    ConfigUtil playerSettings;
    ConfigUtil blockValuesConfig;
    ConfigUtil husbandryValues;

    // Other plugins
    Vault econ;
    boolean hasProtocolLib;

    @Override
    public void onEnable() {
        Bukkit.getLogger().info("Enabling ZombiesMC");

        if(this.getServer().getPluginManager().getPlugin("ProtocolLib") == null){
            Bukkit.getLogger().info("NO VNP");
            this.hasProtocolLib = false;
        }else{
            this.hasProtocolLib = true;
        }

        //Configs
        this.saveDefaultConfig();

        saveResource("PlayerInfo" + System.getProperty("file.separator") + "DefaultPlayerSettings.yml", false);
        saveResource("BlockValues.yml", false);
        saveResource("SkillSettings.yml", false);
        saveResource("HusbandryValues.yml", false);

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
        this.setSubCommands();

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
    public boolean isHasProtocolLib() { return this.hasProtocolLib; }

    /**
     * Config Files
     */
    public void saveConfigs() {
        this.skillSettings.save();
        this.playerSettings.save();
        this.blockValuesConfig.save();
        this.husbandryValues.save();

        this.setConfigs();
    }

    public void setConfigs(){
        this.skillSettings = new ConfigUtil(this, "SkillSettings.yml");
        this.playerSettings = new ConfigUtil(this, System.getProperty("file.separator") + "PlayerInfo" + System.getProperty("file.separator") + "DefaultPlayerSettings.yml");
        this.blockValuesConfig = new ConfigUtil(this, "BlockValues.yml");
        this.husbandryValues = new ConfigUtil(this, "HusbandryValues.yml");
    }

    public ConfigUtil getSkillSettings() { return this.skillSettings; }
    public ConfigUtil getPlayerSettings() { return this.playerSettings; }
    public ConfigUtil getBlockValuesConfig() { return this.blockValuesConfig; }
    public ConfigUtil getHusbandryValues() { return this.husbandryValues; }

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
        this.strength = new Strength(this);
        this.ranged = new Ranged(this);
        this.stealth = new Stealth(this);
        this.husbandry = new Husbandry(this);
    }

    public Reload getReload(){ return this.reload; }
    public Strength getStrength(){ return this.strength; }
    public PlayerLoader getLoader() { return this.loader; }
    public PlayerHandler getPlayerHandler() { return this.playerHandler; }
    public BlockListener getBlockListener() { return this.blockListener; }
    public Ranged getRanged() { return this.ranged; }
    public Stealth getStealth() { return this.stealth; }
    public Husbandry getHusbandry() { return husbandry; }

    /**
     * ECONOMY STUFF
     */
    public void setEcon() { this.econ = new Vault(this); }
}
