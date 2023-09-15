package mctest.minecraft_test.commands;

import mctest.minecraft_test.Minecraft_Test;
import mctest.minecraft_test.roles.SurvivalPlayer;
import mctest.minecraft_test.util.ConfigUtil;
import mctest.minecraft_test.util.SpawnUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Objects;

public class Infected implements CommandExecutor {

    private SurvivalPlayer s;

    public Infected(SurvivalPlayer s){
        this.s = s;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(args.length != 0){
            Player player = (Player) sender;

            switch (args[0].toLowerCase()){
                case "start":
                    try{
                        sender.sendMessage("Starting match.");
                        s.setTimer(0);
                    }catch(Exception e){
                        sender.sendMessage("Something went wrong, please check the console");
                        e.printStackTrace();
                    }
                    break;

                case "end":
                    try{
                        sender.sendMessage("Ending match.");
                        s.setTimer(-42);
                    }catch(Exception e){
                        sender.sendMessage("Something went wrong, please check the console");
                        e.printStackTrace();
                    }
                    break;

                case "setspawn":
                    try{
                        if(args[1] != null){
                            switch(args[1].toLowerCase()){
                                case "infected": case "i":
                                    ConfigUtil infConfig = new ConfigUtil(Minecraft_Test.getPlugin(Minecraft_Test.class), "Infected.yml");

                                    infConfig.getConfig().set("spawn.world", player.getLocation().getWorld().getName());
                                    infConfig.getConfig().set("spawn.x", player.getLocation().getX());
                                    infConfig.getConfig().set("spawn.y", player.getLocation().getY());
                                    infConfig.getConfig().set("spawn.z", player.getLocation().getZ());
                                    infConfig.getConfig().set("spawn.pitch", player.getLocation().getPitch());
                                    infConfig.getConfig().set("spawn.yaw", player.getLocation().getYaw());

                                    infConfig.save();

//                                    spawnUtil.set(player.getLocation());
                                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&bSet the &cinfected &bspawn."));

                                    break;
                                case "survivor": case "s":
                                    ConfigUtil surConfig = new ConfigUtil(Minecraft_Test.getPlugin(Minecraft_Test.class), "Survivor.yml");

                                    surConfig.getConfig().set("spawn.world", player.getLocation().getWorld().getName());
                                    surConfig.getConfig().set("spawn.x", player.getLocation().getX());
                                    surConfig.getConfig().set("spawn.y", player.getLocation().getY());
                                    surConfig.getConfig().set("spawn.z", player.getLocation().getZ());
                                    surConfig.getConfig().set("spawn.pitch", player.getLocation().getPitch());
                                    surConfig.getConfig().set("spawn.yaw", player.getLocation().getYaw());

                                    surConfig.save();

//                                    spawnUtil.set(player.getLocation());
                                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&bSet the &asurvivor &bspawn."));

                                    break;
                                case "default": case "d":
                                    Minecraft_Test.getPlugin(Minecraft_Test.class).getConfig().set("default-spawn.world", player.getLocation().getWorld().getName());
                                    Minecraft_Test.getPlugin(Minecraft_Test.class).getConfig().set("default-spawn.x", player.getLocation().getX());
                                    Minecraft_Test.getPlugin(Minecraft_Test.class).getConfig().set("default-spawn.y", player.getLocation().getY());
                                    Minecraft_Test.getPlugin(Minecraft_Test.class).getConfig().set("default-spawn.z", player.getLocation().getZ());
                                    Minecraft_Test.getPlugin(Minecraft_Test.class).getConfig().set("default-spawn.pitch", player.getLocation().getPitch());
                                    Minecraft_Test.getPlugin(Minecraft_Test.class).getConfig().set("default-spawn.yaw", player.getLocation().getYaw());

                                    Minecraft_Test.getPlugin(Minecraft_Test.class).saveConfig();

                                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&bSet the &edefault &bspawn."));

                                    break;
                                default:
                                    sender.sendMessage("Please specify which spawn you're trying to set! (Infected/Survivor/Default)");
                                    break;
                            }
                        }
                    }catch(Exception e){
                        sender.sendMessage("Something went wrong, please check the console");
                        e.printStackTrace();
                    }
                    break;

                case "spawn":
                    break;

                default:
                    sender.sendMessage("Valid sub commands: start, end, setSpawn.");
            }
        }else{
            sender.sendMessage("Valid sub commands: start, end, setSpawn.");
        }

        return true;
    }
}
