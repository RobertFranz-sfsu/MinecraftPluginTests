package mctest.minecraft_test.commands;

import mctest.minecraft_test.Minecraft_Test;
import mctest.minecraft_test.util.ConfigUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Map;

public class Loadout implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player = (Player) sender;
        ConfigUtil con = new ConfigUtil(Minecraft_Test.getPlugin(Minecraft_Test.class), "Loadouts.yml");

        if(args.length != 0){
            switch(args[0].toLowerCase()){
                case "create": case "c":
                    if(args.length < 3){
                        sender.sendMessage("Usage /loadout create/c [loadoutname] [survivor/infected]");
                    }else {
                        try{
                            if(con.getConfig().contains(args[1])){
                                sender.sendMessage("A loadout with this name already exists!");
                            }else{
                                if(!args[2].toLowerCase().equals("survivor") && !args[2].toLowerCase().equals("infected")){
                                    sender.sendMessage("Loadout not saved");
                                    sender.sendMessage("Please enter whether this is a survivor or infected loadout.");
                                    break;
                                }

                                con.getConfig().createSection(String.valueOf(args[1]));
                                con.getConfig().set(args[1] + ".type", String.valueOf(args[2]).toLowerCase());
                                con.getConfig().set(args[1] + ".placeholder", "WOODEN_SWORD");
                                if(args[2].toLowerCase().equals("survivor")){
                                    con.getConfig().set(args[1] + ".description",
                                            "A survivor loadout.");
                                }else if(args[2].toLowerCase().equals("infected")){
                                    con.getConfig().set(args[1] + ".description",
                                            "An infected loadout.");
                                }
                                con.save();

                                sender.sendMessage("New loadout created with name: " + args[1]);
                            }
                        }catch(Exception e){
                            sender.sendMessage("Something went wrong.");
                        }
                    }
                    break;
                case "delete": case "del":
                    if(args.length < 2){
                        sender.sendMessage("Usage /loadout delete/del [loadoutname]");
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
                            sender.sendMessage("Something went wrong.");
                        }
                    }
                    break;
                case "save":
                    if(args.length < 2){
                        sender.sendMessage("Usage /loadout save [loadoutname]");
                    }else {
                        try{
                            if(!con.getConfig().contains(args[1])){
                                sender.sendMessage("Please enter a valid loadout!");
                            }else{
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
                            sender.sendMessage("Something went wrong.");
                        }
                    }
                    break;
                case "give":
                    if(args.length < 2){
                        sender.sendMessage("Usage /loadout give [playername (optional)] [loadoutname]");
                    }else {
                        try {
                            int index = 1;
                            Player pl = (Player) sender;

                            if((Bukkit.getPlayer(args[1]) instanceof Player)){
                                index = 2;
                                pl = Bukkit.getPlayer(args[1]);
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
                            sender.sendMessage("Something went wrong.");
                        }
                    }
                    break;
                case "list":
                    Map<String, Object> config =
                            con.getConfig().getValues(false);
                    sender.sendMessage("" + config.keySet());
                    break;
                case "setplaceholder": case "sp":
                    if(args.length < 2){
                        sender.sendMessage("The placeholder is the item the loadout is represented by in the menu.");
                        sender.sendMessage("Usage /loadout setPlaceholder/sp [loadoutname]");
                    }else {
                        try{
                            if(!con.getConfig().contains(args[1])){
                                sender.sendMessage("This loadout does not exist!");
                            }else{
                                ItemStack item = player.getInventory().getItemInHand();
                                ItemMeta im = item.getItemMeta();
                                im.setDisplayName(String.valueOf(args[1]));
                                item.setItemMeta(im);

                                con.getConfig().set(args[1] + ".placeholder", item);
                                con.save();

                                sender.sendMessage("Placeholder for " + args[1] + " has been updated!");
                            }
                        }catch(Exception e){
                            sender.sendMessage("Something went wrong.");
                        }
                    }
                    break;
                case "setdescription": case "sd":
                    if(args.length < 2){
                        sender.sendMessage("Usage /loadout setDescription/sd [loadoutname] [description]");
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
                                    loreArr.append(args[i] + " ");
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
                            sender.sendMessage("Something went wrong.");
                        }
                    }

                    break;
                default:
                    sender.sendMessage("Please enter a valid loadout name or create one!");
                    break;
            }
        }else{
            sender.sendMessage("Valid sub commands: create, delete, list, give, setPlaceholder, setDescription.");
        }

        return true;
    }
}