package com.valiantrealms.zombiesmc;

import com.valiantrealms.zombiesmc.util.BlockListener;
import com.valiantrealms.zombiesmc.util.PlayerHandler;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;

public final class ZombiesMC extends JavaPlugin {
    private static Economy econ = null;
    private static Permission perms = null;
    private static Chat chat = null;
    private static HashMap<String, Integer> breakableBlocks = new HashMap<>();
    @Override
    public void onEnable() {
        // Plugin startup logic
        Bukkit.getLogger().info("Enabling ZombiesMC");

        //Configs
        this.saveDefaultConfig();

        saveResource("PlayerInfo" + System.getProperty("file.separator") + "PlayerSettings.yml", false);

        saveResource("SkillSettings" + System.getProperty("file.separator") + "Cooking.yml", false);
        saveResource("SkillSettings" + System.getProperty("file.separator") + "Farming.yml", false);
        saveResource("SkillSettings" + System.getProperty("file.separator") + "Husbandry.yml", false);
        saveResource("SkillSettings" + System.getProperty("file.separator") + "LockPicking.yml", false);
        saveResource("SkillSettings" + System.getProperty("file.separator") + "Melee.yml", false);
        saveResource("SkillSettings" + System.getProperty("file.separator") + "Ranged.yml", false);
        saveResource("SkillSettings" + System.getProperty("file.separator") + "Salvage.yml", false);
        saveResource("SkillSettings" + System.getProperty("file.separator") + "Sneaking.yml", false);
        saveResource("SkillSettings" + System.getProperty("file.separator") + "Stamina.yml", false);
        saveResource("SkillSettings" + System.getProperty("file.separator") + "Strength.yml", false);

        saveResource("BlockValues.yml", false);

        // Economy
        if (!setupEconomy() ) {
            getLogger().warning("Vault not found! Economy features will not work");
        }else{
            getLogger().info("Setting up the economy!");
            setupPermissions();
            setupChat();
        }

        // Enabling commands

        // Enabling utils
        new PlayerHandler(this);
        new BlockListener(this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        Bukkit.getLogger().info("Disabling ZombiesMC");
    }

    /**
     * ECONOMY STUFF
     */
    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }

    private boolean setupChat() {
        RegisteredServiceProvider<Chat> rsp = getServer().getServicesManager().getRegistration(Chat.class);
        chat = rsp.getProvider();
        return chat != null;
    }

    private boolean setupPermissions() {
        RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
        perms = rsp.getProvider();
        return perms != null;
    }

    public Economy getEcon(){
        return econ;
    }
}
