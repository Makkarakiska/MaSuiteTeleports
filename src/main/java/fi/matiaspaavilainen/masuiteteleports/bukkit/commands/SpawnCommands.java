package fi.matiaspaavilainen.masuiteteleports.bukkit.commands;

import fi.matiaspaavilainen.masuitecore.acf.BaseCommand;
import fi.matiaspaavilainen.masuitecore.acf.annotation.*;
import fi.matiaspaavilainen.masuitecore.core.adapters.BukkitAdapter;
import fi.matiaspaavilainen.masuitecore.core.channels.BukkitPluginChannel;
import fi.matiaspaavilainen.masuiteteleports.bukkit.MaSuiteTeleports;
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
        new BukkitPluginChannel(plugin, player, "MaSuiteTeleports", "SpawnPlayer", target == null ? player.getName() : target).send();
    }

    @CommandAlias("setspawn|spawnset|createspawn")
    @Description("Sets spawn point of the server")
    @CommandPermission("masuiteteleports.spawn.set")
    @CommandCompletion("default|first")
    public void setSpawn(Player player, String spawnType) {
        Location loc = player.getLocation();
        new BukkitPluginChannel(plugin, player, "MaSuiteTeleports", "SetSpawn",
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
