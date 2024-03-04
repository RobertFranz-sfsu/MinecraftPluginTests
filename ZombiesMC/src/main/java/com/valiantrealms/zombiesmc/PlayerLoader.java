package com.valiantrealms.zombiesmc;

import org.bukkit.Bukkit;

import java.util.UUID;

public class PlayerLoader {
    private final ZombiesMC plugin;
    public PlayerLoader(ZombiesMC plugin){ this.plugin = plugin; }

    public PlayerProfile loadPlayer(UUID id){
        PlayerProfile profile = new PlayerProfile(plugin);
        profile.register(id);

        return profile;
    }

    public void setPlayer(UUID id){
        // Melee
        plugin.getPlayers().get(id).setMeleeDamage();
        plugin.getPlayers().get(id).setMeleeCritChance();

        // Ranged
        plugin.getPlayers().get(id).setRangedDamage();
        plugin.getPlayers().get(id).setRangedCritChance();

        // Husbandry
        Bukkit.getLogger().info("instant adult chance: " + plugin.getPlayers().get(id).getInstantAdultChance());
        plugin.getPlayers().get(id).setInstantAdultChance();
        Bukkit.getLogger().info("instant adult chance: " + plugin.getPlayers().get(id).getInstantAdultChance());

        plugin.getPlayers().get(id).setMultiBreedChance();
        plugin.getPlayers().get(id).setFarmingMultiHarvestChance();

        plugin.getExperience().addPlayer(id);
    }
}
