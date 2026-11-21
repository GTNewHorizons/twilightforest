package twilightforest.biomes;

import java.util.Random;

import net.minecraft.block.BlockFlower;
import net.minecraft.init.Blocks;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;
import net.minecraft.world.gen.feature.WorldGenBigTree;
import net.minecraft.world.gen.feature.WorldGenTallGrass;
import net.minecraft.world.gen.feature.WorldGenVines;
import net.minecraft.world.gen.feature.WorldGenerator;

import twilightforest.block.BlockTFPlant;
import twilightforest.block.TFBlocks;
import twilightforest.world.TFGenLargeRainboak;
import twilightforest.world.TFGenSmallRainboak;
import twilightforest.world.TFWorld;

public class TFBiomeEnchantedForest extends TFBiomeBase {

    private static final int COLOR_CENTER_SPACING = 256;
    private static final int COLOR_CENTER_OFFSET = 8;
    private static final int COLOR_RING_STEP = 64;
    private static final int COLOR_RING_PERIOD = 512;
    private static final int MAX_COLOR_COMPONENT = 255;
    private static final int COLOR_FLICKER_RANGE = 32;
    private static final int COLOR_FLICKER_OFFSET = 16;
    private static final int VINES_PER_CHUNK = 20;
    private static final int TALL_FERNS_PER_CHUNK = 20;

    Random colorRNG;

    public TFBiomeEnchantedForest(int biomeId) {
        super(biomeId);
        colorRNG = new Random();

        TFBiomeDecorator decorator = getTFBiomeDecorator();
        decorator.setGrassPerChunk(12);
        decorator.setFlowersPerChunk(8);
    }

    /**
     * Provides the basic grass color based on the biome temperature and rainfall
     */
    @Override
    public int getBiomeGrassColor(int x, int y, int z) {
        return (super.getBiomeGrassColor(x, y, z) & 0xFFFF00) + getEnchantedColor(x, z);
    }

    /**
     * Provides the basic foliage color based on the biome temperature and rainfall
     */
    @Override
    public int getBiomeFoliageColor(int x, int y, int z) {
        return (super.getBiomeFoliageColor(x, y, z) & 0xFFFF00) + getEnchantedColor(x, z);
    }

    /**
     * Find a number between 0 and 255. currently returns concentric circles from the biome center
     */
    private int getEnchantedColor(int x, int z) {
        // colour rings are centered on coordinates congruent to 8 256
        int centerX = COLOR_CENTER_SPACING * Math.round((x - COLOR_CENTER_OFFSET) / (float) COLOR_CENTER_SPACING)
                + COLOR_CENTER_OFFSET;
        int centerZ = COLOR_CENTER_SPACING * Math.round((z - COLOR_CENTER_OFFSET) / (float) COLOR_CENTER_SPACING)
                + COLOR_CENTER_OFFSET;

        int distance = (int) MathHelper.sqrt_float((centerX - x) * (centerX - x) + (centerZ - z) * (centerZ - z));
        int color = (distance * COLOR_RING_STEP) % COLOR_RING_PERIOD;

        if (color > MAX_COLOR_COMPONENT) {
            color = COLOR_RING_PERIOD - 1 - color;
        }
        color = MAX_COLOR_COMPONENT - color;

        int flicker = colorRNG.nextInt(COLOR_FLICKER_RANGE) - COLOR_FLICKER_OFFSET;
        int flickeredColor = color + flicker;

        // apply flicker only when the result stays within the blue colour channel
        return flickeredColor >= 0 && flickeredColor <= MAX_COLOR_COMPONENT ? flickeredColor : color;
    }

    /**
     * Occasional rainboaks, birches and big trees
     */
    @Override
    public WorldGenAbstractTree func_150567_a(Random random) {
        if (random.nextInt(15) == 0) {
            return new TFGenSmallRainboak();
        }
        if (random.nextInt(50) == 0) {
            return new TFGenLargeRainboak();
        }
        if (random.nextInt(5) == 0) {
            return birchGen;
        }
        if (random.nextInt(10) == 0) {
            return new WorldGenBigTree(false);
        }
        return worldGeneratorTrees;
    }

    /**
     * EVEN MOAR FERNZ!
     */
    @Override
    public WorldGenerator getRandomWorldGenForGrass(Random random) {
        if (random.nextInt(3) > 0) {
            return new WorldGenTallGrass(Blocks.tallgrass, 2);
        }
        if (random.nextInt(3) == 0) {
            return new WorldGenTallGrass(TFBlocks.plant, BlockTFPlant.META_FIDDLEHEAD);
        }
        return new WorldGenTallGrass(Blocks.tallgrass, 1);
    }

    /**
     * Decorate this biome with extra decorations!
     */
    @Override
    public void decorate(World world, Random random, int mapX, int mapZ) {
        generateVines(world, random, mapX, mapZ);
        generateTallFerns(world, random, mapX, mapZ);
        super.decorate(world, random, mapX, mapZ);
    }

    private void generateVines(World world, Random random, int mapX, int mapZ) {
        WorldGenVines vineGenerator = new WorldGenVines();
        for (int i = 0; i < VINES_PER_CHUNK; i++) {
            int x = mapX + random.nextInt(16) + 8;
            byte y = (byte) TFWorld.SEALEVEL;
            int z = mapZ + random.nextInt(16) + 8;
            vineGenerator.generate(world, random, x, y, z);
        }
    }

    private void generateTallFerns(World world, Random random, int mapX, int mapZ) {
        // tall ferns
        genTallFlowers.func_150548_a(3);

        for (int i = 0; i < TALL_FERNS_PER_CHUNK; i++) {
            int x = mapX + random.nextInt(16) + 8;
            int z = mapZ + random.nextInt(16) + 8;
            int y = random.nextInt(world.getHeightValue(x, z) + 32);
            genTallFlowers.generate(world, random, x, y, z);
        }
    }

    /**
     * Customize flower colors
     */
    @Override
    public String func_150572_a(Random random, int x, int y, int z) {
        if (random.nextInt(3) > 0) {
            return BlockFlower.field_149859_a[1];
        }
        return random.nextBoolean() ? BlockFlower.field_149859_a[2] : BlockFlower.field_149859_a[3];
    }
}
