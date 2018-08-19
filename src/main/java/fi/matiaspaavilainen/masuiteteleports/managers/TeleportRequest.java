package fi.matiaspaavilainen.masuiteteleports.managers;

import fi.matiaspaavilainen.masuitecore.chat.Formator;
import fi.matiaspaavilainen.masuitecore.config.Configuration;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.HashMap;
import java.util.UUID;

public class TeleportRequest {

    HashMap<UUID, UUID> requests = new HashMap<>();
    private Formator formator = new Formator();
    private Configuration config = new Configuration();

    public void createRequest(ProxiedPlayer sender, ProxiedPlayer receiver){
        if(!requests.containsKey(receiver.getUniqueId())){
            requests.put(sender.getUniqueId(), receiver.getUniqueId());
            sender.sendMessage(new TextComponent(formator.colorize(config.load("teleports/messages.yml").getString("sender.teleport-request-incoming"))));
            receiver.sendMessage(new TextComponent(formator.colorize(config.load("teleports/messages.yml").getString("receiver.teleport-request-incoming"))));
        }else{
            sender.sendMessage(new TextComponent(formator.colorize(config.load("teleports/messages.yml").getString("sender.teleport-request-pending"))));
        }

    }

    public void cancelRequest(ProxiedPlayer sender, ProxiedPlayer receiver){
        requests.remove(sender.getUniqueId(), receiver.getUniqueId());
        sender.sendMessage(new TextComponent(formator.colorize(config.load("teleports/messages.yml").getString("sender.teleport-request-denied"))));
        receiver.sendMessage(new TextComponent(formator.colorize(config.load("teleports/messages.yml").getString("receiver.teleport-request-denied"))));
    }

    public void acceptRequest(ProxiedPlayer sender, ProxiedPlayer receiver){
        requests.remove(sender.getUniqueId(), receiver.getUniqueId());
        sender.sendMessage(new TextComponent(formator.colorize(config.load("teleports/messages.yml").getString("sender.teleport-request-accepted"))));
        receiver.sendMessage(new TextComponent(formator.colorize(config.load("teleports/messages.yml").getString("receiver.teleport-request-accepted"))));
    }
}
