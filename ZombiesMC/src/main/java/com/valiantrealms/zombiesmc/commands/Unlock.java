package com.valiantrealms.zombiesmc.commands;

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
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;

//TODO
// Add loot to chests
// Possibly make list which is created from yml which contains lockpick games
// Make file for all chests for 'lock/unlock all' since current set is lost at server restart
public class Unlock implements Listener, CommandExecutor {
    private final ZombiesMC plugin;
    private final String invName = "Unlock Chest";
    private final HashMap<UUID, Integer[]> seqMap = new HashMap<>();
    private final HashMap<UUID, Integer> numMap = new HashMap<>();
    private final HashMap<UUID, Set<Integer>> patternMap = new HashMap<>();
    private final HashMap<UUID, Set<Integer>> patternAnsMap = new HashMap<>();
    private final HashMap<UUID, Inventory> invMap = new HashMap<>();
    private final HashMap<UUID, Integer> patternIncMap = new HashMap<>();
    private final HashMap<UUID, List<Integer>> chimpMap = new HashMap<>();
    private final HashMap<Integer, ItemStack> keyMap = new HashMap<Integer, ItemStack>();
    private final HashMap<UUID, TileState> chestMap = new HashMap<>();
    private final Set<TileState> chestSet = new HashSet<>();

    //TODO These three will most likely be config options
    private final int maxIncorrect = 3;
    private final int numSlots = 5;
    private final int chimpLength = 5;
    private final int autoLockTimer = 15;

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

        this.keyMap.put(2, getItem(new ItemStack(Material.RED_STAINED_GLASS_PANE), ChatColor.RED + "RED", ""));
        this.keyMap.put(3, getItem(new ItemStack(Material.BLUE_STAINED_GLASS_PANE), ChatColor.BLUE + "BLUE", ""));
        this.keyMap.put(4, getItem(new ItemStack(Material.GREEN_STAINED_GLASS_PANE), ChatColor.GREEN + "GREEN", ""));
        this.keyMap.put(5, getItem(new ItemStack(Material.YELLOW_STAINED_GLASS_PANE), ChatColor.YELLOW + "YELLOW", ""));

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
        CountdownTimer time = new CountdownTimer(this.plugin, 10,
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
        Bukkit.getLogger().info(Arrays.toString(this.seqMap.get(uuid)));
        Inventory inv = Bukkit.createInventory(player, 9, "Enter the Colors");
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
            if (this.seqMap.get(uuid)[this.numMap.get(uuid)] != slot) {
                player.sendMessage(ChatColor.RED + "Lock pick broke!");
                player.closeInventory();
                this.initVars(uuid);
                this.chestMap.remove(uuid);
                //this.colorsResult(false);
            } else if (this.numMap.get(uuid) == 4) {
                player.sendMessage(ChatColor.GREEN + "Correct!");
                player.closeInventory();
                this.initVars(uuid);
                this.openChest(uuid);

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

        ArrayList<Integer> slotList = new ArrayList<>();
        for (int i = 0; i < max; i++) {
            slotList.add(i);
        }
        Collections.shuffle(slotList);
        List<Integer> sList = slotList.subList(0, this.numSlots);
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

            this.openChest(uuid);
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
                this.openChest(uuid);

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
        Inventory tempInv = Bukkit.createInventory(player, 9 * 4, this.invName);
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

        if (event.getView().getTitle().equals(this.invName)) {
            if (slot == 0) {
                event.setCancelled(true);
                this.runColorLockPick(uuid);
            } else if (slot == 1) {
                event.setCancelled(true);
                this.runPatternsLockPick(uuid);
            } else if (slot == 2) {
                event.setCancelled(true);
                this.runChimpLockPick(uuid);
            } else if (slot == 34) {
                event.setCancelled(true);
                this.lockAllChests();
            } else if (slot == 35) {
                event.setCancelled(true);
                this.unlockAllChests();
            }
        } else if (event.getView().getTitle().equals("Enter the Colors")) {
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
        inv.setItem(4, getChest());
        inv.setItem(5, getKey());
        inv.setItem(6, getItem(new ItemStack(Material.IRON_DOOR), "", ""));
        inv.setItem(34, getItem(new ItemStack(Material.DIAMOND_BLOCK), "Lock all Chests", ""));
        inv.setItem(35, getItem(new ItemStack(Material.GOLD_BLOCK), "Unlock all Chests", ""));

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

    private ItemStack getChest() {
        ItemStack chest = new ItemStack(Material.CHEST);
        ItemMeta meta = chest.getItemMeta();
        assert meta != null;
        meta.setDisplayName("Rare Chest");
        meta.setLore(Arrays.asList("The chest is locked!", "Requires a key."));
        meta.getPersistentDataContainer().set(Keys.CUSTOM_CHEST, PersistentDataType.BOOLEAN, true);

        chest.setItemMeta(meta);

        return new ItemStack(chest);
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

    @EventHandler
    private void onEntityPlaceEvent(BlockPlaceEvent event) {
        if (event.getBlock().getType() == Material.IRON_DOOR) {
            Bukkit.getLogger().info("IRON DOOR PLACED!");
        }
        if (event.getBlock().getType() != Material.CHEST) {
            return;
        }
        if (!(event.getBlock().getState() instanceof TileState)) {
            return;
        }
        TileState state = (TileState) event.getBlock().getState();
        PersistentDataContainer chestContainer = state.getPersistentDataContainer();
        Chest chest = (Chest) event.getBlock().getState();

        // If the chest is a custom chest, give it the persistent keys
        if (Objects.equals(chest.getCustomName(), "Rare Chest")) {
            chestContainer.set(Keys.CUSTOM_CHEST, PersistentDataType.BOOLEAN, true);
            Bukkit.getLogger().info("Locked:   " + chestContainer.get(Keys.CUSTOM_CHEST, PersistentDataType.BOOLEAN));
            state.update();
            this.chestSet.add(state);
        }
    }

    @EventHandler
    private void onEntityOpenEvent(PlayerInteractEvent event) {
        try {
            if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
                return;
            }
            Block block = event.getClickedBlock();
            if (block.getType() != Material.CHEST) {
                return;
            }
            if (!(block.getState() instanceof TileState)) {
                return;
            }
            //todo POSSIBLY CAN REMOVE
            //Chest chest = (Chest) block.getState();
            TileState state = (TileState) event.getClickedBlock().getState();
            PersistentDataContainer chestContainer = state.getPersistentDataContainer();

            Bukkit.getLogger().info("Locked:   " + chestContainer.get(Keys.CUSTOM_CHEST, PersistentDataType.BOOLEAN));

            if (!chestContainer.has(Keys.CUSTOM_CHEST, PersistentDataType.BOOLEAN)) {
                return;
            }
            //make sure chest is a rare chest
            if (chestContainer.has(Keys.CUSTOM_CHEST, PersistentDataType.BOOLEAN)) {
                // if locked, continue the unlock process
                if (chestContainer.get(Keys.CUSTOM_CHEST, PersistentDataType.BOOLEAN)) {
                    Player player = event.getPlayer();
                    ItemStack item = player.getInventory().getItemInMainHand();
                    try {
                        //todo POSSIBLY CAN REMOVE
                        //PersistentDataContainer keyContainer = Objects.requireNonNull(item.getItemMeta()).getPersistentDataContainer();

                        //if the item being held is a key
                        if (Objects.requireNonNull(item.getItemMeta()).getPersistentDataContainer().has(Keys.CUSTOM_KEY, PersistentDataType.BOOLEAN)) {
                            // Remove one key
                            item.setAmount(item.getAmount() - 1);
                            event.setCancelled(true);
                            this.chestMap.put(player.getUniqueId(), state);
                            this.initVars(player.getUniqueId());
                            this.runRandomSeq(player.getUniqueId());
                        } else {
                            event.getPlayer().sendMessage("You need a key!");
                            event.setCancelled(true);
                        }
                    } catch (Exception e) {
                        event.setCancelled(true);
                        player.sendMessage("You need a key!");
                    }
                // If the chest is unlocked: lock if holding a key
                } else {
                    Player player = event.getPlayer();
                    //TODO make if more specific to non-rare chests to prevent compatibility issues
                    if (!player.getInventory().getItemInMainHand().hasItemMeta()) {
                        // Ignore is normal chest
                        //Bukkit.getLogger().info("Open chest!");
                    } else {
                        ItemStack item = player.getInventory().getItemInMainHand();
                        try {
                            //PersistentDataContainer keyContainer = Objects.requireNonNull(item.getItemMeta()).getPersistentDataContainer();

                            //if the item being held is a key
                            if (Objects.requireNonNull(item.getItemMeta()).getPersistentDataContainer().has(Keys.CUSTOM_KEY, PersistentDataType.BOOLEAN)) {
                                item.setAmount(item.getAmount() - 1);
                                event.setCancelled(true);
                                chestContainer.set(Keys.CUSTOM_CHEST, PersistentDataType.BOOLEAN, true);
                                state.update();
                                Bukkit.getLogger().info("Locked:   " + chestContainer.get(Keys.CUSTOM_CHEST, PersistentDataType.BOOLEAN));

                            } else {
                                event.getPlayer().sendMessage("Still Unlocked!");

                            }
                        } catch (Exception e) {
                            event.setCancelled(true);
                            player.sendMessage("You need a key!");
                        }
                    }
                }
            }
        } catch (Exception e) {

        }
    }
    //TODO Persistant data for doors...
    // https://www.spigotmc.org/threads/custom-block-data-persistentdatacontainer-for-blocks.512422/

    public void runRandomSeq(UUID uuid) {
        Random rand = new Random();

        int min = 1;
        int max = 3;
        int mode = rand.nextInt(max - min + 1) + min;

        switch (mode) {
            case 1:
                this.runColorLockPick(uuid);
                break;
            case 2:
                this.runPatternsLockPick(uuid);
                break;
            case 3:
                this.runChimpLockPick(uuid);
                break;
        }
    }

    public void openChest(UUID uuid) {
        PersistentDataContainer chestContainer = this.chestMap.get(uuid).getPersistentDataContainer();
        TileState tempState = this.chestMap.get(uuid);
        chestContainer.set(Keys.CUSTOM_CHEST, PersistentDataType.BOOLEAN, false);
        tempState.update();

        new DelayedTask(() -> {
            chestContainer.set(Keys.CUSTOM_CHEST, PersistentDataType.BOOLEAN, true);
            tempState.update();
            Bukkit.getLogger().info("Chest auto locked");
        }, 10L * this.autoLockTimer);
        this.chestMap.remove(uuid);

        Bukkit.getLogger().info("Locked:   " + chestContainer.get(Keys.CUSTOM_CHEST, PersistentDataType.BOOLEAN));
    }

    public void lockAllChests() {
        for (TileState tf : this.chestSet) {
            Chest chest = (Chest) tf;
            PersistentDataContainer chestContainer = chest.getPersistentDataContainer();
            chestContainer.set(Keys.CUSTOM_CHEST, PersistentDataType.BOOLEAN, true);
            tf.update();
        }
    }

    public void unlockAllChests() {
        for (TileState tf : this.chestSet) {
            Chest chest = (Chest) tf;
            PersistentDataContainer chestContainer = chest.getPersistentDataContainer();
            chestContainer.set(Keys.CUSTOM_CHEST, PersistentDataType.BOOLEAN, false);
            tf.update();
        }
    }
}
