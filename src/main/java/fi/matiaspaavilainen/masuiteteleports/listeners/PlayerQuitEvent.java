package fi.matiaspaavilainen.masuiteteleports.listeners;

import fi.matiaspaavilainen.masuiteteleports.managers.Teleport;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class PlayerQuitEvent implements Listener {

    @EventHandler
    public void onQuit(PlayerDisconnectEvent e){
        Teleport.receivers.remove(Teleport.receivers.get(e.getPlayer().getUniqueId()));
        Teleport.senders.remove(Teleport.senders.get(e.getPlayer().getUniqueId()));
        Teleport.method.remove(e.getPlayer().getUniqueId());
        Teleport.receivers.remove(e.getPlayer().getUniqueId());
        Teleport.senders.remove(e.getPlayer().getUniqueId());
    }
}
