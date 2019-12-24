package fi.matiaspaavilainen.masuiteteleports.bukkit.commands.force;

import fi.matiaspaavilainen.masuitecore.acf.BaseCommand;
import fi.matiaspaavilainen.masuitecore.acf.annotation.CommandAlias;
import fi.matiaspaavilainen.masuitecore.acf.annotation.CommandPermission;
import fi.matiaspaavilainen.masuitecore.acf.annotation.Description;
import fi.matiaspaavilainen.masuitecore.core.channels.BukkitPluginChannel;
import fi.matiaspaavilainen.masuiteteleports.bukkit.MaSuiteTeleports;
import org.bukkit.entity.Player;

@CommandAlias("tptoggle")
public class TpToggleCommand extends BaseCommand {

    private MaSuiteTeleports plugin;

    public TpToggleCommand(MaSuiteTeleports p) {
        plugin = p;
    }

    @CommandPermission("masuiteleports.teleport.toggle")
    @Description("Enable or disable force teleportations")
    @CommandAlias("teleporttoggle")
    public void teleportToggleCommand(Player player) {
        new BukkitPluginChannel(plugin, player, "MaSuiteTeleports", "TeleportToggle", player.getName()).send();
    }
}
