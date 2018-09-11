package fi.matiaspaavilainen.masuiteteleports.managers;

import fi.matiaspaavilainen.masuitecore.Debugger;
import fi.matiaspaavilainen.masuitecore.MaSuiteCore;
import fi.matiaspaavilainen.masuitecore.config.Configuration;
import fi.matiaspaavilainen.masuitecore.managers.Location;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Spawn {

    private String server;
    private Location location;
    private Connection connection = null;
    private PreparedStatement statement = null;
    private Configuration config = new Configuration();
    private String tablePrefix = config.load(null, "config.yml").getString("database.table-prefix");
    Debugger debugger = new Debugger();

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

    private Spawn find(String server){
        Spawn spawn = new Spawn();
        ResultSet rs = null;

        try {
            connection = MaSuiteCore.db.hikari.getConnection();
            statement = connection.prepareStatement("SELECT * FROM " +  tablePrefix +"spawns WHERE = ?");
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

    public void spawn(ProxiedPlayer p){
        Spawn spawn = new Spawn();
        spawn = spawn.find(p.getServer().getInfo().getName());
        try{
            ByteArrayOutputStream b = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(b);
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
    }
}
