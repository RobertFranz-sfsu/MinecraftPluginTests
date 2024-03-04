package com.valiantrealms.zombiesmc.util.skills;

import com.valiantrealms.zombiesmc.ZombiesMC;
import com.valiantrealms.zombiesmc.util.ConfigUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class Farming {
    private final ZombiesMC plugin;
    private final Random rand = new Random();
    private ConfigUtil con;
    private ConfigUtil values;
    private double maxMultiHarvestChance;
    private double maxAutoGrow;
    private List<Block> crops;


    public Farming(ZombiesMC plugin){
        this.plugin = plugin;
        this.setConfig();
    }

    public void harvestCrops(BlockBreakEvent event){
        Player player = event.getPlayer();
        Ageable block = (Ageable) event.getBlock();

        // Multidrop
        double multiHarvest = plugin.getPlayers().get(player.getUniqueId()).getFarmingMultiHarvestChance();
        int maxDrops = plugin.getSkillSettings().getConfig().getInt("farming.multi-harvest.max-items");
        double num = (Math.floor(this.rand.nextDouble() * 1000))/10;

        Bukkit.getLogger().info("multiharvest: " + multiHarvest);
        Bukkit.getLogger().info("maxDrops: " + maxDrops);
        Bukkit.getLogger().info("num: " + num);

        if(multiHarvest <= num){
            int number = this.rand.nextInt(maxDrops+1);
            Bukkit.getLogger().info("number: " + number);

            if(number < 1){ number = 1; }
            int counter = 0;

            while(counter < number){
                player.getWorld().dropItem(player.getLocation(), new ItemStack(Objects.requireNonNull(Material.getMaterial(block.getName()))));
                counter++;
            }
        }
    }

    public void planting(BlockPlaceEvent event){
        // Auto grow
        Player player = event.getPlayer();

        double num = (Math.floor(this.rand.nextDouble() * 1000))/10;
        double autoGrow = plugin.getPlayers().get(player.getUniqueId()).getFarmingAutoGrow();
        Bukkit.getLogger().info(event.getBlockPlaced().toString());

        if((autoGrow <= num)){
            ((Ageable)event.getBlockPlaced()).setAdult();
        }
    }

    public void setVals(){
        // Getting valid crops
        crops = new ArrayList<>();

        for(int i = 0; i < Objects.requireNonNull(values.getConfig().getList("crops")).size(); i++){
//            crops.add();
        }
    }

    public void levelUp(UUID id){
        plugin.getPlayers().get(id).getSkills()[1] += plugin.getSkillSettings().getConfig().getDouble("farming.points-given-per-level-up");
    }

    public List<Block> getCrops() { return this.crops; }
    public void setConfig() {
        con = plugin.getSkillSettings();
        values = plugin.getFarmingValues();

        this.maxMultiHarvestChance = con.getConfig().getDouble("farming.multi-harvest.max-chance");
        this.maxAutoGrow = con.getConfig().getDouble("farming.auto-bone-meal.max-chance");
    }
}
