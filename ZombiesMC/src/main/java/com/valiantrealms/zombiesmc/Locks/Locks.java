package com.valiantrealms.zombiesmc.Locks;

import com.valiantrealms.zombiesmc.ZombiesMC;
import com.valiantrealms.zombiesmc.util.DelayedTask;
import com.valiantrealms.zombiesmc.util.ItemUtil;
import com.valiantrealms.zombiesmc.util.Keys;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.TileState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;

public class Locks implements Listener {
    private final ZombiesMC plugin;
    private final ItemUtil itemUtil = new ItemUtil();

    // currentBlockMap is the block the player is currently interacting with
    private final HashMap<UUID, TileState> currentBlockMap = new HashMap<>();
    //private final HashMap<TileState, ArrayList<String>> chestLocksMap = new HashMap<>();
    private final Set<TileState> chestSet = new HashSet<>();
    private final int autoLockTimer = 10;

    public Locks(ZombiesMC plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
        this.plugin = plugin;
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

        if (event.getView().getTitle().equals("Customize Lock")) {
            event.setCancelled(true);
            if (slot == 11) {
                this.editLock(uuid, "colors");
                this.displayEditInv(uuid);
            } else if (slot == 12) {
                this.editLock(uuid, "pattern");
                this.displayEditInv(uuid);
            } else if (slot == 13) {
                this.editLock(uuid, "chimp");
                this.displayEditInv(uuid);
            } else if (slot == 14) {
                this.editLock(uuid, "mole");
                this.displayEditInv(uuid);
            } else if (slot == 15) {
                this.editLock(uuid, "all");
                this.displayEditInv(uuid);
            } else if (slot == 21) {
                this.editDifficulty(uuid);
                this.displayEditInv(uuid);
            } else if (slot == 22) {
                this.setAutoLock(uuid);
                this.displayEditInv(uuid);
            } else if (slot == 31) {
                this.setBlockLock(uuid);
                this.displayEditInv(uuid);
            } else if (slot == 23) {
                this.setLockable(uuid);
                this.displayEditInv(uuid);
            }
        }
    }

    @EventHandler
    private void openLockedEvent(PlayerInteractEvent event) {
        try {
            // This allows players to still destroy chests
            if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
                return;
            }
            Block block = event.getClickedBlock();
            if (!(block.getState() instanceof TileState)) {
                return;
            }
            if (event.getPlayer().getInventory().getItemInMainHand() == null) {
                return;
            }
            TileState state = (TileState) event.getClickedBlock().getState();
            PersistentDataContainer blockContainer = state.getPersistentDataContainer();

            if (!blockContainer.has(Keys.CUSTOM_LOCK, PersistentDataType.BOOLEAN)) {
                return;
            }
            // if locked, continue the unlock process
            if (blockContainer.get(Keys.CUSTOM_LOCK, PersistentDataType.BOOLEAN)) {
                Player player = event.getPlayer();
                ItemStack item = player.getInventory().getItemInMainHand();
                try {
                    //if the item being held is a key
                    if (Objects.requireNonNull(item.getItemMeta()).getPersistentDataContainer().has(Keys.CUSTOM_KEY, PersistentDataType.BOOLEAN)) {
                        // Remove one key
                        item.setAmount(item.getAmount() - 1);
                        event.setCancelled(true);
                        this.currentBlockMap.put(player.getUniqueId(), state);
                        //this.minigames.initVars(player.getUniqueId());
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
                if (blockContainer.get(Keys.LOCKABLE, PersistentDataType.BOOLEAN)) {
                    Player player = event.getPlayer();
                    try {
                        ItemStack item = player.getInventory().getItemInMainHand();
                        //if the item being held is a key
                        if (item.getItemMeta().getPersistentDataContainer().has(Keys.CUSTOM_KEY, PersistentDataType.BOOLEAN)) {
                            item.setAmount(item.getAmount() - 1);
                            event.setCancelled(true);
                            blockContainer.set(Keys.CUSTOM_LOCK, PersistentDataType.BOOLEAN, true);
                            player.sendMessage(ChatColor.GREEN + "Locked!");
                            state.update();
                        } else {
                            event.getPlayer().sendMessage("Still Unlocked!");
                        }
                    } catch (Exception e) {

                    }
                }
            }
        } catch (Exception e) {

        }
    }

    /**
     * If a block is clicked on with an Admin Key: set up customization options.
     * @param event Player clicking on block.
     */
    @EventHandler
    public void customizeLockableEvent(PlayerInteractEvent event) {
        try {
            // This allows players to still destroy chests
            if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
                return;
            }
            Block block = event.getClickedBlock();
            TileState state = (TileState) event.getClickedBlock().getState();
            PersistentDataContainer blockContainer = state.getPersistentDataContainer();

            Player player = event.getPlayer();
            ItemStack item = player.getInventory().getItemInMainHand();
            PersistentDataContainer itemContainer = item.getItemMeta().getPersistentDataContainer();

            if (!(block.getState() instanceof TileState)) {
                return;
            }
            // Ignore if not holding an admin key
            if (!itemContainer.has(Keys.ADMIN_KEY, PersistentDataType.BOOLEAN)) {
                //Bukkit.getLogger().info("Not admin key");
                return;
            }
            event.setCancelled(true);
            Bukkit.getLogger().info("Admin key used!");
            this.currentBlockMap.put(player.getUniqueId(), state);

            // Setting Difficulty Persistent Data
            if (!blockContainer.has(Keys.DIFFICULTY_SETTING, PersistentDataType.STRING)) {
                Bukkit.getLogger().info("Difficulty not already set... Setting as normal.");
                blockContainer.set(Keys.DIFFICULTY_SETTING, PersistentDataType.STRING, "normal");
            }
            // Setting Custom Lock Persistent Data
            if (!blockContainer.has(Keys.CUSTOM_LOCK, PersistentDataType.BOOLEAN)) {
                Bukkit.getLogger().info("Lock not already set... Setting as unlocked.");
                blockContainer.set(Keys.CUSTOM_LOCK, PersistentDataType.BOOLEAN, Boolean.FALSE);
            }
            // Setting Auto Lock Persistent Data
            if (!blockContainer.has(Keys.AUTO_LOCK, PersistentDataType.BOOLEAN)) {
                Bukkit.getLogger().info("Auto Lock not already set... Setting as off.");
                blockContainer.set(Keys.AUTO_LOCK, PersistentDataType.BOOLEAN, Boolean.FALSE);
            }
            if (!blockContainer.has(Keys.LOCKABLE, PersistentDataType.BOOLEAN)) {
                Bukkit.getLogger().info("Lockability not already set... Setting as off.");
                blockContainer.set(Keys.LOCKABLE, PersistentDataType.BOOLEAN, Boolean.FALSE);
            }

            this.displayEditInv(player.getUniqueId());

        } catch (Exception e) {
            //Bukkit.getLogger().info("Exception when adding lock");
        }
    }

    /**
     * Sets up the inventory for customizing the bloack's lock
     * @param uuid The ID of the player customizing the block
     */
    public void displayEditInv(UUID uuid) {
        try {
            Player player = Bukkit.getPlayer(uuid);
            TileState state = this.currentBlockMap.get(uuid);
            PersistentDataContainer blockContainer = state.getPersistentDataContainer();
            ArrayList<String> locksList = new ArrayList<>();

            if (blockContainer.has(Keys.LOCK_LIST, PersistentDataType.STRING)) {
                String listString = blockContainer.get(Keys.LOCK_LIST, PersistentDataType.STRING);
                String[] arr = listString.split("::");
                locksList = new ArrayList<>(Arrays.asList(arr));
            }

            String colorsStr = (locksList.contains("colors") ? "Remove" : "Add") + " Colors Lock";
            String patternStr = (locksList.contains("pattern") ? "Remove" : "Add") + " Pattern Lock";
            String chimpStr = (locksList.contains("chimp") ? "Remove" : "Add") + " Chimp Lock";
            String moleStr = (locksList.contains("mole") ? "Remove" : "Add") + " Mole Lock";
            String allStr = (locksList.size() == 3 ? "Remove" : "Add") + " All Locks";
//            String list = (locksList.contains("Colors") ? "Colors Lock\n" : "") + (locksList.contains("Pattern") ? "Pattern Lock\n" : "") + (locksList.contains("Chimp") ? "Chimp Lock" : "");
//            Bukkit.getLogger().info(list);
            String difficulty = blockContainer.get(Keys.DIFFICULTY_SETTING, PersistentDataType.STRING);
            String autoLock = (blockContainer.get(Keys.AUTO_LOCK, PersistentDataType.BOOLEAN) ? "On" : "Off");

            Inventory inv = Bukkit.createInventory(player, 9 * 4, "Customize Lock");

            inv.setItem(4, this.itemUtil.getItem(new ItemStack(Material.CHEST), "Settings Applied", "Difficulty: " + difficulty, "Auto Lock: " + autoLock, "", (locksList.contains("colors") ? "Colors Lock" : ""), (locksList.contains("pattern") ? "Pattern Lock" : ""), (locksList.contains("Chimp") ? "chimp Lock" : ""), (locksList.contains("mole") ? "Mole Lock" : "")));

            inv.setItem(11, this.itemUtil.getItem(new ItemStack(Material.RED_STAINED_GLASS_PANE), colorsStr, "Simon Says."));
            inv.setItem(12, this.itemUtil.getItem(new ItemStack(Material.BAMBOO_HANGING_SIGN), patternStr, "Remember the pattern."));
            inv.setItem(13, this.itemUtil.getItem(new ItemStack(Material.AXOLOTL_BUCKET), chimpStr, "Remember the order."));
            inv.setItem(14, this.itemUtil.getItem(new ItemStack(Material.EGG), moleStr, "Whack a mole."));
            inv.setItem(15, this.itemUtil.getItem(new ItemStack(Material.DIAMOND_BLOCK), allStr, "Lock pick minigame rotation."));

            inv.setItem(21, this.itemUtil.getItem(new ItemStack(Material.TNT), "Difficulty Setting", "Currently: " + difficulty));
            inv.setItem(22, this.itemUtil.getItem(new ItemStack(Material.IRON_DOOR), "Auto Lock", "Currently: " + autoLock));
            inv.setItem(23, this.itemUtil.getItem(new ItemStack(Material.KELP), "Set as " + (blockContainer.get(Keys.LOCKABLE, PersistentDataType.BOOLEAN) ? "Unlockable" : "Lockable"), "Determines if it can be locked with a key."));

            inv.setItem(31, this.itemUtil.getItem(new ItemStack(Material.IRON_BARS), (blockContainer.get(Keys.CUSTOM_LOCK, PersistentDataType.BOOLEAN) ? "Unlock" : "Lock"), " "));

            player.openInventory(inv);
        } catch (Exception e) {

        }

    }

    //TODO Persistent data for doors...
    // https://www.spigotmc.org/threads/custom-block-data-persistentdatacontainer-for-blocks.512422/

    public void editLock(UUID uuid, String type) {
        TileState tileState = this.currentBlockMap.get(uuid);
        PersistentDataContainer blockContainer = tileState.getPersistentDataContainer();
        blockContainer.set(Keys.CUSTOM_LOCK, PersistentDataType.BOOLEAN, Boolean.TRUE);

        ArrayList<String> locksList = new ArrayList<>();

        if (blockContainer.has(Keys.LOCK_LIST, PersistentDataType.STRING)) {
            String listString = blockContainer.get(Keys.LOCK_LIST, PersistentDataType.STRING);
            String[] arr = listString.split("::");
            locksList = new ArrayList<>(Arrays.asList(arr));
        }

        if (!type.equals("All")) {
            if (locksList.contains(type)) {
                locksList.remove(type);
            } else {
                locksList.add(type);
            }
        } else {
            if (locksList.size() == 4) {
                locksList.removeAll(locksList);
            } else {
                if (!locksList.contains("colors")) {
                    locksList.add("colors");
                }
                if (!locksList.contains("pattern")) {
                    locksList.add("pattern");
                }
                if (!locksList.contains("chimp")) {
                    locksList.add("chimp");
                }
                if (!locksList.contains("mole")) {
                    locksList.add("mole");
                }
            }
        }
        //kept getting an empty variable in the beginning when using join
        StringBuilder str = new StringBuilder();
        for (String s : locksList) {
            if (!Objects.equals(s, "")) {
                str.append(s).append("::");
            }
        }
        //Bukkit.getLogger().info("StringBuilder: " + str);
//        if (!locksList.isEmpty()) {
//            str = String.join("::", locksList);
//        }

        blockContainer.set(Keys.LOCK_LIST, PersistentDataType.STRING, str.toString());
        tileState.update();
    }

    /**
     * Sets the difficulty of the lock pick mini-game of a block
     * @param uuid User id of player editing the block
     */
    public void editDifficulty(UUID uuid) {
        TileState tileState = this.currentBlockMap.get(uuid);
        PersistentDataContainer blockContainer = tileState.getPersistentDataContainer();

        if (!blockContainer.has(Keys.DIFFICULTY_SETTING, PersistentDataType.STRING)) {
            blockContainer.set(Keys.DIFFICULTY_SETTING, PersistentDataType.STRING, "Normal");
        }

        switch (blockContainer.get(Keys.DIFFICULTY_SETTING, PersistentDataType.STRING)) {
            case "easy":
                blockContainer.set(Keys.DIFFICULTY_SETTING, PersistentDataType.STRING, "normal");
                break;
            case "normal":
                blockContainer.set(Keys.DIFFICULTY_SETTING, PersistentDataType.STRING, "hard");
                break;
            case "hard":
                blockContainer.set(Keys.DIFFICULTY_SETTING, PersistentDataType.STRING, "impossible");
                break;
            case "impossible":
                blockContainer.set(Keys.DIFFICULTY_SETTING, PersistentDataType.STRING, "easy");
                break;
        }
        tileState.update();
    }

    public void setAutoLock(UUID uuid) {
        TileState tileState = this.currentBlockMap.get(uuid);
        PersistentDataContainer blockContainer = tileState.getPersistentDataContainer();
        blockContainer.set(Keys.AUTO_LOCK, PersistentDataType.BOOLEAN, !blockContainer.get(Keys.AUTO_LOCK, PersistentDataType.BOOLEAN)) ;
        tileState.update();
    }

    public void setBlockLock(UUID uuid) {
        TileState tileState = this.currentBlockMap.get(uuid);
        PersistentDataContainer blockContainer = tileState.getPersistentDataContainer();
        blockContainer.set(Keys.CUSTOM_LOCK, PersistentDataType.BOOLEAN, !blockContainer.get(Keys.CUSTOM_LOCK, PersistentDataType.BOOLEAN)) ;
        tileState.update();
    }

    public void runRandomSeq(UUID uuid) {
        String str = this.currentBlockMap.get(uuid).getPersistentDataContainer().get(Keys.LOCK_LIST, PersistentDataType.STRING);

        // If no minigames selected, open chest
        if (str.isEmpty()) {
            this.openBlock(uuid);
            return;
        }

        String[] arr = str.split("::");

        Random rand = new Random();

        int min = 0;
        int max = arr.length;
        int mode = rand.nextInt(max - min) + min;

        Minigames minigame = new Minigames(plugin, this, uuid);

        switch (arr[mode]) {
            case "colors":
                minigame.runColorLockPick();
                break;
            case "pattern":
                minigame.runPatternsLockPick();
                break;
            case "chimp":
                minigame.runChimpLockPick();
                break;
            case "mole":
                minigame.runMoleLockPick();
                break;
        }

    }

    public void openBlock(UUID uuid) {
        PersistentDataContainer blockContainer = this.currentBlockMap.get(uuid).getPersistentDataContainer();
        TileState tempState = this.currentBlockMap.get(uuid);
        blockContainer.set(Keys.CUSTOM_LOCK, PersistentDataType.BOOLEAN, false);
        tempState.update();

        if (blockContainer.get(Keys.AUTO_LOCK, PersistentDataType.BOOLEAN)) {
            new DelayedTask(() -> {
                blockContainer.set(Keys.CUSTOM_LOCK, PersistentDataType.BOOLEAN, true);
                tempState.update();
                Bukkit.getLogger().info("Chest auto locked");
            }, 10L * this.autoLockTimer);
            this.currentBlockMap.remove(uuid);
        }
    }
    public void setLockable(UUID uuid) {
        TileState tileState = this.currentBlockMap.get(uuid);
        PersistentDataContainer blockContainer = tileState.getPersistentDataContainer();
        blockContainer.set(Keys.LOCKABLE, PersistentDataType.BOOLEAN, !blockContainer.get(Keys.LOCKABLE, PersistentDataType.BOOLEAN)) ;
        tileState.update();
    }

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
    public HashMap<UUID, TileState> getCurrentBlockMap() {
        return this.currentBlockMap;
    }
}
