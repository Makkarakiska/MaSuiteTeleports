package fi.matiaspaavilainen.masuiteteleports;

import fi.matiaspaavilainen.masuitecore.MaSuiteCore;
import fi.matiaspaavilainen.masuitecore.chat.Formator;
import fi.matiaspaavilainen.masuitecore.config.Configuration;
import fi.matiaspaavilainen.masuiteteleports.commands.force.All;
import fi.matiaspaavilainen.masuiteteleports.commands.force.Teleport;
import fi.matiaspaavilainen.masuiteteleports.commands.requests.Accept;
import fi.matiaspaavilainen.masuiteteleports.commands.requests.Deny;
import fi.matiaspaavilainen.masuiteteleports.commands.requests.Here;
import fi.matiaspaavilainen.masuiteteleports.commands.requests.To;
import fi.matiaspaavilainen.masuiteteleports.commands.spawns.Delete;
import fi.matiaspaavilainen.masuiteteleports.commands.spawns.Set;
import fi.matiaspaavilainen.masuiteteleports.commands.spawns.Spawn;
import fi.matiaspaavilainen.masuiteteleports.managers.requests.Request;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.event.EventHandler;

import java.io.*;

public class MaSuiteTeleports extends Plugin implements Listener {

    Configuration config = new Configuration();

    @Override
    public void onEnable() {
        super.onEnable();

        getProxy().getPluginManager().registerListener(this, this);
        //Teleportation
        getProxy().getPluginManager().registerCommand(this, new Teleport(this));
        getProxy().getPluginManager().registerCommand(this, new To(this));
        getProxy().getPluginManager().registerCommand(this, new Here(this));
        getProxy().getPluginManager().registerCommand(this, new Accept(this));
        getProxy().getPluginManager().registerCommand(this, new Deny(this));
        getProxy().getPluginManager().registerListener(this, new Request(this));
        getProxy().getPluginManager().registerCommand(this, new All(this));
        getProxy().getPluginManager().registerCommand(this, new fi.matiaspaavilainen.masuiteteleports.commands.force.Here(this));
        getProxy().getPluginManager().registerCommand(this, new fi.matiaspaavilainen.masuiteteleports.commands.force.Teleport(this));


        // Spawn
        getProxy().getPluginManager().registerCommand(this, new Spawn());
        getProxy().getPluginManager().registerCommand(this, new Set());
        getProxy().getPluginManager().registerCommand(this, new Delete());

        // Table creation
        MaSuiteCore.db.createTable("spawns",
                "(id INT(10) unsigned NOT NULL PRIMARY KEY AUTO_INCREMENT, server VARCHAR(100) UNIQUE NOT NULL, world VARCHAR(100) NOT NULL, x DOUBLE, y DOUBLE, z DOUBLE, yaw FLOAT, pitch FLOAT) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;");

        // Generate configs
        config.create(this, "teleports", "messages.yml");
        config.create(this, "teleports", "settings.yml");
        config.create(this, "teleports", "syntax.yml");
    }

    @EventHandler
    public void onPluginMessage(PluginMessageEvent e) throws IOException {
        if(!e.getTag().equals("BungeeCord")){
            return;
        }

        DataInputStream in = new DataInputStream(new ByteArrayInputStream(e.getData()));
        String subchannel = in.readUTF();
        if(subchannel.equals("SpawnPlayer")){
            ProxiedPlayer p = ProxyServer.getInstance().getPlayer(in.readUTF());
            if(p == null){
                return;
            }
            fi.matiaspaavilainen.masuiteteleports.managers.Spawn spawn = new fi.matiaspaavilainen.masuiteteleports.managers.Spawn();
            spawn = spawn.find(p.getServer().getInfo().getName());
            if(spawn.getServer() == null){
                new Formator().sendMessage(p, config.load("teleports", "messages.yml").getString("spawn.not-found"));
                return;
            }
            try{
                ByteArrayOutputStream b = new ByteArrayOutputStream();
                DataOutputStream out = new DataOutputStream(b);
                out.writeUTF("Teleport");
                out.writeUTF("SpawnPlayer");
                out.writeUTF(String.valueOf(p.getUniqueId()));
                out.writeUTF(spawn.getLocation().getWorld());
                out.writeDouble(spawn.getLocation().getX());
                out.writeDouble(spawn.getLocation().getY());
                out.writeDouble(spawn.getLocation().getZ());
                out.writeFloat(spawn.getLocation().getYaw());
                out.writeFloat(spawn.getLocation().getPitch());
                p.getServer().sendData("BungeeCord", b.toByteArray());
            } catch (IOException ex){
                System.out.println(ex.getMessage());
            }
        }
    }
}
