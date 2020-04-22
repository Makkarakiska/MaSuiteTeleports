package dev.masa.masuiteteleports.bukkit.commands;

import dev.masa.masuitecore.acf.BaseCommand;
import dev.masa.masuitecore.acf.annotation.CommandAlias;
import dev.masa.masuitecore.acf.annotation.CommandPermission;
import dev.masa.masuitecore.acf.annotation.Conditions;
import dev.masa.masuitecore.acf.annotation.Description;
import dev.masa.masuitecore.core.channels.BukkitPluginChannel;
import dev.masa.masuiteteleports.bukkit.MaSuiteTeleports;
import org.bukkit.entity.Player;

public class BackCommand extends BaseCommand {

    private MaSuiteTeleports plugin;

    public BackCommand(MaSuiteTeleports p) {
        plugin = p;
    }

    @CommandAlias("back")
    @Description("Teleports to your last known location")
    @CommandPermission("masuiteteleports.teleport.back")
    @Conditions("cooldown:type=back,bypass:masuiteteleports.cooldown.override")
    public void teleportBack(Player player) {
        plugin.getApi().getWarmupService().applyWarmup(player, "masuiteteleports.warmup.override", "teleports", success -> {
            if (success) {
                new BukkitPluginChannel(plugin, player, "MaSuiteTeleports", "Back", player.getName()).send();
            }
        });
    }
}
