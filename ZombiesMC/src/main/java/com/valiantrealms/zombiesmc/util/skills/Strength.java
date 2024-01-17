package com.valiantrealms.zombiesmc.util.skills;

import com.valiantrealms.zombiesmc.ZombiesMC;
import com.valiantrealms.zombiesmc.util.ConfigUtil;
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
        plugin.getPlayers().get(id).getSkills()[5] += plugin.getSkillSettings().getConfig().getDouble("strength.increase-per-level");
        plugin.getPlayers().get(id).setHealth();
        plugin.getPlayers().get(id).updateStrength();
    }

    public double meleeDamage(UUID id, double originalDamage){
        double melee = plugin.getPlayers().get(id).getMeleeDamage();
        double critChance = plugin.getPlayers().get(id).getMeleeCritChance();

        if(critChance > maxCritChance){
            critChance = maxCritChance;
        }

        double damage = originalDamage + (melee * originalDamage);

        double num = (Math.floor(rand.nextDouble() * 1000))/10;

        boolean isCrit = (num <= critChance);
        if(isCrit){ damage = damage * con.getConfig().getDouble("strength.crit-damage-multiplier"); }

        return damage;
    }

    public void setConfig() {
        con = plugin.getSkillSettings();
        maxCritChance = con.getConfig().getDouble("strength.max-crit-chance");
    }
}
