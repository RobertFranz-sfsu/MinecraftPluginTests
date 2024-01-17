package mctest.minecraft_test.util;

import mctest.minecraft_test.Minecraft_Test;
import mctest.minecraft_test.roles.GamesList;
import mctest.minecraft_test.roles.SurvivalPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

import java.util.Objects;

public class Scoreboards {
    /**
     * Scoreboards
     * setBoard is the in-game scoreboard.
     * waitBoard is the scoreboard for while in game lobby.
     * removeBoard removes the current scoreboard.
     */
//    ScoreboardManager manager = Bukkit.getScoreboardManager();
//    Scoreboard scoreboard = Objects.requireNonNull(manager).getNewScoreboard();

    private final Minecraft_Test plugin;
    private final SurvivalPlayer game;
    public Scoreboards(Minecraft_Test plugin, SurvivalPlayer game) {
        this.plugin = plugin;
        this.game = game;

    }

    public void setBoard(Player player) {
        if (!game.getStatusMap().containsKey(player.getUniqueId())) {
            return;
        }
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        Scoreboard scoreboard = Objects.requireNonNull(manager).getNewScoreboard();

        Objective objective;
        if(plugin.getIs18()){
            objective = scoreboard.registerNewObjective("Game Status", "dummy");
            objective.setDisplayName(ChatColor.GOLD + "Survival Status");
        }else{
            objective = scoreboard.registerNewObjective("Game Status", "", ChatColor.GOLD + "Survival Status");
        }

        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        Score newLine1 = objective.getScore("");
        newLine1.setScore(6);

        String minutes = String.valueOf(game.getTimer() / 60);
        String seconds = ((game.getTimer()%60 < 10) ? "0" : "") + game.getTimer()%60 ;
        Score timer = objective.getScore("Time left in game: " + minutes + ":" + seconds);
        timer.setScore(4);
        Score newLine2 = objective.getScore("");
        newLine2.setScore(3);

        Score survivorScore = objective.getScore(ChatColor.GREEN + "Survivors: " + game.getSurvivorCnt());
        survivorScore.setScore(2);
        Score infectedScore = objective.getScore(ChatColor.RED + "Infected:   " + game.getInfectedCnt());
        infectedScore.setScore(1);

        player.setScoreboard(scoreboard);
    }

    public void respawnBoard(Player player, int sec) {
        if (!game.getStatusMap().containsKey(player.getUniqueId())) {
            return;
        }

        ScoreboardManager manager = Bukkit.getScoreboardManager();
        Scoreboard scoreboard = Objects.requireNonNull(manager).getNewScoreboard();

        Objective objective;
        if(plugin.getIs18()){
            objective = scoreboard.registerNewObjective("Game Status", "dummy");
            objective.setDisplayName(ChatColor.GOLD + "Survival Status");
        }else{
            objective = scoreboard.registerNewObjective("Game Status", "", ChatColor.GOLD + "Survival Status");
        }

//        Bukkit.getLogger().severe("2 INFECTED: " + infected.getPlayers());
//        Bukkit.getLogger().severe("2 SURVIVORS: " + survivors.getPlayers());

        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        Score newLine1 = objective.getScore("");
        newLine1.setScore(6);

        String ellipsis = ".";
        ellipsis =  new String(new char[3-(game.getTimer() % 4)]).replace("\0", ellipsis);
        Score wait = objective.getScore("Respawning in: " + sec + ellipsis);
        wait.setScore(5);

        String minutes = String.valueOf(game.getTimer() / 60);
        String seconds = ((game.getTimer()%60 < 10) ? "0" : "") + game.getTimer()%60 ;
        Score timer = objective.getScore("Time left in game: " + minutes + ":" + seconds);
        timer.setScore(4);
        Score newLine2 = objective.getScore("");
        newLine2.setScore(3);

        Score survivorScore = objective.getScore(ChatColor.GREEN + "Survivors: " + game.getSurvivorCnt());
        survivorScore.setScore(2);
        Score infectedScore = objective.getScore(ChatColor.RED + "Infected:   " + game.getInfectedCnt());
        infectedScore.setScore(1);

        player.setScoreboard(scoreboard);
    }
    public void waitBoard(Player player) {
        if (!game.getStatusMap().containsKey(player.getUniqueId())) {
            return;
        }
        ScoreboardManager manager1 = Bukkit.getScoreboardManager();
        Scoreboard scoreboard = Objects.requireNonNull(manager1).getNewScoreboard();

        Objective objective;
        if(plugin.getIs18()){
            objective = scoreboard.registerNewObjective("Game Status", "dummy");
            objective.setDisplayName(ChatColor.GOLD + "Waiting on players");
        }else{
            objective = scoreboard.registerNewObjective("Game Status", "", ChatColor.GOLD + "Waiting on players");
        }
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        Score newLine1 = objective.getScore("");
        newLine1.setScore(5);
        if (game.getStatusMap().size() >= game.getMinPl()) {
            String minutes = String.valueOf(game.getTimer() / 60);
            String seconds = ((game.getTimer()%60 < 10) ? "0" : "") + game.getTimer()%60 ;
            Score timer = objective.getScore("Time left: " + minutes + ":" + seconds);
            timer.setScore(4);
        } else {
            Score waiting = objective.getScore("Waiting for at least " + (game.getMinPl() - game.getStatusMap().size()) + " more player(s).");
            waiting.setScore(4);
        }
        Score amount = objective.getScore("Players: " + game.getStatusMap().size() + " / " + game.getMaxPl());
        amount.setScore(3);
        Score min = objective.getScore("Minimum required to start: " + game.getMinPl());
        min.setScore(2);

        player.setScoreboard(scoreboard);
    }

    public void countdownBoard(Player player, int sec) {
        if (!game.getStatusMap().containsKey(player.getUniqueId())) {
            return;
        }
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        Scoreboard scoreboard = manager.getNewScoreboard();

        Objective objective = scoreboard.registerNewObjective("Countdown", "dummy");
        String dName = (Objects.equals(game.getStatusMap().get(player.getUniqueId()), "survivor")) ? (ChatColor.GREEN + "Survivor") : (ChatColor.RED + "Infected");
        objective.setDisplayName(dName);
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        Score newLine1 = objective.getScore("");
        newLine1.setScore(6);

        Score timer  = objective.getScore("Starting in: " + sec);
        timer.setScore(5);

        String tip = (Objects.equals(game.getStatusMap().get(player.getUniqueId()), "survivor")) ? "Prepare to survive!" : "Prepare to hunt!";
        Score tips = objective.getScore(tip);
        tips.setScore(4);

        player.setScoreboard(scoreboard);
    }

    public void removeBoard(Player player) {
        if (!game.getStatusMap().containsKey(player.getUniqueId())) {
            return;
        }
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        Scoreboard scoreboard = Objects.requireNonNull(manager).getNewScoreboard();
//        infected.unregister();
//        survivors.unregister();
        player.setScoreboard(scoreboard);

    }
}
