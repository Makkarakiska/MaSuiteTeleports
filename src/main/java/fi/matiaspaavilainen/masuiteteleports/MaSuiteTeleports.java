package fi.matiaspaavilainen.masuiteteleports;

import fi.matiaspaavilainen.masuitecore.MaSuiteCore;
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
import net.md_5.bungee.api.plugin.Plugin;

public class MaSuiteTeleports extends Plugin {

    @Override
    public void onEnable() {
        super.onEnable();

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
        new Configuration().create(this, "teleports", "messages.yml");
        new Configuration().create(this, "teleports", "settings.yml");
        new Configuration().create(this, "teleports", "syntax.yml");
    }
}
