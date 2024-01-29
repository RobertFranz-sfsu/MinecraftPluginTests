package com.valiantrealms.zombiesmc.util.skills;

import com.valiantrealms.zombiesmc.ZombiesMC;
import com.valiantrealms.zombiesmc.util.ConfigUtil;

public class Cooking {
    private final ZombiesMC plugin;
    private ConfigUtil con;

    public Cooking(ZombiesMC plugin){
        this.plugin = plugin;
        this.setConfig();
    }

    public void setConfig() {
        con = plugin.getSkillSettings();
    }
}
