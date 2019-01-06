package fi.matiaspaavilainen.masuiteteleports.bungee;

import fi.matiaspaavilainen.masuitecore.bungee.chat.Formator;
import fi.matiaspaavilainen.masuitecore.core.Updator;
import fi.matiaspaavilainen.masuitecore.core.configuration.BungeeConfiguration;
import fi.matiaspaavilainen.masuitecore.core.database.ConnectionManager;
import fi.matiaspaavilainen.masuitecore.core.objects.Location;
import fi.matiaspaavilainen.masuiteteleports.bungee.commands.SpawnCommand;
import fi.matiaspaavilainen.masuiteteleports.bungee.commands.TeleportForceCommand;
import fi.matiaspaavilainen.masuiteteleports.bungee.commands.TeleportRequestCommand;
import fi.matiaspaavilainen.masuiteteleports.bungee.listeners.PlayerJoinEvent;
import fi.matiaspaavilainen.masuiteteleports.bungee.listeners.PlayerQuitEvent;
import fi.matiaspaavilainen.masuiteteleports.bungee.managers.Teleport;
import fi.matiaspaavilainen.masuiteteleports.managers.PositionListener;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.event.EventHandler;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class MaSuiteTeleports extends Plugin implements Listener {

    private BungeeConfiguration config = new BungeeConfiguration();
    private Formator formator = new Formator();
    public static HashMap<UUID, Long> cooldowns = new HashMap<>();
    public PositionListener positions = new PositionListener(this);

    @Override
    public void onEnable() {

        // Generate configs
        config.create(this, "teleports", "messages.yml");
        config.create(this, "teleports", "settings.yml");
        config.create(this, "teleports", "buttons.yml");

        // Register listeners
        getProxy().getPluginManager().registerListener(this, this);
        getProxy().getPluginManager().registerListener(this, new PlayerJoinEvent(this));
        getProxy().getPluginManager().registerListener(this, new PlayerQuitEvent());

        // Table creation
        ConnectionManager.db.createTable("spawns",
                "(id INT(10) unsigned NOT NULL PRIMARY KEY AUTO_INCREMENT, server VARCHAR(100) NOT NULL, world VARCHAR(100) NOT NULL, x DOUBLE, y DOUBLE, z DOUBLE, yaw FLOAT, pitch FLOAT, type TINYINT(1) NULL DEFAULT 0) " +
                        "ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;");

        // Check updates
        new Updator(new String[]{getDescription().getVersion(), getDescription().getName(), "60125"}).checkUpdates();
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
            if (childchannel.equals("GetLocation")) {
                if (sender != null) {
                    String[] ploc = in.readUTF().split(":");
                    Location loc = new Location(sender.getServer().getInfo(), ploc[0], Double.parseDouble(ploc[1]), Double.parseDouble(ploc[2]), Double.parseDouble(ploc[3]), Float.parseFloat(ploc[4]), Float.parseFloat(ploc[5]));
                    String s = in.readUTF();
                    ServerInfo server = null;
                    if (s.equals("DETECTSERVER")) {
                        server = sender.getServer().getInfo();
                    } else {
                        server = getProxy().getServerInfo(s);
                    }

                    positions.locationReceived(sender, loc);
                    return;
                }
            }
            // Spawn
            String spawnType = config.load("teleports", "settings.yml").getString("spawn-type");
            if (spawnType.equalsIgnoreCase("server") || spawnType.equalsIgnoreCase("global")) {
                SpawnCommand command = new SpawnCommand(this);
                switch (childchannel) {
                    case "SpawnPlayer":
                        command.spawn(sender, 0);
                        break;
                    case "FirstSpawnPlayer":
                        command.spawn(sender, 1);
                        break;
                    case "SetSpawn":
                        String[] loc = in.readUTF().split(":");
                        command.setSpawn(sender, new Location(loc[0], Double.parseDouble(loc[1]), Double.parseDouble(loc[2]), Double.parseDouble(loc[3]), Float.parseFloat(loc[4]), Float.parseFloat(loc[5])), in.readUTF().equals("default") ? 0 : 1);
                        break;
                    case "DelSpawn":
                        command.deleteSpawn(sender, in.readUTF().equals("default") ? 0 : 1);
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
                case "TeleportLock":
                    String c = in.readUTF();
                    if (c.equals("Enable")) {
                        tprequest.tplock(sender, in.readBoolean());
                        break;
                    }
                    if (c.equals("Disable")) {
                        if (Teleport.lock.containsKey(sender.getUniqueId())) {
                            Teleport.lock.remove(sender.getUniqueId());
                            formator.sendMessage(sender, config.load("teleports", "messages.yml").getString("tpalock.disabled"));
                        } else {
                            formator.sendMessage(sender, config.load("teleports", "messages.yml").getString("tpalock.not-locked"));
                        }

                    }
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
                            tpforce.tp(sender, in.readUTF(), in.readDouble(), in.readDouble(), in.readDouble());
                            break;
                        case "TeleportToCoordinates":
                            tpforce.tp(sender, in.readUTF(), new Location(in.readUTF(), in.readDouble(), in.readDouble(), in.readDouble()));
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
            if (childchannel.equals("Back")) {
                if (cooldowns.containsKey(sender.getUniqueId())) {
                    if (System.currentTimeMillis() - cooldowns.get(sender.getUniqueId()) <
                            config.load("teleports", "settings.yml").getInt("cooldown") * 1000) {
                        formator.sendMessage(sender, config.load("teleports", "messages.yml")
                                .getString("in-cooldown")
                                .replace("%time%", String.valueOf(config.load("teleports", "settings.yml").getInt("cooldown"))
                                ));
                        cooldowns.remove(sender.getUniqueId());
                        return;
                    }
                }
                if (positions.positions.containsKey(sender.getUniqueId())) {
                    if (!positions.positions.get(sender.getUniqueId()).getServer().equals(sender.getServer().getInfo())) {
                        sender.connect(positions.positions.get(sender.getUniqueId()).getServer());
                        ProxyServer.getInstance().getScheduler().schedule(this, () -> tpforce.tp(sender, sender.getName(), positions.positions.get(sender.getUniqueId())), 500, TimeUnit.MILLISECONDS);
                    } else {
                        tpforce.tp(sender, sender.getName(), positions.positions.get(sender.getUniqueId()));
                    }
                    formator.sendMessage(sender, config.load("teleports", "messages.yml").getString("back.last-loc"));
                    cooldowns.put(sender.getUniqueId(), System.currentTimeMillis());
                } else {
                    formator.sendMessage(sender, config.load("teleports", "messages.yml").getString("back.no-loc"));
                }
            }
        }
    }
}