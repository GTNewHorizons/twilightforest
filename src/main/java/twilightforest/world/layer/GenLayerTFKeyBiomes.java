package twilightforest.world.layer;

import net.minecraft.world.gen.layer.GenLayer;
import net.minecraft.world.gen.layer.IntCache;

import twilightforest.biomes.TFBiomeBase;

/**
 * Puts key biomes in the proper positions
 *
 * @author Ben
 *
 */
public class GenLayerTFKeyBiomes extends GenLayer {

    private static final int GRID_SIZE = 4;
    private static final int GRID_MASK = GRID_SIZE - 1;
    private static final int REGION_SHIFT = 3;
    private static final int KEY_BIOME_COUNT = 4;

    public GenLayerTFKeyBiomes(long l, GenLayer genlayer) {
        super(l);
        parent = genlayer;
    }

    public GenLayerTFKeyBiomes(long l) {
        super(l);
    }

    @Override
    public int[] getInts(int x, int z, int width, int depth) {
        int[] src = parent.getInts(x, z, width, depth);
        int[] dest = IntCache.getIntCache(width * depth);
        System.arraycopy(src, 0, dest, 0, width * depth);

        if (width == 0 || depth == 0) {
            return dest;
        }

        long endX = (long) x + width;
        long endZ = (long) z + depth;

        // each 4x4 grid has exactly one possible key-biome position
        for (long gridZ = (long) z & ~((long) GRID_MASK); gridZ < endZ; gridZ += GRID_SIZE) {
            for (long gridX = (long) x & ~((long) GRID_MASK); gridX < endX; gridX += GRID_SIZE) {
                // get offsets
                initChunkSeed((int) (gridX | GRID_MASK), (int) (gridZ | GRID_MASK));
                int keyX = (int) gridX + nextInt(GRID_MASK) + 1;
                int keyZ = (int) gridZ + nextInt(GRID_MASK) + 1;

                int dx = keyX - x;
                int dz = keyZ - z;
                if (dx < 0 || dx >= width || dz < 0 || dz >= depth) {
                    continue;
                }

                // determine which of the 4
                int index = ((keyX & GRID_SIZE) >>> 1) | ((keyZ & GRID_SIZE) >>> 2);
                dest[dx + dz * width] = getKeyBiomeFor(keyX, keyZ, index);
            }
        }

        return dest;
    }

    /**
     * Determine which map "region" the specified points are in. Assign the 0-3 of the index to the key biomes based on
     * that region.
     */
    private int getKeyBiomeFor(int mapX, int mapZ, int index) {

        int regionX = (mapX + GRID_SIZE) >> REGION_SHIFT;
        int regionZ = (mapZ + GRID_SIZE) >> REGION_SHIFT;

        initChunkSeed(regionX, regionZ);
        int offset = nextInt(KEY_BIOME_COUNT);

        // do we need to shuffle this better?
        // the current version just "rotates" the 4 key biomes
        return switch ((index + offset) % KEY_BIOME_COUNT) {
            default -> TFBiomeBase.glacier.biomeID;
            case 1 -> TFBiomeBase.fireSwamp.biomeID;
            case 2 -> TFBiomeBase.darkForestCenter.biomeID;
            case 3 -> TFBiomeBase.highlandsCenter.biomeID;
        };
    }
}
