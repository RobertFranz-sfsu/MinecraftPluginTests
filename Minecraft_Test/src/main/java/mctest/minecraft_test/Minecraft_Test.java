package mctest.minecraft_test;

import mctest.minecraft_test.commands.*;
import mctest.minecraft_test.handlers.PlayerHandler;
import mctest.minecraft_test.roles.GamesList;
import mctest.minecraft_test.roles.SurvivalPlayer;
import mctest.minecraft_test.util.DelayedTask;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.scoreboard.NameTagVisibility;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import java.util.HashMap;
import java.util.UUID;

public final class Minecraft_Test extends JavaPlugin {
    private static Economy econ = null;
    private static Permission perms = null;
    private static Chat chat = null;
    public FileConfiguration getDefaultConfig(){
        return this.getConfig();
    }
    private boolean hasProtocolLib = true;
    private HashMap<UUID, Integer> gameIDMap = new HashMap<>();
    public HashMap<UUID, Integer> getGameIDMap() {
        return this.gameIDMap;
    }
    public void setGameIDMap(HashMap<UUID, Integer> map) {
        this.gameIDMap = map;
    }
    @Override
    public void onEnable() {
        // Plugin startup logic
        Bukkit.getLogger().info("Server Started");
        this.saveDefaultConfig();

        // Economy
        if (!setupEconomy() ) {
            getLogger().warning("Vault not found! Economy features will not work");
        }else{
            getLogger().info("Setting up the economy!");
            setupPermissions();
            setupChat();
        }

        if (getServer().getPluginManager().getPlugin("ProtocolLib") == null) {
            getLogger().warning("ProtocolLib not found! Certain features will not work.");
            hasProtocolLib = false;
        }

        // Create these yml files and don't replace if present already
        saveResource("Infected.yml", false);
        saveResource("Survivor.yml", false);
        saveResource("Loadouts.yml", false);

        SurvivalPlayer sp = new SurvivalPlayer(this);
        GamesList g = new GamesList(this);

        getCommand("menu").setExecutor(new Menu(this, g,
                sp
                ));
        getCommand("spawn").setExecutor(new Spawn(sp,
                g
        ));
//        getCommand("setSpawn").setExecutor(new SetSpawn(spawnUtil)); //remove later
        getCommand("loadout").setExecutor(new Loadout());
        getCommand("reload").setExecutor(new Reload(sp));
        getCommand("infected").setExecutor(new Infected(this, sp, g));

        new PlayerHandler(this);
        new DelayedTask(this);
        //new SurvivalPlayer(this);

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        Bukkit.getLogger().info("Shutting Down");
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

    public boolean hasProtocolLib(){ return this.hasProtocolLib; }
}
