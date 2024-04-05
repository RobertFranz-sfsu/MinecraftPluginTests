package com.valiantrealms.zombiesmc.commands;

import com.valiantrealms.zombiesmc.Locks.Minigames;
import com.valiantrealms.zombiesmc.ZombiesMC;
import com.valiantrealms.zombiesmc.util.CountdownTimer;
import com.valiantrealms.zombiesmc.util.DelayedTask;
import com.valiantrealms.zombiesmc.util.Keys;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.TileState;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;

//TODO
// Add loot to chests
// (Most likely not this anymore)Make file for all chests for 'lock/unlock all' since current set is lost at server restart
public class Unlock implements Listener, CommandExecutor {
    private final ZombiesMC plugin;
    private final Minigames minigames;
    private final String invName = "Unlock Chest";
//    private final HashMap<UUID, Integer[]> seqMap = new HashMap<>();
//    private final HashMap<UUID, Integer> numMap = new HashMap<>();
//    private final HashMap<UUID, Set<Integer>> patternMap = new HashMap<>();
//    private final HashMap<UUID, Set<Integer>> patternAnsMap = new HashMap<>();
//    private final HashMap<UUID, Inventory> invMap = new HashMap<>();
//    private final HashMap<UUID, Integer> patternIncMap = new HashMap<>();
//    private final HashMap<UUID, List<Integer>> chimpMap = new HashMap<>();
//    private final HashMap<Integer, ItemStack> keyMap = new HashMap<Integer, ItemStack>();
//    // currentBlockMap is the block the player is currently interacting with
//    private final HashMap<UUID, TileState> currentBlockMap = new HashMap<>();
//    //private final HashMap<TileState, ArrayList<String>> chestLocksMap = new HashMap<>();
    private final Set<TileState> chestSet = new HashSet<>();

    //TODO These will most likely be config options
    //private final int colorLength = 3;
//    private final int maxIncorrect = 3;
    //private final int numSlots = 5;
    //private final int chimpLength = 5;
//    private final int autoLockTimer = 10;
    private final HashMap<String, Integer> colorDiffMap = new HashMap<>();
    private final HashMap<String, Integer> patternDiffMap = new HashMap<>();
    private final HashMap<String, Integer> chimpDiffMap = new HashMap<>();

    public Unlock(ZombiesMC plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
        this.plugin = plugin;
        this.minigames = plugin.getMinigames();

//        this.colorDiffMap.put("Easy", 3);
//        this.colorDiffMap.put("Normal", 5);
//        this.colorDiffMap.put("Hard", 7);
//        this.colorDiffMap.put("Impossible", 10);
//
//        this.patternDiffMap.put("Easy", 3);
//        this.patternDiffMap.put("Normal", 5);
//        this.patternDiffMap.put("Hard", 7);
//        this.patternDiffMap.put("Impossible", 10);
//
//        this.chimpDiffMap.put("Easy", 3);
//        this.chimpDiffMap.put("Normal", 5);
//        this.chimpDiffMap.put("Hard", 7);
//        this.chimpDiffMap.put("Impossible", 10);

    }

//    /**
//     * Colors Lock Pick Section
//     */
//    public void runColorLockPick(UUID uuid) {
//        Player player = Bukkit.getPlayer(uuid);
//        Inventory inv = Bukkit.createInventory(player, 9 * 4, "");
//
//        this.keyMap.put(2, getItem(new ItemStack(Material.RED_STAINED_GLASS_PANE), ChatColor.RED + "RED", ""));
//        this.keyMap.put(3, getItem(new ItemStack(Material.BLUE_STAINED_GLASS_PANE), ChatColor.BLUE + "BLUE", ""));
//        this.keyMap.put(4, getItem(new ItemStack(Material.GREEN_STAINED_GLASS_PANE), ChatColor.GREEN + "GREEN", ""));
//        this.keyMap.put(5, getItem(new ItemStack(Material.YELLOW_STAINED_GLASS_PANE), ChatColor.YELLOW + "YELLOW", ""));
//        this.keyMap.put(6, getItem(new ItemStack(Material.PURPLE_STAINED_GLASS_PANE), ChatColor.DARK_PURPLE + "PURPLE", ""));
//
//        inv.setItem(2, this.keyMap.get(2));
//        inv.setItem(3, this.keyMap.get(3));
//        inv.setItem(4, this.keyMap.get(4));
//        inv.setItem(5, this.keyMap.get(5));
//        inv.setItem(6, this.keyMap.get(6));
//
//        player.openInventory(inv);
//
//        String difficulty = this.currentBlockMap.get(uuid).getPersistentDataContainer().get(Keys.DIFFICULTY_SETTING, PersistentDataType.STRING);
//        Integer length = this.colorDiffMap.get(difficulty);
//        Bukkit.getLogger().info("Colors Difficulty: " + difficulty + "  Length: " + length);
//        Integer[] seqArray = new Integer[length];
//        Random rand = new Random();
//
//        int min = 2;
//        int max = 6;
//        for (int i = 0; i < length; i++) {
//            seqArray[i] = rand.nextInt(max - min + 1) + min;
//        }
//        Bukkit.getLogger().info("Random sequence:  " + Arrays.toString(seqArray));
//        seqMap.put(uuid, seqArray);
//        this.colorSequence(uuid, length);
//    }
//
//    //TODO colorSequence could/should probably just be in runColorLockPick
//    public void colorSequence(UUID uuid, Integer length) {
//        Player player = Bukkit.getPlayer(uuid);
//        CountdownTimer time = new CountdownTimer(this.plugin, length * 2,
//                // What happens at the start
//                () -> {
//
//                },
//                // What happens at the end
//                () -> {
//                    player.closeInventory();
//                    this.colorAnswerMenu(uuid);
//                },
//                // What happens during each tick
//                (t) -> {
//                    int num = t.getTotalSeconds() - t.getSecondsLeft();
//                    if (num % 2 == 1) {
//                        this.flashColorPattern(uuid, this.seqMap.get(uuid)[num / 2]);
//                    } else {
//                        this.flashColorPattern(uuid, -1);
//                    }
//
//                });
//        time.scheduleTimer();
//    }
//
//    /**
//     * Set the board for the colors minigame.
//     * If t == -1 (every other call), then make every slot gray for better visual
//     * clarity in between flashes.  Otherwise, light up the given slot.
//     *
//     * @param slot the slot to light up
//     */
//    public void flashColorPattern(UUID uuid, int slot) {
//        Player player = Bukkit.getPlayer(uuid);
//        Inventory inv = Bukkit.createInventory(player, 9, "Sequence");
//        if (slot == -1) {
//            for (int i = 2; i <= 6; i++) {
//                inv.setItem(i, getItem(new ItemStack(Material.BLACK_STAINED_GLASS_PANE), " ", ""));
//            }
//        } else {
//            for (int i = 2; i <= 6; i++) {
//                if (i == slot) {
//                    inv.setItem(i, this.keyMap.get(i));
//                } else {
//                    inv.setItem(i, getItem(new ItemStack(Material.BLACK_STAINED_GLASS_PANE), " ", ""));
//                }
//            }
//        }
//        player.openInventory(inv);
//    }
//
//    // Create inventory for players to enter pattern
//    public void colorAnswerMenu(UUID uuid) {
//        Player player = Bukkit.getPlayer(uuid);
//        Bukkit.getLogger().info(Arrays.toString(this.seqMap.get(uuid)));
//        Inventory inv = Bukkit.createInventory(player, 9, "Enter the Colors");
//        inv.setItem(2, this.keyMap.get(2));
//        inv.setItem(3, this.keyMap.get(3));
//        inv.setItem(4, this.keyMap.get(4));
//        inv.setItem(5, this.keyMap.get(5));
//        inv.setItem(6, this.keyMap.get(6));
//        player.openInventory(inv);
//    }
//
//    //TODO enterColors and colorsResult might be redundant.
//    // Decide if worth to have separate methods.  Specifically when opening the chests
//    public void enterColors(UUID uuid, int slot) {
//        Player player = Bukkit.getPlayer(uuid);
//        String difficulty = this.currentBlockMap.get(uuid).getPersistentDataContainer().get(Keys.DIFFICULTY_SETTING, PersistentDataType.STRING);
//        Integer length = this.colorDiffMap.get(difficulty);
//
//        if (slot > 1 && slot < 7) {
//            if (this.seqMap.get(uuid)[this.numMap.get(uuid)] != slot) {
//                player.sendMessage(ChatColor.RED + "Lock pick broke!");
//                player.closeInventory();
//                this.initVars(uuid);
//                this.currentBlockMap.remove(uuid);
//                //this.colorsResult(false);
//                //TODO <ight need to change 4 to num from difficulty
//            } else if (this.numMap.get(uuid) == length - 1) {
//                player.sendMessage(ChatColor.GREEN + "Correct!");
//                player.closeInventory();
//                this.initVars(uuid);
//                this.openBlock(uuid);
//
//                //this.colorsResult(true);
//            }
//            this.numMap.put(uuid, this.numMap.get(uuid) + 1);
//        }
//    }
//
//    public void colorsResult(UUID uuid, Boolean result) {
//        Player player = Bukkit.getPlayer(uuid);
//        player.closeInventory();
//        this.initVars(uuid);
//        if (result) {
//            player.sendMessage(ChatColor.GREEN + "Correct!");
//        } else {
//            player.sendMessage(ChatColor.RED + "Lock pick broke!");
//        }
//    }
//
//    /**
//     * Pattern Lock Pick Section
//     */
//
//    /**
//     * Create the decided amount of random slots to light up.
//     * Display the random slots in the menu, then remove them after
//     * a delay.
//     */
//    public void runPatternsLockPick(UUID uuid) {
//        Player player = Bukkit.getPlayer(uuid);
//        Random rand = new Random();
//
//        // Chest dimensions
//        int min = 0;
//        int max = 9 * 4;
//        String difficulty = this.currentBlockMap.get(uuid).getPersistentDataContainer().get(Keys.DIFFICULTY_SETTING, PersistentDataType.STRING);
//
//        ArrayList<Integer> slotList = new ArrayList<>();
//        for (int i = 0; i < max; i++) {
//            slotList.add(i);
//        }
//        Collections.shuffle(slotList);
//        List<Integer> sList = slotList.subList(0, this.patternDiffMap.get(difficulty));
//        Set<Integer> patternSet = new HashSet<>(sList);
//        this.patternMap.put(uuid, patternSet);
//        Bukkit.getLogger().info("PatternMap:  " + this.patternMap.get(uuid));
//
//        Inventory inv = Bukkit.createInventory(player, 9 * 4, "Memorize");
//
//        for (int i : this.patternMap.get(uuid)) {
//            inv.setItem(i, getItem(new ItemStack(Material.BLACK_STAINED_GLASS_PANE), " ", ""));
//        }
//
//        player.openInventory(inv);
//        new DelayedTask(this.plugin);
//        new DelayedTask(() -> {
//            this.patternAnswer(uuid);
//        }, 10L * 5);
//
//    }
//
//    /**
//     * Creates inventory menu for the player to enter their guess
//     */
//    public void patternAnswer(UUID uuid) {
//        Player player = Bukkit.getPlayer(uuid);
//        Inventory inv = Bukkit.createInventory(player, 9 * 4, "Enter the Pattern");
//        this.invMap.put(uuid, inv);
//        assert player != null;
//        player.openInventory(this.invMap.get(uuid));
//    }
//    //TODO possibly redundant/unnecessary with enterPattern.  Decide if needed
//
//    /**
//     * Handles the pattern game end
//     *
//     * @param result result of the game
//     */
//    public void patternEnd(UUID uuid, Boolean result) {
//        Player player = Bukkit.getPlayer(uuid);
//        if (result) {
//            player.sendMessage(ChatColor.GREEN + "Success!!!!");
//
//            this.openBlock(uuid);
//        } else {
//            player.sendMessage(ChatColor.RED + "Lock pick broke");
//        }
//        this.initVars(uuid);
//    }
//
//    /**
//     * Checks the input to see if the player entered a correct tile for the pattern game.
//     * If not, increment fail attempts until max reached and game is ended.
//     *
//     * @param slot The slot entered by the player
//     */
//    public void enterPattern(UUID uuid, int slot) {
//        Player player = Bukkit.getPlayer(uuid);
//        // make new inv so changing inv in map clearer
//        Inventory inv = this.invMap.get(uuid);
//        // If slot has already been clicked, ignore
//        if (this.patternAnsMap.get(uuid).contains(slot)) {
//            //Bukkit.getLogger().info("Already entered");
//        } else if (this.patternMap.get(uuid).contains(slot)) {
//            this.patternAnsMap.get(uuid).add(slot);
//
//            inv.setItem(slot, getItem(new ItemStack(Material.GREEN_STAINED_GLASS_PANE), " ", ""));
//            this.invMap.put(uuid, inv);
//
//            if (this.patternAnsMap.get(uuid).size() == this.patternMap.get(uuid).size()) {
//                this.patternEnd(uuid, true);
//            }
//        } else {
//            inv.setItem(slot, getItem(new ItemStack(Material.RED_STAINED_GLASS_PANE), " ", ""));
//            this.invMap.put(uuid, inv);
//
//            this.patternIncMap.put(uuid, this.patternIncMap.get(uuid) + 1);
//            if (this.patternIncMap.get(uuid) == this.maxIncorrect) {
//                this.patternEnd(uuid, false);
//            }
//        }
//    }
//
//    /**
//     * Chimp Lock Pick Section
//     */
//
//    public void runChimpLockPick(UUID uuid) {
//        Player player = Bukkit.getPlayer(uuid);
//        int max = 9 * 4;
//        String difficulty = this.currentBlockMap.get(uuid).getPersistentDataContainer().get(Keys.DIFFICULTY_SETTING, PersistentDataType.STRING);
//
//        ArrayList<Integer> slotList = new ArrayList<>();
//        for (int i = 0; i < max; i++) {
//            slotList.add(i);
//        }
//        Collections.shuffle(slotList);
//        List<Integer> list = slotList.subList(0, this.chimpDiffMap.get(difficulty));
//        this.chimpMap.put(uuid, list);
//
//        CountdownTimer time = new CountdownTimer(this.plugin, this.chimpDiffMap.get(difficulty),
//                // What happens at the start
//                () -> {
//
//                },
//                // What happens at the end
//                () -> {
//                    player.closeInventory();
//                    this.chimpAnswerMenu(uuid);
//                },
//                // What happens during each tick
//                (t) -> {
//                    int n = t.getTotalSeconds() - t.getSecondsLeft();
//                    this.displayChimpTest(uuid, this.chimpMap.get(uuid).subList(0, n));
//
//                });
//        time.scheduleTimer();
//    }
//
//    public void displayChimpTest(UUID uuid, List<Integer> list) {
//        Player player = Bukkit.getPlayer(uuid);
//        Inventory inv = Bukkit.createInventory(player, 9 * 4, "Memorize Order");
//        for (Integer integer : list) {
//            inv.setItem(integer, getItem(new ItemStack(Material.BLACK_STAINED_GLASS_PANE), " ", ""));
//        }
//        assert player != null;
//        player.openInventory(inv);
//    }
//
//    public void chimpAnswerMenu(UUID uuid) {
//        Player player = Bukkit.getPlayer(uuid);
//        Inventory inv = Bukkit.createInventory(player, 9 * 4, "Enter Chimp Order");
//        for (int i : this.chimpMap.get(uuid)) {
//            inv.setItem(i, getItem(new ItemStack(Material.BLACK_STAINED_GLASS_PANE), " ", ""));
//        }
//        assert player != null;
//        player.openInventory(inv);
//    }
//
//    public void enterChimpPattern(UUID uuid, int slot) {
//        Player player = Bukkit.getPlayer(uuid);
//        int i = this.chimpMap.get(uuid).get(0);
//        if (Objects.equals(i, slot)) {
//            this.chimpMap.get(uuid).remove(0);
//            if (this.chimpMap.get(uuid).isEmpty()) {
//                player.sendMessage(ChatColor.GREEN + "Chest opened");
//                this.openBlock(uuid);
//
//                this.initVars(uuid);
//            }
//        } else {
//            player.sendMessage(ChatColor.RED + "Lock Pick Broke");
//            this.initVars(uuid);
//        }
//
//    }
//
//    /**
//     * Clears all variables
//     */
//    public void initVars(UUID uuid) {
//        Player player = Bukkit.getPlayer(uuid);
//        Set<Integer> tempSet = new HashSet<>();
//        List<Integer> tempList = new ArrayList<>();
//        Inventory tempInv = Bukkit.createInventory(player, 9 * 4, this.invName);
//        Integer[] tempArr = new Integer[5];
//
//        assert player != null;
//        player.closeInventory();
//        try {
//            this.numMap.put(uuid, 0);
//            this.seqMap.put(uuid, tempArr);
//            this.patternMap.put(uuid, tempSet);
//            this.patternAnsMap.put(uuid, tempSet);
//            this.patternIncMap.put(uuid, 0);
//            this.invMap.put(uuid, tempInv);
//            this.chimpMap.put(uuid, tempList);
//
//        } catch (Exception e) {
//            //Bukkit.getLogger().info("Tried to clear something that doesn't exist yet.  No worries");
//        }
//    }

    /**
     * General onClickEvent.  Determine what to do based on current inventory name.
     *
     * @param event inventory click
     */
    @EventHandler
    private void onInventoryClickEvent(InventoryClickEvent event) {
        int slot = event.getSlot();
        UUID uuid = event.getWhoClicked().getUniqueId();

        if (event.getView().getTitle().equals(this.invName)) {
//            if (slot == 0) {
//                event.setCancelled(true);
//                this.runColorLockPick(uuid);
//            } else if (slot == 1) {
//                event.setCancelled(true);
//                this.runPatternsLockPick(uuid);
//            } else if (slot == 2) {
//                event.setCancelled(true);
//                this.runChimpLockPick(uuid);
//            } else if (slot == 34) {
//                event.setCancelled(true);
//                this.lockAllChests();
//            } else if (slot == 35) {
//                event.setCancelled(true);
//                this.unlockAllChests();
//            }
        }
//        } else if (event.getView().getTitle().equals("Enter the Colors")) {
//            event.setCancelled(true);
//            this.enterColors(uuid, slot);
//        } else if (event.getView().getTitle().equals("Enter the Pattern")) {
//            event.setCancelled(true);
//            this.enterPattern(uuid, slot);
//        } else if (event.getView().getTitle().equals("Enter Chimp Order")) {
//            event.setCancelled(true);
//            this.enterChimpPattern(uuid, slot);
//        } else if (event.getView().getTitle().equals("Customize Lock")) {
//            event.setCancelled(true);
//            if (slot == 12) {
//                this.editLock(uuid, "Colors");
//                this.displayEditInv(uuid);
//            } else if (slot == 13) {
//                this.editLock(uuid, "Pattern");
//                this.displayEditInv(uuid);
//            } else if (slot == 14) {
//                this.editLock(uuid, "Chimp");
//                this.displayEditInv(uuid);
//            } else if (slot == 21) {
//                this.editLock(uuid, "All");
//                this.displayEditInv(uuid);
//            } else if (slot == 22) {
//                this.editDifficulty(uuid);
//                this.displayEditInv(uuid);
//            } else if (slot == 23) {
//                this.setAutoLock(uuid);
//                this.displayEditInv(uuid);
//            } else if (slot == 31) {
//                this.setBlockLock(uuid);
//                this.displayEditInv(uuid);
//            }
//        }
    }

    /**
     * /unlock command method.
     *
     * @param sender  who/what sent the command
     * @param command what command is being run
     * @param label   label
     * @param args    arguments
     * @return true
     */
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        Player player = (Player) sender;
        Inventory inv = Bukkit.createInventory(player, 9 * 4, this.invName);

        inv.setItem(0, getItem(new ItemStack(Material.DIAMOND_BLOCK), "&9START Colors", "&aBegin Lockpicking"));
        inv.setItem(1, getItem(new ItemStack(Material.DIAMOND_BLOCK), "&9START Patterns", "&aBegin Lockpicking"));
        inv.setItem(2, getItem(new ItemStack(Material.DIAMOND_BLOCK), "&9START Chimp", "&aBegin Lockpicking"));
        inv.setItem(5, getKey());
        inv.setItem(6, getAdminKey());
        inv.setItem(34, getItem(new ItemStack(Material.DIAMOND_BLOCK), "Lock all Chests", ""));
        inv.setItem(35, getItem(new ItemStack(Material.GOLD_BLOCK), "Unlock all Chests", ""));

        // Makes sure to init variables in case if inventory was manually closed previously and variables were never cleared
        this.minigames.initVars(player.getUniqueId());

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

    private ItemStack getKey() {
        ItemStack chest = new ItemStack(Material.KELP, 30);
        ItemMeta meta = chest.getItemMeta();
        assert meta != null;
        meta.setDisplayName("Rare Key");
        meta.setLore(Collections.singletonList("Use to open a chest"));
        meta.getPersistentDataContainer().set(Keys.CUSTOM_KEY, PersistentDataType.BOOLEAN, true);

        chest.setItemMeta(meta);

        return new ItemStack(chest);
    }
    private ItemStack getAdminKey() {
        ItemStack chest = new ItemStack(Material.LIGHTNING_ROD, 30);
        ItemMeta meta = chest.getItemMeta();
        assert meta != null;
        meta.setDisplayName("Admin Key");
        meta.setLore(Collections.singletonList("Use to customize lockables"));
        meta.getPersistentDataContainer().set(Keys.ADMIN_KEY, PersistentDataType.BOOLEAN, true);

        chest.setItemMeta(meta);

        return new ItemStack(chest);
    }

//    @EventHandler
//    private void openLockedEvent(PlayerInteractEvent event) {
//        try {
//            // This allows players to still destroy chests
//            if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
//                return;
//            }
//            Block block = event.getClickedBlock();
//            if (!(block.getState() instanceof TileState)) {
//                return;
//            }
//            if (event.getPlayer().getInventory().getItemInMainHand() == null) {
//                return;
//            }
//            TileState state = (TileState) event.getClickedBlock().getState();
//            PersistentDataContainer blockContainer = state.getPersistentDataContainer();
//
//            if (!blockContainer.has(Keys.CUSTOM_LOCK, PersistentDataType.BOOLEAN)) {
//                return;
//            }
//            // if locked, continue the unlock process
//            if (blockContainer.get(Keys.CUSTOM_LOCK, PersistentDataType.BOOLEAN)) {
//                Player player = event.getPlayer();
//                ItemStack item = player.getInventory().getItemInMainHand();
//                try {
//                    //if the item being held is a key
//                    if (Objects.requireNonNull(item.getItemMeta()).getPersistentDataContainer().has(Keys.CUSTOM_KEY, PersistentDataType.BOOLEAN)) {
//                        // Remove one key
//                        item.setAmount(item.getAmount() - 1);
//                        event.setCancelled(true);
//                        this.currentBlockMap.put(player.getUniqueId(), state);
//                        this.initVars(player.getUniqueId());
//                        this.runRandomSeq(player.getUniqueId());
//                    } else {
//                        event.getPlayer().sendMessage("You need a key!");
//                        event.setCancelled(true);
//                    }
//                } catch (Exception e) {
//                    event.setCancelled(true);
//                    player.sendMessage("You need a key!");
//                }
//                // If the chest is unlocked: lock if holding a key
//            } else {
//                Player player = event.getPlayer();
//                try {
//                    //PersistentDataContainer keyContainer = Objects.requireNonNull(item.getItemMeta()).getPersistentDataContainer();
//                    ItemStack item = player.getInventory().getItemInMainHand();
//                    //if the item being held is a key
//                    boolean t = item.getItemMeta().getPersistentDataContainer().has(Keys.CUSTOM_KEY, PersistentDataType.BOOLEAN);
//                    if (item.getItemMeta().getPersistentDataContainer().has(Keys.CUSTOM_KEY, PersistentDataType.BOOLEAN)) {
//                        item.setAmount(item.getAmount() - 1);
//                        event.setCancelled(true);
//                        blockContainer.set(Keys.CUSTOM_LOCK, PersistentDataType.BOOLEAN, true);
//                        state.update();
//                    } else {
//                        event.getPlayer().sendMessage("Still Unlocked!");
//                    }
//                } catch (Exception e) {
//
//                }
//            }
//        } catch (Exception e) {
//
//        }
//    }
//
////    @EventHandler
////    public void makeLockableEvent(PlayerInteractEvent event) {
////        try {
////            // This allows players to still destroy chests
////            if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
////                return;
////            }
////            Block block = event.getClickedBlock();
////            TileState state = (TileState) event.getClickedBlock().getState();
////            PersistentDataContainer blockContainer = state.getPersistentDataContainer();
////
////            Player player = event.getPlayer();
////            ItemStack item = player.getInventory().getItemInMainHand();
////            PersistentDataContainer itemContainer = item.getItemMeta().getPersistentDataContainer();
////
////            if (!(block.getState() instanceof TileState)) {
////                return;
////            }
////            // Ignore if not holding a key of if the block is already lockable
////            if (!(itemContainer.has(Keys.CUSTOM_KEY, PersistentDataType.BOOLEAN) && !blockContainer.has(Keys.CUSTOM_LOCK, PersistentDataType.BOOLEAN))) {
////                return;
////            }
////            blockContainer.set(Keys.CUSTOM_LOCK, PersistentDataType.BOOLEAN, true);
////            state.update();
////            this.chestSet.add(state);
////            Bukkit.getLogger().info("Added lock to block!");
////            player.sendMessage(ChatColor.GREEN + "Added a lock!");
////            event.setCancelled(true);
////        } catch (Exception e) {
////            //Bukkit.getLogger().info("Exception when adding lock");
////        }
////    }
//
//    /**
//     * If a block is clicked on with an Admin Key: set up customization options.
//     * @param event Player clicking on block.
//     */
//    @EventHandler
//    public void customizeLockableEvent(PlayerInteractEvent event) {
//        try {
//            // This allows players to still destroy chests
//            if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
//                return;
//            }
//            Block block = event.getClickedBlock();
//            TileState state = (TileState) event.getClickedBlock().getState();
//            PersistentDataContainer blockContainer = state.getPersistentDataContainer();
//
//            Player player = event.getPlayer();
//            ItemStack item = player.getInventory().getItemInMainHand();
//            PersistentDataContainer itemContainer = item.getItemMeta().getPersistentDataContainer();
//
//            if (!(block.getState() instanceof TileState)) {
//                return;
//            }
//            // Ignore if not holding an admin key
//            if (!itemContainer.has(Keys.ADMIN_KEY, PersistentDataType.BOOLEAN)) {
//                //Bukkit.getLogger().info("Not admin key");
//                return;
//            }
//            event.setCancelled(true);
//            Bukkit.getLogger().info("Admin key used!");
//            this.currentBlockMap.put(player.getUniqueId(), state);
//
//            // Setting Difficulty Persistent Data
//            if (!blockContainer.has(Keys.DIFFICULTY_SETTING, PersistentDataType.STRING)) {
//                Bukkit.getLogger().info("Difficulty not already set... Setting as normal.");
//                blockContainer.set(Keys.DIFFICULTY_SETTING, PersistentDataType.STRING, "Normal");
//            }
//            // Setting Custom Lock Persistent Data
//            if (!blockContainer.has(Keys.CUSTOM_LOCK, PersistentDataType.BOOLEAN)) {
//                Bukkit.getLogger().info("Lock not already set... Setting as unlocked.");
//                blockContainer.set(Keys.CUSTOM_LOCK, PersistentDataType.BOOLEAN, Boolean.FALSE);
//            }
//            // Setting Auto Lock Persistent Data
//            if (!blockContainer.has(Keys.AUTO_LOCK, PersistentDataType.BOOLEAN)) {
//                Bukkit.getLogger().info("Auto Lock not already set... Setting as off.");
//                blockContainer.set(Keys.AUTO_LOCK, PersistentDataType.BOOLEAN, Boolean.FALSE);
//            }
//
//            this.displayEditInv(player.getUniqueId());
//
//        } catch (Exception e) {
//            //Bukkit.getLogger().info("Exception when adding lock");
//        }
//    }
//
//    /**
//     * Sets up the inventory for customizing the bloack's lock
//     * @param uuid The ID of the player customizing the block
//     */
//    private void displayEditInv(UUID uuid) {
//        try {
//            Player player = Bukkit.getPlayer(uuid);
//            TileState state = this.currentBlockMap.get(uuid);
//            PersistentDataContainer blockContainer = state.getPersistentDataContainer();
//            ArrayList<String> locksList = new ArrayList<>();
//
//            if (blockContainer.has(Keys.LOCK_LIST, PersistentDataType.STRING)) {
//                String listString = blockContainer.get(Keys.LOCK_LIST, PersistentDataType.STRING);
//                String[] arr = listString.split("::");
//                locksList = new ArrayList<>(Arrays.asList(arr));
//            }
//
//            String colorsStr = (locksList.contains("Colors") ? "Remove" : "Add") + " Colors Lock";
//            String patternStr = (locksList.contains("Pattern") ? "Remove" : "Add") + " Pattern Lock";
//            String chimpStr = (locksList.contains("Chimp") ? "Remove" : "Add") + " Chimp Lock";
//            String allStr = (locksList.size() == 3 ? "Remove" : "Add") + " All Locks";
////            String list = (locksList.contains("Colors") ? "Colors Lock\n" : "") + (locksList.contains("Pattern") ? "Pattern Lock\n" : "") + (locksList.contains("Chimp") ? "Chimp Lock" : "");
////            Bukkit.getLogger().info(list);
//            String difficulty = blockContainer.get(Keys.DIFFICULTY_SETTING, PersistentDataType.STRING);
//            String autoLock = (blockContainer.get(Keys.AUTO_LOCK, PersistentDataType.BOOLEAN) ? "On" : "Off");
//
//            Inventory inv = Bukkit.createInventory(player, 9 * 4, "Customize Lock");
//
//            inv.setItem(4, getItem(new ItemStack(Material.CHEST), "Settings Applied", "Difficulty: " + difficulty, "Auto Lock: " + autoLock, (locksList.contains("Colors") ? "Colors Lock\n" : ""), (locksList.contains("Pattern") ? "Pattern Lock\n" : ""), (locksList.contains("Chimp") ? "Chimp Lock" : "")));
//            inv.setItem(12, getItem(new ItemStack(Material.RED_STAINED_GLASS_PANE), colorsStr, "Lock pick minigame rotation."));
//            inv.setItem(13, getItem(new ItemStack(Material.BAMBOO_HANGING_SIGN), patternStr, "Lock pick minigame rotation."));
//            inv.setItem(14, getItem(new ItemStack(Material.AXOLOTL_BUCKET), chimpStr, "Lock pick minigame rotation."));
//            inv.setItem(21, getItem(new ItemStack(Material.DIAMOND_BLOCK), allStr, "Lock pick minigame rotation."));
//            inv.setItem(22, getItem(new ItemStack(Material.TNT), "Difficulty Setting", "Currently: " + difficulty));
//            inv.setItem(23, getItem(new ItemStack(Material.IRON_DOOR), "Auto Lock", "Currently: " + autoLock));
//            inv.setItem(31, getItem(new ItemStack(Material.IRON_BARS), (blockContainer.get(Keys.CUSTOM_LOCK, PersistentDataType.BOOLEAN) ? "Unlock" : "Lock"), " "));
//
//            player.openInventory(inv);
//        } catch (Exception e) {
//
//        }
//
//    }
//
//    //TODO Persistent data for doors...
//    // https://www.spigotmc.org/threads/custom-block-data-persistentdatacontainer-for-blocks.512422/
//
//    //TODO Possibly unneeded... or figure out how to better implement
//    @EventHandler
//    private void closeInventoryEvent(InventoryCloseEvent event) {
//        if (event.getView().getTitle().equals("Customize Lock")) {
//            //Bukkit.getLogger().info("CLOSE EVENT");
//            UUID uuid = event.getPlayer().getUniqueId();
////            this.chestMap.remove(uuid);
//        }
//    }
//
//    public void editLock(UUID uuid, String type) {
//        TileState tileState = this.currentBlockMap.get(uuid);
//        PersistentDataContainer blockContainer = tileState.getPersistentDataContainer();
//        blockContainer.set(Keys.CUSTOM_LOCK, PersistentDataType.BOOLEAN, Boolean.TRUE);
//
//        ArrayList<String> locksList = new ArrayList<>();
//
//        if (blockContainer.has(Keys.LOCK_LIST, PersistentDataType.STRING)) {
//            String listString = blockContainer.get(Keys.LOCK_LIST, PersistentDataType.STRING);
//            String[] arr = listString.split("::");
//            locksList = new ArrayList<>(Arrays.asList(arr));
//        }
//
//        if (!type.equals("All")) {
//            if (locksList.contains(type)) {
//                locksList.remove(type);
//            } else {
//                locksList.add(type);
//            }
//        } else {
//            if (locksList.size() == 3) {
//                locksList.removeAll(locksList);
//            } else {
//                if (!locksList.contains("Colors")) {
//                    locksList.add("Colors");
//                }
//                if (!locksList.contains("Pattern")) {
//                    locksList.add("Pattern");
//                }
//                if (!locksList.contains("Chimp")) {
//                    locksList.add("Chimp");
//                }
//            }
//        }
//        //kept getting an empty variable in the beginning when using join
//        StringBuilder str = new StringBuilder();
//        for (String s : locksList) {
//            if (!Objects.equals(s, "")) {
//                str.append(s).append("::");
//            }
//        }
//        //Bukkit.getLogger().info("StringBuilder: " + str);
////        if (!locksList.isEmpty()) {
////            str = String.join("::", locksList);
////        }
//
//        blockContainer.set(Keys.LOCK_LIST, PersistentDataType.STRING, str.toString());
//        tileState.update();
//    }
//
//    /**
//     * Sets the difficulty of the lock pick mini-game of a block
//     * @param uuid User id of player editing the block
//     */
//    public void editDifficulty(UUID uuid) {
//        TileState tileState = this.currentBlockMap.get(uuid);
//        PersistentDataContainer blockContainer = tileState.getPersistentDataContainer();
//
//        if (!blockContainer.has(Keys.DIFFICULTY_SETTING, PersistentDataType.STRING)) {
//            blockContainer.set(Keys.DIFFICULTY_SETTING, PersistentDataType.STRING, "Normal");
//        }
//
//        switch (blockContainer.get(Keys.DIFFICULTY_SETTING, PersistentDataType.STRING)) {
//            case "Easy":
//                blockContainer.set(Keys.DIFFICULTY_SETTING, PersistentDataType.STRING, "Normal");
//                break;
//            case "Normal":
//                blockContainer.set(Keys.DIFFICULTY_SETTING, PersistentDataType.STRING, "Hard");
//                break;
//            case "Hard":
//                blockContainer.set(Keys.DIFFICULTY_SETTING, PersistentDataType.STRING, "Impossible");
//                break;
//            case "Impossible":
//                blockContainer.set(Keys.DIFFICULTY_SETTING, PersistentDataType.STRING, "Easy");
//                break;
//        }
//        tileState.update();
//    }
//
//    public void setAutoLock(UUID uuid) {
//        TileState tileState = this.currentBlockMap.get(uuid);
//        PersistentDataContainer blockContainer = tileState.getPersistentDataContainer();
//        blockContainer.set(Keys.AUTO_LOCK, PersistentDataType.BOOLEAN, !blockContainer.get(Keys.AUTO_LOCK, PersistentDataType.BOOLEAN)) ;
//        tileState.update();
//    }
//
//    public void setBlockLock(UUID uuid) {
//        TileState tileState = this.currentBlockMap.get(uuid);
//        PersistentDataContainer blockContainer = tileState.getPersistentDataContainer();
//        blockContainer.set(Keys.CUSTOM_LOCK, PersistentDataType.BOOLEAN, !blockContainer.get(Keys.CUSTOM_LOCK, PersistentDataType.BOOLEAN)) ;
//        tileState.update();
//    }
//
//    public void runRandomSeq(UUID uuid) {
//        String str = this.currentBlockMap.get(uuid).getPersistentDataContainer().get(Keys.LOCK_LIST, PersistentDataType.STRING);
//
//        // If no minigames selected, open chest
//        if (str.isEmpty()) {
//            this.openBlock(uuid);
//            return;
//        }
//
//        String[] arr = str.split("::");
//
//        Random rand = new Random();
//
//        int min = 0;
//        int max = arr.length;
//        int mode = rand.nextInt(max - min) + min;
//
//        switch (arr[mode]) {
//            case "Colors":
//                this.runColorLockPick(uuid);
//                break;
//            case "Pattern":
//                this.runPatternsLockPick(uuid);
//                break;
//            case "Chimp":
//                this.runChimpLockPick(uuid);
//                break;
//        }
//    }
//
//    public void openBlock(UUID uuid) {
//        Bukkit.getLogger().info("Opening Chest!");
//        PersistentDataContainer blockContainer = this.currentBlockMap.get(uuid).getPersistentDataContainer();
//        TileState tempState = this.currentBlockMap.get(uuid);
//        blockContainer.set(Keys.CUSTOM_LOCK, PersistentDataType.BOOLEAN, false);
//        tempState.update();
//
//        if (blockContainer.get(Keys.AUTO_LOCK, PersistentDataType.BOOLEAN)) {
//            new DelayedTask(() -> {
//                blockContainer.set(Keys.CUSTOM_LOCK, PersistentDataType.BOOLEAN, true);
//                tempState.update();
//                Bukkit.getLogger().info("Chest auto locked");
//            }, 10L * this.autoLockTimer);
//            this.currentBlockMap.remove(uuid);
//        }
//    }
//
    public void lockAllChests() {
        for (TileState tf : this.chestSet) {
            Chest chest = (Chest) tf;
            PersistentDataContainer chestContainer = chest.getPersistentDataContainer();
            chestContainer.set(Keys.CUSTOM_LOCK, PersistentDataType.BOOLEAN, true);
            tf.update();
        }
    }

    public void unlockAllChests() {
        for (TileState tf : this.chestSet) {
            Chest chest = (Chest) tf;
            PersistentDataContainer chestContainer = chest.getPersistentDataContainer();
            chestContainer.set(Keys.CUSTOM_LOCK, PersistentDataType.BOOLEAN, false);
            tf.update();
        }
    }
}
