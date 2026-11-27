package twilightforest.structures.lichtower;

import java.util.Random;

import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;

public class ComponentTFTowerRoofGableForwards extends ComponentTFTowerRoof {

    public ComponentTFTowerRoofGableForwards() {
        super();
    }

    public ComponentTFTowerRoofGableForwards(int i, ComponentTFTowerWing wing) {
        super(i, wing);

        // same facing
        this.setCoordBaseMode(wing.getCoordBaseMode());

        this.size = wing.size + 2; // assuming only square towers and roofs right now.
        this.height = size;

        // just hang out at the very top of the tower
        this.makeAttachedOverhangBB(wing);
        // the figurehead extends one block farther than the normal attached roof footprint
        switch (getCoordBaseMode()) {
            case 0 -> boundingBox.maxX++;
            case 1 -> boundingBox.maxZ++;
            case 2 -> boundingBox.minX--;
            case 3 -> boundingBox.minZ--;
        }
        // small roofs have a figurehead that rises above the regular gable
        int figureheadTop = size + 2 - slopeChangeForSize(size);
        boundingBox.maxY = Math.max(boundingBox.maxY, boundingBox.minY + figureheadTop);
    }

    /**
     * Makes a pointy roof out of stuff
     */
    @Override
    public boolean addComponentParts(World world, Random rand, StructureBoundingBox sbb) {
        final int slabMeta = 2;
        int slopeChange = slopeChangeForSize(size);
        for (int y = 0; y <= height; y++) {
            int min = y < slopeChange ? y : (y + slopeChange) / 2;
            int max = size - min - 1;
            if (min > max) break;
            for (int x = 0; x <= size - 2; x++) {
                for (int z = min; z <= max; z++) {
                    if (x < size - 2 || z == min || z == max) {
                        placeBlockAtCurrentPosition(world, Blocks.planks, slabMeta, x, y, z, sbb);
                    }
                }
            }
        }

        // put on the little figurehead-like "cap"

        // where is even the top of our roof?
        int top = size + 1 - slopeChange;
        int zMid = size / 2;

        placeBlockAtCurrentPosition(world, Blocks.wooden_slab, slabMeta | 0xA, size - 1, top - 1, zMid, sbb);
        placeBlockAtCurrentPosition(world, Blocks.wooden_slab, slabMeta, 0, top, zMid, sbb);
        placeBlockAtCurrentPosition(world, Blocks.wooden_slab, slabMeta, size - 3, top, zMid, sbb);
        placeBlockAtCurrentPosition(world, Blocks.planks, slabMeta, size - 2, top, zMid, sbb);
        placeBlockAtCurrentPosition(world, Blocks.planks, slabMeta, size - 1, top, zMid, sbb);
        placeBlockAtCurrentPosition(world, Blocks.planks, slabMeta, size - 1, top + 1, zMid, sbb);

        return true;
    }

    public int slopeChangeForSize(int pSize) {
        if (pSize > 10) {
            return 3;
        } else if (pSize > 6) {
            return 2;
        } else {
            return 1;
        }
    }
}
