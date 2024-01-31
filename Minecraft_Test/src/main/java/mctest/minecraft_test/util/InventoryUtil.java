package mctest.minecraft_test.util;

import mctest.minecraft_test.Minecraft_Test;
import org.bukkit.entity.Player;
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
        player.getInventory().clear();
        player.getInventory().setHelmet(null);
        player.getInventory().setChestplate(null);
        player.getInventory().setLeggings(null);
        player.getInventory().setBoots(null);

        org.bukkit.inventory.Inventory inv = player.getInventory();
        inv.clear();
    }

    public void saveInventory(Player player) {
        int count = player.getInventory().getSize() + 4;
        ItemStack[] inv = new ItemStack[count];

        for (int i = 0; i < player.getInventory().getSize(); i++) {
            inv[i] = player.getInventory().getItem(i);
        }

        if (player.getInventory().getHelmet() != null) {
            inv[count - 4] = player.getInventory().getHelmet();
        }

        if (player.getInventory().getChestplate() != null) {
            inv[count - 3] = player.getInventory().getChestplate();
        }

        if (player.getInventory().getLeggings() != null) {
            inv[count - 2] = player.getInventory().getLeggings();
        }

        if (player.getInventory().getBoots() != null) {
            inv[count - 1] = player.getInventory().getBoots();
        }

        previousInventory.put(player.getUniqueId(), inv.clone());
    }


    public void giveInventory(Player player) {
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
