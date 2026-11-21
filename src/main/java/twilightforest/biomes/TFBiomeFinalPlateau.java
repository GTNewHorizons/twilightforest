package twilightforest.biomes;

import net.minecraft.block.Block;
import net.minecraft.stats.Achievement;

import twilightforest.TFAchievementPage;
import twilightforest.block.TFBlocks;
import twilightforest.entity.passive.EntityTFRaven;

public class TFBiomeFinalPlateau extends TFBiomeBase {

    private static final int DISABLED_TREE_COUNT = -999;

    public TFBiomeFinalPlateau(int biomeId) {
        super(biomeId);

        this.topBlock = TFBlocks.deadrock;
        this.field_150604_aj = 0;
        this.fillerBlock = TFBlocks.deadrock;
        this.field_76754_C = 1;

        // this.rootHeight = 7F;
        // this.heightVariation = 0.4F;

        this.temperature = 0.3F;
        this.rainfall = 0.2F;

        TFBiomeDecorator decorator = getTFBiomeDecorator();
        decorator.canopyPerChunk = DISABLED_TREE_COUNT;
        decorator.setTreesPerChunk(DISABLED_TREE_COUNT);
        decorator.generateLakes = false;

        // custom creature list.
        spawnableCreatureList.clear();
        spawnableCreatureList.add(new SpawnListEntry(EntityTFRaven.class, 10, 4, 4));
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
