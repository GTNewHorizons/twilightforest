package twilightforest.structures.hollowtree;

import java.util.ArrayList;
import java.util.List;

final class HollowTreeBlobGeometry {

    private static final int MAX_CACHED_RADIUS = 4;
    private static final int[][][] CACHED_OFFSETS = new int[MAX_CACHED_RADIUS + 1][][];

    static {
        for (int radius = 0; radius <= MAX_CACHED_RADIUS; radius++) {
            CACHED_OFFSETS[radius] = calculateOffsets(radius);
        }
    }

    private HollowTreeBlobGeometry() {}

    static int[][] getOffsets(int radius) {
        if (radius < 0) {
            return new int[0][];
        }
        return radius <= MAX_CACHED_RADIUS ? CACHED_OFFSETS[radius] : calculateOffsets(radius);
    }

    private static int[][] calculateOffsets(int radius) {
        List<int[]> offsets = new ArrayList<>();
        for (int dx = 0; dx <= radius; dx++) {
            for (int dy = 0; dy <= radius; dy++) {
                for (int dz = 0; dz <= radius; dz++) {
                    int largest = Math.max(dx, Math.max(dy, dz));
                    int smallest = Math.min(dx, Math.min(dy, dz));
                    int middle = dx + dy + dz - largest - smallest;
                    if (largest + (middle * 2 + smallest) / 4 > radius) {
                        continue;
                    }

                    // generate each coordinate once even when it lies on a symmetry plane
                    for (int ySign = 1; ySign >= (dy == 0 ? 1 : -1); ySign -= 2) {
                        for (int xSign = 1; xSign >= (dx == 0 ? 1 : -1); xSign -= 2) {
                            for (int zSign = 1; zSign >= (dz == 0 ? 1 : -1); zSign -= 2) {
                                offsets.add(new int[] { dx * xSign, dy * ySign, dz * zSign });
                            }
                        }
                    }
                }
            }
        }
        return offsets.toArray(new int[0][]);
    }
}
