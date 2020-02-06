package dev.masa.masuiteteleports.core.models;

import dev.masa.masuitecore.core.objects.Location;
import dev.masa.masuiteteleports.core.objects.SpawnType;
import lombok.*;

import javax.persistence.*;

@RequiredArgsConstructor
@NoArgsConstructor
@Data
@Entity
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

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Embedded
    @AttributeOverrides(value = {
            @AttributeOverride(name = "server", column = @Column(name = "server")),
            @AttributeOverride(name = "x", column = @Column(name = "x")),
            @AttributeOverride(name = "y", column = @Column(name = "y")),
            @AttributeOverride(name = "z", column = @Column(name = "z")),
            @AttributeOverride(name = "yaw", column = @Column(name = "yaw")),
            @AttributeOverride(name = "pitch", column = @Column(name = "pitch"))
    })
    @NonNull
    private Location location;

    @Column(name = "type", columnDefinition = "smallint")
    @NonNull
    private SpawnType type;

}
