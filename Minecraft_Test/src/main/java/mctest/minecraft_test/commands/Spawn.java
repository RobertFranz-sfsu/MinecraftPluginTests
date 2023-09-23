package mctest.minecraft_test.commands;

import mctest.minecraft_test.Minecraft_Test;
import mctest.minecraft_test.roles.GamesList;
import mctest.minecraft_test.roles.SurvivalPlayer;
import mctest.minecraft_test.util.ConfigUtil;
import mctest.minecraft_test.util.SpawnUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Objects;

public class Spawn implements CommandExecutor {
    private SurvivalPlayer pl;
    private GamesList g;

    public Spawn(SurvivalPlayer pl, GamesList g) {
        this.pl = pl;
        this.g = g;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String [] args) {
        try{
            Player player = null;
            String world = null;

//            if(args.length > 0){
            if(Bukkit.getPlayer(args[0]) != null){
                player = Bukkit.getPlayer(args[0]);
                if(!Objects.equals(args[1], null)){
                    world = (pl.getLobbies().contains(args[1]) ? args[1]: pl.getLobbies().get(0));
                }else{
                    world = pl.getLobbies().get(0);
                }
            }else if(sender instanceof Player){
                player = (Player) sender;

                Bukkit.getLogger().severe("lobbies: " + pl.getLobbies());

                if(!Objects.equals(args[0], null)){
                    world = (pl.getLobbies().contains(args[0]) ? args[0]: pl.getLobbies().get(0));
                }else{
                    world = pl.getLobbies().get(0);
                }
            }
//            }

            player.teleport(pl.getDefaultSpawn(world));
        }catch(Exception e){
            sender.sendMessage("Something went wrong, please check the console.");
            Bukkit.getLogger().info("Something went wrong trying to teleport.");
            e.printStackTrace();
        }

        return true;
    }
}
