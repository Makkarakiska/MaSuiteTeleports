package fi.matiaspaavilainen.masuiteteleports.commands.spawns;

import fi.matiaspaavilainen.masuitecore.chat.Formator;
import fi.matiaspaavilainen.masuitecore.config.Configuration;
import fi.matiaspaavilainen.masuitecore.listeners.MaSuitePlayerLocation;
import fi.matiaspaavilainen.masuitecore.managers.Location;
import fi.matiaspaavilainen.masuitecore.managers.MaSuitePlayer;
import fi.matiaspaavilainen.masuiteteleports.MaSuiteTeleports;
import fi.matiaspaavilainen.masuiteteleports.managers.Spawn;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.util.concurrent.TimeUnit;

public class Set extends Command {
    public Set() {
        super("setspawn", "masuiteteleports.spawn.set", "spawnset", "createspawn");
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
            MaSuitePlayer msp = new MaSuitePlayer().find(p.getUniqueId());
            msp.requestLocation();
            ProxyServer.getInstance().getScheduler().schedule(new MaSuiteTeleports(), () -> {
                Spawn spawn = new Spawn();
                spawn.setServer(p.getServer().getInfo().getName());
                spawn.setLocation(MaSuitePlayerLocation.locations.get(p.getUniqueId()));
                spawn.create(spawn);
                formator.sendMessage(p, config.load("teleports", "messages.yml").getString("spawn.set"));
            }, 50, TimeUnit.MILLISECONDS);
            MaSuitePlayerLocation.locations.remove(p.getUniqueId());
        } else {
            formator.sendMessage(p, config.load("teleports", "syntax.yml").getString("spawn.set"));
        }
    }
}
