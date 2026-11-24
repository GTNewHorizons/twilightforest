/**
 *
 */
package twilightforest.biomes;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.stats.Achievement;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;
import net.minecraft.world.gen.feature.WorldGenTaiga1;
import net.minecraft.world.gen.feature.WorldGenTaiga2;

import twilightforest.TFAchievementPage;
import twilightforest.TFFeature;
import twilightforest.entity.EntityTFWinterWolf;
import twilightforest.entity.EntityTFYeti;
import twilightforest.world.TFGenLargeWinter;

/**
 * @author Ben
 *
 */
public class TFBiomeSnow extends TFBiomeBase {

    private static final int MONSTER_SPAWN_RATE = 10;
    Random monsterRNG = new Random(53439L);
    ArrayList<SpawnListEntry> emptyList = new ArrayList<>();

    /**
     * @param i
     */
    public TFBiomeSnow(int i) {
        super(i);

        TFBiomeDecorator decorator = getTFBiomeDecorator();
        decorator.setTreesPerChunk(7);
        decorator.setGrassPerChunk(1);

        this.temperature = 0.125F;
        this.rainfall = 0.9F;

        decorator.canopyPerChunk = -999;
        decorator.generateLakes = false;

        this.spawnableMonsterList.add(new SpawnListEntry(EntityTFYeti.class, 20, 4, 4));
        this.spawnableMonsterList.add(new SpawnListEntry(EntityTFWinterWolf.class, 5, 1, 4));
    }

    @Override
    public WorldGenAbstractTree func_150567_a(Random random) {
        if (random.nextInt(3) == 0) {
            return new WorldGenTaiga1();
        }
        if (random.nextInt(8) == 0) {
            return new TFGenLargeWinter();
        }
        return new WorldGenTaiga2(true);
    }

    /**
     * Let it snow!
     */
    @Override
    public boolean getEnableSnow() {
        return true;
    }

    /**
     * Required for actual snow?
     */
    @Override
    public boolean canSpawnLightningBolt() {
        return false;
    }

    /**
     * Returns the correspondent list of the EnumCreatureType informed.
     */
    @Override
    public List<BiomeGenBase.SpawnListEntry> getSpawnableList(EnumCreatureType creatureType) {
        // if is is monster, then only give it the real list 1/MONSTER_SPAWN_RATE of the time
        if (creatureType == EnumCreatureType.monster) {
            return monsterRNG.nextInt(MONSTER_SPAWN_RATE) == 0 ? this.spawnableMonsterList : emptyList;
        }
        if (creatureType == EnumCreatureType.creature) {
            return this.spawnableCreatureList;
        }
        if (creatureType == EnumCreatureType.waterCreature) {
            return this.spawnableWaterCreatureList;
        }
        if (creatureType == EnumCreatureType.ambient) {
            return this.spawnableCaveCreatureList;
        }
        return null;
    }

    /**
     * If there is a required achievement to be here, return it, otherwise return null
     */
    @Override
    protected Achievement getRequiredAchievement() {
        return TFAchievementPage.twilightProgressUrghast;
    }

    /**
     * Do something bad to a player in the wrong biome.
     */
    @Override
    public void enforceProgession(EntityPlayer player, World world) {
        if (world.isRemote || world.getWorldTime() % 60 != 0) {
            return;
        }

        player.addPotionEffect(new PotionEffect(Potion.moveSlowdown.id, 100, 2));

        // hint monster?
        if (world.rand.nextInt(4) == 0) {
            TFFeature.yetiCave.trySpawnHintMonster(world, player);
        }
    }
}
