package fi.matiaspaavilainen.masuiteteleports;

import fi.matiaspaavilainen.masuitecore.Updator;
import fi.matiaspaavilainen.masuitecore.config.Configuration;
import fi.matiaspaavilainen.masuitecore.managers.Location;
import fi.matiaspaavilainen.masuiteteleports.commands.TeleportForceCommand;
import fi.matiaspaavilainen.masuiteteleports.commands.TeleportRequestCommand;
import fi.matiaspaavilainen.masuiteteleports.commands.SpawnCommand;
import fi.matiaspaavilainen.masuiteteleports.database.Database;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.event.EventHandler;

import java.io.*;
import java.util.HashMap;

public class MaSuiteTeleports extends Plugin implements Listener {

    private Configuration config = new Configuration();
    public static Database db = new Database();
    private HashMap<String, Location> lastLocations = new HashMap<>();

    @Override
    public void onEnable() {
        super.onEnable();

        getProxy().getPluginManager().registerListener(this, this);

        // Table creation
        db.connect();
        db.createTable("spawns",
                "(id INT(10) unsigned NOT NULL PRIMARY KEY AUTO_INCREMENT, server VARCHAR(100) UNIQUE NOT NULL, world VARCHAR(100) NOT NULL, x DOUBLE, y DOUBLE, z DOUBLE, yaw FLOAT, pitch FLOAT) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;");

        // Generate configs
        config.create(this, "teleports", "messages.yml");
        config.create(this, "teleports", "settings.yml");
        config.create(this, "teleports", "syntax.yml");
        config.create(this, "teleports", "buttons.yml");

        new Updator().checkVersion(this.getDescription(), "60125");
    }

    public void onDisable() {
        db.hikari.close();
    }

    @EventHandler
    public void onPluginMessage(PluginMessageEvent e) throws IOException {
        if (!e.getTag().equals("BungeeCord")) {
            return;
        }

        DataInputStream in = new DataInputStream(new ByteArrayInputStream(e.getData()));
        String subchannel = in.readUTF();
        if (subchannel.equals("MaSuiteTeleports")) {
            String childchannel = in.readUTF();
            ProxiedPlayer sender = ProxyServer.getInstance().getPlayer(in.readUTF());
            // Spawn
            String spawnType = config.load("teleports", "settings.yml").getString("spawn-type");
            if (spawnType.equalsIgnoreCase("server") || spawnType.equalsIgnoreCase("global")){
                SpawnCommand command = new SpawnCommand(this);
                switch (childchannel) {
                    case "SpawnPlayer":
                        command.spawn(sender);
                        break;
                    case "SetSpawn":
                        String[] loc = in.readUTF().split(":");
                        command.setSpawn(sender, new Location(loc[0], Double.parseDouble(loc[1]), Double.parseDouble(loc[2]), Double.parseDouble(loc[3]), Float.parseFloat(loc[4]), Float.parseFloat(loc[5])));
                        break;
                    case "DelSpawn":
                        command.deleteSpawn(sender);
                        break;
                }
            }

            //Teleportation requests
            TeleportRequestCommand tprequest = new TeleportRequestCommand(this);
            switch (childchannel) {
                case "TeleportRequestTo":
                    tprequest.tpa(sender, in.readUTF());
                    break;
                case "TeleportRequestHere":
                    tprequest.tpahere(sender, in.readUTF());
                    break;
                case "TeleportAccept":
                    tprequest.tpaccept(sender);
                    break;
                case "TeleportDeny":
                    tprequest.tpdeny(sender);
                    break;
            }

            //Teleportation forces
            TeleportForceCommand tpforce = new TeleportForceCommand(this);
            switch (childchannel) {
                case ("TeleportForceTo"):
                    String superchildchannel = in.readUTF();
                    switch (superchildchannel) {
                        case "TeleportSenderToTarget":
                            tpforce.tp(sender, in.readUTF());
                            break;
                        case "TeleportTargetToTarget":
                            tpforce.tp(sender, in.readUTF(), in.readUTF());
                            break;
                        case "TeleportToXYZ":
                            String tname = in.readUTF();
                            Double tx = in.readDouble();
                            Double ty = in.readDouble();
                            Double tz = in.readDouble();
                            tpforce.tp(sender, tname, tx, ty, tz);
                            lastLocations.put(in.readUTF(), new Location("world", tx, ty, tz));
                            break;
                        case "TeleportToCoordinates":
                            String tcname = in.readUTF();
                            String world = in.readUTF();
                            Double tcx = in.readDouble();
                            Double tcy = in.readDouble();
                            Double tcz = in.readDouble();
                            tpforce.tp(sender, tcname, new Location(world, tcx, tcy, tcz));
                            lastLocations.put(in.readUTF(), new Location(world, tcx, tcy, tcz));
                            break;
                    }
                    break;
                case "TeleportForceHere":
                    tpforce.tphere(sender, in.readUTF());
                    break;
                case "TeleportForceAll":
                    tpforce.tpall(sender);
                    break;
            }

            // Back
            if(childchannel.equals("Back")){

            }


        }

    }
}
