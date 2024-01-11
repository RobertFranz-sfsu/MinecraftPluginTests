package mctest.minecraft_test.commands.InfectedSubCommands;

import mctest.minecraft_test.Minecraft_Test;
import mctest.minecraft_test.roles.GamesList;
import org.bukkit.entity.Player;

public class Start {
    Minecraft_Test plugin;
    private GamesList g;

    public Start(Minecraft_Test plugin, GamesList g){
        this.plugin = plugin;
        this.g = g;
    }

    public void startGame(Player player){
        if(player.hasPermission("infected.infected.start") || player.hasPermission("infected.*") || player.hasPermission("infected.infected.*")){
            try{
                player.sendMessage("Starting match.");
                g.getGameMap().get(player.getWorld().getName()).setTimer(0);
            }catch(Exception e){
                player.sendMessage("Something went wrong, please check the console");
                e.printStackTrace();
            }
        }else{
            player.sendMessage("You do not have permission to do this!");
        }
    }
}
