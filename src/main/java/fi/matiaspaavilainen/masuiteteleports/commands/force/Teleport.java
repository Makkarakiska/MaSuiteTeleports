package fi.matiaspaavilainen.masuiteteleports.commands.force;

import fi.matiaspaavilainen.masuitecore.chat.Formator;
import fi.matiaspaavilainen.masuitecore.config.Configuration;
import fi.matiaspaavilainen.masuiteteleports.MaSuiteTeleports;
import fi.matiaspaavilainen.masuiteteleports.managers.PlayerFinder;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Listener;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import static fi.matiaspaavilainen.masuiteteleports.managers.Teleport.PlayerToPlayer;

public class Teleport extends Command implements Listener {
    public Teleport(MaSuiteTeleports p) {
        super("tp", "masuiteteleports.teleport.force", "teleport");
    }
    private PlayerFinder playerFinder = new PlayerFinder();
    @Override
    public void execute(CommandSender cs, String[] args) {
        Configuration config = new Configuration();
        Formator formator = new Formator();

        if(!(cs instanceof ProxiedPlayer)){
            return;
        }

        // Teleport sender to player
        if (args.length == 1) {
            ProxiedPlayer sender = (ProxiedPlayer) cs;
            ProxiedPlayer target = playerFinder.get(args[0]);
            if(target == null){
                formator.sendMessage((ProxiedPlayer) cs,config.load(null,"messages.yml").getString("player-not-online"));
                return;
            }
            PlayerToPlayer(sender, target);
            formator.sendMessage(sender, config.load("teleports","messages.yml")
                    .getString("teleported")
                    .replace("%player%", target.getName())
            );
        }

        // Teleport player to other player
         else if (args.length == 2) {
            ProxiedPlayer sender = playerFinder.get(args[0]);
            ProxiedPlayer target = playerFinder.get(args[1]);
            if(sender == null || target == null){
                formator.sendMessage((ProxiedPlayer) cs,config.load(null,"messages.yml").getString("player-not-online"));
                return;
            }
            PlayerToPlayer(sender, target);
            formator.sendMessage(sender,config.load("teleports","messages.yml")
                    .getString("teleported")
                    .replace("%player%", target.getName())
            );
        }

        // Teleport sender to coords
        else if (args.length == 3) {
            ProxiedPlayer sender = (ProxiedPlayer) cs;
            ByteArrayOutputStream b = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(b);
            if (isDouble(args[0]) && isDouble(args[1]) && isDouble(args[2])) {
                try {
                    out.writeUTF("Teleport");
                    out.writeUTF("SenderToCoords");
                    out.writeUTF(sender.getName());
                    out.writeDouble(Double.parseDouble(args[0]));
                    out.writeDouble(Double.parseDouble(args[1]));
                    out.writeDouble(Double.parseDouble(args[2]));
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }
                sender.getServer().sendData("BungeeCord", b.toByteArray());
            }else{
                formator.sendMessage(sender, config.load("teleports","messages.yml").getString("invalid-coords-given"));
            }

        }

        // Teleport player to coords
        else if (args.length == 4) {
            ProxiedPlayer sender = (ProxiedPlayer) cs;
            ProxiedPlayer target = playerFinder.get(args[0]);
            if (target == null) {
                sender.sendMessage(new TextComponent(formator.colorize(config.load(null,"messages.yml").getString("player-not-online"))));
                return;
            }
                if (isDouble(args[1]) && isDouble(args[2]) && isDouble(args[3])) {
                        ByteArrayOutputStream b = new ByteArrayOutputStream();
                        DataOutputStream out = new DataOutputStream(b);
                        try {
                            out.writeUTF("Teleport");
                            out.writeUTF("PlayerToCoords");
                            out.writeUTF(target.getName());
                            out.writeDouble(Double.parseDouble(args[0]));
                            out.writeDouble(Double.parseDouble(args[1]));
                            out.writeDouble(Double.parseDouble(args[2]));
                        } catch (IOException e) {
                            System.out.println(e.getMessage());
                        }
                        sender.getServer().sendData("BungeeCord", b.toByteArray());
                }else{
                    formator.sendMessage(sender, config.load("teleports","messages.yml").getString("invalid-coords-given"));
                }

        }
        else {
            formator.sendMessage((ProxiedPlayer) cs, config.load("teleports", "syntax.yml").getString("tp.title"));
            for(String syntax: config.load("teleports", "syntax.yml").getStringList("tp.syntaxes")){
                formator.sendMessage((ProxiedPlayer) cs, syntax);
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
