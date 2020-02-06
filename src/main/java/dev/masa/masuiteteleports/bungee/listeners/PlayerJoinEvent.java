package dev.masa.masuiteteleports.bungee.listeners;

import dev.masa.masuitecore.bungee.Utils;
import dev.masa.masuitecore.core.configuration.BungeeConfiguration;
import dev.masa.masuiteteleports.bungee.MaSuiteTeleports;
import dev.masa.masuiteteleports.core.objects.SpawnType;
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
                    plugin.spawnService.teleportToSpawn(e.getPlayer(), SpawnType.FIRST);
                }
            }, plugin.config.load(null, "config.yml").getInt("teleport-delay"), TimeUnit.MILLISECONDS);
            return;
        }

        if (config.load("teleports", "settings.yml").getBoolean("spawn-on-join")) {
            plugin.getProxy().getScheduler().schedule(plugin, () -> {
                if (utils.isOnline(e.getPlayer())) {
                    plugin.spawnService.teleportToSpawn(e.getPlayer(), SpawnType.DEFAULT);
                }
            }, plugin.config.load(null, "config.yml").getInt("teleportation-delay"), TimeUnit.MILLISECONDS);
        }
    }


}
