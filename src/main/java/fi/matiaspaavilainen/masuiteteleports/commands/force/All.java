package fi.matiaspaavilainen.masuiteteleports.commands.force;

import fi.matiaspaavilainen.masuitecore.chat.Formator;
import fi.matiaspaavilainen.masuitecore.config.Configuration;
import fi.matiaspaavilainen.masuiteteleports.MaSuiteTeleports;
import fi.matiaspaavilainen.masuiteteleports.managers.PlayerFinder;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Listener;

import static fi.matiaspaavilainen.masuiteteleports.managers.Teleport.PlayerToPlayer;

public class All extends Command implements Listener {
    public All(MaSuiteTeleports p) {
        super("tpall", "masuiteteleports.teleport.force.all");
    }

    @Override
    public void execute(CommandSender cs, String[] args) {
        Formator formator = new Formator();
        Configuration config = new Configuration();
        if(args.length == 0){
            ProxiedPlayer sender = (ProxiedPlayer) cs;
            for(ProxiedPlayer p : ProxyServer.getInstance().getPlayers()){
                if(p != cs){
                    PlayerToPlayer(p, sender);
                    formator.sendMessage(p, config.load("teleports","messages.yml")
                            .getString("teleported")
                            .replace("%player%", cs.getName())
                    );
                }
            }
        }else if (args.length == 1) {
            ProxiedPlayer target = new PlayerFinder().get(args[0]);
            ;
            if(target == null){
                formator.sendMessage((ProxiedPlayer) cs, config.load("messages.yml").getString("player-not-online"));
                return;
            }
            for(ProxiedPlayer p : ProxyServer.getInstance().getPlayers()){
                PlayerToPlayer(p, target);
                formator.sendMessage(p, config.load("teleports","messages.yml")
                        .getString("teleported")
                        .replace("%player%", target.getName())
                );
            }
        } else {
            formator.sendMessage((ProxiedPlayer) cs, config.load("teleports", "syntax.yml").getString("tpall"));
        }
    }
}
