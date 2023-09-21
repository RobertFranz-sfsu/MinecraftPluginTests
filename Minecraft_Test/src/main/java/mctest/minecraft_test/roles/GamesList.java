package mctest.minecraft_test.roles;

import mctest.minecraft_test.Minecraft_Test;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GamesList {
    private HashMap<String, SurvivalPlayer> gameMap = new HashMap<>();
    private List<String> worldList;
    private ArrayList<String> gameInfos;
    private Minecraft_Test plugin;

    public GamesList(Minecraft_Test plugin) {
        this.plugin = plugin;
        this.setGameList();
        this.initGameMap(this.getGameList());
        Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, this::setGameInfos, 0L, 20L);

    }

    public void initGameMap(List<String> list) {
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
    private void setGameInfos() {
        ArrayList<String> list = new ArrayList<>();
        this.getGameMap().forEach((key, value) -> {
            String str = key + ": " + (value.getPlaying() ? "Game in session" : ("Game lobby: " + value.getStatusMap().size() + "/" + value.getMaxPl()));
            list.add(str);
        });

        this.gameInfos = list;
    }
    public ArrayList<String> getGameInfos() {
        Bukkit.getLogger().info(this.gameInfos + "");
        return this.gameInfos;
    }
}
