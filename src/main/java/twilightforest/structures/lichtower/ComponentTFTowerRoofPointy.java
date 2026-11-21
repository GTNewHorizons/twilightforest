
package twilightforest.structures.lichtower;

import java.util.Random;

import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;

public class ComponentTFTowerRoofPointy extends ComponentTFTowerRoof {

    public ComponentTFTowerRoofPointy() {
        super();
    }

    public ComponentTFTowerRoofPointy(int i, ComponentTFTowerWing wing) {
        super(i, wing);

        // same facing, but it doesn't matter
        setCoordBaseMode(wing.getCoordBaseMode());

        size = wing.size; // assuming only square towers and roofs right now
        height = size;

        // just hang out at the very top of the tower
        makeCapBB(wing);
    }

    // makes a pointy roof out of stuff
    @Override
    public boolean addComponentParts(World world, Random rand, StructureBoundingBox sbb) {
        final int slabMeta = 2;

        for (int y = 0; y <= height; y++) {
            int slopeChange = slopeChangeForSize(size);
            int inset = y < slopeChange ? y : (y + slopeChange) / 2;

            int min = inset;
            int max = size - inset - 1;
            int mid = min + (max - min) / 2;

            for (int x = min; x <= max; x++) {
                for (int z = min; z <= max; z++) {
                    placeBlockAtCurrentPosition(world, Blocks.planks, slabMeta, x, y, z, sbb);

                    boolean xEdge = x == min || x == max;
                    boolean zEdge = z == min || z == max;

                    // some of these are unnecessary and will just be overwritten by a normal block, but whatevs.
                    if (xEdge && zEdge) {
                        placeBlockAtCurrentPosition(world, Blocks.wooden_slab, slabMeta, x, y + 1, z, sbb);
                    }

                    // mid blocks
                    if (mid != min + 1 && ((xEdge && z == mid && x % 2 == 0) || (zEdge && x == mid && z % 2 == 0))) {
                        placeBlockAtCurrentPosition(world, Blocks.wooden_slab, slabMeta, x, y + 1, z, sbb);
                    }
                }
            }
        }

        return true;
    }

    public int slopeChangeForSize(int pSize) {
        if (pSize > 10) {
            return 3;
        }
        if (pSize > 6) {
            return 2;
        }
        return 1;
    }
}
