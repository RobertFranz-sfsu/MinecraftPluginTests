package mctest.minecraft_test.commands;

import mctest.minecraft_test.roles.GamesList;
import mctest.minecraft_test.roles.SurvivalPlayer;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Optional;

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

            if((args.length > 0) && Optional.ofNullable((Bukkit.getPlayer(args[0]))).isPresent()){
                player = Bukkit.getPlayer(args[0]);

                List<String> l = g.getGameMap().get(player.getWorld().getName()).getLobbies();

                if(l.isEmpty()){
                    Bukkit.getLogger().severe("There are no lobbies!");
                    return true;
                }

                if((args.length > 1) && Optional.ofNullable((Bukkit.getPlayer(args[0]))).isPresent()){
                    world = (l.contains(args[1]) ? args[1]: l.get(0));
                }else{
                    world = l.get(0);
                }
            }else if(sender instanceof Player){
                player = (Player) sender;

                List<String> l = g.getGameMap().get(player.getWorld().getName()).getLobbies();

                if(l.isEmpty()){
                    Bukkit.getLogger().severe("There are no lobbies!");
                    return true;
                }

                if((args.length > 0) && Optional.ofNullable(args[0]).isPresent()){
                    world = (l.contains(args[0]) ? args[0]: l.get(0));
                }else{
                    world = l.get(0);
                }
            }

            Bukkit.getLogger().severe("world: " + world);
            player.teleport(g.getGameMap().get(world).getDefaultSpawn(world));
        }catch(Exception e){
            sender.sendMessage("Something went wrong, please check the console.");
            Bukkit.getLogger().info("Something went wrong trying to teleport.");
            e.printStackTrace();
        }

        return true;
    }
}
