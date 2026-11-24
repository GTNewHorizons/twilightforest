package twilightforest.world;

import java.util.Random;

import net.minecraft.init.Blocks;
import net.minecraft.world.World;

import twilightforest.TFTreasure;

public class TFGenFoundation extends TFGenerator {

    private static final int MAX_WALL_HEIGHT = 4;
    private static final int BASEMENT_DEPTH = 4;

    @Override
    public boolean generate(World world, Random rand, int x, int y, int z) {
        int sx = 5 + rand.nextInt(5);
        int sz = 5 + rand.nextInt(5);

        // both endpoints are inclusive when building the wall, so check the full footprint
        if (y < BASEMENT_DEPTH || (long) y + MAX_WALL_HEIGHT > world.getHeight()
                || !isAreaSuitable(world, rand, x, y, z, sx + 1, MAX_WALL_HEIGHT, sz + 1)) {
            return false;
        }

        // okay!
        for (int cx = 0; cx <= sx; cx++) {
            for (int cz = 0; cz <= sz; cz++) {
                if (cx == 0 || cx == sx || cz == 0 || cz == sz) {
                    // stone on the edges
                    int ht = rand.nextInt(MAX_WALL_HEIGHT) + 1;

                    for (int cy = 0; cy <= ht; cy++) {
                        setBlock(world, x + cx, y + cy - 1, z + cz, randStone(rand, cy + 1));
                    }
                } else {
                    // destroyed wooden plank floor
                    if (rand.nextInt(3) != 0) {
                        setBlock(world, x + cx, y - 1, z + cz, Blocks.planks);
                    }
                }
            }
        }

        // TODO: chimney?

        // 50% basement chance!
        if (rand.nextInt(2) == 0) {
            // clear basement
            for (int cx = 1; cx < sx; cx++) {
                for (int cz = 1; cz < sz; cz++) {
                    setBlock(world, x + cx, y - 3, z + cz, Blocks.air);
                    setBlock(world, x + cx, y - 4, z + cz, Blocks.air);
                }
            }

            // make chest
            int cx = rand.nextInt(sx - 1) + 1;
            int cz = rand.nextInt(sz - 1) + 1;
            TFTreasure.basement.generate(world, rand, x + cx, y - 4, z + cz);

        }

        return true;
    }
}
