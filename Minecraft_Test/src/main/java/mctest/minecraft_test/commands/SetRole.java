package mctest.minecraft_test.commands;

import mctest.minecraft_test.roles.Survivor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import mctest.minecraft_test.roles.Infected;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class SetRole implements CommandExecutor, Listener {
    private Infected infected;
    private Survivor survivor;

    public SetRole(Infected infected, Survivor survivor) {
        this.infected = infected;
        this.survivor = survivor;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(("Must be a player to run this command!"));
            return true;
        }
        Player player = (Player) sender;
        Bukkit.getLogger().info(player + "  was infected: " + infected.getI() + "  and was survivor: " + survivor.getSur());
        if (survivor.getSur()) {
            survivor.setSurvivor(player);
        }
        infected.setInfection(player);
        Bukkit.getLogger().info(player + "  is infected: " + infected.getI() + "  and was survivor: " + survivor.getSur());


        return true;
    }
}
