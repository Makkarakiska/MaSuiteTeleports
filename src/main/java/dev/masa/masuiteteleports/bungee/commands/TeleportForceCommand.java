package dev.masa.masuiteteleports.bungee.commands;

import dev.masa.masuitecore.core.channels.BungeePluginChannel;
import dev.masa.masuitecore.core.objects.Location;
import dev.masa.masuitecore.core.utils.PlayerFinder;
import dev.masa.masuiteteleports.bungee.MaSuiteTeleports;
import dev.masa.masuiteteleports.core.services.PlayerTeleportService;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class TeleportForceCommand {

    private MaSuiteTeleports plugin;
    private PlayerTeleportService teleportService;

    public TeleportForceCommand(MaSuiteTeleports plugin) {
        this.plugin = plugin;
        teleportService = plugin.getPlayerTeleportService();
    }

    // Sender to target
    public void tp(ProxiedPlayer sender, String t, boolean bypass) {
        ProxiedPlayer target = new PlayerFinder().get(t);
        if (plugin.utils.isOnline(target, sender)) {
            if (teleportService.toggles.contains(target.getUniqueId()) && !bypass) {
                plugin.formator.sendMessage(sender, plugin.config.load("teleports", "messages.yml").getString("tptoggle.disabled").replace("%player", target.getName()));
                return;
            }
            plugin.getPlayerPositionService().requestPosition(sender);
            teleportService.teleportPlayerToPlayer(sender, target);
            plugin.formator.sendMessage(sender, plugin.config.load("teleports", "messages.yml")
                    .getString("receiver.teleported")
                    .replace("%player%", target.getName())
            );
        }
    }

    // Target to other target
    public void tp(ProxiedPlayer sender, String t1, String t2) {
        ProxiedPlayer target1 = new PlayerFinder().get(t1);
        ProxiedPlayer target2 = new PlayerFinder().get(t2);
        if (plugin.utils.isOnline(target1, sender) && plugin.utils.isOnline(target2, sender)) {
            if (teleportService.toggles.contains(target1.getUniqueId())) {
                plugin.formator.sendMessage(sender, plugin.config.load("teleports", "messages.yml").getString("tptoggle.disabled").replace("%player", target1.getName()));
                return;
            }
            if (teleportService.toggles.contains(target2.getUniqueId())) {
                plugin.formator.sendMessage(sender, plugin.config.load("teleports", "messages.yml").getString("tptoggle.disabled").replace("%player", target2.getName()));
                return;
            }
            plugin.getPlayerPositionService().requestPosition(target1);
            teleportService.teleportPlayerToPlayer(target1, target2);
            plugin.formator.sendMessage(target1, plugin.config.load("teleports", "messages.yml")
                    .getString("receiver.teleported")
                    .replace("%player%", target2.getName())
            );
            plugin.formator.sendMessage(target2, plugin.config.load("teleports", "messages.yml")
                    .getString("sender.teleported")
                    .replace("%player%", target1.getName())
            );
        }
    }

    // TpCommand player to coordinates in the same world in the same world
    public void tp(ProxiedPlayer sender, String t, Double x, Double y, Double z) {
        ProxiedPlayer target = new PlayerFinder().get(t);
        if (plugin.utils.isOnline(target, sender)) {
            plugin.getPlayerPositionService().requestPosition(target);
            new BungeePluginChannel(plugin, sender.getServer().getInfo(), "MaSuiteTeleports", "PlayerToXYZ", sender.getName(), x, y, z).send();
        }

    }

    // TpCommand player to specific location in the same server
    public void tp(ProxiedPlayer sender, String t, Location loc) {
        ProxiedPlayer target = new PlayerFinder().get(t);
        if (plugin.utils.isOnline(target, sender)) {
            plugin.getPlayerPositionService().requestPosition(target);
            new BungeePluginChannel(plugin, sender.getServer().getInfo(), "MaSuiteTeleports", "PlayerToLocation", target.getName(), loc.serialize()).send();
        }
    }

    public void tphere(ProxiedPlayer sender, String t, boolean bypass) {
        ProxiedPlayer target = new PlayerFinder().get(t);
        if (plugin.utils.isOnline(target, sender)) {
            if (teleportService.toggles.contains(target.getUniqueId()) && !bypass) {
                plugin.formator.sendMessage(sender, plugin.config.load("teleports", "messages.yml").getString("tptoggle.disabled").replace("%player", target.getName()));
                return;
            }
            plugin.getPlayerPositionService().requestPosition(target);
            teleportService.teleportPlayerToPlayer(target, sender);
            plugin.formator.sendMessage(sender, plugin.config.load("teleports", "messages.yml")
                    .getString("sender.teleported")
                    .replace("%player%", target.getName())
            );
        }
    }

    public void tpall(ProxiedPlayer target) {
        if (plugin.utils.isOnline(target)) {
            for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
                plugin.getPlayerPositionService().requestPosition(player);
                teleportService.teleportPlayerToPlayer(player, target);
                plugin.formator.sendMessage(player, plugin.config.load("teleports", "messages.yml")
                        .getString("receiver.teleported")
                        .replace("%player%", target.getName())
                );
            }
        }
    }
}
