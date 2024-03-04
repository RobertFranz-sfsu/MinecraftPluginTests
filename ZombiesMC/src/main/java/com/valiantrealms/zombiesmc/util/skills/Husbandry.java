package com.valiantrealms.zombiesmc.util.skills;

import com.valiantrealms.zombiesmc.ZombiesMC;
import com.valiantrealms.zombiesmc.util.ConfigUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityBreedEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityDropItemEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class Husbandry{
    private final ZombiesMC plugin;
    private ConfigUtil con;
    private ConfigUtil values;
    private List<EntityType> animals;
    private final Random rand = new Random();
    private double maxInstantAdultChance;
    private double maxMultiBreedChance;
    private double maxMultiDropChance;
    private int maxMultiDrops;

    public Husbandry(ZombiesMC plugin){
        this.plugin = plugin;
        this.setConfig();
    }

    public void setConfig() {
        con = plugin.getSkillSettings();
        values = plugin.getHusbandryValues();
        maxInstantAdultChance = con.getConfig().getDouble("husbandry.max-instant-adult-chance");
        maxMultiBreedChance = con.getConfig().getDouble("husbandry.max-multi-breeding-chance");

        maxMultiDropChance = con.getConfig().getDouble("husbandry.multi-drop.max-chance");
        maxMultiDrops = con.getConfig().getInt("husbandry.multi-drop.max-drops");
        this.setVals();
    }

    public void setVals(){
        // Getting valid animals
        animals = new ArrayList<>();

        for(int i = 0; i < Objects.requireNonNull(values.getConfig().getList("animals")).size(); i++){
            animals.add(EntityType.valueOf(Objects.requireNonNull(values.getConfig().getList("animals")).get(i).toString()));
        }
    }

    // TODO
    //  Add XP gain for everything
    public void onAnimalKill(EntityDeathEvent event){
        Location loc = event.getEntity().getLocation();
        List<ItemStack> item = event.getDrops();
        Player player = event.getEntity().getKiller();

        double num = (Math.floor(this.rand.nextDouble() * 1000))/10;
        double multiDropChance = Math.min(this.maxMultiDropChance, plugin.getPlayers().get(player.getUniqueId()).getHusbandryMultiDropChance());

        if(multiDropChance <= num){
            int number = this.rand.nextInt(3);
            if(number < 1) { number = 1; }
            else if(number > maxMultiDrops){ number = maxMultiDrops; }
            int i = 0;

            while(i < number){
                for(int j = 0; j < item.size(); j++){
                    event.getEntity().getWorld().dropItem(loc, item.get(j));
                }
                i++;
            }
        }
    }

    public void breeding(EntityBreedEvent event){
        Ageable animal = (Ageable) event.getEntity();
        Player player = (Player) event.getBreeder();
        double num = (Math.floor(this.rand.nextDouble() * 1000))/10;
        double instantAdultChance = Math.min(this.maxInstantAdultChance, plugin.getPlayers().get(player.getUniqueId()).getInstantAdultChance());
        double multiBreedChance = Math.min(this.maxMultiBreedChance, plugin.getPlayers().get(player.getUniqueId()).getMultiBreedChance());

        //Instant adult
        if(instantAdultChance <= num){
            Bukkit.getLogger().info("SET ADULT");
            animal.setAdult();
        }

        // Multibreeding
        num = (Math.floor(this.rand.nextDouble() * 1000))/10;
        if(multiBreedChance <= num){
            Bukkit.getLogger().info("MULTIBREED");
            Location loc = event.getEntity().getLocation();

            player.getWorld().spawnEntity(loc, animal.getType());
        }
    }

    public void shearSheep(EntityDropItemEvent event){
        Location loc = event.getItemDrop().getLocation();
        ItemStack item = event.getItemDrop().getItemStack();

        int number = this.rand.nextInt(3);
        if(number < 1) { number = 1; }
        int i = 0;

        while(i < number){
            event.getItemDrop().getWorld().dropItem(loc, item);
            i++;
        }
    }

    public List<EntityType> getAnimals(){ return this.animals; }
}
