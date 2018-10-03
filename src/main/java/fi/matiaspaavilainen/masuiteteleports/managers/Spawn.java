package fi.matiaspaavilainen.masuiteteleports.managers;

import fi.matiaspaavilainen.masuitecore.Debugger;
import fi.matiaspaavilainen.masuitecore.chat.Formator;
import fi.matiaspaavilainen.masuitecore.config.Configuration;
import fi.matiaspaavilainen.masuiteteleports.database.Database;
import fi.matiaspaavilainen.masuitecore.managers.Location;
import fi.matiaspaavilainen.masuiteteleports.MaSuiteTeleports;
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

public class Spawn {

    private Database db = MaSuiteTeleports.db;
    private String server;
    private Location location;
    private Connection connection = null;
    private PreparedStatement statement = null;
    private Configuration config = new Configuration();
    private String tablePrefix = config.load(null, "config.yml").getString("database.table-prefix");
    private Debugger debugger = new Debugger();

    public Spawn(){}

    public Spawn(String server, Location location) {
        this.server = server;
        this.location = location;
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

    public Spawn find(String server){
        Spawn spawn = new Spawn();
        ResultSet rs = null;

        try {
            connection = db.hikari.getConnection();
            statement = connection.prepareStatement("SELECT * FROM " +  tablePrefix +"spawns WHERE server = ?");
            statement.setString(1, server);
            rs = statement.executeQuery();

            if(rs == null){
                return new Spawn();
            }
            while (rs.next()) {
                spawn.setServer(server);
                spawn.setLocation(new Location(rs.getString("world"), rs.getDouble("x"), rs.getDouble("y"), rs.getDouble("z"), rs.getFloat("yaw"), rs.getFloat("pitch")));
                debugger.sendMessage("[MaSuiteTeleports] [Spawn] spawn loaded.");
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

    public Boolean spawn(ProxiedPlayer p){
        Spawn spawn = new Spawn();
        spawn = spawn.find(p.getServer().getInfo().getName());
        if(spawn.getServer() == null){
            new Formator().sendMessage(p, config.load("teleports", "messages.yml").getString("spawn.not-found"));
            return false;
        }
        try{
            ByteArrayOutputStream b = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(b);
            out.writeUTF("Teleport");
            out.writeUTF("SpawnPlayer");
            out.writeUTF(String.valueOf(p.getUniqueId()));
            out.writeUTF(spawn.getLocation().getWorld());
            out.writeDouble(spawn.getLocation().getX());
            out.writeDouble(spawn.getLocation().getY());
            out.writeDouble(spawn.getLocation().getZ());
            out.writeFloat(spawn.getLocation().getYaw());
            out.writeFloat(spawn.getLocation().getPitch());
            p.getServer().sendData("BungeeCord", b.toByteArray());
        } catch (IOException e){
            System.out.println(e.getMessage());
        }
        return true;
    }

    public Spawn create(Spawn spawn) {
        String insert = "INSERT INTO " + tablePrefix +
                "spawns (server, world, x, y, z, yaw, pitch) VALUES (?, ?, ?, ?, ?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE world = ?, x = ?, y = ?, z = ?, yaw = ?, pitch = ?;";
        try {
            connection = db.hikari.getConnection();
            statement = connection.prepareStatement(insert);
            statement.setString(1, spawn.getServer());
            statement.setString(2, spawn.getLocation().getWorld());
            statement.setDouble(3, spawn.getLocation().getX());
            statement.setDouble(4, spawn.getLocation().getY());
            statement.setDouble(5, spawn.getLocation().getZ());
            statement.setFloat(6, spawn.getLocation().getYaw());
            statement.setFloat(7, spawn.getLocation().getPitch());
            statement.setString(8, spawn.getLocation().getWorld());
            statement.setDouble(9, spawn.getLocation().getX());
            statement.setDouble(10, spawn.getLocation().getY());
            statement.setDouble(11, spawn.getLocation().getZ());
            statement.setFloat(12, spawn.getLocation().getYaw());
            statement.setFloat(13, spawn.getLocation().getPitch());
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
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
        return spawn;
    }
    public Set<Spawn> all(){
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

    public Boolean delete(ProxiedPlayer p){
        try {
            connection = db.hikari.getConnection();
            statement = connection.prepareStatement("DELETE FROM " + tablePrefix + "spawns WHERE server = ?");
            statement.setString(1, p.getServer().getInfo().getName());
            statement.execute();

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
            new Formator().sendMessage(p, config.load("teleports", "messages.yml").getString("spawn.deleted"));
        }
        return true;
    }
}
