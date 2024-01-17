package mctest.minecraft_test.commands.InfectedSubCommands;

import mctest.minecraft_test.Minecraft_Test;
import mctest.minecraft_test.roles.GamesList;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;

public class ListWorlds {
    Minecraft_Test plugin;

    public ListWorlds(Minecraft_Test plugin){ this.plugin = plugin; }

    public void List(Player player){
        if(player.hasPermission("infected.infected.listworlds") || player.hasPermission("infected.*") || player.hasPermission("infected.infected.*")){
            try{
                List<String> worlds = plugin.getConfig().getStringList("allowed-worlds");

                if(!worlds.isEmpty()){
                    int count = 1;
                    for(String x : worlds){
                        String world = count + ") " + x;

                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                                "tellraw " + player.getName() + " {" +
                                        "\"text\": \"" + world +"\"," +
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
