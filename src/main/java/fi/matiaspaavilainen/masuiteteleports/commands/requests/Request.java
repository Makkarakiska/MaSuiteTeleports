package fi.matiaspaavilainen.masuiteteleports.commands.requests;

import fi.matiaspaavilainen.masuitecore.chat.Formator;
import fi.matiaspaavilainen.masuitecore.config.Configuration;
import fi.matiaspaavilainen.masuiteteleports.MaSuiteTeleports;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Listener;

public class Request extends Command implements Listener {
    private MaSuiteTeleports plugin;
    public Request(MaSuiteTeleports p) {
        super("tpa", "masuiteteleports.teleport.request");
        plugin = p;
    }

    @Override
    public void execute(CommandSender cs, String[] args) {
        Configuration config = new Configuration();
        Formator formator = new Formator();


        // Teleport sender to player
        if (args.length == 1) {
            System.out.println("Request");
            ProxiedPlayer sender = (ProxiedPlayer) cs;
            ProxiedPlayer target = ProxyServer.getInstance().getPlayer(args[0]);
            if (target == null) {
                sender.sendMessage(new TextComponent(formator.colorize(config.load(null,"messages.yml").getString("player-not-online"))));
            } else {
                fi.matiaspaavilainen.masuiteteleports.managers.requests.Request tp = new fi.matiaspaavilainen.masuiteteleports.managers.requests.Request(plugin);
                tp.createRequest(sender, target);
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
