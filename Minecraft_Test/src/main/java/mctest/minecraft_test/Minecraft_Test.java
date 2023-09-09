package mctest.minecraft_test;

import mctest.minecraft_test.commands.*;
import mctest.minecraft_test.handlers.PlayerHandler;
import mctest.minecraft_test.roles.SurvivalPlayer;
import mctest.minecraft_test.util.ConfigUtil;
import mctest.minecraft_test.util.DelayedTask;
import mctest.minecraft_test.util.SpawnUtil;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public final class Minecraft_Test extends JavaPlugin {
    public FileConfiguration getDefaultConfig(){
        return this.getConfig();
    }
    @Override
    public void onEnable() {
        // Plugin startup logic
        Bukkit.getLogger().info("Server Started");
        this.saveDefaultConfig();

        // Create these yml files and don't replace if present already
        saveResource("Loadouts.yml", false);
        ConfigUtil c1 = new ConfigUtil("Loadouts.yml");
        c1.save();

        saveResource("Survivor.yml", false);
        ConfigUtil c2 = new ConfigUtil("Loadouts.yml");
        c2.save();

        saveResource("Infected.yml", false);
        ConfigUtil c3 = new ConfigUtil("Loadouts.yml");
        c3.save();

        SpawnUtil spawnUtil = new SpawnUtil(this);

        getCommand("menu").setExecutor(new Menu(this, new SurvivalPlayer(this)));
        getCommand("spawn").setExecutor(new Spawn(spawnUtil));
        getCommand("setSpawn").setExecutor(new SetSpawn(spawnUtil));
        getCommand("loadout").setExecutor(new Loadout());
        getCommand("reload").setExecutor(new Reload());

        new PlayerHandler(this);
        new DelayedTask(this);
//        new PlagueHandler(this);
        //new SurvivalPlayer(this);

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        Bukkit.getLogger().info("Shutting Down");
    }
}
