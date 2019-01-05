package fi.matiaspaavilainen.masuiteteleports.bukkit.commands.force;

import fi.matiaspaavilainen.masuiteteleports.bukkit.MaSuiteTeleports;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Teleport implements CommandExecutor {

    private MaSuiteTeleports plugin;

    public Teleport(MaSuiteTeleports p) {
        plugin = p;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return false;
        }

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {

            if (plugin.in_command.contains(sender)) {
                plugin.formator.sendMessage((Player) sender, plugin.config.load(null, "messages.yml").getString("on-active-command"));
                return;
            }

            plugin.in_command.add(sender);

            Player p = (Player) sender;

            try (ByteArrayOutputStream b = new ByteArrayOutputStream();
                 DataOutputStream out = new DataOutputStream(b)) {

                out.writeUTF("MaSuiteTeleports");
                out.writeUTF("TeleportForceTo");
                out.writeUTF(sender.getName());
                switch (args.length) {
                    case (1):
                        out.writeUTF("TeleportSenderToTarget");
                        out.writeUTF(args[0]);
                        break;
                    case (2):
                        out.writeUTF("TeleportTargetToTarget");
                        out.writeUTF(args[0]);
                        out.writeUTF(args[1]);
                        break;
                    case (3):
                        // Teleport sender to coordinates
                        if (!isDouble(args[0]) && !isDouble(args[1]) && !isDouble(args[2])) {
                            break;
                        }
                        out.writeUTF("TeleportToXYZ");
                        out.writeUTF(sender.getName());
                        out.writeDouble(Double.parseDouble(args[0]));
                        out.writeDouble(Double.parseDouble(args[1]));
                        out.writeDouble(Double.parseDouble(args[2]));
                        break;
                    case (4):
                        if (!isDouble(args[1]) && !isDouble(args[2]) && !isDouble(args[3])) {
                            break;
                        }
                        // If any of the server's worlds match to args[0]
                        if (Bukkit.getWorlds().stream().anyMatch(world -> world.getName().equals(args[0]))) {
                            out.writeUTF("TeleportToCoordinates");
                            out.writeUTF(sender.getName());
                            out.writeUTF(args[0]);
                            out.writeDouble(Double.parseDouble(args[1]));
                            out.writeDouble(Double.parseDouble(args[2]));
                            out.writeDouble(Double.parseDouble(args[3]));
                            break;
                        }

                        // If not, send target to XYZ
                        out.writeUTF("TeleportToXYZ");
                        out.writeUTF(args[0]);
                        out.writeDouble(Double.parseDouble(args[1]));
                        out.writeDouble(Double.parseDouble(args[2]));
                        out.writeDouble(Double.parseDouble(args[3]));
                        break;
                    case (5):
                        // Teleport target to location
                        if (!isDouble(args[2]) && !isDouble(args[3]) && !isDouble(args[4])) {
                            break;
                        }
                        out.writeUTF("TeleportToCoordinates");
                        out.writeUTF(args[0]);
                        out.writeUTF(args[1]);
                        out.writeDouble(Double.parseDouble(args[2]));
                        out.writeDouble(Double.parseDouble(args[3]));
                        out.writeDouble(Double.parseDouble(args[4]));
                        break;
                    default:
                        plugin.formator.sendMessage((Player) sender, plugin.config.load("teleports", "syntax.yml").getString("tp.title"));
                        for (String syntax : plugin.config.load("teleports", "syntax.yml").getStringList("tp.syntaxes")) {
                            plugin.formator.sendMessage(p, syntax);
                        }
                        break;
                }

                p.sendPluginMessage(plugin, "BungeeCord", b.toByteArray());

            } catch (IOException e) {
                e.printStackTrace();
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
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
