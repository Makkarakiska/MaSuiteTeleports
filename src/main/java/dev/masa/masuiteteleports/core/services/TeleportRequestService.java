package dev.masa.masuiteteleports.core.services;

import dev.masa.masuitecore.core.channels.BungeePluginChannel;
import dev.masa.masuiteteleports.bungee.Button;
import dev.masa.masuiteteleports.bungee.MaSuiteTeleports;
import dev.masa.masuiteteleports.core.objects.TeleportRequest;
import dev.masa.masuiteteleports.core.objects.TeleportRequestType;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.scheduler.ScheduledTask;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class TeleportRequestService {

    public HashMap<UUID, TeleportRequest> requests = new HashMap<>();
    public HashMap<UUID, Boolean> locks = new HashMap<>();

    private MaSuiteTeleports plugin;

    private int keepRequestAlive = 0;

    public TeleportRequestService(MaSuiteTeleports plugin) {
        this.plugin = plugin;
        this.keepRequestAlive = plugin.config.load("teleports", "settings.yml").getInt("keep-request-alive");
    }

    /**
     * Get teleportation request of the user
     *
     * @param uuid uuid of the receiver
     * @return returns {@link TeleportRequest} or null
     */
    public TeleportRequest getRequest(UUID uuid) {
        return this.requests.get(uuid);
    }

    /**
     * Get sender's teleportation request
     *
     * @param uuid uuid of the sender
     * @return
     */
    public TeleportRequest getSenderRequest(UUID uuid) {
        return this.requests.values().stream().filter(request -> request.getSender().equals(uuid)).findFirst().orElse(null);
    }

    /**
     * Create a new teleportation request
     *
     * @param sender   sender of the request
     * @param receiver receiver of the request
     * @param type     type ({@link TeleportRequestType}) of the request
     */
    public void createRequest(UUID sender, UUID receiver, TeleportRequestType type) {
        // Check if receiver has pending request
        TeleportRequest pendingRequest = this.getRequest(receiver);
        if (pendingRequest != null) {
            plugin.formator.sendMessage(plugin.getProxy().getPlayer(sender),
                    formatMessage(plugin.config.load("teleports", "messages.yml").getString("sender.teleport-request-pending." + type), pendingRequest));
            return;
        }

        // Create request and save it to cache
        TeleportRequest request = new TeleportRequest(sender, receiver, type);
        ScheduledTask timer = this.plugin.getProxy().getScheduler().schedule(this.plugin, () -> this.expireRequest(request), keepRequestAlive, TimeUnit.SECONDS);
        request.setTimer(timer);
        requests.put(receiver, request);

        // Check if receiver has teleportation lock enabled
        if (locks.containsKey(receiver)) {
            if (locks.get(receiver)) {
                this.acceptRequest(request);
                return;
            }
            this.cancelRequest(request);
            return;
        }

        // Send correct messages
        if (request.getType().equals(TeleportRequestType.REQUEST_TO)) {
            plugin.formator.sendMessage(request.getSenderAsPlayer(), formatMessage(plugin.config.load("teleports", "messages.yml").getString("sender.teleport-to-request-incoming"), request));
            plugin.formator.sendMessage(request.getReceiverAsPlayer(), formatMessage(plugin.config.load("teleports", "messages.yml").getString("receiver.teleport-to-request-incoming"), request));
        }

        if (request.getType().equals(TeleportRequestType.REQUEST_HERE)) {
            plugin.formator.sendMessage(request.getSenderAsPlayer(), formatMessage(plugin.config.load("teleports", "messages.yml").getString("sender.teleport-here-request-incoming"), request));
            plugin.formator.sendMessage(request.getReceiverAsPlayer(), formatMessage(plugin.config.load("teleports", "messages.yml").getString("receiver.teleport-here-request-incoming"), request));
        }

        // Send buttons
        this.createControlButtons(request.getReceiverAsPlayer());
    }

    /**
     * Expire the teleportation request
     *
     * @param request request to expire
     */
    public void expireRequest(TeleportRequest request) {
        requests.remove(request.getReceiver());
        plugin.formator.sendMessage(request.getSenderAsPlayer(), formatMessage(plugin.config.load("teleports", "messages.yml").getString("sender.teleport-request-expired"), request));
        plugin.formator.sendMessage(request.getReceiverAsPlayer(), formatMessage(plugin.config.load("teleports", "messages.yml").getString("receiver.teleport-request-expired"), request));
    }

    /**
     * Accept the teleportation request
     *
     * @param request request to accept
     */
    public void acceptRequest(TeleportRequest request) {
        plugin.formator.sendMessage(request.getSenderAsPlayer(), formatMessage(plugin.config.load("teleports", "messages.yml").getString("sender.teleport-request-accepted"), request));
        plugin.formator.sendMessage(request.getReceiverAsPlayer(), formatMessage(plugin.config.load("teleports", "messages.yml").getString("receiver.teleport-request-accepted"), request));

        new BungeePluginChannel(plugin, request.getSenderAsPlayer().getServer().getInfo(), "MaSuiteTeleports", "ApplyWarmup", request.getReceiver().toString()).send();
    }

    /**
     * Deny the teleportation request
     *
     * @param request the teleportation request to cancel
     */
    public void denyRequest(TeleportRequest request) {
        plugin.formator.sendMessage(request.getSenderAsPlayer(), formatMessage(plugin.config.load("teleports", "messages.yml").getString("sender.teleport-request-denied"), request));
        plugin.formator.sendMessage(request.getReceiverAsPlayer(), formatMessage(plugin.config.load("teleports", "messages.yml").getString("receiver.teleport-request-denied"), request));

        this.cancelRequest(request);
    }

    /**
     * Cancel the teleportation request
     *
     * @param request the request to cancel
     */
    public void cancelRequest(TeleportRequest request) {
        request.getTimer().cancel();
        requests.remove(request.getReceiver());
    }

    /**
     * Format message
     *
     * @param message message to format
     * @param request the request to use in formatting
     * @return returns formatted message
     */
    private String formatMessage(String message, TeleportRequest request) {
        ProxiedPlayer sender = request.getSenderAsPlayer();
        ProxiedPlayer receiver = request.getReceiverAsPlayer();

        return message
                .replace("%sender%", sender.getName())
                .replace("%receiver%", receiver.getName())
                .replace("%server%", receiver.getServer().getInfo().getName());
    }


    /**
     * Toggle teleportation lock for player
     *
     * @param uuid  uuid of the player
     * @param value enabled or disabled
     */
    public void toggleTeleportationLock(UUID uuid, boolean value) {
        this.locks.put(uuid, value);
    }

    /**
     * Remove teleportation lock from a player
     *
     * @param uuid uuid of the player
     */
    public void removeTeleportationLock(UUID uuid) {
        this.locks.remove(uuid);
    }

    /**
     * Create control buttons to accept or deny the request
     *
     * @param receiver receiver of the request
     */
    private void createControlButtons(ProxiedPlayer receiver) {
        if (plugin.config.load("teleports", "buttons.yml").getBoolean("enabled")) {
            TextComponent buttons = new TextComponent();
            buttons.addExtra(new Button("accept", "/tpaccept").create());
            buttons.addExtra(new Button("deny", "/tpdeny").create());
            receiver.sendMessage(buttons);
        }
    }

    /**
     * Teleports player to the correct position
     *
     * @param request request to use
     */
    public void teleport(TeleportRequest request) {
        if (request.getType().equals(TeleportRequestType.REQUEST_TO)) {
            plugin.playerTeleportService.teleportPlayerToPlayer(request.getSenderAsPlayer(), request.getReceiverAsPlayer());
        } else {
            plugin.playerTeleportService.teleportPlayerToPlayer(request.getReceiverAsPlayer(), request.getSenderAsPlayer());
        }
    }
}
