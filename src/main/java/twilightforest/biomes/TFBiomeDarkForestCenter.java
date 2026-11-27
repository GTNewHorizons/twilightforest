package twilightforest.biomes;

public class TFBiomeDarkForestCenter extends TFBiomeDarkForest {

    private static final double COLOR_NOISE_SCALE = 0.0225D;

    public TFBiomeDarkForestCenter(int biomeId) {
        super(biomeId);
    }

    /**
     * Provides the basic grass color based on the biome temperature and rainfall
     */
    @Override
    public int getBiomeGrassColor(int x, int y, int z) {
        return getColorNoise(x, z) < -0.2D ? 0x667540 : 0x554114;
    }

    /**
     * Provides the basic foliage color based on the biome temperature and rainfall
     */
    @Override
    public int getBiomeFoliageColor(int x, int y, int z) {
        return getColorNoise(x, z) < -0.1D ? 0xf9821e : 0xe94e14;
    }

    private double getColorNoise(int x, int z) {
        return plantNoise.func_151601_a((double) x * COLOR_NOISE_SCALE, (double) z * COLOR_NOISE_SCALE);
    }
}
