package fi.matiaspaavilainen.masuiteteleports.commands;

import fi.matiaspaavilainen.masuitecore.chat.Formator;
import fi.matiaspaavilainen.masuitecore.config.Configuration;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Teleport extends Command {
    public Teleport() {
        super("tp", "masuiteteleports.teleport.force", "teleport");
    }

    @Override
    public void execute(CommandSender cs, String[] args) {
        Configuration config = new Configuration();
        Formator formator = new Formator();


        // Teleport sender to player
        if (args.length == 1) {
            System.out.println("SenderToPlayer");
            ProxiedPlayer sender = (ProxiedPlayer) cs;
            ProxiedPlayer target = ProxyServer.getInstance().getPlayer(args[0]);
            if (target == null) {
                sender.sendMessage(new TextComponent(formator.colorize(config.load("messages.yml").getString("player-not-online"))));
            } else {
                ByteArrayOutputStream b = new ByteArrayOutputStream();
                DataOutputStream out = new DataOutputStream(b);
                try {
                    if (!sender.getServer().getInfo().getName().equals(target.getServer().getInfo().getName())) {
                        sender.connect(ProxyServer.getInstance().getServerInfo(target.getServer().getInfo().getName()));
                    }
                    out.writeUTF("Teleport");
                    out.writeUTF("SenderToPlayer");
                    out.writeUTF(sender.getName());
                    out.writeUTF(target.getName());
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }
                target.getServer().sendData("BungeeCord", b.toByteArray());
            }
        }

        // Teleport player to other player
        if (args.length == 2) {
            System.out.println("PlayerToPlayer");
            ProxiedPlayer sender = ProxyServer.getInstance().getPlayer(args[0]);
            ProxiedPlayer target = ProxyServer.getInstance().getPlayer(args[1]);
            if (target == null || sender == null) {
                sender.sendMessage(new TextComponent(formator.colorize(config.load("messages.yml").getString("player-not-online"))));
            } else {
                ByteArrayOutputStream b = new ByteArrayOutputStream();
                DataOutputStream out = new DataOutputStream(b);
                try {
                    if (!sender.getServer().getInfo().getName().equals(target.getServer().getInfo().getName())) {
                        sender.connect(ProxyServer.getInstance().getServerInfo(target.getServer().getInfo().getName()));
                    }
                    out.writeUTF("Teleport");
                    out.writeUTF("PlayerToPlayer");
                    out.writeUTF(sender.getName());
                    out.writeUTF(target.getName());
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }
                sender.getServer().sendData("BungeeCord", b.toByteArray());
            }
        }

        // Teleport sender to coords
        if (args.length == 3) {
            ProxiedPlayer sender = (ProxiedPlayer) cs;
            System.out.println("SenderToCoords");

            ByteArrayOutputStream b = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(b);
            if (isDouble(args[0]) && isDouble(args[1]) && isDouble(args[2])) {
                try {
                    out.writeUTF("Teleport");
                    out.writeUTF("SenderToCoords");
                    out.writeUTF(sender.getName());
                    out.writeUTF(args[0]);
                    out.writeUTF(args[1]);
                    out.writeUTF(args[2]);
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }
                sender.getServer().sendData("BungeeCord", b.toByteArray());
            }else{
                sender.sendMessage(new TextComponent("Invalid coords given"));
            }

        }


        // Teleport player to coords
        if (args.length == 4) {
            System.out.println("PlayerToCoords");
            ProxiedPlayer sender = (ProxiedPlayer) cs;
            ProxiedPlayer target = ProxyServer.getInstance().getPlayer(args[0]);
            if (target == null) {
                sender.sendMessage(new TextComponent(formator.colorize(config.load("messages.yml").getString("player-not-online"))));
            } else {
                if (isDouble(args[1]) && isDouble(args[2]) && isDouble(args[3])) {
                        ByteArrayOutputStream b = new ByteArrayOutputStream();
                        DataOutputStream out = new DataOutputStream(b);
                        try {
                            out.writeUTF("Teleport");
                            out.writeUTF("PlayerToCoords");
                            out.writeUTF(target.getName());
                            out.writeUTF(args[1]);
                            out.writeUTF(args[2]);
                            out.writeUTF(args[3]);
                        } catch (IOException e) {
                            System.out.println(e.getMessage());
                        }
                        sender.getServer().sendData("BungeeCord", b.toByteArray());
                }else{
                    sender.sendMessage(new TextComponent("Invalid coords given"));
                }

            }
        }
    }

    private boolean isDouble(String string) {
        try {
            Double.parseDouble(string);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }
}
