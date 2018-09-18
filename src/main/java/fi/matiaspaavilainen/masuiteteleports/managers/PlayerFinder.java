package fi.matiaspaavilainen.masuiteteleports.managers;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class PlayerFinder {

    public ProxiedPlayer get(String name) {
        try {
            if(ProxyServer.getInstance().getPlayers().stream().anyMatch(proxiedPlayer -> proxiedPlayer.getName().toLowerCase().startsWith(name.toLowerCase()))){
                return ProxyServer.getInstance().getPlayers().stream().filter(proxiedPlayer -> proxiedPlayer.getName().toLowerCase().startsWith(name.toLowerCase())).findFirst().get();
            }else{
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}

