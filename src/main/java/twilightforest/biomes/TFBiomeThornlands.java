package twilightforest.biomes;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.stats.Achievement;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

import twilightforest.TFAchievementPage;
import twilightforest.block.TFBlocks;
import twilightforest.world.TFGenThorns;
import twilightforest.world.TFWorld;

public class TFBiomeThornlands extends TFBiomeBase {

    private static final int THORN_ATTEMPTS = 128;
    private static final int CHUNK_SIZE = 16;
    private static final int CHUNK_OFFSET = 8;
    private static final int DISABLED_TREE_COUNT = -999;
    private static final int DISABLED_CACTUS_COUNT = -9999;

    private final TFGenThorns tfGenThorns;

    public TFBiomeThornlands(int biomeId) {
        super(biomeId);

        this.topBlock = TFBlocks.deadrock;
        this.field_150604_aj = 0;
        this.fillerBlock = TFBlocks.deadrock;
        this.field_76754_C = 1;

        this.temperature = 0.3F;
        this.rainfall = 0.2F;

        TFBiomeDecorator decorator = getTFBiomeDecorator();
        decorator.canopyPerChunk = DISABLED_TREE_COUNT;
        decorator.setTreesPerChunk(DISABLED_TREE_COUNT);
        decorator.deadBushPerChunk = 2;
        decorator.cactiPerChunk = DISABLED_CACTUS_COUNT;
        this.spawnableCreatureList.clear();

        this.tfGenThorns = new TFGenThorns();

        decorator.generateLakes = false;
    }

    @Override
    public void decorate(World world, Random rand, int mapX, int mapZ) {
        super.decorate(world, rand, mapX, mapZ);

        // add thorns!
        for (int i = 0; i < THORN_ATTEMPTS; i++) {
            int x = mapX + rand.nextInt(CHUNK_SIZE) + CHUNK_OFFSET;
            int z = mapZ + rand.nextInt(CHUNK_SIZE) + CHUNK_OFFSET;
            int y = getGroundLevel(world, x, z);

            this.tfGenThorns.generate(world, rand, x, y, z);
        }
    }

    public int getGroundLevel(World world, int x, int z) {
        // go from sea level up. If we get grass, return that, otherwise return the last dirt, stone or
        // gravel we got
        Chunk chunk = world.getChunkFromBlockCoords(x, z);
        int lastDirt = TFWorld.SEALEVEL;
        int localX = x & (CHUNK_SIZE - 1);
        int localZ = z & (CHUNK_SIZE - 1);

        for (int y = TFWorld.SEALEVEL; y < TFWorld.CHUNKHEIGHT - 1; y++) {
            Block block = chunk.getBlock(localX, y, localZ);
            // grass = return immediately
            if (block == Blocks.grass) {
                return y + 1;
            } else if (isValidGroundBlock(block)) {
                lastDirt = y + 1;
            }
        }

        return lastDirt;
    }

    private static boolean isValidGroundBlock(Block block) {
        return block == Blocks.dirt || block == Blocks.stone
                || block == Blocks.gravel
                || block == Blocks.sandstone
                || block == Blocks.sand
                || block == Blocks.clay
                || block == TFBlocks.deadrock;
    }

    /**
     * Return a block if you want it to replace stone in the terrain generation
     */
    @Override
    public Block getStoneReplacementBlock() {
        return TFBlocks.deadrock;
    }

    /**
     * Metadata for the stone replacement block
     */
    @Override
    public byte getStoneReplacementMeta() {
        return 2;
    }

    @Override
    protected Achievement getRequiredAchievement() {
        return TFAchievementPage.twilightProgressGlacier;
    }
}
