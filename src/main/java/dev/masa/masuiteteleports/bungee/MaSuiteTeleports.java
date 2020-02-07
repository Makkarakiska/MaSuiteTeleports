package dev.masa.masuiteteleports.bungee;

import dev.masa.masuitecore.bungee.chat.Formator;
import dev.masa.masuitecore.core.Updator;
import dev.masa.masuitecore.core.api.MaSuiteCoreAPI;
import dev.masa.masuitecore.core.channels.BungeePluginChannel;
import dev.masa.masuitecore.core.configuration.BungeeConfiguration;
import dev.masa.masuiteteleports.bungee.listeners.PlayerJoinEvent;
import dev.masa.masuiteteleports.bungee.listeners.PlayerQuitEvent;
import dev.masa.masuiteteleports.bungee.listeners.TeleportMessageListener;
import dev.masa.masuiteteleports.core.services.PlayerPositionService;
import dev.masa.masuiteteleports.core.services.PlayerTeleportService;
import dev.masa.masuiteteleports.core.services.SpawnService;
import dev.masa.masuiteteleports.core.services.TeleportRequestService;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;

public class MaSuiteTeleports extends Plugin implements Listener {

    public BungeeConfiguration config = new BungeeConfiguration();
    public Formator formator = new Formator();

    public PlayerPositionService playerPositionService;
    public PlayerTeleportService playerTeleportService;
    public TeleportRequestService teleportRequestService;
    public SpawnService spawnService;

    public MaSuiteCoreAPI api = new MaSuiteCoreAPI();

    public boolean warmupEnabled = false;

    @Override
    public void onEnable() {

        // Generate configs
        config.create(this, "teleports", "messages.yml");
        config.create(this, "teleports", "settings.yml");
        config.create(this, "teleports", "buttons.yml");

        // Register services
        playerPositionService = new PlayerPositionService(this);
        playerTeleportService = new PlayerTeleportService(this);
        teleportRequestService = new TeleportRequestService(this);
        spawnService = new SpawnService(this);

        this.spawnService.initializeSpawns();

        // Register listeners
        getProxy().getPluginManager().registerListener(this, new TeleportMessageListener(this));
        getProxy().getPluginManager().registerListener(this, new PlayerJoinEvent(this));
        //getProxy().getPluginManager().registerListener(this, new PlayerServerConnectEvent(this));
        getProxy().getPluginManager().registerListener(this, new PlayerQuitEvent(this));

        // Check updates
        new Updator(getDescription().getVersion(), getDescription().getName(), "60125").checkUpdates();

        // Add default values to configs
        config.addDefault("teleports/messages.yml", "receiver.teleported", "&7You have been teleported to &b%player%&7!");
        config.addDefault("teleports/messages.yml", "sender.teleported", "&9%player%&7 has been teleported to you!");
        config.addDefault("teleports/messages.yml", "tptoggle.on", "&cYou are now denying force teleportations!");
        config.addDefault("teleports/messages.yml", "tptoggle.off", "&aYou are now allowing force teleportations!");
        config.addDefault("teleports/messages.yml", "tptoggle.disabled", "&c%player has disabled force teleportations!");
        config.addDefault("teleports/settings.yml", "teleport-delay", 750);
    }

    public void applyWarmup(ProxiedPlayer player) {
        if (warmupEnabled) {
            new BungeePluginChannel(this, player.getServer().getInfo(), "MaSuiteWarps", "ApplyWarmup", player.getUniqueId().toString()).send();
        }
    }
}