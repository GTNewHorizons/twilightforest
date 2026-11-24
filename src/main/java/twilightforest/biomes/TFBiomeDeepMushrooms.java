package twilightforest.biomes;

public class TFBiomeDeepMushrooms extends TFBiomeBase {

    public TFBiomeDeepMushrooms(int biomeId) {
        super(biomeId);

        this.temperature = 0.8F;
        this.rainfall = 1.0F;
        this.rootHeight = 0.15F;
        this.heightVariation = 0.4F;

        TFBiomeDecorator decorator = getTFBiomeDecorator();
        decorator.setTreesPerChunk(1);
        decorator.setMushroomsPerChunk(12);
        decorator.setBigMushroomsPerChunk(8);
        decorator.myceliumPerChunk = 3;
        decorator.alternateCanopyChance = 0.9F;
    }
}
