package mctest.minecraft_test.listeners;

import mctest.minecraft_test.Minecraft_Test;
import mctest.minecraft_test.roles.PlayerRoles;
import mctest.minecraft_test.roles.SurvivalPlayer;
import mctest.minecraft_test.util.CountdownTimer;
import mctest.minecraft_test.util.InventoryUtil;
import mctest.minecraft_test.util.Scoreboards;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.projectiles.ProjectileSource;

import java.util.Objects;

public class GameListener implements Listener {
    private final Minecraft_Test plugin;
    private final SurvivalPlayer game;
    private final Scoreboards scoreboard;
    private final PlayerRoles role;
    private final InventoryUtil invUtil;

    public GameListener(Minecraft_Test plugin, SurvivalPlayer game, Scoreboards scoreboard, PlayerRoles roles) {
        this.plugin = plugin;
        this.game = game;
        this.scoreboard = scoreboard;
        this.role = roles;
        invUtil = new InventoryUtil(plugin);
    }
    /**
     * Event Handlers
     * @param event
     *
     * onPlayerDeath makes sure players' items don't drop on the ground.
     * onPlayerRespawn sets players who have died to infected.
     * onPlayerDisconnect removes players who have disconnected from the game.
     * onPlayerAttack cancels friendly fire.
     */
    @EventHandler
    private void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        if (!game.getStatusMap().containsKey(player.getUniqueId())) {
            return;
        }
        Bukkit.getLogger().info("Player:  " + player.getName() + "  has died ");
        event.getDrops().clear();
    }
    @EventHandler
    private void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        if (!game.getStatusMap().containsKey(player.getUniqueId())) {
            return;
        }
        Bukkit.getLogger().info(player.getName() + " set as infected");
        game.getStatusMap().put(player.getUniqueId(), "infected");
        role.setRole(player, game);
    }
    @EventHandler
    private void onPlayerDisconnect(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (!game.getStatusMap().containsKey(player.getUniqueId())) {
            return;
        }

        Bukkit.getLogger().info("Player:  " + player.getName() + "  has disconnected");
        role.setNotPlaying(player, game);
    }

    @EventHandler
    private void onPlayerDamage(EntityDamageByEntityEvent event) {
        if(game.getPlaying()){
            try {
                Entity attacker = event.getDamager();
                Entity damaged = event.getEntity();
                Player player = (Player) damaged;

                // If shooter and target are on same team, cancel damage
                if((event.getCause().equals(EntityDamageEvent.DamageCause.PROJECTILE))){
                    ProjectileSource attack = ((Projectile) event.getDamager()).getShooter();
                    Entity victim = event.getEntity();

                    Bukkit.getLogger().severe("PLAYER: " + player.getHealth() + ", DAMAGE: " + event.getDamage());

                    if (!game.getStatusMap().containsKey(Objects.requireNonNull(((Player)attack)).getUniqueId()) && !game.getStatusMap().containsKey(victim.getUniqueId())) {
                        return;
                    }

                    if((victim instanceof Player)){
                        if(Objects.equals(game.getStatusMap().get(((Player) attack).getUniqueId()), game.getStatusMap().get(victim.getUniqueId()))){
                            event.setCancelled(true);
                        }else if(event.getDamage() >= player.getHealth()){
                            event.setCancelled(true);

                            if(plugin.doKeepScore()){
                                Player killer = (Player) attacker;

                                if(plugin.doInfectedKills() && Objects.equals(game.getStatusMap().get(killer.getUniqueId()), "infected")){
                                    int k = game.infectedKills.get(killer.getUniqueId()) + 1;
                                    game.infectedKills.put(killer.getUniqueId(), k);
                                }

                                if(plugin.doSurvivorKills() && Objects.equals(game.getStatusMap().get(killer.getUniqueId()), "survivor")){
                                    int k = game.survivorKills.get(killer.getUniqueId()) + 1;
                                    game.survivorKills.put(killer.getUniqueId(), k);
                                }
                            }

                            Bukkit.getLogger().info("Player:  " + player.getName() + "  has died ");
                            if(Objects.equals(game.getStatusMap().get(player.getUniqueId()).toLowerCase(), "survivor")){
                                game.getStatusMap().put(player.getUniqueId(), "infected");
                                invUtil.clearInventory(player);
                            }

                            role.removeEffects(player);
                            role.setRole(player, game);

                            player.teleport(game.getInfSpawn());

                            new CountdownTimer(this.plugin, game.getRespawnTime(),
                                    // What happens at the start
                                    () -> {
                                        game.gethealthMap().put(player.getUniqueId(), "dead");
                                        player.setWalkSpeed(0);
                                    },
                                    // What happens at the end
                                    () -> {
                                        if (game.getPlaying()) {
                                            game.gethealthMap().put(player.getUniqueId(), "alive");
                                            player.setWalkSpeed(game.getInfSpeed());
                                        }
                                    },
                                    // What happens during each tick
                                    (t) -> {
                                        if (game.getPlaying()) {
                                            scoreboard.respawnBoard(player, t.getSecondsLeft());
                                        }
                                    }).scheduleTimer();
                        }
                    }
                }
                // If on same team, cancel damage
                else if (Objects.equals(game.getStatusMap().get(attacker.getUniqueId()), game.getStatusMap().get(damaged.getUniqueId())) && game.getPlaying()) {
                    event.setCancelled(true);
                }

                else if(event.getDamage() >= player.getHealth()){
                    event.setCancelled(true);

                    if(plugin.doKeepScore()){
                        Player killer = (Player) attacker;

                        if(plugin.doInfectedKills() && Objects.equals(game.getStatusMap().get(killer.getUniqueId()), "infected")){
                            int k = game.infectedKills.get(killer.getUniqueId()) + 1;
                            game.infectedKills.put(killer.getUniqueId(), k);
                        }

                        if(plugin.doSurvivorKills() && Objects.equals(game.getStatusMap().get(killer.getUniqueId()), "survivor")){
                            int k = game.survivorKills.get(killer.getUniqueId()) + 1;
                            game.survivorKills.put(killer.getUniqueId(), k);
                        }
                    }

                    Bukkit.getLogger().info("Player:  " + player.getName() + "  has died ");
                    if(Objects.equals(game.getStatusMap().get(player.getUniqueId()).toLowerCase(), "survivor")){
                        game.getStatusMap().put(player.getUniqueId(), "infected");
                        invUtil.clearInventory(player);
                    }

                    role.removeEffects(player);
                    role.setRole(player, game);

                    player.teleport(game.getInfSpawn());

                    new CountdownTimer(this.plugin, game.getRespawnTime(),
                            // What happens at the start
                            () -> {
                                game.gethealthMap().put(player.getUniqueId(), "dead");
                                player.setWalkSpeed(0);
                            },
                            // What happens at the end
                            () -> {
                                if (game.getPlaying()) {
                                    game.gethealthMap().put(player.getUniqueId(), "alive");
                                    player.setWalkSpeed(game.getInfSpeed());
                                }
                            },
                            // What happens during each tick
                            (t) -> {
                                if (game.getPlaying()) {
                                    scoreboard.respawnBoard(player, t.getSecondsLeft());
                                }
                            }).scheduleTimer();
                }
            } catch (Exception e) {
//            Bukkit.getLogger().info("Mob attacking mob");
            }
        }
    }

    @EventHandler
    private void disableMovement(PlayerMoveEvent event) {
        if (!game.getStatusMap().containsKey(event.getPlayer().getUniqueId())) {
            return;
        }
        if (Objects.equals(game.gethealthMap().get(event.getPlayer().getUniqueId()), "dead")) {
            if (Objects.requireNonNull(event.getTo()).getY() > event.getFrom().getY()) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    private void inventoryOpen(InventoryOpenEvent event){
        if(game.getPlaying()){
            Player player = (Player) event.getPlayer();
            role.setEffects(player, game);
        }
    }
}
