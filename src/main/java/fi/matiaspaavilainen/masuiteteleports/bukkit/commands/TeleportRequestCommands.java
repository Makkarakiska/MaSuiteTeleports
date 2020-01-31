package fi.matiaspaavilainen.masuiteteleports.bukkit.commands;

import fi.matiaspaavilainen.masuitecore.acf.BaseCommand;
import fi.matiaspaavilainen.masuitecore.acf.annotation.*;
import fi.matiaspaavilainen.masuitecore.core.channels.BukkitPluginChannel;
import fi.matiaspaavilainen.masuiteteleports.bukkit.MaSuiteTeleports;
import org.bukkit.entity.Player;

public class TeleportRequestCommands extends BaseCommand {

    private MaSuiteTeleports plugin;

    public TeleportRequestCommands(MaSuiteTeleports plugin) {
        this.plugin = plugin;
    }

    @CommandAlias("tpa")
    @Description("Sends teleportation request to target")
    @CommandPermission("masuiteteleports.teleport.request.to")
    @CommandCompletion("@masuite_players")
    @Conditions("cooldown:type=requests,bypass:masuiteteleports.cooldown.override")
    public void createRequestToTarget(Player player, String target) {
        new BukkitPluginChannel(plugin, player, "MaSuiteTeleports", "TeleportRequestTo", player.getName(), target).send();
    }

    @CommandAlias("tpahere")
    @Description("Sends teleportation request to target")
    @CommandPermission("masuiteteleports.teleport.here")
    @CommandCompletion("@masuite_players")
    @Conditions("cooldown:type=requests,bypass:masuiteteleports.cooldown.override")
    public void createRequestToSender(Player player, String target) {
        new BukkitPluginChannel(plugin, player, "MaSuiteTeleports", "TeleportRequestHere", player.getName(), target).send();
    }

    @CommandAlias("tpaccept|tpyes")
    @Description("Accepts teleportation request")
    @CommandPermission("masuiteteleports.teleport.accept")
    @Conditions("cooldown:type=requests,bypass:masuiteteleports.cooldown.override")
    public void acceptRequest(Player player) {
        new BukkitPluginChannel(plugin, player, "MaSuiteTeleports", "TeleportAccept", player.getName()).send();
    }

    @CommandAlias("tpdeny|tpno")
    @Description("Denies teleportation request")
    @CommandPermission("masuiteteleports.teleport.deny")
    @Conditions("cooldown:type=requests,bypass:masuiteteleports.cooldown.override")
    public void denyRequest(Player player) {
        new BukkitPluginChannel(plugin, player, "MaSuiteTeleports", "TeleportDeny", player.getName()).send();
    }

    @CommandAlias("tpalock")
    @Description("Locks request to accepting or denying them")
    @CommandPermission("masuiteteleports.teleport.lock")
    @CommandCompletion("accept|yes|deny|no|off")
    public void lockRequests(Player player, String type) {
        if(type.equalsIgnoreCase("accept") || type.equalsIgnoreCase("yes")) {
            new BukkitPluginChannel(plugin, player, "MaSuiteTeleports", "TeleportRequestLock", player.getName(), "Enable", true).send();
        }
        if(type.equalsIgnoreCase("deny") || type.equalsIgnoreCase("no")) {
            new BukkitPluginChannel(plugin, player, "MaSuiteTeleports", "TeleportRequestLock", player.getName(), "Enable", false).send();
        }
        if(type.equalsIgnoreCase("off")) {
            new BukkitPluginChannel(plugin, player, "MaSuiteTeleports", "TeleportRequestLock", player.getName(), "Disable").send();
        }
    }
}
