package com.valiantrealms.zombiesmc.util;

import com.valiantrealms.zombiesmc.PlayerProfile;
import com.valiantrealms.zombiesmc.ZombiesMC;
import com.valiantrealms.zombiesmc.util.skills.Strength;
import com.valiantrealms.zombiesmc.util.skills.Unarmed;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;

public class PlayerHandler implements Listener {
    ZombiesMC plugin;

    public PlayerHandler(ZombiesMC plugin){
        Bukkit.getPluginManager().registerEvents(this, plugin);
        this.plugin = plugin;
    }

    @EventHandler
    public void OnPlayerJoin(PlayerLoginEvent event){
        ConfigUtil check = new ConfigUtil(plugin, "config.yml");

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
                con.save();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        plugin.getPlayers().put(player.getUniqueId(), plugin.getLoader().loadPlayer(player.getUniqueId()));
        if(Objects.equals(player.getInventory().getItemInMainHand(), null)){
            plugin.getPlayers().get(player.getUniqueId()).setMainHandEmpty(true);
        }
        Bukkit.getLogger().severe("Registered player: " + player.getUniqueId());
    }

    @EventHandler
    public void OnPlayerQuit(PlayerQuitEvent event){
        UUID id = event.getPlayer().getUniqueId();

        plugin.getPlayers().get(id).unregister(id);
    }

    @EventHandler
    public void isHandEmpty(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();
        player.getInventory().getItem(event.getNewSlot());

//        if (Objects.equals(player.getInventory().getItem(event.getNewSlot()), null) && !plugin.getPlayers().get(player.getUniqueId()).isMainHandEmpty()) {
//            plugin.getPlayers().get(player.getUniqueId()).setMainHandEmpty(true);
//            player.sendMessage("if: " + plugin.getPlayers().get(player.getUniqueId()).isMainHandEmpty());
//        } else if (!Objects.equals(player.getInventory().getItem(event.getNewSlot()), null) && plugin.getPlayers().get(player.getUniqueId()).isMainHandEmpty()){
//            plugin.getPlayers().get(player.getUniqueId()).setMainHandEmpty(false);
//            player.sendMessage("if else: " + plugin.getPlayers().get(player.getUniqueId()).isMainHandEmpty());
//        }
        plugin.getPlayers().get(player.getUniqueId()).setMainHandEmpty(Objects.equals(player.getInventory().getItem(event.getNewSlot()), null) && !plugin.getPlayers().get(player.getUniqueId()).isMainHandEmpty());
    }

    @EventHandler
    public void damageHandler(EntityDamageByEntityEvent event){
        if(event.getDamager() instanceof Player){
            Player player = (Player) event.getDamager();

            PlayerProfile profile = plugin.getPlayers().get(player.getUniqueId());

            if(profile.isMainHandEmpty()){
                Unarmed unarmed = new Unarmed(plugin);
                LivingEntity ent = (LivingEntity) event.getEntity();
                Bukkit.getLogger().severe("DAMAGE BEFORE: " + event.getDamage());
                Bukkit.getLogger().severe("HEALTH BEFORE: " + ent.getHealth());
                event.setDamage(unarmed.unarmedDamage(player.getUniqueId(), event.getDamage()));
                Bukkit.getLogger().severe("DAMAGE AFTER: " + event.getDamage());
                Bukkit.getLogger().severe("HEALTH AFTER: " + ent.getHealth());

            }
        }
    }
}
