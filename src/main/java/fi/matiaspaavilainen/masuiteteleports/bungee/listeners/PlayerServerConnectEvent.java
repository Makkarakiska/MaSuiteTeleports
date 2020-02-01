package fi.matiaspaavilainen.masuiteteleports.bungee.listeners;

import fi.matiaspaavilainen.masuiteteleports.bungee.MaSuiteTeleports;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class PlayerServerConnectEvent implements Listener {

    private MaSuiteTeleports plugin;

    public PlayerServerConnectEvent(MaSuiteTeleports plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerConnect(ServerConnectEvent event) {
        plugin.playerPositionService.requestPosition(event.getPlayer());
    }
}
