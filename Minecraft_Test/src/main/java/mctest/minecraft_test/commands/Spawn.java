package mctest.minecraft_test.commands;

import mctest.minecraft_test.Minecraft_Test;
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
    private SpawnUtil spawnUtil;
    private SurvivalPlayer pl;

    public Spawn(SpawnUtil spawnUtil, SurvivalPlayer pl) {
        this.spawnUtil = spawnUtil;
        this.pl = pl;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String [] args) {
        try{
            Player player = null;

            if(args.length > 0){
                player = Bukkit.getPlayer(args[0]);
            }else if(sender instanceof Player){
                player = (Player) sender;
            }

            if(Objects.equals(pl.getStatusMap().get(player.getUniqueId()), "infected")){
                player.teleport(pl.getInfSpawn());
            }else if(Objects.equals(pl.getStatusMap().get(player.getUniqueId()), "survivor")) {
                player.teleport(pl.getSurSpawn());
            }else {
                player.teleport(pl.getDefaultSpawn());
            }
        }catch(Exception e){
            sender.sendMessage("Something went wrong, please check the console.");
            Bukkit.getLogger().info("Something went wrong trying to teleport.");
            e.printStackTrace();
        }

        return true;
    }
}
