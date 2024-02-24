package twilightforest.world;

import net.minecraft.block.Block;
import net.minecraft.world.World;

public interface OrePlacer {
    boolean placeOre(World world, int x, int y, int z, Block block);
}
