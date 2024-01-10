package com.valiantrealms.zombiesmc.util.skills;

import com.valiantrealms.zombiesmc.PlayerProfile;
import com.valiantrealms.zombiesmc.ZombiesMC;
import com.valiantrealms.zombiesmc.util.ConfigUtil;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Random;
import java.util.UUID;

public class Unarmed implements Listener {
    private ZombiesMC plugin;
    ConfigUtil con;
    Random rand = new Random();

    public Unarmed(ZombiesMC plugin){
        this.plugin = plugin;
        this.setConfig();
    }

    public double unarmedDamage(UUID id, double originalDamage){
        double unarmed = (plugin.getPlayers().get(id).getSkills()[9]) * con.getConfig().getDouble("strength.unarmed.damage-increase");
        double critChance = unarmed * con.getConfig().getDouble("strength.unarmed.crit-chance-increase");

        if(critChance > con.getConfig().getDouble("strength.unarmed.max-crit-chance")){
            critChance = con.getConfig().getDouble("strength.unarmed.max-crit-chance");
        }

        double damage = originalDamage + (unarmed * originalDamage);

        double num = (Math.floor(rand.nextDouble() * 1000))/10;
//        Bukkit.getLogger().severe("is crit: " + isCrit);
        Bukkit.getLogger().severe("DAMAGE 1: " + damage);
        Bukkit.getLogger().severe("num: " + num);
        boolean isCrit = (num <= critChance);
        if(isCrit){ damage = damage * con.getConfig().getDouble("strength.unarmed.crit-damage-mult"); }
        Bukkit.getLogger().severe("is crit: " + isCrit);
        Bukkit.getLogger().severe("DAMAGE 2: " + damage);
        return damage;
    }

    public void setConfig() { con = plugin.getSkillSettings(); }
}
