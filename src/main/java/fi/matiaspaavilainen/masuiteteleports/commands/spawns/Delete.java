package fi.matiaspaavilainen.masuiteteleports.commands.spawns;

import fi.matiaspaavilainen.masuitecore.chat.Formator;
import fi.matiaspaavilainen.masuitecore.config.Configuration;
import fi.matiaspaavilainen.masuiteteleports.managers.Spawn;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class Delete extends Command {
    public Delete() {
        super("delspawn","masuiteteleports.spawn.set", "spawndel", "deletespawn");
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
            Spawn spawn = new Spawn();
            if(spawn.find(p.getServer().getInfo().getName()).getServer() != null){
                spawn.delete(p);
            }else{
                formator.sendMessage(p, config.load("teleports", "messages.yml").getString("spawn.not-found"));
            }
        } else {
            formator.sendMessage(p, config.load("teleports", "syntax.yml").getString("spawn.deleted"));
        }
    }
}
