package mctest.minecraft_test.commands.InfectedSubCommands;

import mctest.minecraft_test.Minecraft_Test;
import mctest.minecraft_test.roles.GamesList;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;

public class ListLobbies {
    Minecraft_Test plugin;

    public ListLobbies(Minecraft_Test plugin){ this.plugin = plugin; }

    public void list(Player player){
        if(player.hasPermission("infected.infected.listlobbies") || player.hasPermission("infected.*") || player.hasPermission("infected.infected.*")){
            try{
                List<String> worldList = plugin.getConfig().getStringList("lobby-worlds");

                if(!worldList.isEmpty()){
                    int count = 1;
                    for(String x : worldList){
                        String world = count + ") " + x;
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                                "tellraw " + player.getName() + " {" +
                                        "\"text\": \"" + world + "\"," +
                                        "\"hoverEvent\": {" +
                                        "\"action\": \"show_text\"," +
                                        "\"value\": \"Shift click to copy to chat\"" +
                                        "}," +
                                        "\"insertion\": \"" + x + "\"" +
                                        "}");
                        count++;
                    }
                }
            }catch(Exception e){
                player.sendMessage("Something went wrong, please check the console");
                e.printStackTrace();
            }
        }else{
            player.sendMessage("You do not have permission to do this!");
        }
    }
}
