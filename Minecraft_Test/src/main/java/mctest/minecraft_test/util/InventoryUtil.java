package mctest.minecraft_test.util;

import mctest.minecraft_test.Minecraft_Test;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.UUID;

public class InventoryUtil {
    public final Minecraft_Test plugin;
    private final HashMap<UUID, ItemStack[]> previousInventory = new HashMap<>();
    public HashMap<UUID, ItemStack[]> getPreviousInventory() {
        return previousInventory;
    }
    public InventoryUtil(Minecraft_Test plugin) {
        this.plugin = plugin;
    }

    public void clearInventory(Player player) {
        if(plugin.isInvHandling()){
            player.getInventory().clear();
            player.getInventory().setHelmet(null);
            player.getInventory().setChestplate(null);
            player.getInventory().setLeggings(null);
            player.getInventory().setBoots(null);

            org.bukkit.inventory.Inventory inv = player.getInventory();
            inv.clear();
        }
    }

    public void saveInventory(Player player) {
        if(plugin.isInvHandling()){
            int count = player.getInventory().getSize() + 4;
            Inventory inv = player.getInventory();
            ItemStack[] invStack = new ItemStack[count];

            for (int i = 0; i < player.getInventory().getSize(); i++) {
                invStack[i] = player.getInventory().getItem(i);
            }

            if (player.getInventory().getHelmet() != null) {
                invStack[count - 4] = player.getInventory().getHelmet();
            }

            if (player.getInventory().getChestplate() != null) {
                invStack[count - 3] = player.getInventory().getChestplate();
            }

            if (player.getInventory().getLeggings() != null) {
                invStack[count - 2] = player.getInventory().getLeggings();
            }

            if (player.getInventory().getBoots() != null) {
                invStack[count - 1] = player.getInventory().getBoots();
            }

            previousInventory.put(player.getUniqueId(), invStack.clone());
        }
    }


    public void giveInventory(Player player) {
        if(plugin.isInvHandling()){
            ItemStack[] inv = previousInventory.get(player.getUniqueId());
            int count = player.getInventory().getSize() + 4;

            for (int i = 0; i < count; i++) {
                if (i < (count - 4)) {
                    player.getInventory().setItem(i, inv[i]);
                } else if (i == (count - 4)) {
                    player.getInventory().setHelmet(inv[i]);
                } else if (i == (count - 3)) {
                    player.getInventory().setChestplate(inv[i]);
                } else if (i == (count - 2)) {
                    player.getInventory().setLeggings(inv[i]);
                } else if (i == (count - 1)) {
                    player.getInventory().setBoots(inv[i]);
                }
            }
        }
    }
}
