package twilightforest.structures.lichtower;

import java.util.Random;

import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;

public class ComponentTFTowerRoofStairs extends ComponentTFTowerRoof {

    public ComponentTFTowerRoofStairs() {
        super();
    }

    public ComponentTFTowerRoofStairs(int i, ComponentTFTowerWing wing) {
        super(i, wing);

        // always facing = 0. this roof cant rotate, due to stair facing issues
        setCoordBaseMode(0);

        size = wing.size; // assuming only square towers and roofs right now
        height = size / 2;

        // just hang out at the very top of the tower
        makeCapBB(wing);
    }

    // makes a pyramid shaped roof out of stairs
    @Override
    public boolean addComponentParts(World world, Random rand, StructureBoundingBox sbb) {
        for (int y = 0; y <= height; y++) {
            int min = y;
            int max = size - y - 1;
            if (min > max) break;

            for (int x = min; x <= max; x++) {
                for (int z = min; z <= max; z++) {
                    boolean xEdge = x == min || x == max;
                    boolean zEdge = z == min || z == max;

                    if (xEdge && zEdge) {
                        placeBlockAtCurrentPosition(world, Blocks.wooden_slab, 2, x, y, z, sbb);
                    } else if (x == min) {
                        placeBlockAtCurrentPosition(world, Blocks.birch_stairs, 0, x, y, z, sbb);
                    } else if (x == max) {
                        placeBlockAtCurrentPosition(world, Blocks.birch_stairs, 1, x, y, z, sbb);
                    } else if (z == max) {
                        placeBlockAtCurrentPosition(world, Blocks.birch_stairs, 3, x, y, z, sbb);
                    } else if (z == min) {
                        placeBlockAtCurrentPosition(world, Blocks.birch_stairs, 2, x, y, z, sbb);
                    } else {
                        placeBlockAtCurrentPosition(world, Blocks.planks, 2, x, y, z, sbb);
                    }
                }
            }
        }

        return true;
    }
}
