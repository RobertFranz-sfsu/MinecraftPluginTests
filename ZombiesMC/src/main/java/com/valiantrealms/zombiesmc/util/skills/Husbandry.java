package com.valiantrealms.zombiesmc.util.skills;

import com.valiantrealms.zombiesmc.ZombiesMC;
import com.valiantrealms.zombiesmc.util.ConfigUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityBreedEvent;
import org.bukkit.event.entity.EntityDeathEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class Husbandry{
    private final ZombiesMC plugin;
    private ConfigUtil con;
    private ConfigUtil values;
    private List<EntityType> vals;
    private final Random rand = new Random();
    private double maxInstantAdultChance;
    private double maxMultiBreedChance;

    public Husbandry(ZombiesMC plugin){
        this.plugin = plugin;
        this.setConfig();
    }

    public void setVals(){
        vals = new ArrayList<>();

        for(int i = 0; i < Objects.requireNonNull(values.getConfig().getList("animals")).size(); i++){
            vals.add(EntityType.valueOf(Objects.requireNonNull(values.getConfig().getList("animals")).get(i).toString()));
        }
    }

    public void setConfig() {
        con = plugin.getSkillSettings();
        values = plugin.getHusbandryValues();
        maxInstantAdultChance = con.getConfig().getDouble("husbandry.max-instant-adult-chance");
        maxMultiBreedChance = con.getConfig().getDouble("husbandry.max-multi-breeding-chance");
    }

    // TODO
    //  Add XP gain for breeding/killing animals
    public void onAnimalKill(EntityDeathEvent event){

    }

    public void breeding(EntityBreedEvent event){
        if((event.getBreeder() instanceof Player)){
            Ageable animal = (Ageable) event.getEntity();
            Player player = (Player) event.getBreeder();
            double num = (Math.floor(this.rand.nextDouble() * 1000))/10;
            double instantAdultChance = Math.min(this.maxInstantAdultChance, plugin.getPlayers().get(player.getUniqueId()).getInstantAdultChance());
            double multiBreedChance = Math.min(this.maxMultiBreedChance, plugin.getPlayers().get(player.getUniqueId()).getMultiBreedChance());

            //Instant adult
            if(this.vals.contains(animal.getType()) && (instantAdultChance <= num)){
                Bukkit.getLogger().info("SET ADULT");
                animal.setAdult();
            }

            // Multibreeding
            num = (Math.floor(this.rand.nextDouble() * 1000))/10;
            if(this.vals.contains(animal.getType()) && (multiBreedChance <= num)){
                Bukkit.getLogger().info("MULTIBREED");
                Location loc = event.getEntity().getLocation();

                player.getWorld().spawnEntity(loc, animal.getType());
            }
        }
    }

    public List<EntityType> getVals(){ return this.vals; }
}
