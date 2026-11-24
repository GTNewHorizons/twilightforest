package twilightforest.world;

import java.util.Random;

import net.minecraft.init.Blocks;
import net.minecraft.world.World;

public class TFGenStoneCircle extends TFGenerator {

    /**
     * We make a stone circle, generally 3 squares in diameter
     */
    @Override
    public boolean generate(World world, Random rand, int x, int y, int z) {
        // both +-3 edges must be included in the clearance check
        if (y < 1 || y + 4 > world.getHeight() || !isAreaSuitable(world, rand, x - 3, y, z - 3, 7, 4, 7)) {
            return false;
        }

        // okay! circle!
        for (int cy = 0; cy <= 2; cy++) {
            setBlock(world, x - 3, y + cy, z, Blocks.mossy_cobblestone);
            setBlock(world, x + 3, y + cy, z, Blocks.mossy_cobblestone);
            setBlock(world, x, y + cy, z - 3, Blocks.mossy_cobblestone);
            setBlock(world, x, y + cy, z + 3, Blocks.mossy_cobblestone);
            setBlock(world, x - 2, y + cy, z - 2, Blocks.mossy_cobblestone);
            setBlock(world, x + 2, y + cy, z - 2, Blocks.mossy_cobblestone);
            setBlock(world, x - 2, y + cy, z + 2, Blocks.mossy_cobblestone);
            setBlock(world, x + 2, y + cy, z + 2, Blocks.mossy_cobblestone);
        }
        return true;
    }
}
