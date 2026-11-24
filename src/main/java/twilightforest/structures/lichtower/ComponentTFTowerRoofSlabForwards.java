package twilightforest.structures.lichtower;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;

/**
 * A flat tower roof using slabs that is larger than the tower under it.
 *
 * @author Ben
 */
public class ComponentTFTowerRoofSlabForwards extends ComponentTFTowerRoofSlab {

    public ComponentTFTowerRoofSlabForwards() {
        super();
    }

    public ComponentTFTowerRoofSlabForwards(int i, ComponentTFTowerWing wing) {
        super(i, wing);

        // same alignment
        setCoordBaseMode(wing.getCoordBaseMode());
        // the overhang roof is like a cap roof that's 2 sizes bigger
        size = wing.size + 2; // assuming only square towers and roofs right now.
        height = size / 2;

        // bounding box
        makeAttachedOverhangBB(wing);
    }

    /**
     * Makes flat hip roof
     */
    @Override
    public boolean addComponentParts(World world, Random rand, StructureBoundingBox sbb) {
        for (int y = 0; y <= height; y++) {
            int minZ = 2 * y;
            int maxZ = size - minZ - 1;
            if (minZ > maxZ) break;
            for (int x = 0; x < maxZ; x++) {
                for (int z = minZ; z <= maxZ; z++) {
                    Block slab = x == maxZ - 1 || z == minZ || z == maxZ ? Blocks.wooden_slab
                            : Blocks.double_wooden_slab;
                    placeBlockAtCurrentPosition(world, slab, 2, x, y, z, sbb);
                }
            }
        }
        return true;
    }
}
