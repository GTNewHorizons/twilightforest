package twilightforest.world.layer;

import net.minecraft.world.gen.layer.GenLayer;
import net.minecraft.world.gen.layer.GenLayerSmooth;
import net.minecraft.world.gen.layer.GenLayerVoronoiZoom;
import net.minecraft.world.gen.layer.GenLayerZoom;

public abstract class GenLayerTF extends GenLayer {

    private static final long BASE_BIOME_SEED = 1L;
    private static final long KEY_AND_COMPANION_BIOME_SEED = 1000L;
    private static final long BIOME_STABILIZATION_SEED = 700L;
    private static final long BIOME_BORDER_SEED = 500L;
    private static final long STREAM_SEED = 1L;
    private static final long STREAM_SMOOTHING_SEED = 7000L;
    private static final long RIVER_MIX_SEED = 100L;
    private static final long VORONOI_ZOOM_SEED = 10L;

    public GenLayerTF(long seed) {
        super(seed);
    }

    public static GenLayer[] makeTheWorld(long seed) {
        GenLayer biomes = new GenLayerTFBiomes1Point7(BASE_BIOME_SEED);
        biomes = new GenLayerTFKeyBiomes(KEY_AND_COMPANION_BIOME_SEED, biomes);
        biomes = new GenLayerTFCompanionBiomes(KEY_AND_COMPANION_BIOME_SEED, biomes);

        biomes = new GenLayerZoom(1000L, biomes);
        biomes = new GenLayerZoom(1001L, biomes);
        biomes = new GenLayerTFBiomeStabilize(BIOME_STABILIZATION_SEED, biomes);
        biomes = new GenLayerTFThornBorder(BIOME_BORDER_SEED, biomes);
        biomes = new GenLayerZoom(1002L, biomes);

        return finishWorldGeneration(seed, biomes);
    }

    public static GenLayer[] makeTheWorldOldMapGen(long seed) {
        GenLayer biomes = new GenLayerTFBiomes(BASE_BIOME_SEED);

        biomes = new GenLayerZoom(1000L, biomes);
        biomes = new GenLayerZoom(1001L, biomes);
        biomes = new GenLayerTFBiomeStabilize(BIOME_STABILIZATION_SEED, biomes);
        biomes = new GenLayerZoom(1002L, biomes);
        biomes = new GenLayerTFBiomeBorders(BIOME_BORDER_SEED, biomes);

        return finishWorldGeneration(seed, biomes);
    }

    private static GenLayer[] finishWorldGeneration(long seed, GenLayer biomes) {
        // keep the zoom seeds and layer order unchanged to preserve generated biome layouts
        biomes = new GenLayerZoom(1003L, biomes);
        biomes = new GenLayerZoom(1004L, biomes);
        biomes = new GenLayerZoom(1005L, biomes);

        GenLayer riverLayer = new GenLayerTFStream(STREAM_SEED, biomes);
        riverLayer = new GenLayerSmooth(STREAM_SMOOTHING_SEED, riverLayer);
        biomes = new GenLayerTFRiverMix(RIVER_MIX_SEED, biomes, riverLayer);

        // do "voronoi" zoom
        GenLayer voronoiZoom = new GenLayerVoronoiZoom(VORONOI_ZOOM_SEED, biomes);

        biomes.initWorldGenSeed(seed);
        voronoiZoom.initWorldGenSeed(seed);

        return new GenLayer[] { biomes, voronoiZoom };
    }
}
