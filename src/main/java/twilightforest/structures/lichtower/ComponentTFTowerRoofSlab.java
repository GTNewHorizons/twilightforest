package twilightforest.structures.lichtower;

import java.util.Random;

import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;

public class ComponentTFTowerRoofSlab extends ComponentTFTowerRoof {

    public ComponentTFTowerRoofSlab() {
        super();
    }

    public ComponentTFTowerRoofSlab(int i, ComponentTFTowerWing wing) {
        super(i, wing);

        // same alignment
        this.setCoordBaseMode(wing.getCoordBaseMode());
        // same size
        this.size = wing.size; // assuming only square towers and roofs right now.
        this.height = size / 2;

        // just hang out at the very top of the tower
        makeCapBB(wing);
    }

    /**
     * Makes a flat, pyramid-shaped roof
     */
    @Override
    public boolean addComponentParts(World world, Random rand, StructureBoundingBox sbb) {
        return makePyramidCap(world, 2, sbb);
    }

    protected boolean makePyramidCap(World world, int slabMeta, StructureBoundingBox sbb) {
        generateCap(world, slabMeta, sbb, false);
        return true;
    }

    protected boolean makeConnectedCap(World world, int slabMeta, StructureBoundingBox sbb) {
        generateCap(world, slabMeta, sbb, true);
        return true;
    }

    private void generateCap(World world, int slabMeta, StructureBoundingBox sbb, boolean connected) {
        // connected caps extend to the rear edge and stop one layer below pyramid caps
        int maxY = connected ? height - 1 : height;
        for (int y = 0; y <= maxY; y++) {
            int min = 2 * y;
            int max = size - min - 1;
            if (min > max) break;
            for (int x = connected ? 0 : min; x <= max; x++) {
                for (int z = min; z <= max; z++) {
                    boolean edge = x == max || z == min || z == max || (!connected && x == min);
                    placeBlockAtCurrentPosition(
                            world,
                            edge ? Blocks.wooden_slab : Blocks.planks,
                            slabMeta,
                            x,
                            y,
                            z,
                            sbb);
                }
            }
        }
    }
}
