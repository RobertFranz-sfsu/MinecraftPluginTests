package com.valiantrealms.zombiesmc;

import com.valiantrealms.zombiesmc.commands.ZMC;
import com.valiantrealms.zombiesmc.commands.ZmcSub.Reload;
import com.valiantrealms.zombiesmc.util.BlockListener;
import com.valiantrealms.zombiesmc.util.ConfigUtil;
import com.valiantrealms.zombiesmc.util.PlayerHandler;
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
    private static Economy econ = null;
    private static Permission perms = null;
    private static Chat chat = null;

    // Info
    private static HashMap<String, Integer> breakableBlocks = new HashMap<>();
    private ConcurrentHashMap<UUID, PlayerProfile> players = new ConcurrentHashMap<>();

    // Sub Commands
    private Reload reload;
    private PlayerProfile profile;
    private PlayerLoader loader;

    ConfigUtil skillSettings;
    @Override
    public void onEnable() {
        Bukkit.getLogger().info("Enabling ZombiesMC");

        //Configs
        this.saveDefaultConfig();

        saveResource("PlayerInfo" + System.getProperty("file.separator") + "PlayerSettings.yml", false);

        saveResource("BlockValues.yml", false);

        saveResource("SkillSettings.yml", false);
        this.setSkillSettings();

        // Economy
        if (!setupEconomy() ) {
            getLogger().warning("Vault not found! Economy features will not work");
        }else{
            getLogger().info("Setting up the economy!");
            setupPermissions();
            setupChat();
        }

        // Enabling commands
        Objects.requireNonNull(getCommand("zmc")).setExecutor(new ZMC(this));
        this.setReload();
        this.setLoader();

        // Enabling utils
        new PlayerHandler(this);
        new BlockListener(this);

        // Saving breakable blocks
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        Bukkit.getServer().getScheduler().cancelTask(867530942); // Canceling save process
        Bukkit.getLogger().info("Disabling ZombiesMC");
    }

    /**
     * Main Stuff
     */
    public ConcurrentHashMap<UUID, PlayerProfile> getPlayers() { return this.players; }
    public void setPlayers(UUID id, PlayerProfile pm){

    }

    /**
     * Config Files
     */
    public void setSkillSettings() { this.skillSettings = new ConfigUtil(this, "SkillSettings.yml"); }
    public ConfigUtil getSkillSettings() { return this.skillSettings; }

    /**
     * Sub-Commands
     */
    private void setReload(){ this.reload = new Reload(this); }
    public Reload getReload(){ return this.reload; }

    public PlayerProfile getProfile(UUID id) { return this.profile; }

    private void setLoader(){ this.loader = new PlayerLoader(this); }
    public PlayerLoader getLoader() { return this.loader; }

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
}
