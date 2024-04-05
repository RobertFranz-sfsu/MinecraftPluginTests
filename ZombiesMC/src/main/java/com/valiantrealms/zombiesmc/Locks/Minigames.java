package com.valiantrealms.zombiesmc.Locks;

import com.valiantrealms.zombiesmc.ZombiesMC;
import com.valiantrealms.zombiesmc.util.CountdownTimer;
import com.valiantrealms.zombiesmc.util.DelayedTask;
import com.valiantrealms.zombiesmc.util.Keys;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;

public class Minigames implements Listener {
    private final ZombiesMC plugin;
    private final Locks locks;
    private final HashMap<UUID, Integer[]> seqMap = new HashMap<>();
    private final HashMap<UUID, Integer> numMap = new HashMap<>();
    private final HashMap<UUID, Set<Integer>> patternMap = new HashMap<>();
    private final HashMap<UUID, Set<Integer>> patternAnsMap = new HashMap<>();
    private final HashMap<UUID, Inventory> invMap = new HashMap<>();
    private final HashMap<UUID, Integer> patternIncMap = new HashMap<>();
    private final HashMap<UUID, List<Integer>> chimpMap = new HashMap<>();
    private final HashMap<Integer, ItemStack> keyMap = new HashMap<Integer, ItemStack>();
    private final HashMap<String, Integer> colorDiffMap = new HashMap<>();
    private final HashMap<String, Integer> patternDiffMap = new HashMap<>();
    private final HashMap<String, Integer> chimpDiffMap = new HashMap<>();
    private final int maxIncorrect = 3;

    public Minigames(ZombiesMC plugin, Locks locks) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
        this.plugin = plugin;
        this.locks = locks;

        this.colorDiffMap.put("Easy", 3);
        this.colorDiffMap.put("Normal", 5);
        this.colorDiffMap.put("Hard", 7);
        this.colorDiffMap.put("Impossible", 10);

        this.patternDiffMap.put("Easy", 3);
        this.patternDiffMap.put("Normal", 5);
        this.patternDiffMap.put("Hard", 7);
        this.patternDiffMap.put("Impossible", 10);

        this.chimpDiffMap.put("Easy", 3);
        this.chimpDiffMap.put("Normal", 5);
        this.chimpDiffMap.put("Hard", 7);
        this.chimpDiffMap.put("Impossible", 10);
    }

    /**
     * Colors Lock Pick Section
     */
    public void runColorLockPick(UUID uuid) {
        Player player = Bukkit.getPlayer(uuid);
        Inventory inv = Bukkit.createInventory(player, 9 * 4, "");

        this.keyMap.put(2, getItem(new ItemStack(Material.RED_STAINED_GLASS_PANE), ChatColor.RED + "RED", ""));
        this.keyMap.put(3, getItem(new ItemStack(Material.BLUE_STAINED_GLASS_PANE), ChatColor.BLUE + "BLUE", ""));
        this.keyMap.put(4, getItem(new ItemStack(Material.GREEN_STAINED_GLASS_PANE), ChatColor.GREEN + "GREEN", ""));
        this.keyMap.put(5, getItem(new ItemStack(Material.YELLOW_STAINED_GLASS_PANE), ChatColor.YELLOW + "YELLOW", ""));
        this.keyMap.put(6, getItem(new ItemStack(Material.PURPLE_STAINED_GLASS_PANE), ChatColor.DARK_PURPLE + "PURPLE", ""));

        inv.setItem(2, this.keyMap.get(2));
        inv.setItem(3, this.keyMap.get(3));
        inv.setItem(4, this.keyMap.get(4));
        inv.setItem(5, this.keyMap.get(5));
        inv.setItem(6, this.keyMap.get(6));

        player.openInventory(inv);
        String difficulty = this.locks.getCurrentBlockMap().get(uuid).getPersistentDataContainer().get(Keys.DIFFICULTY_SETTING, PersistentDataType.STRING);
        Integer length = this.colorDiffMap.get(difficulty);
        Integer[] seqArray = new Integer[length];
        Random rand = new Random();

        int min = 2;
        int max = 6;
        for (int i = 0; i < length; i++) {
            seqArray[i] = rand.nextInt(max - min + 1) + min;
        }
        Bukkit.getLogger().info("Random sequence:  " + Arrays.toString(seqArray));
        seqMap.put(uuid, seqArray);
        this.colorSequence(uuid, length);
    }

    //TODO colorSequence could/should probably just be in runColorLockPick
    public void colorSequence(UUID uuid, Integer length) {
        Player player = Bukkit.getPlayer(uuid);
        CountdownTimer time = new CountdownTimer(this.plugin, length * 2,
                // What happens at the start
                () -> {

                },
                // What happens at the end
                () -> {
                    player.closeInventory();
                    this.colorAnswerMenu(uuid);
                },
                // What happens during each tick
                (t) -> {
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
        Inventory inv = Bukkit.createInventory(player, 9, "Sequence");
        if (slot == -1) {
            for (int i = 2; i <= 6; i++) {
                inv.setItem(i, getItem(new ItemStack(Material.BLACK_STAINED_GLASS_PANE), " ", ""));
            }
        } else {
            for (int i = 2; i <= 6; i++) {
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
        Bukkit.getLogger().info(Arrays.toString(this.seqMap.get(uuid)));
        Inventory inv = Bukkit.createInventory(player, 9, "Enter the Colors");
        inv.setItem(2, this.keyMap.get(2));
        inv.setItem(3, this.keyMap.get(3));
        inv.setItem(4, this.keyMap.get(4));
        inv.setItem(5, this.keyMap.get(5));
        inv.setItem(6, this.keyMap.get(6));
        player.openInventory(inv);
    }

    //TODO enterColors and colorsResult might be redundant.
    // Decide if worth to have separate methods.  Specifically when opening the chests
    public void enterColors(UUID uuid, int slot) {
        Player player = Bukkit.getPlayer(uuid);
        String difficulty = this.locks.getCurrentBlockMap().get(uuid).getPersistentDataContainer().get(Keys.DIFFICULTY_SETTING, PersistentDataType.STRING);
        Integer length = this.colorDiffMap.get(difficulty);

        if (slot > 1 && slot < 7) {
            if (this.seqMap.get(uuid)[this.numMap.get(uuid)] != slot) {
                player.sendMessage(ChatColor.RED + "Lock pick broke!");
                player.closeInventory();
                this.initVars(uuid);
                this.locks.getCurrentBlockMap().remove(uuid);
                //this.colorsResult(false);
                //TODO <ight need to change 4 to num from difficulty
            } else if (this.numMap.get(uuid) == length - 1) {
                player.sendMessage(ChatColor.GREEN + "Correct!");
                player.closeInventory();
                this.initVars(uuid);
                this.locks.openBlock(uuid);

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
            player.sendMessage(ChatColor.GREEN + "Correct!");
        } else {
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
    public void runPatternsLockPick(UUID uuid) {
        Player player = Bukkit.getPlayer(uuid);
        Random rand = new Random();

        // Chest dimensions
        int min = 0;
        int max = 9 * 4;
        String difficulty = this.locks.getCurrentBlockMap().get(uuid).getPersistentDataContainer().get(Keys.DIFFICULTY_SETTING, PersistentDataType.STRING);

        ArrayList<Integer> slotList = new ArrayList<>();
        for (int i = 0; i < max; i++) {
            slotList.add(i);
        }
        Collections.shuffle(slotList);
        List<Integer> sList = slotList.subList(0, this.patternDiffMap.get(difficulty));
        Set<Integer> patternSet = new HashSet<>(sList);
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
        assert player != null;
        player.openInventory(this.invMap.get(uuid));
    }
    //TODO possibly redundant/unnecessary with enterPattern.  Decide if needed

    /**
     * Handles the pattern game end
     *
     * @param result result of the game
     */
    public void patternEnd(UUID uuid, Boolean result) {
        Player player = Bukkit.getPlayer(uuid);
        if (result) {
            player.sendMessage(ChatColor.GREEN + "Success!!!!");

            this.locks.openBlock(uuid);
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
            //Bukkit.getLogger().info("Already entered");
        } else if (this.patternMap.get(uuid).contains(slot)) {
            this.patternAnsMap.get(uuid).add(slot);

            inv.setItem(slot, getItem(new ItemStack(Material.GREEN_STAINED_GLASS_PANE), " ", ""));
            this.invMap.put(uuid, inv);

            if (this.patternAnsMap.get(uuid).size() == this.patternMap.get(uuid).size()) {
                this.patternEnd(uuid, true);
            }
        } else {
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

    public void runChimpLockPick(UUID uuid) {
        Player player = Bukkit.getPlayer(uuid);
        int max = 9 * 4;
        String difficulty = this.locks.getCurrentBlockMap().get(uuid).getPersistentDataContainer().get(Keys.DIFFICULTY_SETTING, PersistentDataType.STRING);

        ArrayList<Integer> slotList = new ArrayList<>();
        for (int i = 0; i < max; i++) {
            slotList.add(i);
        }
        Collections.shuffle(slotList);
        List<Integer> list = slotList.subList(0, this.chimpDiffMap.get(difficulty));
        this.chimpMap.put(uuid, list);

        CountdownTimer time = new CountdownTimer(this.plugin, this.chimpDiffMap.get(difficulty),
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
        assert player != null;
        player.openInventory(inv);
    }

    public void chimpAnswerMenu(UUID uuid) {
        Player player = Bukkit.getPlayer(uuid);
        Inventory inv = Bukkit.createInventory(player, 9 * 4, "Enter Chimp Order");
        for (int i : this.chimpMap.get(uuid)) {
            inv.setItem(i, getItem(new ItemStack(Material.BLACK_STAINED_GLASS_PANE), " ", ""));
        }
        assert player != null;
        player.openInventory(inv);
    }

    public void enterChimpPattern(UUID uuid, int slot) {
        Player player = Bukkit.getPlayer(uuid);
        int i = this.chimpMap.get(uuid).get(0);
        if (Objects.equals(i, slot)) {
            this.chimpMap.get(uuid).remove(0);
            if (this.chimpMap.get(uuid).isEmpty()) {
                player.sendMessage(ChatColor.GREEN + "Chest opened");
                this.locks.openBlock(uuid);

                this.initVars(uuid);
            }
        } else {
            player.sendMessage(ChatColor.RED + "Lock Pick Broke");
            this.initVars(uuid);
        }

    }

    /**
     * Clears all variables
     */
    public void initVars(UUID uuid) {
        Player player = Bukkit.getPlayer(uuid);
        Set<Integer> tempSet = new HashSet<>();
        List<Integer> tempList = new ArrayList<>();
        Inventory tempInv = Bukkit.createInventory(player, 9 * 4, "Unlock Chest");
        Integer[] tempArr = new Integer[5];

        assert player != null;
        player.closeInventory();
        try {
            this.numMap.put(uuid, 0);
            this.seqMap.put(uuid, tempArr);
            this.patternMap.put(uuid, tempSet);
            this.patternAnsMap.put(uuid, tempSet);
            this.patternIncMap.put(uuid, 0);
            this.invMap.put(uuid, tempInv);
            this.chimpMap.put(uuid, tempList);

        } catch (Exception e) {
            //Bukkit.getLogger().info("Tried to clear something that doesn't exist yet.  No worries");
        }
    }

    /**
     * General onClickEvent.  Determine what to do based on current inventory name.
     *
     * @param event inventory click
     */
    @EventHandler
    private void onInventoryClickEvent(InventoryClickEvent event) {
        int slot = event.getSlot();
        UUID uuid = event.getWhoClicked().getUniqueId();

        if (event.getView().getTitle().equals("Enter the Colors")) {
            event.setCancelled(true);
            this.enterColors(uuid, slot);
        } else if (event.getView().getTitle().equals("Enter the Pattern")) {
            event.setCancelled(true);
            this.enterPattern(uuid, slot);
        } else if (event.getView().getTitle().equals("Enter Chimp Order")) {
            event.setCancelled(true);
            this.enterChimpPattern(uuid, slot);
        }
    }
    /**
     * Helper function for creating item stacks
     *
     * @param item Item to be created
     * @param name Custom name of the item
     * @param lore Custom Lore of the item
     * @return Custom item
     */
    public ItemStack getItem(ItemStack item, String name, String... lore) {
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
