package fi.matiaspaavilainen.masuiteteleports.bukkit.commands.requests;

import fi.matiaspaavilainen.masuiteteleports.bukkit.MaSuiteTeleports;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class To implements CommandExecutor {

    private MaSuiteTeleports plugin;

    public To(MaSuiteTeleports p) {
        plugin = p;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return false;
        }

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {

            if (args.length != 1) {
                plugin.formator.sendMessage((Player) sender, plugin.config.load("teleports", "syntax.yml").getString("tpa"));
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
                out.writeUTF("TeleportRequestTo");
                out.writeUTF(sender.getName());
                out.writeUTF(args[0]);
                p.sendPluginMessage(plugin, "BungeeCord", b.toByteArray());

            } catch (IOException e) {
                e.printStackTrace();
            }

            plugin.in_command.remove(sender);

        });

        return true;
    }
}
