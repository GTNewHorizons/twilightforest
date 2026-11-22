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
    private static final long MONSTER_RANDOM_SEED = 53439L;
    private static final int CHUNK_SIZE = 16;
    private static final int DECORATION_OFFSET = 8;
    private static final int VINES_PER_CHUNK = 50;
    private static final int HUGE_LILY_PADS_PER_CHUNK = 25;
    private static final int HUGE_WATER_LILIES_PER_CHUNK = 2;
    private static final int PROGRESSION_INTERVAL = 60;
    private static final int HUNGER_DURATION = 100;
    private static final int HINT_MONSTER_CHANCE = 4;
    private static final int SWAMP_COLOR_TINT = 0x4E0E4E;

    private final Random monsterRandom = new Random(MONSTER_RANDOM_SEED);
    private final ArrayList<SpawnListEntry> emptyMonsterSpawns = new ArrayList<>();

    private final WorldGenVines vineGenerator = new WorldGenVines();
    private final WorldGenerator hugeLilyPadGenerator = new TFGenHugeLilyPad();
    private final WorldGenerator hugeWaterLilyGenerator = new TFGenHugeWaterLily();

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

        // keep the legacy sea level cast so this doesnt alter generated coords
        generateDecorations(world, random, mapX, mapZ, vineGenerator, VINES_PER_CHUNK, (byte) TFWorld.SEALEVEL);
        generateDecorations(
                world,
                random,
                mapX,
                mapZ,
                hugeLilyPadGenerator,
                HUGE_LILY_PADS_PER_CHUNK,
                TFWorld.SEALEVEL);
        generateDecorations(
                world,
                random,
                mapX,
                mapZ,
                hugeWaterLilyGenerator,
                HUGE_WATER_LILIES_PER_CHUNK,
                TFWorld.SEALEVEL);
    }

    private static void generateDecorations(World world, Random random, int mapX, int mapZ, WorldGenerator generator,
            int attempts, int y) {
        for (int i = 0; i < attempts; i++) {
            int x = mapX + random.nextInt(CHUNK_SIZE) + DECORATION_OFFSET;
            int z = mapZ + random.nextInt(CHUNK_SIZE) + DECORATION_OFFSET;
            generator.generate(world, random, x, y, z);
        }
    }

    /**
     * Provides the basic grass color based on the biome temperature and rainfall
     */
    @Override
    public int getBiomeGrassColor(int x, int y, int z) {
        double temperature = MathHelper.clamp_float(getFloatTemperature(x, y, z), 0.0F, 1.0F);
        double rainfall = MathHelper.clamp_float(getFloatRainfall(), 0.0F, 1.0F);
        return tintColor(ColorizerGrass.getGrassColor(temperature, rainfall));
    }

    /**
     * Provides the basic foliage color based on the biome temperature and rainfall
     */
    @Override
    public int getBiomeFoliageColor(int x, int y, int z) {
        double temperature = MathHelper.clamp_float(getFloatTemperature(x, y, z), 0.0F, 1.0F);
        double rainfall = MathHelper.clamp_float(getFloatRainfall(), 0.0F, 1.0F);
        return tintColor(ColorizerFoliage.getFoliageColor(temperature, rainfall));
    }

    private static int tintColor(int color) {
        return ((color & 0xFEFEFE) + SWAMP_COLOR_TINT) / 2;
    }

    /**
     * Returns the correspondent list of the EnumCreatureType informed.
     */
    @Override
    public List<BiomeGenBase.SpawnListEntry> getSpawnableList(EnumCreatureType creatureType) {
        if (creatureType == EnumCreatureType.monster) {
            // Keep the independent seeded RNG and advance it only for monster spawn queries.
            return monsterRandom.nextInt(MONSTER_SPAWN_RATE) == 0 ? spawnableMonsterList : emptyMonsterSpawns;
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
        player.addPotionEffect(new PotionEffect(Potion.hunger.id, HUNGER_DURATION, hungerLevel));

        // hint monster?
        if (world.rand.nextInt(HINT_MONSTER_CHANCE) == 0) {
            TFFeature.labyrinth.trySpawnHintMonster(world, player);
        }
    }
}
