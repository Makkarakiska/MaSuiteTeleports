package fi.matiaspaavilainen.masuiteteleports.commands.spawns;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class Delete extends Command {
    public Delete() {
        super("setspawn","masuiteteleports.spawn.set", "spawnset", "createspawn");
    }

    @Override
    public void execute(CommandSender cs, String[] args) {
        if(!(cs instanceof ProxiedPlayer)) {return;}
        if(args.length == 1){

        }else{

        }
    }
}
