package com.valiantrealms.zombiesmc.util.skills;

import com.valiantrealms.zombiesmc.ZombiesMC;
import com.valiantrealms.zombiesmc.util.ConfigUtil;

import java.util.Random;
import java.util.UUID;

public class Ranged {
    private final ZombiesMC plugin;
    private ConfigUtil con;
    private Random rand = new Random();

    public Ranged(ZombiesMC plugin){
        this.plugin = plugin;
        this.setConfig();
    }

    public void setConfig() { con = plugin.getSkillSettings(); }

    public double rangedDamage(UUID id, double originalDamage){
        double damage = 0.0;



        return damage;
    }
}
