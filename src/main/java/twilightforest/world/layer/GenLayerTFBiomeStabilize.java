package twilightforest.world.layer;

import net.minecraft.world.gen.layer.GenLayer;
import net.minecraft.world.gen.layer.IntCache;

public class GenLayerTFBiomeStabilize extends GenLayer {

    private static final int GRID_MASK = 3;
    private static final int UNSTABILIZED_OFFSET = 2;
    private static final int NEIGHBOR_RADIUS = 1;

    public GenLayerTFBiomeStabilize(long l, GenLayer genlayer) {
        super(l);
        parent = genlayer;
    }

    /**
     * When we are near the center of each biome, make nearby areas that biome too
     */
    @Override
    public int[] getInts(int x, int z, int width, int depth) {
        int parentWidth = width + NEIGHBOR_RADIUS * 2;
        int[] input = parent
                .getInts(x - NEIGHBOR_RADIUS, z - NEIGHBOR_RADIUS, parentWidth, depth + NEIGHBOR_RADIUS * 2);
        int[] output = IntCache.getIntCache(width * depth);

        for (int dz = 0; dz < depth; dz++) {
            int inputRow = (dz + NEIGHBOR_RADIUS) * parentWidth + NEIGHBOR_RADIUS;
            int outputRow = dz * width;
            int zOffset = (z + dz) & GRID_MASK;

            // offset 2 lies outside the 3x3 area around every four block grid center
            if (zOffset == UNSTABILIZED_OFFSET) {
                System.arraycopy(input, inputRow, output, outputRow, width);
                continue;
            }

            int centerZ = dz + NEIGHBOR_RADIUS + getCenterOffset(zOffset);
            int centerRow = centerZ * parentWidth;

            for (int dx = 0; dx < width; dx++) {
                int xOffset = (x + dx) & GRID_MASK;
                if (xOffset == UNSTABILIZED_OFFSET) {
                    output[outputRow + dx] = input[inputRow + dx];
                } else {
                    int centerX = dx + NEIGHBOR_RADIUS + getCenterOffset(xOffset);
                    output[outputRow + dx] = input[centerRow + centerX];
                }
            }
        }

        return output;
    }

    private static int getCenterOffset(int offset) {
        return offset == GRID_MASK ? 1 : -offset;
    }
}
