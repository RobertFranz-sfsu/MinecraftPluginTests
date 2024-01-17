package com.valiantrealms.zombiesmc.util;

import com.valiantrealms.zombiesmc.PlayerProfile;
import com.valiantrealms.zombiesmc.ZombiesMC;
import com.valiantrealms.zombiesmc.util.skills.Strength;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;

public class PlayerHandler implements Listener {
    private final ZombiesMC plugin;

    public PlayerHandler(ZombiesMC plugin){
        Bukkit.getPluginManager().registerEvents(this, plugin);
        this.plugin = plugin;
    }

    public int skillNumber(String input){
        /**
         * 0 lockpicking
         * 1 farming
         * 2 stamina (skill)
         * 3 salvage
         * 4 husbandry
         * 5 strength
         * 6 cooking
         * 7 ranged
         * 8 melee
         */

        switch(input.toLowerCase()){
            case "lockpicking":
                return 0;
            case "farming":
                return 1;
            case "stamina":
                return 2;
            case "salvage":
                return 3;
            case "husbandry":
                return 4;
            case "strength":
                return 5;
            case "cooking":
                return 6;
            case "ranged":
                return 7;
            case "melee":
                return 8;
            default:
                return -1;
        }
    }

    @EventHandler
    public void OnPlayerLogin(PlayerLoginEvent event){
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

                con.getConfig().set("melee-damage", 0.0);
                con.getConfig().set("ranged-damage", 0.0);
                con.getConfig().set("ranged-crit-chance", 0.0);
                con.getConfig().set("melee-crit-chance", 0.0);

                con.getConfig().set("skills.lockpicking", 0.0);
                con.getConfig().set("skills.farming", 0.0);
                con.getConfig().set("skills.stamina", 0.0);
                con.getConfig().set("skills.salvage", 0.0);
                con.getConfig().set("skills.husbandry", 0.0);
                con.getConfig().set("skills.strength", 0.0);
                con.getConfig().set("skills.cooking", 0.0);
                con.getConfig().set("skills.ranged", 0.0);
                con.getConfig().set("skills.melee", 0.0);
                con.save();

                plugin.getPlayers().put(player.getUniqueId(), plugin.getLoader().loadPlayer(player.getUniqueId()));

                plugin.getPlayers().get(player.getUniqueId()).setMeleeDamage();
                plugin.getPlayers().get(player.getUniqueId()).setMeleeCritChance();
                plugin.getPlayers().get(player.getUniqueId()).setRangedDamage();
                plugin.getPlayers().get(player.getUniqueId()).setRangedCritChance();
                plugin.getPlayers().get(player.getUniqueId()).reload();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }else{
            plugin.getPlayers().put(player.getUniqueId(), plugin.getLoader().loadPlayer(player.getUniqueId()));
        }

        Bukkit.getLogger().severe("Registered player: " + player.getUniqueId());
    }

    @EventHandler
    public void OnPlayerJoin(PlayerJoinEvent event){
        Player player = event.getPlayer();

        if(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue() != plugin.getPlayers().get(player.getUniqueId()).getHealth()){
            new BukkitRunnable(){
                @Override
                public void run(){
                    plugin.getPlayers().get(player.getUniqueId()).setHealth();
                }
            }.runTaskLater(plugin, 40);
        }

//        try{
//            if(Objects.equals(player.getInventory().getItemInMainHand(), null) && !player.isOp()){
//                plugin.getPlayers().get(player.getUniqueId()).setMainHandEmpty(true);
//            }
//        }catch(Exception e){
//            e.printStackTrace();
//        }

        // Check if main hand is empty on login
//        plugin.getPlayers().get(player.getUniqueId()).setMainHandEmpty(Objects.equals(player.getInventory().getItemInMainHand(), null));
    }

    @EventHandler
    public void OnPlayerQuit(PlayerQuitEvent event){
        UUID id = event.getPlayer().getUniqueId();

        plugin.getPlayers().get(id).unregister(id);
    }


    @EventHandler
    public void damageHandler(EntityDamageByEntityEvent event){
        if(event.getDamager() instanceof Player){
            Player player = (Player) event.getDamager();
            PlayerProfile profile = plugin.getPlayers().get(player.getUniqueId());

            if(event.getCause() != EntityDamageEvent.DamageCause.PROJECTILE){
                event.setDamage(plugin.getStrength().meleeDamage(player.getUniqueId(), event.getDamage()));
            }else if(Objects.equals(event.getCause(), EntityDamageEvent.DamageCause.PROJECTILE)){
                event.setDamage(plugin.getRanged().rangedDamage(player.getUniqueId(), event.getDamage()));
            }
        }
    }
}
