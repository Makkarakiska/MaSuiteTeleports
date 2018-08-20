package fi.matiaspaavilainen.masuiteteleports.commands.requests;

import fi.matiaspaavilainen.masuitecore.chat.Formator;
import fi.matiaspaavilainen.masuitecore.config.Configuration;
import fi.matiaspaavilainen.masuiteteleports.MaSuiteTeleports;
import fi.matiaspaavilainen.masuiteteleports.managers.requests.Request;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class Deny extends Command {
    private MaSuiteTeleports plugin;
    public Deny(MaSuiteTeleports p) {
        super("tpdeny", "masuiteteleports.teleport.deny", "tpno");
        plugin = p;
    }

    @Override
    public void execute(CommandSender cs, String[] args) {
        if (args.length == 0) {
            ProxiedPlayer sender = (ProxiedPlayer) cs;
            fi.matiaspaavilainen.masuiteteleports.managers.requests.Request tp = new Request(plugin);
            tp.cancelRequest(sender, "player");
        }
    }
}
