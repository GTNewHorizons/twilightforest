package twilightforest.world.layer;

import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.gen.layer.GenLayer;
import net.minecraft.world.gen.layer.IntCache;

import twilightforest.biomes.TFBiomeBase;

public class GenLayerTF7x7Preset extends GenLayer {

    private static final int MAP_SIZE = 9;
    private static final int ACTIVE_MAP_SIZE = 8;
    private static final int MAP_ORIGIN_OFFSET = 4;
    private static final char[][] DEFAULT_MAP = { { 'P', 'P', 'P', 'P', 'P', 'P', 'P', 'P', 'P' },
            { 'P', 'H', 'H', 'H', 'H', 'D', 'D', 'D', 'P' }, { 'P', 'H', 'H', 'H', 'H', 'D', 'D', 'D', 'P' },
            { 'P', 'F', 'f', 'F', 'm', 'M', 'D', 'D', 'P' }, { 'P', 'F', 'F', 'F', 'C', 'm', 'D', 'D', 'P' },
            { 'P', 'F', 'f', 'f', 'F', 'E', 'O', 'O', 'P' }, { 'P', 'S', 'S', 'S', 'L', 'L', 'O', 'G', 'P' },
            { 'P', 'Y', 'S', 'S', 'L', 'L', 'O', 'G', 'P' }, { 'P', 'P', 'P', 'P', 'P', 'P', 'P', 'P', 'P' } };

    BiomeGenBase[][] preset = new BiomeGenBase[MAP_SIZE][MAP_SIZE];

    public GenLayerTF7x7Preset(long seed) {
        this(seed, DEFAULT_MAP);
    }

    protected GenLayerTF7x7Preset(long seed, char[][] map) {
        super(seed);
        loadPreset(map);
    }

    @Override
    public int[] getInts(int x, int z, int width, int depth) {
        int[] dest = IntCache.getIntCache(width * depth);
        for (int dz = 0; dz < depth; dz++) {
            for (int dx = 0; dx < width; dx++) {
                int sx = x + dx + MAP_ORIGIN_OFFSET;
                int sz = z + dz + MAP_ORIGIN_OFFSET;

                // the 9th row and column are padding, keep the original eight cell active range
                if (sx >= 0 && sx < ACTIVE_MAP_SIZE && sz >= 0 && sz < ACTIVE_MAP_SIZE) {
                    dest[dx + dz * width] = preset[sx][sz].biomeID;
                } else {
                    dest[dx + dz * width] = BiomeGenBase.ocean.biomeID;
                }
            }

        }

        return dest;

    }

    protected final void loadPreset(char[][] map) {
        for (int x = 0; x < map.length; x++) {
            for (int z = 0; z < map[x].length; z++) {
                preset[x][z] = getBiomeFor(map[z][x]);
            }
        }

    }

    protected BiomeGenBase getBiomeFor(char c) {
        return switch (c) {
            default -> BiomeGenBase.ocean;
            // case 'P' :
            // return BiomeGenBase.plains;
            case 'F' -> TFBiomeBase.twilightForest;
            case 'f' -> TFBiomeBase.twilightForest2;
            case 'E' -> TFBiomeBase.enchantedForest;
            case 'm' -> TFBiomeBase.mushrooms;
            case 'M' -> TFBiomeBase.deepMushrooms;
            case 'C' -> TFBiomeBase.clearing;
            case 'S' -> TFBiomeBase.tfSwamp;
            case 'Y' -> TFBiomeBase.fireSwamp;
            case 'D' -> TFBiomeBase.darkForest;
            case 'L' -> TFBiomeBase.tfLake;
            case 'O' -> TFBiomeBase.tfSnow;
            case 'G' -> TFBiomeBase.glacier;
            case 'H' -> TFBiomeBase.highlands;
        };
    }

}
