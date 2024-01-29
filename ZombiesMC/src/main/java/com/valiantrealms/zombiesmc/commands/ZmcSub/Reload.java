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
            plugin.saveConfigs();

            plugin.getPlayers().forEach((key, value) -> value.reload());
            Bukkit.getLogger().info("Saved player files...");

            // Setting config files again
            plugin.getStrength().setConfig();
            plugin.getRanged().setConfig();
            plugin.getStealth().setConfig();

        }catch(Exception e){
            Bukkit.getLogger().severe("Something went wrong trying to reload the configs," +
                    " please check the console.");
            e.printStackTrace();
        }
    }
}
