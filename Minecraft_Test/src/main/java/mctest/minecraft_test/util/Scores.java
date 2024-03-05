package mctest.minecraft_test.util;

import mctest.minecraft_test.Minecraft_Test;
import mctest.minecraft_test.roles.SurvivalPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Objects;
import java.util.UUID;

@SuppressWarnings("ALL")
public class Scores {
    private final Minecraft_Test plugin;
    private final SurvivalPlayer game;

    public Scores(Minecraft_Test plugin, SurvivalPlayer game) {
        this.plugin = plugin;
        this.game = game;
    }

    public void setScores(UUID player) {
        ConfigUtil con = new ConfigUtil(plugin, System.getProperty("file.separator") + "Scores" + System.getProperty("file.separator") + player + ".yml");
        Integer[] nArr = plugin.getStatsMap().get(player);

//        for(String x : plugin.getStatsMap().keySet()){
//            Bukkit.getLogger().severe("NAME: " + x);
//            if(player!= null){
//                Bukkit.getLogger().severe("STUFF 1: " + plugin.getStatsMap().get(Objects.requireNonNull(Bukkit.getPlayer(player)).getName()).toString());
//            }
//            Bukkit.getLogger().severe("STUFF 2: " + Arrays.toString(plugin.getStatsMap().get(x)));
//        }

        if (plugin.doSurvivorKills() && game.getSurvivorKills().containsKey(player)) {
            int kills = con.getConfig().getInt("survivor-kills");

            if (!Objects.equals(game.getSurvivorKills().get(player), null)) {
                kills += game.getSurvivorKills().getOrDefault(player, 0);
                con.getConfig().set("survivor-kills", kills);
                nArr[3] += game.getSurvivorKills().getOrDefault(player, 0);
            }
        }

        if (plugin.doInfectedKills() && game.getInfectedKills().containsKey(player)) {
            int kills = con.getConfig().getInt("infected-kills");

            if (!Objects.equals(game.getInfectedKills().get(player), null)) {
                kills += game.getInfectedKills().getOrDefault(player, 0);
                con.getConfig().set("infected-kills", kills);
                nArr[1] += game.getInfectedKills().getOrDefault(player, 0);
            }
        }

        if (plugin.doGamesPlayed()) {
            int games = con.getConfig().getInt("games-played");
            games++;
            con.getConfig().set("games-played", games);
            nArr[0]++;
        }

        con.save();
        plugin.getStatsMap().put(Objects.requireNonNull(Bukkit.getPlayer(player)).getUniqueId(), nArr);
    }

    public void setGamesWon(UUID player, String team) {
        ConfigUtil con = new ConfigUtil(plugin, "/Scores/" + player + ".yml");
        Integer[] nArr = plugin.getStatsMap().get(Objects.requireNonNull(Bukkit.getPlayer(player)).getUniqueId());

        if (plugin.doInfectedGamesWon() && team.equalsIgnoreCase("infected")) {
            int wins = con.getConfig().getInt("infected-wins");
            wins++;
            con.getConfig().set("infected-wins", wins);
            nArr[2]++;
        }

        if (plugin.doSurvivorGamesWon() && team.equalsIgnoreCase("survivor")) {
            int wins = con.getConfig().getInt("survivor-wins");
            wins++;
            con.getConfig().set("survivor-wins", wins);
            nArr[4]++;
        }

        con.save();
        plugin.getStatsMap().put(Objects.requireNonNull(Bukkit.getPlayer(player)).getUniqueId(), nArr);
    }
    public void giveRewards(Player player, String winner) {
        double reward;

        if (winner.equals("survivor")) {
            reward = game.getSurMoneyReward();
        } else if (winner.equals("infected")) {
            reward = game.getInfMoneyReward();
        } else {
            reward = 0;
        }

        if (plugin.getEcon() != null) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&fYou have been awarded &a" +
                    plugin.getConfig().getString("currency-symbol") + reward + "&f for winning!"));
            plugin.getEcon().depositPlayer(player, reward);
        }

        try {
            if (winner.equals("survivor") && !game.getSurCommandRewards().isEmpty()) {
                for (String x : game.getSurCommandRewards()) {
                    Bukkit.getLogger().info(x.replaceAll("PLAYER_NAME", Objects.requireNonNull(Bukkit.getPlayer(player.getUniqueId())).getName()));
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), x.replaceAll("PLAYER_NAME", Objects.requireNonNull(Bukkit.getPlayer(player.getUniqueId())).getName()));
                }
            } else if (winner.equals("infected") && !game.getInfCommandRewards().isEmpty()) {
                for (String x : game.getInfCommandRewards()) {
                    Bukkit.getLogger().info(x.replaceAll("PLAYER_NAME", Objects.requireNonNull(Bukkit.getPlayer(player.getUniqueId())).getName()));
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), x.replaceAll("PLAYER_NAME", Objects.requireNonNull(Bukkit.getPlayer(player.getUniqueId())).getName()));
                }
            }
        } catch (Exception e) {
            Bukkit.getLogger().warning("Something went wrong.");
            e.printStackTrace();
        }
    }
}
