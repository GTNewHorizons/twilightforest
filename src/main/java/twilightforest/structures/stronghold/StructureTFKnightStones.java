package twilightforest.structures.stronghold;

import java.util.Random;

import net.minecraft.init.Blocks;
import net.minecraft.world.gen.structure.StructureComponent;

import twilightforest.block.TFBlocks;

public class StructureTFKnightStones extends StructureComponent.BlockSelector {

    @Override
    public void selectBlocks(Random random, int x, int y, int z, boolean isWall) {
        if (!isWall) {
            field_151562_a = Blocks.air;
            selectedBlockMetaData = 0;
            return;
        }

        field_151562_a = TFBlocks.underBrick;
        float roll = random.nextFloat();
        selectedBlockMetaData = roll < 0.2F ? 2 : roll < 0.5F ? 1 : 0;
    }

}
