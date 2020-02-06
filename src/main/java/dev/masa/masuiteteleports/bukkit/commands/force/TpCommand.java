package dev.masa.masuiteteleports.bukkit.commands.force;

import dev.masa.masuitecore.acf.BaseCommand;
import dev.masa.masuitecore.acf.annotation.CommandAlias;
import dev.masa.masuitecore.acf.annotation.CommandCompletion;
import dev.masa.masuitecore.acf.annotation.CommandPermission;
import dev.masa.masuitecore.acf.annotation.Default;
import dev.masa.masuitecore.core.channels.BukkitPluginChannel;
import dev.masa.masuiteteleports.bukkit.MaSuiteTeleports;
import org.bukkit.entity.Player;


@CommandAlias("tp")
public class TpCommand extends BaseCommand {

    private MaSuiteTeleports plugin;

    public TpCommand(MaSuiteTeleports p) {
        plugin = p;
    }

    // TODO: Add console commands
    @Default
    @CommandPermission("masuiteteleports.teleport.force.player")
    @CommandCompletion("@masuite_players")
    public void teleportSenderToPlayer(Player player, String target) {
        new BukkitPluginChannel(plugin, player, "MaSuiteTeleports", "TeleportForceTo", player.getName(), "TeleportSenderToTarget", target, player.hasPermission("masuiteteleports.teleport.toggle.bypass")).send();
    }

    @Default
    @CommandPermission("masuiteteleports.teleport.force.player")
    @CommandCompletion("@masuite_players")
    public void teleportPlayerToPlayer(Player player, String fromPlayer, String toPlayer) {
        new BukkitPluginChannel(plugin, player, "MaSuiteTeleports", "TeleportForceTo", player.getName(), "TeleportTargetToTarget", fromPlayer, toPlayer).send();
    }

    @Default
    @CommandPermission("masuiteteleports.teleport.force.coordinates")
    public void teleportSenderToXYZ(Player player, double x, double y, double z) {
        new BukkitPluginChannel(plugin, player, "MaSuiteTeleports", "TeleportForceTo", player.getName(), "TeleportToXYZ", player.getName(), x, y, z).send();
    }

    @Default
    @CommandPermission("masuiteteleports.teleport.force.coordinates")
    @CommandCompletion("@worlds")
    public void teleportSenderToCoordinates(Player player, String world, double x, double y, double z) {
        new BukkitPluginChannel(plugin, player, "MaSuiteTeleports", "TeleportForceTo", player.getName(), "TeleportToCoordinates", player.getName(), world, x, y, z).send();
    }

    @Default
    @CommandPermission("masuiteteleports.teleport.force.coordinates")
    @CommandCompletion("@masuite_players @worlds")
    public void teleportPlayerToCoordinates(Player player, String targetPlayer, String world, double x, double y, double z) {
        new BukkitPluginChannel(plugin, player, "MaSuiteTeleports", "TeleportForceTo", player.getName(), "TeleportToCoordinates", targetPlayer, world, x, y, z).send();
    }
}
