package com.valiantrealms.zombiesmc;

import com.valiantrealms.zombiesmc.util.ConfigUtil;
import com.valiantrealms.zombiesmc.util.Experience;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

public class PlayerProfile {
    private final ZombiesMC plugin;
    private UUID id;
    private double health;
    private double stamina;
    private double rangedDamage;
    private double meleeDamage;
    private double rangedCritChance;
    private double meleeCritChance;
    private double instantAdultChance;
    private double multiBreedChance;
    private double farmingMultiHarvest;
    private double husbandryMultiDrop;
    private double farmingAutoGrow;
    private Experience experience;

    double[] skills = new double[9];
    double[] skillXP = new double[9];
    double[] skillXPNeeded = new double[9];

    // Configs
    ConfigUtil playerCon;
    ConfigUtil xpCon;

    /*
     * 0 lockpicking
     * 1 farming
     * 2 stamina (skill)
     * 3 salvage
     * 4 husbandry
     * 5 strength
     * 6 cooking
     * 7 ranged
     * 8 stealth
     */

    public PlayerProfile(ZombiesMC plugin){
        this.plugin = plugin;
        this.experience = plugin.getExperience();
    }

    public void register(UUID id){ // Loads player from saved file
        this.setPlayerCon();

        this.id = id;
        this.health = playerCon.getConfig().getDouble("health");
        this.stamina = playerCon.getConfig().getDouble("stamina");

        this.rangedDamage = playerCon.getConfig().getDouble("ranged.damage");
        this.meleeDamage = playerCon.getConfig().getDouble("melee.damage");
        this.rangedCritChance = playerCon.getConfig().getDouble("ranged.crit-chance");
        this.meleeCritChance = playerCon.getConfig().getDouble("melee.crit-chance");

        this.instantAdultChance = playerCon.getConfig().getDouble("husbandry.instant-adult-chance");
        this.multiBreedChance = playerCon.getConfig().getDouble("husbandry.multi-breeding-chance");
        this.husbandryMultiDrop = playerCon.getConfig().getDouble("husbandry.multi-drop-chance");

        this.farmingMultiHarvest = playerCon.getConfig().getDouble("farming.multi-harvest-chance");
        this.farmingAutoGrow = playerCon.getConfig().getDouble("farming.auto-grow-chance");

        skills[0] = playerCon.getConfig().getDouble("skills.lockpicking");
        skills[1] = playerCon.getConfig().getDouble("skills.farming");
        skills[2] = playerCon.getConfig().getDouble("skills.stamina");
        skills[3] = playerCon.getConfig().getDouble("skills.salvage");
        skills[4] = playerCon.getConfig().getDouble("skills.husbandry");
        skills[5] = playerCon.getConfig().getDouble("skills.strength");
        skills[6] = playerCon.getConfig().getDouble("skills.cooking");
        skills[7] = playerCon.getConfig().getDouble("skills.ranged");
        skills[8] = playerCon.getConfig().getDouble("skills.stealth");

        skillXP[0] = playerCon.getConfig().getDouble("experience.lockpicking");
        skillXP[1] = playerCon.getConfig().getDouble("experience.farming");
        skillXP[2] = playerCon.getConfig().getDouble("experience.stamina");
        skillXP[3] = playerCon.getConfig().getDouble("experience.salvage");
        skillXP[4] = playerCon.getConfig().getDouble("experience.husbandry");
        skillXP[5] = playerCon.getConfig().getDouble("experience.strength");
        skillXP[6] = playerCon.getConfig().getDouble("experience.cooking");
        skillXP[7] = playerCon.getConfig().getDouble("experience.ranged");
        skillXP[8] = playerCon.getConfig().getDouble("experience.stealth");

        skillXPNeeded[0] = playerCon.getConfig().getDouble("experience-needed.lockpicking");
        skillXPNeeded[1] = playerCon.getConfig().getDouble("experience-needed.farming");
        skillXPNeeded[2] = playerCon.getConfig().getDouble("experience-needed.stamina");
        skillXPNeeded[3] = playerCon.getConfig().getDouble("experience-needed.salvage");
        skillXPNeeded[4] = playerCon.getConfig().getDouble("experience-needed.husbandry");
        skillXPNeeded[5] = playerCon.getConfig().getDouble("experience-needed.strength");
        skillXPNeeded[6] = playerCon.getConfig().getDouble("experience-needed.cooking");
        skillXPNeeded[7] = playerCon.getConfig().getDouble("experience-needed.ranged");
        skillXPNeeded[8] = playerCon.getConfig().getDouble("experience-needed.stealth");
    }

    public void save(){ // Saves current stats to config then sets config to updated version
        playerCon.getConfig().set("health", this.health);
        playerCon.getConfig().set("stamina", this.stamina);

        playerCon.getConfig().set("ranged.damage", this.rangedDamage);
        playerCon.getConfig().set("ranged.crit-chance", this.rangedCritChance);
        playerCon.getConfig().set("melee.damage", this.meleeDamage);
        playerCon.getConfig().set("melee.crit-chance", this.meleeCritChance);

        playerCon.getConfig().set("husbandry.instant-adult-chance", this.instantAdultChance);
        playerCon.getConfig().set("husbandry.multi-breeding-chance", this.multiBreedChance);
        playerCon.getConfig().set("husbandry.multi-drop-chance", this.farmingMultiHarvest);

        playerCon.getConfig().set("farming.multi-harvest-chance", this.farmingMultiHarvest);
        playerCon.getConfig().set("farming.auto-grow-chance", this.farmingAutoGrow);

        playerCon.getConfig().set("skills.lockpicking", skills[0]);
        playerCon.getConfig().set("skills.farming", skills[1]);
        playerCon.getConfig().set("skills.stamina", skills[2]);
        playerCon.getConfig().set("skills.salvage", skills[3]);
        playerCon.getConfig().set("skills.husbandry", skills[4]);
        playerCon.getConfig().set("skills.strength", skills[5]);
        playerCon.getConfig().set("skills.cooking", skills[6]);
        playerCon.getConfig().set("skills.ranged", skills[7]);
        playerCon.getConfig().set("skills.stealth", skills[8]);

        playerCon.getConfig().set("experience.lockpicking", skillXP[0]);
        playerCon.getConfig().set("experience.farming", skillXP[1]);
        playerCon.getConfig().set("experience.stamina", skillXP[2]);
        playerCon.getConfig().set("experience.salvage", skillXP[3]);
        playerCon.getConfig().set("experience.husbandry", skillXP[4]);
        playerCon.getConfig().set("experience.strength", skillXP[5]);
        playerCon.getConfig().set("experience.cooking", skillXP[6]);
        playerCon.getConfig().set("experience.ranged", skillXP[7]);
        playerCon.getConfig().set("experience.stealth", skillXP[8]);

        playerCon.getConfig().set("experience-needed.lockpicking", skillXPNeeded[0]);
        playerCon.getConfig().set("experience-needed.farming", skillXPNeeded[1]);
        playerCon.getConfig().set("experience-needed.stamina", skillXPNeeded[2]);
        playerCon.getConfig().set("experience-needed.salvage", skillXPNeeded[3]);
        playerCon.getConfig().set("experience-needed.husbandry", skillXPNeeded[4]);
        playerCon.getConfig().set("experience-needed.strength", skillXPNeeded[5]);
        playerCon.getConfig().set("experience-needed.cooking", skillXPNeeded[6]);
        playerCon.getConfig().set("experience-needed.ranged", skillXPNeeded[7]);
        playerCon.getConfig().set("experience-needed.stealth", skillXPNeeded[8]);

        playerCon.save();
        this.setPlayerCon();
    }

    public void reload(){ // Writes current in-game stats to the config then reloads
        this.save();
        this.register(id);
    }

    public void unregister(){
        this.save();
        plugin.getPlayers().remove(id);
        plugin.getExperience().removePlayer(id);
    }

    public void setHealth(){
        this.health = (BigDecimal.valueOf(plugin.getPlayers().get(id).getSkills()[5]).multiply(BigDecimal.valueOf(plugin.getSkillSettings().getConfig().getDouble("strength.health.increase-per-level")))).add(BigDecimal.valueOf(plugin.getSkillSettings().getConfig().getDouble("strength.health.base"))).doubleValue();

        Objects.requireNonNull(Objects.requireNonNull(Bukkit.getPlayer(id)).getAttribute(Attribute.GENERIC_MAX_HEALTH)).setBaseValue(this.health);
        Objects.requireNonNull(Objects.requireNonNull(Bukkit.getPlayer(id)).getAttribute(Attribute.GENERIC_MAX_HEALTH)).setBaseValue(health);
    }

    public boolean setSkillCommand(String input, double level){
        plugin.getPlayers().get(id).getSkills()[plugin.getPlayerHandler().skillNumber(input)] = level;
        plugin.getPlayers().get(id).save();
        return true;
    }

    /**
     * Ranged
     */

    public void setRangedDamage(){
        this.rangedDamage = (plugin.getPlayers().get(id).getSkills()[7]) * plugin.getSkillSettings().getConfig().getDouble("ranged.damage.increase-per-level");
        playerCon.getConfig().set("ranged.damage", this.rangedDamage);
        playerCon.save();
    }

    public void setRangedCritChance(){
        this.rangedCritChance = rangedDamage * plugin.getSkillSettings().getConfig().getDouble("ranged.crit.chance-increase-per-level");
        playerCon.getConfig().set("ranged.crit-chance", this.rangedCritChance);
        playerCon.save();
    }

    public void setRangedDamageAndCrit(){
        this.rangedDamage = (plugin.getPlayers().get(id).getSkills()[7]) * plugin.getSkillSettings().getConfig().getDouble("ranged.damage.increase-per-level");
        playerCon.getConfig().set("ranged.damage", this.rangedDamage);
        this.setRangedCritChance();

        playerCon.save();
    }

    /**
     * Melee
     */

    public void setMeleeDamage(){
        this.meleeDamage = (plugin.getPlayers().get(id).getSkills()[5]) * plugin.getSkillSettings().getConfig().getDouble("strength.damage.increase-per-level");
        playerCon.getConfig().set("melee.damage", this.meleeDamage);

        playerCon.save();
    }

    public void setMeleeCritChance(){
        this.meleeCritChance = (this.meleeDamage * plugin.getSkillSettings().getConfig().getDouble("strength.crit.chance-increase-per-level"));
        playerCon.getConfig().set("melee.crit-chance", this.meleeCritChance);
        playerCon.save();
    }

    public void setMeleeDamageAndCrit(){
        this.meleeDamage = (plugin.getPlayers().get(id).getSkills()[5]) * plugin.getSkillSettings().getConfig().getDouble("strength.damage.increase-per-level");
        playerCon.getConfig().set("melee.damage", this.meleeDamage);
        this.setMeleeCritChance();
        playerCon.save();
    }

    /**
     * Husbandry
     */

    public void setInstantAdultChance(){
        this.instantAdultChance = plugin.getSkillSettings().getConfig().getDouble("husbandry.instant-adult.base-chance") +
                (plugin.getPlayers().get(id).getSkills()[4] * plugin.getSkillSettings().getConfig().getDouble("husbandry.instant-adult.increase-per-level"));
        playerCon.getConfig().set("husbandry.instant-adult-chance", this.instantAdultChance);
        playerCon.save();
    }

    public void setMultiBreedChance(){
        this.multiBreedChance = plugin.getSkillSettings().getConfig().getDouble("husbandry.multi-breeding.base-chance") +
                (plugin.getPlayers().get(id).getSkills()[4] * plugin.getSkillSettings().getConfig().getDouble("husbandry.multi-breeding.increase-per-level"));
        playerCon.getConfig().set("husbandry.multi-breeding-chance", this.multiBreedChance);
        playerCon.save();
    }

    public void setHusbandryMultiDrop() {
        this.husbandryMultiDrop = plugin.getSkillSettings().getConfig().getDouble("husbandry.multi-breeding.base-chance") +
                (plugin.getPlayers().get(id).getSkills()[4] * plugin.getSkillSettings().getConfig().getDouble("husbandry.multi-breeding.increase-per-level"));
        playerCon.getConfig().set("husbandry.multi-drop-chance", this.multiBreedChance);
        playerCon.save();
    }

    /**
     * Farming
     */

    public void setFarmingMultiHarvestChance(){
        this.farmingMultiHarvest = Math.min((playerCon.getConfig().getDouble("farming.multi-harvest.base-chance")) + (plugin.getPlayers().get(id).getSkills()[1]*playerCon.getConfig().getDouble("farming.multi-harvest.chance-increase-per-level")), playerCon.getConfig().getDouble("farming.multi-harvest.max-chance"));

        playerCon.getConfig().set("farming.multi-harvest-chance", this.farmingMultiHarvest);
        playerCon.save();
    }

    public void setFarmingAutoGrowChance(){
        this.farmingAutoGrow = Math.min((playerCon.getConfig().getDouble("farming.auto-grow.base-chance")) + (plugin.getPlayers().get(id).getSkills()[1]*playerCon.getConfig().getDouble("farming.auto-grow.chance-increase-per-level")), playerCon.getConfig().getDouble("farming.auto-grow.max-chance"));

        playerCon.getConfig().set("farming.auto-grow-chance", this.farmingAutoGrow);
        playerCon.save();
    }

    /**
     * Other
     */

    public void setVals(){
        this.setHealth();

        this.setRangedDamageAndCrit();
        this.setMeleeDamageAndCrit();

        this.setInstantAdultChance();
        this.setMultiBreedChance();
        this.setHusbandryMultiDrop();

        this.setFarmingAutoGrowChance();
        this.setFarmingMultiHarvestChance();

        this.save();
    }

    public void setPlayerCon(){ playerCon = new ConfigUtil(plugin, System.getProperty("file.separator") + "PlayerInfo" + System.getProperty("file.separator") + id + ".yml"); }

    /**
     * Individual updaters
     */
    public void updateStrength(){
        playerCon.getConfig().set("melee.damage", this.meleeDamage);
        playerCon.getConfig().set("melee.crit-chance", this.meleeCritChance);

        this.setMeleeDamage();
        this.setMeleeCritChance();
    }
    public void updateRanged(){
        playerCon.getConfig().set("ranged.damage", this.rangedDamage);
        playerCon.getConfig().set("ranged.crit-chance", this.rangedCritChance);

        this.setRangedDamage();
        this.setRangedCritChance();
    }

    /**
     * Getters
     */
    public double[] getSkills(){ return this.skills; }
    public double[] getSkillXP(){ return this.skillXP; }
    public double[] getSkillXPNeeded(){ return this.skillXPNeeded; }
    public ConfigUtil getPlayerConfig(){ return this.playerCon; }
    public double getHealth() { return this.health; }
    public double getRangedDamage() { return this.rangedDamage; }
    public double getRangedCritChance() { return this.rangedCritChance; }
    public double getMeleeDamage() { return this.meleeDamage; }
    public double getMeleeCritChance() { return this.meleeCritChance; }
    public double getInstantAdultChance() { return this.instantAdultChance; }
    public double getMultiBreedChance() { return this.multiBreedChance; }
    public double getFarmingMultiHarvestChance() { return this.farmingMultiHarvest; }
    public double getHusbandryMultiDropChance() { return this.husbandryMultiDrop; }
    public double getFarmingAutoGrow() { return this.farmingAutoGrow; }
}
