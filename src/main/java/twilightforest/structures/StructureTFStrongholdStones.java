package twilightforest.structures;

import java.util.Random;

import net.minecraft.init.Blocks;
import net.minecraft.world.gen.structure.StructureComponent;

public class StructureTFStrongholdStones extends StructureComponent.BlockSelector {

    @Override
    public void selectBlocks(Random random, int x, int y, int z, boolean isBoundary) {
        if (!isBoundary) {
            field_151562_a = Blocks.air;
            selectedBlockMetaData = 0;
            return;
        }

        field_151562_a = Blocks.stonebrick;
        float roll = random.nextFloat();

        if (roll < 0.2F) {
            selectedBlockMetaData = 2;
        } else if (roll < 0.5F) {
            selectedBlockMetaData = 1;
        } else if (roll < 0.55F) {
            field_151562_a = Blocks.monster_egg;
            selectedBlockMetaData = 2;
        } else {
            selectedBlockMetaData = 0;
        }
    }
}
