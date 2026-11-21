package twilightforest.biomes;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.stats.Achievement;
import net.minecraft.util.MathHelper;
import net.minecraft.world.ColorizerFoliage;
import net.minecraft.world.ColorizerGrass;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;
import net.minecraft.world.gen.feature.WorldGenShrub;
import net.minecraft.world.gen.feature.WorldGenTallGrass;
import net.minecraft.world.gen.feature.WorldGenVines;
import net.minecraft.world.gen.feature.WorldGenerator;

import twilightforest.TFAchievementPage;
import twilightforest.TFFeature;
import twilightforest.block.BlockTFPlant;
import twilightforest.block.TFBlocks;
import twilightforest.entity.EntityTFMosquitoSwarm;
import twilightforest.world.TFGenHugeLilyPad;
import twilightforest.world.TFWorld;

public class TFBiomeSwamp extends TFBiomeBase {

    private static final int MONSTER_SPAWN_RATE = 20;
    private static final int CHUNK_SIZE = 16;
    private static final int DECORATION_OFFSET = 8;
    private static final int VINES_PER_CHUNK = 50;
    private static final int HUGE_LILY_PADS_PER_CHUNK = 25;
    private static final int HUGE_WATER_LILIES_PER_CHUNK = 2;
    private static final int PROGRESSION_INTERVAL = 60;

    Random monsterRNG = new Random(53439L);
    ArrayList<SpawnListEntry> emptyList = new ArrayList<>();

    WorldGenVines worldgenvines = new WorldGenVines();
    WorldGenerator hugeLilyPadGen = new TFGenHugeLilyPad();
    WorldGenerator hugeWaterLilyGen = new TFGenHugeWaterLily();

    public TFBiomeSwamp(int biomeId) {
        super(biomeId);

        temperature = 0.8F;
        rainfall = 0.9F;
        waterColorMultiplier = 0xE0FFAE;

        TFBiomeDecorator decorator = getTFBiomeDecorator();
        decorator.setDeadBushPerChunk(1);
        decorator.setMushroomsPerChunk(8);
        decorator.setReedsPerChunk(10);
        decorator.setClayPerChunk(1);
        decorator.setTreesPerChunk(2);
        decorator.setWaterlilyPerChunk(20);
        decorator.canopyPerChunk = -999;
        decorator.lakesPerChunk = 2;
        decorator.mangrovesPerChunk = 3;

        spawnableMonsterList.add(new SpawnListEntry(EntityTFMosquitoSwarm.class, 10, 1, 1));
        spawnableMonsterList.add(new SpawnListEntry(EntityCreeper.class, 10, 4, 4));
        spawnableMonsterList.add(new SpawnListEntry(EntityZombie.class, 10, 4, 4));
    }

    @Override
    public WorldGenAbstractTree func_150567_a(Random random) {
        return random.nextInt(3) == 0 ? new WorldGenShrub(3, 0) : worldGeneratorSwamp;
    }

    /**
     * Ferns!
     */
    @Override
    public WorldGenerator getRandomWorldGenForGrass(Random random) {
        if (random.nextInt(4) == 0) {
            return new WorldGenTallGrass(Blocks.tallgrass, 2);
        }
        if (random.nextInt(4) == 0) {
            return new WorldGenTallGrass(TFBlocks.plant, BlockTFPlant.META_MAYAPPLE);
        }
        return new WorldGenTallGrass(Blocks.tallgrass, 1);
    }

    /**
     * Decorate this biome. This is stolen from the jungle code
     */
    @Override
    public void decorate(World world, Random random, int mapX, int mapZ) {
        super.decorate(world, random, mapX, mapZ);
        generateVines(world, random, mapX, mapZ);
        generateHugeLilyPads(world, random, mapX, mapZ);
        generateHugeWaterLilies(world, random, mapX, mapZ);
    }

    private void generateVines(World world, Random random, int mapX, int mapZ) {
        for (int i = 0; i < VINES_PER_CHUNK; i++) {
            int x = mapX + random.nextInt(CHUNK_SIZE) + DECORATION_OFFSET;
            int z = mapZ + random.nextInt(CHUNK_SIZE) + DECORATION_OFFSET;
            // keep the legacy sea level cast so this doesnt alter generated coords
            int y = (byte) TFWorld.SEALEVEL;
            worldgenvines.generate(world, random, x, y, z);
        }
    }

    private void generateHugeLilyPads(World world, Random random, int mapX, int mapZ) {
        for (int i = 0; i < HUGE_LILY_PADS_PER_CHUNK; i++) {
            int x = mapX + random.nextInt(CHUNK_SIZE) + DECORATION_OFFSET;
            int z = mapZ + random.nextInt(CHUNK_SIZE) + DECORATION_OFFSET;
            hugeLilyPadGen.generate(world, random, x, TFWorld.SEALEVEL, z);
        }
    }

    private void generateHugeWaterLilies(World world, Random random, int mapX, int mapZ) {
        for (int i = 0; i < HUGE_WATER_LILIES_PER_CHUNK; i++) {
            int x = mapX + random.nextInt(CHUNK_SIZE) + DECORATION_OFFSET;
            int z = mapZ + random.nextInt(CHUNK_SIZE) + DECORATION_OFFSET;
            hugeWaterLilyGen.generate(world, random, x, TFWorld.SEALEVEL, z);
        }
    }

    /**
     * Provides the basic grass color based on the biome temperature and rainfall
     */
    @Override
    public int getBiomeGrassColor(int x, int y, int z) {
        double temperature = MathHelper.clamp_float(getFloatTemperature(x, y, z), 0.0F, 1.0F);
        double rainfall = MathHelper.clamp_float(getFloatRainfall(), 0.0F, 1.0F);
        return ((ColorizerGrass.getGrassColor(temperature, rainfall) & 0xFEFEFE) + 0x4E0E4E) / 2;
    }

    /**
     * Provides the basic foliage color based on the biome temperature and rainfall
     */
    @Override
    public int getBiomeFoliageColor(int x, int y, int z) {
        double temperature = MathHelper.clamp_float(getFloatTemperature(x, y, z), 0.0F, 1.0F);
        double rainfall = MathHelper.clamp_float(getFloatRainfall(), 0.0F, 1.0F);
        return ((ColorizerFoliage.getFoliageColor(temperature, rainfall) & 0xFEFEFE) + 0x4E0E4E) / 2;
    }

    /**
     * Returns the correspondent list of the EnumCreatureType informed.
     */
    @Override
    public List<BiomeGenBase.SpawnListEntry> getSpawnableList(EnumCreatureType creatureType) {
        if (creatureType == EnumCreatureType.monster) {
            // keep the independent seeded rng and advance it only for monster spawn queries
            return monsterRNG.nextInt(MONSTER_SPAWN_RATE) == 0 ? spawnableMonsterList : emptyList;
        }
        if (creatureType == EnumCreatureType.creature) {
            return spawnableCreatureList;
        }
        if (creatureType == EnumCreatureType.waterCreature) {
            return spawnableWaterCreatureList;
        }
        if (creatureType == EnumCreatureType.ambient) {
            return spawnableCaveCreatureList;
        }
        return null;
    }

    /**
     * If there is a required achievement to be here, return it, otherwise return null
     */
    @Override
    protected Achievement getRequiredAchievement() {
        return TFAchievementPage.twilightProgressLich;
    }

    /**
     * Do something bad to a player in the wrong biome.
     */
    @Override
    public void enforceProgession(EntityPlayer player, World world) {
        if (world.isRemote || world.getWorldTime() % PROGRESSION_INTERVAL != 0) {
            return;
        }

        PotionEffect currentHunger = player.getActivePotionEffect(Potion.hunger);
        int hungerLevel = currentHunger != null ? currentHunger.getAmplifier() + 1 : 1;
        player.addPotionEffect(new PotionEffect(Potion.hunger.id, 100, hungerLevel));

        // hint monster?
        if (world.rand.nextInt(4) == 0) {
            TFFeature.labyrinth.trySpawnHintMonster(world, player);
        }
    }
}
