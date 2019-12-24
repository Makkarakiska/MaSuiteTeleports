package fi.matiaspaavilainen.masuiteteleports.bukkit.commands.force;

import fi.matiaspaavilainen.masuitecore.acf.BaseCommand;
import fi.matiaspaavilainen.masuitecore.acf.annotation.CommandAlias;
import fi.matiaspaavilainen.masuitecore.acf.annotation.CommandCompletion;
import fi.matiaspaavilainen.masuitecore.acf.annotation.CommandPermission;
import fi.matiaspaavilainen.masuitecore.acf.annotation.Description;
import fi.matiaspaavilainen.masuitecore.core.channels.BukkitPluginChannel;
import fi.matiaspaavilainen.masuiteteleports.bukkit.MaSuiteTeleports;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandAlias("tphere")
public class TpHereCommand extends BaseCommand {

    private MaSuiteTeleports plugin;

    public TpHereCommand(MaSuiteTeleports p) {
        plugin = p;
    }

    @CommandPermission("masuiteteleports.teleport.force.here")
    @CommandCompletion("@masuite_players")
    @Description("Teleports target to sender")
    @CommandAlias("teleporthere")
    public void teleportCommand(Player player, String target) {
        new BukkitPluginChannel(plugin, player, new Object[]{"MaSuiteTeleports", "TeleportForceHere", player.getName(), target, player.hasPermission("masuiteteleports.teleport.toggle.bypass")}).send();
    }
}
