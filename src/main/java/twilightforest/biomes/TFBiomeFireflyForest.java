package twilightforest.biomes;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFlower;
import net.minecraft.init.Blocks;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.feature.WorldGenPumpkin;
import net.minecraft.world.gen.feature.WorldGenTallGrass;

import twilightforest.block.BlockTFPlant;
import twilightforest.block.TFBlocks;
import twilightforest.world.TFGenHangingLamps;
import twilightforest.world.TFGenLampposts;
import twilightforest.world.TFWorld;

public class TFBiomeFireflyForest extends TFBiomeTwilightForest {

    private static final int LAMPPOST_CHANCE = 4;
    private static final int MUSHGLOOM_CHANCE = 24;
    private static final int PUMPKIN_CHANCE = 32;
    private static final int HANGING_LAMP_ATTEMPTS = 30;
    private static final int CHUNK_SIZE = 16;
    private static final int CHUNK_OFFSET = 8;
    private static final int FLOWER_HEIGHT_OFFSET = 32;
    private static final double FLOWER_NOISE_SCALE = 48.0D;

    TFGenHangingLamps tfGenHangingLamps;
    TFGenLampposts tfGenLampposts;
    WorldGenTallGrass worldGenMushgloom;

    public TFBiomeFireflyForest(int biomeId) {
        super(biomeId);

        this.worldGenMushgloom = new WorldGenTallGrass(TFBlocks.plant, BlockTFPlant.META_MUSHGLOOM);
        this.tfGenHangingLamps = new TFGenHangingLamps();
        this.tfGenLampposts = new TFGenLampposts();

        this.temperature = 0.5F;
        this.rainfall = 1.0F;

        TFBiomeDecorator decorator = getTFBiomeDecorator();
        decorator.flowersPerChunk = 4;
        decorator.grassPerChunk = 1;
        decorator.setTreesPerChunk(2);
    }

    /**
     * Decorate this biome. This is stolen from the flower forest code
     */
    @Override
    public void decorate(World world, Random rand, int mapX, int mapZ) {
        generateTallFlowers(world, rand, mapX, mapZ);
        super.decorate(world, rand, mapX, mapZ);
        generateMushglooms(world, rand, mapX, mapZ);
        generateHangingLamps(world, rand, mapX, mapZ);
        generateLampposts(world, rand, mapX, mapZ);
        generatePumpkins(world, rand, mapX, mapZ);
    }

    private void generateTallFlowers(World world, Random rand, int mapX, int mapZ) {
        int flowerCycles = rand.nextInt(3) - 1;

        // the original range, -1 to 1 permits at most one flower attempt
        if (flowerCycles <= 0) {
            return;
        }

        switch (rand.nextInt(3)) {
            case 0:
                genTallFlowers.func_150548_a(1);
                break;
            case 1:
                genTallFlowers.func_150548_a(4);
                break;
            default:
                genTallFlowers.func_150548_a(5);
                break;
        }

        int x = randomChunkCoordinate(rand, mapX);
        int z = randomChunkCoordinate(rand, mapZ);
        int y = rand.nextInt(world.getHeightValue(x, z) + FLOWER_HEIGHT_OFFSET);
        genTallFlowers.generate(world, rand, x, y, z);
    }

    private void generateMushglooms(World world, Random rand, int mapX, int mapZ) {
        if (rand.nextInt(MUSHGLOOM_CHANCE) == 0) {
            int x = randomChunkCoordinate(rand, mapX);
            int z = randomChunkCoordinate(rand, mapZ);
            int y = getGroundLevel(world, x, z);
            // mushglooms
            worldGenMushgloom.generate(world, rand, x, y, z);
        }
    }

    private void generateHangingLamps(World world, Random rand, int mapX, int mapZ) {
        // hanging lamps
        for (int i = 0; i < HANGING_LAMP_ATTEMPTS; i++) {
            int x = randomChunkCoordinate(rand, mapX);
            int z = randomChunkCoordinate(rand, mapZ);
            int y = TFWorld.SEALEVEL + rand.nextInt(TFWorld.CHUNKHEIGHT - TFWorld.SEALEVEL);
            tfGenHangingLamps.generate(world, rand, x, y, z);
        }
    }

    private void generateLampposts(World world, Random rand, int mapX, int mapZ) {
        if (rand.nextInt(LAMPPOST_CHANCE) == 0) {
            int x = randomChunkCoordinate(rand, mapX);
            int z = randomChunkCoordinate(rand, mapZ);
            int y = getGroundLevel(world, x, z);
            tfGenLampposts.generate(world, rand, x, y, z);
        }
    }

    private void generatePumpkins(World world, Random rand, int mapX, int mapZ) {
        // extra pumpkins (should they be lit?)
        if (rand.nextInt(PUMPKIN_CHANCE) == 0) {
            int x = randomChunkCoordinate(rand, mapX);
            int z = randomChunkCoordinate(rand, mapZ);
            int y = getGroundLevel(world, x, z);
            new WorldGenPumpkin().generate(world, rand, x, y, z);
        }
    }

    private int randomChunkCoordinate(Random rand, int mapCoordinate) {
        return mapCoordinate + rand.nextInt(CHUNK_SIZE) + CHUNK_OFFSET;
    }

    public int getGroundLevel(World world, int x, int z) {
        // go from sea level up. If we get grass, return that, otherwise return the last dirt, stone or
        // gravel we got
        Chunk chunk = world.getChunkFromBlockCoords(x, z);
        int lastDirt = TFWorld.SEALEVEL;
        for (int y = TFWorld.SEALEVEL; y < TFWorld.CHUNKHEIGHT - 1; y++) {
            Block blockID = chunk.getBlock(x & 15, y, z & 15);
            // grass = return immediately
            if (blockID == Blocks.grass) {
                return y + 1;
            } else if (blockID == Blocks.dirt || blockID == Blocks.stone || blockID == Blocks.gravel) {
                lastDirt = y + 1;
            }
        }

        return lastDirt;
    }

    /**
     * Every color flower!
     */
    @Override
    public String func_150572_a(Random rand, int x, int y, int z) {
        double flowerVar = MathHelper.clamp_double(
                (1.0D + plantNoise.func_151601_a(x / FLOWER_NOISE_SCALE, z / FLOWER_NOISE_SCALE)) / 2.0D,
                0.0D,
                0.9999D);
        int flowerIndex = (int) (flowerVar * (double) BlockFlower.field_149859_a.length);

        if (flowerIndex == 1) {
            flowerIndex = 0;
        }

        return BlockFlower.field_149859_a[flowerIndex];
    }
}
