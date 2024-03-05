package mctest.minecraft_test.commands;

import mctest.minecraft_test.Minecraft_Test;
import mctest.minecraft_test.roles.GamesList;
import mctest.minecraft_test.roles.PlayerRoles;
import mctest.minecraft_test.roles.SurvivalPlayer;
import mctest.minecraft_test.util.ConfigUtil;
import mctest.minecraft_test.util.InventoryUtil;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@SuppressWarnings({"FieldMayBeFinal", "NullableProblems"})
public class Menu implements Listener, CommandExecutor {
    private String invName = "Loadouts";
    private Minecraft_Test plugin = Minecraft_Test.getPlugin(Minecraft_Test.class);

    ConfigUtil con = plugin.getLoadoutCon();
    private GamesList g;
    private Player player;
    private PlayerRoles roles;

    public Menu(Minecraft_Test plugin,  GamesList g) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
        this.plugin = plugin;
        this.g = g;
        // Testing: Might not work if player isnt gotten in time... currently getting from sender in onCommand
        // additionally... need to see if its getting the player's game at command call or plugin start
        this.roles = new PlayerRoles(plugin);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!event.getView().getTitle().equals(invName)) {
            return;
        }

        try {
            // Might need to uncomment if doesnt work
            this.player = (Player) event.getWhoClicked();

            int slot = event.getSlot();
            SurvivalPlayer game = g.getGameMap().get(this.player.getWorld().getName());
            InventoryUtil invUtil = game.getInvUtil();
            if(slot == 53){
                roles.setNotPlaying(player, game, invUtil);
                event.setCancelled(true);
            } else if (slot == 45) {
                g.getGameMap().get(player.getWorld().getName()).gameInit();
                event.setCancelled(true);
            } else if (slot == 46) {
                roles.setUnassigned(player, game);
                event.setCancelled(true);
            }

            if(slot < 45){
                ItemStack loadout = event.getCurrentItem();
                String name = Objects.requireNonNull(Objects.requireNonNull(loadout).getItemMeta()).getDisplayName().replace('§', '&');

                if(plugin.isLoadoutPrices()){
                    if((plugin.getEcon().getBalance(player) < con.getConfig().getDouble(name + ".price") && !player.isOp())){
                        player.sendMessage(ChatColor.translateAlternateColorCodes ('&',
                                "&4You do not have enough money for this loadout!"));
                        event.setCancelled(true);
                    }else{
                        plugin.getEcon().withdrawPlayer(player, con.getConfig().getDouble(name + ".price"));
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "loadout give " + Objects.requireNonNull(Bukkit.getPlayer(player.getUniqueId())).getName() + " " + name);
                        player.sendMessage(ChatColor.translateAlternateColorCodes ('&',
                                "&a" + con.getConfig().getDouble(name + ".price") + " &fhas been removed from your balance."));
                        event.setCancelled(true);
                        player.closeInventory();
                    }
                }else{
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "loadout give " + Objects.requireNonNull(Bukkit.getPlayer(player.getUniqueId())).getName() + " " + name);
                    event.setCancelled(true);
                    player.closeInventory();
                }

//                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "loadout give " + Objects.requireNonNull(Bukkit.getPlayer(player.getUniqueId())).getName() + " " + name);
            }
//            event.setCancelled(true);
//            player.closeInventory();
        } catch (Exception e) {
            //Bukkit.getLogger().info("Selected empty slot");
        }

    }

    // Removing the warning from the passed in objects.
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String [] args) {
        con = plugin.getLoadoutCon();

        if (!(sender instanceof Player)) {
            sender.sendMessage(("Must be a player to run this command!"));
            return false;
        }

        this.player = (Player) sender;
        if(!player.isOp() && (!player.hasPermission("*") || !player.hasPermission("infected.*") || !player.hasPermission("infected.infected.*")) && !plugin.getIsPlayingSet().contains(player.getUniqueId())){
            sender.sendMessage("Must be in a game to run this command!");
            return false;
        }

        Inventory inv = Bukkit.createInventory(player, 9 * 6, invName);
        int index = 0;

        if(sender.isOp()){
            for(String keys : con.getConfig().getKeys(false)){
                inv.setItem(index, Objects.requireNonNull(con.getConfig().getConfigurationSection(keys)).getItemStack("placeholder"));
                index++;
            }

            inv.setItem(45, getItem(new ItemStack(Material.DIAMOND_BLOCK), "&9START GAME", "&aClick to start the game"));
        }else{
            for(String keys : con.getConfig().getKeys(false)){
                if(g.getGameMap().get(player.getWorld().getName()).getPlaying() && plugin.getConfig().getBoolean("use-custom-loadout-perms") && !Objects.equals(con.getConfig().get(keys + ".permission"), null) && index < 45){
                    if(player.hasPermission("infected.loadout." + con.getConfig().getString(keys + ".permission"))){
                        inv.setItem(index, Objects.requireNonNull(con.getConfig().getConfigurationSection(keys)).getItemStack("placeholder"));
                        index++;
                    }
                }else{
                    if(Objects.equals(g.getGameMap().get(player.getWorld().getName()).getStatusMap().get(player.getUniqueId()), "infected")  && index < 45){
                        if(Objects.equals(con.getConfig().getString(keys + ".type"), "infected")){
                            inv.setItem(index, Objects.requireNonNull(con.getConfig().getConfigurationSection(keys)).getItemStack("placeholder"));
                            index++;
                        }
                    }else if(Objects.equals(g.getGameMap().get(player.getWorld().getName()).getStatusMap().get(player.getUniqueId()), "survivor")  && index < 45){
                        if(Objects.equals(con.getConfig().getString(keys + ".type"), "survivor")){
                            inv.setItem(index, Objects.requireNonNull(con.getConfig().getConfigurationSection(keys)).getItemStack("placeholder"));
                            index++;
                        }
                    }
                }
            }
        }

        inv.setItem(53, getItem(new ItemStack(Material.DIAMOND_HOE), "&9N/A", "&aClick to leave the game"));

        // TODO
        //  REMOVE THIS
//        inv.setItem(46, getItem(new ItemStack(Material.DIAMOND_BLOCK), "&9Unassigned", "&aClick to set role as unassigned"));

        player.openInventory(inv);

        return true;
    }

    private ItemStack getItem(ItemStack item, String name, String ... lore) {
        ItemMeta meta = item.getItemMeta();

        Objects.requireNonNull(meta).setDisplayName(ChatColor.translateAlternateColorCodes('&', name));

        List<String> lores = new ArrayList<>();
        for(String s : lore) {
            lores.add(ChatColor.translateAlternateColorCodes('&', s));
        }
        meta.setLore(lores);
        item.setItemMeta(meta);

        return item;
    }
}
