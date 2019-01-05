package fi.matiaspaavilainen.masuiteteleports.bungee.managers;

import fi.matiaspaavilainen.masuitecore.bungee.chat.Formator;
import fi.matiaspaavilainen.masuitecore.core.configuration.BukkitConfiguration;
import fi.matiaspaavilainen.masuitecore.core.database.ConnectionManager;
import fi.matiaspaavilainen.masuitecore.core.database.Database;
import fi.matiaspaavilainen.masuitecore.core.objects.Location;
import fi.matiaspaavilainen.masuiteteleports.bungee.MaSuiteTeleports;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class Spawn {

    private Database db = ConnectionManager.db;
    private String server;
    private Location location;
    private int type;

    private Connection connection = null;
    private PreparedStatement statement = null;
    private BukkitConfiguration config = new BukkitConfiguration();
    private String tablePrefix = db.getTablePrefix();

    /**
     * An empty constructor for Spawn
     */
    public Spawn() {
    }

    /**
     * Constructor for spawn
     *
     * @param server   server's name
     * @param location spawn location
     * @param type     default (0) or first (1)
     */
    public Spawn(String server, Location location, int type) {
        this.server = server;
        this.location = location;
        this.type = type;
    }

    /**
     * Find spawn by server and type
     *
     * @param server server's name
     * @param type   default (0) or first (1)
     * @return spawn
     */
    public Spawn find(String server, int type) {
        Spawn spawn = new Spawn();
        ResultSet rs = null;
        String spawnType = config.load("teleports", "settings.yml").getString("spawn-type");
        String select = null;
        if (spawnType.equalsIgnoreCase("server")) {
            select = "SELECT * FROM " + tablePrefix + "spawns WHERE type = ? AND server = ?;";
        }

        if (spawnType.equalsIgnoreCase("global")) {
            select = "SELECT * FROM " + tablePrefix + "spawns WHERE type = ?;";

        }
        try {
            connection = db.hikari.getConnection();
            statement = connection.prepareStatement(select);
            statement.setInt(1, type);
            if (spawnType.equalsIgnoreCase("server")) {
                statement.setString(2, server);
            }
            rs = statement.executeQuery();

            boolean empty = true;
            while (rs.next()) {
                spawn.setServer(rs.getString("server"));
                spawn.setLocation(new Location(rs.getString("world"), rs.getDouble("x"), rs.getDouble("y"), rs.getDouble("z"), rs.getFloat("yaw"), rs.getFloat("pitch")));
                spawn.setType(type);
                empty = false;
            }
            if (empty) {
                return null;
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return spawn;
    }

    /**
     * Spawns player
     *
     * @param p      player to spawn
     * @param plugin plugin to use when sending plugin message
     * @param type   default (0) or first (1)
     * @return if player has been spawned or not
     */
    public boolean spawn(ProxiedPlayer p, MaSuiteTeleports plugin, int type) {
        new Spawn();
        Spawn spawn = new Spawn();
        spawn.find(p.getServer().getInfo().getName(), type);
        if (spawn == null) {
            if (type == 0) {
                new Formator().sendMessage(p, config.load("teleports", "messages.yml").getString("spawn.not-found"));
            }
            return false;
        }
        try {
            if (type == 0) {
                plugin.positions.requestPosition(p);
            }
            ByteArrayOutputStream b = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(b);
            out.writeUTF("MaSuiteTeleports");
            out.writeUTF("SpawnPlayer");
            out.writeUTF(p.getName());
            Location loc = spawn.getLocation();
            out.writeUTF(loc.getWorld() + ":" + loc.getX() + ":" + loc.getY() + ":" + loc.getZ() + ":" + loc.getYaw() + ":" + loc.getPitch());
            if (!spawn.getServer().equals(p.getServer().getInfo().getName())) {
                p.connect(ProxyServer.getInstance().getServerInfo(spawn.getServer()));
                ProxyServer.getInstance().getScheduler().schedule(plugin, () -> p.getServer().sendData("BungeeCord", b.toByteArray()), 500, TimeUnit.MILLISECONDS);
            } else {
                p.getServer().sendData("BungeeCord", b.toByteArray());
            }

        } catch (IOException e) {
            e.getStackTrace();
        }
        return true;
    }

    /**
     * Creates and saves the spawn
     *
     * @param spawn to save
     * @return if saving was success
     */
    public boolean create(Spawn spawn) {
        String spawnType = config.load("teleports", "settings.yml").getString("spawn-type");
        String query = null;
        Set<Spawn> spawns = spawn.all();
        if (spawn.getType() == 1) {
            if (spawnType.equalsIgnoreCase("server")) {
                if (spawns.stream().anyMatch(s -> s.getType() == 1 && s.getServer().equalsIgnoreCase(spawn.getServer()))) {
                    query = "UPDATE " + tablePrefix + "spawns SET server = ?, world = ?, x = ?, y = ?, z = ?, yaw = ?, pitch = ? WHERE type = ?;";
                } else {
                    query = "INSERT INTO " + tablePrefix + "spawns (server, world, x, y, z, yaw, pitch, type) VALUES (?, ?, ?, ?, ?, ?, ?, ?);";
                }
            } else if (spawnType.equalsIgnoreCase("global")) {
                if (spawns.stream().anyMatch(s -> s.getType() == 1)) {
                    query = "UPDATE " + tablePrefix + "spawns SET server = ?, world = ?, x = ?, y = ?, z = ?, yaw = ?, pitch = ? WHERE type = ?;";
                } else {
                    query = "INSERT INTO " + tablePrefix + "spawns (server, world, x, y, z, yaw, pitch, type) VALUES (?, ?, ?, ?, ?, ?, ?, ?);";
                }
            } else {
                return false;
            }
        } else {
            if (spawnType.equalsIgnoreCase("server")) {
                if (spawns.stream().anyMatch(s -> s.getType() == 0 && s.getServer().equalsIgnoreCase(spawn.getServer()))) {
                    query = "UPDATE " + tablePrefix + "spawns SET server = ?, world = ?, x = ?, y = ?, z = ?, yaw = ?, pitch = ? WHERE type = ?;";
                } else {
                    query = "INSERT INTO " + tablePrefix + "spawns (server, world, x, y, z, yaw, pitch, type) VALUES (?, ?, ?, ?, ?, ?, ?, ?);";
                }
            } else if (spawnType.equalsIgnoreCase("global")) {
                if (spawns.stream().anyMatch(s -> s.getType() == 0)) {
                    query = "UPDATE " + tablePrefix + "spawns SET server = ?, world = ?, x = ?, y = ?, z = ?, yaw = ?, pitch = ? WHERE type = ?;";
                } else {
                    query = "INSERT INTO " + tablePrefix + "spawns (server, world, x, y, z, yaw, pitch, type) VALUES (?, ?, ?, ?, ?, ?, ?, ?);";

                }
            } else {
                return false;
            }
        }

        try {
            connection = db.hikari.getConnection();
            statement = connection.prepareStatement(query);
            statement.setString(1, spawn.getServer());
            statement.setString(2, spawn.getLocation().getWorld());
            statement.setDouble(3, spawn.getLocation().getX());
            statement.setDouble(4, spawn.getLocation().getY());
            statement.setDouble(5, spawn.getLocation().getZ());
            statement.setFloat(6, spawn.getLocation().getYaw());
            statement.setFloat(7, spawn.getLocation().getPitch());
            statement.setInt(8, spawn.getType());

            statement.execute();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
            }
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * List all spawns
     *
     * @return list all spawns
     */
    public Set<Spawn> all() {
        Set<Spawn> spawns = new HashSet<>();
        ResultSet rs = null;

        try {
            connection = db.hikari.getConnection();
            statement = connection.prepareStatement("SELECT * FROM " + tablePrefix + "spawns;");
            rs = statement.executeQuery();
            while (rs.next()) {
                Spawn spawn = new Spawn();
                spawn.setServer(rs.getString("server"));
                spawn.setLocation(new Location(rs.getString("world"), rs.getDouble("x"), rs.getDouble("y"), rs.getDouble("z"), rs.getFloat("yaw"), rs.getFloat("pitch")));
                spawn.setType(rs.getInt("type"));
                spawns.add(spawn);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return spawns;
    }

    /**
     * Deletes spawn
     *
     * @return if deleting was successful
     */
    public boolean delete() {
        String spawnType = config.load("teleports", "settings.yml").getString("spawn-type");
        String query = null;
        if (spawnType.equalsIgnoreCase("server")) {
            query = "DELETE FROM " + tablePrefix + "spawns WHERE type = ? AND server = ?";
        } else if (spawnType.equalsIgnoreCase("global")) {
            query = "DELETE FROM " + tablePrefix + "spawns WHERE type = ?";
        } else {
            return false;
        }

        try {
            connection = db.hikari.getConnection();
            statement = connection.prepareStatement(query);
            statement.setInt(1, getType());
            if (spawnType.equalsIgnoreCase("server")) {
                statement.setString(2, getServer());
            }
            statement.execute();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
