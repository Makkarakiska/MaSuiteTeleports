package fi.matiaspaavilainen.masuiteteleports.managers;

import fi.matiaspaavilainen.masuitecore.chat.Formator;
import fi.matiaspaavilainen.masuitecore.config.Configuration;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

public class Teleport {
    public static HashMap<UUID, UUID> senders = new HashMap<>();
    public static HashMap<UUID, UUID> receivers = new HashMap<>();
    public static HashMap<UUID, String> method = new HashMap<>();

    public static void PlayerToPlayer(ProxiedPlayer sender, ProxiedPlayer receiver) {
        Formator formator = new Formator();
        Configuration config = new Configuration();
        if (receiver == null) {
            formator.sendMessage(sender, config.load(null,"messages.yml").getString("player-not-online"));
        } else {
            ByteArrayOutputStream b = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(b);
            try {
                if (!sender.getServer().getInfo().getName().equals(receiver.getServer().getInfo().getName())) {
                    sender.connect(ProxyServer.getInstance().getServerInfo(receiver.getServer().getInfo().getName()));
                }
                out.writeUTF("Teleport");
                out.writeUTF("PlayerToPlayer");
                if(method.containsKey(sender.getUniqueId())){
                    if(method.get(sender.getUniqueId()).equals("here")){
                        out.writeUTF(receiver.getName());
                        out.writeUTF(sender.getName());
                    }else{
                        out.writeUTF(sender.getName());
                        out.writeUTF(receiver.getName());
                    }
                }else{
                    out.writeUTF(sender.getName());
                    out.writeUTF(receiver.getName());
                }
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
            receiver.getServer().sendData("BungeeCord", b.toByteArray());
        }
    }
}
