package fi.matiaspaavilainen.masuiteteleports.managers;

import fi.matiaspaavilainen.masuitecore.managers.Location;
import fi.matiaspaavilainen.masuiteteleports.MaSuiteTeleports;
import net.md_5.bungee.api.config.ServerInfo;
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
    public HashMap<UUID, Location> positions, localPositions = new HashMap<>();
    private HashMap<UUID, ServerInfo> serverPositions = new HashMap<>();

    public PositionListener(MaSuiteTeleports p) {
        plugin = p;
    }

    public void requestPosition(ProxiedPlayer p) {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(b);
        try {
            out.writeUTF("MaSuiteTeleports");
            out.writeUTF("GetLocation");
            out.writeUTF(p.getName());
            out.writeUTF(p.getServer().getInfo().getName());
        } catch (IOException e) {
            e.printStackTrace();
        }
        p.getServer().sendData("ProxySuite", b.toByteArray());
    }

    public void locationReceived(ProxiedPlayer p, Location loc, ServerInfo serverInfo) {
        if (positionRunnables.containsKey(p.getUniqueId())) {
            localPositions.put(p.getUniqueId(), loc);
            serverPositions.put(p.getUniqueId(), serverInfo);
            positionRunnables.remove(p.getUniqueId()).run();
        } else {
            positions.put(p.getUniqueId(), loc);
            serverPositions.put(p.getUniqueId(), serverInfo);
        }
    }
}
