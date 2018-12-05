package fi.matiaspaavilainen.masuiteteleports.commands;

import fi.matiaspaavilainen.masuitecore.Utils;
import fi.matiaspaavilainen.masuitecore.chat.Formator;
import fi.matiaspaavilainen.masuitecore.config.Configuration;
import fi.matiaspaavilainen.masuitecore.managers.Location;
import fi.matiaspaavilainen.masuiteteleports.MaSuiteTeleports;
import fi.matiaspaavilainen.masuiteteleports.managers.PlayerFinder;
import fi.matiaspaavilainen.masuiteteleports.managers.Teleport;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class TeleportForceCommand {

    private MaSuiteTeleports plugin;
    public TeleportForceCommand(MaSuiteTeleports p){
        plugin = p;
    }
    private Utils utils = new Utils();
    private Configuration config = new Configuration();
    private Formator formator = new Formator();

    // Sender to target
    public void tp(ProxiedPlayer sender, String t){
        ProxiedPlayer target = new PlayerFinder().get(t);
        if(utils.isOnline(target, sender)){
            plugin.positions.requestPosition(sender);
            new Teleport(plugin).playerToPlayer(sender, target);
            formator.sendMessage(sender, config.load("teleports","messages.yml")
                    .getString("teleported")
                    .replace("%player%", target.getName())
            );
        }
    }

    // Target to other target
    public void tp(ProxiedPlayer sender, String t1, String t2){
        ProxiedPlayer target1 = new PlayerFinder().get(t1);
        ProxiedPlayer target2 = new PlayerFinder().get(t2);
        if(utils.isOnline(target1, sender) &&  utils.isOnline(target2, sender)){
            plugin.positions.requestPosition(target1);
            new Teleport(plugin).playerToPlayer(target1, target2);
            formator.sendMessage(target1, config.load("teleports","messages.yml")
                    .getString("teleported")
                    .replace("%player%", target2.getName())
            );
        }
    }

    // Teleport player to coordinates in the same world in the same world
    public void tp(ProxiedPlayer sender, String t, Double x, Double y, Double z){
        ProxiedPlayer target = new PlayerFinder().get(t);
        if(utils.isOnline(target, sender)){
            plugin.positions.requestPosition(target);
            ByteArrayOutputStream b = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(b);
            try {
                out.writeUTF("MaSuiteTeleports");
                out.writeUTF("PlayerToXYZ");
                out.writeUTF(target.getName());
                out.writeDouble(x);
                out.writeDouble(y);
                out.writeDouble(z);
                target.getServer().sendData("BungeeCord", b.toByteArray());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    // Teleport player to specific location in the same server
    public void tp(ProxiedPlayer sender, String t, Location loc){
        ProxiedPlayer target = new PlayerFinder().get(t);
        if(utils.isOnline(target, sender)){
            plugin.positions.requestPosition(target);
            ByteArrayOutputStream b = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(b);
            try {
                out.writeUTF("MaSuiteTeleports");
                out.writeUTF("PlayerToLocation");
                out.writeUTF(target.getName());
                out.writeUTF(loc.getWorld());
                out.writeDouble(loc.getX());
                out.writeDouble(loc.getY());
                out.writeDouble(loc.getZ());
                target.getServer().sendData("BungeeCord", b.toByteArray());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void tphere(ProxiedPlayer sender, String t){
        ProxiedPlayer target = new PlayerFinder().get(t);
        if(utils.isOnline(target, sender)){
            plugin.positions.requestPosition(target);
            new Teleport(plugin).playerToPlayer(target, sender);
        }
    }

    public void tpall(ProxiedPlayer target){
        if(utils.isOnline(target)){
            for(ProxiedPlayer p : ProxyServer.getInstance().getPlayers()){
                plugin.positions.requestPosition(p);
                new Teleport(plugin).playerToPlayer(p, target);
            }
        }
    }
}
