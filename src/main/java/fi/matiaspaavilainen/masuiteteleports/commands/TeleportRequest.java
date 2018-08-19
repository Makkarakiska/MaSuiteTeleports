package fi.matiaspaavilainen.masuiteteleports.commands;

import fi.matiaspaavilainen.masuitecore.chat.Formator;
import fi.matiaspaavilainen.masuitecore.config.Configuration;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class TeleportRequest extends Command {
    public TeleportRequest() {
        super("tpa", "masuiteteleports.teleport.request");
    }

    @Override
    public void execute(CommandSender cs, String[] args) {
        Configuration config = new Configuration();
        Formator formator = new Formator();


        // Teleport sender to player
        if (args.length == 1) {
            System.out.println("TeleportRequest");
            ProxiedPlayer sender = (ProxiedPlayer) cs;
            ProxiedPlayer target = ProxyServer.getInstance().getPlayer(args[0]);
            if (target == null) {
                sender.sendMessage(new TextComponent(formator.colorize(config.load("messages.yml").getString("player-not-online"))));
            } else {
                fi.matiaspaavilainen.masuiteteleports.managers.TeleportRequest tp = new fi.matiaspaavilainen.masuiteteleports.managers.TeleportRequest();
                tp.createRequest(sender, target);
                /*ByteArrayOutputStream b = new ByteArrayOutputStream();
                DataOutputStream out = new DataOutputStream(b);
                try {
                    if (!sender.getServer().getInfo().getName().equals(target.getServer().getInfo().getName())) {
                        sender.connect(ProxyServer.getInstance().getServerInfo(target.getServer().getInfo().getName()));
                    }
                    out.writeUTF("Teleport");
                    out.writeUTF("TeleportRequest");
                    out.writeUTF(sender.getName());
                    out.writeUTF(target.getName());
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }
                target.getServer().sendData("BungeeCord", b.toByteArray());*/
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
