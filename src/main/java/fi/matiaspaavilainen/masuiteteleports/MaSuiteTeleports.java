package fi.matiaspaavilainen.masuiteteleports;

import fi.matiaspaavilainen.masuitecore.config.Configuration;
import fi.matiaspaavilainen.masuiteteleports.commands.Teleport;
import net.md_5.bungee.api.plugin.Plugin;

public class MaSuiteTeleports extends Plugin {

    @Override
    public void onEnable() {
        super.onEnable();
        getProxy().getPluginManager().registerCommand(this, new Teleport());
        new Configuration().create(this, "/teleports/messages.yml");
    }
}
