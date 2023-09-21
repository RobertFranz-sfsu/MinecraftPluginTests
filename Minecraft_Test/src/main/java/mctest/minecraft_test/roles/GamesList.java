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
        //Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, this::setGameInfos, 0L, 20L);

    }

    /**
     * Initializes the Hashmap of worlds and games.  Called at server start
     * @param list
     */
    public void initGameMap(List<String> list) {
        for (String s : list) {
            this.gameMap.put(s, new SurvivalPlayer(this.plugin));
        }
    }
    public HashMap<String, SurvivalPlayer> getGameMap() {
        return this.gameMap;
    }

    /**
     * Adds a world and game to the hashmap
     * @param world
     */
    public void addWorld(String world) {
        this.gameMap.put(world, new SurvivalPlayer(this.plugin));
    }
    public void removeWorld(String world) {
        this.gameMap.remove(world);
    }

    /**
     * Set and get the list of world names
     */
    public void setGameList() {
        this.worldList = Minecraft_Test.getPlugin(Minecraft_Test.class).getConfig().getStringList("lobby-worlds");
    }
    public List<String> getGameList() {
        return this.worldList;
    }

    /**
     *
     * @return Hashmap of whether each world is in session or not
     */
    public HashMap<String, String> getGameStatus() {
        HashMap<String, String> statuses = new HashMap<>();
        for (Map.Entry<String, SurvivalPlayer> entry : this.getGameMap().entrySet()) {
            statuses.put(entry.getKey(), (entry.getValue().getPlaying() + ""));
        }
        return statuses;
    }

    /**
     *
     * @return Hashmap of the amount of people in each game
     */
    public HashMap<String, String> getGameSizes() {
        HashMap<String, String> statuses = new HashMap<>();
        for (Map.Entry<String, SurvivalPlayer> entry : this.getGameMap().entrySet()) {
            statuses.put(entry.getKey(), (entry.getValue().getStatusMap().size() + ""));
        }
        return statuses;
    }

    /**
     * Set and get all the info for each world as an Arraylist of strings
     */
    private void setGameInfos() {
        ArrayList<String> list = new ArrayList<>();
        this.getGameMap().forEach((key, value) -> {
            String str = key + ": " + (value.getPlaying() ? "Game in session" : ("Game lobby: " + value.getStatusMap().size() + "/" + value.getMaxPl()));
            list.add(str);
        });

        this.gameInfos = list;
    }
    public ArrayList<String> getGameInfos() {
        this.setGameInfos();
        return this.gameInfos;
    }

}
