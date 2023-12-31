package mctest.minecraft_test.handlers;

import mctest.minecraft_test.Minecraft_Test;
import mctest.minecraft_test.util.ConfigUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

@SuppressWarnings("FieldMayBeFinal")
public class PlayerHandler implements Listener {
    Minecraft_Test plugin ;
    public PlayerHandler(Minecraft_Test plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if(plugin.doKeepScore()){
            String path = System.getProperty("file.separator") + "Scores" + System.getProperty("file.separator") + player.getUniqueId() + ".yml";
            File dir = new File(plugin.getDataFolder().getPath() + System.getProperty("file.separator") +"Scores" + System.getProperty("file.separator"));
            File[] dirList = dir.listFiles();
            assert dirList != null;
            ArrayList<String> fileNames = new ArrayList<>();
            for (File f : dirList) {
                fileNames.add(f.getName());
            }
            if(!fileNames.contains(player.getUniqueId() + ".yml")){
                try {
                    Bukkit.getLogger().info("Creating new profile");
                    File p = new File(plugin.getDataFolder().getPath() + System.getProperty("file.separator") + "Scores" + System.getProperty("file.separator") + player.getUniqueId() + ".yml");
                    p.createNewFile();


                    ConfigUtil con = new ConfigUtil(plugin, path);
                    con.getConfig().set("username", player.getName());
                    con.getConfig().set("survivor-wins", 0);
                    con.getConfig().set("infected-wins", 0);
                    con.getConfig().set("survivor-kills", 0);
                    con.getConfig().set("infected-kills", 0);
                    con.getConfig().set("games-played", 0);
                    con.save();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }else{
                Bukkit.getLogger().info("Already played");
                ConfigUtil con = new ConfigUtil(plugin, path);
                if(!Objects.equals(con.getConfig().getString("username"), player.getName())){
                    con.getConfig().set("username", player.getName());
                }
            }
        }
    }

    @EventHandler
    public void onPlayerDisconnect(PlayerQuitEvent event){

    }
}
