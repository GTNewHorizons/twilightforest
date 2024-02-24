package twilightforest.world;

import net.minecraft.block.Block;
import net.minecraft.world.World;

class DefaultOrePlacer implements OrePlacer {
    @Override
    public boolean placeOre(World world, int x, int y, int z, Block block) {
        // Set the block without block updates.
        // Taken from WorldGenerator::func_150515_a
        return world.setBlock(x, y, z, block, 0, 2);
    }
}
