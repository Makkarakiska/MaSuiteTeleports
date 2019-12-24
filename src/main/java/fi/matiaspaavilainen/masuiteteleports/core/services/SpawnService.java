package fi.matiaspaavilainen.masuiteteleports.core.services;

import fi.matiaspaavilainen.masuitecore.core.utils.HibernateUtil;
import fi.matiaspaavilainen.masuiteteleports.bungee.MaSuiteTeleports;
import fi.matiaspaavilainen.masuiteteleports.core.models.Spawn;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class SpawnService {

    private EntityManager entityManager = HibernateUtil.getEntityManager();
    public HashMap<String, List<Spawn>> spawns = new HashMap<>();

    private MaSuiteTeleports plugin;

    public SpawnService(MaSuiteTeleports plugin) {
        this.plugin = plugin;
    }

    /**
     * Load {@link Spawn} from database or cache
     *
     * @param server name of the server
     * @param type   type of the spawn
     * @return returns {@link Spawn} or null
     */
    public Spawn getSpawn(String server, int type) {
        return this.loadSpawn(server, type);
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
    public void removeHome(Spawn spawn) {
        entityManager.getTransaction().begin();
        entityManager.remove(spawn);
        entityManager.getTransaction().commit();

        // Update cache
        spawns.put(spawn.getLocation().getServer(), spawns.get(spawn.getLocation().getServer()).stream().filter(cachedSpawn -> cachedSpawn.getType() != spawn.getType()).collect(Collectors.toList()));
    }

    /**
     * Load {@link Spawn} from database or cache
     *
     * @param server name of the server
     * @param type   type of the spawn
     * @return returns {@link Spawn} or null
     */
    private Spawn loadSpawn(String server, int type) {
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

        Spawn spawn = entityManager.createNamedQuery(query, Spawn.class)
                .setParameter("type", type)
                .setParameter("server", server)
                .getResultList().stream().findFirst().orElse(null);

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
