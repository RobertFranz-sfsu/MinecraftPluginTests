package mctest.minecraft_test.commands;

import com.google.gson.Gson;
import mctest.minecraft_test.Minecraft_Test;
import mctest.minecraft_test.roles.GamesList;
import mctest.minecraft_test.roles.SurvivalPlayer;
import mctest.minecraft_test.util.ConfigUtil;
import org.apache.commons.io.IOUtils;
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
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.bukkit.inventory.meta.SkullMeta;

import java.io.*;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Base64;
import java.util.UUID;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.bukkit.inventory.ItemFlag;

import com.google.gson.JsonObject;

import java.util.*;


@SuppressWarnings({"FieldMayBeFinal", "CallToPrintStackTrace", "NullableProblems"})
public class Infected implements CommandExecutor, Listener {
    private GamesList g;

    private Minecraft_Test pl = Minecraft_Test.getPlugin(Minecraft_Test.class);
    public Infected(Minecraft_Test plugin, GamesList g){
        Bukkit.getPluginManager().registerEvents(this, plugin);
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
                if (event.getSlot() < 18) {
                    event.setCancelled(true);
                } else {
                    if (g.getGameMap().get(Objects.requireNonNull(event.getCurrentItem().getItemMeta()).getDisplayName()).getPlaying()) {
                        player.sendMessage("Game already in session");
                    }
                    else if (g.getGameMap().get(event.getCurrentItem().getItemMeta().getDisplayName()).getStatusMap().containsKey(player.getUniqueId())) {
                        player.sendMessage("You're already in the game");
                    }else {
                        String current = player.getWorld().getName();

                        player.teleport(g.getGameMap().get(event.getCurrentItem().getItemMeta().getDisplayName()).getDefaultSpawn(event.getCurrentItem().getItemMeta().getDisplayName()));
                        g.getGameMap().get(event.getCurrentItem().getItemMeta().getDisplayName()).setUnassigned(player);
                        g.getGameMap().get(event.getCurrentItem().getItemMeta().getDisplayName()).addPreviousWorld(player.getUniqueId(), current);

                        player.closeInventory();

                        event.setCancelled(true);
                    }
                }
            }
        } catch (Exception e) {
            Bukkit.getLogger().info("Clicked on empty slot");
        }
    }

    private ItemStack createLeaderboard(int pos, String name) {
        ArrayList<String> topList = new ArrayList<>();
        ArrayList<UUID> uid = new ArrayList<>();
        topList.add("");

        pl.getStatsMap().entrySet().stream()
                .sorted((k1, k2) -> -k1.getValue()[pos].compareTo(k2.getValue()[pos]))
                .limit(3)
                .forEach(k -> {
                    UUID u = UUID.fromString(k.getKey());
                    uid.add(u);
                    topList.add(Bukkit.getOfflinePlayer(u).getName() + ": " + k.getValue()[pos]);
                });

        ItemStack leaderboard = new ItemStack(this.getCustomHead(uid.get(0)));
        ItemMeta leaderboardMeta = leaderboard.getItemMeta();
        assert leaderboardMeta != null;
        leaderboardMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        Objects.requireNonNull(leaderboardMeta).setDisplayName(ChatColor.GOLD + name);

        leaderboardMeta.setLore(topList);
        leaderboard.setItemMeta(leaderboardMeta);
        return leaderboard;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(args.length != 0){
            Player player = (Player) sender;
            String playerWorld = player.getWorld().getName();

            switch (args[0].toLowerCase()){
                case "start":
                    if(sender.hasPermission("infected.infected.start") || sender.hasPermission("infected.*") || sender.hasPermission("infected.infected.*")){
                        try{
                            sender.sendMessage("Starting match.");
                            g.getGameMap().get(player.getWorld().getName()).setTimer(0);
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
                                        String infPath = "spawns." + Objects.requireNonNull(player.getLocation().getWorld()).getName();
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
                                        g.getGameMap().get(playerWorld).reloadConfigs();

                                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&bAdded an &cinfected &bspawn in world &a" +
                                                player.getLocation().getWorld().getName() + "&b with the label &c" + infLabel));

                                        break;
                                    case "survivor": case "s":
                                        ConfigUtil surConfig = new ConfigUtil(pl, "Survivor.yml");
                                        String surPath = "spawns." + Objects.requireNonNull(player.getLocation().getWorld()).getName();
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
                                        g.getGameMap().get(playerWorld).reloadConfigs();

                                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&bAdded a &asurvivor &bspawn in world &a" +
                                                player.getLocation().getWorld().getName() + "&b with the label &a" + surLabel));

                                        break;
                                    case "default": case "d":
                                        String defPath = "default-spawns." + Objects.requireNonNull(player.getLocation().getWorld()).getName();

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
                    if(sender.hasPermission("infected.infected.delspawn") || sender.hasPermission("infected.*") || sender.hasPermission("infected.infected.*")) {
                        try {
                            if (args.length > 4 || args.length < 3) {
                                sender.sendMessage("Usage to delete all spawns in a world: /infected delSpawn(ds) [infected/survivor/default] [world name]");
                                sender.sendMessage("Usage to delete a specific spawn in a world: /infected delSpawn(ds) [infected/survivor/default] [world name] [label]");
                                break;
                            } else {
                                ConfigUtil c = null;
                                String path = null;
                                boolean isDefault = false;
                                String type = null;
                                String color = null;

                                switch (args[1].toLowerCase()) {
                                    case "infected":
                                    case "i":
                                        c = new ConfigUtil(pl, "Infected.yml");
                                        color = "&c";
                                        type = "infected";
                                        break;
                                    case "survivor":
                                    case "s":
                                        c = new ConfigUtil(pl, "Survivor.yml");
                                        color = "&a";
                                        type = "survivor";
                                        break;
                                    case "default":
                                    case "d":
                                        path = "default-spawns";
                                        isDefault = true;
                                        break;
                                }
                                if (args.length == 3) {
                                    if (isDefault) {
                                        path += ("." + args[2].toLowerCase());

                                        if (pl.getConfig().get(path) != null) {
                                            pl.getConfig().set(path, null);
                                            pl.saveConfig();

                                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&bDeleted &edefault &bspawn" +
                                                    " for world &e " + args[2].toLowerCase()));
                                        } else {
                                            sender.sendMessage("That world doesn't exist!");
                                        }

                                        break;
                                    } else {
                                        path = "spawns." + args[2].toLowerCase();
                                    }
                                } else {
                                    path = "spawns." + args[2].toLowerCase() + "." + args[3].toLowerCase();
                                }

                                if (Objects.requireNonNull(c).getConfig().get(path) != null) {
                                    c.getConfig().set(path, null);
                                    c.save();
                                    g.getGameMap().get(playerWorld).reloadConfigs();

                                    if (args.length == 3) {
                                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&bDeleted all " + color + type +
                                                "&b spawns in world &a" + args[2].toLowerCase()));
                                    } else {
                                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&bDeleted " + color + type +
                                                "&b spawn with label " + color + args[3].toLowerCase() + "&b in world &a" + args[2].toLowerCase()));
                                    }
                                    break;
                                } else {
                                    sender.sendMessage("That world doesn't exist!");
                                    break;
                                }
                            }
                        } catch (Exception e) {
                            sender.sendMessage("Something went wrong, please check the console");
                            Bukkit.getLogger().warning("Something went wrong trying to delete a spawn!");
                            e.printStackTrace();
                        }
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

                                    player.teleport(entry.getValue().getDefaultSpawn(entry.getKey()));
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
                                    String current = player.getWorld().getName();
                                    Bukkit.getLogger().severe(current);

                                    ((Player) sender).teleport(g.getGameMap().get(args[1]).getDefaultSpawn(args[1]));
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
                                            "tellraw " + sender.getName() + " {" +
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
                                            "tellraw " + sender.getName() + " {" +
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
                                            g.getGameMap().get(player.getWorld().getName()).setNotPlaying(Objects.requireNonNull(Bukkit.getPlayer(args[1])));
                                            break;
                                        default:
                                            sender.sendMessage("Must specify role:");
                                            sender.sendMessage("infected (i), survivor (s), notplaying (np)");
                                            break;
                                    }

                                    if(role != null){
                                        g.getGameMap().get(player.getWorld().getName()).removeEffects(Objects.requireNonNull(Bukkit.getPlayer(args[1])));
                                        if(g.getGameMap().get(player.getWorld().getName()).getPlaying()){
                                            g.getGameMap().get(player.getWorld().getName()).getStatusMap().put(Objects.requireNonNull(Bukkit.getPlayer(args[1])).getUniqueId(), role);
                                            g.getGameMap().get(player.getWorld().getName()).setRole(Objects.requireNonNull(Bukkit.getPlayer(args[1])));
                                        }else{
                                            sender.sendMessage("The player " + Objects.requireNonNull(Bukkit.getPlayer(args[1])).getName() + " is not currently in a match!");
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
                case "games": case "g":
                    if(sender.hasPermission("infected.infected.games") || sender.hasPermission("infected.*") || sender.hasPermission("infected.infected.*")){
                        Inventory gamesList = Bukkit.createInventory(player, 9*6, "Infection Games Available");

                        // List of all games
                        ItemStack list = new ItemStack(Material.GOLD_BLOCK);
                        ItemMeta meta = list.getItemMeta();
                        Objects.requireNonNull(meta).setDisplayName(ChatColor.GOLD + "Worlds Available");
                        meta.setLore(g.getGameInfos());
                        list.setItemMeta(meta);
                        gamesList.setItem(13, list);

                        // Players' Stats
                        if(pl.doKeepScore()) {
                            ConfigUtil scoreCon = new ConfigUtil(pl, System.getProperty("file.separator") + "config.yml");
                            String path = System.getProperty("file.separator") + "Scores" + System.getProperty("file.separator") + player.getUniqueId() + ".yml";
                            ConfigUtil con = new ConfigUtil(pl, path);

                            // Personal Stats List
                            ItemStack stats = new ItemStack(Material.IRON_SWORD);
                            ItemMeta statsMeta = stats.getItemMeta();
                            Objects.requireNonNull(statsMeta).setDisplayName(ChatColor.GOLD + "Personal Stats");
                            ArrayList<String> statList = new ArrayList<>();
                            statList.add("");

                            statList.add(ChatColor.GRAY + "Games Played: " + con.getConfig().get("games-played"));
                            int kills = con.getConfig().getInt("infected-kills") + con.getConfig().getInt("survivor-kills");
                            statList.add(ChatColor.GRAY + "Total Kills: " + kills);
                            statList.add(ChatColor.BLUE + "Games Played: " + con.getConfig().get("games-played"));
                            statList.add(ChatColor.RED + "Kills as Infected: " + con.getConfig().get("infected-kills"));
                            statList.add(ChatColor.RED + "Infected Wins: " + con.getConfig().get("infected-wins"));
                            statList.add(ChatColor.GREEN + "Kills as Survivor: " + con.getConfig().get("survivor-kills"));
                            statList.add(ChatColor.GREEN + "Survivor Wins: " + con.getConfig().get("survivor-wins"));
                            statsMeta.setLore(statList);
                            statsMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                            stats.setItemMeta(statsMeta);
                            gamesList.setItem(4, stats);

                            // Top Survivor Wins List
                            gamesList.setItem(3, createLeaderboard(4, "Most Wins as Survivor"));

                            // Top Survivor Kills List
                            gamesList.setItem(2, createLeaderboard(3, "Most Kills as Survivor"));

                            // Top Infected Kills List
                            gamesList.setItem(5, createLeaderboard(1, "Most Kills as Infected"));

                            // Top Infected Wins List
                            gamesList.setItem(6, createLeaderboard(2, "Most Wins as Infected"));
                        }

                        // Each game that is available
                        int i = 18;
                        for (Map.Entry<String, SurvivalPlayer> entry : g.getGameMap().entrySet()) {
                            ItemStack game = new ItemStack(Material.DIAMOND_BLOCK);
                            ItemMeta m = game.getItemMeta();
                            Objects.requireNonNull(m).setDisplayName(entry.getKey());
                            m.setLore(g.getInfoString(entry.getKey()));
                            game.setItemMeta(m);
                            gamesList.setItem(i++, game);
                        }
                        player.openInventory(gamesList);

                        break;
                    }else{
                        sender.sendMessage("You do not have permission to do this!");
                        break;
                    }

                case "test":
                    Integer[] nArr = pl.getStatsMap().get(Objects.requireNonNull(((Player) sender).getUniqueId().toString()));
                    for(Integer x : nArr){
                        sender.sendMessage(nArr[x] + "");
                    }
//                    for(String x : pl.getStatsMap().keySet()){
//                        sender.sendMessage("UUID: " + x);
//                        sender.sendMessage("VALS: " + Arrays.toString(pl.getStatsMap().get(x)));
//                    }
                    break;
                default:
                    sender.sendMessage("Valid sub commands: start, end, games (g), addSpawn (as), setLobby (sl), delLobby (dl)," +
                            " listLobbies (ll), setWorld(sw), delWorld (dw), listWorlds (lw), setRole (sr).");
            }
        }else{
            sender.sendMessage("Valid sub commands: start, end, games (g), addSpawn (as), setLobby (sl), delLobby (dl)," +
                    " listLobbies (ll), setWorld(sw), delWorld (dw), listWorlds (lw), setRole (sr), games (g).");
        }

        return true;
    }

    public ItemStack getCustomHead(UUID url1) {
        ItemStack skull;

        if(pl.getIs18()){
            return new ItemStack(Objects.requireNonNull(Material.getMaterial("GOLD_INGOT")));
        }else{
            skull = new ItemStack(Material.PLAYER_HEAD);
        }
        SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();

        assert skullMeta != null;

        StringBuilder s_url = new StringBuilder();
        s_url.append("https://sessionserver.mojang.com/session/minecraft/profile/").append(url1.toString()); // Texture link.

        URL u;
        try {
            u = new URL(s_url.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        JsonObject json;
        try {
            json = getJson(u);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        String[] je = json.get("properties").toString().split(":");
        String id = "";
        int count = 0;
        for(String x : je){
            if((x.length() > 20 ) && (count == 0)){
                x = x.replaceAll("\\p{P}", "");
                id = x;
                count++;
            }
        }

        GameProfile gameProfile = new GameProfile(UUID.randomUUID(), null); // Create a GameProfile
        byte[] data = Base64.getEncoder().encode(String.format("{textures:{SKIN:{url:\"%s\"}}}", s_url.toString()).getBytes());

        // Set the texture property in the GameProfile.
        gameProfile.getProperties().put("textures", new Property("textures", new String(data)));
        Field field = null;

        try {
            field = skullMeta.getClass().getDeclaredField("profile"); // Get the field profile.
            field.setAccessible(true); // Set as accessible to modify.
            field.set(skullMeta, gameProfile); // Set in the skullMeta the modified GameProfile that we created.
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(pl.getIs18()){;
            skullMeta.setOwner(url1.toString());
        }else{
            skullMeta.setOwningPlayer(Bukkit.getOfflinePlayer(url1));
        }

        skull.setItemMeta(skullMeta);
        return skull;
    }

    public static JsonObject getJson(URL url) throws IOException {
        String json = IOUtils.toString(url, Charset.forName("UTF-8"));
        return new Gson().fromJson(json, JsonObject.class);
    }
}

