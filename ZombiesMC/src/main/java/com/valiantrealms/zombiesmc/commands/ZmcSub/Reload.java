package com.valiantrealms.zombiesmc.commands.ZmcSub;

import com.valiantrealms.zombiesmc.ZombiesMC;
import com.valiantrealms.zombiesmc.util.ConfigUtil;
import com.valiantrealms.zombiesmc.util.skills.Unarmed;
import org.bukkit.Bukkit;

public class Reload {
    private ZombiesMC plugin;
    public Reload(ZombiesMC plugin){
        this.plugin = plugin;
    }

    public void ReloadAll(){
        try{
            plugin.reloadConfig();
            Bukkit.getLogger().info("Reloaded config.yml.");

            ConfigUtil c1 = new ConfigUtil(plugin, "BlockListener.yml");
            c1.save();
            Bukkit.getLogger().info("Saved BlockListener.yml...");

            ConfigUtil c2 = new ConfigUtil(plugin, "SkillSettings.yml");
            c2.save();
            plugin.setSkillSettings();
            Bukkit.getLogger().info("Saved SkillSettings.yml...");

            plugin.getPlayers().forEach((key, value) -> value.reload());
            Bukkit.getLogger().info("Saved player files...");

            // Setting config files again
            plugin.getUnarmed().setConfig();
        }catch(Exception e){
            Bukkit.getLogger().severe("Something went wrong trying to reload the configs," +
                    " please check the console.");
            e.printStackTrace();
        }
    }
}
