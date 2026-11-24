package twilightforest.world;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

public class TFGenFireJet extends TFGenerator {

    private static final int GENERATION_ATTEMPTS = 4;
    private static final int RESERVOIR_RADIUS = 2;
    private static final int LAVA_RADIUS = 1;

    private final Block plantBlockId;
    private final int plantBlockMeta;

    public TFGenFireJet(Block fireJet, int meta) {
        this.plantBlockId = fireJet;
        this.plantBlockMeta = meta;
    }

    @Override
    public boolean generate(World world, Random rand, int x, int y, int z) {
        for (int attempt = 0; attempt < GENERATION_ATTEMPTS; attempt++) {
            int dx = x + rand.nextInt(8) - rand.nextInt(8);
            int dy = y + rand.nextInt(4) - rand.nextInt(4);
            int dz = z + rand.nextInt(8) - rand.nextInt(8);

            // the reservoir occupies the three blocks immediately below the jet
            if (dy < 3 || dy >= world.getHeight()) {
                continue;
            }

            if (world.isAirBlock(dx, dy, dz) && world.canBlockSeeTheSky(dx, dy, dz)
                    && world.getBlock(dx, dy - 1, dz).getMaterial() == Material.grass
                    && world.getBlock(dx + 1, dy - 1, dz).getMaterial() == Material.grass
                    && world.getBlock(dx - 1, dy - 1, dz).getMaterial() == Material.grass
                    && world.getBlock(dx, dy - 1, dz + 1).getMaterial() == Material.grass
                    && world.getBlock(dx, dy - 1, dz - 1).getMaterial() == Material.grass) {
                // jet
                world.setBlock(dx, dy - 1, dz, this.plantBlockId, this.plantBlockMeta, 0);

                // create reservoir with stone walls
                for (int rx = -RESERVOIR_RADIUS; rx <= RESERVOIR_RADIUS; rx++) {
                    for (int rz = -RESERVOIR_RADIUS; rz <= RESERVOIR_RADIUS; rz++) {
                        if (Math.abs(rx) <= LAVA_RADIUS && Math.abs(rz) <= LAVA_RADIUS) {
                            // lava reservoir
                            world.setBlock(dx + rx, dy - 2, dz + rz, Blocks.flowing_lava, 0, 0);
                        } else if (world.getBlock(dx + rx, dy - 2, dz + rz).getMaterial() != Material.lava) {
                            // only stone where there is no lava
                            world.setBlock(dx + rx, dy - 2, dz + rz, Blocks.stone, 0, 0);
                        }
                        world.setBlock(dx + rx, dy - 3, dz + rz, Blocks.stone, 0, 0);
                    }
                }
            }
        }

        return true;
    }
}
