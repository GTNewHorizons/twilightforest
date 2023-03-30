package twilightforest.structures.finalcastle;

import java.util.List;
import java.util.Random;

import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;

import twilightforest.block.TFBlocks;
import twilightforest.structures.StructureTFDecoratorCastle;

public class ComponentTFFinalCastleDungeonEntrance extends ComponentTFFinalCastleDungeonRoom31 {

    public boolean hasExit = false;

    public ComponentTFFinalCastleDungeonEntrance() {}

    public ComponentTFFinalCastleDungeonEntrance(Random rand, int i, int x, int y, int z, int direction, int level) {
        super(rand, i, x, y, z, direction, level);
    }

    @Override
    public void buildComponent(StructureComponent parent, List<StructureComponent> list, Random rand) {
        this.deco = new StructureTFDecoratorCastle();
        this.deco.blockID = TFBlocks.castleMagic;
        this.deco.blockMeta = 2;

        this.deco.fenceID = TFBlocks.forceField;
        this.deco.fenceMeta = 1;

        // this is going to be the parent for all rooms on this level
        super.buildComponent(this, list, rand);
    }

    @Override
    public boolean addComponentParts(World world, Random rand, StructureBoundingBox sbb) {
        super.addComponentParts(world, rand, sbb);

        // stairs
        for (int y = 0; y <= this.height; y++) {
            int x = (this.size / 2) - 2;
            int z = (this.size / 2) - y + 2;
            this.fillWithMetadataBlocks(
                    world,
                    sbb,
                    x,
                    y,
                    z,
                    x + 4,
                    y,
                    z,
                    deco.stairID,
                    getStairMeta(3),
                    deco.stairID,
                    getStairMeta(3),
                    false);
            this.fillWithMetadataBlocks(
                    world,
                    sbb,
                    x,
                    0,
                    z,
                    x + 4,
                    y - 1,
                    z,
                    TFBlocks.deadrock,
                    0,
                    TFBlocks.deadrock,
                    0,
                    false);
        }

        // door
        this.fillWithMetadataBlocks(world, sbb, 23, 0, 12, 23, 3, 14, TFBlocks.castleDoor, 2, Blocks.air, 0, false);
        this.fillWithMetadataBlocks(
                world,
                sbb,
                23,
                4,
                12,
                23,
                4,
                14,
                deco.blockID,
                deco.blockMeta,
                deco.blockID,
                deco.blockMeta,
                false);

        return true;
    }

    protected int getForceFieldMeta(Random decoRNG) {
        return 1;
    }

    protected int getRuneMeta(int fieldMeta) {
        return 0;
    }
}
