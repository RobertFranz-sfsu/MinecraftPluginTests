package com.valiantrealms.zombiesmc.util.skills;

import com.valiantrealms.zombiesmc.ZombiesMC;
import com.valiantrealms.zombiesmc.util.ConfigUtil;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

import java.util.Random;
import java.util.UUID;

public class Strength implements Listener {
    private final ZombiesMC plugin;
    private ConfigUtil con;
    private Random rand = new Random();
    private double maxCritChance;

    public Strength(ZombiesMC plugin){
        this.plugin = plugin;
        this.setConfig();
    }

    public void levelUp(UUID id){
        plugin.getPlayers().get(id).getSkills()[5] += plugin.getSkillSettings().getConfig().getDouble("strength.points-given-per-level-up");
        plugin.getPlayers().get(id).setHealth();
        plugin.getPlayers().get(id).updateStrength();
    }

    public double meleeDamage(UUID id, double originalDamage){
        double melee = plugin.getPlayers().get(id).getMeleeDamage();
        double critChance = Math.min(maxCritChance, plugin.getPlayers().get(id).getMeleeCritChance());

        if(critChance > maxCritChance){
            Bukkit.getLogger().info("MELEE CRIT");
            critChance = maxCritChance;
        }

        double damage = originalDamage + (melee * originalDamage);

        double num = (Math.floor(rand.nextDouble() * 1000))/10;

        boolean isCrit = (num <= critChance);
        if(isCrit){ damage = damage * con.getConfig().getDouble("strength.crit.multiplier"); }

        return damage;
    }

    public void setConfig() {
        con = plugin.getSkillSettings();
        maxCritChance = con.getConfig().getDouble("strength.crit.max-chance");
    }
}
