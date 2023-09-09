package mctest.minecraft_test.commands;

import mctest.minecraft_test.Minecraft_Test;
import mctest.minecraft_test.roles.SurvivalPlayer;
import mctest.minecraft_test.util.ConfigUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class Reload implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        try{
            Minecraft_Test.getPlugin(Minecraft_Test.class).reloadConfig();
            Bukkit.getLogger().info("Reloading config.yml.");

            ConfigUtil c1 = new ConfigUtil("Infected.yml");
            Bukkit.getLogger().info("Reloading Infected.yml.");
            c1.save();

            ConfigUtil c2 = new ConfigUtil("Survivor.yml");
            Bukkit.getLogger().info("Reloading Survivor.yml.");
            c2.save();

            ConfigUtil c3 = new ConfigUtil("Loadouts.yml");
            Bukkit.getLogger().info("Reloading Loadouts.yml.");
            c3.save();

            sender.sendMessage("Config files have been reloaded.");
        }catch(Exception e){
            sender.sendMessage("Something went wrong, please check the console.");
            e.printStackTrace();
        }
        return true;
    }
}
