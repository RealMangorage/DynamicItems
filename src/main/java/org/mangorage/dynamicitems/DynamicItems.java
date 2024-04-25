package org.mangorage.dynamicitems;

import fi.iki.elonen.NanoHTTPD;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.mangorage.dynamicitems.generator.Startup;
import org.mangorage.ftp.FileSharing;

import java.io.IOException;

public final class DynamicItems extends JavaPlugin implements Listener {

    private static final String PLUGIN_ID = "DynamicItems";
    // Use this anywhere you want a custom item.
    // Will tell the API to go ahead and put the png into the proper spot with proper files asssoicated
    public static final ImageItem CUSTOM_ON = ImageItem.CreateImageItem("assets/items/on.png", Material.GRAY_STAINED_GLASS_PANE, PLUGIN_ID, true);
    public static final ImageItem CUSTOM_OFF = ImageItem.CreateImageItem("assets/items/off.png", Material.GRAY_STAINED_GLASS_PANE, PLUGIN_ID, true);
    public static final FileSharing SHARING = new FileSharing(1080);;


    @Override
    public void onEnable() {
        try {
            SHARING.start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        // Plugin startup logic
        Bukkit.getScheduler().runTaskLater(this, Startup::cook, 20 * 5); // Cook after 5 seconds!
        getServer().getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        SHARING.stop();
    }

    @EventHandler
    public void onPlayerPreJoin(PlayerLoginEvent event) {
        if (!Startup.isCooked()) {
            event.kickMessage(Component.text("Server not fully loaded. Please try again in a few minutes"));
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (!Startup.isCooked()) {
            event.getPlayer().kick(Component.text("Server not fully loaded. Please try again in a few minutes"));
        } else {
            var plr = event.getPlayer();
            Startup.sendPack(this, plr);
            plr.getInventory().clear();
            plr.getInventory().addItem(CUSTOM_OFF.create());
            plr.setGameMode(GameMode.CREATIVE);
        }
    }

    private boolean on = false;

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        var inv = event.getClickedInventory();
        var slot = event.getSlot();
        on = !on;
        inv.setItem(slot, on ? CUSTOM_OFF.create() : CUSTOM_ON.create());

        event.setCancelled(true);

    }
}
