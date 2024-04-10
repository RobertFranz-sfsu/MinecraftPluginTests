package com.valiantrealms.zombiesmc.Locks;

import com.valiantrealms.zombiesmc.ZombiesMC;
import com.valiantrealms.zombiesmc.util.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class Minigames implements Listener {
    private final ZombiesMC plugin;
    private final Locks locks;
    private final ItemUtil itemUtil = new ItemUtil();
    private final UUID uuid;
    private final ConfigUtil diffConfig;
    private final HashMap<UUID, Integer[]> seqMap = new HashMap<>();
    private final HashMap<UUID, Integer> numMap = new HashMap<>();
    private final HashMap<UUID, Set<Integer>> patternMap = new HashMap<>();
    private final HashMap<UUID, Set<Integer>> patternAnsMap = new HashMap<>();
    private final HashMap<UUID, Inventory> invMap = new HashMap<>();
    private final HashMap<UUID, Integer> patternIncMap = new HashMap<>();
    private final HashMap<UUID, List<Integer>> chimpMap = new HashMap<>();
    private final HashMap<Integer, ItemStack> keyMap = new HashMap<Integer, ItemStack>();
    //private final HashMap<String, Integer> colorDiffMap = new HashMap<>();
    //private final HashMap<String, Integer> patternDiffMap = new HashMap<>();
    //private final HashMap<String, Integer> chimpDiffMap = new HashMap<>();
    //private final HashMap<String, HashMap<String, Integer>> moleDiffMap = new HashMap<>();
    private final HashMap<UUID, List<Integer>> moleListMap = new HashMap<>();
    private final HashMap<UUID, CountdownTimer> moleTimerMap = new HashMap<>();
    private final HashMap<UUID, Integer> moleFailsMap = new HashMap<>();
    private final String difficulty;
    private final int colorsLeft;
    private final int colorsRight;

    //TODO rename mole stuff
    // Add more difficulty options to other games
    // Possibly change mole difficulty ints to floats to better customize flash time

    public Minigames(ZombiesMC plugin, Locks locks, UUID uuid) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
        this.plugin = plugin;
        this.locks = locks;
        this.uuid = uuid;

        this.difficulty = this.locks.getCurrentBlockMap().get(uuid).getPersistentDataContainer().get(Keys.DIFFICULTY_SETTING, PersistentDataType.STRING);
        this.diffConfig = new ConfigUtil(plugin, "LockPickDifficulties.yml");
        this.diffConfig.save();

        this.keyMap.put(0, this.itemUtil.getItem(new ItemStack(Material.CYAN_STAINED_GLASS_PANE), ChatColor.BLUE + "CYAN", ""));
        this.keyMap.put(1, this.itemUtil.getItem(new ItemStack(Material.ORANGE_STAINED_GLASS_PANE), ChatColor.YELLOW + "ORANGE", ""));
        this.keyMap.put(2, this.itemUtil.getItem(new ItemStack(Material.RED_STAINED_GLASS_PANE), ChatColor.RED + "RED", ""));
        this.keyMap.put(3, this.itemUtil.getItem(new ItemStack(Material.BLUE_STAINED_GLASS_PANE), ChatColor.BLUE + "BLUE", ""));
        this.keyMap.put(4, this.itemUtil.getItem(new ItemStack(Material.GREEN_STAINED_GLASS_PANE), ChatColor.GREEN + "GREEN", ""));
        this.keyMap.put(5, this.itemUtil.getItem(new ItemStack(Material.YELLOW_STAINED_GLASS_PANE), ChatColor.YELLOW + "YELLOW", ""));
        this.keyMap.put(6, this.itemUtil.getItem(new ItemStack(Material.PURPLE_STAINED_GLASS_PANE), ChatColor.DARK_PURPLE + "PURPLE", ""));
        this.keyMap.put(7, this.itemUtil.getItem(new ItemStack(Material.LIGHT_BLUE_STAINED_GLASS_PANE), ChatColor.BLUE + "LIGHT BLUE", ""));
        this.keyMap.put(8, this.itemUtil.getItem(new ItemStack(Material.MAGENTA_STAINED_GLASS_PANE), ChatColor.BLUE + "MAGENTA", ""));

        this.colorsLeft = this.diffConfig.getConfig().getInt("colors." + difficulty + ".left");
        this.colorsRight = this.diffConfig.getConfig().getInt("colors." + difficulty + ".right");

//        this.colorDiffMap.put("Easy", 3);
//        this.colorDiffMap.put("Normal", 5);
//        this.colorDiffMap.put("Hard", 7);
//        this.colorDiffMap.put("Impossible", 10);
//
//        this.patternDiffMap.put("Easy", 3);
//        this.patternDiffMap.put("Normal", 5);
//        this.patternDiffMap.put("Hard", 7);
//        this.patternDiffMap.put("Impossible", 10);

//        this.chimpDiffMap.put("Easy", 3);
//        this.chimpDiffMap.put("Normal", 5);
//        this.chimpDiffMap.put("Hard", 7);
//        this.chimpDiffMap.put("Impossible", 10);

//        HashMap<String, Integer> easyMoleMap = new HashMap<>();
//        easyMoleMap.put("errors", 3);
//        easyMoleMap.put("length", 5);
//        easyMoleMap.put("fail time", 3);
//        easyMoleMap.put("flash time", 2);
//        this.moleDiffMap.put("Easy", easyMoleMap);
//
//        HashMap<String, Integer> normalMoleMap = new HashMap<>();
//        normalMoleMap.put("errors", 2);
//        normalMoleMap.put("length", 10);
//        normalMoleMap.put("fail time", 3);
//        normalMoleMap.put("flash time", 2);
//        this.moleDiffMap.put("Normal", normalMoleMap);
//
//        HashMap<String, Integer> hardMoleMap = new HashMap<>();
//        hardMoleMap.put("errors", 1);
//        hardMoleMap.put("length", 15);
//        hardMoleMap.put("fail time", 2);
//        hardMoleMap.put("flash time", 1);
//        this.moleDiffMap.put("Hard", hardMoleMap);
//
//        HashMap<String, Integer> impMoleMap = new HashMap<>();
//        impMoleMap.put("errors", 0);
//        impMoleMap.put("length", 20);
//        impMoleMap.put("fail time", 2);
//        impMoleMap.put("flash time", 1);
//        this.moleDiffMap.put("Impossible", impMoleMap);
    }

    /**
     * Colors Lock Pick Section
     */
    public void runColorLockPick() {
        this.initVars();
        Player player = Bukkit.getPlayer(uuid);
        Inventory inv = Bukkit.createInventory(player, 9 * 4, "");

        for (int i = this.colorsLeft; i < this.colorsRight; i++) {
            inv.setItem(i, this.keyMap.get(i));
        }

        player.openInventory(inv);

        int length = this.diffConfig.getConfig().getInt("colors." + this.difficulty + ".length");
        Integer[] seqArray = new Integer[length];
        Random rand = new Random();

        for (int i = 0; i < length; i++) {
            seqArray[i] = rand.nextInt(this.colorsRight - this.colorsLeft + 1) + this.colorsLeft;
        }
        seqMap.put(uuid, seqArray);
        this.colorSequence(length);
    }

    public void colorSequence(Integer length) {
        Player player = Bukkit.getPlayer(uuid);
        CountdownTimer time = new CountdownTimer(this.plugin, length * 2,
                // What happens at the start
                () -> {

                },
                // What happens at the end
                () -> {
                    player.closeInventory();
                    this.colorAnswerMenu();
                },
                // What happens during each tick
                (t) -> {
                    int count = t.getTotalSeconds() - t.getSecondsLeft();
                    if (count % 2 == 1) {
                        this.flashColorPattern(this.seqMap.get(uuid)[count / 2]);
                    } else {
                        this.flashColorPattern( -1);
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
    public void flashColorPattern(int slot) {
        Player player = Bukkit.getPlayer(uuid);
        Inventory inv = Bukkit.createInventory(player, 9, "Sequence");
        for (int i = this.colorsLeft; i <= this.colorsRight; i++) {
            if (slot == -1) {
                inv.setItem(i, this.itemUtil.getItem(new ItemStack(Material.BLACK_STAINED_GLASS_PANE), " ", ""));
            } else {
                if (i == slot) {
                    inv.setItem(i, this.keyMap.get(i));
                } else {
                    inv.setItem(i, this.itemUtil.getItem(new ItemStack(Material.BLACK_STAINED_GLASS_PANE), " ", ""));
                }
            }
        }

        player.openInventory(inv);
    }

    // Create inventory for players to enter pattern
    public void colorAnswerMenu() {
        Player player = Bukkit.getPlayer(uuid);
        Inventory inv = Bukkit.createInventory(player, 9, "Enter the Colors");
        for (int i = this.colorsLeft; i <= this.colorsRight; i++) {
            inv.setItem(i, this.keyMap.get(i));
        }
        player.openInventory(inv);
    }

    public void enterColors(int slot) {
        Player player = Bukkit.getPlayer(uuid);
        Integer length = this.diffConfig.getConfig().getInt("colors." + this.difficulty + ".length");
        //Integer length = this.colorDiffMap.get(difficulty);

        if ((slot > this.colorsLeft-1) && slot < (this.colorsRight+1)) {
            if (this.seqMap.get(uuid)[this.numMap.get(uuid)] != slot) {
                player.sendMessage(ChatColor.RED + "Lock pick broke!");
                player.closeInventory();
                this.initVars();
                this.locks.getCurrentBlockMap().remove(uuid);
                this.end();
            } else if (this.numMap.get(uuid) == length - 1) {
                player.sendMessage(ChatColor.GREEN + "Unlocked");
                player.closeInventory();
                this.initVars();
                this.locks.openBlock(uuid);
                this.end();
            }
            this.numMap.put(uuid, this.numMap.get(uuid) + 1);
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
    public void runPatternsLockPick() {
        this.initVars();
        Player player = Bukkit.getPlayer(uuid);

        int max = 9 * 4;

        ArrayList<Integer> slotList = new ArrayList<>();
        for (int i = 0; i < max; i++) {
            slotList.add(i);
        }
        Collections.shuffle(slotList);
        int amount = this.diffConfig.getConfig().getInt("pattern." + difficulty + ".amount");
        List<Integer> sList = slotList.subList(0, amount);
        Set<Integer> patternSet = new HashSet<>(sList);
        this.patternMap.put(uuid, patternSet);
        //Bukkit.getLogger().info("PatternMap:  " + this.patternMap.get(uuid));

        Inventory inv = Bukkit.createInventory(player, 9 * 4, "Memorize");

        for (int i : this.patternMap.get(uuid)) {
            inv.setItem(i, this.itemUtil.getItem(new ItemStack(Material.BLACK_STAINED_GLASS_PANE), " ", ""));
        }

        player.openInventory(inv);
        new DelayedTask(this.plugin);
        new DelayedTask(() -> {
            this.patternAnswer();
        }, 10L * 5);

    }

    /**
     * Creates inventory menu for the player to enter their guess
     */
    public void patternAnswer() {
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
    public void patternEnd(Boolean result) {
        Player player = Bukkit.getPlayer(uuid);
        if (result) {
            player.sendMessage(ChatColor.GREEN + "Chest Opened");

            this.locks.openBlock(uuid);
        } else {
            player.sendMessage(ChatColor.RED + "Lock pick broke");
        }
        this.initVars();
        this.end();
    }

    /**
     * Checks the input to see if the player entered a correct tile for the pattern game.
     * If not, increment fail attempts until max reached and game is ended.
     *
     * @param slot The slot entered by the player
     */
    public void enterPattern(int slot) {
        // make new inv so changing inv in map clearer
        Inventory inv = this.invMap.get(uuid);
        int maxIncorrect = this.diffConfig.getConfig().getInt("pattern." + this.difficulty + ".errors");
        // If slot has already been clicked, ignore
        if (this.patternAnsMap.get(uuid).contains(slot)) {
            //Bukkit.getLogger().info("Already entered");
        } else if (this.patternMap.get(uuid).contains(slot)) {
            this.patternAnsMap.get(uuid).add(slot);

            inv.setItem(slot, this.itemUtil.getItem(new ItemStack(Material.GREEN_STAINED_GLASS_PANE), " ", ""));
            this.invMap.put(uuid, inv);

            if (this.patternAnsMap.get(uuid).size() == this.patternMap.get(uuid).size()) {
                this.patternEnd(true);
            }
        } else {
            inv.setItem(slot, this.itemUtil.getItem(new ItemStack(Material.RED_STAINED_GLASS_PANE), " ", ""));
            this.invMap.put(uuid, inv);

            this.patternIncMap.put(uuid, this.patternIncMap.get(uuid) + 1);
            Bukkit.getLogger().info("Incorrect: " + this.patternIncMap.get(uuid) + "  Max: " + maxIncorrect);
            if (this.patternIncMap.get(uuid) == maxIncorrect) {
                this.patternEnd(false);
            }
        }
    }

    /**
     * Chimp Lock Pick Section
     */

    public void runChimpLockPick() {
        this.initVars();
        Player player = Bukkit.getPlayer(uuid);
        int max = 9 * 4;

        ArrayList<Integer> slotList = new ArrayList<>();
        for (int i = 0; i < max; i++) {
            slotList.add(i);
        }
        Collections.shuffle(slotList);
        int amount = this.diffConfig.getConfig().getInt("chimp." + this.difficulty + ".amount");
        List<Integer> list = slotList.subList(0, amount);
        this.chimpMap.put(uuid, list);

        CountdownTimer time = new CountdownTimer(this.plugin, amount,
                // What happens at the start
                () -> {

                },
                // What happens at the end
                () -> {
                    player.closeInventory();
                    this.chimpAnswerMenu();
                },
                // What happens during each tick
                (t) -> {
                    int n = t.getTotalSeconds() - t.getSecondsLeft();
                    this.displayChimpTest(this.chimpMap.get(uuid).subList(0, n));

                });
        time.scheduleTimer();
    }

    public void displayChimpTest(List<Integer> list) {
        Player player = Bukkit.getPlayer(uuid);
        Inventory inv = Bukkit.createInventory(player, 9 * 4, "Memorize Order");
        for (Integer integer : list) {
            inv.setItem(integer, this.itemUtil.getItem(new ItemStack(Material.BLACK_STAINED_GLASS_PANE), " ", ""));
        }
        assert player != null;
        player.openInventory(inv);
    }

    public void chimpAnswerMenu() {
        Player player = Bukkit.getPlayer(uuid);
        Inventory inv = Bukkit.createInventory(player, 9 * 4, "Enter Chimp Order");
        for (int i : this.chimpMap.get(uuid)) {
            inv.setItem(i, this.itemUtil.getItem(new ItemStack(Material.BLACK_STAINED_GLASS_PANE), " ", ""));
        }
        assert player != null;
        player.openInventory(inv);
    }

    public void enterChimpPattern(int slot) {
        Player player = Bukkit.getPlayer(uuid);
        int i = this.chimpMap.get(uuid).get(0);
        if (Objects.equals(i, slot)) {
            this.chimpMap.get(uuid).remove(0);
            if (this.chimpMap.get(uuid).isEmpty()) {
                player.sendMessage(ChatColor.GREEN + "Chest opened");
                this.locks.openBlock(uuid);

                this.initVars();
                this.end();
            }
        } else {
            player.sendMessage(ChatColor.RED + "Lock Pick Broke");
            this.initVars();
            this.end();
        }

    }


    /**
     * Whack a mole Minigame
     */
    public void runMoleLockPick() {
        this.initVars();
        Player player = Bukkit.getPlayer(uuid);
        int max = 9 * 4;
        int length = this.diffConfig.getConfig().getInt("mole." + this.difficulty + ".length");

        ArrayList<Integer> slotList = new ArrayList<>();
        for (int i = 0; i < max; i++) {
            slotList.add(i);
        }
        Collections.shuffle(slotList);
        List<Integer> list = slotList.subList(0, length);
        List<Integer> temp = new ArrayList<>();
        this.moleListMap.put(uuid, temp);
        this.moleFailsMap.put(uuid, 0);
        int flashTime = this.diffConfig.getConfig().getInt("mole." + this.difficulty + ".flash-time");

        CountdownTimer time = new CountdownTimer(this.plugin, length * flashTime,
                // What happens at the start
                () -> {

                },
                // What happens at the end
                () -> {
                    player.closeInventory();
                    Bukkit.getPlayer(uuid).sendMessage(ChatColor.GREEN + "Opened!");
                    this.initVars();
                    this.locks.openBlock(uuid);
                    this.end();

                },
                // What happens during each tick
                (t) -> {
                    int n = t.getTotalSeconds() - t.getSecondsLeft();
                    if (n % flashTime == 0) {
                        this.addMole(list.get(n/flashTime), t.getSecondsLeft());
                    }
                });
        this.moleTimerMap.put(uuid, time);
        time.scheduleTimer();


    }

    public void addMole(Integer slot, Integer time) {
        List<Integer> tempList = this.moleListMap.get(uuid);
        tempList.add(slot);
        this.moleListMap.put(uuid, tempList);
        int failTime = this.diffConfig.getConfig().getInt("mole." + this.difficulty + ".fail-time");
        int errors = this.diffConfig.getConfig().getInt("mole." + this.difficulty + ".errors");
        this.displayMoleInv();

        // For each new tile: create a timer to see if the player clicked before the timer runs out
        new DelayedTask(() -> {
            if (this.moleListMap.get(uuid).contains(slot)) {
                // If there is still time left to fail so that it doesn't run after game ended
                if ((time - failTime) > 0) {
                    // If the timer is still running
                    if (Bukkit.getScheduler().isQueued(this.moleTimerMap.get(uuid).getId())) {
                        Bukkit.getPlayer(uuid).sendMessage(ChatColor.RED + "Missed one!");
                        this.moleFailsMap.put(uuid, this.moleFailsMap.get(uuid) + 1);
                        this.moleListMap.get(uuid).remove(slot);

                        // If the player missed more than allowed: they fail
                        if (this.moleFailsMap.get(uuid) >= errors) {
                            Bukkit.getPlayer(uuid).sendMessage(ChatColor.RED + "Lock Pick Broke");
                            Bukkit.getScheduler().cancelTask(this.moleTimerMap.get(uuid).getId());
                            Bukkit.getPlayer(uuid).closeInventory();
                            this.end();
                        }
                    }
                }
            }
        }, 10L * failTime);
    }

    public void displayMoleInv() {
        Player player = Bukkit.getPlayer(uuid);
        Inventory inv = Bukkit.createInventory(player, 9 * 4, "Whack a Mole");
        for (Integer integer : this.moleListMap.get(uuid)) {
            inv.setItem(integer, this.itemUtil.getItem(new ItemStack(Material.BLACK_STAINED_GLASS_PANE), " ", ""));
        }
        assert player != null;
        player.openInventory(inv);
    }

    //TODO Actually implement incorrect clicks
    public void enterMoleAnswer(Integer slot) {
        if (this.moleListMap.get(uuid).contains(slot)) {
            this.moleListMap.get(uuid).remove(slot);
        } else {
            //Bukkit.getLogger().info("Clicked Wrong Slot!");
            Bukkit.getPlayer(uuid).sendMessage(ChatColor.RED + "Clicked Wrong Slot!");
        }
    }

    /**
     * Clears all variables
     */
    public void initVars() {
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
            this.moleListMap.put(uuid, tempList);
            this.moleFailsMap.put(uuid, 0);

        } catch (Exception e) {
            //Bukkit.getLogger().info("Tried to clear something that doesn't exist yet.  No worries");
        }
    }

    public void end() {
        //Bukkit.getLogger().info("Ending Listener");
        InventoryClickEvent.getHandlerList().unregister(this);
    }

    /**
     * General onClickEvent.  Determine what to do based on current inventory name.
     *
     * @param event inventory click
     */
    @EventHandler
    private void onInventoryClickEvent(InventoryClickEvent event) {
        UUID uuidEvent = event.getWhoClicked().getUniqueId();
        if (uuidEvent != uuid) {
            return;
        }

        int slot = event.getSlot();

        if (event.getView().getTitle().equals("Enter the Colors")) {
            event.setCancelled(true);
            this.enterColors(slot);
        } else if (event.getView().getTitle().equals("Enter the Pattern")) {
            event.setCancelled(true);
            this.enterPattern(slot);
        } else if (event.getView().getTitle().equals("Enter Chimp Order")) {
            event.setCancelled(true);
            this.enterChimpPattern(slot);
        } else if (event.getView().getTitle().equals("Whack a Mole")) {
            event.setCancelled(true);
            this.enterMoleAnswer(slot);
        }
    }

}
