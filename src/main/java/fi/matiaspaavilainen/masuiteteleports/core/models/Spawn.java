package fi.matiaspaavilainen.masuiteteleports.core.models;

import fi.matiaspaavilainen.masuitecore.core.objects.Location;
import lombok.*;

import javax.persistence.*;

@AllArgsConstructor
@RequiredArgsConstructor
@NoArgsConstructor
@Data
@Entity

@NamedQuery(
        name = "findSpawnByType",
        query = "SELECT s FROM Spawn s WHERE s.type = :type"
)

@NamedQuery(
        name = "findSpawnByTypeAndServer",
        query = "SELECT s FROM Spawn s WHERE s.type = :type AND s.server = :server"
)
public class Spawn {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Embedded
    @AttributeOverrides(value = {
            @AttributeOverride(name = "x", column = @Column(name = "x")),
            @AttributeOverride(name = "y", column = @Column(name = "y")),
            @AttributeOverride(name = "z", column = @Column(name = "z")),
            @AttributeOverride(name = "yaw", column = @Column(name = "yaw")),
            @AttributeOverride(name = "pitch", column = @Column(name = "pitch")),
            @AttributeOverride(name = "server", column = @Column(name = "server"))
    })
    @NonNull
    private Location location;

    @Column(name = "type")
    @NonNull
    private int type;

}
