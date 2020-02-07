package dev.masa.masuiteteleports.bungee.listeners;

import dev.masa.masuiteteleports.bungee.MaSuiteTeleports;
import dev.masa.masuiteteleports.core.handlers.TeleportHandler;
import dev.masa.masuiteteleports.core.objects.TeleportRequest;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class PlayerQuitEvent implements Listener {

    private MaSuiteTeleports plugin;

    public PlayerQuitEvent(MaSuiteTeleports plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onQuit(PlayerDisconnectEvent event) {
        TeleportRequest request = plugin.teleportRequestService.getRequest(event.getPlayer().getUniqueId());
        if (request != null) {
            plugin.teleportRequestService.cancelRequest(request);
        }

        TeleportHandler.toggles.remove(event.getPlayer().getUniqueId());
        TeleportHandler.lock.remove(event.getPlayer().getUniqueId());
    }
}
