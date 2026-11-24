package twilightforest.world.layer;

import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.gen.layer.GenLayer;

import twilightforest.biomes.TFBiomeBase;

/**
 * Applies the twilight forest biomes to the map
 *
 * @author Ben
 *
 */
public class GenLayerTFBiomes1Point7 extends GenLayerTFBiomeSelection {

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
        return selectBiomes(x, z, width, depth, commonBiomes, rareBiomes);
    }
}
