package fi.matiaspaavilainen.masuiteteleports.bukkit.commands.force;

import fi.matiaspaavilainen.masuitecore.acf.BaseCommand;
import fi.matiaspaavilainen.masuitecore.acf.annotation.*;
import fi.matiaspaavilainen.masuitecore.core.channels.BukkitPluginChannel;
import fi.matiaspaavilainen.masuiteteleports.bukkit.MaSuiteTeleports;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandAlias("tpall")
public class TpAllCommand extends BaseCommand {

    private MaSuiteTeleports plugin;

    public TpAllCommand(MaSuiteTeleports p) {
        plugin = p;
    }

    @CommandPermission("masuiteteleports.teleport.force.all")
    @CommandCompletion("@masuite_players")
    @Description("Teleports all of the players to sender")
    @CommandAlias("teleportall")
    public void teleportCommand(Player player, @Optional String targetPlayer) {
        new BukkitPluginChannel(plugin, player, "MaSuiteTeleports", "TeleportForceAll", targetPlayer == null ? player.getName() : targetPlayer).send();
    }
}
