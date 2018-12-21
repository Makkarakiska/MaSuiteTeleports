package fi.matiaspaavilainen.masuiteteleports.managers.requests;

import fi.matiaspaavilainen.masuitecore.Utils;
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

public class Request implements Listener {

    private Formator formator = new Formator();
    private Configuration config = new Configuration();
    private MaSuiteTeleports plugin;
    private Utils utils = new Utils();

    public Request(MaSuiteTeleports p) {
        plugin = p;
    }

    public void createRequest(ProxiedPlayer sender, ProxiedPlayer receiver) {
        if (checkIfPending(sender, receiver)) return;
        if (!Teleport.receivers.containsKey(receiver.getUniqueId())) {
            Teleport.senders.put(sender.getUniqueId(), receiver.getUniqueId());
            Teleport.receivers.put(receiver.getUniqueId(), sender.getUniqueId());
            Teleport.method.put(sender.getUniqueId(), "to");
            Teleport.method.put(receiver.getUniqueId(), "to");
            if (Teleport.lock.containsKey(receiver.getUniqueId())) {
                if(Teleport.lock.get(receiver.getUniqueId())){
                    acceptRequest(receiver);
                } else {
                    cancelRequest(receiver, "player");
                }

                return;
            }
            formator.sendMessage(sender, formator.colorize(config.load("teleports", "messages.yml")
                    .getString("sender.teleport-to-request-incoming")
                    .replace("%sender%", sender.getName())
                    .replace("%receiver%", receiver.getName())));
            formator.sendMessage(receiver, formator.colorize(config.load("teleports", "messages.yml")
                    .getString("receiver.teleport-to-request-incoming")
                    .replace("%sender%", sender.getName())
                    .replace("%receiver%", receiver.getName())));
            buttons(receiver);
            ProxyServer.getInstance().getScheduler().schedule(plugin, () -> cancelRequest(receiver, "timer"), config.load("teleports", "settings.yml").getInt("keep-request-alive"), TimeUnit.SECONDS);
        } else {
            formator.sendMessage(sender, formator.colorize(config.load("teleports", "messages.yml")
                    .getString("sender.teleport-request-pending.receiver")
                    .replace("%sender%", sender.getName())
                    .replace("%receiver%", receiver.getName())
            ));
        }

    }

    public void createHereRequest(ProxiedPlayer sender, ProxiedPlayer receiver) {
        if (checkIfPending(sender, receiver)) return;
        if (!Teleport.receivers.containsKey(receiver.getUniqueId())) {
            Teleport.senders.put(sender.getUniqueId(), receiver.getUniqueId());
            Teleport.receivers.put(receiver.getUniqueId(), sender.getUniqueId());
            Teleport.method.put(sender.getUniqueId(), "here");
            Teleport.method.put(receiver.getUniqueId(), "here");
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
            buttons(receiver);
            ProxyServer.getInstance().getScheduler().schedule(plugin,
                    () -> cancelRequest(receiver, "timer"),
                    config.load("teleports", "settings.yml").getInt("keep-request-alive"),
                    TimeUnit.SECONDS);
        } else {
            formator.sendMessage(sender, config.load("teleports", "messages.yml")
                    .getString("sender.teleport-request-pending.receiver")
                    .replace("%sender%", sender.getName())
                    .replace("%receiver%", receiver.getName())
            );
        }

    }

    private boolean checkIfPending(ProxiedPlayer sender, ProxiedPlayer receiver) {
        if(Teleport.receivers.containsKey(sender.getUniqueId())){
            formator.sendMessage(sender, formator.colorize(config.load("teleports", "messages.yml")
                    .getString("sender.teleport-request-pending.sender")
                    .replace("%sender%", sender.getName())
                    .replace("%receiver%", receiver.getName())
            ));
            return true;
        }
        return false;
    }

    private void buttons(ProxiedPlayer receiver) {
        if (config.load("teleports", "buttons.yml").getBoolean("enabled")) {
            TextComponent buttons = new TextComponent();
            buttons.addExtra(new Button().create("accept", "/tpaccept"));
            buttons.addExtra(new Button().create("deny", "/tpdeny"));
            receiver.sendMessage(buttons);
        }
    }


    public void cancelRequest(ProxiedPlayer receiver, String type) {
        if (Teleport.receivers.containsKey(receiver.getUniqueId())) {
            ProxiedPlayer sender = ProxyServer.getInstance().getPlayer(Teleport.receivers.get(receiver.getUniqueId()));
            if (utils.isOnline(sender)) {
                if (Teleport.senders.containsKey(sender.getUniqueId())) {
                    if (type.equals("timer")) {
                        formator.sendMessage(sender, config.load("teleports", "messages.yml")
                                .getString("sender.teleport-request-expired")
                                .replace("%sender%", sender.getName())
                                .replace("%receiver%", receiver.getName())
                        );
                        formator.sendMessage(receiver, config.load("teleports", "messages.yml")
                                .getString("receiver.teleport-request-expired")
                                .replace("%sender%", sender.getName())
                                .replace("%receiver%", receiver.getName())

                        );
                    } else if (type.equals("player")) {
                        formator.sendMessage(sender, config.load("teleports", "messages.yml")
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
                    Teleport.method.remove(receiver.getUniqueId());
                }
            }
        } else {
            if (type.equals("player")) {
                formator.sendMessage(receiver, config.load("teleports", "messages.yml").getString("receiver.no-pending-teleport-requests"));
            }
        }
    }

    public void acceptRequest(ProxiedPlayer receiver) {
        if (Teleport.receivers.containsKey(receiver.getUniqueId())) {
            ProxiedPlayer sender = ProxyServer.getInstance().getPlayer(Teleport.receivers.get(receiver.getUniqueId()));
            if (utils.isOnline(sender, receiver)) {
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
                plugin.positions.requestPosition(sender);
                new Teleport(plugin).playerToPlayer(sender, receiver);
                Teleport.senders.remove(sender.getUniqueId());
                Teleport.receivers.remove(receiver.getUniqueId());
                Teleport.method.remove(sender.getUniqueId());
                Teleport.method.remove(receiver.getUniqueId());
            }
        } else {
            formator.sendMessage(receiver, config.load("teleports", "messages.yml").getString("receiver.no-pending-teleport-requests"));
        }

    }
}
