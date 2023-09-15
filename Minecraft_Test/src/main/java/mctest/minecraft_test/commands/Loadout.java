package mctest.minecraft_test.commands;

import com.google.gson.stream.JsonReader;
import mctest.minecraft_test.Minecraft_Test;
import mctest.minecraft_test.util.ConfigUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Map;

import static net.md_5.bungee.api.chat.ClickEvent.Action.COPY_TO_CLIPBOARD;

public class Loadout implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(args.length != 0){
            ConfigUtil con = new ConfigUtil(Minecraft_Test.getPlugin(Minecraft_Test.class), "Loadouts.yml");

            switch(args[0].toLowerCase()){
                case "create": case "c":
                    if(args.length < 2){
                        sender.sendMessage("Usage: /loadout create/c [loadoutname]");
                    }else {
                        try{
                            if(con.getConfig().contains(args[1])){
                                sender.sendMessage("A loadout with this name already exists!");
                            }else{
                                con.getConfig().createSection(String.valueOf(args[1]));
                                con.getConfig().set(args[1] + ".description", "A loadout.");

                                ItemStack item = new ItemStack(Material.matchMaterial("IRON_SWORD"), 1);
                                ItemMeta im = item.getItemMeta();
                                im.setDisplayName(ChatColor.translateAlternateColorCodes ('&', String.valueOf(args[1])));

                                ArrayList<String> lore = new ArrayList<>();

                                lore.add("A loadout.");

                                im.setLore(lore);
                                item.setItemMeta(im);

                                con.getConfig().set(args[1] + ".placeholder", item);
                                con.save();

                                sender.sendMessage("New loadout created with name: " + args[1]);
                            }
                        }catch(Exception e){
                            sender.sendMessage("Something went wrong. Please check the console.");
                            e.printStackTrace();
                        }
                    }
                    break;
                case "delete": case "del":
                    if(args.length < 2){
                        sender.sendMessage("Usage: /loadout delete/del [loadoutname]");
                    }else {
                        try{
                            if(!con.getConfig().contains(args[1])){
                                sender.sendMessage("This loadout does not exist!");
                            }else{
                                con.getConfig().set(args[1], null);
                                con.save();

                                sender.sendMessage("Loadout " + args[1] + " has been deleted!");
                            }
                        }catch(Exception e){
                            sender.sendMessage("Something went wrong. Please check the console.");
                            e.printStackTrace();
                        }
                    }
                    break;
                case "save":
                    if(args.length < 2){
                        sender.sendMessage("Usage: /loadout save [loadoutname]");
                    }else {
                        try{
                            if(!con.getConfig().contains(args[1])){
                                sender.sendMessage("Please enter a valid loadout!");
                            }else{
                                Player player = (Player) sender;

                                for(int i = 0 ; i < player.getInventory().getSize() ; i++) {
                                    con.getConfig().set(args[1] + "." + i, player.getInventory().getItem(i));
                                }

                                if(player.getInventory().getHelmet() != null){
                                    con.getConfig().set(args[1] + ".helmet", player.getInventory().getHelmet());
                                }else{
                                    con.getConfig().set(args[1] + ".helmet", null);
                                }

                                if(player.getInventory().getChestplate() != null){
                                    con.getConfig().set(args[1] + ".chestplate", player.getInventory().getChestplate());
                                }else{
                                    con.getConfig().set(args[1] + ".chestplate", null);
                                }

                                if(player.getInventory().getLeggings() != null){
                                    con.getConfig().set(args[1] + ".leggings", player.getInventory().getLeggings());
                                }else{
                                    con.getConfig().set(args[1] + ".leggings", null);
                                }

                                if(player.getInventory().getBoots() != null){
                                    con.getConfig().set(args[1] + ".boots", player.getInventory().getBoots());
                                }else{
                                    con.getConfig().set(args[1] + ".boots", null);
                                }

                                con.save();

                                sender.sendMessage("Loadout has been saved to " + args[1]);
                            }
                        }catch(Exception e){
                            sender.sendMessage("Something went wrong. Please check the console.");
                            e.printStackTrace();
                        }
                    }
                    break;
                case "give":
                    if(args.length < 2){
                        sender.sendMessage("Usage: /loadout give [playername (optional)] [loadoutname]");
                    }else {
                        try {
                            int index = 1;
                            Player pl = null;

                            if((Bukkit.getPlayer(args[1]) instanceof Player)){
                                index = 2;
                                pl = Bukkit.getPlayer(args[1]);
                            }else if(sender instanceof Player){
                                pl = (Player) sender;
                            }

                            pl.getInventory().clear();
                            for (String keys : con.getConfig().getConfigurationSection(args[index]).getKeys(false)) {
                                if (keys.equals("helmet")) {
                                    ItemStack helm = con.getConfig().getConfigurationSection(args[index]).getItemStack(keys);
                                    pl.getInventory().setHelmet(helm);
                                } else if (keys.equals("chestplate")) {
                                    ItemStack chest = con.getConfig().getConfigurationSection(args[index]).getItemStack(keys);
                                    pl.getInventory().setChestplate(chest);
                                } else if (keys.equals("leggings")) {
                                    ItemStack legs = con.getConfig().getConfigurationSection(args[index]).getItemStack(keys);
                                    pl.getInventory().setLeggings(legs);
                                } else if (keys.equals("boots")) {
                                    ItemStack boots = con.getConfig().getConfigurationSection(args[index]).getItemStack(keys);
                                    pl.getInventory().setBoots(boots);
                                } else if(keys.equals("placeholder") || keys.equals("type") || keys.equals("description")){
                                    // Do nothing
                                }else {
                                    int slot = Integer.parseInt(keys);
                                    ItemStack item = con.getConfig().getConfigurationSection(args[index]).getItemStack(keys);

                                    pl.getInventory().setItem(slot, item);
                                }
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                case "list":
                    Map<String, Object> config =
                            con.getConfig().getValues(false);

                    if(!(sender instanceof Player)){
                        for(String x : config.keySet()){
                            Bukkit.getLogger().info(x);
                        }
                        break;
                    }

                    Player pl = (Player) sender;

                    if(!config.isEmpty()){
                        for(String x : config.keySet()){
                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                                    "tellraw " + pl.getUniqueId() + " {" +
                                            "\"text\": \"" + ChatColor.translateAlternateColorCodes ('&', x) + "\"," +
                                            "\"hoverEvent\": {" +
                                            "\"action\": \"show_text\"," +
                                            "\"value\": \"Shift click to copy to chat\"" +
                                            "}," +
                                            "\"insertion\": \"" + x + "\"" +
                                            "}");
                        }
                    }else{
                        sender.sendMessage("There are no configs!");
                    }

                    break;
                case "setplaceholder": case "sp":
                    if(args.length < 2){
                        sender.sendMessage("The placeholder is the item the loadout is represented by in the menu.");
                        sender.sendMessage("Usage: /loadout setPlaceholder/sp [loadoutname]");
                    }else {
                        try{
                            Player player = (Player) sender;
                            if(!con.getConfig().contains(args[1])){
                                sender.sendMessage("This loadout does not exist!");
                            }else{
                                ArrayList<String> lore = new ArrayList<>();
                                StringBuilder loreArr = new StringBuilder();

                                String l = con.getConfig().getString(args[1] + ".description");
                                String[] arr = l.split(" ");

                                int count = 0;
                                for(int i = 0; i < arr.length; i++){
                                    if(count > 4){
                                        lore.add(loreArr.toString());
                                        loreArr.setLength(0);
                                        count = 0;
                                    }
                                    loreArr.append(arr[i] + " ");
                                    count++;
                                }
                                lore.add(loreArr.toString());

                                ItemStack item = player.getInventory().getItemInHand().clone();
                                ItemMeta im = item.getItemMeta();
                                im.setDisplayName(ChatColor.translateAlternateColorCodes ('&', String.valueOf(args[1])));
                                im.setLore(lore);
                                item.setItemMeta(im);

                                con.getConfig().set(args[1] + ".placeholder", item);
                                con.save();

                                sender.sendMessage("Placeholder for " + args[1] + " has been updated!");
                            }
                        }catch(Exception e){
                            sender.sendMessage("Something went wrong. Please check the console.");
                            e.printStackTrace();
                        }
                    }
                    break;
                case "setdescription": case "sd":
                    if(args.length < 2){
                        sender.sendMessage("Usage: /loadout setDescription/sd [loadoutname] [description]");
                    }else {
                        try{
                            if(!con.getConfig().contains(args[1])){
                                sender.sendMessage("This loadout does not exist!");
                            }else{
                                ArrayList<String> lore = new ArrayList<>();
                                StringBuilder loreArr = new StringBuilder();;

                                for(int i = 2; i < args.length; i++){
                                    if(i == (args.length-2)/2){
                                        lore.add(loreArr.toString());
                                        loreArr.setLength(0);
                                    }
                                    loreArr.append(ChatColor.translateAlternateColorCodes ('&', args[i] + " "));
                                }
                                lore.add(loreArr.toString());

                                ItemStack item = con.getConfig().getConfigurationSection(args[1]).getItemStack("placeholder");
                                ItemMeta im = item.getItemMeta();
                                im.setLore(lore);
                                item.setItemMeta(im);

                                con.getConfig().set(args[1] + ".placeholder", item);

                                for(String s : lore){
                                    con.getConfig().set(args[1] + ".description", s);
                                }

                                con.save();

                                sender.sendMessage("Description for " + args[1] + " has been updated!");
                            }
                        }catch(Exception e){
                            sender.sendMessage("Something went wrong. Please check the console.");
                            e.printStackTrace();
                        }
                    }

                    break;
                case "settype": case "st":
                    if(args.length < 3){
                        sender.sendMessage("Usage: /loadout setType/st [loadoutname] [type]");
                    }else {
                        try{
                            if(!args[2].toLowerCase().equals("survivor") && !args[2].toLowerCase().equals("infected")){
                                sender.sendMessage("Please enter whether this is a survivor or infected loadout.");
                            }else{
                                con.getConfig().set(args[1] + ".type", String.valueOf(args[2]).toLowerCase());
                                con.save();
                                sender.sendMessage("Type for loadout " + args[1] + " set to " + String.valueOf(args[2]).toLowerCase());
                            }
                        }catch(Exception e){
                            sender.sendMessage("Something went wrong. Please check the console.");
                            e.printStackTrace();
                        }
                    }
                    break;
                default:
                    sender.sendMessage("Please enter a valid loadout name or create one!");
                    break;
            }
        }else{
            sender.sendMessage("Valid sub commands: create, save, delete, list, give, setPlaceholder, setDescription, setType.");
        }

        return true;
    }
}
