package twilightforest.structures.finalcastle;

import java.util.Random;

import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;

import twilightforest.structures.StructureTFComponent;

// i sure hope this castle has a final boss!
// A larger foundation that comes all the way from the top of a tower
public class ComponentTFFinalCastleBellFoundation21 extends ComponentTFFinalCastleFoundation13 {

    public ComponentTFFinalCastleBellFoundation21() {}

    public ComponentTFFinalCastleBellFoundation21(Random rand, int i, StructureTFComponent sideTower) {
        super(rand, i, sideTower);

        this.boundingBox = new StructureBoundingBox(
                sideTower.getBoundingBox().minX - 2,
                sideTower.getBoundingBox().maxY - 1,
                sideTower.getBoundingBox().minZ - 2,
                sideTower.getBoundingBox().maxX + 2,
                sideTower.getBoundingBox().maxY,
                sideTower.getBoundingBox().maxZ + 2);
    }

    @Override
    public boolean addComponentParts(World world, Random rand, StructureBoundingBox sbb) {
        // these supports descend until they meet terrain, was unnecessary and could choose different levels on
        // different chunks
        final int mid = 16;
        final int low = 32;

        // assume square
        int size = this.boundingBox.maxX - this.boundingBox.minX;

        for (int rotation = 0; rotation < 4; rotation++) {
            // do corner
            this.fillToGroundRotated(world, deco.blockID, deco.blockMeta, 2, -1, 1, rotation, sbb);
            this.fillToGroundRotated(world, deco.blockID, deco.blockMeta, 2, -mid, 0, rotation, sbb);
            this.fillToGroundRotated(world, deco.blockID, deco.blockMeta, 1, -1, 2, rotation, sbb);
            this.fillToGroundRotated(world, deco.blockID, deco.blockMeta, 0, -mid, 2, rotation, sbb);

            this.fillToGroundRotated(world, deco.blockID, deco.blockMeta, 1, -low, 1, rotation, sbb);
            this.fillToGroundRotated(world, deco.blockID, deco.blockMeta, 0, -low, 1, rotation, sbb);
            this.fillToGroundRotated(world, deco.blockID, deco.blockMeta, 1, -low, 0, rotation, sbb);
            this.fillToGroundRotated(world, deco.blockID, deco.blockMeta, 0, -low, 0, rotation, sbb);

            for (int x = 6; x < (size - 3); x += 4) {
                this.fillToGroundRotated(world, deco.blockID, deco.blockMeta, x, -1, 1, rotation, sbb);
                this.fillToGroundRotated(world, deco.blockID, deco.blockMeta, x, -mid, 0, rotation, sbb);
            }
        }
        return true;
    }
}
