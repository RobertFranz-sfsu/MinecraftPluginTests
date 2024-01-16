package com.valiantrealms.zombiesmc;

import com.valiantrealms.zombiesmc.commands.ZMC;
import com.valiantrealms.zombiesmc.commands.ZmcSub.Reload;
import com.valiantrealms.zombiesmc.util.BlockListener;
import com.valiantrealms.zombiesmc.util.ConfigUtil;
import com.valiantrealms.zombiesmc.util.PlayerHandler;
import com.valiantrealms.zombiesmc.util.Vault;
import com.valiantrealms.zombiesmc.util.skills.Unarmed;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
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

    // Skills
    private Unarmed unarmed;

    // Configs
    ConfigUtil skillSettings;
    ConfigUtil playerSettings;
    ConfigUtil blockValuesConfig;

    // Other plugins
    Vault econ;

    @Override
    public void onEnable() {
        Bukkit.getLogger().info("Enabling ZombiesMC");

        //Configs
        this.saveDefaultConfig();

        saveResource("PlayerInfo" + System.getProperty("file.separator") + "PlayerSettings.yml", false);
        saveResource("BlockValues.yml", false);
        saveResource("SkillSettings.yml", false);

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
        new PlayerHandler(this);
        new BlockListener(this);
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
    public void setPlayers(UUID id, PlayerProfile pm){

    }

    public void savePlayerData(UUID id){
        if(!this.players.isEmpty()){
            this.players.get(id).save(id);
        }else{
            Bukkit.getLogger().info("Nothing to save!");
        }
    }
    public void savePlayerData(){
        Bukkit.getLogger().info("Attempting to save all player data...");

        if(!this.players.isEmpty()){
            this.players.forEach((key, value) -> value.save(key));
            Bukkit.getLogger().info("Done saving all player data!");
        }else{
            Bukkit.getLogger().info("Nothing to save!");
        }
    }

    public void updatePlayerFromConfig(UUID id){
        this.getPlayers().get(id).setSkillsFromConfig();
    }

    /**
     * Config Files
     */
    public void saveConfigs() {
        this.skillSettings.save();
        this.playerSettings.save();
        this.blockValuesConfig.save();

        this.setConfigs();
    }

    public void setConfigs(){
        this.skillSettings = new ConfigUtil(this, "SkillSettings.yml");
        this.playerSettings = new ConfigUtil(this, System.getProperty("file.separator") + "PlayerInfo" + System.getProperty("file.separator") + "PlayerSettings.yml");
        this.blockValuesConfig = new ConfigUtil(this, "BlockValues.yml");
    }

    public ConfigUtil getSkillSettings() { return this.skillSettings; }
    public ConfigUtil getPlayerSettings() { return this.playerSettings; }
    public ConfigUtil getBlockValuesConfig() { return this.blockValuesConfig; }
    /**
     * Sub-Commands
     */

    private void setSubCommands(){
        this.reload = new Reload(this);
        this.loader = new PlayerLoader(this);
    }

    private void setSkills(){
        this.unarmed = new Unarmed(this);
    }

    public Reload getReload(){ return this.reload; }
    public Unarmed getUnarmed(){ return this.unarmed; }
    public PlayerLoader getLoader() { return this.loader; }

    /**
     * ECONOMY STUFF
     */
    public void setEcon() { this.econ = new Vault(this); }
}
