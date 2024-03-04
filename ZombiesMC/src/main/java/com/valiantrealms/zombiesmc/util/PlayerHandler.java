package com.valiantrealms.zombiesmc.util;

import com.valiantrealms.zombiesmc.ZombiesMC;
import jdk.internal.net.http.common.Pair;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
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

    /*
     * 0 lockpicking
     * 1 farming
     * 2 stamina (skill)
     * 3 salvage
     * 4 husbandry
     * 5 strength
     * 6 cooking
     * 7 ranged
     * 8 stealth
     */

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @EventHandler
    public void OnPlayerLogin(PlayerLoginEvent event){
        Player player = event.getPlayer();
        UUID id = event.getPlayer().getUniqueId();
        String path = System.getProperty("file.separator") + "PlayerInfo" + System.getProperty("file.separator") + Objects.requireNonNull(player.getUniqueId()) + ".yml";
        File dir = new File(plugin.getDataFolder().getPath() + System.getProperty("file.separator") + "PlayerInfo" + System.getProperty("file.separator"));
        File[] dirList = dir.listFiles();
        assert dirList != null;

        ArrayList<String> fileNames = new ArrayList<>();

        for (File f : dirList) {
            fileNames.add(f.getName());
        }

        if (!fileNames.contains(id + ".yml")) {
            try {
                Bukkit.getLogger().info("Creating new profile");
                File p = new File(plugin.getDataFolder().getPath() + System.getProperty("file.separator") + "PlayerInfo" + System.getProperty("file.separator") + id + ".yml");
                p.createNewFile();

                ConfigUtil con = new ConfigUtil(plugin, path);
                ConfigUtil con1 = new ConfigUtil(plugin, "PlayerInfo" + System.getProperty("file.separator") + "DefaultPlayerSettings.yml");
//                ConfigUtil config = plugin.getSkillSettings();

                // TODO
                //  set these to base values from configs!!!
                con.getConfig().set("username", player.getName());
                con.getConfig().set("health", con1.getConfig().getInt("starting-health"));
                con.getConfig().set("stamina", con1.getConfig().getInt("starting-stamina"));

                con.getConfig().set("melee.damage", 0.0);
                con.getConfig().set("melee.crit-chance", 0.0);

                con.getConfig().set("ranged.damage", 0.0);
                con.getConfig().set("ranged.crit-chance", 0.0);

                con.getConfig().set("husbandry.instant-adult-chance", 0.0);
                con.getConfig().set("husbandry.multi-breeding-chance", 0.0);
                con.getConfig().set("husbandry.multi-drop-chance", 0.0);

                con.getConfig().set("farming.multi-harvest-chance", 0.0);
                con.getConfig().set("farming.auto-grow-chance", 0.0);

                con.getConfig().set("skills.lockpicking", 0.0);
                con.getConfig().set("skills.farming", 0.0);
                con.getConfig().set("skills.stamina", 0.0);
                con.getConfig().set("skills.salvage", 0.0);
                con.getConfig().set("skills.husbandry", 0.0);
                con.getConfig().set("skills.strength", 0.0);
                con.getConfig().set("skills.cooking", 0.0);
                con.getConfig().set("skills.ranged", 0.0);
                con.getConfig().set("skills.stealth", 0.0);

                con.getConfig().set("experience.lockpicking", 0.0);
                con.getConfig().set("experience.farming", 0.0);
                con.getConfig().set("experience.stamina", 0.0);
                con.getConfig().set("experience.salvage", 0.0);
                con.getConfig().set("experience.husbandry", 0.0);
                con.getConfig().set("experience.strength", 0.0);
                con.getConfig().set("experience.cooking", 0.0);
                con.getConfig().set("experience.ranged", 0.0);
                con.getConfig().set("experience.stealth", 0.0);

                con.getConfig().set("experience-needed.lockpicking", 0.0);
                con.getConfig().set("experience-needed.farming", 0.0);
                con.getConfig().set("experience-needed.stamina", 0.0);
                con.getConfig().set("experience-needed.salvage", 0.0);
                con.getConfig().set("experience-needed.husbandry", 0.0);
                con.getConfig().set("experience-needed.strength", 0.0);
                con.getConfig().set("experience-needed.cooking", 0.0);
                con.getConfig().set("experience-needed.ranged", 0.0);
                con.getConfig().set("experience-needed.stealth", 0.0);

                con.getConfig().set("saved-cooking-devices", null);
                con.save();

                plugin.getPlayers().put(id, plugin.getLoader().loadPlayer(id));
                plugin.getLoader().setPlayer(id);
//                plugin.getExperience().getPlayerList().put(id, new Pair<>(plugin.getPlayers().get(id).getSkillXP(), plugin.getPlayers().get(id).getSkillXPNeeded()));
//
//                // Melee
//                plugin.getPlayers().get(id).setMeleeDamage();
//                plugin.getPlayers().get(id).setMeleeCritChance();
//
//                // Ranged
//                plugin.getPlayers().get(id).setRangedDamage();
//                plugin.getPlayers().get(id).setRangedCritChance();
//
//                // Husbandry
//                Bukkit.getLogger().info("instant adult chance: " + plugin.getPlayers().get(id).getInstantAdultChance());
//                plugin.getPlayers().get(id).setInstantAdultChance();
//                Bukkit.getLogger().info("instant adult chance: " + plugin.getPlayers().get(id).getInstantAdultChance());
//
//                plugin.getPlayers().get(id).setMultiBreedChance();
//                plugin.getPlayers().get(id).setFarmingMultiHarvestChance();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }else{
            plugin.getPlayers().put(id, plugin.getLoader().loadPlayer(id));
            plugin.getExperience().getPlayerList().put(id, new Pair<>(plugin.getPlayers().get(id).getSkillXP(), plugin.getPlayers().get(id).getSkillXPNeeded()));
        }

        Bukkit.getLogger().severe("Registered player: " + player.getUniqueId());
    }

    @EventHandler
    public void OnPlayerJoin(PlayerJoinEvent event){
        new BukkitRunnable(){
            @Override
            public void run(){
            Player player = event.getPlayer();

            if(Objects.requireNonNull(player.getAttribute(Attribute.GENERIC_MAX_HEALTH)).getBaseValue() != plugin.getPlayers().get(player.getUniqueId()).getHealth()){
                plugin.getPlayers().get(player.getUniqueId()).setHealth();
            }
            }
        }.runTaskLater(plugin, 40);
    }

    @EventHandler
    public void OnPlayerQuit(PlayerQuitEvent event){
        UUID id = event.getPlayer().getUniqueId();

        plugin.getPlayers().get(id).unregister();
    }

    @EventHandler
    public void damageHandler(EntityDamageByEntityEvent event){
        if(event.getDamager() instanceof Player){
            Player player = (Player) event.getDamager();

            if(!event.getCause().equals(EntityDamageEvent.DamageCause.PROJECTILE)){
                event.setDamage(plugin.getStrength().meleeDamage(player.getUniqueId(), event.getDamage()));
            }
        }else if(event.getCause().equals(EntityDamageEvent.DamageCause.PROJECTILE) && (((Projectile) event.getDamager()).getShooter() instanceof Player)){
            Player player = (Player) ((Projectile) event.getDamager()).getShooter();

            event.setDamage(plugin.getRanged().rangedDamage(player.getUniqueId(), event.getDamage()));
        }
    }

    // TODO
    //  ADD XP GAIN STUFF LOL

    @EventHandler
    public void breedingHandler(EntityBreedEvent event){
        Ageable animal = (Ageable) event.getEntity();
        if(plugin.getHusbandry().getAnimals().contains(animal.getType()) && (event.getBreeder() instanceof Player));{
            plugin.getHusbandry().breeding(event);
        }
    }

    @EventHandler
    public void killHandler(EntityDeathEvent event){
        if(event.getEntity().getKiller() == null){ // If killer isn't a player

        }else{
            // Husbandry
            if((event.getEntity() instanceof Animals) || plugin.getHusbandry().getAnimals().contains(event.getEntityType())){
                Bukkit.getLogger().info("ANIMAL KILL");

                plugin.getHusbandry().onAnimalKill(event);
            }
        }
    }

    @EventHandler
    public void blockBreakHandler(BlockBreakEvent event){
        // Husbandry
        if(!Objects.equals(plugin.getFarming().getCrops(), null) && !plugin.getFarming().getCrops().isEmpty() && plugin.getFarming().getCrops().contains(event.getBlock())){
            plugin.getFarming().harvestCrops(event);
        }
    }

    @EventHandler
    public void animalHarvestHandler(EntityDropItemEvent event){
        if(event.getEntityType().equals(EntityType.valueOf("SHEEP"))){
            plugin.getHusbandry().shearSheep(event);
        }
    }

    @EventHandler
    public void blockPlaceHandler(BlockPlaceEvent event){
//        Bukkit.getLogger().info("CAN BUILD: " + event.canBuild());
//        Bukkit.getLogger().info("Placed: " + event.getBlockPlaced());
        if((event.getBlockPlaced() instanceof Ageable) && !Objects.equals(plugin.getFarming().getCrops(), null) && !plugin.getFarming().getCrops().isEmpty() && plugin.getFarming().getCrops().contains(event.getBlock())){
            plugin.getFarming().planting(event);
        }
    }

    public int skillNumber(String input){
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
            case "stealth":
                return 8;
            default:
                return -1;
        }
    }
}
