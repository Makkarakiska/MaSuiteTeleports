package fi.matiaspaavilainen.masuiteteleports.bukkit.commands;

import fi.matiaspaavilainen.masuitecore.acf.BaseCommand;
import fi.matiaspaavilainen.masuitecore.acf.annotation.CommandAlias;
import fi.matiaspaavilainen.masuitecore.acf.annotation.CommandPermission;
import fi.matiaspaavilainen.masuitecore.acf.annotation.Conditions;
import fi.matiaspaavilainen.masuitecore.acf.annotation.Description;
import fi.matiaspaavilainen.masuitecore.core.channels.BukkitPluginChannel;
import fi.matiaspaavilainen.masuiteteleports.bukkit.MaSuiteTeleports;
import org.bukkit.entity.Player;

public class BackCommand extends BaseCommand {

    private MaSuiteTeleports plugin;

    public BackCommand(MaSuiteTeleports p) {
        plugin = p;
    }

    @CommandAlias("back")
    @Description("Teleports to your last known location")
    @CommandPermission("masuiteteleports.back")
    @Conditions("cooldown:type=back,bypass:masuiteteleports.cooldown.override")
    public void teleportBack(Player player) {
        new BukkitPluginChannel(plugin, player, "MaSuiteTeleports", "Back", player.getName()).send();
    }
}
