/**
 *
 */
package twilightforest.biomes;

import java.util.Random;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.stats.Achievement;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;
import net.minecraft.world.gen.feature.WorldGenTaiga1;
import net.minecraft.world.gen.feature.WorldGenTaiga2;

import twilightforest.TFAchievementPage;
import twilightforest.TFFeature;
import twilightforest.entity.passive.EntityTFPenguin;
import twilightforest.world.TFGenPenguins;
import twilightforest.world.TFWorld;

/**
 * @author Ben
 *
 */
public class TFBiomeGlacier extends TFBiomeBase {

    /**
     * @param i
     */
    public TFBiomeGlacier(int i) {
        super(i);

        TFBiomeDecorator decorator = getTFBiomeDecorator();
        decorator.setTreesPerChunk(1);
        decorator.setGrassPerChunk(0);
        decorator.canopyPerChunk = -999;

        this.temperature = 0.0F;
        this.rainfall = 0.1F;

        spawnableCreatureList.add(new SpawnListEntry(EntityTFPenguin.class, 10, 4, 4));
    }

    @Override
    public WorldGenAbstractTree func_150567_a(Random random) {
        return random.nextInt(3) == 0 ? new WorldGenTaiga1() : new WorldGenTaiga2(true);
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
     * Decorate this biome. Weeeee should generate penguins
     */
    @Override
    public void decorate(World world, Random random, int mapX, int mapZ) {
        super.decorate(world, random, mapX, mapZ);

        TFGenPenguins penguins = new TFGenPenguins();
        if (random.nextInt(4) != 0) {
            return;
        }

        int x = mapX + random.nextInt(16) + 8;
        byte y = (byte) TFWorld.SEALEVEL;
        int z = mapZ + random.nextInt(16) + 8;
        penguins.generate(world, random, x, y, z);
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

        player.addPotionEffect(new PotionEffect(Potion.moveSlowdown.id, 100, 3));

        // hint monster?
        if (world.rand.nextInt(4) == 0) {
            TFFeature.iceTower.trySpawnHintMonster(world, player);
        }
    }
}
