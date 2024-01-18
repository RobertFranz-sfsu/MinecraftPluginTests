package com.valiantrealms.zombiesmc.util.skills;

import com.valiantrealms.zombiesmc.ZombiesMC;
import com.valiantrealms.zombiesmc.util.ConfigUtil;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;

import java.math.BigDecimal;
import java.util.UUID;

public class Stealth {
    private final ZombiesMC plugin;
    private ConfigUtil con;
    private double detectionReduction;
    private double defaultRadius;

    public Stealth(ZombiesMC plugin){
        this.plugin = plugin;
        this.setConfig();
    }

    public void levelUp(UUID id){
        plugin.getPlayers().get(id).getSkills()[9]++;
        plugin.getPlayers().get(id).save();
        this.setStealth(id);
    }

    public void setStealth(UUID id){
        // Equation: base radius - (stealth level * detection reduction)
        Bukkit.getPlayer(id).getAttribute(Attribute.GENERIC_FOLLOW_RANGE).setBaseValue((BigDecimal.valueOf(plugin.getPlayers().get(id).getSkills()[9]).multiply(BigDecimal.valueOf(this.detectionReduction)).subtract(BigDecimal.valueOf(this.defaultRadius)).doubleValue()));
    }

    public void setConfig() {
        con = plugin.getSkillSettings();
        this.detectionReduction = con.getConfig().getDouble("stealth.detection-reduction");
        this.defaultRadius = con.getConfig().getDouble("stealth.default-radius");
    }
}
