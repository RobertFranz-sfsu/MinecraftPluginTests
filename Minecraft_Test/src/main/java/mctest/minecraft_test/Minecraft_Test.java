package mctest.minecraft_test;

import mctest.minecraft_test.commands.*;
import mctest.minecraft_test.handlers.PlayerHandler;
import mctest.minecraft_test.roles.SurvivalPlayer;
import mctest.minecraft_test.util.ConfigUtil;
import mctest.minecraft_test.util.CountdownTimer;
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
        saveResource("Infected.yml", false);
        saveResource("Survivor.yml", false);
        saveResource("Loadouts.yml", false);

        SpawnUtil spawnUtil = new SpawnUtil(this);
        SurvivalPlayer sp = new SurvivalPlayer(this);

        getCommand("menu").setExecutor(new Menu(this, sp));
        getCommand("spawn").setExecutor(new Spawn(spawnUtil, sp));
        getCommand("setSpawn").setExecutor(new SetSpawn(spawnUtil)); //remove later
        getCommand("loadout").setExecutor(new Loadout());
        getCommand("reload").setExecutor(new Reload(sp));
        getCommand("infected").setExecutor(new Infected(sp));

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
