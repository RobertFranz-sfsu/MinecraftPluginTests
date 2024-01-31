package com.valiantrealms.zombiesmc;

import org.bukkit.Bukkit;

import java.util.UUID;

public class PlayerLoader {
    private final ZombiesMC plugin;
    public PlayerLoader(ZombiesMC plugin){ this.plugin = plugin; }

    public PlayerProfile loadPlayer(UUID id){
        PlayerProfile profile = new PlayerProfile(plugin);
        profile.register(id);

        return profile;
    }
}
