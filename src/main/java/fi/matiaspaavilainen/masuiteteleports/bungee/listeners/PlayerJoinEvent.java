package fi.matiaspaavilainen.masuiteteleports.bungee.listeners;

import fi.matiaspaavilainen.masuitecore.core.configuration.BungeeConfiguration;
import fi.matiaspaavilainen.masuiteteleports.bungee.MaSuiteTeleports;
import fi.matiaspaavilainen.masuiteteleports.bungee.commands.SpawnCommand;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

public class PlayerJoinEvent implements Listener {

    private MaSuiteTeleports plugin;
    private BungeeConfiguration config = new BungeeConfiguration();
    private String tablePrefix = config.load(null, "config.yml").getString("database.table-prefix");

    public PlayerJoinEvent(MaSuiteTeleports plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PostLoginEvent e) {
        if (config.load("teleports", "settings.yml").getBoolean("enable-first-spawn") && !hasPlayedBefore(e.getPlayer())) {
            ProxyServer.getInstance().getScheduler().schedule(plugin, () -> new SpawnCommand(plugin).spawn(e.getPlayer(), 1), 500, TimeUnit.MILLISECONDS);
        } else if (config.load("teleports", "settings.yml").getBoolean("spawn-on-join")) {
            ProxyServer.getInstance().getScheduler().schedule(plugin, () -> new SpawnCommand(plugin).spawn(e.getPlayer(), 0), 500, TimeUnit.MILLISECONDS);
        }
    }

    private boolean hasPlayedBefore(ProxiedPlayer p) {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet rs = null;
        boolean empty = false;
        try {
            connection = MaSuiteTeleports.db.hikari.getConnection();
            statement = connection.prepareStatement("SELECT * FROM " + tablePrefix + "players WHERE uuid = ?;");
            statement.setString(1, p.getUniqueId().toString());
            rs = statement.executeQuery();
            while (rs.next()) {
                empty = true;
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
        return empty;
    }
}
