package com.valiantrealms.zombiesmc.util.skills;

import com.valiantrealms.zombiesmc.ZombiesMC;
import com.valiantrealms.zombiesmc.util.ConfigUtil;

import java.util.Random;
import java.util.UUID;

public class Ranged {
    private final ZombiesMC plugin;
    private ConfigUtil con;
    private final Random rand = new Random();
    private double maxCritChance;

    public Ranged(ZombiesMC plugin){
        this.plugin = plugin;
        this.setConfig();
    }

    public void levelUp(UUID id){
        plugin.getPlayers().get(id).getSkills()[7] += plugin.getSkillSettings().getConfig().getDouble("ranged.increase-per-level");
        plugin.getPlayers().get(id).updateRanged();
    }

    public double rangedDamage(UUID id, double originalDamage){
        double ranged = plugin.getPlayers().get(id).getRangedDamage();
        double critChance = plugin.getPlayers().get(id).getRangedCritChance();

        if(critChance > maxCritChance){
            critChance = maxCritChance;
        }

        double damage = originalDamage + (ranged * originalDamage);

        double num = (Math.floor(rand.nextDouble() * 1000))/10;

        boolean isCrit = (num <= critChance);
        if(isCrit){ damage = damage * con.getConfig().getDouble("ranged.crit-damage-multiplier"); }

        return damage;
    }

    public void setConfig() {
        con = plugin.getSkillSettings();
        maxCritChance = con.getConfig().getDouble("ranged.max-crit-chance");
    }
}
