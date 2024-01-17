package com.valiantrealms.zombiesmc.commands.ZmcSub;

import com.valiantrealms.zombiesmc.ZombiesMC;
import org.bukkit.Bukkit;

public class Reload {
    private final ZombiesMC plugin;
    public Reload(ZombiesMC plugin){
        this.plugin = plugin;
    }

    public void ReloadAll(){
        try{
            plugin.reloadConfig();
//            Bukkit.getLogger().info("Reloaded config.yml.");

//            ConfigUtil c1 = new ConfigUtil(plugin, "BlockValues.yml");
//            c1.save();
//            Bukkit.getLogger().info("Saved BlockListener.yml...");

//            ConfigUtil c2 = new ConfigUtil(plugin, "SkillSettings.yml");
//            c2.save();
//            plugin.setSkillSettings();
//            Bukkit.getLogger().info("Saved SkillSettings.yml...");
//
//            ConfigUtil c3 = new ConfigUtil(plugin, System.getProperty("file.separator") + "PlayerInfo" + System.getProperty("file.separator") + "SkillSettings.yml");
//            c3.save();
//            plugin.setPlayerSettings();
            plugin.saveConfigs();

            plugin.getPlayers().forEach((key, value) -> value.reload());
            Bukkit.getLogger().info("Saved player files...");

            // Setting config files again
            plugin.getStrength().setConfig();
        }catch(Exception e){
            Bukkit.getLogger().severe("Something went wrong trying to reload the configs," +
                    " please check the console.");
            e.printStackTrace();
        }
    }
}
