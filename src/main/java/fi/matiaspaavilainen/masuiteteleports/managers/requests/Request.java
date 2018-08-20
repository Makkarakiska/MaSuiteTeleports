package fi.matiaspaavilainen.masuiteteleports.managers.requests;

import fi.matiaspaavilainen.masuitecore.MaSuiteCore;
import fi.matiaspaavilainen.masuitecore.chat.Formator;
import fi.matiaspaavilainen.masuitecore.config.Configuration;
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

    public Request(MaSuiteTeleports p){
        plugin = p;
    }
    public void createRequest(ProxiedPlayer sender, ProxiedPlayer receiver){

        if(!Teleport.receivers.containsKey(receiver.getUniqueId())){
            Teleport.senders.put(sender.getUniqueId(), receiver.getUniqueId());
            Teleport.receivers.put(receiver.getUniqueId(), sender.getUniqueId());
            sender.sendMessage(new TextComponent(formator.colorize(config.load("teleports","messages.yml").getString("sender.teleport-request-incoming"))));
            receiver.sendMessage(new TextComponent(formator.colorize(config.load("teleports","messages.yml").getString("receiver.teleport-request-incoming"))));

            ProxyServer.getInstance().getScheduler().schedule(plugin, new Runnable() {
                public void run() {
                    cancelRequest(receiver, "timer");
                }
            }, config.load("teleports","settings.yml").getInt("keep-request-alive"), TimeUnit.SECONDS);
        }else{
            sender.sendMessage(new TextComponent(formator.colorize(config.load("teleports","messages.yml").getString("sender.teleport-request-pending"))));
        }

    }

    public void cancelRequest(ProxiedPlayer receiver, String type) {
        if(Teleport.receivers.containsKey(receiver.getUniqueId())) {
            ProxiedPlayer sender = ProxyServer.getInstance().getPlayer(Teleport.receivers.get(receiver.getUniqueId()));
            if(sender != null){
                if (Teleport.senders.containsKey(sender.getUniqueId())) {
                    Teleport.senders.remove(sender.getUniqueId(), receiver.getUniqueId());
                    Teleport.receivers.remove(receiver.getUniqueId(), sender.getUniqueId());
                    if(type.equals("timer")){
                        sender.sendMessage(new TextComponent(formator.colorize(config.load("teleports", "messages.yml").getString("sender.teleport-request-expired"))));
                        receiver.sendMessage(new TextComponent(formator.colorize(config.load("teleports", "messages.yml").getString("receiver.teleport-request-expired"))));
                    }else if(type.equals("player")){
                        sender.sendMessage(new TextComponent(formator.colorize(config.load("teleports", "messages.yml").getString("sender.teleport-request-denied"))));
                        receiver.sendMessage(new TextComponent(formator.colorize(config.load("teleports", "messages.yml").getString("receiver.teleport-request-denied"))));
                    }

                }
            }
        }
    }

    public void acceptRequest(ProxiedPlayer receiver){
        if(Teleport.receivers.containsKey(receiver.getUniqueId())){
            ProxiedPlayer sender = ProxyServer.getInstance().getPlayer(Teleport.receivers.get(receiver.getUniqueId()));
            if(sender != null){
                sender.sendMessage(new TextComponent(formator.colorize(config.load("teleports","messages.yml").getString("sender.teleport-request-accepted"))));
                receiver.sendMessage(new TextComponent(formator.colorize(config.load("teleports","messages.yml").getString("receiver.teleport-request-accepted"))));
                PlayerToPlayer(sender, receiver);
                Teleport.senders.remove(sender.getUniqueId());
                Teleport.receivers.remove(receiver.getUniqueId());
            }else{
                receiver.sendMessage(new TextComponent(formator.colorize(config.load("messages.yml").getString("player-not-online"))));
            }
        }else{
            receiver.sendMessage(new TextComponent(formator.colorize(config.load("teleports","messages.yml").getString("receiver.no-pending-teleport-requests"))));
        }

    }
}
