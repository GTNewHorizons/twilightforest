package twilightforest.structures.finalcastle;

import java.util.List;
import java.util.Random;

import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;

import twilightforest.structures.StructureTFComponent;

// Stair blocks heading to the entrance tower doors
public class ComponentTFFinalCastleEntranceStairs extends StructureTFComponent {

    private static final int STAIR_DEPTH = 11;

    public ComponentTFFinalCastleEntranceStairs() {}

    public ComponentTFFinalCastleEntranceStairs(int index, int x, int y, int z, int direction) {
        super(index);
        setCoordBaseMode(direction);
        // move the local Y origin down so the entire staircase is represented
        // by a valid bounding box, without shifting any space steps
        boundingBox = StructureTFComponent
                .getComponentToAddBoundingBox2(x, y, z, 0, -1 - STAIR_DEPTH, -6, 12, STAIR_DEPTH, 13, direction);
    }

    @Override
    public void buildComponent(StructureComponent parent, List<StructureComponent> list, Random rand) {
        if (parent instanceof StructureTFComponent) {
            this.deco = ((StructureTFComponent) parent).deco;
        }
    }

    @Override
    public boolean addComponentParts(World world, Random rand, StructureBoundingBox sbb) {

        final int size = 13;

        for (int x = 1; x < size; x++) {

            placeStairs(world, sbb, x, stairY(1 - x), stairZ(5), 2);

            for (int z = 0; z <= x; z++) {

                if (z > 0 && z <= size / 2) {
                    placeStairs(world, sbb, x, stairY(1 - x), stairZ(5 - z), 2);
                    placeStairs(world, sbb, x, stairY(1 - x), stairZ(5 + z), 2);
                }

                if (x <= size / 2) {
                    placeStairs(world, sbb, z, stairY(1 - x), stairZ(5 - x), 1);
                    placeStairs(world, sbb, z, stairY(1 - x), stairZ(5 + x), 3);
                }
            }
        }
        func_151554_b(world, deco.blockID, deco.blockMeta, 0, stairY(0), stairZ(5), sbb);

        return true;
    }

    private int stairY(int oldY) {
        return oldY + (boundingBox.getYSize() <= 1 ? 0 : STAIR_DEPTH);
    }

    private int stairZ(int oldZ) {
        // include the outermost stair without moving world blocks
        return oldZ + (boundingBox.getYSize() <= 1 ? 0 : 1);
    }

    private void placeStairs(World world, StructureBoundingBox sbb, int x, int y, int z, int stairMeta) {
        int worldX = getXWithOffset(x, z);
        int worldY = getYWithOffset(y);
        int worldZ = getZWithOffset(x, z);
        if (!sbb.isVecInside(worldX, worldY, worldZ)) {
            return;
        }
        if (world.getBlock(worldX, worldY, worldZ).isReplaceable(world, worldX, worldY, worldZ)) {
            placeBlockAtCurrentPosition(world, deco.stairID, getStairMeta(stairMeta), x, y, z, sbb);
            func_151554_b(world, deco.blockID, deco.blockMeta, x, y - 1, z, sbb);
        }
    }
}
