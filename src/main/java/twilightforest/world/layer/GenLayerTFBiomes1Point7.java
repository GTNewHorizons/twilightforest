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
    protected BiomeGenBase[] commonBiomes = (new BiomeGenBase[] { TFBiomeBase.twilightForest,
            TFBiomeBase.twilightForest2, TFBiomeBase.mushrooms, TFBiomeBase.oakSavanna, TFBiomeBase.fireflyForest });
    protected BiomeGenBase[] rareBiomes = (new BiomeGenBase[] { TFBiomeBase.tfLake, TFBiomeBase.deepMushrooms,
            TFBiomeBase.enchantedForest, TFBiomeBase.clearing });

    public GenLayerTFBiomes1Point7(long l, GenLayer genlayer) {
        super(l);
        parent = genlayer;
    }

    public GenLayerTFBiomes1Point7(long l) {
        super(l);
    }

    @Override
    public int[] getInts(int x, int z, int width, int depth) {
        int[] dest = IntCache.getIntCache(width * depth);
        for (int dz = 0; dz < depth; dz++) {
            for (int dx = 0; dx < width; dx++) {
                initChunkSeed(dx + x, dz + z);
                if (nextInt(RARE_BIOME_CHANCE) == 0) {
                    // make rare biome
                    dest[dx + dz * width] = rareBiomes[nextInt(rareBiomes.length)].biomeID;
                } else {
                    // make common biome
                    dest[dx + dz * width] = commonBiomes[nextInt(commonBiomes.length)].biomeID;
                }
            }

        }

        // for (int i = 0; i < width * depth; i++)
        // {
        // if (dest[i] < 0 || dest[i] > TFBiomeBase.fireSwamp.biomeID)
        // {
        // System.err.printf("Made a bad ID, %d at %d, %d while generating\n", dest[i], x, z);
        // }
        // }

        return dest;
    }
}
