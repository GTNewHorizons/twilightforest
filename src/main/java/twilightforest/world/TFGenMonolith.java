package twilightforest.world;

import java.util.Random;

import net.minecraft.init.Blocks;
import net.minecraft.world.World;

import twilightforest.entity.passive.EntityTFRaven;

/**
 * A 2x2 monolith of obsidian
 *
 * @author Ben
 *
 */
public class TFGenMonolith extends TFGenerator {

    private static final int COLUMN_COUNT = 4;
    private static final int RAVEN_ATTEMPTS = 2;

    @Override
    public boolean generate(World world, Random rand, int x, int y, int z) {
        int height = rand.nextInt(10) + 10;
        int direction = rand.nextInt(COLUMN_COUNT);

        if (y < 1 || (long) y + height > world.getHeight() || !isAreaSuitable(world, rand, x, y, z, 2, height, 2)) {
            return false;
        }

        // rotating profile gives the same 4 column heights as the original
        int[] heights = { height, (int) (height * .75), (int) (height * .75), (int) (height * .5) };
        for (int column = 0; column < COLUMN_COUNT; column++) {
            int columnHeight = heights[(column - direction + COLUMN_COUNT) % COLUMN_COUNT];
            int columnX = x + (column & 1);
            int columnZ = z + (column >> 1);
            for (int cy = 0; cy <= columnHeight; cy++) {
                setBlock(world, columnX, y + cy - 1, columnZ, cy == height ? Blocks.lapis_block : Blocks.obsidian);
            }
        }

        // spawn a few ravens nearby
        for (int i = 0; i < RAVEN_ATTEMPTS; i++) {
            int dx = (x + rand.nextInt(8)) - rand.nextInt(8);
            int dz = (z + rand.nextInt(8)) - rand.nextInt(8);
            int dy = world.getTopSolidOrLiquidBlock(dx, dz);

            EntityTFRaven raven = new EntityTFRaven(world);
            raven.setLocationAndAngles(dx, dy, dz, rand.nextFloat() * 360.0F, 0.0F);

            world.spawnEntityInWorld(raven);
        }
        return true;
    }
}
