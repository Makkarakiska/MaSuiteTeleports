package dev.masa.masuiteteleports.bungee.listeners;

import dev.masa.masuitecore.bungee.Utils;
import dev.masa.masuitecore.bungee.events.MaSuitePlayerCreationEvent;
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

    private boolean firstSpawnEnabled;
    private int teleportationDelay;

    public PlayerJoinEvent(MaSuiteTeleports plugin) {
        this.plugin = plugin;
        this.firstSpawnEnabled = config.load("teleports", "settings.yml").getBoolean("enable-first-spawn");
        this.teleportationDelay = plugin.config.load(null, "config.yml").getInt("teleportation-delay");
    }

    @EventHandler
    public void onJoin(PostLoginEvent e) {
        if (config.load("teleports", "settings.yml").getBoolean("spawn-on-join")) {
            plugin.getProxy().getScheduler().schedule(plugin, () -> {
                if (utils.isOnline(e.getPlayer())) {
                    plugin.getSpawnService().teleportToSpawn(e.getPlayer(), SpawnType.DEFAULT);
                }
            }, teleportationDelay, TimeUnit.MILLISECONDS);
        }
    }

    @EventHandler
    public void onPlayerCreation(MaSuitePlayerCreationEvent event) {
        if (firstSpawnEnabled) {
            plugin.getProxy().getScheduler().schedule(plugin, () -> plugin.getSpawnService().teleportToSpawn(plugin.getProxy().getPlayer(event.getPlayer().getUniqueId()), SpawnType.FIRST), teleportationDelay, TimeUnit.MILLISECONDS);
        }
    }
}
