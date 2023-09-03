package mctest.minecraft_test;

import mctest.minecraft_test.commands.*;
import mctest.minecraft_test.handlers.PlayerHandler;
import mctest.minecraft_test.handlers.TorchHandler;
import mctest.minecraft_test.roles.SurvivalPlayer;
import mctest.minecraft_test.util.ConfigUtil;
import mctest.minecraft_test.util.DelayedTask;
import mctest.minecraft_test.util.SpawnUtil;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.Map;

public final class Minecraft_Test extends JavaPlugin {
    public FileConfiguration getDefaultConfig(){
        return this.getConfig();
    }
    @Override
    public void onEnable() {
        // Plugin startup logic
        Bukkit.getLogger().info("Server Started");
        this.saveDefaultConfig();

        // THIS IS JUST FOR TESTING
//        Map<String, Object> test2 = this.getConfig().getConfigurationSection("loadouts").getValues(true);

//        Bukkit.getLogger().info("!!!!!!!!!!!!!: " + test2.get("survivor.items").toString());

//        for (Map.Entry<String, Object> t :
//                test2.entrySet()) {
//
//            Bukkit.getLogger().info("!!!!! TEST 2 !!!!!");
//            Bukkit.getLogger().info(t.getKey() + ":");
//            Bukkit.getLogger().info((String) t.getValue().toString());
//        }

        ConfigUtil config = new ConfigUtil(this, "config.yml");
        config.save();

        SpawnUtil spawnUtil = new SpawnUtil(this);

        getCommand("fly").setExecutor(new Fly());
        getCommand("menu").setExecutor(new Menu(this, new SurvivalPlayer(this)));
        getCommand("spawn").setExecutor(new Spawn(spawnUtil));
        getCommand("setSpawn").setExecutor(new SetSpawn(spawnUtil));

        new TorchHandler(this);
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
