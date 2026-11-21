package twilightforest.world;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.BiomeCache;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.WorldChunkManager;
import net.minecraft.world.gen.layer.GenLayer;
import net.minecraft.world.gen.layer.IntCache;

import twilightforest.TFFeature;
import twilightforest.TwilightForestMod;
import twilightforest.biomes.TFBiomeBase;
import twilightforest.world.layer.GenLayerTF;

public class TFWorldChunkManager extends WorldChunkManager {

    private static final int CHUNK_SHIFT = 4;
    private static final int CHUNK_SIZE = 1 << CHUNK_SHIFT;
    private static final int BIOME_SAMPLE_SHIFT = 2;
    private static final int FEATURE_REGION_SIZE = 16;
    private static final int FEATURE_CENTER_MAX_OFFSET = 3;
    private static final float MAX_RAINFALL = 1.0F;
    private static final float RAINFALL_SCALE = 65536.0F;

    private GenLayer unzoomedBiomes;

    /** A GenLayer containing the indices into BiomeGenBase.biomeList[] */
    private GenLayer zoomedBiomes;

    /** The BiomeCache object for this world. */
    private final BiomeCache myBiomeCache;

    /** A list of biomes that the player can spawn in. */
    private final List<BiomeGenBase> myBiomesToSpawnIn;

    protected TFWorldChunkManager() {
        myBiomeCache = new BiomeCache(this);
        myBiomesToSpawnIn = new ArrayList<>();
        myBiomesToSpawnIn.add(TFBiomeBase.twilightForest);
        myBiomesToSpawnIn.add(TFBiomeBase.twilightForest2);
        myBiomesToSpawnIn.add(TFBiomeBase.clearing);
        myBiomesToSpawnIn.add(TFBiomeBase.tfSwamp);
        myBiomesToSpawnIn.add(TFBiomeBase.mushrooms);
    }

    public TFWorldChunkManager(long seed, WorldType worldType) {
        this();
        GenLayer[] layers;
        // new world gen!
        if (TwilightForestMod.oldMapGen) {
            layers = GenLayerTF.makeTheWorldOldMapGen(seed);
        } else {
            layers = GenLayerTF.makeTheWorld(seed);
        }
        unzoomedBiomes = layers[0];
        zoomedBiomes = layers[1];
    }

    public TFWorldChunkManager(World world) {
        this(world.getSeed(), world.getWorldInfo().getTerrainType());
    }

    /**
     * Gets the list of valid biomes for the player to spawn in.
     */
    @Override
    public List<BiomeGenBase> getBiomesToSpawnIn() {
        return myBiomesToSpawnIn;
    }

    /**
     * Returns the BiomeGenBase related to the x, z position on the world.
     */
    @Override
    public BiomeGenBase getBiomeGenAt(int x, int z) {
        BiomeGenBase biome = myBiomeCache.getBiomeGenAt(x, z);
        return biome != null ? biome : TFBiomeBase.twilightForest;
    }

    /**
     * Returns a list of rainfall values for the specified blocks. Args: listToReuse, x, z, width, length.
     */
    @Override
    public float[] getRainfall(float[] rainfall, int x, int z, int width, int length) {
        IntCache.resetIntCache();

        int area = width * length;
        if (rainfall == null || rainfall.length < area) {
            rainfall = new float[area];
        }

        int[] biomeIds = zoomedBiomes.getInts(x, z, width, length);
        for (int i = 0; i < area; i++) {
            BiomeGenBase biome = biomeIds[i] >= 0 ? BiomeGenBase.getBiome(biomeIds[i]) : null;
            // clear invalid samples even when the caller reuses a previously populated array
            rainfall[i] = biome != null ? Math.min(MAX_RAINFALL, biome.getIntRainfall() / RAINFALL_SCALE) : 0.0F;
        }

        return rainfall;
    }

    /**
     * Return an adjusted version of a given temperature based on the y height
     */
    @Override
    public float getTemperatureAtHeight(float temperature, int y) {
        return temperature;
    }

    private static BiomeGenBase resolveBiome(int biomeId) {
        BiomeGenBase biome = biomeId >= 0 ? BiomeGenBase.getBiome(biomeId) : null;
        return biome != null ? biome : TFBiomeBase.twilightForest;
    }

    /**
     * Returns an array of biomes for the location input.
     */
    @Override
    public BiomeGenBase[] getBiomesForGeneration(BiomeGenBase[] biomes, int x, int z, int length, int width) {
        IntCache.resetIntCache();

        int area = length * width;
        if (biomes == null || biomes.length < area) {
            biomes = new BiomeGenBase[area];
        }

        int[] biomeIds = unzoomedBiomes.getInts(x, z, length, width);
        for (int i = 0; i < area; i++) {
            // biome validity check
            biomes[i] = resolveBiome(biomeIds[i]);
        }

        return biomes;
    }

    /**
     * Returns biomes to use for the blocks and loads the other data like temperature and humidity onto the
     * WorldChunkManager Args: oldBiomeList, x, z, width, depth
     */
    @Override
    public BiomeGenBase[] loadBlockGeneratorData(BiomeGenBase[] biomes, int x, int z, int width, int depth) {
        return getBiomeGenAt(biomes, x, z, width, depth, true);
    }

    /**
     * Return a list of biomes for the specified blocks. Args: listToReuse, x, y, width, length, cacheFlag (if false,
     * don't check biomeCache to avoid infinite loop in BiomeCacheBlock)
     */
    @Override
    public BiomeGenBase[] getBiomeGenAt(BiomeGenBase[] biomes, int x, int z, int width, int length, boolean cacheFlag) {
        IntCache.resetIntCache();

        int area = width * length;
        if (biomes == null || biomes.length < area) {
            biomes = new BiomeGenBase[area];
        }

        if (cacheFlag && width == CHUNK_SIZE
                && length == CHUNK_SIZE
                && (x & (CHUNK_SIZE - 1)) == 0
                && (z & (CHUNK_SIZE - 1)) == 0) {
            BiomeGenBase[] cachedBiomes = myBiomeCache.getCachedBiomes(x, z);
            System.arraycopy(cachedBiomes, 0, biomes, 0, area);
            return biomes;
        }

        int[] biomeIds = zoomedBiomes.getInts(x, z, width, length);
        for (int i = 0; i < area; i++) {
            // biome validity check
            biomes[i] = resolveBiome(biomeIds[i]);
        }

        return biomes;
    }

    /**
     * checks given Chunk's Biomes against List of allowed ones
     */
    @Override
    @SuppressWarnings("rawtypes")
    public boolean areBiomesViable(int x, int z, int radius, List allowedBiomes) {
        IntCache.resetIntCache();
        int minX = (x - radius) >> BIOME_SAMPLE_SHIFT;
        int minZ = (z - radius) >> BIOME_SAMPLE_SHIFT;
        int maxX = (x + radius) >> BIOME_SAMPLE_SHIFT;
        int maxZ = (z + radius) >> BIOME_SAMPLE_SHIFT;
        int width = maxX - minX + 1;
        int length = maxZ - minZ + 1;
        int[] biomeIds = unzoomedBiomes.getInts(minX, minZ, width, length);

        for (int i = 0; i < width * length; i++) {
            if (!allowedBiomes.contains(BiomeGenBase.getBiome(biomeIds[i]))) {
                return false;
            }
        }

        return true;
    }

    /**
     * Finds a valid position within a range, that is once of the listed biomes.
     */
    @Override
    @SuppressWarnings("rawtypes")
    public ChunkPosition findBiomePosition(int x, int z, int radius, List allowedBiomes, Random random) {
        IntCache.resetIntCache();
        int minX = (x - radius) >> BIOME_SAMPLE_SHIFT;
        int minZ = (z - radius) >> BIOME_SAMPLE_SHIFT;
        int maxX = (x + radius) >> BIOME_SAMPLE_SHIFT;
        int maxZ = (z + radius) >> BIOME_SAMPLE_SHIFT;
        int width = maxX - minX + 1;
        int length = maxZ - minZ + 1;
        int[] biomeIds = unzoomedBiomes.getInts(minX, minZ, width, length);
        ChunkPosition chosen = null;
        int matches = 0;

        // may return a larger backing array, only the requested area is valid
        for (int i = 0; i < width * length; i++) {
            if (!allowedBiomes.contains(BiomeGenBase.getBiome(biomeIds[i]))) {
                continue;
            }

            matches++;
            // sampling gives every matching location the same probability
            if (matches == 1 || random.nextInt(matches) == 0) {
                int sampleX = (minX + i % width) << BIOME_SAMPLE_SHIFT;
                int sampleZ = (minZ + i / width) << BIOME_SAMPLE_SHIFT;
                chosen = new ChunkPosition(sampleX, 0, sampleZ);
            }
        }

        return chosen;
    }

    /**
     * Calls the WorldChunkManager's biomeCache.cleanupCache()
     */
    @Override
    public void cleanupCache() {
        myBiomeCache.cleanupCache();
    }

    /**
     * Returns feature ID at the specified location
     */
    public int getFeatureID(int mapX, int mapZ, World world) {
        return getFeatureAt(mapX, mapZ, world).featureID;
    }

    /**
     * Returns feature at the specified location
     */
    public TFFeature getFeatureAt(int mapX, int mapZ, World world) {
        return TFFeature.generateFeatureFor1Point7(mapX >> CHUNK_SHIFT, mapZ >> CHUNK_SHIFT, world);
    }

    /**
     * Checks if the coordinates are in a feature chunk.
     *
     * @param world
     */
    public boolean isInFeatureChunk(World world, int mapX, int mapZ) {
        // legacy support
        if (TwilightForestMod.oldMapGen) {
            return isInFeatureChunkOld(world, mapX, mapZ);
        }

        int chunkX = mapX >> CHUNK_SHIFT;
        int chunkZ = mapZ >> CHUNK_SHIFT;

        // all feature centres lie within three chunks of a chunk boundary
        // reject impossible candidates without calculating a centre or allocating ChunkCoordinates
        if (!couldBeFeatureCenter(chunkX) || !couldBeFeatureCenter(chunkZ)) {
            return false;
        }

        ChunkCoordinates center = TFFeature.getNearestCenterXYZ(chunkX, chunkZ, world);
        return chunkX == (center.posX >> CHUNK_SHIFT) && chunkZ == (center.posZ >> CHUNK_SHIFT);
    }

    private static boolean couldBeFeatureCenter(int chunkCoordinate) {
        int offset = chunkCoordinate & (FEATURE_REGION_SIZE - 1);
        return offset <= FEATURE_CENTER_MAX_OFFSET || offset >= FEATURE_REGION_SIZE - FEATURE_CENTER_MAX_OFFSET;
    }

    /**
     * Checks if the coordinates are in a feature chunk.
     *
     * @param world
     */
    public boolean isInFeatureChunkOld(World world, int mapX, int mapZ) {
        int chunkX = mapX >> CHUNK_SHIFT;
        int chunkZ = mapZ >> CHUNK_SHIFT;
        return chunkX % FEATURE_REGION_SIZE == 0 && chunkZ % FEATURE_REGION_SIZE == 0;
    }
}
