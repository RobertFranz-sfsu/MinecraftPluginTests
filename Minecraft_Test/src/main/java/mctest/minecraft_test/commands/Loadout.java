package mctest.minecraft_test.commands;

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
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Loadout implements CommandExecutor {
    Minecraft_Test plugin;
    public Loadout(Minecraft_Test plugin){
        this.plugin = plugin;
    }

    @SuppressWarnings({"NullableProblems", "CallToPrintStackTrace", "deprecation"}) // Removing the warning from the passed in objects.
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        ConfigUtil con = plugin.getLoadoutCon();

        if(args.length != 0){
            switch(args[0].toLowerCase()){
                case "create": case "c":
                    if(sender.hasPermission("infected.loadout.create") || sender.hasPermission("infected.loadout.*")){
                        if(args.length < 2){
                            sender.sendMessage("Usage: /loadout create/c [loadoutname]");
                        }else {
                            try{
                                if(con.getConfig().contains(args[1])){
                                    sender.sendMessage("A loadout with this name already exists!");
                                }else{
                                    con.getConfig().createSection(String.valueOf(args[1]));
                                    con.getConfig().set(args[1] + ".description", "A loadout.");

                                    ItemStack item = new ItemStack(Objects.requireNonNull(Material.matchMaterial("IRON_SWORD")), 1);
                                    ItemMeta im = item.getItemMeta();
                                    Objects.requireNonNull(im).setDisplayName(ChatColor.translateAlternateColorCodes ('&', String.valueOf(args[1].replaceAll("_", " "))));

                                    ArrayList<String> lore = new ArrayList<>();

                                    lore.add("A loadout.");
                                    if(plugin.isLoadoutPrices()){
//                                        con.getConfig().set(args[1] + ".price", 0.00);
                                        lore.add("Price: " + 0.00 + "");
                                    }

                                    im.setLore(lore);
                                    item.setItemMeta(im);

                                    for(String x : lore){
                                        sender.sendMessage(x);
                                    }

                                    con.getConfig().set(args[1] + ".placeholder", item);
                                    con.save();
                                    plugin.setLoadoutCon();

                                    sender.sendMessage("New loadout created with name: " + args[1]);
                                }
                            }catch(Exception e){
                                sender.sendMessage("Something went wrong. Please check the console.");
                                Bukkit.getLogger().warning("Something went wrong trying to create a loadout.");
                                e.printStackTrace();
                            }
                        }
                    }else{
                        sender.sendMessage("You do not have permission to do this!");
                        break;
                    }

                    break;
                case "delete": case "del":
                    if(sender.hasPermission("infected.loadout.delete") || sender.hasPermission("infected.loadout.*")){
                        if(args.length < 2){
                            sender.sendMessage("Usage: /loadout delete/del [loadoutname]");
                        }else {
                            try{
                                if(!con.getConfig().contains(args[1])){
                                    sender.sendMessage("This loadout does not exist!");
                                }else{
                                    con.getConfig().set(args[1], null);
                                    con.save();
                                    plugin.setLoadoutCon();

                                    sender.sendMessage("Loadout " + args[1] + " has been deleted!");
                                }
                            }catch(Exception e){
                                sender.sendMessage("Something went wrong. Please check the console.");
                                Bukkit.getLogger().warning("Something went wrong trying to delete a loadout.");
                                e.printStackTrace();
                            }
                        }
                    }else{
                        sender.sendMessage("You do not have permission to do this!");
                        break;
                    }

                    break;
                case "save":
                    if(sender.hasPermission("infected.loadout.save") || sender.hasPermission("infected.loadout.*")){
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
                                        ItemStack item = new ItemStack(player.getInventory().getHelmet().getType(), 1);
                                        item.setItemMeta(player.getInventory().getHelmet().getItemMeta());

                                        con.getConfig().set(args[1] + ".helmet", item);
                                    }else{
                                        con.getConfig().set(args[1] + ".helmet", null);
                                    }

                                    if(player.getInventory().getChestplate() != null){
                                        ItemStack item = new ItemStack(player.getInventory().getChestplate().getType(), 1);
                                        item.setItemMeta(player.getInventory().getChestplate().getItemMeta());

                                        con.getConfig().set(args[1] + ".chestplate", item);
                                    }else{
                                        con.getConfig().set(args[1] + ".chestplate", null);
                                    }

                                    if(player.getInventory().getLeggings() != null){
                                        ItemStack item = new ItemStack(player.getInventory().getLeggings().getType(), 1);
                                        item.setItemMeta(player.getInventory().getLeggings().getItemMeta());

                                        con.getConfig().set(args[1] + ".leggings", item);
                                    }else{
                                        con.getConfig().set(args[1] + ".leggings", null);
                                    }

                                    if(player.getInventory().getBoots() != null){
                                        ItemStack item = new ItemStack(player.getInventory().getBoots().getType(), 1);
                                        item.setItemMeta(player.getInventory().getBoots().getItemMeta());

                                        con.getConfig().set(args[1] + ".boots", item);
                                    }else{
                                        con.getConfig().set(args[1] + ".boots", null);
                                    }

                                    con.save();
                                    plugin.setLoadoutCon();

                                    sender.sendMessage("Loadout has been saved to " + args[1]);
                                }
                            }catch(Exception e){
                                sender.sendMessage("Something went wrong. Please check the console.");
                                Bukkit.getLogger().warning("Something went wrong trying to save a loadout.");
                                e.printStackTrace();
                            }
                        }
                    }else{
                        sender.sendMessage("You do not have permission to do this!");
                        break;
                    }

                    break;
                case "give":
                    if(sender.hasPermission("infected.loadout.give") || sender.hasPermission("infected.loadout.*")){
                        if(args.length < 2){
                            sender.sendMessage("Usage: /loadout give [playername (optional)] [loadoutname]");
                        }else {
                            try {
                                int index = 1;
                                Player pl = null;
                                String name = "";

                                if((Bukkit.getPlayer(args[1]) != null)){
                                    index = 2;
                                    pl = Bukkit.getPlayer(args[1]);
                                    name = Objects.requireNonNull(Bukkit.getPlayer(args[1])).getDisplayName();
                                }else if(sender instanceof Player){
                                    pl = (Player) sender;
                                    name = ((Player) sender).getDisplayName();
                                }

                                Objects.requireNonNull(pl).getInventory().clear();
                                for (String keys : Objects.requireNonNull(con.getConfig().getConfigurationSection(args[index])).getKeys(false)) {
                                    switch (keys) {
                                        case "helmet":
                                            ItemStack helm = Objects.requireNonNull(con.getConfig().getConfigurationSection(args[index])).getItemStack(keys);
                                            pl.getInventory().setHelmet(helm);
                                            break;
                                        case "chestplate":
                                            ItemStack chest = Objects.requireNonNull(con.getConfig().getConfigurationSection(args[index])).getItemStack(keys);
                                            pl.getInventory().setChestplate(chest);
                                            break;
                                        case "leggings":
                                            ItemStack legs = Objects.requireNonNull(con.getConfig().getConfigurationSection(args[index])).getItemStack(keys);
                                            pl.getInventory().setLeggings(legs);
                                            break;
                                        case "boots":
                                            ItemStack boots = Objects.requireNonNull(con.getConfig().getConfigurationSection(args[index])).getItemStack(keys);
                                            pl.getInventory().setBoots(boots);
                                            break;
                                        case "placeholder": case "type": case "description": case "permission": case "price":
                                            // Do nothing
                                            break;
                                        default:
                                            int slot = Integer.parseInt(keys);
                                            ItemStack item = Objects.requireNonNull(con.getConfig().getConfigurationSection(args[index])).getItemStack(keys);

                                            pl.getInventory().setItem(slot, item);
                                            break;
                                    }
                                }

                                sender.sendMessage("Gave kit " + args[index] + " to player " + name + ".");
                                Bukkit.getLogger().info("Gave kit " + args[index] + " to player " + name + ".");

                            } catch (Exception e) {
                                Bukkit.getLogger().warning("Something went wrong trying to give a loadout.");
                                e.printStackTrace();
                            }
                        }
                    }else{
                        sender.sendMessage("You do not have permission to do this!");
                        break;
                    }

                    break;

                case "list": case "l":
                    if(sender.hasPermission("infected.loadout.list") || sender.hasPermission("infected.loadout.*")){
                        try{
                            Map<String, Object> config =
                                    con.getConfig().getValues(false);

                            if(!(sender instanceof Player)){
                                for(String x : config.keySet()){
                                    Bukkit.getLogger().info(x.replaceAll("_", " "));
                                }
                                break;
                            }

                            Player pl = (Player) sender;

                            if(!config.isEmpty()){
                                int count = 1;

                                for(String x : config.keySet()){
                                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                                            "tellraw " + pl.getName() + " {" +
                                                    "\"text\": \"" + ChatColor.translateAlternateColorCodes ('&', count + ") " + x.replaceAll("_", " ")) + "\"," +
                                                    "\"hoverEvent\": {" +
                                                    "\"action\": \"show_text\"," +
                                                    "\"value\": \"Shift click to copy to chat\"" +
                                                    "}," +
                                                    "\"insertion\": \"" + x + "\"" +
                                                    "}");
                                    count++;
                                }
                            }else{
                                sender.sendMessage("There are no configs!");
                            }
                        }catch (Exception e){
                            sender.sendMessage("Something went wrong, please check console.");
                            Bukkit.getLogger().warning("Something went wrong trying to list loadouts.");
                            e.printStackTrace();
                        }
                    }else{
                        sender.sendMessage("You do not have permission to do this!");
                        break;
                    }

                    break;
                case "setplaceholder": case "sp":
                    if(sender.hasPermission("infected.loadout.setplaceholder") || sender.hasPermission("infected.loadout.*")){
                        if(args.length < 2){
                            sender.sendMessage("The placeholder is the item the loadout is represented by in the menu.");
                            sender.sendMessage("Usage: /loadout setPlaceholder/sp [loadoutname]");
                        }else {
                            try{
                                Player player = (Player) sender;
                                if(!con.getConfig().contains(args[1])){
                                    sender.sendMessage("This loadout does not exist!");
                                }else{
                                    ItemStack item;

                                    if(plugin.getIs18()){
                                        item = player.getInventory().getItemInHand().clone();
                                    }else{
                                        item = player.getInventory().getItemInMainHand();
                                    }

                                    ItemStack itemLore = Objects.requireNonNull(con.getConfig().getConfigurationSection(args[1])).getItemStack("placeholder");
                                    ItemMeta imLore = Objects.requireNonNull(itemLore).getItemMeta();
                                    List<String> lore = Objects.requireNonNull(imLore).getLore();

                                    ItemMeta im = item.getItemMeta();
                                    Objects.requireNonNull(im).setDisplayName(ChatColor.translateAlternateColorCodes ('&', String.valueOf(args[1].replaceAll("_", " "))));
                                    im.setLore(lore);
                                    item.setItemMeta(im);

                                    con.getConfig().set(args[1] + ".placeholder", item);
                                    con.save();
                                    plugin.setLoadoutCon();

                                    sender.sendMessage("Placeholder for " + args[1] + " has been updated!");
                                }
                            }catch(Exception e){
                                sender.sendMessage("Something went wrong. Please check the console.");
                                Bukkit.getLogger().warning("Something went wrong trying to set the placeholder.");
                                e.printStackTrace();
                            }
                        }
                    }else{
                        sender.sendMessage("You do not have permission to do this!");
                        break;
                    }

                    break;
                case "setdescription": case "sd":
                    if(sender.hasPermission("infected.loadout.setdescription") || sender.hasPermission("infected.loadout.*")){
                        if(args.length < 2){
                            sender.sendMessage("Usage: /loadout setDescription/sd [loadoutname] [description]");
                        }else {
                            try{
                                if(!con.getConfig().contains(args[1])){
                                    sender.sendMessage("This loadout does not exist!");
                                }else{
                                    ArrayList<String> lore = new ArrayList<>();
                                    StringBuilder loreArr = new StringBuilder();;

                                    int count = 0;

                                    for(int i = 2; i < args.length; i++){
                                        if(count == 5){
                                            lore.add(loreArr.toString());
                                            loreArr.setLength(0);
                                            count = 0;
                                        }
                                        loreArr.append(ChatColor.translateAlternateColorCodes ('&', args[i] + " "));
                                        count++;
                                    }
                                    lore.add(loreArr.toString());

                                    if(plugin.isLoadoutPrices()){
                                        lore.add("Price: " + con.getConfig().getDouble(args[1] + ".price"));
                                    }

                                    ItemStack item = Objects.requireNonNull(con.getConfig().getConfigurationSection(args[1])).getItemStack("placeholder");
                                    ItemMeta im = Objects.requireNonNull(item).getItemMeta();
                                    Objects.requireNonNull(im).setLore(lore);
                                    item.setItemMeta(im);

                                    con.getConfig().set(args[1] + ".placeholder", item);

                                    for(String s : lore){
                                        con.getConfig().set(args[1] + ".description", s);
                                    }

                                    con.save();
                                    plugin.setLoadoutCon();

                                    sender.sendMessage("Description for " + args[1] + " has been updated!");
                                }
                            }catch(Exception e){
                                sender.sendMessage("Something went wrong. Please check the console.");
                                Bukkit.getLogger().warning("Something went wrong trying to set the description.");
                                e.printStackTrace();
                            }
                        }
                    }else{
                        sender.sendMessage("You do not have permission to do this!");
                        break;
                    }

                    break;
                case "settype": case "st":
                    if(sender.hasPermission("infected.loadout.settype") || sender.hasPermission("infected.loadout.*")){
                        if(args.length < 3){
                            sender.sendMessage("Usage: /loadout setType/st [loadoutname] [type]");
                        }else {
                            try{
                                if(!args[2].equalsIgnoreCase("survivor") && !args[2].equalsIgnoreCase("infected") &&
                                !args[2].equalsIgnoreCase("i") && !args[2].equalsIgnoreCase("s")){
                                    sender.sendMessage("Please enter whether this is a survivor or infected loadout.");
                                }else{
                                    String type = "";

                                    if(args[2].equals("i")){
                                        type = "infected";
                                    }else if(args[2].equals("s")){
                                        type = "survivor";
                                    }else{
                                        type = args[2].toLowerCase();
                                    }
                                    con.getConfig().set(args[1] + ".type", type);
                                    con.save();
                                    plugin.setLoadoutCon();
                                    sender.sendMessage("Type for loadout " + args[1] + " set to " + type);
                                }
                            }catch(Exception e){
                                sender.sendMessage("Something went wrong. Please check the console.");
                                Bukkit.getLogger().warning("Something went wrong trying to set the type.");
                                e.printStackTrace();
                            }
                        }
                    }else{
                        sender.sendMessage("You do not have permission to do this!");
                        break;
                    }

                    break;
                case "setperm": case "perm":
                    if(sender.hasPermission("infected.loadout.setperm") || sender.hasPermission("infected.loadout.*")){
                        if(args.length < 3){
                            sender.sendMessage("Usage: /loadout setPerm/perm [loadoutname] [permission]");
                            sender.sendMessage("This will make the permission node: infected.loadout.[permission]");
                        }else{
                            try{
                                con.getConfig().set(args[1] + ".permission", args[2].toLowerCase());
                                con.save();
                                plugin.setLoadoutCon();
                                sender.sendMessage("&fThe permission for " + args[1] + " has been set to " + args[2].toLowerCase());
                            }catch(Exception e){
                                sender.sendMessage("Something went wrong. Please check the console.");
                                Bukkit.getLogger().warning("Something went wrong trying to set the permission.");
                                e.printStackTrace();
                            }
                        }
                    }else{
                        sender.sendMessage("You do not have permission to do this!");
                        break;
                    }
                    break;

                case "setprice": case "price":
                    if(sender.hasPermission("infected.loadout.setprice") || sender.hasPermission("infected.loadout.*")){
                        if(args.length < 3){
                            sender.sendMessage("Usage: /loadout setPrice/price [loadoutname] [price]");
                        }else{
                            try{
                                con.getConfig().set(args[1] + ".price", Double.valueOf(args[2]));

                                ItemStack item = Objects.requireNonNull(con.getConfig().getConfigurationSection(args[1])).getItemStack("placeholder");
                                ItemMeta im = Objects.requireNonNull(item).getItemMeta();
                                List<String> lore = Objects.requireNonNull(im).getLore();

                                int num = 0;
                                for(String x : lore){
                                    if(x.contains("Price:")){
                                        lore.set(num, "Price: " + Double.valueOf(args[2]));
                                    }
                                    num++;
                                }

                                Objects.requireNonNull(im).setLore(lore);
                                item.setItemMeta(im);
                                con.save();
                                plugin.setLoadoutCon();

                                sender.sendMessage("The price for " + args[1] + " has been set to " + Double.valueOf(args[2]));
                            }catch(Exception e){
                                sender.sendMessage("Something went wrong. Please check the console.");
                                Bukkit.getLogger().warning("Something went wrong trying to set the price.");
                                e.printStackTrace();
                            }
                        }
                    }else{
                        sender.sendMessage("You do not have permission to do this!");
                        break;
                    }

                    break;

                default:
                    sender.sendMessage("Please enter a valid loadout name or create one!");
                    break;
            }
        }else{
            sender.sendMessage("Valid sub commands: create (c), save, delete (del), list (l), give, " +
                    "setPlaceholder (sp), setDescription (sd), setType (st), setPerm (perm), setPrice (price).");
        }

        return true;
    }
}
