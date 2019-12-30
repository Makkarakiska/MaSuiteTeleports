package fi.matiaspaavilainen.masuiteteleports.bungee.listeners;

import fi.matiaspaavilainen.masuiteteleports.core.handlers.TeleportHandler;
import fi.matiaspaavilainen.masuiteteleports.core.services.TeleportRequestService;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class PlayerQuitEvent implements Listener {

    @EventHandler
    public void onQuit(PlayerDisconnectEvent e) {
        TeleportRequestService request = TeleportHandler.getTeleportRequest(e.getPlayer());
        if (request != null) {
            request.cancel();
        }

        TeleportHandler.toggles.remove(e.getPlayer().getUniqueId());
        TeleportHandler.lock.remove(e.getPlayer().getUniqueId());
    }
}
