package fi.matiaspaavilainen.masuiteteleports.core.services;

import fi.matiaspaavilainen.masuiteteleports.bungee.Button;
import fi.matiaspaavilainen.masuiteteleports.bungee.MaSuiteTeleports;
import fi.matiaspaavilainen.masuiteteleports.core.handlers.TeleportHandler;
import fi.matiaspaavilainen.masuiteteleports.core.objects.TeleportType;
import lombok.Data;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.scheduler.ScheduledTask;
import net.md_5.bungee.config.Configuration;

import java.util.concurrent.TimeUnit;

@Data
public class TeleportRequestService {

    private ProxiedPlayer sender, receiver;
    private ScheduledTask scheduler;
    private MaSuiteTeleports plugin;
    private TeleportType type;

    /**
     * A constructor for {@link TeleportRequestService}
     *
     * @param sender   unique id of the sender
     * @param receiver unique id of the receiver
     * @param type     request {@link TeleportRequestService}
     */
    public TeleportRequestService(MaSuiteTeleports plugin, ProxiedPlayer sender, ProxiedPlayer receiver, TeleportType type) {
        this.plugin = plugin;
        this.sender = sender;
        this.receiver = receiver;
        this.type = type;

    }

    /**
     * Create request with given params
     */
    public void create() {
        TeleportHandler.requests.add(this);
        scheduler = this.plugin.getProxy().getScheduler().schedule(this.plugin, this::expired, plugin.config.load("teleports", "settings.yml").getInt("keep-request-alive"), TimeUnit.SECONDS);

        Configuration messages = plugin.config.load("teleports", "messages.yml");
        if (TeleportHandler.lock.containsKey(this.receiver.getUniqueId())) {
            if (TeleportHandler.lock.get(this.receiver.getUniqueId())) {
                this.accept();
            } else {
                this.deny();
            }
            return;
        }
        if (this.type.equals(TeleportType.REQUEST_TO)) {
            plugin.formator.sendMessage(sender, messages
                    .getString("sender.teleport-to-request-incoming")
                    .replace("%sender%", sender.getName())
                    .replace("%receiver%", receiver.getName()));
            plugin.formator.sendMessage(receiver, messages
                    .getString("receiver.teleport-to-request-incoming")
                    .replace("%sender%", sender.getName())
                    .replace("%receiver%", receiver.getName()));
        } else if (this.type.equals(TeleportType.REQUEST_HERE)) {
            plugin.formator.sendMessage(sender, messages
                    .getString("sender.teleport-here-request-incoming")
                    .replace("%sender%", sender.getName())
                    .replace("%receiver%", receiver.getName())
            );
            plugin.formator.sendMessage(receiver, messages
                    .getString("receiver.teleport-here-request-incoming")
                    .replace("%sender%", sender.getName())
                    .replace("%receiver%", receiver.getName())
            );
        }
        this.buttons(receiver);

    }

    /**
     * Cancel after time has expired and send messages to players
     */
    public void expired() {
        plugin.formator.sendMessage(sender, plugin.config.load("teleports", "messages.yml")
                .getString("sender.teleport-request-expired")
                .replace("%sender%", sender.getName())
                .replace("%receiver%", receiver.getName())
        );
        plugin.formator.sendMessage(receiver, plugin.config.load("teleports", "messages.yml")
                .getString("receiver.teleport-request-expired")
                .replace("%sender%", sender.getName())
                .replace("%receiver%", receiver.getName())

        );
        TeleportHandler.requests.remove(this);
    }

    /**
     * Accept the request
     */
    public void accept() {
        if (this.type.equals(TeleportType.REQUEST_TO)) {
            plugin.formator.sendMessage(sender, plugin.config.load("teleports", "messages.yml")
                    .getString("sender.teleport-request-accepted")
                    .replace("%sender%", sender.getName())
                    .replace("%receiver%", receiver.getName())
            );
            plugin.formator.sendMessage(receiver, plugin.config.load("teleports", "messages.yml")
                    .getString("receiver.teleport-request-accepted")
                    .replace("%sender%", sender.getName())
                    .replace("%receiver%", receiver.getName())
            );
            new TeleportHandler(this.plugin).teleportPlayerToPlayer(this.sender, this.receiver);
        } else if (this.type.equals(TeleportType.REQUEST_HERE)) {
            plugin.formator.sendMessage(sender, plugin.config.load("teleports", "messages.yml")
                    .getString("sender.teleport-request-accepted")
                    .replace("%sender%", sender.getName())
                    .replace("%receiver%", receiver.getName())
            );
            plugin.formator.sendMessage(receiver, plugin.config.load("teleports", "messages.yml")
                    .getString("receiver.teleport-request-accepted")
                    .replace("%sender%", sender.getName())
                    .replace("%receiver%", receiver.getName())
            );
            new TeleportHandler(this.plugin).teleportPlayerToPlayer(this.receiver, this.sender);
        }

        this.cancel();
    }

    /**
     * Deny request
     */
    public void deny() {
        plugin.formator.sendMessage(sender, plugin.config.load("teleports", "messages.yml")
                .getString("sender.teleport-request-denied")
                .replace("%sender%", sender.getName())
                .replace("%receiver%", receiver.getName())
        );
        plugin.formator.sendMessage(receiver, plugin.config.load("teleports", "messages.yml")
                .getString("receiver.teleport-request-denied")
                .replace("%sender%", sender.getName())
                .replace("%receiver%", receiver.getName())
        );
        this.cancel();
    }

    /**
     * Cancel scheduler
     */
    public void cancel() {
        this.scheduler.cancel();
        TeleportHandler.requests.remove(this);

    }

    /**
     * Show buttons for player
     *
     * @param receiver the player who will be used
     */
    private void buttons(ProxiedPlayer receiver) {
        if (plugin.config.load("teleports", "buttons.yml").getBoolean("enabled")) {
            TextComponent buttons = new TextComponent();
            buttons.addExtra(new Button("accept", "/tpaccept").create());
            buttons.addExtra(new Button("deny", "/tpdeny").create());
            receiver.sendMessage(buttons);
        }
    }
}

