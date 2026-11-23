package twilightforest.structures.darktower;

public enum EnumDarkTowerDoor {

    VANISHING,
    REAPPEARING,
    LOCKED;

    private static final EnumDarkTowerDoor[] SERIALIZED_VALUES = values();

    public static EnumDarkTowerDoor fromSerializedId(int id) {
        return id >= 0 && id < SERIALIZED_VALUES.length ? SERIALIZED_VALUES[id] : VANISHING;
    }
}
