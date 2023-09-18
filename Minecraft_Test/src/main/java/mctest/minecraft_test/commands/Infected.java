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

import java.util.List;
import java.util.Map;
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
                    if(sender.hasPermission("infected.infected.start") || sender.hasPermission("infected.*")){
                        try{
                            sender.sendMessage("Starting match.");
                            s.setTimer(0);
                        }catch(Exception e){
                            sender.sendMessage("Something went wrong, please check the console");
                            e.printStackTrace();
                        }
                    }else{
                        sender.sendMessage("You do not have permission to do this!");
                        break;
                    }
                    break;

                case "end":
                    if(sender.hasPermission("infected.end") || sender.hasPermission("infected.*")){
                        try{
                            sender.sendMessage("Ending match.");
                            s.setTimer(-42);
                        }catch(Exception e){
                            sender.sendMessage("Something went wrong, please check the console");
                            e.printStackTrace();
                        }
                    }else{
                        sender.sendMessage("You do not have permission to do this!");
                        break;
                    }
                    break;

                case "setspawn": case "ss":
                    if(sender.hasPermission("infected.infected.setspawn") || sender.hasPermission("infected.*")){
                        try{
                            if(args.length < 2){
                                sender.sendMessage("Please specify which spawn you're trying to set! (Infected/Survivor/Default)");
                                break;
                            }

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
                            }else{
                                sender.sendMessage("Please specify which spawn you're trying to set! (Infected/Survivor/Default)");
                            }
                        }catch(Exception e){
                            sender.sendMessage("Something went wrong, please check the console");
                            e.printStackTrace();
                        }
                    }else{
                        sender.sendMessage("You do not have permission to do this!");
                        break;
                    }

                    break;

                case "setlobby": case "sl":
                    if(sender.hasPermission("infected.infected.setlobby") || sender.hasPermission("infected.*")){
                        try{
                            List<String> val = Minecraft_Test.getPlugin(Minecraft_Test.class).getConfig().getStringList("lobby-worlds");
                            String world = "";

                            if(args.length == 2){
                                if(Bukkit.getWorld(args[1]) != null){
                                    world = args[1];
                                }else{
                                    sender.sendMessage("This world does not exist!");
                                    break;
                                }
                            }else if(args.length == 1){
                                world = ((Player) sender).getWorld().getName();
                            }else{
                                sender.sendMessage("Correct usage (sets current world as lobby): /infected setLobby");
                                sender.sendMessage("or /infected setLobby [world name]");
                                break;
                            }

                            if(!val.contains(world)){
                                val.add(world);
                                Minecraft_Test.getPlugin(Minecraft_Test.class).getConfig().set("lobby-worlds", val);
                                Minecraft_Test.getPlugin(Minecraft_Test.class).saveConfig();
                                Minecraft_Test.getPlugin(Minecraft_Test.class).reloadConfig();

                                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&fSuccessfully &aadded &fworld &a" + ((Player) sender).getWorld().getName() + " &fto list of lobbies."));
                            }else{
                                sender.sendMessage("This world is already set as a lobby!");
                            }
                        }catch(Exception e){
                            sender.sendMessage("Something went wrong, please check the console");
                            e.printStackTrace();
                        }

                    }else{
                        sender.sendMessage("You do not have permission to do this!");
                        break;
                    }


                    break;

                case "dellobby": case "dl":
                    if(sender.hasPermission("infected.infected.dellobby") || sender.hasPermission("infected.*")){
                        try {
                            if(args.length == 2){
                                List<String> worlds = Minecraft_Test.getPlugin(Minecraft_Test.class).getConfig().getStringList("lobby-worlds");

                                if(worlds.contains(args[1])){
                                    worlds.remove(args[1]);
                                    Minecraft_Test.getPlugin(Minecraft_Test.class).getConfig().set("lobby-worlds", worlds);
                                    Minecraft_Test.getPlugin(Minecraft_Test.class).saveConfig();
                                    Minecraft_Test.getPlugin(Minecraft_Test.class).reloadConfig();

                                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&fSuccessfully &cremoved &fworld &c" + args[1] + " &ffrom list of lobbies."));
                                }else{
                                    sender.sendMessage("This world does not exist in lobbies!");
                                }
                            }else{
                                sender.sendMessage("Correct usage: /infected delLobby [world name]");
                            }
                        }catch(Exception e){
                            sender.sendMessage("Something went wrong, please check the console");
                            e.printStackTrace();
                        }
                    }else{
                        sender.sendMessage("You do not have permission to do this!");
                        break;
                    }

                    break;

                case "listlobbies": case "ll":
                    if(sender.hasPermission("infected.infected.listlobbies") || sender.hasPermission("infected.*")){
                        try{
                            List<String> worldList = Minecraft_Test.getPlugin(Minecraft_Test.class).getConfig().getStringList("lobby-worlds");

                            if(!worldList.isEmpty()){
                                int count = 1;
                                for(String x : worldList){
                                    String world = count + ") " + x;
                                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                                            "tellraw " + ((Player) sender).getUniqueId() + " {" +
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
                            sender.sendMessage("Something went wrong, please check the console");
                            e.printStackTrace();
                        }
                    }else{
                        sender.sendMessage("You do not have permission to do this!");
                        break;
                    }

                    break;
                case "setworld": case "sw":
                    if(sender.hasPermission("infected.infected.setworld") || sender.hasPermission("infected.*")){
                        try{
                            List<String> vals = Minecraft_Test.getPlugin(Minecraft_Test.class).getConfig().getStringList("allowed-worlds");
                            String world = "";

                            if(args.length == 2){
                                if(Bukkit.getWorld(args[1]) != null){
                                    world = args[1];
                                }else{
                                    sender.sendMessage("This world does not exist!");
                                    break;
                                }
                            }else if(args.length == 1){
                                world = ((Player) sender).getWorld().getName();
                            }else{
                                sender.sendMessage("Correct usage (sets current world as enabled): /infected setWorld");
                                sender.sendMessage("or /infected setWorld [world name]");
                                break;
                            }

                            if(!vals.contains(world)){
                                vals.add(world);
                                Minecraft_Test.getPlugin(Minecraft_Test.class).getConfig().set("allowed-worlds", vals);
                                Minecraft_Test.getPlugin(Minecraft_Test.class).saveConfig();
                                Minecraft_Test.getPlugin(Minecraft_Test.class).reloadConfig();

                                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&fSuccessfully &aadded &fworld &a" + ((Player) sender).getWorld().getName() + " &fto list of allowed worlds."));
                            }else{
                                sender.sendMessage("This world is already set as a lobby!");
                            }
                        }catch(Exception e){
                            sender.sendMessage("Something went wrong, please check the console");
                            e.printStackTrace();
                        }
                    }else{
                        sender.sendMessage("You do not have permission to do this!");
                        break;
                    }

                    break;

                case "delworld": case "dw":
                    if(sender.hasPermission("infected.infected.delworld") || sender.hasPermission("infected.*")){
                        try {
                            if(args.length == 2){
                                List<String> worlds = Minecraft_Test.getPlugin(Minecraft_Test.class).getConfig().getStringList("allowed-worlds");

                                if(worlds.contains(args[1])){
                                    worlds.remove(args[1]);
                                    Minecraft_Test.getPlugin(Minecraft_Test.class).getConfig().set("allowed-worlds", worlds);
                                    Minecraft_Test.getPlugin(Minecraft_Test.class).saveConfig();
                                    Minecraft_Test.getPlugin(Minecraft_Test.class).reloadConfig();

                                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&fSuccessfully &cremoved &fworld &c" + args[1] + " &ffrom allowed worlds."));
                                }else{
                                    sender.sendMessage("This world does not exist in allowed-worlds!");
                                }
                            }else{
                                sender.sendMessage("Correct usage: /infected delWorld [world name]");
                            }
                        }catch(Exception e){
                            sender.sendMessage("Something went wrong, please check the console");
                            e.printStackTrace();
                        }
                    }else{
                        sender.sendMessage("You do not have permission to do this!");
                        break;
                    }

                    break;

                case "listworlds": case "lw":
                    if(sender.hasPermission("infected.infected.listworlds") || sender.hasPermission("infected.*")){
                        try{
                            List<String> worlds = Minecraft_Test.getPlugin(Minecraft_Test.class).getConfig().getStringList("allowed-worlds");

                            if(!worlds.isEmpty()){
                                int count = 1;
                                for(String x : worlds){
                                    String world = count + ") " + x;
                                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                                            "tellraw " + ((Player) sender).getUniqueId() + " {" +
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
                            sender.sendMessage("Something went wrong, please check the console");
                            e.printStackTrace();
                        }
                    }else{
                        sender.sendMessage("You do not have permission to do this!");
                        break;
                    }

                    break;
                default:
                    sender.sendMessage("Valid sub commands: start, end, setSpawn (ss), setLobby (sl), delLobby (dl), listLobbies (ll), setWorld(sw), delWorld (dw), listWorlds (lw).");
            }
        }else{
            sender.sendMessage("Valid sub commands: start, end, setSpawn (ss), setLobby (sl), delLobby (dl), listLobbies (ll), setWorld(sw), delWorld (dw), listWorlds (lw).");
        }

        return true;
    }
}
