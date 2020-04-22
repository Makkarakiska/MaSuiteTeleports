package dev.masa.masuiteteleports.core.services;

import dev.masa.masuitecore.core.channels.BungeePluginChannel;
import dev.masa.masuitecore.core.models.MaSuitePlayer;
import dev.masa.masuitecore.core.objects.Location;
import dev.masa.masuiteteleports.bungee.MaSuiteTeleports;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class PlayerPositionService {

    private MaSuiteTeleports plugin;

    public PlayerPositionService(MaSuiteTeleports plugin) {
        this.plugin = plugin;
    }

    /**
     * Request {@link ProxiedPlayer} location from server side
     *
     * @param player player to use
     */
    public void requestPosition(ProxiedPlayer player) {
        new BungeePluginChannel(plugin,
                player.getServer().getInfo(),
                "MaSuiteTeleports",
                "GetLocation",
                player.getName(),
                player.getServer().getInfo().getName()).send();
    }


    /**
     * Called when {@link Location} of the {@link ProxiedPlayer} has received
     *
     * @param player player whose location was received
     * @param loc    location of the {@link ProxiedPlayer}
     */
    public void locationReceived(ProxiedPlayer player, Location loc) {
        MaSuitePlayer msp = plugin.getApi().getPlayerService().getPlayer(player.getUniqueId());
        plugin.getApi().getPlayerService().updatePlayerLocation(msp, loc);
    }
}
