package twilightforest.structures.darktower;

import java.util.Random;

import net.minecraft.init.Blocks;
import net.minecraft.world.gen.structure.StructureComponent;

import twilightforest.block.TFBlocks;

public class StructureTFTowerWoods extends StructureComponent.BlockSelector {

    private static final float CRACKED_CHANCE = 0.1F;
    private static final float MOSSY_CHANCE = 0.2F;
    private static final float INFESTED_CHANCE = 0.225F;

    @Override
    public void selectBlocks(Random random, int x, int y, int z, boolean isWall) {
        if (!isWall) {
            field_151562_a = Blocks.air;
            selectedBlockMetaData = 0;
            return;
        }

        field_151562_a = TFBlocks.towerWood;
        float roll = random.nextFloat();

        if (roll < CRACKED_CHANCE) {
            selectedBlockMetaData = 2;
        } else if (roll < MOSSY_CHANCE) {
            selectedBlockMetaData = 3;
        } else if (roll < INFESTED_CHANCE) {
            selectedBlockMetaData = 4;
        } else {
            selectedBlockMetaData = 0;
        }
    }
}
