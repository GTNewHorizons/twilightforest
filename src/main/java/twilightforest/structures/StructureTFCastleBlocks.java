package twilightforest.structures;

import java.util.Random;

import net.minecraft.init.Blocks;
import net.minecraft.world.gen.structure.StructureComponent;

import twilightforest.block.TFBlocks;

public class StructureTFCastleBlocks extends StructureComponent.BlockSelector {

    @Override
    public void selectBlocks(Random rand, int x, int y, int z, boolean isWall) {
        if (!isWall) {
            this.field_151562_a = Blocks.air;
            this.selectedBlockMetaData = 0;
            return;
        }

        this.field_151562_a = TFBlocks.castleBlock;
        float roll = rand.nextFloat();

        if (roll < 0.1F) {
            this.selectedBlockMetaData = 1;
        } else if (roll < 0.2F) {
            this.selectedBlockMetaData = 2;
        } else {
            this.selectedBlockMetaData = 0;
        }
    }
}
