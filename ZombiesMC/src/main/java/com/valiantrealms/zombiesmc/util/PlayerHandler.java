package com.valiantrealms.zombiesmc.util;

import com.valiantrealms.zombiesmc.ZombiesMC;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class PlayerHandler implements Listener {
    ZombiesMC plugin;

    public PlayerHandler(ZombiesMC plugin){
        Bukkit.getPluginManager().registerEvents(this, plugin);
        this.plugin = plugin;
    }

    @EventHandler
    public void OnPlayerJoin(PlayerJoinEvent event){
        Player player = event.getPlayer();
        String path = System.getProperty("file.separator") + "PlayerInfo" + System.getProperty("file.separator") + player.getUniqueId() + ".yml";
        File dir = new File(plugin.getDataFolder().getPath() + System.getProperty("file.separator") + "PlayerInfo" + System.getProperty("file.separator"));
        File[] dirList = dir.listFiles();
        assert dirList != null;

        ArrayList<String> fileNames = new ArrayList<>();

        for (File f : dirList) {
            fileNames.add(f.getName());
        }

        if (!fileNames.contains(player.getUniqueId() + ".yml")) {
            try {
                Bukkit.getLogger().info("Creating new profile");
                File p = new File(plugin.getDataFolder().getPath() + System.getProperty("file.separator") + "PlayerInfo" + System.getProperty("file.separator") + player.getUniqueId() + ".yml");
                p.createNewFile();

                ConfigUtil con = new ConfigUtil(plugin, path);
                ConfigUtil con1 = new ConfigUtil(plugin, "PlayerInfo" + System.getProperty("file.separator") + "PlayerSettings.yml");

                con.getConfig().set("username", player.getName());
                con.getConfig().set("health", con1.getConfig().getInt("starting-health"));
                con.getConfig().set("stamina", con1.getConfig().getInt("starting-stamina"));
                con.getConfig().set("skills.lockpicking", 0);
                con.getConfig().set("skills.farming", 0);
                con.getConfig().set("skills.stamina", 0);
                con.getConfig().set("skills.salvage", 0);
                con.getConfig().set("skills.husbandry", 0);
                con.getConfig().set("skills.strength", 0);
                con.getConfig().set("skills.cooking", 0);
                con.getConfig().set("skills.ranged", 0);
                con.getConfig().set("skills.melee", 0);
                con.getConfig().set("custom-perms", null);
                con.save();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
}
}
