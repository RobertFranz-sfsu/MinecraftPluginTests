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
@SuppressWarnings({"FieldMayBeFinal", "NullableProblems", "CallToPrintStackTrace"})
public class Reload implements CommandExecutor {

    private GamesList g;
    private Minecraft_Test plugin;

    public Reload(Minecraft_Test plugin, GamesList g){
        this.plugin = plugin;
        this.g = g;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        try{
            plugin.reloadConfig();
            Bukkit.getLogger().info("Reloaded config.yml.");

            ConfigUtil c1 = new ConfigUtil(plugin, "Infected.yml");
            c1.save();
            Bukkit.getLogger().info("Saved Infected.yml.");

            ConfigUtil c2 = new ConfigUtil(plugin, "Survivor.yml");
            c2.save();
            Bukkit.getLogger().info("Saved Survivor.yml.");

            ConfigUtil c3 = new ConfigUtil(plugin, "Loadouts.yml");
            c3.save();
            Bukkit.getLogger().info("Saved Loadouts.yml.");

            plugin.setLoadoutCon();
            Bukkit.getLogger().info("Reloaded Loadouts.yml.");

            plugin.reloadSubCommands();
            plugin.getInfected().setSubCommands();
            Bukkit.getLogger().info("Reloaded sub-commands.");

            plugin.setLoadoutPrices();
            plugin.setInvHandling();
            plugin.setDoKeepScore();
            plugin.setScoreOptions();
            Bukkit.getLogger().info("Finished checking main config settings.");

            for(String x : g.getGameMap().keySet()){
                g.getGameMap().get(x).reloadConfigs();
            }
            Bukkit.getLogger().info("Finished reloading infected/survivor configs and applying new values for each map.");

            sender.sendMessage("All configs have been successfully reloaded.");
        }catch(Exception e){
            sender.sendMessage("Something went wrong, please check the console.");
            e.printStackTrace();
        }
        return true;
    }
}
