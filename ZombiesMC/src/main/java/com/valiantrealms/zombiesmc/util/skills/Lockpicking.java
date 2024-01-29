package com.valiantrealms.zombiesmc.util.skills;

import com.valiantrealms.zombiesmc.ZombiesMC;
import com.valiantrealms.zombiesmc.util.ConfigUtil;

public class Lockpicking {
    private final ZombiesMC plugin;
    private ConfigUtil con;

    public Lockpicking(ZombiesMC plugin){
        this.plugin = plugin;
        this.setConfig();
    }

    public void setConfig() {
        con = plugin.getSkillSettings();
    }
}
