package fi.matiaspaavilainen.masuiteteleports.bungee.commands;

import fi.matiaspaavilainen.masuitecore.bungee.chat.Formator;
import fi.matiaspaavilainen.masuitecore.core.configuration.BungeeConfiguration;
import fi.matiaspaavilainen.masuitecore.core.objects.Location;
import fi.matiaspaavilainen.masuiteteleports.bungee.MaSuiteTeleports;
import fi.matiaspaavilainen.masuiteteleports.core.objects.Spawn;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class SpawnCommand {

    private MaSuiteTeleports plugin;

    public SpawnCommand(MaSuiteTeleports plugin) {
        this.plugin = plugin;
    }

    private Formator formator = new Formator();
    private BungeeConfiguration config = new BungeeConfiguration();

    /**
     * Spawn player
     *
     * @param p    player to spawn
     * @param type default (0) or first (1)
     */
    public void spawn(ProxiedPlayer p, int type) {
        if (p == null) {
            return;
        }
        Spawn spawn = new Spawn();
        if (MaSuiteTeleports.cooldowns.containsKey(p.getUniqueId())) {
            if (System.currentTimeMillis() - MaSuiteTeleports.cooldowns.get(p.getUniqueId()) < config.load("teleports", "settings.yml").getInt("cooldown") * 1000) {
                formator.sendMessage(p, config.load("teleports", "messages.yml")
                        .getString("in-cooldown")
                        .replace("%time%", String.valueOf(config.load("teleports", "settings.yml").getInt("cooldown"))
                        ));
                MaSuiteTeleports.cooldowns.remove(p.getUniqueId());
                return;
            }
        }
        if (spawn.spawn(p, plugin, type)) {
            if(type == 0){
                formator.sendMessage(p, config.load("teleports", "messages.yml").getString("spawn.teleported"));
            }

            if(!p.hasPermission("masuiteteleports.cooldown.override"))
                MaSuiteTeleports.cooldowns.put(p.getUniqueId(), System.currentTimeMillis());
        }
    }

    /**
     * Set spawn
     *
     * @param p    executor
     * @param loc  spawn location
     * @param type default (0) or first (1)
     */
    public void setSpawn(ProxiedPlayer p, Location loc, int type) {
        if (p == null) {
            return;
        }
        Spawn spawn = new Spawn(p.getServer().getInfo().getName(), loc, type);
        if (spawn.create(spawn)) {
            formator.sendMessage(p, config.load("teleports", "messages.yml").getString("spawn.set"));
        } else {
            System.out.println("[MaSuite] [Teleports] [Spawn] Error while creating spawn.");
        }

    }

    /**
     * Deletes specific spawn
     *
     * @param p    executor
     * @param type default (0) or first (1)
     */
    public void deleteSpawn(ProxiedPlayer p, int type) {
        if (p == null) {
            return;
        }
        Spawn spawn = new Spawn().find(p.getServer().getInfo().getName(), type);
        if (spawn != null) {
            if (spawn.delete()) {
                formator.sendMessage(p, config.load("teleports", "messages.yml").getString("spawn.deleted"));
            } else {
                System.out.println("[MaSuite] [Teleports] [Spawn] Error while deleting spawn.");
            }
        } else {
            formator.sendMessage(p, config.load("teleports", "messages.yml").getString("spawn.not-found"));
        }
    }
}
