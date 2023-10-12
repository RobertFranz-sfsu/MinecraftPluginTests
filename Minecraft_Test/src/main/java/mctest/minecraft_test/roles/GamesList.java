package mctest.minecraft_test.roles;

import mctest.minecraft_test.Minecraft_Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("FieldMayBeFinal")
public class GamesList {
    private HashMap<String, SurvivalPlayer> gameMap = new HashMap<>();
    private List<String> worldList;
    private ArrayList<String> gameInfos;
    private Minecraft_Test plugin;
    private HashMap<String, HashMap<Integer, SurvivalPlayer>> multiGameMap = new HashMap<>();
    private HashMap<String, ArrayList<SurvivalPlayer>> arrayListGameMap = new HashMap<>();

    private HashMap<Integer, String> idWorldMap = new HashMap<>();

    public GamesList(Minecraft_Test plugin) {
        this.plugin = plugin;
        this.setGameList();
        this.initGameMap(this.getGameList());
        //Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, this::setGameInfos, 0L, 20L);

    }

    /**
     * @param list
     * Initializes the Hashmap of worlds and games.  Called at server start
     */
    public void initGameMap(List<String> list) {
        for (String s : list) {

            SurvivalPlayer game = new SurvivalPlayer(this.plugin);

            // Original map
            this.gameMap.put(s, game);
            game.setCurrentWorld(s);

//            // Hashmap that has key: world name, value: hashmap of game id and game
//            HashMap<Integer, SurvivalPlayer> id = new HashMap<>();
//            id.put(game.getGameID(), game);
//            this.multiGameMap.put(s, id);
//
//            // Hashmap of key: world name, key: arraylist of all games
//            ArrayList<SurvivalPlayer> aL = new ArrayList<>();
//            aL.add(game);
//            this.arrayListGameMap.put(s, aL);
        }
    }
    public HashMap<String, SurvivalPlayer> getGameMap() {
        return this.gameMap;
    }

    /**
     * @param world
     * Adds a world and game to the hashmap
     */
    public void addWorld(String world) {
        this.gameMap.put(world, new SurvivalPlayer(this.plugin));
    }
    public void removeWorld(String world) {
        this.gameMap.remove(world);
    }

    /**
     * Set and get the list of allowed world names
     */
    public void setGameList() {
        this.worldList = Minecraft_Test.getPlugin(Minecraft_Test.class).getConfig().getStringList("allowed-worlds");
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

    public List<String> getInfoString(String world) {
        List<String> l = new ArrayList<>();
        l.add((this.getGameMap().get(world).getPlaying() ? "Game in session" : ("Game in lobby: " + this.getGameMap().get(world).getStatusMap().size() + "/" + this.getGameMap().get(world).getMaxPl()))) ;
        return l;
    }

    public HashMap<String, HashMap<Integer, SurvivalPlayer>> getMultiGameMap() {
        return this.multiGameMap;
    }

    /**
     * Testing
     */
    public void reloadGameMap(){
        initGameMap(this.getGameList());
    }

}
