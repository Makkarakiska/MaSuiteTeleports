package fi.matiaspaavilainen.masuiteteleports.managers.requests;

import fi.matiaspaavilainen.masuitecore.chat.Formator;
import fi.matiaspaavilainen.masuitecore.config.Configuration;
import fi.matiaspaavilainen.masuiteteleports.Button;
import fi.matiaspaavilainen.masuiteteleports.MaSuiteTeleports;
import fi.matiaspaavilainen.masuiteteleports.managers.Teleport;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Listener;

import java.util.concurrent.TimeUnit;

import static fi.matiaspaavilainen.masuiteteleports.managers.Teleport.PlayerToPlayer;

public class Request implements Listener {

    private Formator formator = new Formator();
    private Configuration config = new Configuration();
    private MaSuiteTeleports plugin;

    public Request(MaSuiteTeleports p) {
        plugin = p;
    }

    public void createRequest(ProxiedPlayer sender, ProxiedPlayer receiver) {

        if (!Teleport.receivers.containsKey(receiver.getUniqueId())) {
            Teleport.senders.put(sender.getUniqueId(), receiver.getUniqueId());
            Teleport.receivers.put(receiver.getUniqueId(), sender.getUniqueId());
            Teleport.method.put(sender.getUniqueId(), "to");
            sender.sendMessage(new TextComponent(formator.colorize(config.load("teleports", "messages.yml")
                    .getString("sender.teleport-to-request-incoming")
                    .replace("%sender%", sender.getName())
                    .replace("%receiver%", receiver.getName()))));
            receiver.sendMessage(new TextComponent(formator.colorize(config.load("teleports", "messages.yml")
                    .getString("receiver.teleport-to-request-incoming")
                    .replace("%sender%", sender.getName())
                    .replace("%receiver%", receiver.getName()))));
            TextComponent buttons = new TextComponent();
            buttons.addExtra(new Button().create("accept", "/tpaccept"));
            buttons.addExtra(new Button().create("deny", "/tpdeny"));
            receiver.sendMessage(buttons);
            ProxyServer.getInstance().getScheduler().schedule(plugin, new Runnable() {
                public void run() {
                    cancelRequest(receiver, "timer");
                }
            }, config.load("teleports", "settings.yml").getInt("keep-request-alive"), TimeUnit.SECONDS);
        } else {
            sender.sendMessage(new TextComponent(formator.colorize(config.load("teleports", "messages.yml")
                    .getString("sender.teleport-request-pending")
                    .replace("%sender%", sender.getName())
                    .replace("%receiver%", receiver.getName())
            )));
        }

    }

    public void createHereRequest(ProxiedPlayer sender, ProxiedPlayer receiver) {

        if (!Teleport.receivers.containsKey(receiver.getUniqueId())) {
            Teleport.senders.put(sender.getUniqueId(), receiver.getUniqueId());
            Teleport.receivers.put(receiver.getUniqueId(), sender.getUniqueId());
            Teleport.method.put(sender.getUniqueId(), "here");
            formator.sendMessage(sender, config.load("teleports", "messages.yml")
                    .getString("sender.teleport-here-request-incoming")
                    .replace("%sender%", sender.getName())
                    .replace("%receiver%", receiver.getName())
            );
            formator.sendMessage(receiver, config.load("teleports", "messages.yml")
                    .getString("receiver.teleport-here-request-incoming")
                    .replace("%sender%", sender.getName())
                    .replace("%receiver%", receiver.getName())
            );

            ProxyServer.getInstance().getScheduler().schedule(plugin, new Runnable() {
                public void run() {
                    cancelRequest(receiver, "timer");
                }
            }, config.load("teleports", "settings.yml").getInt("keep-request-alive"), TimeUnit.SECONDS);
        } else {
            formator.sendMessage(sender, config.load("teleports", "messages.yml")
                    .getString("sender.teleport-request-pending")
                    .replace("%sender%", sender.getName())
                    .replace("%receiver%", receiver.getName())
            );
        }

    }


    public void cancelRequest(ProxiedPlayer receiver, String type) {
        if (Teleport.receivers.containsKey(receiver.getUniqueId())) {
            ProxiedPlayer sender = ProxyServer.getInstance().getPlayer(Teleport.receivers.get(receiver.getUniqueId()));
            if (sender != null) {
                if (Teleport.senders.containsKey(sender.getUniqueId())) {
                    if (type.equals("timer")) {
                        formator.sendMessage(sender, config.load("teleports", "messages.yml")
                                .getString("sender.teleport-request-expired")
                                .replace("%receiver%", receiver.getName())
                        );
                        formator.sendMessage(receiver, config.load("teleports", "messages.yml")
                                .getString("receiver.teleport-request-expired")
                                .replace("%sender%", sender.getName())

                        );
                    } else if (type.equals("player")) {
                        formator.sendMessage(receiver, config.load("teleports", "messages.yml")
                                .getString("sender.teleport-request-denied")
                                .replace("%sender%", sender.getName())
                                .replace("%receiver%", receiver.getName())
                        );
                        formator.sendMessage(receiver, config.load("teleports", "messages.yml")
                                .getString("receiver.teleport-request-denied")
                                .replace("%sender%", sender.getName())
                                .replace("%receiver%", receiver.getName())
                        );
                    }
                    Teleport.senders.remove(sender.getUniqueId(), receiver.getUniqueId());
                    Teleport.receivers.remove(receiver.getUniqueId(), sender.getUniqueId());
                    Teleport.method.remove(sender.getUniqueId());
                }
            }
        }
    }

    public void acceptRequest(ProxiedPlayer receiver) {
        if (Teleport.receivers.containsKey(receiver.getUniqueId())) {
            ProxiedPlayer sender = ProxyServer.getInstance().getPlayer(Teleport.receivers.get(receiver.getUniqueId()));
            if (sender != null) {
                formator.sendMessage(sender, config.load("teleports", "messages.yml")
                        .getString("sender.teleport-request-accepted")
                        .replace("%sender%", sender.getName())
                        .replace("%receiver%", receiver.getName())
                );
                formator.sendMessage(receiver, config.load("teleports", "messages.yml")
                        .getString("receiver.teleport-request-accepted")
                        .replace("%sender%", sender.getName())
                        .replace("%receiver%", receiver.getName())
                );
                PlayerToPlayer(sender, receiver);
                Teleport.senders.remove(sender.getUniqueId());
                Teleport.receivers.remove(receiver.getUniqueId());
                Teleport.method.remove(sender.getUniqueId());
            } else {
                formator.sendMessage(receiver,config.load("messages.yml").getString("player-not-online"));
            }
        } else {
            formator.sendMessage(receiver,config.load("teleports", "messages.yml").getString("receiver.no-pending-teleport-requests"));
        }

    }
}
