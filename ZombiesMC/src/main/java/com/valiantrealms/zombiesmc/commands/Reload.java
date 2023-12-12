package com.valiantrealms.zombiesmc.commands;

import com.valiantrealms.zombiesmc.ZombiesMC;
import com.valiantrealms.zombiesmc.util.ConfigUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class Reload implements CommandExecutor {
    private ZombiesMC plugin;
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        try{
            Bukkit.getLogger().info("Reloading config.yml...");
            plugin.reloadConfig();
            Bukkit.getLogger().info("Reloaded config.yml.");

            Bukkit.getLogger().info("Reloading BlockListener.yml...");
            ConfigUtil c1 = new ConfigUtil(plugin, "BlockListener.yml");
            c1.save();
            Bukkit.getLogger().info("Saved BlockListener.yml...");

        }catch(Exception e){
            sender.sendMessage("Something went wrong, please check the console.");
            e.printStackTrace();
        }

        return true;
    }
}
