package com.valiantrealms.zombiesmc.commands;

import com.valiantrealms.zombiesmc.PlayerProfile;
import com.valiantrealms.zombiesmc.ZombiesMC;
import com.valiantrealms.zombiesmc.util.ConfigUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ZMC implements CommandExecutor {
    private ZombiesMC plugin;
    int id = 867530942;

    Long saveTime;
    public ZMC(ZombiesMC plugin){
        this.plugin = plugin;
        this.setSaveTime();
        this.startSaveTask();
    }

    public void startSaveTask(){
        id = plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
            Bukkit.getLogger().info("Attempting to save all player data...");

            ConcurrentHashMap<UUID, PlayerProfile> saving = plugin.getPlayers();

            if(!saving.isEmpty()){
                saving.forEach((key, value) -> value.save(key));
                Bukkit.getLogger().info("Done saving all player data!");
            }else{
                Bukkit.getLogger().info("Nothing to save!");
            }
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
                case "reload": case "r":
                    plugin.getReload().ReloadAll();
                    this.setSaveTime();
                    Bukkit.getServer().getScheduler().cancelTask(id);
                    this.startSaveTask();
                    break;

                /**
                 * TESTING SECTION
                 */
                case "test":
//                    plugin.getPlayers().get(testPlay.getUniqueId()).getSkills();
                    plugin.getPlayers().get(player.getUniqueId()).setSkill(2, 42);
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
}
