package twilightforest.biomes;

public class TFBiomeMushrooms extends TFBiomeBase {

    public TFBiomeMushrooms(int biomeId) {
        super(biomeId);

        this.rainfall = 0.8F;
        this.temperature = 0.8F;

        TFBiomeDecorator decorator = getTFBiomeDecorator();
        decorator.setTreesPerChunk(8);
        decorator.setMushroomsPerChunk(8);
        decorator.setBigMushroomsPerChunk(2);
        decorator.alternateCanopyChance = 0.2F;
    }
}
