package fi.matiaspaavilainen.masuiteteleports.bukkit;

import fi.matiaspaavilainen.masuitecore.bukkit.chat.Formator;
import fi.matiaspaavilainen.masuitecore.core.configuration.BukkitConfiguration;
import fi.matiaspaavilainen.masuiteteleports.bukkit.commands.Back;
import fi.matiaspaavilainen.masuiteteleports.bukkit.commands.force.All;
import fi.matiaspaavilainen.masuiteteleports.bukkit.commands.force.Here;
import fi.matiaspaavilainen.masuiteteleports.bukkit.commands.force.Teleport;
import fi.matiaspaavilainen.masuiteteleports.bukkit.commands.requests.Accept;
import fi.matiaspaavilainen.masuiteteleports.bukkit.commands.requests.Deny;
import fi.matiaspaavilainen.masuiteteleports.bukkit.commands.requests.Lock;
import fi.matiaspaavilainen.masuiteteleports.bukkit.commands.requests.To;
import fi.matiaspaavilainen.masuiteteleports.bukkit.commands.spawns.Delete;
import fi.matiaspaavilainen.masuiteteleports.bukkit.commands.spawns.Set;
import fi.matiaspaavilainen.masuiteteleports.bukkit.commands.spawns.Spawn;
import fi.matiaspaavilainen.masuiteteleports.bukkit.listeners.TeleportListener;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MaSuiteTeleports extends JavaPlugin implements Listener {

    public BukkitConfiguration config = new BukkitConfiguration();
    public Formator formator = new Formator();

    public final List<CommandSender> in_command = new ArrayList<>();
    public List<UUID> tpQue = new ArrayList<>();


    @Override
    public void onEnable() {
        // Create configs
        config.create(this, "teleports", "config.yml");
        config.create(this, "teleports", "messages.yml");
        config.create(this, "teleports", "syntax.yml");

        getServer().getPluginManager().registerEvents(this, this);
        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        getServer().getMessenger().registerIncomingPluginChannel(this, "BungeeCord", new TeleportListener(this));

        // Load commands
        loadCommands();

    }

    private void loadCommands() {
        // Force
        getCommand("tpall").setExecutor(new All(this));
        getCommand("tphere").setExecutor(new Here(this));
        getCommand("tp").setExecutor(new Teleport(this));

        // Requests
        getCommand("tpaccept").setExecutor(new Accept(this));
        getCommand("tpdeny").setExecutor(new Deny(this));
        getCommand("tpahere").setExecutor(new fi.matiaspaavilainen.masuiteteleports.bukkit.commands.requests.Here(this));
        getCommand("tpa").setExecutor(new To(this));
        getCommand("tpalock").setExecutor(new Lock(this));

        // Spawn
        getCommand("delspawn").setExecutor(new Delete(this));
        getCommand("setspawn").setExecutor(new Set(this));
        getCommand("spawn").setExecutor(new Spawn(this));

        // Back
        getCommand("back").setExecutor(new Back(this));
    }

    @EventHandler
    public void onDeath(PlayerRespawnEvent e) {
        Player p = e.getPlayer();
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(b);
        switch (getConfig().getString("respawn-type").toLowerCase()) {
            case ("none"):
                break;
            case ("bed"):
                if (p.getBedSpawnLocation() != null) {
                    p.teleport(p.getBedSpawnLocation());
                } else {
                    try {
                        out.writeUTF("MaSuiteTeleports");
                        out.writeUTF("SpawnPlayer");
                        out.writeUTF(p.getName());
                        p.sendPluginMessage(this, "BungeeCord", b.toByteArray());
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
                break;
            case ("home"):
                try {
                    out.writeUTF("HomeCommand");
                    out.writeUTF(p.getName());
                    out.writeUTF("home");
                    p.sendPluginMessage(this, "BungeeCord", b.toByteArray());
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                break;
            case ("spawn"):
                try {
                    out.writeUTF("MaSuiteTeleports");
                    out.writeUTF("SpawnPlayer");
                    out.writeUTF(p.getName());
                    p.sendPluginMessage(this, "BungeeCord", b.toByteArray());
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                break;

        }
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        try (ByteArrayOutputStream b = new ByteArrayOutputStream();
             DataOutputStream out = new DataOutputStream(b)) {
            out.writeUTF("MaSuiteTeleports");
            out.writeUTF("GetLocation");
            out.writeUTF(e.getEntity().getName());
            Location loc = e.getEntity().getLocation();
            out.writeUTF(loc.getWorld().getName() + ":" + loc.getX() + ":" + loc.getY() + ":" + loc.getZ() + ":" + loc.getYaw() + ":" + loc.getPitch());
            out.writeUTF("DETECTSERVER");
            e.getEntity().sendPluginMessage(this, "BungeeCord", b.toByteArray());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }


    @EventHandler
    public void onDamage(EntityDamageEvent e) {
        if (e.getEntity() instanceof Player) {
            if (tpQue.contains(e.getEntity().getUniqueId())) {
                e.setCancelled(true);
            }
        }

    }

    @EventHandler
    public void onLeave(PlayerQuitEvent e) {
        in_command.remove(e.getPlayer());
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        if (getConfig().getBoolean("spawn.first")) {
            if (!e.getPlayer().hasPlayedBefore()) {
                try (ByteArrayOutputStream b = new ByteArrayOutputStream();
                     DataOutputStream out = new DataOutputStream(b)) {
                    out.writeUTF("MaSuiteTeleports");
                    out.writeUTF("FirstSpawnPlayer");
                    out.writeUTF(e.getPlayer().getName());
                    getServer().getScheduler().runTaskLaterAsynchronously(this, () -> e.getPlayer().sendPluginMessage(this, "BungeeCord", b.toByteArray()), 10);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }
}
