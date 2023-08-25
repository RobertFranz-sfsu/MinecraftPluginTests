package mctest.minecraft_test;

import mctest.minecraft_test.commands.*;
import mctest.minecraft_test.handlers.ItemHandler;
import mctest.minecraft_test.handlers.PlayerHandler;
import mctest.minecraft_test.handlers.TorchHandler;
import mctest.minecraft_test.roles.Infected;
import mctest.minecraft_test.roles.SurvivalPlayer;
import mctest.minecraft_test.roles.Survivor;
import mctest.minecraft_test.util.ConfigUtil;
import mctest.minecraft_test.util.DelayedTask;
import mctest.minecraft_test.util.SpawnUtil;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public final class Minecraft_Test extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        Bukkit.getLogger().info("Hello World");
        //saveDefaultConfig();

        //ConfigUtil config = new ConfigUtil(this, "test.yml");

        SpawnUtil spawnUtil = new SpawnUtil(this);

        getCommand("fly").setExecutor(new Fly());
        getCommand("menu").setExecutor(new Menu(this, new SurvivalPlayer(this)));
        //getCommand("infect").setExecutor(new SetRole(new Infected(this), new Survivor(this)));
        getCommand("spawn").setExecutor(new Spawn(spawnUtil));
        getCommand("setSpawn").setExecutor(new SetSpawn(spawnUtil));

        new TorchHandler(this);
        new PlayerHandler(this);
        new DelayedTask(this);
        new ItemHandler(this);
//        new Infected(this);
//        new Survivor(this);
        new SurvivalPlayer(this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        Bukkit.getLogger().info("Shutting Down");
    }
}
