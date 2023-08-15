package mctest.minecraft_test.handlers;

import mctest.minecraft_test.Minecraft_Test;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

public class ItemHandler implements Listener {
    public ItemHandler(Minecraft_Test plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }


}
