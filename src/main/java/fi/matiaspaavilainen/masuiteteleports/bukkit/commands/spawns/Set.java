package fi.matiaspaavilainen.masuiteteleports.bukkit.commands.spawns;

import fi.matiaspaavilainen.masuitecore.core.channels.BukkitPluginChannel;
import fi.matiaspaavilainen.masuiteteleports.bukkit.MaSuiteTeleports;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Set implements CommandExecutor {

    private MaSuiteTeleports plugin;

    public Set(MaSuiteTeleports p) {
        plugin = p;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return false;
        }

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {

            if (args.length != 1) {
                plugin.formator.sendMessage(sender, plugin.config.load("teleports", "syntax.yml").getString("spawn.set"));
                return;
            }

            if (!args[0].equalsIgnoreCase("default") && !args[0].equalsIgnoreCase("first")) {
                plugin.formator.sendMessage(sender, plugin.config.load("teleports", "syntax.yml").getString("spawn.set"));
                return;
            }

            if (plugin.in_command.contains(sender)) {
                plugin.formator.sendMessage(sender, plugin.config.load(null, "messages.yml").getString("on-active-command"));
                return;
            }

            plugin.in_command.add(sender);

            Player p = (Player) sender;
            Location loc = p.getLocation();
            new BukkitPluginChannel(plugin, p, new Object[]{"MaSuiteTeleports", "SetSpawn", p.getName(),
                    loc.getWorld().getName() + ":" + loc.getX() + ":" + loc.getY() + ":" + loc.getZ() + ":"
                    + loc.getYaw() + ":" + loc.getPitch(), args[0].toLowerCase()}).send();
            plugin.in_command.remove(sender);

        });

        return true;
    }
}
