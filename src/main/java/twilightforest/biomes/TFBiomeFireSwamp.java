package twilightforest.biomes;

import java.util.Random;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.stats.Achievement;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;
import net.minecraft.world.gen.feature.WorldGenShrub;
import net.minecraft.world.gen.feature.WorldGenVines;

import twilightforest.TFAchievementPage;
import twilightforest.TFFeature;
import twilightforest.block.BlockTFFireJet;
import twilightforest.block.TFBlocks;
import twilightforest.world.TFGenFireJet;
import twilightforest.world.TFWorld;

public class TFBiomeFireSwamp extends TFBiomeBase {

    private static final int VINES_PER_CHUNK = 50;
    private static final int CHUNK_WIDTH = 16;
    private static final int CHUNK_DECORATION_OFFSET = 8;
    private static final int PROGRESSION_INTERVAL = 60;

    protected TFBiomeFireSwamp(int biomeId) {
        super(biomeId);

        this.temperature = 1.0F;
        this.rainfall = 0.4F;

        TFBiomeDecorator decorator = getTFBiomeDecorator();
        decorator.setDeadBushPerChunk(2);
        decorator.setMushroomsPerChunk(8);
        decorator.setReedsPerChunk(4);
        decorator.setClayPerChunk(1);
        decorator.setTreesPerChunk(3);
        decorator.setWaterlilyPerChunk(6);
        decorator.canopyPerChunk = -999;
        decorator.lavaPoolChance = 0.125F;
        decorator.mangrovesPerChunk = 3;

        this.waterColorMultiplier = 0x6C2C2C;
    }

    /**
     * Gets a WorldGen appropriate for this biome.
     */
    @Override
    public WorldGenAbstractTree func_150567_a(Random random) {
        return random.nextInt(3) == 0 ? new WorldGenShrub(3, 0) : worldGeneratorSwamp;
    }

    /**
     * Decorate this biome. This is stolen from the jungle code
     */
    @Override
    public void decorate(World world, Random random, int mapX, int mapZ) {
        super.decorate(world, random, mapX, mapZ);

        TFFeature nearFeature = TFFeature.getNearestFeature(mapX >> 4, mapZ >> 4, world);
        if (!nearFeature.areChunkDecorationsEnabled) {
            return;
        }

        generateVines(world, random, mapX, mapZ);
        generateFireJets(world, random, mapX, mapZ);
    }

    private void generateVines(World world, Random random, int mapX, int mapZ) {
        WorldGenVines vines = new WorldGenVines();

        for (int i = 0; i < VINES_PER_CHUNK; i++) {
            int x = mapX + random.nextInt(CHUNK_WIDTH) + CHUNK_DECORATION_OFFSET;
            byte y = (byte) TFWorld.SEALEVEL;
            int z = mapZ + random.nextInt(CHUNK_WIDTH) + CHUNK_DECORATION_OFFSET;
            vines.generate(world, random, x, y, z);
        }
    }

    private void generateFireJets(World world, Random random, int mapX, int mapZ) {
        TFGenFireJet smoker = new TFGenFireJet(TFBlocks.fireJet, BlockTFFireJet.META_SMOKER);
        if (random.nextInt(4) == 0) {
            int x = mapX + random.nextInt(CHUNK_WIDTH) + CHUNK_DECORATION_OFFSET;
            byte y = (byte) TFWorld.SEALEVEL;
            int z = mapZ + random.nextInt(CHUNK_WIDTH) + CHUNK_DECORATION_OFFSET;
            smoker.generate(world, random, x, y, z);
        }

        TFGenFireJet fireJet = new TFGenFireJet(TFBlocks.fireJet, BlockTFFireJet.META_JET_IDLE);
        int x = mapX + random.nextInt(CHUNK_WIDTH) + CHUNK_DECORATION_OFFSET;
        byte y = (byte) TFWorld.SEALEVEL;
        int z = mapZ + random.nextInt(CHUNK_WIDTH) + CHUNK_DECORATION_OFFSET;
        fireJet.generate(world, random, x, y, z);
    }

    /**
     * Provides the basic grass color based on the biome temperature and rainfall
     */
    @Override
    public int getBiomeGrassColor(int x, int y, int z) {
        return 0x572E23;
    }

    /**
     * Provides the basic foliage color based on the biome temperature and rainfall
     */
    @Override
    public int getBiomeFoliageColor(int x, int y, int z) {
        return 0x64260F;
    }

    /**
     * If there is a required achievement to be here, return it, otherwise return null
     */
    @Override
    protected Achievement getRequiredAchievement() {
        return TFAchievementPage.twilightProgressLabyrinth;
    }

    /**
     * Do something bad to a player in the wrong biome.
     */
    @Override
    public void enforceProgession(EntityPlayer player, World world) {
        if (world.isRemote || world.getWorldTime() % PROGRESSION_INTERVAL != 0) {
            return;
        }

        player.setFire(8);

        // hint monster?
        if (world.rand.nextInt(4) == 0) {
            TFFeature.hydraLair.trySpawnHintMonster(world, player);
        }
    }
}
