package mctest.minecraft_test.roles;

import mctest.minecraft_test.Minecraft_Test;
import mctest.minecraft_test.util.InventoryUtil;
import mctest.minecraft_test.util.Scoreboards;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.Objects;

public class PlayerRoles {
    private final Minecraft_Test plugin;

    public PlayerRoles(Minecraft_Test plugin) {
        this.plugin = plugin;
    }

    public void setRole(Player player, SurvivalPlayer game) {
        if (Objects.equals(game.getStatusMap().get(player.getUniqueId()), "infected")) {
            this.setAttributes(player, game.getInfSpeed(), game.getInfHealth(), game.getInfHealth());

            if (!Objects.equals(game.infConfig.getConfig().get("effects"), null)) {
                this.setEffects(player, game);
            } else {
                Bukkit.getLogger().info("No infected effects to apply.");
            }

            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&bYou are &cinfected&b!"));
//            infected.addPlayer(player);

            player.teleport(game.getInfSpawn());
//            if(plugin.getConfig().getBoolean("hide-nametags")){
//                this.hideNames(player);
//            }
        } else if (Objects.equals(game.getStatusMap().get(player.getUniqueId()), "survivor")) {
            this.setAttributes(player, game.getSurSpeed(), game.getSurHealth(), game.getSurHealth());

            if (!Objects.equals(game.surConfig.getConfig().get("effects"), null)) {
                this.setEffects(player, game);
            } else {
                Bukkit.getLogger().info("No survivor effects to apply.");
            }

            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&bYou are a &asurvivor&b!"));
//            survivors.addPlayer(player);

            player.teleport(game.getSurSpawn());
//            if(plugin.getConfig().getBoolean("hide-nametags")){
//                this.hideNames(player);
//            }
        }
    }

    private void setAttributes(Player player, Float speed, int maxHealth, int health) {
        player.setWalkSpeed(speed);
        if (this.plugin.getIs18()) {
            player.setMaxHealth(maxHealth);
        } else {
            Objects.requireNonNull(player.getAttribute(Attribute.GENERIC_MAX_HEALTH)).setBaseValue(maxHealth);
        }
        player.setHealth(health);
    }

    public void setEffects(Player player, SurvivalPlayer game) {
        if (Objects.equals(game.getStatusMap().get(player.getUniqueId()), "infected")) {
            if (Objects.equals(game.infConfig.getConfig().getConfigurationSection("effects"), null)) {
                return;
            }

            for (String x : Objects.requireNonNull(game.infConfig.getConfig().getConfigurationSection("effects")).getKeys(false)) {
                if (!Objects.equals(x, null)) {
                    String path = ("effects." + x);
                    boolean force = false;

                    int duration;
                    if (Objects.requireNonNull(game.infConfig.getConfig().getString((path + ".duration"))).equalsIgnoreCase("INFINITE")) {
                        duration = Integer.MAX_VALUE;
                        force = true;
                    } else {
                        duration = game.infConfig.getConfig().getInt(path + ".duration");
                    }
                    if (plugin.getIs18()) {
                        player.addPotionEffect(new PotionEffect(Objects.requireNonNull(PotionEffectType.getByName(x)),
                                        duration,
                                        game.infConfig.getConfig().getInt(path + ".level")),
                                force);
                    } else {
                        player.addPotionEffect(new PotionEffect(Objects.requireNonNull(PotionEffectType.getByName(x)),
                                duration,
                                game.infConfig.getConfig().getInt(path + ".level")));
                    }
                }
            }
        } else if (Objects.equals(game.getStatusMap().get(player.getUniqueId()), "survivor")) {
            if (Objects.equals(game.surConfig.getConfig().getConfigurationSection("effects"), null)) {
                return;
            }

            for (String x : Objects.requireNonNull(game.surConfig.getConfig().getConfigurationSection("effects")).getKeys(false)) {
                if (!Objects.equals(x, null)) {
                    String path = ("effects." + x);
                    boolean force = false;

                    int duration;
                    if (Objects.requireNonNull(game.surConfig.getConfig().getString((path + ".duration"))).equalsIgnoreCase("INFINITE")) {
                        duration = Integer.MAX_VALUE;
                        force = true;
                    } else {
                        duration = game.surConfig.getConfig().getInt(path + ".duration");
                    }
                    if (plugin.getIs18()) {
                        player.addPotionEffect(new PotionEffect(Objects.requireNonNull(PotionEffectType.getByName(x)),
                                        duration,
                                        game.surConfig.getConfig().getInt(path + ".level")),
                                force);
                    } else {
                        player.addPotionEffect(new PotionEffect(Objects.requireNonNull(PotionEffectType.getByName(x)),
                                duration,
                                game.infConfig.getConfig().getInt(path + ".level")));
                    }
                }
            }
        }
    }

    public void removeEffects(Player player) {
        ArrayList<PotionEffect> pe = new ArrayList<>(player.getActivePotionEffects());

        for (PotionEffect x : pe) {
            player.removePotionEffect(x.getType());
        }
    }

    public void setNotPlaying(Player player, SurvivalPlayer game, InventoryUtil invUtil) {
        Bukkit.getLogger().info(player.getName() + " is no longer playing!");

        player.setFoodLevel(20);
        this.setAttributes(player, .2f, 20, 20);
        Scoreboards scoreboard = new Scoreboards(plugin, game);
        scoreboard.removeBoard(player);
        game.getStatusMap().remove(player.getUniqueId());
        this.plugin.getIsPlayingSet().remove(player.getUniqueId());
        game.gethealthMap().remove(player.getUniqueId());

//        if(statusMap.get(player.getUniqueId()).equalsIgnoreCase("infected")){
//            infected.removePlayer(player);
//        }else if(statusMap.get(player.getUniqueId()).equalsIgnoreCase("survivor")){
//            survivors.removePlayer(player);
//        }

//        if(plugin.getConfig().getBoolean("hide-nametags")){
//            this.showNames(player);
//        }

        if (Objects.requireNonNull(invUtil.getPreviousInventory()).containsKey(player.getUniqueId())) {
            invUtil.getPreviousInventory().remove(player.getUniqueId());
        }

        removeEffects(player);
        player.sendMessage("No longer playing");
    }

    public void setUnassigned(Player player, SurvivalPlayer game) {
        //plugin.getGameIDMap().put(player.getUniqueId(), game.getGameID());
        if (game.getAllowedWorlds().contains(player.getWorld().getName())) {
            try {
                game.getStatusMap().forEach((key, value) -> Bukkit.getLogger().info(key + " " + value));
                game.getStatusMap().put(player.getUniqueId(), "unassigned");
                game.gethealthMap().put(player.getUniqueId(), "alive");
                game.previousWorlds.put(player.getUniqueId(), player.getWorld().getName());

//                if(plugin.getConfig().getBoolean("hide-nametags")){
//                    this.showNames(player);
//                }

                this.removeEffects(player);
            } catch (Exception e) {
                Bukkit.getLogger().warning("Something went wrong.");
                e.printStackTrace();
            }
        }
    }
}
