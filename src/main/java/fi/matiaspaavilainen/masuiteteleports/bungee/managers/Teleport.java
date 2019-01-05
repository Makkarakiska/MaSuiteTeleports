package fi.matiaspaavilainen.masuiteteleports.bungee.managers;

import fi.matiaspaavilainen.masuitecore.bungee.Utils;
import fi.matiaspaavilainen.masuiteteleports.bungee.MaSuiteTeleports;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class Teleport {
    public static HashMap<UUID, UUID> senders = new HashMap<>();
    public static HashMap<UUID, UUID> receivers = new HashMap<>();
    public static HashMap<UUID, String> method = new HashMap<>();
    public static HashMap<UUID, Boolean> lock = new HashMap<>();
    private MaSuiteTeleports plugin;

    public Teleport(MaSuiteTeleports p) {
        plugin = p;
    }

    public void playerToPlayer(ProxiedPlayer sender, ProxiedPlayer receiver) {
        if (new Utils().isOnline(receiver, sender)) {
            ByteArrayOutputStream b = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(b);

            try {
                out.writeUTF("MaSuiteTeleports");
                out.writeUTF("PlayerToPlayer");
                if (method.containsKey(sender.getUniqueId())) {
                    if (method.get(sender.getUniqueId()).equals("here")) {
                        if (!receiver.getServer().getInfo().getName().equals(sender.getServer().getInfo().getName())) {
                            receiver.connect(ProxyServer.getInstance().getServerInfo(sender.getServer().getInfo().getName()));
                        }
                    }
                    if (method.get(sender.getUniqueId()).equals("to")) {
                        if (!sender.getServer().getInfo().getName().equals(receiver.getServer().getInfo().getName())) {
                            sender.connect(ProxyServer.getInstance().getServerInfo(receiver.getServer().getInfo().getName()));
                        }
                    }
                    if (method.get(sender.getUniqueId()).equals("here")) {
                        out.writeUTF(receiver.getName());
                        out.writeUTF(sender.getName());
                    } else if (method.get(sender.getUniqueId()).equals("to")) {
                        out.writeUTF(sender.getName());
                        out.writeUTF(receiver.getName());
                    }
                } else {
                    if (!sender.getServer().getInfo().getName().equals(receiver.getServer().getInfo().getName())) {
                        sender.connect(ProxyServer.getInstance().getServerInfo(receiver.getServer().getInfo().getName()));
                    }
                    out.writeUTF(sender.getName());
                    out.writeUTF(receiver.getName());
                }
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }

            if (method.containsKey(sender.getUniqueId())) {
                if (method.get(sender.getUniqueId()).equals("to")) {
                    teleport(sender, receiver, b);
                } else if (method.get(sender.getUniqueId()).equals("here")) {
                    teleport(sender, receiver, b);
                }
            } else {
                teleport(sender, receiver, b);
            }
        }
    }

    private void teleport(ProxiedPlayer sender, ProxiedPlayer receiver, ByteArrayOutputStream b) {
        if (!receiver.getServer().getInfo().getName().equals(sender.getServer().getInfo().getName())) {
            ProxyServer.getInstance().getScheduler().schedule(plugin, () -> receiver.getServer().sendData("BungeeCord", b.toByteArray()), 500, TimeUnit.MILLISECONDS);
        } else {
            receiver.getServer().sendData("BungeeCord", b.toByteArray());
        }
    }
}
