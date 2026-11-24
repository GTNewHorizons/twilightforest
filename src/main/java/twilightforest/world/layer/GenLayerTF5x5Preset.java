package twilightforest.world.layer;

public class GenLayerTF5x5Preset extends GenLayerTF7x7Preset {

    private static final char[][] PRESET_MAP = { { 'P', 'P', 'P', 'P', 'P', 'P', 'P', 'P', 'P' },
            { 'P', 'P', 'P', 'P', 'P', 'P', 'P', 'P', 'P' }, { 'P', 'P', 'H', 'H', 'L', 'D', 'D', 'P', 'P' },
            { 'P', 'P', 'H', 'H', 'm', 'M', 'D', 'P', 'P' }, { 'P', 'P', 'F', 'F', 'F', 'C', 'E', 'P', 'P' },
            { 'P', 'P', 'S', 'f', 'F', 'F', 'O', 'P', 'P' }, { 'P', 'P', 'Y', 'S', 'F', 'O', 'G', 'P', 'P' },
            { 'P', 'P', 'P', 'P', 'P', 'P', 'P', 'P', 'P' }, { 'P', 'P', 'P', 'P', 'P', 'P', 'P', 'P', 'P' } };

    public GenLayerTF5x5Preset(long seed) {
        super(seed, PRESET_MAP);
    }

}
