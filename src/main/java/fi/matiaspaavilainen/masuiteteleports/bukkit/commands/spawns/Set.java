package fi.matiaspaavilainen.masuiteteleports.bukkit.commands.spawns;

import fi.matiaspaavilainen.masuiteteleports.bukkit.MaSuiteTeleports;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

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
                plugin.formator.sendMessage((Player) sender, plugin.config.load("teleports", "syntax.yml").getString("spawn.set"));
                return;
            }

            if (!args[0].equalsIgnoreCase("default") && !args[0].equalsIgnoreCase("first")) {
                plugin.formator.sendMessage((Player) sender, plugin.config.load("teleports", "syntax.yml").getString("spawn.set"));
                return;
            }

            if (plugin.in_command.contains(sender)) { // this function is not really necessary, but safety first
                plugin.formator.sendMessage((Player) sender, plugin.config.load(null, "messages.yml").getString("on-active-command"));
                return;
            }

            plugin.in_command.add(sender);

            Player p = (Player) sender;

            try (ByteArrayOutputStream b = new ByteArrayOutputStream();
                 DataOutputStream out = new DataOutputStream(b)) {
                out.writeUTF("MaSuiteTeleports");
                out.writeUTF("SetSpawn");
                out.writeUTF(p.getName());
                Location loc = p.getLocation();
                out.writeUTF(loc.getWorld().getName() + ":" + loc.getX() + ":" + loc.getY() + ":" + loc.getZ() + ":"
                        + loc.getYaw() + ":" + loc.getPitch());
                out.writeUTF(args[0].toLowerCase());
                p.sendPluginMessage(plugin, "BungeeCord", b.toByteArray());

            } catch (IOException e) {
                e.printStackTrace();
            }

            plugin.in_command.remove(sender);

        });

        return true;
    }
}
