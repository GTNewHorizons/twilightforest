package twilightforest.world.layer;

import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.gen.layer.GenLayer;
import net.minecraft.world.gen.layer.IntCache;

import twilightforest.biomes.TFBiomeBase;

/**
 * Applies the twilight forest biomes to the map
 *
 * @author Ben
 *
 */
public class GenLayerTFBiomes1Point7 extends GenLayer {

    private static final int RARE_BIOME_CHANCE = 15;

    // keep these arrays per instance and mutable for compatibility with biome customisation
    protected BiomeGenBase[] commonBiomes = { TFBiomeBase.twilightForest, TFBiomeBase.twilightForest2,
            TFBiomeBase.mushrooms, TFBiomeBase.oakSavanna, TFBiomeBase.fireflyForest };
    protected BiomeGenBase[] rareBiomes = { TFBiomeBase.tfLake, TFBiomeBase.deepMushrooms, TFBiomeBase.enchantedForest,
            TFBiomeBase.clearing };

    public GenLayerTFBiomes1Point7(long seed, GenLayer parent) {
        super(seed);
        this.parent = parent;
    }

    public GenLayerTFBiomes1Point7(long seed) {
        super(seed);
    }

    @Override
    public int[] getInts(int x, int z, int width, int depth) {
        int[] biomes = IntCache.getIntCache(width * depth);

        for (int dz = 0; dz < depth; dz++) {
            int rowOffset = dz * width;
            for (int dx = 0; dx < width; dx++) {
                initChunkSeed(x + dx, z + dz);
                BiomeGenBase[] candidates = nextInt(RARE_BIOME_CHANCE) == 0 ? rareBiomes : commonBiomes;
                biomes[rowOffset + dx] = candidates[nextInt(candidates.length)].biomeID;
            }
        }

        return biomes;
    }
}
