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
import net.minecraft.world.gen.feature.WorldGenerator;

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

    private final TFGenHangingLamps hangingLampGenerator;
    private final TFGenLampposts lamppostGenerator;
    private final WorldGenTallGrass mushgloomGenerator;

    public TFBiomeFireflyForest(int biomeId) {
        super(biomeId);

        this.mushgloomGenerator = new WorldGenTallGrass(TFBlocks.plant, BlockTFPlant.META_MUSHGLOOM);
        this.hangingLampGenerator = new TFGenHangingLamps();
        this.lamppostGenerator = new TFGenLampposts();

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

        // the original range, -1 to 1 lets at most 1 flower attempt
        if (flowerCycles <= 0) {
            return;
        }

        int flowerType = switch (rand.nextInt(3)) {
            case 0 -> 1;
            case 1 -> 4;
            default -> 5;
        };
        genTallFlowers.func_150548_a(flowerType);

        int x = randomChunkCoordinate(rand, mapX);
        int z = randomChunkCoordinate(rand, mapZ);
        int y = rand.nextInt(world.getHeightValue(x, z) + FLOWER_HEIGHT_OFFSET);
        genTallFlowers.generate(world, rand, x, y, z);
    }

    private void generateMushglooms(World world, Random rand, int mapX, int mapZ) {
        if (rand.nextInt(MUSHGLOOM_CHANCE) == 0) {
            // mushglooms
            generateGroundDecoration(world, rand, mapX, mapZ, mushgloomGenerator);
        }
    }

    private void generateHangingLamps(World world, Random rand, int mapX, int mapZ) {
        // hanging lamps
        for (int i = 0; i < HANGING_LAMP_ATTEMPTS; i++) {
            int x = randomChunkCoordinate(rand, mapX);
            int z = randomChunkCoordinate(rand, mapZ);
            int y = TFWorld.SEALEVEL + rand.nextInt(TFWorld.CHUNKHEIGHT - TFWorld.SEALEVEL);
            hangingLampGenerator.generate(world, rand, x, y, z);
        }
    }

    private void generateLampposts(World world, Random rand, int mapX, int mapZ) {
        if (rand.nextInt(LAMPPOST_CHANCE) == 0) {
            generateGroundDecoration(world, rand, mapX, mapZ, lamppostGenerator);
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

    private void generateGroundDecoration(World world, Random rand, int mapX, int mapZ, WorldGenerator generator) {
        int x = randomChunkCoordinate(rand, mapX);
        int z = randomChunkCoordinate(rand, mapZ);
        int y = getGroundLevel(world, x, z);
        generator.generate(world, rand, x, y, z);
    }

    private static int randomChunkCoordinate(Random rand, int mapCoordinate) {
        return mapCoordinate + rand.nextInt(CHUNK_SIZE) + CHUNK_OFFSET;
    }

    public int getGroundLevel(World world, int x, int z) {
        // go from sea level up. If we get grass, return that, otherwise return the last dirt, stone or
        // gravel we got
        Chunk chunk = world.getChunkFromBlockCoords(x, z);
        int lastDirt = TFWorld.SEALEVEL;
        for (int y = TFWorld.SEALEVEL; y < TFWorld.CHUNKHEIGHT - 1; y++) {
            Block block = chunk.getBlock(x & (CHUNK_SIZE - 1), y, z & (CHUNK_SIZE - 1));
            // grass = return immediately
            if (block == Blocks.grass) {
                return y + 1;
            }
            if (block == Blocks.dirt || block == Blocks.stone || block == Blocks.gravel) {
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
