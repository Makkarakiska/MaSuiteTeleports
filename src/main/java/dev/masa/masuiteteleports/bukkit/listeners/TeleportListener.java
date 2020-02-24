package dev.masa.masuiteteleports.bukkit.listeners;

import dev.masa.masuitecore.core.adapters.BukkitAdapter;
import dev.masa.masuitecore.core.channels.BukkitPluginChannel;
import dev.masa.masuitecore.core.objects.Location;
import dev.masa.masuiteteleports.bukkit.MaSuiteTeleports;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

public class TeleportListener implements PluginMessageListener {
    private MaSuiteTeleports plugin;

    public TeleportListener(MaSuiteTeleports p) {
        plugin = p;
    }

    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        if (!channel.equals("BungeeCord")) {
            return;
        }

        final DataInputStream in = new DataInputStream(new ByteArrayInputStream(message));
        String subchannel = null;
        String method = null;
        try {
            subchannel = in.readUTF();
            if (!subchannel.equals("MaSuiteTeleports")) {
                return;
            }
            method = in.readUTF();
            if (method.equals("PlayerToPlayer")) {
                teleportPlayer(in.readUTF(), in.readUTF());
            }
            if (method.equals("PlayerToXYZ")) {
                Player p = Bukkit.getPlayer(in.readUTF());
                if (p == null) {
                    return;
                }
                plugin.tpQue.add(p.getUniqueId());
                p.leaveVehicle();

                Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                    try {
                        p.teleport(new org.bukkit.Location(p.getWorld(), in.readDouble(), in.readDouble(), in.readDouble()));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }, 1);

                Bukkit.getServer().getScheduler().runTaskLater(plugin, () -> plugin.tpQue.remove(p.getUniqueId()), 100);
            }
            if (method.equals("PlayerToLocation")) {
                Player p = Bukkit.getPlayer(in.readUTF());
                if (p == null) {
                    return;
                }
                plugin.tpQue.add(p.getUniqueId());
                p.leaveVehicle();

                Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                    try {
                        org.bukkit.Location location = BukkitAdapter.adapt(new Location().deserialize(in.readUTF()));
                        p.teleport(location);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }, 1);

                Bukkit.getServer().getScheduler().runTaskLater(plugin, () -> plugin.tpQue.remove(p.getUniqueId()), 100);
            }

            if (method.equals("SpawnPlayer")) {
                Player p = Bukkit.getPlayer(in.readUTF());
                if (p == null) {
                    return;
                }
                Location loc = new Location().deserialize(in.readUTF());
                p.leaveVehicle();
                Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> p.teleport(BukkitAdapter.adapt(loc)), 1);
            }
            if (method.equals("GetLocation")) {
                Player p = Bukkit.getPlayer(in.readUTF());
                if (p == null) {
                    return;
                }
                String server = in.readUTF();
                new BukkitPluginChannel(plugin, p, "MaSuiteTeleports", "GetLocation", p.getName(), BukkitAdapter.adapt(p.getLocation()).serialize(), server).send();
            }

            if (method.equals("ApplyWarmup")) {
                String receiverUUID = in.readUTF();
                plugin.api.getWarmupService().applyWarmup(player, "masuiteteleports.warmup.bypass", "teleports", success -> {
                    Player receiver = plugin.getServer().getPlayer(receiverUUID);
                    if (receiver == null) {
                        return;
                    }
                    if (success) {

                        new BukkitPluginChannel(plugin, receiver, "MaSuiteTeleports", "GetLocation", receiver.getName(), BukkitAdapter.adapt(receiver.getLocation()).serialize()).send();
                        new BukkitPluginChannel(plugin, receiver, "MaSuiteTeleports", "TeleportRequest", player.getName(), true, receiverUUID).send();
                        return;
                    }
                    new BukkitPluginChannel(plugin, receiver, "MaSuiteTeleports", "TeleportRequest", receiver.getName(), false, receiverUUID).send();
                });
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void teleportPlayer(final String s, final String t) {
        Player player = Bukkit.getPlayer(s);
        Player target = Bukkit.getPlayer(t);
        Bukkit.getServer().getScheduler().runTaskLater(plugin, () -> {
            if (player != null && target != null) {
                plugin.tpQue.add(player.getUniqueId());
                player.leaveVehicle();

                Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                    player.teleport(target);
                }, 1);

            }
        }, 5);

        if (player != null) {
            Bukkit.getServer().getScheduler().runTaskLater(plugin, () -> plugin.tpQue.remove(player.getUniqueId()), 100);
        }
    }
}
