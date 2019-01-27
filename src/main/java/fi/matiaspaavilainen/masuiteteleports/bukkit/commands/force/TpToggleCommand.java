package fi.matiaspaavilainen.masuiteteleports.bukkit.commands.force;

import fi.matiaspaavilainen.masuitecore.core.channels.BukkitPluginChannel;
import fi.matiaspaavilainen.masuiteteleports.bukkit.MaSuiteTeleports;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TpToggleCommand implements CommandExecutor {

    private MaSuiteTeleports plugin;

    public TpToggleCommand(MaSuiteTeleports p) {
        plugin = p;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return false;
        }

        new BukkitPluginChannel(plugin, (Player) sender, new Object[]{
                "MaSuiteTeleports",
                "TeleportToggle",
                sender.getName()
        }).send();
        return false;
    }
}
