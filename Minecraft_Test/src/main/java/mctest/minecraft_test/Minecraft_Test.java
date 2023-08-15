package mctest.minecraft_test;

import mctest.minecraft_test.commands.Fly;
import mctest.minecraft_test.commands.Menu;
import mctest.minecraft_test.handlers.PlayerHandler;
import mctest.minecraft_test.handlers.TorchHandler;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class Minecraft_Test extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        Bukkit.getLogger().info("Hello World");

        getCommand("fly").setExecutor(new Fly());
        getCommand("menu").setExecutor(new Menu(this));

        new TorchHandler(this);
        new PlayerHandler(this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        Bukkit.getLogger().info("Shutting Down");
    }
}
