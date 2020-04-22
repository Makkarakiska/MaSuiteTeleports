package dev.masa.masuiteteleports.core.models;

import com.j256.ormlite.field.DatabaseField;
import dev.masa.masuitecore.core.objects.Location;
import dev.masa.masuiteteleports.core.objects.SpawnType;
import lombok.*;

import javax.persistence.*;

@RequiredArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "masuite_spawns")
@NamedQuery(
        name = "findSpawnByType",
        query = "SELECT s FROM Spawn s WHERE s.type = :type"
)

@NamedQuery(
        name = "findSpawnByTypeAndServer",
        query = "SELECT s FROM Spawn s WHERE s.type = :type AND s.location.server = :server"
)
public class Spawn {

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField
    private String server;
    @DatabaseField
    private String world;
    @DatabaseField
    private Double x;
    @DatabaseField
    private Double y;
    @DatabaseField
    private Double z;
    @DatabaseField
    private Float yaw = 0.0F;
    @DatabaseField
    private Float pitch = 0.0F;

    @NonNull
    @DatabaseField(columnDefinition = "smallint")
    private SpawnType type;

    public Spawn (Location location, SpawnType type) {
        this.setLocation(location);
        this.type = type;
    }

    public Location getLocation() {
        return new Location(server, world, x, y, z, yaw, pitch);
    }

    public void setLocation(Location loc) {
        this.server = loc.getServer();
        this.world = loc.getWorld();
        this.x = loc.getX();
        this.y = loc.getY();
        this.z = loc.getZ();
        this.yaw = loc.getYaw();
        this.pitch = loc.getPitch();
    }
}
