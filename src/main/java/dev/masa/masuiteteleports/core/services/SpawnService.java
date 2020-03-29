package dev.masa.masuiteteleports.core.services;

import dev.masa.masuitecore.core.channels.BungeePluginChannel;
import dev.masa.masuitecore.core.objects.Location;
import dev.masa.masuitecore.core.utils.HibernateUtil;
import dev.masa.masuiteteleports.bungee.MaSuiteTeleports;
import dev.masa.masuiteteleports.core.models.Spawn;
import dev.masa.masuiteteleports.core.objects.SpawnType;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class SpawnService {

    private EntityManager entityManager = HibernateUtil.addClasses(Spawn.class).getEntityManager();
    public HashMap<String, List<Spawn>> spawns = new HashMap<>();

    private MaSuiteTeleports plugin;

    public SpawnService(MaSuiteTeleports plugin) {
        this.plugin = plugin;
    }

    public boolean teleportToSpawn(ProxiedPlayer player, SpawnType spawnType) {
        if(player == null) {
            return false;
        }
        Spawn spawn = this.getSpawn(player.getServer().getInfo(), spawnType);
        if (spawn == null) {
            if (spawnType == SpawnType.DEFAULT) {
                plugin.formator.sendMessage(player, plugin.config.load("teleports", "messages.yml").getString("spawn.not-found"));
            }

            return false;
        }

        if (spawnType == SpawnType.DEFAULT) {
            plugin.playerPositionService.requestPosition(player);
        }

        Location loc = spawn.getLocation();
        BungeePluginChannel bpc = new BungeePluginChannel(plugin,
                plugin.getProxy().getServerInfo(loc.getServer()),
                "MaSuiteTeleports",
                "SpawnPlayer",
                player.getName(),
                loc.serialize()
        );

        if (!loc.getServer().equals(player.getServer().getInfo().getName())) {
            player.connect(plugin.getProxy().getServerInfo(loc.getServer()));
            plugin.getProxy().getScheduler().schedule(plugin, bpc::send, plugin.config.load(null, "config.yml").getInt("teleportation-delay"), TimeUnit.MILLISECONDS);
        } else {
            bpc.send();
        }

        return true;
    }

    /**
     * Load {@link Spawn} from database or cache
     *
     * @param server name of the server
     * @param type   type of the spawn
     * @return returns {@link Spawn} or null
     */
    public Spawn getSpawn(String server, SpawnType type) {
        return this.loadSpawn(server, type);
    }

    /**
     * Load {@link Spawn} from database or cache
     *
     * @param server name of the server
     * @param type   type of the spawn
     * @return returns {@link Spawn} or null
     */
    public Spawn getSpawn(ServerInfo server, SpawnType type) {
        return this.loadSpawn(server.getName(), type);
    }

    /**
     * Create a new {@link Spawn}
     *
     * @param spawn spawn to create
     */
    public Spawn createSpawn(Spawn spawn) {
        entityManager.getTransaction().begin();
        entityManager.persist(spawn);
        entityManager.getTransaction().commit();

        if (!spawns.containsKey(spawn.getLocation().getServer())) {
            spawns.put(spawn.getLocation().getServer(), new ArrayList<>());
        }

        spawns.get(spawn.getLocation().getServer()).add(spawn);

        return spawn;
    }

    /**
     * Update specific {@link Spawn}
     */
    public Spawn updateSpawn(Spawn spawn) {
        entityManager.getTransaction().begin();
        entityManager.merge(spawn);
        entityManager.getTransaction().commit();

        // Remove spawn from list and add new back
        List<Spawn> spawnList = spawns.get(spawn.getLocation().getServer()).stream().filter(cachedSpawn -> cachedSpawn.getType() != spawn.getType()).collect(Collectors.toList());
        spawnList.add(spawn);
        spawns.put(spawn.getLocation().getServer(), spawnList);
        return spawn;
    }

    /**
     * Remove spawn
     *
     * @param spawn spawn to remove
     */
    public void removeSpawn(Spawn spawn) {
        entityManager.getTransaction().begin();
        entityManager.remove(spawn);
        entityManager.getTransaction().commit();

        // Update cache
        spawns.put(spawn.getLocation().getServer(), spawns.get(spawn.getLocation().getServer()).stream().filter(cachedSpawn -> cachedSpawn.getType() != spawn.getType()).collect(Collectors.toList()));
    }

    /**
     * Initialize spawns for use
     */
    public void initializeSpawns() {
        List<Spawn> spawnList = entityManager.createQuery("SELECT s FROM Spawn s", Spawn.class).getResultList();
        spawnList.forEach(spawn -> spawns.put(spawn.getLocation().getServer(),
                spawnList.stream().filter(listedSpawn -> listedSpawn.getLocation().getServer().equalsIgnoreCase(spawn.getLocation().getServer())).collect(Collectors.toList())));
    }

    /**
     * Load {@link Spawn} from database or cache
     *
     * @param server name of the server
     * @param type   type of the spawn
     * @return returns {@link Spawn} or null
     */
    private Spawn loadSpawn(String server, SpawnType type) {
        /// Try to load from cache
        if (spawns.containsKey(server)) {
            Optional<Spawn> cachedHome = spawns.get(server).stream().filter(spawn -> spawn.getType() == type).findFirst();
            if (cachedHome.isPresent()) {
                return cachedHome.get();
            }
        }

        // Get spawn type from config
        String spawnType = plugin.config.load("teleports", "settings.yml").getString("spawn-type");
        String query = spawnType.equalsIgnoreCase("server") ? "findSpawnByTypeAndServer" : "findSpawnByType";

        TypedQuery<Spawn> namedQuery = entityManager.createNamedQuery(query, Spawn.class).setParameter("type", type);

        // Add server if spawn type is server
        if (query.equals("findSpawnByTypeAndServer")) {
            namedQuery.setParameter("server", server);
        }

        // Execute query
        Spawn spawn = namedQuery.getResultList().stream().findFirst().orElse(null);

        // If not null, try to add cache
        if (spawn != null) {
            if (!spawns.containsKey(spawn.getLocation().getServer())) {
                spawns.put(spawn.getLocation().getServer(), new ArrayList<>());
            }
            spawns.get(spawn.getLocation().getServer()).add(spawn);
        }
        return spawn;
    }
}
