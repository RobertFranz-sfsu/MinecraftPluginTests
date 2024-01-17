package mctest.minecraft_test.commands.InfectedSubCommands;

import mctest.minecraft_test.Minecraft_Test;
import mctest.minecraft_test.roles.GamesList;
import org.bukkit.entity.Player;

public class End {
    Minecraft_Test plugin;
    private GamesList g;

    public End(Minecraft_Test plugin, GamesList g){
        this.plugin = plugin;
        this.g = g;
    }

    public void endGame(Player player){
        if(player.hasPermission("infected.infected.end") || player.hasPermission("infected.*") || player.hasPermission("infected.infected.*")){
            try{
                player.sendMessage("Ending match.");
                g.getGameMap().get(player.getWorld().getName()).setTimer(-42);
            }catch(Exception e){
                player.sendMessage("Something went wrong, please check the console");
                e.printStackTrace();
            }
        }else{
            player.sendMessage("You do not have permission to do this!");
        }
    }
}
