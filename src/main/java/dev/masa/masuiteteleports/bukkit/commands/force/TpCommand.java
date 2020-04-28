package dev.masa.masuiteteleports.bukkit.commands.force;

import dev.masa.masuitecore.acf.BaseCommand;
import dev.masa.masuitecore.acf.annotation.*;
import dev.masa.masuitecore.core.channels.BukkitPluginChannel;
import dev.masa.masuiteteleports.bukkit.MaSuiteTeleports;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TpCommand extends BaseCommand {

    private MaSuiteTeleports plugin;

    public TpCommand(MaSuiteTeleports p) {
        plugin = p;
    }

    @CommandAlias("tp")
    @CommandPermission("masuiteteleports.teleport.force.player")
    @CommandCompletion("@masuite_players")
    @Description("Teleport to a player")
    public void teleportSenderToPlayer(Player player, String target) {
        new BukkitPluginChannel(plugin, player, "MaSuiteTeleports", "TeleportForceTo", player.getName(), "TeleportSenderToTarget", target, player.hasPermission("masuiteteleports.teleport.toggle.bypass")).send();
    }

    @CommandAlias("tp")
    @CommandPermission("masuiteteleports.teleport.force.player")
    @CommandCompletion("@masuite_players|@worlds")
    @Description("Teleport a player to an other player")
    public void teleportPlayerToPlayer(CommandSender sender, String fromPlayer, String toPlayer) {
        Player player = this.getPlayerFromSender(sender);
        new BukkitPluginChannel(plugin, player, "MaSuiteTeleports", "TeleportForceTo", player.getName(), "TeleportTargetToTarget", fromPlayer, toPlayer).send();
    }


    @CommandAlias("tp")
    @CommandPermission("masuiteteleports.teleport.force.coordinates")
    public void teleportSenderToXYZ(Player player, double x, double y, double z) {
        new BukkitPluginChannel(plugin, player, "MaSuiteTeleports", "TeleportForceTo", player.getName(), "TeleportToXYZ", player.getName(), x, y, z).send();
    }

    @CommandAlias("tp")
    @CommandPermission("masuiteteleports.teleport.force.coordinates")
    @CommandCompletion("@worlds")
    public void teleportSenderToCoordinates(Player player, String world, double x, double y, double z) {
        new BukkitPluginChannel(plugin, player, "MaSuiteTeleports", "TeleportForceTo", player.getName(), "TeleportToCoordinates", player.getName(), world, x, y, z).send();
    }

    @CommandAlias("tp")
    @CommandPermission("masuiteteleports.teleport.force.coordinates")
    @CommandCompletion("@masuite_players @masuite_players|@worlds")
    public void teleportPlayerToCoordinates(CommandSender sender, String targetPlayer, String world, double x, double y, double z) {
        Player player = this.getPlayerFromSender(sender);
        new BukkitPluginChannel(plugin, player, "MaSuiteTeleports", "TeleportForceTo", player.getName(), "TeleportToCoordinates", targetPlayer, world, x, y, z).send();
    }

    /**
     * Get the player from command sender or a random player if the sender is console
     * @param sender sender of the command
     * @return returns player
     */
    private Player getPlayerFromSender(CommandSender sender) {
        Player player;
        if(sender instanceof Player) {
            player = (Player) sender;
        } else {
            player = plugin.getServer().getOnlinePlayers().stream().findFirst().orElse(null);
        }

        return player;
    }
}
