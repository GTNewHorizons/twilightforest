package twilightforest.world.layer;

import net.minecraft.world.gen.layer.GenLayer;
import net.minecraft.world.gen.layer.IntCache;

import twilightforest.biomes.TFBiomeBase;

public class GenLayerTFBiomeBorders extends GenLayer {

    private static final int BORDER_RADIUS = 1;

    public GenLayerTFBiomeBorders(long l, GenLayer genlayer) {
        super(l);
        parent = genlayer;
    }

    /**
     * Turn specific biomes into other biomes along their borders
     */
    @Override
    public int[] getInts(int x, int z, int width, int depth) {
        int inputWidth = width + BORDER_RADIUS * 2;
        int[] input = parent.getInts(x - BORDER_RADIUS, z - BORDER_RADIUS, inputWidth, depth + BORDER_RADIUS * 2);
        int[] output = IntCache.getIntCache(width * depth);

        int lakeId = TFBiomeBase.tfLake.biomeID;
        int clearingId = TFBiomeBase.clearing.biomeID;
        int deepMushroomsId = TFBiomeBase.deepMushrooms.biomeID;
        int glacierId = TFBiomeBase.glacier.biomeID;
        int fireflyForestId = TFBiomeBase.fireflyForest.biomeID;
        int oakSavannaId = TFBiomeBase.oakSavanna.biomeID;
        int mushroomsId = TFBiomeBase.mushrooms.biomeID;
        int snowId = TFBiomeBase.tfSnow.biomeID;

        for (int dz = 0; dz < depth; dz++) {
            int inputRow = (dz + BORDER_RADIUS) * inputWidth + BORDER_RADIUS;
            int outputRow = dz * width;

            for (int dx = 0; dx < width; dx++) {
                int index = inputRow + dx;
                int center = input[index];
                int borderBiome;

                if (center == lakeId) {
                    borderBiome = fireflyForestId;
                } else if (center == clearingId) {
                    borderBiome = oakSavannaId;
                } else if (center == deepMushroomsId) {
                    borderBiome = mushroomsId;
                } else if (center == glacierId) {
                    borderBiome = snowId;
                } else {
                    output[outputRow + dx] = center;
                    continue;
                }

                if (onBorder(
                        center,
                        center,
                        input[index - 1],
                        input[index + 1],
                        input[index - inputWidth],
                        input[index + inputWidth])) {
                    output[outputRow + dx] = borderBiome;
                } else {
                    output[outputRow + dx] = center;
                }
            }
        }

        return output;
    }

    /**
     * Returns true if the center biome is the specified biome and any of the surrounding biomes are not
     */
    boolean onBorder(int biome, int center, int right, int left, int up, int down) {
        return center == biome && (right != biome || left != biome || up != biome || down != biome);
    }
}
