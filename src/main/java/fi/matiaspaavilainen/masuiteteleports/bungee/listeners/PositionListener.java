package fi.matiaspaavilainen.masuiteteleports.bungee.listeners;

import fi.matiaspaavilainen.masuitecore.core.channels.BungeePluginChannel;
import fi.matiaspaavilainen.masuitecore.core.objects.Location;
import fi.matiaspaavilainen.masuiteteleports.bungee.MaSuiteTeleports;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.HashMap;
import java.util.UUID;

/**
 * Original author Sabbertan
 * Updated version for MaSuite: Masa
 */
public class PositionListener {

    private MaSuiteTeleports plugin;
    private HashMap<UUID, Runnable> positionRunnables = new HashMap<>();
    public HashMap<UUID, Location> positions;

    public PositionListener(MaSuiteTeleports plugin) {
        this.plugin = plugin;
        positions = new HashMap<>();
    }

    public void requestPosition(ProxiedPlayer p) {
        new BungeePluginChannel(plugin, p.getServer().getInfo(), new Object[]{
                "MaSuiteTeleports",
                "GetLocation",
                p.getName(),
                p.getServer().getInfo().getName()
        }).send();
    }
    public void locationReceived(ProxiedPlayer p, Location loc) {
        if (positionRunnables.containsKey(p.getUniqueId())) {
            positionRunnables.remove(p.getUniqueId()).run();
        } else {
            positions.put(p.getUniqueId(), loc);
        }
    }
}
