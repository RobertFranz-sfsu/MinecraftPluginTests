package mctest.minecraft_test.commands;

import mctest.minecraft_test.Minecraft_Test;
import mctest.minecraft_test.roles.GamesList;
import mctest.minecraft_test.roles.SurvivalPlayer;
import mctest.minecraft_test.util.ConfigUtil;
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

public class Menu implements Listener, CommandExecutor {
    private String invName = "Server Selector";
    private Minecraft_Test plugin;
    private GamesList g;
    private SurvivalPlayer s;

    public Menu(Minecraft_Test plugin,  GamesList g, SurvivalPlayer s) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
        this.plugin = plugin;
        this.g = g;
        this.s = s;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!event.getView().getTitle().equals(invName)) {
            return;
        }

//        g.getGameMap().get(Bukkit.getWorld(player.getUniqueId()))

        Player player = (Player) event.getWhoClicked();
        int slot = event.getSlot();

        if(slot == 53){
            g.getGameMap().get(player.getWorld().getName()).setNotPlaying(player);
            event.setCancelled(true);
        } else if (slot == 45) {
            g.getGameMap().get(player.getWorld().getName()).gameInit();
            event.setCancelled(true);
        } else if (slot == 46) {
//            if(g.getGameMap().get(Bukkit.getWorld(player.getUniqueId())) == null){
//                g.initGameMap();
//            }

//            if(Objects.equals(g.getGameMap().get(Bukkit.getWorld(player.getUniqueId())), null)){
//                g.game
//            }

            g.getGameMap().get(player.getWorld().getName()).setUnassigned(player);
            event.setCancelled(true);
        }

        if(slot < 45){
            ItemStack loadout = event.getCurrentItem();
            String name = loadout.getItemMeta().getDisplayName().replace('§', '&');

            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "loadout give " + Bukkit.getPlayer(player.getUniqueId()).getName() + " " + name);
        }
        event.setCancelled(true);
        player.closeInventory();
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String [] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(("Must be a player to run this command!"));
            return true;
        }

        Player player = (Player) sender;
        ConfigUtil con = new ConfigUtil(Minecraft_Test.getPlugin(Minecraft_Test.class), "Loadouts.yml");
        Inventory inv = Bukkit.createInventory(player, 9 * 6, invName);
        int index = 0;

        if(sender.isOp()){
            for(String keys : con.getConfig().getKeys(false)){
                inv.setItem(index, con.getConfig().getConfigurationSection(keys).getItemStack("placeholder"));
                index++;
            }

            inv.setItem(45, getItem(new ItemStack(Material.DIAMOND_BLOCK), "&9START GAME", "&aClick to start the game"));
        }else{
            for(String keys : con.getConfig().getKeys(false)){
                if(g.getGameMap().get(player.getWorld().getName()).getPlaying() && plugin.getConfig().getBoolean("use-custom-loadout-perms") && !Objects.equals(con.getConfig().get(keys + ".permission"), null) && index < 45){
                    if(player.hasPermission("infected.loadout." + con.getConfig().getString(keys + ".permission"))){
                        inv.setItem(index, con.getConfig().getConfigurationSection(keys).getItemStack("placeholder"));
                        index++;
                    }
                }else{
                    if(Objects.equals(g.getGameMap().get(player.getWorld().getName()).getStatusMap().get(player.getUniqueId()), "infected")  && index < 45){
                        if(Objects.equals(con.getConfig().getString(keys + ".type"), "infected")){
                            inv.setItem(index, con.getConfig().getConfigurationSection(keys).getItemStack("placeholder"));
                            index++;
                        }
                    }else if(Objects.equals(g.getGameMap().get(player.getWorld().getName()).getStatusMap().get(player.getUniqueId()), "survivor")  && index < 45){
                        if(Objects.equals(con.getConfig().getString(keys + ".type"), "survivor")){
                            inv.setItem(index, con.getConfig().getConfigurationSection(keys).getItemStack("placeholder"));
                            index++;
                        }
                    }
                }
            }
        }

        inv.setItem(53, getItem(new ItemStack(Material.DIAMOND_HOE), "&9N/A", "&aClick to leave the game"));

        // TODO
        //  REMOVE THIS
        inv.setItem(46, getItem(new ItemStack(Material.DIAMOND_BLOCK), "&9Unassigned", "&aClick to set role as unassigned"));

        //TODO Make a new menu for this
        ItemStack item = new ItemStack(Material.GOLD_BLOCK);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.GOLD + "Worlds available");
        meta.setLore(g.getGameInfos());
        item.setItemMeta(meta);
        inv.setItem(49, item);

        player.openInventory(inv);

        return true;
    }

    private ItemStack getItem(ItemStack item, String name, String ... lore) {
        ItemMeta meta = item.getItemMeta();

        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));

        List<String> lores = new ArrayList<>();
        for(String s : lore) {
            lores.add(ChatColor.translateAlternateColorCodes('&', s));
        }
        meta.setLore(lores);
        item.setItemMeta(meta);

        return item;
    }
}
