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
//        if (!(sender instanceof Player)) {
//            sender.sendMessage("Only players can use this command.");
//            return true;
//        }

//        Player player = (Player) sender;
//        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&bSent to spawn."));
//        spawnUtil.teleport(player);

        try{
            Player player = null;

            if(args.length > 0){
                player = Bukkit.getPlayer(args[0]);
                Bukkit.getLogger().info("Player: " + player);
            }else if(sender instanceof Player){
                player = (Player) sender;
                Bukkit.getLogger().info("Player: " + player);
            }

            if(Objects.equals(pl.getStatusMap().get(player.getUniqueId()), "infected")){
                sender.sendMessage("1");

                ConfigUtil infConfig = new ConfigUtil(Minecraft_Test.getPlugin(Minecraft_Test.class), "Infected.yml");

                Location loc = new Location(
                        Bukkit.getWorld(infConfig.getConfig().getString("spawn.world")),
                        infConfig.getConfig().getDouble("spawn.x"),
                        infConfig.getConfig().getDouble("spawn.y"),
                        infConfig.getConfig().getDouble("spawn.z"),
                        (float) infConfig.getConfig().getDouble("spawn.yaw"),
                        (float) infConfig.getConfig().getDouble("spawn.pitch")
                );

                player.teleport(loc);
            }else if(Objects.equals(pl.getStatusMap().get(player.getUniqueId()), "survivor")) {
                sender.sendMessage("2");

                ConfigUtil surConfig = new ConfigUtil(Minecraft_Test.getPlugin(Minecraft_Test.class), "Survivor.yml");

                Location loc = new Location(
                        Bukkit.getWorld(surConfig.getConfig().getString("spawn.world")),
                        surConfig.getConfig().getDouble("spawn.x"),
                        surConfig.getConfig().getDouble("spawn.y"),
                        surConfig.getConfig().getDouble("spawn.z"),
                        (float) surConfig.getConfig().getDouble("spawn.yaw"),
                        (float) surConfig.getConfig().getDouble("spawn.pitch")
                );

                player.teleport(loc);
            }else {
                sender.sendMessage("3");

                Minecraft_Test.getPlugin(Minecraft_Test.class).getConfig();

                Location loc = new Location(
                        Bukkit.getWorld(Minecraft_Test.getPlugin(Minecraft_Test.class).getConfig().getString("default-spawn.world")),
                        Minecraft_Test.getPlugin(Minecraft_Test.class).getConfig().getDouble("default-spawn.x"),
                        Minecraft_Test.getPlugin(Minecraft_Test.class).getConfig().getDouble("default-spawn.y"),
                        Minecraft_Test.getPlugin(Minecraft_Test.class).getConfig().getDouble("default-spawn.z"),
                        (float) Minecraft_Test.getPlugin(Minecraft_Test.class).getConfig().getDouble("default-spawn.yaw"),
                        (float) Minecraft_Test.getPlugin(Minecraft_Test.class).getConfig().getDouble("default-spawn.pitch")
                );

                player.teleport(loc);
            }
        }catch(Exception e){
            sender.sendMessage("Something went wrong, please check the console.");
            Bukkit.getLogger().info("Something went wrong trying to teleport.");
            e.printStackTrace();
        }

        return true;
    }
}
