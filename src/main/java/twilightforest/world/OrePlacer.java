package twilightforest.world;

import net.minecraft.block.Block;
import net.minecraft.world.World;

public interface OrePlacer {

    /**
     * Method which is expected to place a block in the given world at the given coordinates. The block can be
     * overridden as the implementer chooses.
     */
    void placeOre(World world, int x, int y, int z, Block block);
}
