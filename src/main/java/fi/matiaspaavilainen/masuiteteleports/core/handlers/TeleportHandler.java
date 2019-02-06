package fi.matiaspaavilainen.masuiteteleports.core.handlers;

import fi.matiaspaavilainen.masuitecore.core.channels.BungeePluginChannel;
import fi.matiaspaavilainen.masuiteteleports.bungee.MaSuiteTeleports;
import fi.matiaspaavilainen.masuiteteleports.core.objects.TeleportRequest;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class TeleportHandler {

    public static List<TeleportRequest> requests = new ArrayList<>();
    public static HashMap<UUID, Boolean> lock = new HashMap<>();
    public static HashSet<UUID> toggles = new HashSet<>();

    private MaSuiteTeleports plugin;

    public TeleportHandler(MaSuiteTeleports plugin) {
        this.plugin = plugin;
    }

    public void teleportPlayerToPlayer(ProxiedPlayer sender, ProxiedPlayer receiver) {
        BungeePluginChannel bpc = new BungeePluginChannel(plugin, sender.getServer().getInfo(), new Object[]{
                "MaSuiteTeleports",
                "PlayerToPlayer",
                sender.getName(),
                receiver.getName()
        });

        if (!receiver.getServer().getInfo().getName().equals(sender.getServer().getInfo().getName())) {
            receiver.connect(ProxyServer.getInstance().getServerInfo(sender.getServer().getInfo().getName()));
            plugin.getProxy().getScheduler().schedule(plugin, bpc::send, 500, TimeUnit.MICROSECONDS);
        } else {
            bpc.send();
        }
    }

    public static TeleportRequest getTeleportRequest(ProxiedPlayer player) {
        for (TeleportRequest request : requests) {
            if (request.getReceiver().equals(player)) {
                return request;
            }
        }
        return null;
    }

}
