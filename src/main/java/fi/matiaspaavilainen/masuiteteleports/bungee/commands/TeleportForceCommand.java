package fi.matiaspaavilainen.masuiteteleports.bungee.commands;

import fi.matiaspaavilainen.masuitecore.bungee.Utils;
import fi.matiaspaavilainen.masuitecore.bungee.chat.Formator;
import fi.matiaspaavilainen.masuitecore.core.channels.BungeePluginChannel;
import fi.matiaspaavilainen.masuitecore.core.configuration.BungeeConfiguration;
import fi.matiaspaavilainen.masuitecore.core.objects.Location;
import fi.matiaspaavilainen.masuiteteleports.bungee.MaSuiteTeleports;
import fi.matiaspaavilainen.masuiteteleports.bungee.managers.PlayerFinder;
import fi.matiaspaavilainen.masuiteteleports.bungee.managers.Teleport;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class TeleportForceCommand {

    private MaSuiteTeleports plugin;

    public TeleportForceCommand(MaSuiteTeleports plugin) {
        this.plugin = plugin;
    }

    private Utils utils = new Utils();
    private BungeeConfiguration config = new BungeeConfiguration();
    private Formator formator = new Formator();

    // Sender to target
    public void tp(ProxiedPlayer sender, String t) {
        ProxiedPlayer target = new PlayerFinder().get(t);
        if (utils.isOnline(target, sender)) {
            plugin.positions.requestPosition(sender);
            new Teleport(plugin).playerToPlayer(sender, target);
            formator.sendMessage(sender, config.load("teleports", "messages.yml")
                    .getString("teleported")
                    .replace("%player%", target.getName())
            );
        }
    }

    // Target to other target
    public void tp(ProxiedPlayer sender, String t1, String t2) {
        ProxiedPlayer target1 = new PlayerFinder().get(t1);
        ProxiedPlayer target2 = new PlayerFinder().get(t2);
        if (utils.isOnline(target1, sender) && utils.isOnline(target2, sender)) {
            plugin.positions.requestPosition(target1);
            new Teleport(plugin).playerToPlayer(target1, target2);
            formator.sendMessage(target1, config.load("teleports", "messages.yml")
                    .getString("teleported")
                    .replace("%player%", target2.getName())
            );
        }
    }

    // Teleport player to coordinates in the same world in the same world
    public void tp(ProxiedPlayer sender, String t, Double x, Double y, Double z) {
        ProxiedPlayer target = new PlayerFinder().get(t);
        if (utils.isOnline(target, sender)) {
            plugin.positions.requestPosition(target);
            new BungeePluginChannel(plugin, sender.getServer().getInfo(), new Object[]{"MaSuiteTeleports", "PlayerToXYZ", x, y, z}).send();
        }

    }

    // Teleport player to specific location in the same server
    public void tp(ProxiedPlayer sender, String t, Location loc) {
        ProxiedPlayer target = new PlayerFinder().get(t);
        if (utils.isOnline(target, sender)) {
            plugin.positions.requestPosition(target);
            new BungeePluginChannel(plugin, sender.getServer().getInfo(), new Object[]{"MaSuiteTeleports", "PlayerToLocation",
                    loc.getWorld(), loc.getX(), loc.getY(), loc.getZ()}).send();
        }
    }

    public void tphere(ProxiedPlayer sender, String t) {
        ProxiedPlayer target = new PlayerFinder().get(t);
        if (utils.isOnline(target, sender)) {
            plugin.positions.requestPosition(target);
            new Teleport(plugin).playerToPlayer(target, sender);
        }
    }

    public void tpall(ProxiedPlayer target) {
        if (utils.isOnline(target)) {
            for (ProxiedPlayer p : ProxyServer.getInstance().getPlayers()) {
                plugin.positions.requestPosition(p);
                new Teleport(plugin).playerToPlayer(p, target);
            }
        }
    }
}
