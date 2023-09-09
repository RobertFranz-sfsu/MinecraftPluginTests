package mctest.minecraft_test.commands;

import mctest.minecraft_test.Minecraft_Test;
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
    private SurvivalPlayer gamer;

    public Menu(Minecraft_Test plugin, SurvivalPlayer gamer) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
        this.gamer = gamer;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!event.getView().getTitle().equals(invName)) {
            return;
        }

        Player player = (Player) event.getWhoClicked();
        int slot = event.getSlot();

        if (slot == 11) {
            gamer.setInfection(player);
            event.setCancelled(true);
        } else if (slot == 13) {
            gamer.setSurvivor(player);
            event.setCancelled(true);
        } else if(slot == 15){
            gamer.setNotPlaying(player);
            event.setCancelled(true);
        } else if (slot == 4) {
            gamer.gameInit();
            event.setCancelled(true);
        } else if (slot == 6) {
            gamer.setUnassigned(player);
            event.setCancelled(true);
        }

        ItemStack loadout = event.getCurrentItem();
        String name = loadout.getItemMeta().getDisplayName();
//        String name = ChatColor.translateAlternateColorCodes ('§', loadout.getItemMeta().getDisplayName());

//        player.sendMessage(name);

        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "loadout give " + Bukkit.getPlayer(player.getUniqueId()).getName() + " " + name);
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
        }else if(Objects.equals(gamer.getStatusMap().get(player.getUniqueId()), "infected")){
            for(String keys : con.getConfig().getKeys(false)){
                if(Objects.equals(con.getConfig().getString(keys + ".type"), "infected")){
                    inv.setItem(index, con.getConfig().getConfigurationSection(keys).getItemStack("placeholder"));
                    index++;
                }
            }
        }else if(Objects.equals(gamer.getStatusMap().get(player.getUniqueId()), "survivor")){
            for(String keys : con.getConfig().getKeys(false)){
                if(Objects.equals(con.getConfig().getString(keys + ".type"), "survivor")){
                    inv.setItem(index, con.getConfig().getConfigurationSection(keys).getItemStack("placeholder"));
                    index++;
                }
            }
        }

        inv.setItem(11, getItem(new ItemStack(Material.IRON_SWORD), "&9Infected", "&aClick to become infected"));
        inv.setItem(13, getItem(new ItemStack(Material.BOW), "&9Survivor", "&aClick to become a survivor"));
        inv.setItem(15, getItem(new ItemStack(Material.DIAMOND_HOE), "&9N/A", "&aClick to leave the game"));
        inv.setItem(4, getItem(new ItemStack(Material.DIAMOND_BLOCK), "&9START GAME", "&aClick to start the game"));
        inv.setItem(6, getItem(new ItemStack(Material.DIAMOND_BLOCK), "&9Unassigned", "&aClick to set role as unassigned"));

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
