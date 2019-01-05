package fi.matiaspaavilainen.masuiteteleports.bungee.commands;

import fi.matiaspaavilainen.masuitecore.bungee.Utils;
import fi.matiaspaavilainen.masuitecore.bungee.chat.Formator;
import fi.matiaspaavilainen.masuitecore.core.configuration.BungeeConfiguration;
import fi.matiaspaavilainen.masuiteteleports.bungee.MaSuiteTeleports;
import fi.matiaspaavilainen.masuiteteleports.bungee.managers.PlayerFinder;
import fi.matiaspaavilainen.masuiteteleports.bungee.managers.Teleport;
import fi.matiaspaavilainen.masuiteteleports.bungee.managers.requests.Request;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class TeleportRequestCommand {

    private MaSuiteTeleports plugin;

    public TeleportRequestCommand(MaSuiteTeleports plugin) {
        this.plugin = plugin;
    }

    private Utils utils = new Utils();
    private BungeeConfiguration config = new BungeeConfiguration();
    private Formator formator = new Formator();

    public void tpa(ProxiedPlayer sender, String t) {
        ProxiedPlayer target = new PlayerFinder().get(t);
        if (utils.isOnline(target, sender)) {
            Request tp = new Request(plugin);
            tp.createRequest(sender, target);
        }
    }

    public void tpahere(ProxiedPlayer sender, String t) {
        ProxiedPlayer target = new PlayerFinder().get(t);
        if (utils.isOnline(target, sender)) {
            Request tp = new Request(plugin);
            tp.createHereRequest(sender, target);
        }
    }

    public void tpaccept(ProxiedPlayer sender) {
        if (utils.isOnline(sender)) {
            Request tp = new Request(plugin);
            tp.acceptRequest(sender);
        }
    }

    public void tpdeny(ProxiedPlayer sender) {
        if (utils.isOnline(sender)) {
            Request tp = new Request(plugin);
            tp.cancelRequest(sender, "player");
        }
    }

    public void tplock(ProxiedPlayer sender, boolean lock) {
        if (utils.isOnline(sender)) {
            Teleport.lock.put(sender.getUniqueId(), lock);
            if (lock) {
                formator.sendMessage(sender, config.load("teleports", "messages.yml").getString("tpalock.allow"));
            } else {
                formator.sendMessage(sender, config.load("teleports", "messages.yml").getString("tpalock.deny"));
            }

        }
    }
}
