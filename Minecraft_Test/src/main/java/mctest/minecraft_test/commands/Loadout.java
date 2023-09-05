package mctest.minecraft_test.commands;

import mctest.minecraft_test.Minecraft_Test;
import mctest.minecraft_test.util.ConfigUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class Loadout implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player = (Player) sender;
        ConfigUtil con = new ConfigUtil(Minecraft_Test.getPlugin(Minecraft_Test.class), "Loadouts.yml");

        if(args.length != 0){
            switch(args[0]){
                case "create":
                    if(args.length < 2){
                        sender.sendMessage("Usage /loadout create [loadoutname]");
                    }else {
                        try{
                            if(con.getConfig().contains(args[1])){
                                sender.sendMessage("A loadout with this name already exists!");
                            }else{
                                con.getConfig().createSection(args[1].toString().replaceAll("[\\[\\],]",""));
                                con.save();

                                sender.sendMessage("New loadout created with name: " + args[1]);
                            }
                        }catch(Exception e){
                            sender.sendMessage("Something went wrong.");
                        }
                    }
                    break;
                case "delete":
                    if(args.length < 2){
                        sender.sendMessage("Usage /loadout delete [loadoutname]");
                    }else {
                        try{
                            if(!con.getConfig().contains(args[1])){
                                sender.sendMessage("This loadout does not exist!");
                            }else{
                                con.getConfig().set(args[1], null);
                                con.save();

                                sender.sendMessage("Loadout " + args[1] + "has been deleted!");
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
//                                Bukkit.getLogger().info("CONFIG: " + con.getConfig().getConfigurationSection(args[1]).getValues(false).toString());
                                for(int i = 0 ; i < player.getInventory().getSize() ; i++) {
                                    con.getConfig().set(args[1].toString().replaceAll("[\\[\\],]","") + "." + i, player.getInventory().getItem(i));
//                                    if(player.getInventory().getItem(i) != null) {
//                                        con.getConfig().set(args[1].toString().replaceAll("[\\[\\],]","") + "." + i, player.getInventory().getItem(i));
//                                    }
                                }

                                if(player.getInventory().getHelmet() != null){
                                    con.getConfig().set(args[1].toString().replaceAll("[\\[\\],]","") + "." + "helmet", player.getInventory().getHelmet());
                                }else{
                                    con.getConfig().set(args[1].toString().replaceAll("[\\[\\],]","") + "." + "helmet", null);
                                }

                                if(player.getInventory().getChestplate() != null){
                                    con.getConfig().set(args[1].toString().replaceAll("[\\[\\],]","") + "." + "chestplate", player.getInventory().getChestplate());
                                }else{
                                    con.getConfig().set(args[1].toString().replaceAll("[\\[\\],]","") + "." + "chestplate", null);
                                }

                                if(player.getInventory().getLeggings() != null){
                                    con.getConfig().set(args[1].toString().replaceAll("[\\[\\],]","") + "." + "leggings", player.getInventory().getLeggings());
                                }else{
                                    con.getConfig().set(args[1].toString().replaceAll("[\\[\\],]","") + "." + "leggings", null);
                                }

                                if(player.getInventory().getBoots() != null){
                                    con.getConfig().set(args[1].toString().replaceAll("[\\[\\],]","") + "." + "boots", player.getInventory().getBoots());
                                }else{
                                    con.getConfig().set(args[1].toString().replaceAll("[\\[\\],]","") + "." + "boots", null);
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
                        sender.sendMessage("Usage /loadout give [loadoutname]");
                    }else {
                        try {
                            player.getInventory().clear();
                            for (String keys : con.getConfig().getConfigurationSection(args[1].toString().replaceAll("[\\[\\],]", "")).getKeys(false)) {
                                if (keys.equals("helmet")) {
                                    ItemStack helm = con.getConfig().getConfigurationSection(args[1].toString().replaceAll("[\\[\\],]", "")).getItemStack("helmet");
                                    player.getInventory().setHelmet(helm);
                                } else if (keys.equals("chestplate")) {
                                    ItemStack chest = con.getConfig().getConfigurationSection(args[1].toString().replaceAll("[\\[\\],]", "")).getItemStack("chestplate");
                                    player.getInventory().setChestplate(chest);
                                } else if (keys.equals("leggings")) {
                                    ItemStack legs = con.getConfig().getConfigurationSection(args[1].toString().replaceAll("[\\[\\],]", "")).getItemStack("leggings");
                                    player.getInventory().setLeggings(legs);
                                } else if (keys.equals("boots")) {
                                    ItemStack boots = con.getConfig().getConfigurationSection(args[1].toString().replaceAll("[\\[\\],]", "")).getItemStack("boots");
                                    player.getInventory().setBoots(boots);
                                } else {
                                    int slot = Integer.parseInt(keys);
                                    Bukkit.getLogger().info("KEYS: " + Integer.parseInt(keys));
                                    ItemStack item = con.getConfig().getConfigurationSection(args[1].toString().replaceAll("[\\[\\],]", "")).getItemStack(keys);

                                    player.getInventory().setItem(slot, item);
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
                default:
                    sender.sendMessage("Please enter a valid loadout name or create one!");
                    break;
            }
        }else{
            sender.sendMessage("Valid sub commands: create, delete, list, save.");
        }

        return true;
    }
}
