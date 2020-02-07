package dev.masa.masuiteteleports.bungee.commands;

import dev.masa.masuitecore.core.objects.Location;
import dev.masa.masuiteteleports.bungee.MaSuiteTeleports;
import dev.masa.masuiteteleports.core.models.Spawn;
import dev.masa.masuiteteleports.core.objects.SpawnType;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.ArrayList;

public class SpawnCommand {

    private MaSuiteTeleports plugin;

    public SpawnCommand(MaSuiteTeleports plugin) {
        this.plugin = plugin;
    }

    /**
     * Spawn player
     *
     * @param player player to spawn
     * @param type   default (0) or first (1)
     */
    public void spawn(ProxiedPlayer player, int type) {
        if (player == null) {
            return;
        }
        if (plugin.spawnService.teleportToSpawn(player, SpawnType.getType(type))) {
            if (SpawnType.getType(type) == SpawnType.DEFAULT) {
                plugin.formator.sendMessage(player, plugin.config.load("teleports", "messages.yml").getString("spawn.teleported"));
            }
        }
    }

    /**
     * Set spawn
     *
     * @param player executor
     * @param loc    spawn location
     * @param type   default (0) or first (1)
     */
    public void setSpawn(ProxiedPlayer player, Location loc, int type) {
        if (player == null) {
            return;
        }
        loc.setServer(player.getServer().getInfo().getName());
        Spawn spawn = new Spawn(loc, SpawnType.getType(type));

        // Check if spawn exists in specific server with specific type
        plugin.spawnService.spawns.computeIfAbsent(player.getServer().getInfo().getName(), k -> new ArrayList<>());
        Spawn cachedSpawn = plugin.spawnService.spawns.get(player.getServer().getInfo().getName()).stream().filter(filteredSpawn -> filteredSpawn.getType() == spawn.getType()).findFirst().orElse(null);
        if (cachedSpawn != null) {
            cachedSpawn.setLocation(spawn.getLocation());
            plugin.spawnService.updateSpawn(cachedSpawn);
        } else {
            plugin.spawnService.createSpawn(spawn);
        }

        plugin.formator.sendMessage(player, plugin.config.load("teleports", "messages.yml").getString("spawn.set"));
    }

    /**
     * Deletes specific spawn
     *
     * @param player executor
     * @param type   default (0) or first (1)
     */
    public void deleteSpawn(ProxiedPlayer player, int type) {
        if (player == null) {
            return;
        }
        Spawn spawn = plugin.spawnService.getSpawn(player.getServer().getInfo().getName(), SpawnType.getType(type));
        if (spawn != null) {
            plugin.spawnService.removeSpawn(spawn);
            plugin.formator.sendMessage(player, plugin.config.load("teleports", "messages.yml").getString("spawn.deleted"));

        } else {
            plugin.formator.sendMessage(player, plugin.config.load("teleports", "messages.yml").getString("spawn.not-found"));
        }
    }
}
