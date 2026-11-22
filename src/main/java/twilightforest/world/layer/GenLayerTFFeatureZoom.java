package twilightforest.world.layer;

import net.minecraft.world.gen.layer.GenLayer;
import net.minecraft.world.gen.layer.IntCache;

public class GenLayerTFFeatureZoom extends GenLayer {

    public GenLayerTFFeatureZoom(long l, GenLayer genlayer) {
        super(l);
        this.parent = genlayer;
    }

    @Override
    public int[] getInts(int x, int z, int width, int depth) {
        int sourceX = x >> 1;
        int sourceZ = z >> 1;
        int sourceWidth = (width >> 1) + 3;
        int sourceDepth = (depth >> 1) + 3;
        int[] source = parent.getInts(sourceX, sourceZ, sourceWidth, sourceDepth);
        int[] output = IntCache.getIntCache(width * depth);

        // only even/even expanded positions contain a feature, all other positions are zero
        // preserve the original negative coord parity and parent sampling window
        int xParity = x & 1;
        int zParity = z & 1;
        for (int dz = 0; dz < depth; dz++) {
            int expandedZ = dz + zParity;
            int outputRow = dz * width;
            if ((expandedZ & 1) != 0) {
                for (int dx = 0; dx < width; dx++) {
                    output[outputRow + dx] = 0;
                }
                continue;
            }

            int sourceRow = (expandedZ >> 1) * sourceWidth;
            for (int dx = 0; dx < width; dx++) {
                int expandedX = dx + xParity;
                output[outputRow + dx] = (expandedX & 1) == 0 ? source[sourceRow + (expandedX >> 1)] : 0;
            }
        }
        return output;
    }

    public static GenLayer multipleZoom(long seed, GenLayer genlayer, int loops) {
        GenLayer layer = genlayer;
        for (int i = 0; i < loops; i++) {
            layer = new GenLayerTFFeatureZoom(seed + (long) i, layer);
        }

        return layer;
    }
}
