package com.valiantrealms.zombiesmc;

import com.valiantrealms.zombiesmc.util.ConfigUtil;
import org.bukkit.Bukkit;
import org.bukkit.event.player.PlayerPortalEvent;

import java.util.UUID;

public class PlayerLoader {
    private final ZombiesMC plugin;
    public PlayerLoader(ZombiesMC plugin){ this.plugin = plugin; }

    public PlayerProfile loadPlayer(UUID id){
        PlayerProfile profile = new PlayerProfile(plugin);
        profile.register(id);

        /**
         * String UUID
         * int health
         * int stamina
         * double[] skills
         */

        return profile;
    }

}
