package com.valiantrealms.zombiesmc.util;

import com.valiantrealms.zombiesmc.ZombiesMC;
import jdk.internal.net.http.common.Pair;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class Experience {
    private ConcurrentHashMap<UUID, Pair<double[], double[]>> playerList = new ConcurrentHashMap<>();
    private ConcurrentHashMap<UUID, double[]> xp = new ConcurrentHashMap<>();
    private ConcurrentHashMap<UUID, double[]> xpForLevelUp = new ConcurrentHashMap<>();
    private ConfigUtil con;
    private final ZombiesMC plugin;

    public Experience(ZombiesMC plugin) {
        this.plugin = plugin;
    }

    public void addPlayer(UUID id){
        Pair<double[], double[]> pair = new Pair<>(plugin.getPlayers().get(id).getSkillXP(), plugin.getPlayers().get(id).getSkillXPNeeded());
        playerList.put(id, pair);
    }

    public void removePlayer(UUID id){
        playerList.remove(id);
    }

    public void setConfig() { this.con = plugin.getPlayerXPSettings(); }

    // Getters
    public ConcurrentHashMap<UUID, Pair<double[], double[]>> getPlayerList() { return this.playerList; }
    public ConcurrentHashMap<UUID, double[]> getXp() { return this.xp; }
    public ConcurrentHashMap<UUID, double[]> getxpForLevelUp() { return this.xpForLevelUp; }
    public double getCurrentXp(UUID id, int index){ return playerList.get(id).first[index]; }
    public double getSpecificXpForLevelUp(UUID id, int index){ return playerList.get(id).second[index]; }
}
