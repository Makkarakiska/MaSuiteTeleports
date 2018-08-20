package fi.matiaspaavilainen.masuiteteleports;

import fi.matiaspaavilainen.masuitecore.config.Configuration;
import fi.matiaspaavilainen.masuiteteleports.commands.Teleport;
import fi.matiaspaavilainen.masuiteteleports.commands.requests.Accept;
import fi.matiaspaavilainen.masuiteteleports.commands.requests.Deny;
import fi.matiaspaavilainen.masuiteteleports.commands.requests.Request;
import net.md_5.bungee.api.plugin.Plugin;

public class MaSuiteTeleports extends Plugin {

    @Override
    public void onEnable() {
        super.onEnable();
        getProxy().getPluginManager().registerCommand(this, new Teleport());
        getProxy().getPluginManager().registerCommand(this, new Request(this));
        getProxy().getPluginManager().registerCommand(this, new Accept(this));
        getProxy().getPluginManager().registerCommand(this, new Deny(this));
        getProxy().getPluginManager().registerListener(this, new fi.matiaspaavilainen.masuiteteleports.managers.requests.Request(this));
        new Configuration().create(this, "teleports", "messages.yml");
        new Configuration().create(this, "teleports", "settings.yml");
    }
}
