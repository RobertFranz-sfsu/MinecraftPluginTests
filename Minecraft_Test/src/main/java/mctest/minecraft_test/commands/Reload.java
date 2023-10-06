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

    SurvivalPlayer s;
    private GamesList g;
    private Minecraft_Test plugin = Minecraft_Test.getPlugin(Minecraft_Test.class);

    public Reload(SurvivalPlayer s, GamesList g){
        this.g = g;
        this.s = s;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        try{
            Bukkit.getLogger().info("Reloading config.yml...");
            plugin.reloadConfig();
            Bukkit.getLogger().info("Reloaded config.yml.");

            Bukkit.getLogger().info("Saving Infected.yml...");
            ConfigUtil c1 = new ConfigUtil(plugin, "Infected.yml");
            c1.save();
            Bukkit.getLogger().info("Saved Infected.yml.");

            Bukkit.getLogger().info("Saving Survivor.yml...");
            ConfigUtil c2 = new ConfigUtil(plugin, "Survivor.yml");
            c2.save();
            Bukkit.getLogger().info("Saved Survivor.yml.");

            Bukkit.getLogger().info("Saving Loadouts.yml...");
            ConfigUtil c3 = new ConfigUtil(plugin, "Loadouts.yml");
            c3.save();
            Bukkit.getLogger().info("Saved Loadouts.yml.");

            Bukkit.getLogger().info("Reloading Loadouts.yml...");
            plugin.setLoadoutCon();
            Bukkit.getLogger().info("Reloaded Loadouts.yml.");

            Bukkit.getLogger().info("Checking loadout prices setting...");
            plugin.setLoadoutPrices();
            Bukkit.getLogger().info("Finished checking loadout prices setting.");

            Bukkit.getLogger().info("Reloading infected/survivor configs and applying new values for each map.");
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
