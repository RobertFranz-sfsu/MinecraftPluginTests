package mctest.minecraft_test.commands;

import mctest.minecraft_test.Minecraft_Test;
import mctest.minecraft_test.commands.InfectedSubCommands.*;
import mctest.minecraft_test.roles.GamesList;
import mctest.minecraft_test.roles.PlayerRoles;
import mctest.minecraft_test.roles.SurvivalPlayer;
import mctest.minecraft_test.util.ConfigUtil;
import mctest.minecraft_test.util.InventoryUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.List;
import java.util.Map;
import java.util.Objects;


@SuppressWarnings({"FieldMayBeFinal", "CallToPrintStackTrace", "NullableProblems"})
public class Infected implements CommandExecutor, Listener {
    private Minecraft_Test plugin;
    private GamesList g;
    Start start;
    End end;
    ListLobbies list;
    ListWorlds listWorlds;
    GamesListCommand glc;
    PlayerRoles roles = new PlayerRoles(plugin);

    public Infected(Minecraft_Test plugin, GamesList g){
        Bukkit.getPluginManager().registerEvents(this, plugin);
        this.plugin = plugin;
        this.g = g;

        this.setSubCommands();
    }
    public void setSubCommands(){
        this.start = plugin.getStart();
        this.end = plugin.getEnd();
        this.list = plugin.getListLobbies();
        this.listWorlds = plugin.getListWorlds();
        this.glc = plugin.getGamesListCommand();
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!event.getView().getTitle().equals("Infection Games Available")) {
            return;
        }

        try {
            Player player = (Player) event.getWhoClicked();
            SurvivalPlayer game = g.getGameMap().get(Objects.requireNonNull(Objects.requireNonNull(event.getCurrentItem()).getItemMeta()).getDisplayName());
            if (event.getCurrentItem() != null) {
                if (event.getSlot() < 18) {
                    event.setCancelled(true);
                } else {
                    if (game.getPlaying()) {
                        player.sendMessage("Game already in session");
                    }
                    else if (game.getStatusMap().containsKey(player.getUniqueId())) {
                        player.sendMessage("You're already in the game");
                    } else if (plugin.getIsPlayingSet().contains(player.getUniqueId())) {
                        player.sendMessage("You're already in an active game");
                    } else {
                        String current = player.getWorld().getName();
                        player.teleport(game.getDefaultSpawn(event.getCurrentItem().getItemMeta().getDisplayName()));
                        roles.setUnassigned(player, game);
                        game.addPreviousWorld(player.getUniqueId(), current);
                        player.closeInventory();

                        event.setCancelled(true);
                    }
                }
            }
        } catch (Exception e) {
            //Bukkit.getLogger().info("Clicked on empty slot");
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(args.length != 0){
            Player player = (Player) sender;
            String playerWorld = player.getWorld().getName();

            switch (args[0].toLowerCase()){
                case "start":
                    start.startGame(player);
                    break;

                case "end":
                    end.endGame(player);
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
                                        ConfigUtil infConfig = new ConfigUtil(plugin, "Infected.yml");
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
                                        ConfigUtil surConfig = new ConfigUtil(plugin, "Survivor.yml");
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

                                        if(plugin.getConfig().get(defPath) != null && args.length < 3){
                                            sender.sendMessage("This world already has a default spawn, please choose another world or choose to " +
                                                    "overwrite!");
                                            break;
                                        }else if(plugin.getConfig().get(defPath) != null && !Objects.equals(args[2].toLowerCase(), "overwrite")){
                                            sender.sendMessage("This world already has a default spawn, please choose another world or choose to " +
                                                    "overwrite!");
                                            break;
                                        }

                                        plugin.getConfig().set(defPath, "");

                                        plugin.getConfig().set(defPath + ".x", player.getLocation().getX());
                                        plugin.getConfig().set(defPath + ".y", player.getLocation().getY());
                                        plugin.getConfig().set(defPath + ".z", player.getLocation().getZ());
                                        plugin.getConfig().set(defPath + ".pitch", player.getLocation().getPitch());
                                        plugin.getConfig().set(defPath + ".yaw", player.getLocation().getYaw());

                                        plugin.saveConfig();
                                        plugin.reloadConfig();

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
                                        c = new ConfigUtil(plugin, "Infected.yml");
                                        color = "&c";
                                        type = "infected";
                                        break;
                                    case "survivor":
                                    case "s":
                                        c = new ConfigUtil(plugin, "Survivor.yml");
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

                                        if (plugin.getConfig().get(path) != null) {
                                            plugin.getConfig().set(path, null);
                                            plugin.saveConfig();

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
                                    roles.setUnassigned(player, entry.getValue());
                                    //entry.getValue().setUnassigned(player);
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
                                    roles.setUnassigned(player, g.getGameMap().get(args[1]));
                                    //g.getGameMap().get(args[1]).setUnassigned(player);
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
                            List<String> val = plugin.getConfig().getStringList("lobby-worlds");
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
                                plugin.getConfig().set("lobby-worlds", val);
                                plugin.saveConfig();
                                plugin.reloadConfig();

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
                                List<String> worlds = plugin.getConfig().getStringList("lobby-worlds");

                                if(worlds.contains(args[1])){
                                    worlds.remove(args[1]);
                                    plugin.getConfig().set("lobby-worlds", worlds);
                                    plugin.saveConfig();
                                    plugin.reloadConfig();

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
                    list.list(player);
                    break;

                case "setworld": case "sw":
                    if(sender.hasPermission("infected.infected.setworld") || sender.hasPermission("infected.*") || sender.hasPermission("infected.infected.*")){
                        try{
                            List<String> vals = plugin.getConfig().getStringList("allowed-worlds");
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
                                plugin.getConfig().set("allowed-worlds", vals);
                                plugin.saveConfig();
                                plugin.reloadConfig();

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
                                List<String> worlds = plugin.getConfig().getStringList("allowed-worlds");

                                if(worlds.contains(args[1])){
                                    worlds.remove(args[1]);
                                    g.removeWorld(args[1]);
                                    plugin.getConfig().set("allowed-worlds", worlds);
                                    plugin.saveConfig();
                                    plugin.reloadConfig();

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
                    listWorlds.List(player);
                    break;

                case "setrole": case "sr": case "role":
                    if(sender.hasPermission("infected.infected.setrole") || sender.hasPermission("infected.*") || sender.hasPermission("infected.infected.*")){
                        try{
                            SurvivalPlayer game = g.getGameMap().get(player.getWorld().getName());
                            InventoryUtil invUtil = game.getInvUtil();
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
                                            roles.setNotPlaying(Objects.requireNonNull(Bukkit.getPlayer(args[1])), game, invUtil);
                                            break;
                                        default:
                                            sender.sendMessage("Must specify role:");
                                            sender.sendMessage("infected (i), survivor (s), notplaying (np)");
                                            break;
                                    }

                                    if(role != null){
                                        roles.removeEffects(Objects.requireNonNull(Bukkit.getPlayer(args[1])));
                                        if(g.getGameMap().get(player.getWorld().getName()).getPlaying()){
                                            g.getGameMap().get(player.getWorld().getName()).getStatusMap().put(Objects.requireNonNull(Bukkit.getPlayer(args[1])).getUniqueId(), role);
                                            roles.setRole(Objects.requireNonNull(Bukkit.getPlayer(args[1])), game);
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
                    glc.gamesList(player);
                    break;

                case "menu": case "m":
                    Bukkit.dispatchCommand(sender, "infectedmenu");
                    break;

                case "test":
                    Integer[] nArr = plugin.getStatsMap().get(Objects.requireNonNull(((Player) sender).getUniqueId()));
                    for(Integer x : nArr){
                        sender.sendMessage(nArr[x] + "");
                    }
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
}