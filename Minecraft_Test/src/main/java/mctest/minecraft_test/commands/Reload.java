package mctest.minecraft_test.commands;

import mctest.minecraft_test.Minecraft_Test;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class Reload implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        try{
            Minecraft_Test.getPlugin(Minecraft_Test.class).reloadConfig();
            sender.sendMessage("Config.yml has been reloaded.");
        }catch(Exception e){
            sender.sendMessage("Something went wrong, is the config formatted correctly?");
        }
        return true;
    }
}
