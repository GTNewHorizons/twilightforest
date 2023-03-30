package twilightforest.structures.finalcastle;

import java.util.List;
import java.util.Random;

import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;

import twilightforest.block.TFBlocks;
import twilightforest.structures.StructureTFComponent;

public class ComponentTFFinalCastleDungeonExit extends ComponentTFFinalCastleDungeonRoom31 {

    public ComponentTFFinalCastleDungeonExit() {}

    public ComponentTFFinalCastleDungeonExit(Random rand, int i, int x, int y, int z, int direction, int level) {
        super(rand, i, x, y, z, direction, level);
    }

    @Override
    public void buildComponent(StructureComponent parent, List<StructureComponent> list, Random rand) {
        if (parent != null && parent instanceof StructureTFComponent) {
            this.deco = ((StructureTFComponent) parent).deco;
        }

        // no need for additional rooms, we're along the outside anyways

        // add stairway down
        int bestDir = this.findStairDirectionTowards(parent.getBoundingBox().minX, parent.getBoundingBox().minZ);

        ComponentTFFinalCastleDungeonSteps steps0 = new ComponentTFFinalCastleDungeonSteps(
                rand,
                5,
                boundingBox.minX + 15,
                boundingBox.minY + 0,
                boundingBox.minZ + 15,
                bestDir);
        list.add(steps0);
        steps0.buildComponent(this, list, rand);

        // another level!?
        if (this.level == 1) {
            steps0.buildLevelUnder(parent, list, rand, this.level + 1);
        } else {
            steps0.buildBossRoomUnder(parent, list, rand);
        }
    }

    @Override
    public boolean addComponentParts(World world, Random rand, StructureBoundingBox sbb) {
        super.addComponentParts(world, rand, sbb);

        // door
        this.fillWithMetadataBlocks(world, sbb, 7, 0, 16, 7, 3, 18, TFBlocks.castleDoor, 2, Blocks.air, 0, false);
        this.fillWithMetadataBlocks(
                world,
                sbb,
                7,
                4,
                16,
                7,
                4,
                18,
                deco.blockID,
                deco.blockMeta,
                deco.blockID,
                deco.blockMeta,
                false);

        return true;
    }

    public int findStairDirectionTowards(int x, int z) {
        // center of component
        int cx = this.boundingBox.getCenterX();
        int cz = this.boundingBox.getCenterZ();

        // difference
        int dx = cx - x;
        int dz = cz - z;

        int absoluteDir;
        if (Math.abs(dz) >= Math.abs(dx)) {
            absoluteDir = (dz >= 0) ? 2 : 0;
        } else {
            absoluteDir = (dx >= 0) ? 3 : 1;
        }

        return absoluteDir;
    }

    protected int getForceFieldMeta(Random decoRNG) {
        return 1;
    }

    protected int getRuneMeta(int fieldMeta) {
        return 0;
    }
}
