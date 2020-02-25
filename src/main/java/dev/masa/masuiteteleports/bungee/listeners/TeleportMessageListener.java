package dev.masa.masuiteteleports.bungee.listeners;

import dev.masa.masuitecore.bungee.Utils;
import dev.masa.masuitecore.core.objects.Location;
import dev.masa.masuitecore.core.utils.PlayerFinder;
import dev.masa.masuiteteleports.bungee.MaSuiteTeleports;
import dev.masa.masuiteteleports.bungee.commands.SpawnCommand;
import dev.masa.masuiteteleports.bungee.commands.TeleportForceCommand;
import dev.masa.masuiteteleports.core.objects.TeleportRequest;
import dev.masa.masuiteteleports.core.objects.TeleportRequestType;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class TeleportMessageListener implements Listener {

    private MaSuiteTeleports plugin;
    private Utils utils = new Utils();

    public TeleportMessageListener(MaSuiteTeleports plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPluginMessage(PluginMessageEvent e) throws IOException {
        if (!e.getTag().equals("BungeeCord")) {
            return;
        }
        DataInputStream in = new DataInputStream(new ByteArrayInputStream(e.getData()));
        String subchannel = in.readUTF();
        if (!subchannel.equals("MaSuiteTeleports")) {
            return;
        }
        String childchannel = in.readUTF();
        ProxiedPlayer sender = plugin.getProxy().getPlayer(in.readUTF());
        if (childchannel.equals("GetLocation")) {
            if (sender != null) {
                Location loc = new Location().deserialize(in.readUTF());
                loc.setServer(sender.getServer().getInfo().getName());
                plugin.playerPositionService.locationReceived(sender, loc);
                return;
            }
        }

        if (childchannel.equals("GetLocationWithServer")) {
            if (sender != null) {
                Location loc = new Location().deserialize(in.readUTF());
                loc.setServer(plugin.getProxy().getServerInfo(in.readUTF()).getName());
                plugin.playerPositionService.locationReceived(sender, loc);
                return;
            }
        }

        if (childchannel.equals("TeleportRequest")) {
            boolean status = in.readBoolean();
            UUID receiverUUID = UUID.fromString(in.readUTF());
            TeleportRequest request = plugin.teleportRequestService.getRequest(receiverUUID);
            if (request == null) return;
            if (status) {
                plugin.teleportRequestService.teleport(request);
            }
            plugin.teleportRequestService.cancelRequest(request);
        }

        // Spawn
        String spawnType = plugin.config.load("teleports", "settings.yml").getString("spawn-type");
        if (spawnType.equalsIgnoreCase("server") || spawnType.equalsIgnoreCase("global")) {
            SpawnCommand command = new SpawnCommand(plugin);
            switch (childchannel) {
                case "SpawnPlayer":
                    command.spawn(sender, 0);
                    break;
                case "FirstSpawnPlayer":
                    command.spawn(sender, 1);
                    break;
                case "SetSpawn":
                    Location loc = new Location().deserialize(in.readUTF());
                    command.setSpawn(sender, loc, in.readUTF().equals("default") ? 0 : 1);
                    break;
                case "DelSpawn":
                    command.deleteSpawn(sender, in.readUTF().equals("default") ? 0 : 1);
                    break;
            }
        }

        //Teleportation requests
        ProxiedPlayer receiver;
        switch (childchannel) {
            case "TeleportRequestTo":
                receiver = new PlayerFinder().get(in.readUTF());
                if (utils.isOnline(receiver, sender)) {
                    plugin.teleportRequestService.createRequest(sender.getUniqueId(), receiver.getUniqueId(), TeleportRequestType.REQUEST_TO);
                }
                break;
            case "TeleportRequestHere":
                receiver = new PlayerFinder().get(in.readUTF());
                if (utils.isOnline(receiver, sender)) {
                    plugin.teleportRequestService.createRequest(sender.getUniqueId(), receiver.getUniqueId(), TeleportRequestType.REQUEST_HERE);
                }
                break;
            case "TeleportAccept":
                if (utils.isOnline(sender)) {
                    TeleportRequest request = plugin.teleportRequestService.getRequest(sender.getUniqueId());
                    if (request != null) {
                        plugin.teleportRequestService.acceptRequest(request);
                    } else {
                        plugin.formator.sendMessage(sender, plugin.config.load("teleports", "messages.yml").getString("receiver.no-pending-teleport-requests"));
                    }
                }
                break;
            case "TeleportDeny":
                if (utils.isOnline(sender)) {
                    TeleportRequest request = plugin.teleportRequestService.getRequest(sender.getUniqueId());
                    if (request != null) {
                        plugin.teleportRequestService.denyRequest(request);
                    } else {
                        plugin.formator.sendMessage(sender, plugin.config.load("teleports", "messages.yml").getString("receiver.no-pending-teleport-requests"));
                    }
                }
                break;
            case "TeleportRequestLock":
                String c = in.readUTF();
                if (c.equals("Enable")) {
                    if (utils.isOnline(sender)) {
                        boolean lock = in.readBoolean();
                        plugin.teleportRequestService.toggleTeleportationLock(sender.getUniqueId(), lock);
                        if (lock) {
                            plugin.formator.sendMessage(sender, plugin.config.load("teleports", "messages.yml").getString("tpalock.allow"));
                        } else {
                            plugin.formator.sendMessage(sender, plugin.config.load("teleports", "messages.yml").getString("tpalock.deny"));
                        }
                    }
                    break;
                }
                if (c.equals("Disable")) {
                    if (plugin.teleportRequestService.locks.containsKey(sender.getUniqueId())) {
                        plugin.teleportRequestService.removeTeleportationLock(sender.getUniqueId());
                        plugin.formator.sendMessage(sender, plugin.config.load("teleports", "messages.yml").getString("tpalock.disabled"));
                    } else {
                        plugin.formator.sendMessage(sender, plugin.config.load("teleports", "messages.yml").getString("tpalock.not-locked"));
                    }

                }
                break;
            case "TeleportToggle":
                if (plugin.playerTeleportService.toggles.contains(sender.getUniqueId())) {
                    plugin.playerTeleportService.removeToggle(sender.getUniqueId());
                    plugin.formator.sendMessage(sender, plugin.config.load("teleports", "messages.yml").getString("tptoggle.off"));
                } else {
                    plugin.playerTeleportService.addToggle(sender.getUniqueId());
                    plugin.formator.sendMessage(sender, plugin.config.load("teleports", "messages.yml").getString("tptoggle.on"));
                }
                break;
        }

        //Teleportation forces
        TeleportForceCommand tpforce = new TeleportForceCommand(plugin);
        switch (childchannel) {
            case ("TeleportForceTo"):
                String superchildchannel = in.readUTF();
                switch (superchildchannel) {
                    case "TeleportSenderToTarget":
                        tpforce.tp(sender, in.readUTF(), in.readBoolean());
                        break;
                    case "TeleportTargetToTarget":
                        tpforce.tp(sender, in.readUTF(), in.readUTF());
                        break;
                    case "TeleportToXYZ":
                        tpforce.tp(sender, in.readUTF(), in.readDouble(), in.readDouble(), in.readDouble());
                        break;
                    case "TeleportToCoordinates":
                        tpforce.tp(sender, in.readUTF(), new Location(in.readUTF(), in.readDouble(), in.readDouble(), in.readDouble()));
                        break;
                }
                break;
            case "TeleportForceHere":
                tpforce.tphere(sender, in.readUTF(), in.readBoolean());
                break;
            case "TeleportForceAll":
                tpforce.tpall(sender);
                break;
        }

        // Back
        if (childchannel.equals("Back")) {
            if (plugin.playerPositionService.positions.containsKey(sender.getUniqueId())) {
                Location loc = plugin.playerPositionService.positions.get(sender.getUniqueId());
                plugin.playerPositionService.requestPosition(sender);
                if (!loc.getServer().equals(sender.getServer().getInfo().getName())) {
                    sender.connect(plugin.getProxy().getServerInfo(loc.getServer()));
                    plugin.getProxy().getScheduler().schedule(plugin, () -> tpforce.tp(sender, sender.getName(), loc), plugin.config.load(null, "config.yml").getInt("teleportation-delay"), TimeUnit.MILLISECONDS);
                } else {
                    tpforce.tp(sender, sender.getName(), loc);
                }
                plugin.formator.sendMessage(sender, plugin.config.load("teleports", "messages.yml").getString("back.last-loc"));
            } else {
                plugin.formator.sendMessage(sender, plugin.config.load("teleports", "messages.yml").getString("back.no-loc"));
            }
        }

    }
}
