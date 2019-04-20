package fi.matiaspaavilainen.masuiteteleports.bukkit.commands.force;

import fi.matiaspaavilainen.masuitecore.core.channels.BukkitPluginChannel;
import fi.matiaspaavilainen.masuiteteleports.bukkit.MaSuiteTeleports;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TpCommand implements CommandExecutor {

    private MaSuiteTeleports plugin;

    public TpCommand(MaSuiteTeleports p) {
        plugin = p;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return false;
        }

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {

            if (plugin.in_command.contains(sender)) {
                plugin.formator.sendMessage(sender, plugin.config.load(null, "messages.yml").getString("on-active-command"));
                return;
            }

            plugin.in_command.add(sender);

            Player p = (Player) sender;

            if(args.length == 1 || args.length == 2){
                if(!hasPerm(p, "masuiteteleports.teleport.force.player")){ plugin.in_command.remove(sender); return; }
            } else if(args.length == 3 || args.length == 4 || args.length == 5){
                if(!hasPerm(p, "masuiteteleports.teleport.force.coordinates")){ plugin.in_command.remove(sender); return; }
            }

            switch (args.length) {
                case (1):
                    new BukkitPluginChannel(plugin, p, new Object[]{"MaSuiteTeleports", "TeleportForceTo", sender.getName(), "TeleportSenderToTarget", args[0], p.hasPermission("masuiteteleports.teleport.toggle.bypass")}).send();
                    break;
                case (2):
                    new BukkitPluginChannel(plugin, p, new Object[]{"MaSuiteTeleports", "TeleportForceTo", sender.getName(), "TeleportTargetToTarget", args[0], args[1]}).send();
                    break;
                case (3):
                    // TpCommand sender to coordinates
                    if (Double.isNaN(parse(args[0], 0)) && Double.isNaN(parse(args[1], 0)) && Double.isNaN(parse(args[2], 0))) {
                        return;
                    }
                    new BukkitPluginChannel(plugin, p, new Object[]{"MaSuiteTeleports", "TeleportForceTo", sender.getName(), "TeleportToXYZ",
                            sender.getName(),
                            parse(args[0], p.getLocation().getX()),
                            parse(args[1], p.getLocation().getY()),
                            parse(args[2], p.getLocation().getZ())}).send();
                    break;
                case (4):

                    // tp <x> <y> <z> <world>
                    if(!Double.isNaN(parse(args[0], 0)) && !Double.isNaN(parse(args[1], 0)) && !Double.isNaN(parse(args[2], 0))) {
                        if (Bukkit.getWorlds().stream().anyMatch(world -> world.getName().equals(args[3]))) {
                            new BukkitPluginChannel(plugin, p, new Object[]{"MaSuiteTeleports", "TeleportForceTo", sender.getName(), "TeleportToCoordinates",
                                    sender.getName(),
                                    args[3],
                                    parse(args[0], p.getLocation().getX()),
                                    parse(args[1], p.getLocation().getY()),
                                    parse(args[2], p.getLocation().getZ())}).send();
                            break;
                        }
                    }

                    if (Double.isNaN(parse(args[1], 0)) && Double.isNaN(parse(args[2], 0)) && Double.isNaN(parse(args[3], 0))) {
                        return;
                    }
                    // If any of the server's worlds match to args[0]
                    if (Bukkit.getWorlds().stream().anyMatch(world -> world.getName().equals(args[0]))) {
                        new BukkitPluginChannel(plugin, p, new Object[]{"MaSuiteTeleports", "TeleportForceTo", sender.getName(), "TeleportToCoordinates",
                                sender.getName(),
                                args[0],
                                parse(args[1], p.getLocation().getX()),
                                parse(args[2], p.getLocation().getY()),
                                parse(args[3], p.getLocation().getZ())}).send();
                        break;
                    }

                    // If not, send target to XYZ
                    new BukkitPluginChannel(plugin, p, new Object[]{"MaSuiteTeleports", "TeleportForceTo", sender.getName(), "TeleportToXYZ",
                            args[0],
                            parse(args[0], p.getLocation().getX()),
                            parse(args[1], p.getLocation().getY()),
                            parse(args[2], p.getLocation().getZ())}).send();
                    break;
                case (5):
                    // TpCommand target to location
                    if (Double.isNaN(parse(args[2], 0)) && Double.isNaN(parse(args[3], 0)) && Double.isNaN(parse(args[4], 0))) {
                        return;
                    }
                    new BukkitPluginChannel(plugin, p, new Object[]{"MaSuiteTeleports", "TeleportForceTo", sender.getName(), "TeleportToCoordinates",
                            args[0],
                            args[1],
                            parse(args[2], p.getLocation().getX()),
                            parse(args[3], p.getLocation().getY()),
                            parse(args[4], p.getLocation().getZ())}).send();
                    break;
                default:
                    plugin.formator.sendMessage(sender, plugin.config.load("teleports", "syntax.yml").getString("tp.title"));
                    for (String syntax : plugin.config.load("teleports", "syntax.yml").getStringList("tp.syntaxes")) {
                        plugin.formator.sendMessage(p, syntax);
                    }
                    break;
            }
            plugin.in_command.remove(sender);
        });

        return true;
    }

    // Check if string is parsable to Double
    private boolean isDouble(String string) {
        try {
            Double.parseDouble(string);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private double parse(String string, double currentCoord) {
        if (string.startsWith("~")) {
            if (isDouble(string.replace("~", "") + currentCoord)) {
                String s = string.replace("~", "");
                return !s.isEmpty() ? Double.parseDouble(s) + currentCoord : currentCoord;
            }
        } else if (isDouble(string)) {
            return Double.parseDouble(string);
        }
        return Double.NaN;
    }

    private boolean hasPerm(Player player, String perm){
        if(player.hasPermission(perm)){
            return true;
        } else {
            plugin.formator.sendMessage(player, plugin.config.load(null, "messages.yml").getString("no-permission"));
            return false;
        }
    }
}
