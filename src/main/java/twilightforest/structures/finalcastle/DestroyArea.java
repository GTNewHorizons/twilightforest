package twilightforest.structures.finalcastle;

import java.util.List;
import java.util.Random;

import net.minecraft.world.gen.structure.StructureBoundingBox;

// An area that we're going to destroy. Default is just a StructureBoundingBox
public class DestroyArea {

    private static final int MAX_ATTEMPTS = 100;
    private final StructureBoundingBox destroyBox;

    public DestroyArea(StructureBoundingBox tower, Random rand, int y) {
        // 5x5 area with the legacy 2 block overhang around the tower
        int bx = tower.minX - 2 + rand.nextInt(tower.getXSize());
        int bz = tower.minZ - 2 + rand.nextInt(tower.getZSize());
        this.destroyBox = new StructureBoundingBox(bx, y - 10, bz, bx + 4, y, bz + 4);
    }

    public boolean isEntirelyAbove(int y) {
        return this.destroyBox.minY > y;
    }

    public boolean isVecInside(int x, int y, int z) {
        return this.destroyBox.isVecInside(x, y, z);
    }

    // construct a new area that does not intersect any other areas in the list
    public static DestroyArea createNonIntersecting(StructureBoundingBox tower, Random rand, int y,
            List<DestroyArea> otherAreas) {
        for (int attempt = 0; attempt < MAX_ATTEMPTS; attempt++) {
            DestroyArea candidate = new DestroyArea(tower, rand, y);
            boolean intersects = false;
            for (DestroyArea other : otherAreas) {
                if (other != null && candidate.intersectsWith(other)) {
                    intersects = true;
                    break;
                }
            }
            if (!intersects) {
                return candidate;
            }
        }
        return null;
    }

    // check if the box would intersect even if it was one block larger in the x and z directions
    private boolean intersectsWith(DestroyArea otherArea) {
        return this.destroyBox.intersectsWith(
                otherArea.destroyBox.minX - 1,
                otherArea.destroyBox.minZ - 1,
                otherArea.destroyBox.maxX + 1,
                otherArea.destroyBox.maxZ + 1);
    }
}
