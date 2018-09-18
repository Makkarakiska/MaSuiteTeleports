package fi.matiaspaavilainen.masuiteteleports.commands.requests;

import fi.matiaspaavilainen.masuitecore.chat.Formator;
import fi.matiaspaavilainen.masuitecore.config.Configuration;
import fi.matiaspaavilainen.masuiteteleports.MaSuiteTeleports;
import fi.matiaspaavilainen.masuiteteleports.managers.PlayerFinder;
import fi.matiaspaavilainen.masuiteteleports.managers.requests.Request;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Listener;

public class To extends Command implements Listener {
    private MaSuiteTeleports plugin;
    public To(MaSuiteTeleports p) {
        super("tpa", "masuiteteleports.teleport.request");
        plugin = p;
    }

    @Override
    public void execute(CommandSender cs, String[] args) {
        Configuration config = new Configuration();
        Formator formator = new Formator();
        // Teleport sender to player
        if (args.length == 1) {
            ProxiedPlayer sender = (ProxiedPlayer) cs;
            ProxiedPlayer target = new PlayerFinder().get(args[0]);
            if (target == null) {
                formator.sendMessage(sender, config.load(null,"messages.yml").getString("player-not-online"));
            } else {
                Request tp = new Request(plugin);
                tp.createRequest(sender, target);
            }
        }else{
            formator.sendMessage((ProxiedPlayer) cs, config.load("teleports", "syntax.yml").getString("tpa"));
        }
    }
}
