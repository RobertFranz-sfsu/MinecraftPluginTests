package mctest.minecraft_test.commands.InfectedSubCommands;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import mctest.minecraft_test.Minecraft_Test;
import org.apache.commons.io.IOUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Skull;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Base64;
import java.util.Objects;
import java.util.UUID;

public class GetCustomHead {
    Minecraft_Test plugin;

    public GetCustomHead(Minecraft_Test plugin){ this.plugin = plugin; }

    public ItemStack getCustomHead(UUID url1) {
        ItemStack skull;

        if(plugin.getIs18()){
            return new ItemStack(Objects.requireNonNull(Material.getMaterial("GOLD_INGOT")));
        }else{
            skull = new ItemStack(Material.PLAYER_HEAD);
        }
        SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();

        assert skullMeta != null;

        StringBuilder s_url = new StringBuilder();
        s_url.append("https://sessionserver.mojang.com/session/minecraft/profile/").append(url1.toString()); // Texture link.

        URL u;
        try {
            u = new URL(s_url.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        JsonObject json;
        try {
            json = getJson(u);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        String[] je = json.get("properties").toString().split(":");
        String id = "";
        int count = 0;
        for(String x : je){
            if((x.length() > 20 ) && (count == 0)){
                x = x.replaceAll("\\p{P}", "");
                id = x;
                count++;
            }
        }

//            GameProfile gameProfile = new GameProfile(UUID.randomUUID(), "null"); // Create a GameProfile
        GameProfile gameProfile = new GameProfile(url1, Objects.requireNonNull(Bukkit.getOfflinePlayer(url1)).getName());
        byte[] data = Base64.getEncoder().encode(String.format("{textures:{SKIN:{url:\"%s\"}}}", s_url.toString()).getBytes());

            // Set the texture property in the GameProfile.
            gameProfile.getProperties().put("textures", new Property("textures", new String(data)));
            Field field = null;

            try {
                field = skullMeta.getClass().getDeclaredField("profile"); // Get the field profile.
                field.setAccessible(true); // Set as accessible to modify.
                field.set(skullMeta, gameProfile); // Set in the skullMeta the modified GameProfile that we created.
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (plugin.getIs18()) {
                skullMeta.setOwner(url1.toString());
            } else {
                skullMeta.setOwningPlayer(Bukkit.getOfflinePlayer(url1));
            }

        skull.setItemMeta(skullMeta);
        return skull;
    }

    public static JsonObject getJson(URL url) throws IOException {
        String json = IOUtils.toString(url, Charset.forName("UTF-8"));
        return new Gson().fromJson(json, JsonObject.class);
    }
}
