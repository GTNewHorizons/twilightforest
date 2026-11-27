package twilightforest.world;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

public class TFGenTallGrass extends WorldGenerator {

    private final Block tallGrassID;
    private final int tallGrassMetadata;
    private final int amount;

    public TFGenTallGrass(Block blockID, int meta, int amount) {
        this.amount = amount;
        this.tallGrassID = blockID;
        this.tallGrassMetadata = meta;
    }

    @Override
    public boolean generate(World world, Random rand, int x, int y, int z) {
        Block block;
        do {
            block = world.getBlock(x, y, z);
            if (block != null && !block.isLeaves(world, x, y, z)) {
                break;
            }
            y--;
        } while (y > 0);

        for (int i = 0; i < amount; ++i) {
            int dx = x + rand.nextInt(8) - rand.nextInt(8);
            int dy = y + rand.nextInt(4) - rand.nextInt(4);
            int dz = z + rand.nextInt(8) - rand.nextInt(8);

            if (world.isAirBlock(dx, dy, dz) && this.tallGrassID.canBlockStay(world, dx, dy, dz)) {
                world.setBlock(dx, dy, dz, this.tallGrassID, this.tallGrassMetadata, 2);
            }
        }
        return true;
    }
}
