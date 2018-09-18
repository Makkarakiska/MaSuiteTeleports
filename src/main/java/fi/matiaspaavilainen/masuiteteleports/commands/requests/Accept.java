package fi.matiaspaavilainen.masuiteteleports.commands.requests;

import fi.matiaspaavilainen.masuitecore.chat.Formator;
import fi.matiaspaavilainen.masuitecore.config.Configuration;
import fi.matiaspaavilainen.masuiteteleports.MaSuiteTeleports;
import fi.matiaspaavilainen.masuiteteleports.managers.requests.Request;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class Accept extends Command {
    private MaSuiteTeleports plugin;
    public Accept(MaSuiteTeleports p) {
        super("tpaccept", "masuiteteleports.teleport.accept", "tpyes");
        plugin = p;
    }

    @Override
    public void execute(CommandSender cs, String[] args) {
        Configuration config = new Configuration();
        Formator formator = new Formator();

        // Teleport sender to player
        if (args.length == 0) {
            ProxiedPlayer sender = (ProxiedPlayer) cs;
                Request tp = new Request(plugin);
                tp.acceptRequest(sender);
        }else{
            formator.sendMessage((ProxiedPlayer) cs, config.load("teleports", "syntax.yml").getString("tpaccept"));
        }
    }
}
