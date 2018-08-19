package fi.matiaspaavilainen.masuiteteleports;

import fi.matiaspaavilainen.masuiteteleports.commands.Teleport;
import net.md_5.bungee.api.plugin.Plugin;

public class MaSuiteTeleports extends Plugin {

    @Override
    public void onEnable() {
        super.onEnable();
        getProxy().getPluginManager().registerCommand(this, new Teleport());
    }
}
