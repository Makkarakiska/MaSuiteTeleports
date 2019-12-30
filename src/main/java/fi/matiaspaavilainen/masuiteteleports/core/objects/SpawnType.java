package fi.matiaspaavilainen.masuiteteleports.core.objects;

import lombok.Getter;

public enum SpawnType {
    DEFAULT(0),
    FIRST(1);

    @Getter
    private final int spawnType;

    private SpawnType(int spawnType) {
        this.spawnType = spawnType;
    }

    public static SpawnType getType(int type) {
        for (SpawnType l : SpawnType.values()) {
            if (l.spawnType == type) return l;
        }
        throw new IllegalArgumentException("Unknown spawn type! Type: " + type);
    }
}
