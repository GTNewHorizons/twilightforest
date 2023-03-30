package twilightforest.structures.finalcastle;

import java.util.List;
import java.util.Random;

import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;

import twilightforest.block.TFBlocks;
import twilightforest.structures.StructureTFComponent;
import twilightforest.structures.StructureTFDecoratorCastle;

public class ComponentTFFinalCastleBossGazebo extends StructureTFComponent {

    public ComponentTFFinalCastleBossGazebo() {}

    public ComponentTFFinalCastleBossGazebo(Random rand, int i, StructureTFComponent keep) {
        super(i);
        this.spawnListIndex = -1; // no monsters

        this.setCoordBaseMode(keep.getCoordBaseMode());
        this.boundingBox = new StructureBoundingBox(
                keep.getBoundingBox().minX + 14,
                keep.getBoundingBox().maxY + 2,
                keep.getBoundingBox().minZ + 14,
                keep.getBoundingBox().maxX - 14,
                keep.getBoundingBox().maxY + 13,
                keep.getBoundingBox().maxZ - 14);
    }

    @Override
    public void buildComponent(StructureComponent parent, List<StructureComponent> list, Random rand) {
        this.deco = new StructureTFDecoratorCastle();
        this.deco.blockID = TFBlocks.castleMagic;
        this.deco.blockMeta = 1;

        this.deco.fenceID = TFBlocks.forceField;
        this.deco.fenceMeta = 10;
    }

    @Override
    public boolean addComponentParts(World world, Random rand, StructureBoundingBox sbb) {
        // walls
        for (int rotation = 0; rotation < 4; rotation++) {
            this.fillBlocksRotated(world, sbb, 0, 0, 0, 0, 10, 20, deco.fenceID, deco.fenceMeta, rotation);
        }

        // roof
        this.fillWithMetadataBlocks(
                world,
                sbb,
                0,
                11,
                0,
                20,
                11,
                20,
                deco.fenceID,
                deco.fenceMeta,
                deco.fenceID,
                deco.fenceMeta,
                false);

        this.placeSignAtCurrentPosition(world, 10, 0, 10, "Final Boss Here", "You win!", sbb);

        return true;
    }
}
