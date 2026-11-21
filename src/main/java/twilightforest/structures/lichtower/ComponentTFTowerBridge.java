package twilightforest.structures.lichtower;

import java.util.List;
import java.util.Random;

import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;

import twilightforest.structures.StructureTFComponent;

public class ComponentTFTowerBridge extends ComponentTFTowerWing {

    private static final int BRIDGE_SIZE = 3;
    private static final int EXIT_X = 2;
    private static final int EXIT_Y = 1;
    private static final int EXIT_Z = 1;

    int dSize;
    int dHeight;

    public ComponentTFTowerBridge() {
        super();
        // TODO Auto-generated constructor stub
    }

    protected ComponentTFTowerBridge(int i, int x, int y, int z, int pSize, int pHeight, int direction) {
        super(i, x, y, z, BRIDGE_SIZE, BRIDGE_SIZE, direction);
        this.dSize = pSize;
        this.dHeight = pHeight;
    }

    @Override
    public void buildComponent(StructureComponent parent, List<StructureComponent> list, Random rand) {
        // getValidOpening(rand, 0);
        makeTowerWing(list, rand, 1, EXIT_X, EXIT_Y, EXIT_Z, dSize, dHeight, 0);
    }

    /**
     * Gets the bounding box of the tower wing we would like to make.
     *
     * @return
     */
    public StructureBoundingBox getWingBB() {
        int direction = getCoordBaseMode();
        int[] origin = offsetTowerCoords(EXIT_X, EXIT_Y, EXIT_Z, dSize, direction);
        return StructureTFComponent.getComponentToAddBoundingBox(
                origin[0],
                origin[1],
                origin[2],
                0,
                0,
                0,
                dSize - 1,
                dHeight - 1,
                dSize - 1,
                direction);
    }

    @Override
    public boolean addComponentParts(World world, Random rand, StructureBoundingBox sbb) {
        // make walls
        for (int x = 0; x < BRIDGE_SIZE; x++) {
            placeBlockAtCurrentPosition(world, Blocks.fence, 0, x, 2, 0, sbb);
            placeBlockAtCurrentPosition(world, Blocks.fence, 0, x, 2, 2, sbb);
            placeBlockAtCurrentPosition(world, Blocks.stonebrick, 0, x, 1, 0, sbb);
            placeBlockAtCurrentPosition(world, Blocks.stonebrick, 0, x, 1, 2, sbb);
            placeBlockAtCurrentPosition(world, Blocks.stonebrick, 0, x, 0, 0, sbb);
            placeBlockAtCurrentPosition(world, Blocks.stonebrick, 0, x, 0, 1, sbb);
            placeBlockAtCurrentPosition(world, Blocks.stonebrick, 0, x, 0, 2, sbb);
            placeBlockAtCurrentPosition(world, Blocks.stonebrick, 0, x, -1, 1, sbb);
        }

        // try two blocks outside the boundries
        placeBlockAtCurrentPosition(world, Blocks.stonebrick, 0, -1, -1, 1, sbb);
        placeBlockAtCurrentPosition(world, Blocks.stonebrick, 0, BRIDGE_SIZE, -1, 1, sbb);

        // clear bridge walkway
        fillWithAir(world, sbb, 0, 1, 1, BRIDGE_SIZE - 1, 2, 1);

        // marker blocks
        // placeBlockAtCurrentPosition(world, Blocks.wool, this.coordBaseMode, size / 2, 2, size / 2, sbb);
        // placeBlockAtCurrentPosition(world, Blocks.gold_block, 0, 0, 0, 0, sbb);

        // door opening?
        // makeDoorOpening(world, sbb);

        return true;
    }
}
