package mctest.minecraft_test.commands;

import mctest.minecraft_test.Minecraft_Test;
import mctest.minecraft_test.roles.GamesList;
import mctest.minecraft_test.roles.SurvivalPlayer;
import mctest.minecraft_test.util.ConfigUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Reload implements CommandExecutor {

    SurvivalPlayer s;
    @SuppressWarnings("FieldMayBeFinal")
    private GamesList g;

    public Reload(SurvivalPlayer s, GamesList g){
        this.g = g;
        this.s = s;
    }

    @SuppressWarnings("NullableProblems") // Removing the warning from the passed in objects.
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        try{
            String world = ((Player) sender).getWorld().getName();
            Minecraft_Test.getPlugin(Minecraft_Test.class).reloadConfig();
            Bukkit.getLogger().info("Reloading config.yml.");

            ConfigUtil c1 = new ConfigUtil(Minecraft_Test.getPlugin(Minecraft_Test.class), "Infected.yml");
            Bukkit.getLogger().info("Reloading Infected.yml.");
            c1.save();

            ConfigUtil c2 = new ConfigUtil(Minecraft_Test.getPlugin(Minecraft_Test.class), "Survivor.yml");
            Bukkit.getLogger().info("Reloading Survivor.yml.");
            c2.save();

            ConfigUtil c3 = new ConfigUtil(Minecraft_Test.getPlugin(Minecraft_Test.class), "Loadouts.yml");
            Bukkit.getLogger().info("Reloading Loadouts.yml.");
            c3.save();

            g.getGameMap().get(world).reloadConfigs();

            sender.sendMessage("Config files have been reloaded.");
        }catch(Exception e){
            sender.sendMessage("Something went wrong, please check the console.");
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
        }
        return true;
    }
}
