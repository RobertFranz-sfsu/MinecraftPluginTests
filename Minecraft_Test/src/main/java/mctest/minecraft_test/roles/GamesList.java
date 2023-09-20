package mctest.minecraft_test.roles;

import mctest.minecraft_test.Minecraft_Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GamesList {
    private HashMap<String, SurvivalPlayer> gameMap = new HashMap<>();
    private List<String> worldList;
    private Minecraft_Test plugin;

    public GamesList(Minecraft_Test plugin) {
        this.plugin = plugin;
        this.setGameList();
        setGameMap(this.getGameList());
    }

    public void setGameMap(List<String> list) {
        for (String s : list) {
            this.gameMap.put(s, new SurvivalPlayer(this.plugin));
        }
    }
    public HashMap<String, SurvivalPlayer> getGameMap() {
        return this.gameMap;
    }
    public void setGameList() {
        this.worldList = Minecraft_Test.getPlugin(Minecraft_Test.class).getConfig().getStringList("lobby-worlds");
    }
    public List<String> getGameList() {
        return this.worldList;
    }
    public HashMap<String, String> getGameStatus() {
        HashMap<String, String> statuses = new HashMap<>();
        for (Map.Entry<String, SurvivalPlayer> entry : this.getGameMap().entrySet()) {
            statuses.put(entry.getKey(), (entry.getValue().getPlaying() + ""));
        }
        return statuses;
    }
    public HashMap<String, String> getGameSizes() {
        HashMap<String, String> statuses = new HashMap<>();
        for (Map.Entry<String, SurvivalPlayer> entry : this.getGameMap().entrySet()) {
            statuses.put(entry.getKey(), (entry.getValue().getStatusMap().size() + ""));
        }
        return statuses;
    }
}
