package com.valiantrealms.zombiesmc.util.skills;

import com.valiantrealms.zombiesmc.ZombiesMC;
import com.valiantrealms.zombiesmc.util.ConfigUtil;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

public class Stealth {
    private final ZombiesMC plugin;
    private ConfigUtil con;
    private double detectionReduction;
    private double defaultRadius;
    private double minRadius;

    public Stealth(ZombiesMC plugin){
        this.plugin = plugin;
        this.setConfig();
    }

    public void levelUp(UUID id){
        plugin.getPlayers().get(id).getSkills()[8]++;
        plugin.getPlayers().get(id).save();
        this.setStealth(id);
    }

    // CHANGE ALL OF THIS LOL
    /*
    Maybe make a custom sorted list that takes in a HashMap and sorts it based on the values as [UID, detection radius]
    Whenever they get within range of a mob, the mob must either not see the player if they are beyond the detection radius number (Set target to null)
    OR prioritise the player with the highest detection number
    ie. [Name, 20]
     */
    public void setStealth(UUID id){
        // Equation: base radius - (stealth level * detection reduction)
        Bukkit.getLogger().info("B4 range: " + Objects.requireNonNull(Objects.requireNonNull(Bukkit.getPlayer(id)).getAttribute(Attribute.GENERIC_FOLLOW_RANGE)).getBaseValue());
        Bukkit.getLogger().info("New Follow range: " + (BigDecimal.valueOf(plugin.getPlayers().get(id).getSkills()[8]).multiply(BigDecimal.valueOf(this.detectionReduction)).subtract(BigDecimal.valueOf(this.defaultRadius)).doubleValue()));
        Bukkit.getLogger().info("detectionReduction: " + this.detectionReduction);
        Bukkit.getLogger().info("defaultRadius: " + this.defaultRadius);
        Bukkit.getLogger().info("minRadius: " + this.minRadius);

        Objects.requireNonNull(Objects.requireNonNull(Bukkit.getPlayer(id)).getAttribute(Attribute.GENERIC_FOLLOW_RANGE)).setBaseValue(Math.min((BigDecimal.valueOf(plugin.getPlayers().get(id).getSkills()[8]).multiply(BigDecimal.valueOf(this.detectionReduction)).subtract(BigDecimal.valueOf(this.defaultRadius)).doubleValue()), this.minRadius));
        Bukkit.getLogger().info("After range: " + Objects.requireNonNull(Objects.requireNonNull(Bukkit.getPlayer(id)).getAttribute(Attribute.GENERIC_FOLLOW_RANGE)).getBaseValue());
    }

    public void setConfig() {
        con = plugin.getSkillSettings();
        this.detectionReduction = con.getConfig().getDouble("stealth.detection.radius-reduction-per-level");
        this.defaultRadius = con.getConfig().getDouble("stealth.detection.default-radius");
        this.minRadius = con.getConfig().getDouble("stealth.detection.min-radius");
    }
}
