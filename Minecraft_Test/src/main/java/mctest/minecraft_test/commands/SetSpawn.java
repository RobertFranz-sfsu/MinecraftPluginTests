package mctest.minecraft_test.commands;

import mctest.minecraft_test.Minecraft_Test;
import mctest.minecraft_test.util.ConfigUtil;
import mctest.minecraft_test.util.SpawnUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetSpawn implements CommandExecutor {
    private SpawnUtil spawnUtil;

    public SetSpawn(SpawnUtil spawnUtil) {
        this.spawnUtil = spawnUtil;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String [] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }

        Player player = (Player) sender;
        spawnUtil.set(player.getLocation());
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aSet the server spawn."));

        return true;
    }
}
