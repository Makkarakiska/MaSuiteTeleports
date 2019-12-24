package fi.matiaspaavilainen.masuiteteleports.bukkit.commands.force;

import fi.matiaspaavilainen.masuitecore.acf.BaseCommand;
import fi.matiaspaavilainen.masuitecore.acf.annotation.CommandAlias;
import fi.matiaspaavilainen.masuitecore.acf.annotation.CommandCompletion;
import fi.matiaspaavilainen.masuitecore.acf.annotation.CommandPermission;
import fi.matiaspaavilainen.masuitecore.core.channels.BukkitPluginChannel;
import fi.matiaspaavilainen.masuiteteleports.bukkit.MaSuiteTeleports;
import org.bukkit.entity.Player;

@CommandAlias("tp")
public class TpCommand extends BaseCommand {

    private MaSuiteTeleports plugin;

    public TpCommand(MaSuiteTeleports p) {
        plugin = p;
    }

    // TODO: Add console commands

    @CommandPermission("masuiteteleports.teleport.force.player")
    @CommandCompletion("@masuite_players")
    public void teleportSenderToPlayer(Player player, String target) {
        new BukkitPluginChannel(plugin, player, "MaSuiteTeleports", "TeleportForceTo", player.getName(), "TeleportSenderToTarget", target, player.hasPermission("masuiteteleports.teleport.toggle.bypass")).send();
    }

    @CommandPermission("masuiteteleports.teleport.force.player")
    @CommandCompletion("@masuite_players")
    public void teleportPlayerToPlayer(Player player, String fromPlayer, String toPlayer) {
        new BukkitPluginChannel(plugin, player, "MaSuiteTeleports", "TeleportForceTo", player.getName(), "TeleportTargetToTarget", fromPlayer, toPlayer).send();
    }

    // TODO: Add tilde (~) support
    @CommandPermission("masuiteteleports.teleport.force.coordinates")
    public void teleportSenderToXYZ(Player player, double x, double y, double z) {
        new BukkitPluginChannel(plugin, player, "MaSuiteTeleports", "TeleportForceTo", player.getName(), "TeleportToXYZ", player.getName(), x, y, z).send();
    }

    // TODO: Add tilde (~) support
    @CommandPermission("masuiteteleports.teleport.force.coordinates")
    @CommandCompletion("@worlds")
    public void teleportSenderToCoordinates(Player player, String world, double x, double y, double z) {
        new BukkitPluginChannel(plugin, player, "MaSuiteTeleports", "TeleportForceTo", player.getName(), "TeleportToCoordinates", player.getName(), world, x, y, z).send();
    }

    // TODO: Add tilde (~) support
    @CommandPermission("masuiteteleports.teleport.force.coordinates")
    @CommandCompletion("@masuite_players")
    public void teleportPlayerToXYZ(Player player, String targetPlayer, double x, double y, double z) {
        new BukkitPluginChannel(plugin, player, "MaSuiteTeleports", "TeleportForceTo", player.getName(), "TeleportToXYZ", targetPlayer, x, y, z).send();
    }

    // TODO: Add tilde (~) support
    @CommandPermission("masuiteteleports.teleport.force.coordinates")
    @CommandCompletion("@masuite_players @worlds")
    public void teleportPlayerToCoordinates(Player player, String targetPlayer, String world, double x, double y, double z) {
        new BukkitPluginChannel(plugin, player, "MaSuiteTeleports", "TeleportForceTo", player.getName(), "TeleportToCoordinates", targetPlayer, world, x, y, z).send();
    }
}
