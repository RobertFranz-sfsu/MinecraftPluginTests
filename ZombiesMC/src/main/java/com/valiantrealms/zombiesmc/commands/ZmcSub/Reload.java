package com.valiantrealms.zombiesmc.commands.ZmcSub;

import com.valiantrealms.zombiesmc.ZombiesMC;
import com.valiantrealms.zombiesmc.util.ConfigUtil;
import org.bukkit.Bukkit;

public class Reload {
    private ZombiesMC plugin;
    public Reload(ZombiesMC plugin){
        this.plugin = plugin;
    }

    public void ReloadAll(){
        try{
            Bukkit.getLogger().info("Reloading config.yml...");
            plugin.reloadConfig();
            Bukkit.getLogger().info("Reloaded config.yml.");

            Bukkit.getLogger().info("Reloading BlockListener.yml...");
            ConfigUtil c1 = new ConfigUtil(plugin, "BlockListener.yml");
            c1.save();
            Bukkit.getLogger().info("Saved BlockListener.yml...");

            Bukkit.getLogger().info("Reloading SkillSettings.yml...");
            ConfigUtil c2 = new ConfigUtil(plugin, "SkillSettings.yml");
            c2.save();
            plugin.setSkillSettings();
            Bukkit.getLogger().info("Saved SkillSettings.yml...");

        }catch(Exception e){
            Bukkit.getLogger().severe("Something went wrong trying to reload the configs," +
                    " please check the console.");
            e.printStackTrace();
        }
    }
}
