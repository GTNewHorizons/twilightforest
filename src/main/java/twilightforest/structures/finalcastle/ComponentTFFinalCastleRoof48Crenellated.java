package twilightforest.structures.finalcastle;

import java.util.List;
import java.util.Random;

import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;

import twilightforest.block.TFBlocks;
import twilightforest.structures.StructureTFComponent;

public class ComponentTFFinalCastleRoof48Crenellated extends StructureTFComponent {

    public ComponentTFFinalCastleRoof48Crenellated() {}

    public ComponentTFFinalCastleRoof48Crenellated(Random rand, int i, StructureTFComponent keep) {
        super(i);

        int height = 5;

        this.setCoordBaseMode(keep.getCoordBaseMode());
        this.boundingBox = new StructureBoundingBox(
                keep.getBoundingBox().minX - 2,
                keep.getBoundingBox().maxY - 1,
                keep.getBoundingBox().minZ - 2,
                keep.getBoundingBox().maxX + 2,
                keep.getBoundingBox().maxY + height - 1,
                keep.getBoundingBox().maxZ + 2);
    }

    @Override
    public void buildComponent(StructureComponent parent, List<StructureComponent> list, Random rand) {
        if (parent != null && parent instanceof StructureTFComponent) {
            this.deco = ((StructureTFComponent) parent).deco;
        }
    }

    @Override
    public boolean addComponentParts(World world, Random rand, StructureBoundingBox sbb) {
        // add second layer of floor
        this.fillWithMetadataBlocks(
                world,
                sbb,
                2,
                2,
                2,
                50,
                2,
                50,
                TFBlocks.castleMagic,
                3,
                TFBlocks.castleMagic,
                3,
                false);

        // crenellations
        for (int rotation = 0; rotation < 4; rotation++) {
            this.fillBlocksRotated(world, sbb, 3, 1, 1, 45, 3, 1, deco.blockID, deco.blockMeta, rotation);

            for (int i = 10; i < 41; i += 5) {
                this.fillBlocksRotated(world, sbb, i, 1, 0, i + 2, 5, 2, deco.blockID, deco.blockMeta, rotation);
                this.placeBlockRotated(world, deco.blockID, deco.blockMeta, i + 1, 0, 1, rotation, sbb);
            }
        }
        return true;
    }
}
