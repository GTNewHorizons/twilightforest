package twilightforest.biomes;

public class TFBiomeStream extends TFBiomeBase {

    public TFBiomeStream(int biomeId) {
        super(biomeId);

        this.temperature = 0.5F;
        this.rainfall = 1.0F;

        getTFBiomeDecorator().setWaterlilyPerChunk(2);
        spawnableCreatureList.clear();
    }
}
