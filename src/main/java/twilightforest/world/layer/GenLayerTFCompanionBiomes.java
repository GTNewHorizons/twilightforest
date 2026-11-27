package twilightforest.world.layer;

import net.minecraft.world.gen.layer.GenLayer;
import net.minecraft.world.gen.layer.IntCache;

import twilightforest.biomes.TFBiomeBase;

public class GenLayerTFCompanionBiomes extends GenLayer {

    private static final int NEIGHBOR_RADIUS = 1;

    public GenLayerTFCompanionBiomes(long l, GenLayer genlayer) {
        super(l);
        parent = genlayer;
    }

    // adjacent key biomes determine the companion biome, remains original priority order
    @Override
    public int[] getInts(int x, int z, int width, int depth) {
        int parentWidth = width + NEIGHBOR_RADIUS * 2;
        int parentDepth = depth + NEIGHBOR_RADIUS * 2;
        int[] input = parent.getInts(x - NEIGHBOR_RADIUS, z - NEIGHBOR_RADIUS, parentWidth, parentDepth);
        int[] output = IntCache.getIntCache(width * depth);

        int fireSwampId = TFBiomeBase.fireSwamp.biomeID;
        int glacierId = TFBiomeBase.glacier.biomeID;
        int darkForestCenterId = TFBiomeBase.darkForestCenter.biomeID;
        int highlandsCenterId = TFBiomeBase.highlandsCenter.biomeID;
        int swampId = TFBiomeBase.tfSwamp.biomeID;
        int snowId = TFBiomeBase.tfSnow.biomeID;
        int darkForestId = TFBiomeBase.darkForest.biomeID;
        int highlandsId = TFBiomeBase.highlands.biomeID;

        for (int dz = 0; dz < depth; dz++) {
            int inputRow = (dz + NEIGHBOR_RADIUS) * parentWidth + NEIGHBOR_RADIUS;
            int outputRow = dz * width;

            for (int dx = 0; dx < width; dx++) {
                int index = inputRow + dx;
                int center = input[index];
                int left = input[index - NEIGHBOR_RADIUS];
                int right = input[index + NEIGHBOR_RADIUS];
                int up = input[index - parentWidth];
                int down = input[index + parentWidth];

                if (isKey(fireSwampId, center, right, left, up, down)) {
                    output[outputRow + dx] = swampId;
                } else if (isKey(glacierId, center, right, left, up, down)) {
                    output[outputRow + dx] = snowId;
                } else if (isKey(darkForestCenterId, center, right, left, up, down)) {
                    output[outputRow + dx] = darkForestId;
                } else if (isKey(highlandsCenterId, center, right, left, up, down)) {
                    output[outputRow + dx] = highlandsId;
                } else {
                    output[outputRow + dx] = center;
                }
            }
        }

        return output;
    }

    /**
     * Returns true if any of the surrounding biomes is the specified biome
     */
    boolean isKey(int biome, int center, int right, int left, int up, int down) {
        return center != biome && (right == biome || left == biome || up == biome || down == biome);
    }
}
