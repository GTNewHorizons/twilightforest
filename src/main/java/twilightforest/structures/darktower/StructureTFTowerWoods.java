package twilightforest.structures.darktower;

import java.util.Random;

import net.minecraft.init.Blocks;
import net.minecraft.world.gen.structure.StructureComponent;

import twilightforest.block.TFBlocks;

public class StructureTFTowerWoods extends StructureComponent.BlockSelector {

    @Override
    public void selectBlocks(Random random, int x, int y, int z, boolean isWall) {
        if (!isWall) {
            field_151562_a = Blocks.air;
            selectedBlockMetaData = 0;
            return;
        }

        field_151562_a = TFBlocks.towerWood;
        float roll = random.nextFloat();

        if (roll < 0.1F) {
            selectedBlockMetaData = 2;
        } else if (roll < 0.2F) {
            selectedBlockMetaData = 3;
        } else if (roll < 0.225F) {
            selectedBlockMetaData = 4;
        } else {
            selectedBlockMetaData = 0;
        }
    }
}
