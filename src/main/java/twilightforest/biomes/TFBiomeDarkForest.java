package twilightforest.biomes;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.stats.Achievement;
import net.minecraft.util.MathHelper;
import net.minecraft.world.ColorizerFoliage;
import net.minecraft.world.ColorizerGrass;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeDecorator;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;
import net.minecraft.world.gen.feature.WorldGenShrub;

import twilightforest.TFAchievementPage;
import twilightforest.TFFeature;
import twilightforest.entity.EntityTFKingSpider;
import twilightforest.entity.EntityTFKobold;
import twilightforest.entity.EntityTFMistWolf;
import twilightforest.entity.EntityTFSkeletonDruid;

public class TFBiomeDarkForest extends TFBiomeBase {

    private static final int MONSTER_SPAWN_RATE = 20;
    private static final int PROGRESSION_INTERVAL = 60;
    private static final int DARK_FOREST_COLOR_TINT = 0x1E0E4E;

    Random monsterRNG;
    ArrayList<SpawnListEntry> emptyList = new ArrayList<>();

    public TFBiomeDarkForest(int biomeId) {
        super(biomeId);

        temperature = 0.7F;
        rainfall = 0.8F;

        TFBiomeDecorator decorator = getTFBiomeDecorator();
        decorator.canopyPerChunk = 5.5F;
        decorator.setTreesPerChunk(10);
        decorator.setGrassPerChunk(-99);
        decorator.setFlowersPerChunk(-99);
        decorator.setMushroomsPerChunk(2);
        decorator.setDeadBushPerChunk(10);

        rootHeight = 0.05F;
        heightVariation = 0.05F;

        monsterRNG = new Random();

        spawnableMonsterList.add(new SpawnListEntry(EntityEnderman.class, 1, 1, 4));
        spawnableMonsterList.add(new SpawnListEntry(EntityZombie.class, 5, 1, 4));
        spawnableMonsterList.add(new SpawnListEntry(EntitySkeleton.class, 5, 1, 4));
        spawnableMonsterList.add(new SpawnListEntry(EntityTFMistWolf.class, 10, 1, 4));
        spawnableMonsterList.add(new SpawnListEntry(EntityTFSkeletonDruid.class, 10, 1, 4));
        spawnableMonsterList.add(new SpawnListEntry(EntityTFKingSpider.class, 10, 1, 4));
        spawnableMonsterList.add(new SpawnListEntry(EntityTFKobold.class, 10, 4, 8));
        spawnableMonsterList.add(new SpawnListEntry(EntityWitch.class, 1, 1, 1));

        decorator.generateLakes = false;
    }

    /**
     * We have our own decorator since we have strange generation
     */
    @Override
    public BiomeDecorator createBiomeDecorator() {
        return new TFDarkForestBiomeDecorator();
    }

    /**
     * Occasional shrub, no big trees
     */
    @Override
    public WorldGenAbstractTree func_150567_a(Random random) {
        if (random.nextInt(5) == 0) {
            return new WorldGenShrub(3, 0);
        }
        if (random.nextInt(8) == 0) {
            return birchGen;
        }
        return worldGeneratorTrees;
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
        return ((color & 0xFEFEFE) + DARK_FOREST_COLOR_TINT) / 2;
    }

    /**
     * Returns the correspondent list of the EnumCreatureType informed.
     */
    @Override
    public List<BiomeGenBase.SpawnListEntry> getSpawnableList(EnumCreatureType creatureType) {
        // if is is monster, then only give it the real list 1/MONSTER_SPAWN_RATE of the time
        if (creatureType == EnumCreatureType.monster) {
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
     * This just seems to act as a fire discouragement
     */
    @Override
    public boolean isHighHumidity() {
        return true;
    }

    /**
     * If there is a required achievement to be here, return it, otherwise return null
     */
    @Override
    protected Achievement getRequiredAchievement() {
        return TFAchievementPage.twilightProgressHydra;
    }

    /**
     * Do something bad to a player in the wrong biome.
     */
    @Override
    public void enforceProgession(EntityPlayer player, World world) {
        if (world.isRemote || world.getWorldTime() % PROGRESSION_INTERVAL != 0) {
            return;
        }

        player.addPotionEffect(new PotionEffect(Potion.blindness.id, 100, 0));

        // hint monster?
        if (world.rand.nextInt(4) == 0) {
            TFFeature.tfStronghold.trySpawnHintMonster(world, player);
        }
    }
}
