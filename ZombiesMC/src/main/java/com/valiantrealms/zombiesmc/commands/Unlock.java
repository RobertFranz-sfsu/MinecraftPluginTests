package com.valiantrealms.zombiesmc.commands;

import com.valiantrealms.zombiesmc.ZombiesMC;
import com.valiantrealms.zombiesmc.util.CountdownTimer;
import com.valiantrealms.zombiesmc.util.DelayedTask;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
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

import java.util.*;

public class Unlock implements Listener, CommandExecutor {
    private final ZombiesMC plugin;
    //private Player player;
    private final String invName = "Unlock Chest";
    //private final Integer[] seqArray = new Integer[5];
    private final HashMap<UUID, Integer[]> seqMap = new HashMap<>();
    //private int num = 0;
    private Map<UUID, Integer> numMap = new HashMap<>();
    //private final Set<Integer> patternSet = new HashSet<Integer>();
    //private final Set<Integer> answerSet = new HashSet<Integer>();
    private final Map<UUID, Set<Integer>> patternMap = new HashMap<>();
    private final Map<UUID, Set<Integer>> patternAnsMap = new HashMap<>();
    //private Inventory patternAnswerInv;
    private final Map<UUID,Inventory> invMap = new HashMap<>();
    //private int patternIncorrect = 0;
    private Map<UUID, Integer> patternIncMap = new HashMap<>();
    //private List<Integer> chimpList;
    private final Map<UUID,List<Integer>> chimpMap = new HashMap<>();
    //original menu keys for convinirnvr
    private final HashMap<Integer, ItemStack> keyMap = new HashMap<Integer, ItemStack>();

    //TODO These three will most likely be config options
    private final int maxIncorrect = 3;
    private final int numSlots = 10;
    private final int chimpLength = 5;


    public Unlock(ZombiesMC plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
        this.plugin = plugin;
    }

    /**
     * Colors Lock Pick Section
     */
    public void runColorLockPick(UUID uuid) {
        Player player = Bukkit.getPlayer(uuid);
        Inventory inv = Bukkit.createInventory(player, 9 * 4, "");

        this.keyMap.put(2, getItem(new ItemStack(Material.RED_STAINED_GLASS_PANE), "&9RED", ""));
        this.keyMap.put(3, getItem(new ItemStack(Material.BLUE_STAINED_GLASS_PANE), "&9BLUE", ""));
        this.keyMap.put(4, getItem(new ItemStack(Material.GREEN_STAINED_GLASS_PANE), "&9GREEN", ""));
        this.keyMap.put(5, getItem(new ItemStack(Material.YELLOW_STAINED_GLASS_PANE), "&9YELLOW", ""));

        inv.setItem(2, this.keyMap.get(2));
        inv.setItem(3, this.keyMap.get(3));
        inv.setItem(4, this.keyMap.get(4));
        inv.setItem(5, this.keyMap.get(5));

        player.openInventory(inv);

        Integer[] seqArray = new Integer[5];
        Random rand = new Random();

        int min = 2;
        int max = 5;
        for (int i = 0; i < 5; i++) {
            seqArray[i] = rand.nextInt(max - min + 1) + min;
        }
        Bukkit.getLogger().info("Random sequence:  " + Arrays.toString(seqArray));
        seqMap.put(uuid, seqArray);
        this.colorSequence(uuid);
    }
    //TODO colorSequence could/should probably just be in runColorLockPick
    public void colorSequence(UUID uuid) {
        Player player = Bukkit.getPlayer(uuid);
        Bukkit.getLogger().info("Sequence timer called");
        CountdownTimer time = new CountdownTimer(this.plugin, 10,
                // What happens at the start
                () -> {

                },
                // What happens at the end
                () -> {
                    Bukkit.getLogger().info("SEQUENCE ENDED");
                    player.closeInventory();
                    this.colorAnswerMenu(uuid);
                },
                // What happens during each tick
                (t) -> {
                    Bukkit.getLogger().info("Running");
                    int num = t.getTotalSeconds() - t.getSecondsLeft();
                    if (num % 2 == 1) {
                        this.flashColorPattern(uuid, this.seqMap.get(uuid)[num / 2]);
                    } else {
                        this.flashColorPattern(uuid, -1);
                    }

                });
        time.scheduleTimer();
    }

    /**
     * Set the board for the colors minigame.
     * If t == -1 (every other call), then make every slot gray for better visual
     * clarity in between flashes.  Otherwise, light up the given slot.
     *
     * @param slot the slot to light up
     */
    public void flashColorPattern(UUID uuid, int slot) {
        Player player = Bukkit.getPlayer(uuid);
        Bukkit.getLogger().info("Flashing sequence... Current Answer: " + slot);
        Inventory inv = Bukkit.createInventory(player, 9 * 4, "Sequence");
        if (slot == -1) {
            for (int i = 2; i <= 5; i++) {
                inv.setItem(i, getItem(new ItemStack(Material.BLACK_STAINED_GLASS_PANE), " ", ""));
            }
        } else {
            for (int i = 2; i <= 5; i++) {
                if (i == slot) {
                    inv.setItem(i, this.keyMap.get(i));
                } else {
                    inv.setItem(i, getItem(new ItemStack(Material.BLACK_STAINED_GLASS_PANE), " ", ""));
                }
            }
        }

        player.openInventory(inv);
    }

    // Create inventory for players to enter pattern
    public void colorAnswerMenu(UUID uuid) {
        Player player = Bukkit.getPlayer(uuid);
        Bukkit.getLogger().info("Answer menu opened");
        Bukkit.getLogger().info(Arrays.toString(this.seqMap.get(uuid)));
        Inventory inv = Bukkit.createInventory(player, 9 * 4, "Enter the Colors");
        inv.setItem(2, this.keyMap.get(2));
        inv.setItem(3, this.keyMap.get(3));
        inv.setItem(4, this.keyMap.get(4));
        inv.setItem(5, this.keyMap.get(5));
        player.openInventory(inv);
    }

    //TODO enterColors and colorsResult might be redundant.
    // Decide if worth to have separate methods.  Specifically when opening the chests
    public void enterColors(UUID uuid, int slot) {
        Player player = Bukkit.getPlayer(uuid);

        if (slot > 1 && slot < 6) {

            //Bukkit.getLogger().info("seqMap: " + this.seqMap.get(uuid)[this.numMap.get(uuid)] + "  numMap: " + this.numMap.get(uuid));
            if (this.seqMap.get(uuid)[this.numMap.get(uuid)] != slot) {
                Bukkit.getLogger().info("Incorrect :(");
                player.sendMessage(ChatColor.RED + "Lock pick broke!");
                player.closeInventory();
                this.initVars(uuid);

                //this.colorsResult(false);
            } else if (this.numMap.get(uuid) == 4) {
                Bukkit.getLogger().info("CORRECT!!!!!");
                player.sendMessage(ChatColor.GREEN + "Correct!");
                player.closeInventory();
                this.initVars(uuid);

                //this.colorsResult(true);
            }
            this.numMap.put(uuid, this.numMap.get(uuid) + 1);
        }
    }

    public void colorsResult(UUID uuid, Boolean result) {
        Player player = Bukkit.getPlayer(uuid);
        player.closeInventory();
        this.initVars(uuid);
        if (result) {
            Bukkit.getLogger().info("CORRECT!!!!!");
            player.sendMessage(ChatColor.GREEN + "Correct!");
        } else {
            Bukkit.getLogger().info("Incorrect :(");
            player.sendMessage(ChatColor.RED + "Lock pick broke!");
        }
    }

    /**
     * Pattern Lock Pick Section
     */

    /**
     * Create the decided amount of random slots to light up.
     * Display the random slots in the menu, then remove them after
     * a delay.
     */
    public void runPatterns(UUID uuid) {
        Player player = Bukkit.getPlayer(uuid);
        Random rand = new Random();

        // Chest dimensions
        int min = 0;
        int max = 9 * 4;

        ArrayList<Integer> slotList = new ArrayList<>();
        for (int i = 0; i < max; i++) {
            slotList.add(i);
        }
        Collections.shuffle(slotList);
        List<Integer> sList;
        sList = slotList.subList(0, this.numSlots);
        Set<Integer> patternSet = new HashSet<>();
        patternSet.addAll(sList);
        this.patternMap.put(uuid, patternSet);
        Bukkit.getLogger().info("PatternMap:  " + this.patternMap.get(uuid));

        Inventory inv = Bukkit.createInventory(player, 9 * 4, "Memorize");

        for (int i : this.patternMap.get(uuid)) {
            inv.setItem(i, getItem(new ItemStack(Material.BLACK_STAINED_GLASS_PANE), " ", ""));
        }

        player.openInventory(inv);
        new DelayedTask(this.plugin);
        new DelayedTask(() -> {
            this.patternAnswer(uuid);
        }, 10L * 5);

    }

    /**
     * Creates inventory menu for the player to enter their guess
     */
    public void patternAnswer(UUID uuid) {
        Player player = Bukkit.getPlayer(uuid);
        Inventory inv = Bukkit.createInventory(player, 9 * 4, "Enter the Pattern");
        this.invMap.put(uuid, inv);
        player.openInventory(this.invMap.get(uuid));
    }
    //TODO possibly redundant/unnecessary with enterPattern.  Decide if needed

    /**
     * Handles the pattern game end
     * @param result result of the game
     */
    public void patternEnd(UUID uuid, Boolean result) {
        Player player = Bukkit.getPlayer(uuid);
        if (result) {
            player.sendMessage(ChatColor.GREEN + "Success!!!!");
        } else {
            player.sendMessage(ChatColor.RED + "Lock pick broke");
        }
        this.initVars(uuid);
    }

    /**
     * Checks the input to see if the player entered a correct tile for the pattern game.
     * If not, increment fail attempts until max reached and game is ended.
     *
     * @param slot The slot entered by the player
     */
    public void enterPattern(UUID uuid, int slot) {
        Player player = Bukkit.getPlayer(uuid);
        // make new inv so changing inv in map clearer
        Inventory inv = this.invMap.get(uuid);
        // If slot has already been clicked, ignore
        if (this.patternAnsMap.get(uuid).contains(slot)) {
            Bukkit.getLogger().info("Already entered");
        }else if (this.patternMap.get(uuid).contains(slot)) {
            this.patternAnsMap.get(uuid).add(slot);

            inv.setItem(slot, getItem(new ItemStack(Material.GREEN_STAINED_GLASS_PANE), " ", ""));
            this.invMap.put(uuid, inv);

            if (this.patternAnsMap.get(uuid).size() == this.patternMap.get(uuid).size()) {
                Bukkit.getLogger().info("SUCCESS!!!");
                this.patternEnd(uuid, true);
            }
        } else {
            Bukkit.getLogger().info("Incorrect Guess");

            inv.setItem(slot, getItem(new ItemStack(Material.RED_STAINED_GLASS_PANE), " ", ""));
            this.invMap.put(uuid, inv);

            this.patternIncMap.put(uuid, this.patternIncMap.get(uuid) + 1);
            if (this.patternIncMap.get(uuid) == this.maxIncorrect) {
                this.patternEnd(uuid, false);
            }
        }
    }

    /**
     * Chimp Lock Pick Section
     */

    public void runChimp(UUID uuid) {
        Player player = Bukkit.getPlayer(uuid);
        int max = 9 * 4;

        ArrayList<Integer> slotList = new ArrayList<>();
        for (int i = 0; i < max; i++) {
            slotList.add(i);
        }
        Collections.shuffle(slotList);
        List<Integer> list = slotList.subList(0, this.chimpLength);
        this.chimpMap.put(uuid, list);

        CountdownTimer time = new CountdownTimer(this.plugin, this.chimpLength,
                // What happens at the start
                () -> {

                },
                // What happens at the end
                () -> {
                    player.closeInventory();
                    this.chimpAnswerMenu(uuid);
                },
                // What happens during each tick
                (t) -> {
                    int n = t.getTotalSeconds() - t.getSecondsLeft();
                    this.displayChimpTest(uuid, this.chimpMap.get(uuid).subList(0, n));

                });
        time.scheduleTimer();
    }

    public void displayChimpTest(UUID uuid, List<Integer> list) {
        Player player = Bukkit.getPlayer(uuid);
        Inventory inv = Bukkit.createInventory(player, 9 * 4, "Memorize Order");
        for (Integer integer : list) {
            inv.setItem(integer, getItem(new ItemStack(Material.BLACK_STAINED_GLASS_PANE), " ", ""));
        }
        player.openInventory(inv);
    }

    public void chimpAnswerMenu(UUID uuid) {
        Player player = Bukkit.getPlayer(uuid);
        Inventory inv = Bukkit.createInventory(player, 9 * 4, "Enter Chimp Order");
        for (int i : this.chimpMap.get(uuid)) {
            inv.setItem(i, getItem(new ItemStack(Material.BLACK_STAINED_GLASS_PANE), " ", ""));
        }
        player.openInventory(inv);
    }

    public void enterChimpPattern(UUID uuid, int slot) {
        Player player = Bukkit.getPlayer(uuid);
        int i = this.chimpMap.get(uuid).get(0);
        if (Objects.equals(i, slot)) {
            this.chimpMap.get(uuid).remove(0);
            if (this.chimpMap.get(uuid).isEmpty()) {
                Bukkit.getLogger().info("All Correct");
                player.sendMessage(ChatColor.GREEN + "Chest opened");
                this.initVars(uuid);
            }
        } else {
            Bukkit.getLogger().info("Incorrect");
            player.sendMessage(ChatColor.RED + "Lock Pick Broke");
            this.initVars(uuid);
        }

    }

    /**093
     * Clears all variables
     */
    public void initVars(UUID uuid) {
        Player player = Bukkit.getPlayer(uuid);
        Set<Integer> tempSet = new HashSet<>();
        List<Integer> tempList = new ArrayList<>();
        Inventory tempInv = Bukkit.createInventory(player, 9 * 4, this.invName);
        Integer[] tempArr = new Integer[5];

        player.closeInventory();
        try {
            this.numMap.put(uuid, 0);
            this.seqMap.put(uuid, tempArr);
            this.patternMap.put(uuid, tempSet);
            this.patternAnsMap.put(uuid, tempSet);
            this.patternIncMap.put(uuid, 0);
            this.invMap.put(uuid, tempInv);
            this.chimpMap.put(uuid, tempList);

//            Bukkit.getLogger().info("numMap " + this.numMap.get(uuid));
//            Bukkit.getLogger().info("seqMap " + Arrays.toString(this.seqMap.get(uuid)));
//            Bukkit.getLogger().info("patternMap " + this.patternMap.get(uuid));
//            Bukkit.getLogger().info("patternAnsMap " + this.patternAnsMap.get(uuid));
//            Bukkit.getLogger().info("patternIncMap " + this.patternIncMap.get(uuid));
//            Bukkit.getLogger().info("chimpMap " + this.chimpMap.get(uuid));
        } catch (Exception e) {
            Bukkit.getLogger().info("Tried to clear something that doesn't exist yet.  No worries");
        }
    }

    /**
     * General onClickEvent.  Determine what to do based on current inventory name.
     *
     * @param event inventory click
     */
    @EventHandler
    public void onInventoryClickEvent(InventoryClickEvent event) {
        int slot = event.getSlot();
        UUID uuid = event.getWhoClicked().getUniqueId();
        event.setCancelled(true);
        if (event.getView().getTitle().equals(this.invName)) {
            if (slot == 0) {
                this.runColorLockPick(uuid);
            } else if (slot == 1) {
                this.runPatterns(uuid);
            } else if (slot == 2) {
                this.runChimp(uuid);
            }
        } else if (event.getView().getTitle().equals("Enter the Colors")) {
            this.enterColors(uuid, slot);
        } else if (event.getView().getTitle().equals("Enter the Pattern")) {
            this.enterPattern(uuid, slot);
        } else if (event.getView().getTitle().equals("Enter Chimp Order")) {
            this.enterChimpPattern(uuid, slot);
        }
    }

    /**
     * /unlock command method.
     *
     * @param sender  who/what sent the command
     * @param command what command is being run
     * @param label
     * @param args
     * @return true
     */
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        Player player = (Player) sender;
        Inventory inv = Bukkit.createInventory(player, 9 * 4, this.invName);

        inv.setItem(0, getItem(new ItemStack(Material.DIAMOND_BLOCK), "&9START Colors", "&aBegin Lockpicking"));
        inv.setItem(1, getItem(new ItemStack(Material.DIAMOND_BLOCK), "&9START Patterns", "&aBegin Lockpicking"));
        inv.setItem(2, getItem(new ItemStack(Material.DIAMOND_BLOCK), "&9START Chimp", "&aBegin Lockpicking"));

        // Makes sure to init variables in case if inventory was manually closed previously and variables were never cleared
        this.initVars(player.getUniqueId());

        player.openInventory(inv);

        return true;
    }

    /**
     * Helper function for creating item stacks
     *
     * @param item Item to be created
     * @param name Custom name of the item
     * @param lore Custom Lore of the item
     * @return Custom item
     */
    private ItemStack getItem(ItemStack item, String name, String... lore) {
        ItemMeta meta = item.getItemMeta();

        Objects.requireNonNull(meta).setDisplayName(ChatColor.translateAlternateColorCodes('&', name));

        List<String> lores = new ArrayList<>();
        for (String s : lore) {
            lores.add(ChatColor.translateAlternateColorCodes('&', s));
        }
        meta.setLore(lores);
        item.setItemMeta(meta);

        return item;
    }
}
