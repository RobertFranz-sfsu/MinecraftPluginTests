package com.valiantrealms.zombiesmc.util;

import com.valiantrealms.zombiesmc.ZombiesMC;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

public class BlockListener implements Listener {
    ZombiesMC plugin;
    public BlockListener(ZombiesMC plugin){
        Bukkit.getPluginManager().registerEvents(this, plugin);
        this.plugin = plugin;
    }
}
