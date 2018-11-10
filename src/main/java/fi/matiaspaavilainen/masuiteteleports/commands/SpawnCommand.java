package fi.matiaspaavilainen.masuiteteleports.commands;

import fi.matiaspaavilainen.masuitecore.chat.Formator;
import fi.matiaspaavilainen.masuitecore.config.Configuration;
import fi.matiaspaavilainen.masuitecore.managers.Location;
import fi.matiaspaavilainen.masuiteteleports.MaSuiteTeleports;
import fi.matiaspaavilainen.masuiteteleports.managers.Spawn;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class SpawnCommand {

    private MaSuiteTeleports plugin;
    public SpawnCommand(MaSuiteTeleports p){
        plugin = p;
    }
    private Formator formator = new Formator();
    private Configuration config = new Configuration();

    public void spawn(ProxiedPlayer p){
        if(p == null){
            return;
        }
        Spawn spawn = new Spawn();
        if(spawn.spawn(p, plugin)){formator.sendMessage(p, config.load("teleports", "messages.yml").getString("spawn.teleported"));}
    }

    public void setSpawn(ProxiedPlayer p, Location loc, String type){
        if(p == null){
            return;
        }
        Spawn spawn = new Spawn(p.getServer().getInfo().getName(), loc, type.equals("default") ? 1 : 0);
        if(spawn.create(spawn)){
            formator.sendMessage(p, config.load("teleports", "messages.yml").getString("spawn.set"));
        } else {
            System.out.println("[MaSuite] [Teleports] [Spawn] Error while creating spawn.");
        }

    }
    public void deleteSpawn(ProxiedPlayer p){
        if(p == null){
            return;
        }
        Spawn spawn = new Spawn();
        if(spawn.find(p.getServer().getInfo().getName()) != null){
            if(spawn.delete(p.getServer().getInfo().getName())){
                formator.sendMessage(p, config.load("teleports", "messages.yml").getString("spawn.deleted"));
            }else {
                System.out.println("[MaSuite] [Teleports] [Spawn] Error while deleting spawn.");
            }
        }else{
            formator.sendMessage(p, config.load("teleports", "messages.yml").getString("spawn.not-found"));
        }
    }
}
