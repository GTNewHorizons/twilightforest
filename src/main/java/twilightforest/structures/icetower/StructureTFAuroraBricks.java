package twilightforest.structures.icetower;

import java.util.Random;

import net.minecraft.init.Blocks;
import net.minecraft.world.gen.structure.StructureComponent;

import twilightforest.block.TFBlocks;

public class StructureTFAuroraBricks extends StructureComponent.BlockSelector {

    @Override
    public void selectBlocks(Random random, int x, int y, int z, boolean isWall) {
        if (!isWall) {
            field_151562_a = Blocks.air;
            selectedBlockMetaData = 0;
            return;
        }

        field_151562_a = TFBlocks.auroraBlock;
        selectedBlockMetaData = Math.abs(x + z) & 15;
    }
}
