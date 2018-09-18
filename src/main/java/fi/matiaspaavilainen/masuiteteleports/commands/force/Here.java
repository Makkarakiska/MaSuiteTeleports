package fi.matiaspaavilainen.masuiteteleports.commands.force;

import fi.matiaspaavilainen.masuitecore.chat.Formator;
import fi.matiaspaavilainen.masuitecore.config.Configuration;
import fi.matiaspaavilainen.masuiteteleports.MaSuiteTeleports;
import fi.matiaspaavilainen.masuiteteleports.managers.PlayerFinder;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Listener;

import static fi.matiaspaavilainen.masuiteteleports.managers.Teleport.PlayerToPlayer;

public class Here extends Command implements Listener {
    public Here(MaSuiteTeleports p) {
        super("tphere", "masuiteteleports.teleport.force.here");
    }

    @Override
    public void execute(CommandSender cs, String[] args) {
        Configuration config = new Configuration();
        Formator formator = new Formator();
        if (args.length == 1) {
            ProxiedPlayer sender = (ProxiedPlayer) cs;
            ProxiedPlayer target = new PlayerFinder().get(args[0]);
            if(target == null){
                formator.sendMessage((ProxiedPlayer) cs, config.load(null,"messages.yml").getString("player-not-online"));
                return;
            }
            PlayerToPlayer(target, sender);
        }else{
            formator.sendMessage((ProxiedPlayer) cs, config.load("teleports", "syntax.yml").getString("tphere"));
        }
    }
}
