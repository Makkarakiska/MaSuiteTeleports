package fi.matiaspaavilainen.masuiteteleports.bukkit.commands;

import fi.matiaspaavilainen.masuitecore.acf.BaseCommand;
import fi.matiaspaavilainen.masuitecore.acf.annotation.*;
import fi.matiaspaavilainen.masuitecore.core.channels.BukkitPluginChannel;
import fi.matiaspaavilainen.masuiteteleports.bukkit.MaSuiteTeleports;
import org.bukkit.entity.Player;

public class TeleportForceCommands extends BaseCommand {

    private MaSuiteTeleports plugin;

    public TeleportForceCommands(MaSuiteTeleports plugin) {
        this.plugin = plugin;
    }

    @CommandAlias("tptoggle")
    @Description("Enable or disable force teleportations")
    @CommandPermission("masuiteleports.teleport.toggle")
    public void teleportToggleCommand(Player player) {
        new BukkitPluginChannel(plugin, player, "MaSuiteTeleports", "TeleportToggle", player.getName()).send();
    }

    @CommandAlias("tphere")
    @Description("Teleports target to sender")
    @CommandPermission("masuiteteleports.teleport.force.here")
    @CommandCompletion("@masuite_players")
    public void teleportHereCommand(Player player, String target) {
        new BukkitPluginChannel(plugin, player, new Object[]{"MaSuiteTeleports", "TeleportForceHere", player.getName(), target, player.hasPermission("masuiteteleports.teleport.toggle.bypass")}).send();
    }

    @CommandAlias("tpall")
    @Description("Teleports all players to the sender")
    @CommandPermission("masuiteteleports.teleport.force.all")
    @CommandCompletion("@masuite_players")
    public void teleportAllCommand(Player player, @Optional String targetPlayer) {
        new BukkitPluginChannel(plugin, player, "MaSuiteTeleports", "TeleportForceAll", targetPlayer == null ? player.getName() : targetPlayer).send();
    }
}