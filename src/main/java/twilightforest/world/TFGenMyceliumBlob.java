package twilightforest.world;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

/**
 * This is a copypasta of the sand/gravel/clay generator that produces mycelium blobs for mushroom biomes
 *
 * @author Ben
 *
 */
public class TFGenMyceliumBlob extends WorldGenerator {

    private final Block myceliumBlockId;
    private final int numberOfBlocks;

    public TFGenMyceliumBlob(int numberOfBlocks) {
        this(Blocks.mycelium, numberOfBlocks);
    }

    public TFGenMyceliumBlob(Block block, int numberOfBlocks) {
        this.myceliumBlockId = block;
        this.numberOfBlocks = numberOfBlocks;
    }

    @Override
    public boolean generate(World world, Random random, int x, int y, int z) {
        // if (world.getBlock(i, j, k).getMaterial() != Material.water)
        // {
        // return false;
        // }
        // a radius roll requires a positive bound
        if (numberOfBlocks <= 2) {
            return false;
        }

        int range = random.nextInt(numberOfBlocks - 2) + 2;
        final int yRange = 1;
        for (int dx = x - range; dx <= x + range; dx++) {
            for (int dz = z - range; dz <= z + range; dz++) {
                int offsetX = dx - x;
                int offsetZ = dz - z;
                if (offsetX * offsetX + offsetZ * offsetZ > range * range) {
                    continue;
                }
                for (int dy = y - yRange; dy <= y + yRange; dy++) {
                    Block blockThere = world.getBlock(dx, dy, dz);
                    if (blockThere == Blocks.dirt || blockThere == Blocks.grass || blockThere == Blocks.stone) {
                        world.setBlock(dx, dy, dz, myceliumBlockId, 0, 2);
                    }
                }
            }
        }
        return true;
    }
}
