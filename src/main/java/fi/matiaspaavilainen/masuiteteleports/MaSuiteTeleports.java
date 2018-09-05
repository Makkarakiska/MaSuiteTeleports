package fi.matiaspaavilainen.masuiteteleports;

import fi.matiaspaavilainen.masuitecore.config.Configuration;
import fi.matiaspaavilainen.masuiteteleports.commands.force.All;
import fi.matiaspaavilainen.masuiteteleports.commands.force.Teleport;
import fi.matiaspaavilainen.masuiteteleports.commands.requests.Accept;
import fi.matiaspaavilainen.masuiteteleports.commands.requests.Deny;
import fi.matiaspaavilainen.masuiteteleports.commands.requests.Here;
import fi.matiaspaavilainen.masuiteteleports.commands.requests.To;
import fi.matiaspaavilainen.masuiteteleports.managers.requests.Request;
import net.md_5.bungee.api.plugin.Plugin;

public class MaSuiteTeleports extends Plugin {

    @Override
    public void onEnable() {
        super.onEnable();
        getProxy().getPluginManager().registerCommand(this, new Teleport(this));
        getProxy().getPluginManager().registerCommand(this, new To(this));
        getProxy().getPluginManager().registerCommand(this, new Here(this));
        getProxy().getPluginManager().registerCommand(this, new Accept(this));
        getProxy().getPluginManager().registerCommand(this, new Deny(this));
        getProxy().getPluginManager().registerListener(this, new Request(this));
        getProxy().getPluginManager().registerCommand(this, new All(this));
        getProxy().getPluginManager().registerCommand(this, new fi.matiaspaavilainen.masuiteteleports.commands.force.Here(this));
        getProxy().getPluginManager().registerCommand(this, new fi.matiaspaavilainen.masuiteteleports.commands.force.Teleport(this));
        new Configuration().create(this, "teleports", "messages.yml");
        new Configuration().create(this, "teleports", "settings.yml");
        new Configuration().create(this, "teleports", "syntax.yml");
    }
}
