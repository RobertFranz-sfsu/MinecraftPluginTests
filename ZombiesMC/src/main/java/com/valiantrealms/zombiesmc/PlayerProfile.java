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
    private double rangedDamage;
    private double meleeDamage;
    private double rangedCritChance;
    private double meleeCritChance;
    ConfigUtil playerCon;
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

    public void register(UUID id){ // Loads player from saved file
        this.setPlayerCon();

        this.uid = id;
        this.health = playerCon.getConfig().getDouble("health");
        this.stamina = playerCon.getConfig().getDouble("stamina");

        this.rangedDamage = playerCon.getConfig().getDouble("ranged-damage");
        this.meleeDamage = playerCon.getConfig().getDouble("melee-damage");
        this.rangedCritChance = playerCon.getConfig().getDouble("ranged-crit-chance");
        this.meleeCritChance = playerCon.getConfig().getDouble("melee-crit-chance");

        skills[0] = playerCon.getConfig().getDouble("skills.lockpicking");
        skills[1] = playerCon.getConfig().getDouble("skills.farming");
        skills[2] = playerCon.getConfig().getDouble("skills.stamina");
        skills[3] = playerCon.getConfig().getDouble("skills.salvage");
        skills[4] = playerCon.getConfig().getDouble("skills.husbandry");
        skills[5] = playerCon.getConfig().getDouble("skills.strength");
        skills[6] = playerCon.getConfig().getDouble("skills.cooking");
        skills[7] = playerCon.getConfig().getDouble("skills.ranged");
        skills[8] = playerCon.getConfig().getDouble("skills.melee");
    }

    public void save(UUID id){ // Saves current stats to config then sets config to updated version
        playerCon.getConfig().set("health", this.health);
        playerCon.getConfig().set("stamina", this.stamina);

        playerCon.getConfig().set("ranged-damage", this.rangedDamage);
        playerCon.getConfig().set("ranged-crit-chance", this.rangedCritChance);
        playerCon.getConfig().set("melee-damage", this.meleeDamage);
        playerCon.getConfig().set("melee-crit-chance", this.meleeCritChance);

        playerCon.getConfig().set("skills.lockpicking", skills[0]);
        playerCon.getConfig().set("skills.farming", skills[1]);
        playerCon.getConfig().set("skills.stamina", skills[2]);
        playerCon.getConfig().set("skills.salvage", skills[3]);
        playerCon.getConfig().set("skills.husbandry", skills[4]);
        playerCon.getConfig().set("skills.strength", skills[5]);
        playerCon.getConfig().set("skills.cooking", skills[6]);
        playerCon.getConfig().set("skills.ranged", skills[7]);
        playerCon.getConfig().set("skills.melee", skills[8]);

        playerCon.save();
        this.setPlayerCon();
    }

    public void reload(){ // Writes current in-game stats to the config then reloads
        playerCon.getConfig().set("health", this.health);
        playerCon.getConfig().set("stamina", this.stamina);

        this.rangedDamage = playerCon.getConfig().getDouble("ranged-damage");
        this.meleeDamage = playerCon.getConfig().getDouble("melee-damage");
        this.rangedCritChance = playerCon.getConfig().getDouble("ranged-crit-chance");
        this.meleeCritChance = playerCon.getConfig().getDouble("melee-crit-chance");

        playerCon.getConfig().set("skills.lockpicking", skills[0]);
        playerCon.getConfig().set("skills.farming", skills[1]);
        playerCon.getConfig().set("skills.stamina", skills[2]);
        playerCon.getConfig().set("skills.salvage", skills[3]);
        playerCon.getConfig().set("skills.husbandry", skills[4]);
        playerCon.getConfig().set("skills.strength", skills[5]);
        playerCon.getConfig().set("skills.cooking", skills[6]);
        playerCon.getConfig().set("skills.ranged", skills[7]);
        playerCon.getConfig().set("skills.melee", skills[8]);
        playerCon.save();

        this.health = playerCon.getConfig().getDouble("health");
        this.stamina =playerCon.getConfig().getDouble("stamina");

        this.rangedDamage = playerCon.getConfig().getDouble("ranged-damage");
        this.meleeDamage = playerCon.getConfig().getDouble("melee-damage");
        this.rangedCritChance = playerCon.getConfig().getDouble("ranged-crit-chance");
        this.meleeCritChance = playerCon.getConfig().getDouble("melee-crit-chance");

        skills[0] = playerCon.getConfig().getDouble("skills.lockpicking");
        skills[1] = playerCon.getConfig().getDouble("skills.farming");
        skills[2] = playerCon.getConfig().getDouble("skills.stamina");
        skills[3] = playerCon.getConfig().getDouble("skills.salvage");
        skills[4] = playerCon.getConfig().getDouble("skills.husbandry");
        skills[5] = playerCon.getConfig().getDouble("skills.strength");
        skills[6] = playerCon.getConfig().getDouble("skills.cooking");
        skills[7] = playerCon.getConfig().getDouble("skills.ranged");
        skills[8] = playerCon.getConfig().getDouble("skills.melee");

        this.setHealth();

        this.setRangedDamage();
        this.setRangedCritChance();
        this.setMeleeDamage();
        this.setMeleeCritChance();

        this.setPlayerCon();
    }

    public void reloadNoSave(){
        this.health = playerCon.getConfig().getDouble("health");
        this.stamina =playerCon.getConfig().getDouble("stamina");

        this.rangedDamage = playerCon.getConfig().getDouble("ranged-damage");
        this.meleeDamage = playerCon.getConfig().getDouble("melee-damage");
        this.rangedCritChance = playerCon.getConfig().getDouble("ranged-crit-chance");
        this.meleeCritChance = playerCon.getConfig().getDouble("melee-crit-chance");

        skills[0] = playerCon.getConfig().getDouble("skills.lockpicking");
        skills[1] = playerCon.getConfig().getDouble("skills.farming");
        skills[2] = playerCon.getConfig().getDouble("skills.stamina");
        skills[3] = playerCon.getConfig().getDouble("skills.salvage");
        skills[4] = playerCon.getConfig().getDouble("skills.husbandry");
        skills[5] = playerCon.getConfig().getDouble("skills.strength");
        skills[6] = playerCon.getConfig().getDouble("skills.cooking");
        skills[7] = playerCon.getConfig().getDouble("skills.ranged");
        skills[8] = playerCon.getConfig().getDouble("skills.melee");

        this.setHealth();

        this.setRangedDamage();
        this.setRangedCritChance();
        this.setMeleeDamage();
        this.setMeleeCritChance();
    }

    public void setSkillsFromConfig(){ // Updates to new player config then updates player server stats
        this.setPlayerCon();

        this.health = playerCon.getConfig().getDouble("health");
        this.stamina = playerCon.getConfig().getDouble("stamina");

        this.rangedDamage = playerCon.getConfig().getDouble("ranged-damage");
        this.meleeDamage = playerCon.getConfig().getDouble("melee-damage");
        this.rangedCritChance = playerCon.getConfig().getDouble("ranged-crit-chance");
        this.meleeCritChance = playerCon.getConfig().getDouble("melee-crit-chance");

        skills[0] = playerCon.getConfig().getDouble("skills.lockpicking");
        skills[1] = playerCon.getConfig().getDouble("skills.farming");
        skills[2] = playerCon.getConfig().getDouble("skills.stamina");
        skills[3] = playerCon.getConfig().getDouble("skills.salvage");
        skills[4] = playerCon.getConfig().getDouble("skills.husbandry");
        skills[5] = playerCon.getConfig().getDouble("skills.strength");
        skills[6] = playerCon.getConfig().getDouble("skills.cooking");
        skills[7] = playerCon.getConfig().getDouble("skills.ranged");
        skills[8] = playerCon.getConfig().getDouble("skills.melee");

        this.setHealth();

        this.setRangedDamage();
        this.setRangedCritChance();
        this.setMeleeDamage();
        this.setMeleeCritChance();
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

    public void setRangedDamage(){
        this.rangedDamage = (plugin.getPlayers().get(uid).getSkills()[7]) * plugin.getSkillSettings().getConfig().getDouble("ranged.damage-increase");
        playerCon.getConfig().set("ranged-damage", this.rangedDamage);
        playerCon.save();
    }

    public void setRangedCritChance(){
        this.rangedCritChance = rangedDamage * plugin.getSkillSettings().getConfig().getDouble("ranged.crit-chance-increase");
        playerCon.getConfig().set("ranged-crit-chance", this.rangedCritChance);
        playerCon.save();
    }

    public void setMeleeDamage(){
        this.meleeDamage = (plugin.getPlayers().get(uid).getSkills()[5]) * plugin.getSkillSettings().getConfig().getDouble("strength.damage-increase");
        playerCon.getConfig().set("melee-damage", this.meleeDamage);
        playerCon.save();
    }

    public void setMeleeCritChance(){
        this.meleeCritChance = this.meleeDamage * plugin.getSkillSettings().getConfig().getDouble("strength.crit-chance-increase");
        playerCon.getConfig().set("melee-crit-chance", this.meleeCritChance);
        playerCon.save();
    }
    public void setPlayerCon(){ playerCon = new ConfigUtil(plugin, System.getProperty("file.separator") + "PlayerInfo" + System.getProperty("file.separator") + uid + ".yml"); }

    /**
     * Individual updaters
     */
    public void updateStrength(){
        playerCon.getConfig().set("melee-damage", this.meleeDamage);
        playerCon.getConfig().set("melee-crit-chance", this.meleeCritChance);

        this.setMeleeDamage();
        this.setMeleeCritChance();
    }
    public void updateRanged(){
        playerCon.getConfig().set("ranged-damage", this.rangedDamage);
        playerCon.getConfig().set("ranged-crit-chance", this.rangedCritChance);

        this.setRangedDamage();
        this.setRangedCritChance();
    }

    /**
     * Getters
     */
    public double[] getSkills(){ return this.skills; }
    public ConfigUtil getPlayerConfig(){ return this.playerCon; }
    public double getHealth() { return this.health; }
    public double getRangedDamage() { return this.rangedDamage; }
    public double getRangedCritChance() { return this.rangedCritChance; }
    public double getMeleeDamage() { return meleeDamage; }
    public double getMeleeCritChance() { return meleeCritChance; }
}
