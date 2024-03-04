package com.valiantrealms.zombiesmc.util.skills;

import com.valiantrealms.zombiesmc.ZombiesMC;
import com.valiantrealms.zombiesmc.util.ConfigUtil;

public class Salvage {
    private final ZombiesMC plugin;
    private ConfigUtil con;

    public Salvage(ZombiesMC plugin){
        this.plugin = plugin;
        this.setConfig();
    }

    public void setConfig() {
        con = plugin.getSkillSettings();
    }
}
