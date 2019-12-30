package fi.matiaspaavilainen.masuiteteleports.bungee.commands;

import fi.matiaspaavilainen.masuitecore.bungee.Utils;
import fi.matiaspaavilainen.masuitecore.bungee.chat.Formator;
import fi.matiaspaavilainen.masuitecore.core.channels.BungeePluginChannel;
import fi.matiaspaavilainen.masuitecore.core.configuration.BungeeConfiguration;
import fi.matiaspaavilainen.masuitecore.core.objects.Location;
import fi.matiaspaavilainen.masuitecore.core.utils.PlayerFinder;
import fi.matiaspaavilainen.masuiteteleports.bungee.MaSuiteTeleports;
import fi.matiaspaavilainen.masuiteteleports.core.handlers.TeleportHandler;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class TeleportForceCommand {

    private MaSuiteTeleports plugin;
    private TeleportHandler teleportHandler;

    public TeleportForceCommand(MaSuiteTeleports plugin) {
        this.plugin = plugin;
        teleportHandler = new TeleportHandler(plugin);
    }

    private Utils utils = new Utils();
    private BungeeConfiguration config = new BungeeConfiguration();
    private Formator formator = new Formator();

    // Sender to target
    public void tp(ProxiedPlayer sender, String t, boolean bypass) {
        ProxiedPlayer target = new PlayerFinder().get(t);
        if (utils.isOnline(target, sender)) {
            if (TeleportHandler.toggles.contains(target.getUniqueId()) && !bypass) {
                formator.sendMessage(sender, config.load("teleports", "messages.yml").getString("tptoggle.disabled").replace("%player", target.getName()));
                return;
            }
            plugin.playerPositionService.requestPosition(sender);
            teleportHandler.teleport(sender, target);
            formator.sendMessage(sender, config.load("teleports", "messages.yml")
                    .getString("receiver.teleported")
                    .replace("%player%", target.getName())
            );
        }
    }

    // Target to other target
    public void tp(ProxiedPlayer sender, String t1, String t2) {
        ProxiedPlayer target1 = new PlayerFinder().get(t1);
        ProxiedPlayer target2 = new PlayerFinder().get(t2);
        if (utils.isOnline(target1, sender) && utils.isOnline(target2, sender)) {
            plugin.playerPositionService.requestPosition(target1);
            teleportHandler.teleport(target1, target2);
            formator.sendMessage(target1, config.load("teleports", "messages.yml")
                    .getString("receiver.teleported")
                    .replace("%player%", target2.getName())
            );
            formator.sendMessage(target2, config.load("teleports", "messages.yml")
                    .getString("sender.teleported")
                    .replace("%player%", target1.getName())
            );
        }
    }

    // TpCommand player to coordinates in the same world in the same world
    public void tp(ProxiedPlayer sender, String t, Double x, Double y, Double z) {
        ProxiedPlayer target = new PlayerFinder().get(t);
        if (utils.isOnline(target, sender)) {
            plugin.playerPositionService.requestPosition(target);
            new BungeePluginChannel(plugin, sender.getServer().getInfo(), new Object[]{"MaSuiteTeleports", "PlayerToXYZ", sender.getName(), x, y, z}).send();
        }

    }

    // TpCommand player to specific location in the same server
    public void tp(ProxiedPlayer sender, String t, Location loc) {
        ProxiedPlayer target = new PlayerFinder().get(t);
        if (utils.isOnline(target, sender)) {
            plugin.playerPositionService.requestPosition(target);
            new BungeePluginChannel(plugin, sender.getServer().getInfo(), new Object[]{"MaSuiteTeleports", "PlayerToLocation", target.getName(),
                    loc.getWorld(), loc.getX(), loc.getY(), loc.getZ()}).send();
        }
    }

    public void tphere(ProxiedPlayer sender, String t, boolean bypass) {
        ProxiedPlayer target = new PlayerFinder().get(t);
        if (utils.isOnline(target, sender)) {
            if (TeleportHandler.toggles.contains(target.getUniqueId()) && !bypass) {
                formator.sendMessage(sender, config.load("teleports", "messages.yml").getString("tptoggle.disabled").replace("%player", target.getName()));
                return;
            }
            plugin.playerPositionService.requestPosition(target);
            teleportHandler.teleport(target, sender);
            formator.sendMessage(sender, config.load("teleports", "messages.yml")
                    .getString("sender.teleported")
                    .replace("%player%", target.getName())
            );
        }
    }

    public void tpall(ProxiedPlayer target) {
        if (utils.isOnline(target)) {
            for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
                plugin.playerPositionService.requestPosition(player);
                teleportHandler.teleport(player, target);
                formator.sendMessage(player, config.load("teleports", "messages.yml")
                        .getString("receiver.teleported")
                        .replace("%player%", target.getName())
                );
            }
        }
    }
}
