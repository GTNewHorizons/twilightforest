package twilightforest.world;

import java.util.Random;

import net.minecraft.world.World;

import twilightforest.entity.passive.EntityTFPenguin;

public class TFGenPenguins extends TFGenerator {

    private static final int SPAWN_ATTEMPTS = 10;
    private static final int HORIZONTAL_SPREAD = 8;

    @Override
    public boolean generate(World world, Random rand, int x, int y, int z) {
        for (int i = 0; i < SPAWN_ATTEMPTS; i++) {
            int spawnX = x + rand.nextInt(HORIZONTAL_SPREAD) - rand.nextInt(HORIZONTAL_SPREAD);
            int spawnZ = z + rand.nextInt(HORIZONTAL_SPREAD) - rand.nextInt(HORIZONTAL_SPREAD);
            int spawnY = world.getTopSolidOrLiquidBlock(spawnX, spawnZ);

            EntityTFPenguin penguin = new EntityTFPenguin(world);
            penguin.setLocationAndAngles(spawnX, spawnY, spawnZ, rand.nextFloat() * 360.0F, 0.0F);

            world.spawnEntityInWorld(penguin);
        }
        return true;
    }
}
