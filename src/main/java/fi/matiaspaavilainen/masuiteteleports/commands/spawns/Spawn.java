package fi.matiaspaavilainen.masuiteteleports.commands.spawns;

import fi.matiaspaavilainen.masuitecore.chat.Formator;
import fi.matiaspaavilainen.masuitecore.config.Configuration;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class Spawn extends Command {
    public Spawn() {
        super("spawn", "masuiteteleports.spawn.teleport");
    }

    @Override
    public void execute(CommandSender cs, String[] args) {
        if (!(cs instanceof ProxiedPlayer)) {
            return;
        }

        ProxiedPlayer p = (ProxiedPlayer) cs;
        Formator formator = new Formator();
        Configuration config = new Configuration();
        if (args.length == 0) {

        } else {
            formator.sendMessage(p, config.load("teleports", "syntax.yml").getString("spawn.teleport"));
        }
    }
}
