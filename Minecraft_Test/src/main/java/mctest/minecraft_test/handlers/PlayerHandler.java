package mctest.minecraft_test.handlers;

import mctest.minecraft_test.Minecraft_Test;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.ArrayList;
import java.util.UUID;

public class PlayerHandler implements Listener {
    private ArrayList<UUID> players = new ArrayList<>();
    public PlayerHandler(Minecraft_Test plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public ArrayList<UUID> getPlayers(){
        return this.players;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        // TODO
        //  Change when implementing MV
        Player p = event.getPlayer();
        players.add(p.getUniqueId());
        Bukkit.getLogger().info(players.toString());
    }

    @EventHandler
    public void onPlayerDisconnect(PlayerQuitEvent event){
        // TODO
        //  Change when implementing MV
        Player p = event.getPlayer();
        players.removeIf(pl -> (pl.equals(p.getUniqueId())));
    }
}
