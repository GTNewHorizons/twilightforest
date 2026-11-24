package twilightforest.world.layer;

import net.minecraft.world.gen.layer.GenLayer;
import net.minecraft.world.gen.layer.IntCache;

import twilightforest.biomes.TFBiomeBase;

public class GenLayerTFThornBorder extends GenLayer {

    private static final int BORDER_RADIUS = 1;

    public GenLayerTFThornBorder(long l, GenLayer genlayer) {
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

        int highlandsCenterId = TFBiomeBase.highlandsCenter.biomeID;
        int thornlandsId = TFBiomeBase.thornlands.biomeID;

        for (int dz = 0; dz < depth; dz++) {
            int inputRow = (dz + BORDER_RADIUS) * inputWidth + BORDER_RADIUS;
            int outputRow = dz * width;

            for (int dx = 0; dx < width; dx++) {
                int index = inputRow + dx;
                int center = input[index];

                // thorn border requires also checking diagonally
                if (center != highlandsCenterId && touchesBiome(input, index, inputWidth, highlandsCenterId)) {
                    output[outputRow + dx] = thornlandsId;
                } else {
                    output[outputRow + dx] = center;
                }
            }
        }

        return output;
    }

    private static boolean touchesBiome(int[] biomes, int index, int stride, int biomeId) {
        return biomes[index - 1] == biomeId || biomes[index + 1] == biomeId
                || biomes[index - stride] == biomeId
                || biomes[index + stride] == biomeId
                || biomes[index - stride - 1] == biomeId
                || biomes[index - stride + 1] == biomeId
                || biomes[index + stride - 1] == biomeId
                || biomes[index + stride + 1] == biomeId;
    }
}
