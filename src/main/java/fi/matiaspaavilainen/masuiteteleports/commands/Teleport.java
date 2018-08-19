package fi.matiaspaavilainen.masuiteteleports.commands;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import fi.matiaspaavilainen.masuitecore.chat.Formator;
import fi.matiaspaavilainen.masuitecore.config.Configuration;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class Teleport extends Command {
    public Teleport() {
        super("teleport");
    }

    @Override
    public void execute(CommandSender cs, String[] args) {
        Configuration config = new Configuration();
        Formator formator = new Formator();
        ProxiedPlayer sender = (ProxiedPlayer) cs;

        // If target is player
        if(args.length == 1 ){
            ProxiedPlayer target = ProxyServer.getInstance().getPlayer(args[0]);
            if(target != null && !target.isConnected()){
                sender.sendMessage(new TextComponent(formator.colorize(config.load("messages.yml").getString("player-not-online"))));
            } else {

            }
        }

    }
}
