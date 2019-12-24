package fi.matiaspaavilainen.masuiteteleports.bungee.listeners;

import fi.matiaspaavilainen.masuitecore.bungee.Utils;
import fi.matiaspaavilainen.masuitecore.core.configuration.BungeeConfiguration;
import fi.matiaspaavilainen.masuiteteleports.bungee.MaSuiteTeleports;
import fi.matiaspaavilainen.masuiteteleports.bungee.commands.SpawnCommand;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.concurrent.TimeUnit;

public class PlayerJoinEvent implements Listener {

    private MaSuiteTeleports plugin;
    private BungeeConfiguration config = new BungeeConfiguration();
    private Utils utils = new Utils();

    public PlayerJoinEvent(MaSuiteTeleports plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PostLoginEvent e) {
        if (config.load("teleports", "settings.yml").getBoolean("enable-first-spawn") && plugin.api.getPlayerService().getPlayer(e.getPlayer().getUniqueId()) == null) {
            plugin.getProxy().getScheduler().schedule(plugin, () -> {
                if (utils.isOnline(e.getPlayer())) {
                    new SpawnCommand(plugin).spawn(e.getPlayer(), 1);
                }
            }, plugin.config.load("teleports", "settings.yml").getInt("teleport-delay"), TimeUnit.MILLISECONDS);
            return;
        }

        if (config.load("teleports", "settings.yml").getBoolean("spawn-on-join")) {
            plugin.getProxy().getScheduler().schedule(plugin, () -> {
                if (utils.isOnline(e.getPlayer())) {
                    new SpawnCommand(plugin).spawn(e.getPlayer(), 0);
                }
            }, plugin.config.load("teleports", "settings.yml").getInt("teleport-delay"), TimeUnit.MILLISECONDS);
        }
    }


}
