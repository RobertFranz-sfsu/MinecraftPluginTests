package com.valiantrealms.zombiesmc.commands;

import com.valiantrealms.zombiesmc.PlayerProfile;
import com.valiantrealms.zombiesmc.ZombiesMC;
import com.valiantrealms.zombiesmc.util.ConfigUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@SuppressWarnings({"CallToPrintStackTrace"})
public class ZMC implements CommandExecutor {
    private final ZombiesMC plugin;
    private int id = 867530942;

    Long saveTime;
    public ZMC(ZombiesMC plugin){
        this.plugin = plugin;
        this.setSaveTime();
        this.startSaveTask();
    }

    public void startSaveTask(){
        id = plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
            plugin.savePlayerData();
        }, 0L, saveTime);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        ConfigUtil con = plugin.getSkillSettings();

        try{
            Player player = null;
            if(sender != Bukkit.getConsoleSender()){
                player = (Player) sender;
            }

            assert player != null;

            switch(args[0].toLowerCase()){
                case "reload": case "r": // Saves then reloads all configs and players
                    try{
                        plugin.getReload().ReloadAll();

                        // Restarting the save task to update timer if time has changed
                        if(!Objects.equals(plugin.getConfig().getLong("save-timer"), this.getSaveTime())){
                            Bukkit.getServer().getScheduler().cancelTask(id);
                            this.setSaveTime();
                            this.startSaveTask();
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                        sender.sendMessage("Something went wrong. Please check the console.");
                        Bukkit.getLogger().severe("Something went wrong trying to update player from config.");
                    }

                    break;

                case "save": // Saves all player data to config
                    try{
                        plugin.savePlayerData();
                    }catch (Exception e){
                        e.printStackTrace();
                        sender.sendMessage("Something went wrong. Please check the console.");
                        Bukkit.getLogger().severe("Something went wrong trying to update player from config.");
                    }
                    break;

                case "saveplayer": case "sp": // Save one player's skills to config
                    try{
                        if(args.length == 1){
                            plugin.savePlayerData(player.getUniqueId());
                        }else if(args.length == 2){
                            plugin.savePlayerData(Bukkit.getPlayer(args[1]).getUniqueId());
                        }else{
                            sender.sendMessage("Correct usage: /zmc saveplayer (optional)[username]");
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                        sender.sendMessage("Something went wrong. Please check the console.");
                        Bukkit.getLogger().severe("Something went wrong trying to update player from config.");
                    }
                    break;

                case "updateplayer": case "up": case "register":// Update player skills from config
                    try{
                        if(args.length == 1){
                            plugin.register(player.getUniqueId());
                        }else if(args.length == 2){
                            plugin.register(Bukkit.getPlayer(args[1]).getUniqueId());
                        }else{
                            sender.sendMessage("Correct usage: /zmc updateplayer (optional)[username]");
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                        sender.sendMessage("Something went wrong. Please check the console.");
                        Bukkit.getLogger().severe("Something went wrong trying to update player from config.");
                    }
                    break;

                case "setskill": case "ss":
                    // 1 - username, 2 - skill, 3 - numbers
                    if (args.length != 4) {
                        sender.sendMessage("Correct usage: /zmc setskill [Username] [skill] [skill level]");
                        break;
                    }

                    try{
                        boolean setSkill = plugin.getPlayers().get(Bukkit.getPlayer(args[1]).getUniqueId()).setSkillCommand(args[2], Double.parseDouble(args[3]));
                        if(setSkill){
                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&fSuccessfully updated &b" + args[1] + "'s &c" + args[2] + " &flevel to &c" + args[3] + "&f."));
                        }else{
                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cCouldn't update player skill. Check the input arguments and try again."));
                        }
                    }catch(Exception e){
                        e.printStackTrace();
                        sender.sendMessage("Something went wrong. Please check the console.");
                        Bukkit.getLogger().severe("Something went wrong trying to update player skill.");
                    }
                    break;

                /**
                 * TESTING SECTION
                 */
                case "test":
                  Bukkit.getLogger().info("husbandry.base-instant-adult-while-breeding: " + plugin.getSkillSettings().getConfig().getDouble("husbandry.base-instant-adult-while-breeding"));
                  break;

                case "t":
//                    plugin.getPlayers().get(testPlay.getUniqueId()).getSkills();
                    for(int i = 0; i < plugin.getPlayers().get(player.getUniqueId()).getSkills().length; i++){
                        sender.sendMessage(String.valueOf(plugin.getPlayers().get(player.getUniqueId()).getSkills()[i]));
                    }
                    break;
                case "e":
                    if(args.length == 1){
                        plugin.getPlayers().get(player.getUniqueId()).getSkills()[5] = 0;
                    }else{
                        plugin.getPlayers().get(player.getUniqueId()).getSkills()[5] = Integer.parseInt(args[1]);
                    }

                    break;
                case "wow":
                    ConcurrentHashMap<UUID, PlayerProfile> test = plugin.getPlayers();

                    test.forEach((key, value) -> Bukkit.getLogger().info(key + ""));
                    break;
            }
        }catch(Exception e){
            sender.sendMessage("Something went wrong, please check the console.");
            e.printStackTrace();
        }

        return true;
    }

    public void setSaveTime(){ this.saveTime = 20 * plugin.getConfig().getLong("save-timer"); }
    public long getSaveTime() { return this.saveTime; }
}
