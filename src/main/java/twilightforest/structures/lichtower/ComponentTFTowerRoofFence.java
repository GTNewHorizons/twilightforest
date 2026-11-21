package twilightforest.structures.lichtower;

import java.util.Random;

import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;

public class ComponentTFTowerRoofFence extends ComponentTFTowerRoof {

    public ComponentTFTowerRoofFence() {
        super();
    }

    public ComponentTFTowerRoofFence(int i, ComponentTFTowerWing wing) {
        super(i, wing);

        // same alignment
        setCoordBaseMode(wing.getCoordBaseMode());

        // same size
        size = wing.size; // assuming only square towers and roofs right now
        height = 0;

        // just hang out at the very top of the tower
        makeCapBB(wing);
    }

    // a fence around the roof
    @Override
    public boolean addComponentParts(World world, Random rand, StructureBoundingBox sbb) {
        int y = height + 1;
        int last = size - 1;

        for (int x = 0; x < size; x++) {
            if (x == 0 || x == last) {
                for (int z = 0; z < size; z++) {
                    placeBlockAtCurrentPosition(world, Blocks.fence, 0, x, y, z, sbb);
                }
            } else {
                placeBlockAtCurrentPosition(world, Blocks.fence, 0, x, y, 0, sbb);
                placeBlockAtCurrentPosition(world, Blocks.fence, 0, x, y, last, sbb);
            }
        }

        return true;
    }
}
