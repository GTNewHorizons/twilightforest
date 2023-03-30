package twilightforest.structures.finalcastle;

import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;

import twilightforest.block.TFBlocks;
import twilightforest.structures.StructureTFComponent;

public class ComponentTFFinalCastleFoundation13 extends StructureTFComponent {

    protected int groundLevel = -1;

    public ComponentTFFinalCastleFoundation13() {}

    public ComponentTFFinalCastleFoundation13(Random rand, int i, StructureTFComponent sideTower) {
        super(i);

        this.setCoordBaseMode(sideTower.getCoordBaseMode());
        this.boundingBox = new StructureBoundingBox(
                sideTower.getBoundingBox().minX - 2,
                sideTower.getBoundingBox().minY - 1,
                sideTower.getBoundingBox().minZ - 2,
                sideTower.getBoundingBox().maxX + 2,
                sideTower.getBoundingBox().minY,
                sideTower.getBoundingBox().maxZ + 2);
    }

    @Override
    public void buildComponent(StructureComponent parent, List<StructureComponent> list, Random rand) {
        if (parent != null && parent instanceof StructureTFComponent) {
            this.deco = ((StructureTFComponent) parent).deco;
        }
    }

    @Override
    public boolean addComponentParts(World world, Random rand, StructureBoundingBox sbb) {
        // offset bounding box to average ground level
        if (this.groundLevel < 0) {
            this.groundLevel = this.getDeadrockLevel(world, sbb);

            if (this.groundLevel < 0) {
                return true;
            }
            // System.out.println("Adjusting root bounding box to " + this.boundingBox.minY);
        }

        // how tall are we
        int height = this.boundingBox.maxY - this.groundLevel;
        int mid = height / 2;

        // assume square
        int size = this.boundingBox.maxX - this.boundingBox.minX;

        for (int rotation = 0; rotation < 4; rotation++) {
            // do corner
            this.fillToGroundRotated(world, deco.blockID, deco.blockMeta, 1, -1, 1, rotation, sbb);
            this.fillToGroundRotated(world, deco.blockID, deco.blockMeta, 2, -1, 1, rotation, sbb);
            this.fillToGroundRotated(world, deco.blockID, deco.blockMeta, 2, -mid, 0, rotation, sbb);
            this.fillToGroundRotated(world, deco.blockID, deco.blockMeta, 1, -1, 2, rotation, sbb);
            this.fillToGroundRotated(world, deco.blockID, deco.blockMeta, 0, -mid, 2, rotation, sbb);

            for (int x = 6; x < (size - 3); x += 4) {
                this.fillToGroundRotated(world, deco.blockID, deco.blockMeta, x, -1, 1, rotation, sbb);
                this.fillToGroundRotated(world, deco.blockID, deco.blockMeta, x, -mid, 0, rotation, sbb);
            }
        }
        return true;
    }

    /**
     * Find what y level the local deadrock is. Just check the center of the chunk we're given
     */
    protected int getDeadrockLevel(World world, StructureBoundingBox sbb) {
        int groundLevel = 256;

        // is 150 a good place to start? :)
        for (int y = 150; y > 0; y--) {
            int cx = sbb.getCenterX();
            int cz = sbb.getCenterZ();

            Block block = world.getBlock(cx, y, cz);
            if (block == TFBlocks.deadrock) {
                groundLevel = y;
                break;
            }
        }
        return groundLevel;
    }
}
