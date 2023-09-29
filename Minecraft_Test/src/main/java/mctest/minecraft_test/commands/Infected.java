package mctest.minecraft_test.commands;

import mctest.minecraft_test.Minecraft_Test;
import mctest.minecraft_test.roles.GamesList;
import mctest.minecraft_test.roles.SurvivalPlayer;
import mctest.minecraft_test.util.ConfigUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import java.util.*;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Infected implements CommandExecutor, Listener {

    private SurvivalPlayer s;
    private GamesList g;
    private Minecraft_Test pl = Minecraft_Test.getPlugin(Minecraft_Test.class);
    public Infected(Minecraft_Test plugin, SurvivalPlayer s, GamesList g){
        Bukkit.getPluginManager().registerEvents(this, plugin);
        this.s = s;
        this.g = g;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!event.getView().getTitle().equals("Infection Games Available")) {
            return;
        }
        try {
            Player player = (Player) event.getWhoClicked();
            if (event.getCurrentItem() != null) {
                if (event.getSlot() == 4) {
                    event.setCancelled(true);
                } else {
                    g.getGameMap().get(event.getCurrentItem().getItemMeta().getDisplayName()).setUnassigned(player);
                    event.setCancelled(true);
                    player.closeInventory();
                }
            }
        } catch (Exception e) {
            Bukkit.getLogger().info("Clicked on empty slot");
        }

    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(args.length != 0){
            Player player = (Player) sender;

            switch (args[0].toLowerCase()){
                case "start":
                    if(sender.hasPermission("infected.infected.start") || sender.hasPermission("infected.*") || sender.hasPermission("infected.infected.*")){
                        try{
                            sender.sendMessage("Starting match.");
                            g.getGameMap().get(player.getWorld().getName()).setTimer(0);
//                            s.setTimer(0);
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
                    if(sender.hasPermission("infected.infected.end") || sender.hasPermission("infected.*") || sender.hasPermission("infected.infected.*")){
                        try{
                            sender.sendMessage("Ending match.");
                            g.getGameMap().get(player.getWorld().getName()).setTimer(-42);
//                            s.setTimer(-42);
                        }catch(Exception e){
                            sender.sendMessage("Something went wrong, please check the console");
                            e.printStackTrace();
                        }
                    }else{
                        sender.sendMessage("You do not have permission to do this!");
                        break;
                    }
                    break;

                case "addspawn": case "as":
                    if(sender.hasPermission("infected.infected.addspawn") || sender.hasPermission("infected.*") || sender.hasPermission("infected.infected.*")){
                        try{
                            if(args.length < 2 || args.length > 4){
                                sender.sendMessage("Usage: /infected addSpawn/as [infected/survivor/default] [label (optional for first spawn)] overwrite(optional to overwrite" +
                                        " existing spawn)");
                                break;
                            }

                            if(args[1] != null){
                                switch(args[1].toLowerCase()){
                                    case "infected": case "i":
                                        ConfigUtil infConfig = new ConfigUtil(pl, "Infected.yml");
                                        String infPath = "spawns." + player.getLocation().getWorld().getName();
                                        String infLabel = null;

                                        if(infConfig.getConfig().get(infPath) == null && (args.length < 3)){
                                            infLabel = "first";
                                            infPath += ".first";
                                            infConfig.getConfig().createSection(infPath);
                                        }else{
                                            if(args.length < 3){
                                                sender.sendMessage("Usage: /infected addSpawn/as [infected/survivor/default] [label (optional for first spawn)] overwrite(optional to overwrite" +
                                                        " existing spawn)");
                                                break;
                                            }else{
                                                infPath += ("." + args[2].toLowerCase());
                                                infLabel = args[2].toLowerCase();

                                                if(infConfig.getConfig().get(infPath) != null && args.length < 4){
                                                    sender.sendMessage("This label already exists, please choose another or choose to " +
                                                            "overwrite!");
                                                    break;
                                                }else if(infConfig.getConfig().get(infPath) != null && !Objects.equals(args[3].toLowerCase(), "overwrite")){
                                                    sender.sendMessage("This label already exists, please choose another or choose to " +
                                                            "overwrite!");
                                                    break;
                                                }

                                                infConfig.getConfig().set(infPath, "");
                                            }
                                        }

                                        infConfig.getConfig().set(infPath + ".x", player.getLocation().getX());
                                        infConfig.getConfig().set(infPath + ".y", player.getLocation().getY());
                                        infConfig.getConfig().set(infPath + ".z", player.getLocation().getZ());
                                        infConfig.getConfig().set(infPath + ".pitch", player.getLocation().getPitch());
                                        infConfig.getConfig().set(infPath + ".yaw", player.getLocation().getYaw());

                                        infConfig.save();
                                        s.reloadConfigs();

                                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&bAdded an &cinfected &bspawn in world &a" +
                                                player.getLocation().getWorld().getName() + "&b with the label &c" + infLabel));

                                        break;
                                    case "survivor": case "s":
                                        ConfigUtil surConfig = new ConfigUtil(pl, "Survivor.yml");
                                        String surPath = "spawns." + player.getLocation().getWorld().getName();
                                        String surLabel = null;

                                        if(surConfig.getConfig().get(surPath) == null && (args.length < 3)){
                                            surLabel = "first";
                                            surPath += ".first";
                                            surConfig.getConfig().createSection(surPath);
                                        }else{
                                            if(args.length < 3){
                                                sender.sendMessage("Usage: /infected addSpawn/as [infected/survivor/default] [label (optional for first spawn)] overwrite(optional to overwrite" +
                                                        " existing spawn)");
                                                break;
                                            }else{
                                                surPath += ("." + args[2].toLowerCase());
                                                surLabel = args[2].toLowerCase();

                                                if(surConfig.getConfig().get(surPath) != null && args.length < 4){
                                                    sender.sendMessage("This label already exists, please choose another or choose to " +
                                                            "overwrite!");
                                                    break;
                                                }else if(surConfig.getConfig().get(surPath) != null && !Objects.equals(args[3].toLowerCase(), "overwrite")){
                                                    sender.sendMessage("This label already exists, please choose another or choose to " +
                                                            "overwrite!");
                                                    break;
                                                }

                                                surConfig.getConfig().set(surPath, "");
                                            }
                                        }

                                        surConfig.getConfig().set(surPath + ".x", player.getLocation().getX());
                                        surConfig.getConfig().set(surPath + ".y", player.getLocation().getY());
                                        surConfig.getConfig().set(surPath + ".z", player.getLocation().getZ());
                                        surConfig.getConfig().set(surPath + ".pitch", player.getLocation().getPitch());
                                        surConfig.getConfig().set(surPath + ".yaw", player.getLocation().getYaw());

                                        surConfig.save();
                                        s.reloadConfigs();

                                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&bAdded a &asurvivor &bspawn in world &a" +
                                                player.getLocation().getWorld().getName() + "&b with the label &a" + surLabel));

                                        break;
                                    case "default": case "d":
                                        String defPath = "default-spawns." + player.getLocation().getWorld().getName();

                                        if(pl.getConfig().get(defPath) != null && args.length < 3){
                                            sender.sendMessage("This world already has a default spawn, please choose another world or choose to " +
                                                    "overwrite!");
                                            break;
                                        }else if(pl.getConfig().get(defPath) != null && !Objects.equals(args[2].toLowerCase(), "overwrite")){
                                            sender.sendMessage("This world already has a default spawn, please choose another world or choose to " +
                                                    "overwrite!");
                                            break;
                                        }

                                        pl.getConfig().set(defPath, "");

                                        pl.getConfig().set(defPath + ".x", player.getLocation().getX());
                                        pl.getConfig().set(defPath + ".y", player.getLocation().getY());
                                        pl.getConfig().set(defPath + ".z", player.getLocation().getZ());
                                        pl.getConfig().set(defPath + ".pitch", player.getLocation().getPitch());
                                        pl.getConfig().set(defPath + ".yaw", player.getLocation().getYaw());

                                        pl.saveConfig();
                                        pl.reloadConfig();

                                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&bSet the &edefault &bspawn in world &e" +
                                                player.getLocation().getWorld().getName()));

                                        break;
                                    default:
                                        sender.sendMessage("Usage: /infected addSpawn/as [infected/survivor/default] [label (optional for first spawn)] overwrite(optional to overwrite" +
                                                " existing spawn)");
                                        break;
                                }
                            }else{
                                sender.sendMessage("Usage: /infected addSpawn/as [infected/survivor/default] [label (optional for first spawn)] overwrite(optional to overwrite" +
                                        " existing spawn)");
                            }
                        }catch(Exception e){
                            sender.sendMessage("Something went wrong, please check the console");
                            Bukkit.getLogger().warning("Something went wrong trying to set a spawn!");
                            e.printStackTrace();
                        }
                    }else{
                        sender.sendMessage("You do not have permission to do this!");
                        break;
                    }

                    break;
                case "delspawn": case "ds":
                    try{
                        if(args.length > 4 || args.length < 3){
                            sender.sendMessage("Usage to delete all spawns in a world: /infected delSpawn(ds) [infected/survivor/default] [world name]");
                            sender.sendMessage("Usage to delete a specific spawn in a world: /infected delSpawn(ds) [infected/survivor/default] [world name] [label]");
                            break;
                        }else{
                            ConfigUtil c = null;
                            String path = null;
                            boolean isDefault = false;
                            String type = null;
                            String color = null;

                            switch(args[1].toLowerCase()){
                                case "infected": case "i":
                                    c = new ConfigUtil(pl, "Infected.yml");
                                    color = "&c";
                                    type = "infected";
                                    break;
                                case "survivor": case "s":
                                    c = new ConfigUtil(pl, "Survivor.yml");
                                    color = "&a";
                                    type = "survivor";
                                    break;
                                case "default": case "d":
                                    path = "default-spawns";
                                    isDefault = true;
                                    break;
                            }
                            if(args.length == 3){
                                if(isDefault){
                                    path += ("." + args[2].toLowerCase());

                                    if(pl.getConfig().get(path) != null){
                                        pl.getConfig().set(path, null);
                                        pl.saveConfig();

                                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&bDeleted &edefault &bspawn" +
                                                " for world &e " + args[2].toLowerCase()));
                                    }else{
                                        sender.sendMessage("That world doesn't exist!");
                                    }

                                    break;
                                }else{
                                    path = "spawns." + args[2].toLowerCase();
                                }
                            }else {
                                path = "spawns." + args[2].toLowerCase() + "." + args[3].toLowerCase();
                            }

                            if(c.getConfig().get(path) != null){
                                c.getConfig().set(path, null);
                                c.save();
                                s.reloadConfigs();

                                if(args.length == 3){
                                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&bDeleted all " + color + type +
                                            "&b spawns in world &a" + args[2].toLowerCase()));
                                }else{
                                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&bDeleted " + color + type +
                                            "&b spawn with label " + color + args[3].toLowerCase() + "&b in world &a" + args[2].toLowerCase()));
                                }
                                break;
                            }else{
                                sender.sendMessage("That world doesn't exist!");
                                break;
                            }
                        }
                    }catch(Exception e){
                        sender.sendMessage("Something went wrong, please check the console");
                        Bukkit.getLogger().warning("Something went wrong trying to delete a spawn!");
                        e.printStackTrace();
                    }

                    break;

                case "join" : case "j":
                    if(sender.hasPermission("infected.infected.join") || sender.hasPermission("infected.*") || sender.hasPermission("infected.infected.*")){
                        if (args.length == 1) {
                            int it = 0;
                            for (Map.Entry<String, SurvivalPlayer> entry : g.getGameMap().entrySet()) {
                                it++;
                                if (!entry.getValue().getPlaying()) {
                                    String current = player.getWorld().getName();
                                    Bukkit.getLogger().severe(current);

                                    player.teleport(s.getDefaultSpawn(entry.getKey()));
                                    entry.getValue().setUnassigned(player);
                                    g.getGameMap().get(entry.getKey()).addPreviousWorld(player.getUniqueId(), current);
                                    break;
                                }
                                if (it == g.getGameMap().size()) {
                                    player.sendMessage("All games are full");
                                }
                            }
                        } else if (args.length == 2) {
                            if (g.getGameMap().containsKey(args[1])) {
                                if (!g.getGameMap().get(args[1]).getPlaying()) {
//                                    previousWorlds.put(player.getUniqueId(), player.getWorld().getName());
                                    String current = player.getWorld().getName();
                                    Bukkit.getLogger().severe(current);

                                    ((Player) sender).teleport(s.getDefaultSpawn(args[1]));
                                    g.getGameMap().get(args[1]).setUnassigned(player);
                                    g.getGameMap().get(args[1]).addPreviousWorld(player.getUniqueId(), current);
                                } else {
                                    player.sendMessage("Game is already in session.");
                                }
                            }
                        }else{
                            sender.sendMessage("Correct usage (Random map): /infected join/j");
                            sender.sendMessage("Correct usage (Specific map): /infected join/j [name]");
                        }
                    }else{
                        sender.sendMessage("You do not have permission to do this!");
                        break;
                    }
                    break;
                case "setlobby": case "sl":
                    if(sender.hasPermission("infected.infected.setlobby") || sender.hasPermission("infected.*") || sender.hasPermission("infected.infected.*")){
                        try{
                            List<String> val = pl.getConfig().getStringList("lobby-worlds");
                            String world = "";

                            if(args.length > 2){
                                sender.sendMessage("Correct usage (sets current world as lobby): /infected setLobby");
                                sender.sendMessage("or /infected setLobby [world name]");
                            }

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
                                pl.getConfig().set("lobby-worlds", val);
                                pl.saveConfig();
                                pl.reloadConfig();

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
                    if(sender.hasPermission("infected.infected.dellobby") || sender.hasPermission("infected.*") || sender.hasPermission("infected.infected.*")){
                        try {
                            if(args.length == 2){
                                List<String> worlds = pl.getConfig().getStringList("lobby-worlds");

                                if(worlds.contains(args[1])){
                                    worlds.remove(args[1]);
                                    pl.getConfig().set("lobby-worlds", worlds);
                                    pl.saveConfig();
                                    pl.reloadConfig();

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
                    if(sender.hasPermission("infected.infected.listlobbies") || sender.hasPermission("infected.*") || sender.hasPermission("infected.infected.*")){
                        try{
                            List<String> worldList = pl.getConfig().getStringList("lobby-worlds");

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
                    if(sender.hasPermission("infected.infected.setworld") || sender.hasPermission("infected.*") || sender.hasPermission("infected.infected.*")){
                        try{
                            List<String> vals = pl.getConfig().getStringList("allowed-worlds");
                            String world = "";

                            if(args.length > 2){
                                sender.sendMessage("Correct usage (sets current world as enabled): /infected setWorld");
                                sender.sendMessage("or /infected setWorld [world name]");
                            }
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
                                g.addWorld(world);
                                pl.getConfig().set("allowed-worlds", vals);
                                pl.saveConfig();
                                pl.reloadConfig();

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
                    if(sender.hasPermission("infected.infected.delworld") || sender.hasPermission("infected.*") || sender.hasPermission("infected.infected.*")){
                        try {
                            if(args.length == 2){
                                List<String> worlds = pl.getConfig().getStringList("allowed-worlds");

                                if(worlds.contains(args[1])){
                                    worlds.remove(args[1]);
                                    g.removeWorld(args[1]);
                                    pl.getConfig().set("allowed-worlds", worlds);
                                    pl.saveConfig();
                                    pl.reloadConfig();

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
                    if(sender.hasPermission("infected.infected.listworlds") || sender.hasPermission("infected.*") || sender.hasPermission("infected.infected.*")){
                        try{
                            List<String> worlds = pl.getConfig().getStringList("allowed-worlds");

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
                case "setrole": case "sr": case "role":
                    if(sender.hasPermission("infected.infected.setrole") || sender.hasPermission("infected.*") || sender.hasPermission("infected.infected.*")){
                        try{
                            if(args.length != 3){
                                sender.sendMessage("Usage: /infected setRole/sr [player] [infected(i)/survivor(s)/notplaying(np)]");
                            }else{
                                if(Bukkit.getPlayer(args[1]) == null){
                                    sender.sendMessage("That was not a valid player!");
                                }else{
                                    String role = null;
                                    switch (args[2].toLowerCase()){
                                        case "infected": case "i":
                                            role = "infected";
                                            break;
                                        case "survivor": case "s":
                                            role = "survivor";
                                            break;
                                        case "notplaying": case "np":
//                                            s.setNotPlaying(Bukkit.getPlayer(args[1]));
                                            g.getGameMap().get(player.getWorld().getName()).setNotPlaying(Bukkit.getPlayer(args[1]));
                                            break;
                                        default:
                                            sender.sendMessage("Must specify role:");
                                            sender.sendMessage("infected (i), survivor (s), notplaying (np)");
                                            break;
                                    }

                                    if(role != null){
//                                        s.removeEffects(Bukkit.getPlayer(args[1]));
                                        g.getGameMap().get(player.getWorld().getName()).removeEffects(Bukkit.getPlayer(args[1]));
                                        if(s.getPlaying()){
//                                            s.getStatusMap().put(Bukkit.getPlayer(args[1]).getUniqueId(), role);
//                                            s.setRole(Bukkit.getPlayer(args[1]));
                                            g.getGameMap().get(player.getWorld().getName()).getStatusMap().put(Bukkit.getPlayer(args[1]).getUniqueId(), role);
                                            g.getGameMap().get(player.getWorld().getName()).setRole(Bukkit.getPlayer(args[1]));
                                        }else{
                                            sender.sendMessage("The player " + Bukkit.getPlayer(args[1]).getName() + " is not currently in a match!");
                                        }
                                    }
                                }
                            }
                        }catch(Exception e){
                            sender.sendMessage("Something went wrong, please check the console");
                            Bukkit.getLogger().warning("Couldn't set role.");
                            e.printStackTrace();
                        }
                    }else{
                        sender.sendMessage("You do not have permission to do this!");
                        break;
                    }

                    break;
//                case "reloadgamemap": case "rgm":
//                    try{
//                        g.reloadGameMap();
//                    }catch(Exception e){
//                        sender.sendMessage("Something went wrong, please check the console");
//                        Bukkit.getLogger().warning("Couldn't reload game map.");
//                        e.printStackTrace();
//                    }
//                    break;
                case "games": case "g":
                    Inventory gamesList = Bukkit.createInventory(player, 9*6, "Infection Games Available");
                    ItemStack list = new ItemStack(Material.GOLD_BLOCK);
                    ItemMeta meta = list.getItemMeta();
                    meta.setDisplayName(ChatColor.GOLD + "Worlds available");
                    meta.setLore(g.getGameInfos());
                    list.setItemMeta(meta);
                    gamesList.setItem(4, list);
                    int i = 9;
                    for (Map.Entry<String, SurvivalPlayer> entry : g.getGameMap().entrySet()) {
                        ItemStack game = new ItemStack(Material.DIAMOND_BLOCK);
                        ItemMeta m = game.getItemMeta();
                        m.setDisplayName(entry.getKey());
                        m.setLore(g.getInfoString(entry.getKey()));
                        game.setItemMeta(m);
                        gamesList.setItem(i++, game);
                    }
                    player.openInventory(gamesList);
                default:
                    sender.sendMessage("Valid sub commands: start, end, games (g), addSpawn (as), setLobby (sl), delLobby (dl)," +
                            " listLobbies (ll), setWorld(sw), delWorld (dw), listWorlds (lw), setRole (sr).");
            }
        }else{
            sender.sendMessage("Valid sub commands: start, end, games (g), addSpawn (as), setLobby (sl), delLobby (dl)," +
                    " listLobbies (ll), setWorld(sw), delWorld (dw), listWorlds (lw), setRole (sr).");
        }

        return true;
    }
}
