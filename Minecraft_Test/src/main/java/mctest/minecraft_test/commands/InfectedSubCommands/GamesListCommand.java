package mctest.minecraft_test.commands.InfectedSubCommands;

import mctest.minecraft_test.Minecraft_Test;
import mctest.minecraft_test.roles.GamesList;
import mctest.minecraft_test.roles.SurvivalPlayer;
import mctest.minecraft_test.util.ConfigUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Skull;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class GamesListCommand {
    Minecraft_Test plugin;
    private GamesList g;
    GetCustomHead custom;

    public GamesListCommand(Minecraft_Test plugin, GamesList g){
        this.plugin = plugin;
        this.g = g;
        custom = plugin.getCustomHead();
    }

    public void gamesList(Player player){
        if(player.hasPermission("infected.infected.games") || player.hasPermission("infected.*") || player.hasPermission("infected.infected.*")){
            Inventory gamesList = Bukkit.createInventory(player, 9*6, "Infection Games Available");

            // List of all games
            ItemStack list = new ItemStack(Material.GOLD_BLOCK);
            ItemMeta meta = list.getItemMeta();
            Objects.requireNonNull(meta).setDisplayName(ChatColor.GOLD + "Worlds Available");
            meta.setLore(g.getGameInfos());
            list.setItemMeta(meta);
            gamesList.setItem(13, list);

            // Players' Stats
            if(plugin.doKeepScore()) {
                ConfigUtil scoreCon = new ConfigUtil(plugin, System.getProperty("file.separator") + "config.yml");
                String path = System.getProperty("file.separator") + "Scores" + System.getProperty("file.separator") + player.getUniqueId() + ".yml";
                ConfigUtil con = new ConfigUtil(plugin, path);

                // Personal Stats List
                ItemStack stats = new ItemStack(Material.IRON_SWORD);
                ItemMeta statsMeta = stats.getItemMeta();
                Objects.requireNonNull(statsMeta).setDisplayName(ChatColor.GOLD + "Personal Stats");
                ArrayList<String> statList = new ArrayList<>();
                statList.add("");

                statList.add(ChatColor.GRAY + "Games Played: " + con.getConfig().get("games-played"));
                int kills = con.getConfig().getInt("infected-kills") + con.getConfig().getInt("survivor-kills");
                statList.add(ChatColor.GRAY + "Total Kills: " + kills);
                statList.add(ChatColor.BLUE + "Games Played: " + con.getConfig().get("games-played"));
                statList.add(ChatColor.RED + "Kills as Infected: " + con.getConfig().get("infected-kills"));
                statList.add(ChatColor.RED + "Infected Wins: " + con.getConfig().get("infected-wins"));
                statList.add(ChatColor.GREEN + "Kills as Survivor: " + con.getConfig().get("survivor-kills"));
                statList.add(ChatColor.GREEN + "Survivor Wins: " + con.getConfig().get("survivor-wins"));
                statsMeta.setLore(statList);
                statsMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                stats.setItemMeta(statsMeta);
                gamesList.setItem(4, stats);

                // Top Survivor Wins List
                gamesList.setItem(3, createLeaderboard(4, "Most Wins as Survivor"));

                // Top Survivor Kills List
                gamesList.setItem(2, createLeaderboard(3, "Most Kills as Survivor"));

                // Top Infected Kills List
                gamesList.setItem(5, createLeaderboard(1, "Most Kills as Infected"));

                // Top Infected Wins List
                gamesList.setItem(6, createLeaderboard(2, "Most Wins as Infected"));
            }

            // Each game that is available
            int i = 18;
            for (Map.Entry<String, SurvivalPlayer> entry : g.getGameMap().entrySet()) {
                ItemStack game = new ItemStack(Material.DIAMOND_BLOCK);
                ItemMeta m = game.getItemMeta();
                Objects.requireNonNull(m).setDisplayName(entry.getKey());
                m.setLore(g.getInfoString(entry.getKey()));
                game.setItemMeta(m);
                gamesList.setItem(i++, game);
            }
            player.openInventory(gamesList);

        }else{
            player.sendMessage("You do not have permission to do this!");
        }
    }

    private ItemStack createLeaderboard(int pos, String name) {
        ArrayList<String> topList = new ArrayList<>();
        ArrayList<UUID> uid = new ArrayList<>();
        topList.add("");

        plugin.getStatsMap().entrySet().stream()
                .sorted((k1, k2) -> -k1.getValue()[pos].compareTo(k2.getValue()[pos]))
                .limit(3)
                .forEach(k -> {
                    UUID u = k.getKey();
                    uid.add(u);
                    topList.add(Bukkit.getOfflinePlayer(u).getName() + ": " + k.getValue()[pos]);
                });

//        ItemStack leaderboard = new ItemStack(custom.getCustomHead(uid.get(0)));
        ItemStack leaderboard = new ItemStack(custom.getCustomHead(uid.get(0)));
        ItemMeta leaderboardMeta = leaderboard.getItemMeta();
        assert leaderboardMeta != null;
        leaderboardMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        Objects.requireNonNull(leaderboardMeta).setDisplayName(ChatColor.GOLD + name);

        leaderboardMeta.setLore(topList);
        leaderboard.setItemMeta(leaderboardMeta);
        return leaderboard;

//        Skull skull = custom.getCustomSkull(uid.get(0));
//        SkullMeta skullMeta = leaderboard.getMetadata();
//        ItemMeta leaderboardMeta = leaderboard.getItemMeta();
//        assert leaderboardMeta != null;
//        leaderboardMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
//        Objects.requireNonNull(leaderboardMeta).setDisplayName(ChatColor.GOLD + name);
//
//        leaderboardMeta.setLore(topList);
//        leaderboard.setItemMeta(leaderboardMeta);
//        return leaderboard;
    }
}
