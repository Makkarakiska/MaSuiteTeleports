package dev.masa.masuiteteleports.bukkit.commands;

import dev.masa.masuitecore.acf.BaseCommand;
import dev.masa.masuitecore.acf.annotation.*;
import dev.masa.masuitecore.core.adapters.BukkitAdapter;
import dev.masa.masuitecore.core.channels.BukkitPluginChannel;
import dev.masa.masuiteteleports.bukkit.MaSuiteTeleports;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class SpawnCommands extends BaseCommand {

    private MaSuiteTeleports plugin;

    public SpawnCommands(MaSuiteTeleports plugin) {
        this.plugin = plugin;
    }

    @CommandAlias("spawn")
    @Description("Teleports to spawn")
    @CommandPermission("masuiteteleports.spawn.teleport")
    @CommandCompletion("@masuite_players")
    @Conditions("cooldown:type=spawns,bypass:masuiteteleports.cooldown.override")
    public void teleportToSpawn(Player player, @Optional @CommandPermission("masuiteteleports.spawn.teleport.other") String target) {
        plugin.api.getWarmupService().applyWarmup(player, "masuiteteleports.warmup.override", "teleports", success -> {
            if (success) {
                new BukkitPluginChannel(plugin, player, "MaSuiteTeleports", "SpawnPlayer", target == null ? player.getName() : target).send();
            }
        });
    }

    @CommandAlias("setspawn|spawnset|createspawn")
    @Description("Sets spawn point of the server")
    @CommandPermission("masuiteteleports.spawn.set")
    @CommandCompletion("default|first")
    public void setSpawn(Player player, String spawnType) {
        Location loc = player.getLocation();
        new BukkitPluginChannel(plugin, player,
                "MaSuiteTeleports",
                "SetSpawn",
                player.getName(),
                BukkitAdapter.adapt(loc).serialize(),
                spawnType.toLowerCase()).send();
    }

    @CommandAlias("delspawn|spawndel|deletespawn")
    @Description("Deletes spawn point of the server")
    @CommandPermission("masuiteteleports.spawn.delete")
    @CommandCompletion("default|first")
    public void deleteSpawn(Player player, String spawnType) {
        new BukkitPluginChannel(plugin, player, "MaSuiteTeleports", "DelSpawn", player.getName(), spawnType.toLowerCase()).send();
    }

}
