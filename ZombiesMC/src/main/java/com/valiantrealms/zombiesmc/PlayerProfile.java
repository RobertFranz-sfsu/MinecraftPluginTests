package com.valiantrealms.zombiesmc;

import com.valiantrealms.zombiesmc.util.ConfigUtil;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

public class PlayerProfile {
    private final ZombiesMC plugin;
    private UUID uid;
    private double health;
    private double stamina;
    private boolean isMainHandEmpty;
    private ConfigUtil config;
    double[] skills = new double[9];
    /**
     * 0 lockpicking
     * 1 farming
     * 2 stamina (skill)
     * 3 salvage
     * 4 husbandry
     * 5 strength
     * 6 cooking
     * 7 ranged
     * 8 melee
     */

    public PlayerProfile(ZombiesMC plugin){ this.plugin = plugin; }

    public void register(UUID id){
        ConfigUtil con = new ConfigUtil(plugin, System.getProperty("file.separator") + "PlayerInfo" + System.getProperty("file.separator") + id + ".yml");

        this.uid = id;
        this.health = con.getConfig().getDouble("health");
        this.stamina = con.getConfig().getDouble("stamina");

        skills[0] = con.getConfig().getDouble("skills.lockpicking");
        skills[1] = con.getConfig().getDouble("skills.farming");
        skills[2] = con.getConfig().getDouble("skills.stamina");
        skills[3] = con.getConfig().getDouble("skills.salvage");
        skills[4] = con.getConfig().getDouble("skills.husbandry");
        skills[5] = con.getConfig().getDouble("skills.strength");
        skills[6] = con.getConfig().getDouble("skills.cooking");
        skills[7] = con.getConfig().getDouble("skills.ranged");
        skills[8] = con.getConfig().getDouble("skills.melee");
    }

    public void save(UUID id){
        ConfigUtil con = new ConfigUtil(plugin, System.getProperty("file.separator") + "PlayerInfo" + System.getProperty("file.separator") + id + ".yml");

        con.getConfig().set("health", this.health);
        con.getConfig().set("stamina", this.stamina);

        con.getConfig().set("skills.lockpicking", skills[0]);
        con.getConfig().set("skills.farming", skills[1]);
        con.getConfig().set("skills.stamina", skills[2]);
        con.getConfig().set("skills.salvage", skills[3]);
        con.getConfig().set("skills.husbandry", skills[4]);
        con.getConfig().set("skills.strength", skills[5]);
        con.getConfig().set("skills.cooking", skills[6]);
        con.getConfig().set("skills.ranged", skills[7]);
        con.getConfig().set("skills.melee", skills[8]);
        con.save();

        this.config = con;
    }

    public void reload(){
        ConfigUtil con = new ConfigUtil(plugin, System.getProperty("file.separator") + "PlayerInfo" + System.getProperty("file.separator") + uid + ".yml");

        con.getConfig().set("health", this.health);
        con.getConfig().set("stamina", this.stamina);

        con.getConfig().set("skills.lockpicking", skills[0]);
        con.getConfig().set("skills.farming", skills[1]);
        con.getConfig().set("skills.stamina", skills[2]);
        con.getConfig().set("skills.salvage", skills[3]);
        con.getConfig().set("skills.husbandry", skills[4]);
        con.getConfig().set("skills.strength", skills[5]);
        con.getConfig().set("skills.cooking", skills[6]);
        con.getConfig().set("skills.ranged", skills[7]);
        con.getConfig().set("skills.melee", skills[8]);
        con.save();

        this.health = con.getConfig().getDouble("health");
        this.stamina =con.getConfig().getDouble("stamina");

        skills[0] = con.getConfig().getDouble("skills.lockpicking");
        skills[1] = con.getConfig().getDouble("skills.farming");
        skills[2] = con.getConfig().getDouble("skills.stamina");
        skills[3] = con.getConfig().getDouble("skills.salvage");
        skills[4] = con.getConfig().getDouble("skills.husbandry");
        skills[5] = con.getConfig().getDouble("skills.strength");
        skills[6] = con.getConfig().getDouble("skills.cooking");
        skills[7] = con.getConfig().getDouble("skills.ranged");
        skills[8] = con.getConfig().getDouble("skills.melee");

        this.config = con;
    }

    public void setSkillsFromConfig(){
        ConfigUtil con = new ConfigUtil(plugin, System.getProperty("file.separator") + "PlayerInfo" + System.getProperty("file.separator") + uid + ".yml");

        this.health = con.getConfig().getDouble("health");
        this.stamina = con.getConfig().getDouble("stamina");

        skills[0] = con.getConfig().getDouble("skills.lockpicking");
        skills[1] = con.getConfig().getDouble("skills.farming");
        skills[2] = con.getConfig().getDouble("skills.stamina");
        skills[3] = con.getConfig().getDouble("skills.salvage");
        skills[4] = con.getConfig().getDouble("skills.husbandry");
        skills[5] = con.getConfig().getDouble("skills.strength");
        skills[6] = con.getConfig().getDouble("skills.cooking");
        skills[7] = con.getConfig().getDouble("skills.ranged");
        skills[8] = con.getConfig().getDouble("skills.melee");
    }

    public void unregister(UUID id){
        this.save(id);
        plugin.getPlayers().remove(id);
    }

    public void setHealth(){
        this.health = (BigDecimal.valueOf(plugin.getPlayers().get(uid).getSkills()[5]).multiply(BigDecimal.valueOf(plugin.getSkillSettings().getConfig().getDouble("strength.health-increase-per-level")))).add(BigDecimal.valueOf(plugin.getPlayerSettings().getConfig().getDouble("starting-health"))).doubleValue();

        Objects.requireNonNull(Bukkit.getPlayer(uid).getAttribute(Attribute.GENERIC_MAX_HEALTH)).setBaseValue(this.health);
        Bukkit.getPlayer(uid).getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(health);
    }

    public boolean setSkillCommand(String input, double level){
        plugin.getPlayers().get(uid).getSkills()[plugin.getPlayerHandler().skillNumber(input)] = level;
        plugin.getPlayers().get(uid).save(uid);
        return true;
    }

    public void setMainHandEmpty(boolean isEmpty){
        this.isMainHandEmpty = isEmpty;
    }


    /**
     * Getters
     */
    public double[] getSkills(){ return this.skills; }

    public boolean isMainHandEmpty() { return isMainHandEmpty; }

    public ConfigUtil getConfig(){ return this.config; }

    public double getHealth() { return this.health; }
}
