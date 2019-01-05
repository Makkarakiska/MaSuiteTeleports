package fi.matiaspaavilainen.masuiteteleports.managers;

import fi.matiaspaavilainen.masuitecore.core.objects.Location;
import fi.matiaspaavilainen.masuiteteleports.bungee.MaSuiteTeleports;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

/**
 * Original author Sabbertan
 * Updated version for MaSuite: Masa
 */
public class PositionListener {

    private MaSuiteTeleports plugin;
    private HashMap<UUID, Runnable> positionRunnables = new HashMap<>();
    public HashMap<UUID, Location> positions;

    public PositionListener(MaSuiteTeleports plugin) {
        this.plugin = plugin;
        positions = new HashMap<>();
    }

    public void requestPosition(ProxiedPlayer p) {
        try (ByteArrayOutputStream b = new ByteArrayOutputStream();
             DataOutputStream out = new DataOutputStream(b)) {
            out.writeUTF("MaSuiteTeleports");
            out.writeUTF("GetLocation");
            out.writeUTF(p.getName());
            out.writeUTF(p.getServer().getInfo().getName());
            p.getServer().sendData("BungeeCord", b.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void locationReceived(ProxiedPlayer p, Location loc) {
        if (positionRunnables.containsKey(p.getUniqueId())) {
            positionRunnables.remove(p.getUniqueId()).run();
        } else {
            positions.put(p.getUniqueId(), loc);
        }
    }
}
