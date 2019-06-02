package fi.matiaspaavilainen.masuiteteleports.bukkit.listeners;

import fi.matiaspaavilainen.masuitecore.core.channels.BukkitPluginChannel;
import fi.matiaspaavilainen.masuiteteleports.bukkit.MaSuiteTeleports;
import org.bukkit.Bukkit;
import org.bukkit.Location;
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
            if (subchannel.equals("MaSuiteTeleports")) {
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
                    p.teleport(new Location(p.getWorld(), in.readDouble(), in.readDouble(), in.readDouble()));
                    Bukkit.getServer().getScheduler().runTaskLater(plugin, () -> plugin.tpQue.remove(p.getUniqueId()), 100);
                }
                if (method.equals("PlayerToLocation")) {
                    Player p = Bukkit.getPlayer(in.readUTF());
                    if (p == null) {
                        return;
                    }
                    plugin.tpQue.add(p.getUniqueId());
                    p.leaveVehicle();
                    p.teleport(new Location(Bukkit.getWorld(in.readUTF()), in.readDouble(), in.readDouble(), in.readDouble()));
                    Bukkit.getServer().getScheduler().runTaskLater(plugin, () -> plugin.tpQue.remove(p.getUniqueId()), 100);
                }

                if (method.equals("SpawnPlayer")) {
                    Player p = Bukkit.getPlayer(in.readUTF());
                    if (p == null) {
                        return;
                    }
                    String[] locInfo = in.readUTF().split(":");
                    Location loc = new Location(Bukkit.getWorld(locInfo[0]), Double.parseDouble(locInfo[1]), Double.parseDouble(locInfo[2]), Double.parseDouble(locInfo[3]), Float.parseFloat(locInfo[4]), Float.parseFloat(locInfo[5]));
                    p.leaveVehicle();
                    p.teleport(loc);
                }
                if (method.equals("GetLocation")) {
                    Player p = Bukkit.getPlayer(in.readUTF());
                    if (p == null) {
                        return;
                    }
                    String server = in.readUTF();
                    Location loc = p.getLocation();
                    new BukkitPluginChannel(plugin, p, new Object[]{"MaSuiteTeleports", "GetLocation", p.getName(), loc.getWorld().getName() + ":" + loc.getX() + ":" + loc.getY() + ":" + loc.getZ() + ":" + loc.getYaw() + ":" + loc.getPitch(), server}).send();

                }
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
                player.teleport(target);
            }
        }, 5);

        if (player != null) {
            Bukkit.getServer().getScheduler().runTaskLater(plugin, () -> plugin.tpQue.remove(player.getUniqueId()), 100);
        }
    }
}
