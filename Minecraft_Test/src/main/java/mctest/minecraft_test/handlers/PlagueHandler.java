package mctest.minecraft_test.handlers;

import mctest.minecraft_test.Minecraft_Test;
import mctest.minecraft_test.util.DelayedTask;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.Collection;

public class PlagueHandler implements Listener {
    public PlagueHandler(Minecraft_Test plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        Location loc = player.getLocation();
        Collection<Entity> nearbyPlayers = loc.getBlock().getWorld().getNearbyEntities(loc, 20, 20, 20, (e -> e instanceof Player));
        Bukkit.getLogger().info(player.getName() + " has died");

        for (Entity p : nearbyPlayers) {
            Bukkit.getLogger().info(p.getName() + " was nearby");
            p.sendMessage("NEARBY PLAYER DIED!");
            if (p instanceof Player) {
                Player pl = (Player) p;
                //Makes sure to not trigger for the person who died
                if (pl != player) {
                    pl.playNote(player.getLocation(), Instrument.DRAGON, Note.natural(1, Note.Tone.A));
                    new DelayedTask(() -> {
                        pl.setHealth(1);
                    }, 20 * 60);
                }
            }

        }

    }

}
