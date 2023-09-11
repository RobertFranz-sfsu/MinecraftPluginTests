package mctest.minecraft_test.commands;

import mctest.minecraft_test.Minecraft_Test;
import mctest.minecraft_test.roles.SurvivalPlayer;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.Objects;

public class Infected implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(args.length != 0){
            SurvivalPlayer s = new SurvivalPlayer(Minecraft_Test.getPlugin(Minecraft_Test.class));

            switch (args[0].toLowerCase()){
                case "start":
                    try{
                        sender.sendMessage("Starting match.");
                        s.getStatusMap().forEach((key, value) ->  Bukkit.getPlayer(key).sendMessage("Game starting"));
                        s.gameInit();
                    }catch(Exception e){
                        sender.sendMessage("Something went wrong, please check the console");
                        e.printStackTrace();
                    }
                    break;
                case "end":
                    try{
                        sender.sendMessage("Ending match.");
                        s.endGame();
                    }catch(Exception e){
                        sender.sendMessage("Something went wrong, please check the console");
                        e.printStackTrace();
                    }
                    break;
                default:
                    sender.sendMessage("Valid sub commands: start, end.");
            }
        }else{
            sender.sendMessage("Valid sub commands: start, end.");
        }

        return true;
    }
}
