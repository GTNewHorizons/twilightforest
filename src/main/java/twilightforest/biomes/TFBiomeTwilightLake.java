package twilightforest.biomes;

import net.minecraft.entity.passive.EntitySquid;

public class TFBiomeTwilightLake extends TFBiomeBase {

    public TFBiomeTwilightLake(int biomeId) {
        super(biomeId);

        // this.rootHeight = -1.9F;
        // this.heightVariation = 0.5F;

        this.temperature = 0.66F;
        this.rainfall = 1.0F;

        // spawnableCreatureList.clear();
        this.spawnableWaterCreatureList.add(new SpawnListEntry(EntitySquid.class, 10, 4, 4));
    }
}
