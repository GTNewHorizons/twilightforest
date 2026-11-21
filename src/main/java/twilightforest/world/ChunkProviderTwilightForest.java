// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) braces deadcode

package twilightforest.world;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFalling;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Blocks;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.util.MathHelper;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.SpawnerAnimals;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.BiomeGenBase.SpawnListEntry;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.NoiseGeneratorOctaves;
import net.minecraft.world.gen.NoiseGeneratorPerlin;
import net.minecraft.world.gen.feature.WorldGenDungeons;
import net.minecraft.world.gen.feature.WorldGenLakes;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.terraingen.ChunkProviderEvent;

import com.falsepattern.endlessids.mixin.helpers.ChunkBiomeHook;

import cpw.mods.fml.common.eventhandler.Event.Result;
import twilightforest.TFFeature;
import twilightforest.biomes.TFBiomeBase;
import twilightforest.block.TFBlocks;
import twilightforest.compat.Mods;

public class ChunkProviderTwilightForest implements IChunkProvider {

    private static final int CHUNK_SIZE = 16;
    private static final int CHUNK_CENTER_OFFSET = CHUNK_SIZE / 2;
    private static final int NOISE_CELL_SIZE = 4;
    private static final int DENSITY_CELL_HEIGHT = 8;
    private static final int BIOME_BLEND_RADIUS = 2;
    private static final int BIOME_WEIGHT_DIAMETER = BIOME_BLEND_RADIUS * 2 + 1;
    private static final int WATER_LAKE_CHANCE = 4;
    private static final int LAVA_LAKE_CHANCE = 32;
    private static final int DUNGEON_ATTEMPTS = 8;
    private static final int BIOME_SAMPLES = 10;
    private static final int NOISE_SAMPLES = 5;
    private static final int NOISE_HEIGHT = 33;
    private static final int TERRAIN_SEA_LEVEL = 63;
    private static final int SURFACE_LIMIT = 128;
    private static final int GLACIER_HEIGHT = 32;
    private static final Block[] NO_STRUCTURE_BLOCKS = new Block[0];

    private final World worldObj;
    private final WorldType worldType;
    private final FastRandom rand;
    private final TFGenCaves caveGenerator;
    private final TFGenRavine ravineGenerator;
    private final MapGenTFMajorFeature majorFeatureGenerator;
    private final MapGenTFHollowTree hollowTreeGenerator;

    // use this exact order
    private final NoiseGeneratorOctaves canopyNoiseGenerator;
    public NoiseGeneratorOctaves noiseGen5;
    public NoiseGeneratorOctaves noiseGen6;
    public NoiseGeneratorOctaves mobSpawnerNoise;
    private final NoiseGeneratorOctaves minLimitNoiseGenerator;
    private final NoiseGeneratorOctaves maxLimitNoiseGenerator;
    private final NoiseGeneratorOctaves interpolationNoiseGenerator;
    private final NoiseGeneratorPerlin surfaceNoiseGenerator;

    private final double[] terrainDensity = new double[NOISE_SAMPLES * NOISE_SAMPLES * NOISE_HEIGHT];
    private final float[] biomeWeight = new float[BIOME_WEIGHT_DIAMETER * BIOME_WEIGHT_DIAMETER];
    private double[] stoneNoise = new double[CHUNK_SIZE * CHUNK_SIZE];
    private double[] depthNoise;
    private double[] interpolationNoise;
    private double[] minLimitNoise;
    private double[] maxLimitNoise;
    private BiomeGenBase[] biomesForGeneration;

    // dont change constructor parameters
    public ChunkProviderTwilightForest(World world, long seed, boolean mapFeaturesEnabled) {
        this.worldObj = world;
        this.worldType = world.getWorldInfo().getTerrainType();
        this.rand = new FastRandom(seed);
        this.caveGenerator = new TFGenCaves();
        this.ravineGenerator = new TFGenRavine();
        this.majorFeatureGenerator = new MapGenTFMajorFeature();
        this.hollowTreeGenerator = new MapGenTFHollowTree();

        this.canopyNoiseGenerator = new NoiseGeneratorOctaves(rand, 4);
        this.noiseGen5 = new NoiseGeneratorOctaves(rand, 10);
        this.noiseGen6 = new NoiseGeneratorOctaves(rand, 16);
        this.mobSpawnerNoise = new NoiseGeneratorOctaves(rand, 8);
        this.minLimitNoiseGenerator = new NoiseGeneratorOctaves(rand, 16);
        this.maxLimitNoiseGenerator = new NoiseGeneratorOctaves(rand, 16);
        this.interpolationNoiseGenerator = new NoiseGeneratorOctaves(rand, 8);
        this.surfaceNoiseGenerator = new NoiseGeneratorPerlin(rand, 4);

        for (int z = -BIOME_BLEND_RADIUS; z <= BIOME_BLEND_RADIUS; z++) {
            for (int x = -BIOME_BLEND_RADIUS; x <= BIOME_BLEND_RADIUS; x++) {
                biomeWeight[x + BIOME_BLEND_RADIUS + (z + BIOME_BLEND_RADIUS) * BIOME_WEIGHT_DIAMETER] = 10.0F
                        / MathHelper.sqrt_float(x * x + z * z + 0.2F);
            }
        }
    }

    private static int columnIndex(int x, int z) {
        return (x * CHUNK_SIZE + z) * TFWorld.CHUNKHEIGHT;
    }

    @Override
    public Chunk provideChunk(int chunkX, int chunkZ) {
        rand.setSeed(chunkX * 0x4f9939f508L + chunkZ * 0x1ef1565bd5L);

        Block[] blocks = new Block[CHUNK_SIZE * CHUNK_SIZE * TFWorld.CHUNKHEIGHT];
        byte[] metadata = new byte[blocks.length];

        generateTerrain2(chunkX, chunkZ, blocks);
        squishTerrain(blocks);
        addDarkForestCanopy2(chunkX, chunkZ, blocks, metadata);

        biomesForGeneration = worldObj.getWorldChunkManager().loadBlockGeneratorData(
                biomesForGeneration,
                chunkX * CHUNK_SIZE,
                chunkZ * CHUNK_SIZE,
                CHUNK_SIZE,
                CHUNK_SIZE);
        addGlaciers(chunkX, chunkZ, blocks, metadata, biomesForGeneration);
        deformTerrainForFeature(chunkX, chunkZ, blocks, metadata);
        replaceBlocksForBiome(chunkX, chunkZ, blocks, metadata, biomesForGeneration);

        caveGenerator.func_151539_a(this, worldObj, chunkX, chunkZ, blocks);
        ravineGenerator.func_151539_a(this, worldObj, chunkX, chunkZ, blocks);
        majorFeatureGenerator.func_151539_a(this, worldObj, chunkX, chunkZ, NO_STRUCTURE_BLOCKS);
        hollowTreeGenerator.func_151539_a(this, worldObj, chunkX, chunkZ, NO_STRUCTURE_BLOCKS);

        Chunk chunk = new Chunk(worldObj, blocks, metadata, chunkX, chunkZ);
        writeChunkBiomes(chunk);
        chunk.generateSkylightMap();
        return chunk;
    }

    private void writeChunkBiomes(Chunk chunk) {
        if (Mods.endlessids.isLoaded()) {
            short[] biomeIDs = ((ChunkBiomeHook) chunk).getBiomeShortArray();
            for (int i = 0; i < biomeIDs.length; i++) {
                biomeIDs[i] = (short) biomesForGeneration[i].biomeID;
            }
        } else {
            byte[] biomeIDs = chunk.getBiomeArray();
            for (int i = 0; i < biomeIDs.length; i++) {
                biomeIDs[i] = (byte) biomesForGeneration[i].biomeID;
            }
        }
    }

    public void generateTerrain2(int chunkX, int chunkZ, Block[] blocks) {
        biomesForGeneration = worldObj.getWorldChunkManager().getBiomesForGeneration(
                biomesForGeneration,
                chunkX * NOISE_CELL_SIZE - BIOME_BLEND_RADIUS,
                chunkZ * NOISE_CELL_SIZE - BIOME_BLEND_RADIUS,
                BIOME_SAMPLES,
                BIOME_SAMPLES);
        calculateTerrainDensity(chunkX * NOISE_CELL_SIZE, chunkZ * NOISE_CELL_SIZE);

        for (int cellX = 0; cellX < NOISE_CELL_SIZE; cellX++) {
            for (int cellZ = 0; cellZ < NOISE_CELL_SIZE; cellZ++) {
                int corner00 = (cellX * NOISE_SAMPLES + cellZ) * NOISE_HEIGHT;
                int corner01 = corner00 + NOISE_HEIGHT;
                int corner10 = ((cellX + 1) * NOISE_SAMPLES + cellZ) * NOISE_HEIGHT;
                int corner11 = corner10 + NOISE_HEIGHT;

                for (int sampleY = 0; sampleY < NOISE_HEIGHT - 1; sampleY++) {
                    double density00 = terrainDensity[corner00 + sampleY];
                    double density01 = terrainDensity[corner01 + sampleY];
                    double density10 = terrainDensity[corner10 + sampleY];
                    double density11 = terrainDensity[corner11 + sampleY];
                    double step00 = (terrainDensity[corner00 + sampleY + 1] - density00) / DENSITY_CELL_HEIGHT;
                    double step01 = (terrainDensity[corner01 + sampleY + 1] - density01) / DENSITY_CELL_HEIGHT;
                    double step10 = (terrainDensity[corner10 + sampleY + 1] - density10) / DENSITY_CELL_HEIGHT;
                    double step11 = (terrainDensity[corner11 + sampleY + 1] - density11) / DENSITY_CELL_HEIGHT;

                    for (int subY = 0; subY < DENSITY_CELL_HEIGHT; subY++) {
                        double left = density00;
                        double right = density01;
                        double leftStep = (density10 - density00) / NOISE_CELL_SIZE;
                        double rightStep = (density11 - density01) / NOISE_CELL_SIZE;
                        int y = sampleY * DENSITY_CELL_HEIGHT + subY;

                        for (int subX = 0; subX < NOISE_CELL_SIZE; subX++) {
                            int x = cellX * NOISE_CELL_SIZE + subX;
                            int base = columnIndex(x, cellZ * NOISE_CELL_SIZE) + y;
                            double zStep = (right - left) / NOISE_CELL_SIZE;
                            double density = left - zStep;

                            for (int subZ = 0; subZ < NOISE_CELL_SIZE; subZ++) {
                                density += zStep;
                                blocks[base + subZ * TFWorld.CHUNKHEIGHT] = density > 0.0D ? Blocks.stone
                                        : y < TERRAIN_SEA_LEVEL ? Blocks.water : null;
                            }
                            left += leftStep;
                            right += rightStep;
                        }
                        density00 += step00;
                        density01 += step01;
                        density10 += step10;
                        density11 += step11;
                    }
                }
            }
        }
    }

    private void calculateTerrainDensity(int x, int z) {
        depthNoise = noiseGen6.generateNoiseOctaves(depthNoise, x, z, 5, 5, 200.0D, 200.0D, 0.5D);
        interpolationNoise = interpolationNoiseGenerator.generateNoiseOctaves(
                interpolationNoise,
                x,
                0,
                z,
                5,
                33,
                5,
                8.555150000000001D,
                4.277575000000001D,
                8.555150000000001D);
        minLimitNoise = minLimitNoiseGenerator
                .generateNoiseOctaves(minLimitNoise, x, 0, z, 5, 33, 5, 684.412D, 684.412D, 684.412D);
        maxLimitNoise = maxLimitNoiseGenerator
                .generateNoiseOctaves(maxLimitNoise, x, 0, z, 5, 33, 5, 684.412D, 684.412D, 684.412D);

        int terrainIndex = 0;
        int depthIndex = 0;
        for (int sampleX = 0; sampleX < NOISE_SAMPLES; sampleX++) {
            for (int sampleZ = 0; sampleZ < NOISE_SAMPLES; sampleZ++) {
                float totalVariation = 0.0F;
                float totalHeight = 0.0F;
                float totalWeight = 0.0F;
                BiomeGenBase center = biomesForGeneration[sampleX + BIOME_BLEND_RADIUS
                        + (sampleZ + BIOME_BLEND_RADIUS) * BIOME_SAMPLES];

                for (int offsetX = -BIOME_BLEND_RADIUS; offsetX <= BIOME_BLEND_RADIUS; offsetX++) {
                    for (int offsetZ = -BIOME_BLEND_RADIUS; offsetZ <= BIOME_BLEND_RADIUS; offsetZ++) {
                        BiomeGenBase biome = biomesForGeneration[sampleX + offsetX
                                + BIOME_BLEND_RADIUS
                                + (sampleZ + offsetZ + BIOME_BLEND_RADIUS) * BIOME_SAMPLES];
                        float rootHeight = biome.rootHeight;
                        float variation = biome.heightVariation;

                        if (worldType == WorldType.AMPLIFIED && rootHeight > 0.0F) {
                            rootHeight = 1.0F + rootHeight * 2.0F;
                            variation = 1.0F + variation * 4.0F;
                        }

                        float weight = biomeWeight[offsetX + BIOME_BLEND_RADIUS
                                + (offsetZ + BIOME_BLEND_RADIUS) * BIOME_WEIGHT_DIAMETER] / (rootHeight + 2.0F);
                        if (biome.rootHeight > center.rootHeight) {
                            weight /= 2.0F;
                        }

                        totalVariation += variation * weight;
                        totalHeight += rootHeight * weight;
                        totalWeight += weight;
                    }
                }

                totalVariation /= totalWeight;
                totalHeight /= totalWeight;
                totalVariation = totalVariation * 0.9F + 0.1F;
                totalHeight = (totalHeight * 4.0F - 1.0F) / 8.0F;

                double depth = depthNoise[depthIndex] / 8000.0D;
                if (depth < 0.0D) {
                    depth = -depth * 0.3D;
                }
                depth = depth * 3.0D - 2.0D;
                if (depth < 0.0D) {
                    depth /= 2.0D;
                    if (depth < -1.0D) {
                        depth = -1.0D;
                    }
                    depth /= 1.4D;
                    depth /= 2.0D;
                } else {
                    if (depth > 1.0D) {
                        depth = 1.0D;
                    }
                    depth /= 8.0D;
                }
                depthIndex++;

                double height = totalHeight;
                double variation = totalVariation;
                height += depth * 0.2D;
                height = height * 8.5D / 8.0D;
                double centerHeight = 8.5D + height * 4.0D;

                for (int sampleY = 0; sampleY < NOISE_HEIGHT; sampleY++) {
                    double verticalOffset = ((double) sampleY - centerHeight) * 12.0D * 128.0D / 256.0D / variation;
                    if (verticalOffset < 0.0D) {
                        verticalOffset *= 4.0D;
                    }

                    double lower = minLimitNoise[terrainIndex] / 512.0D;
                    double upper = maxLimitNoise[terrainIndex] / 512.0D;
                    double fraction = (interpolationNoise[terrainIndex] / 10.0D + 1.0D) / 2.0D;
                    double density = MathHelper.denormalizeClamp(lower, upper, fraction) - verticalOffset;

                    if (sampleY > 29) {
                        double fade = (float) (sampleY - 29) / 3.0F;
                        density = density * (1.0D - fade) + -10.0D * fade;
                    }
                    terrainDensity[terrainIndex++] = density;
                }
            }
        }
    }

    // half the vertical scale without allocating a second block array
    private void squishTerrain(Block[] blocks) {
        int halfHeight = TFWorld.MAXHEIGHT / 2;
        for (int x = 0; x < CHUNK_SIZE; x++) {
            for (int z = 0; z < CHUNK_SIZE; z++) {
                int base = columnIndex(x, z);
                for (int y = 0; y < TFWorld.CHUNKHEIGHT; y++) {
                    blocks[base + y] = y < halfHeight ? blocks[base + y * 2 + 1] : Blocks.air;
                }
            }
        }
    }

    public void replaceBlocksForBiome(int chunkX, int chunkZ, Block[] blocks, byte[] metadata, BiomeGenBase[] biomes) {
        ChunkProviderEvent.ReplaceBiomeBlocks event = new ChunkProviderEvent.ReplaceBiomeBlocks(
                this,
                chunkX,
                chunkZ,
                blocks,
                biomes);
        MinecraftForge.EVENT_BUS.post(event);
        if (event.getResult() == Result.DENY) {
            return;
        }

        double scale = 0.03125D;
        stoneNoise = surfaceNoiseGenerator.func_151599_a(
                stoneNoise,
                chunkX * CHUNK_SIZE,
                chunkZ * CHUNK_SIZE,
                CHUNK_SIZE,
                CHUNK_SIZE,
                scale * 2.0D,
                scale * 2.0D,
                1.0D);

        // the X Z arguments are intentionally swapped to match the biome terrain array layout
        for (int z = 0; z < CHUNK_SIZE; z++) {
            for (int x = 0; x < CHUNK_SIZE; x++) {
                biomes[x + z * CHUNK_SIZE].genTerrainBlocks(
                        worldObj,
                        rand,
                        blocks,
                        metadata,
                        chunkX * CHUNK_SIZE + z,
                        chunkZ * CHUNK_SIZE + x,
                        stoneNoise[x + z * CHUNK_SIZE]);
            }
        }
    }

    @Override
    public Chunk loadChunk(int chunkX, int chunkZ) {
        return provideChunk(chunkX, chunkZ);
    }

    public void deformTerrainForFeature(int chunkX, int chunkZ, Block[] blocks, byte[] metadata) {
        TFFeature feature = TFFeature.getNearestFeature(chunkX, chunkZ, worldObj);
        if (!feature.isTerrainAltered) {
            return;
        }

        int[] center = TFFeature.getNearestCenter(chunkX, chunkZ, worldObj);
        int centerX = center[0];
        int centerZ = center[1];

        if (feature == TFFeature.trollCave) {
            generateTrollClouds(blocks, chunkX, chunkZ, centerX, centerZ);
            return;
        }

        boolean hollowHill = feature == TFFeature.hill1 || feature == TFFeature.hill2
                || feature == TFFeature.hill3
                || feature == TFFeature.hydraLair;
        boolean flatFeature = feature == TFFeature.hedgeMaze || feature == TFFeature.nagaCourtyard
                || feature == TFFeature.questGrove;
        boolean yetiCave = feature == TFFeature.yetiCave;
        if (!hollowHill && !flatFeature && !yetiCave) {
            return;
        }

        int hillDiameter = (feature.size * 2 + 1) * CHUNK_SIZE;
        for (int x = 0; x < CHUNK_SIZE; x++) {
            for (int z = 0; z < CHUNK_SIZE; z++) {
                int dx = x - centerX;
                int dz = z - centerZ;

                if (hollowHill) {
                    int distance = (int) Math.sqrt(dx * dx + dz * dz);
                    int hillHeight = (int) (Math.cos((float) distance / (float) hillDiameter * Math.PI)
                            * ((float) hillDiameter / 3F));
                    raiseHill(blocks, feature, hillDiameter, x, z, dx, dz, hillHeight);
                } else if (flatFeature) {
                    flattenFeatureTerrain(blocks, feature, x, z, dx, dz);
                } else {
                    carveYetiCave(blocks, feature, x, z, dx, dz);
                }
            }
        }
    }

    private void raiseHill(Block[] blocks, TFFeature feature, int diameter, int x, int z, int dx, int dz,
            int hillHeight) {
        int base = columnIndex(x, z);
        int newGround = -1;
        boolean foundGround = false;

        for (int y = 0; y < TFWorld.CHUNKHEIGHT; y++) {
            if (blocks[base + y] != Blocks.stone && !foundGround) {
                newGround = y + hillHeight;
                foundGround = true;
            }
            if (foundGround && y <= newGround) {
                blocks[base + y] = Blocks.stone;
            }
        }

        int hollowHeight = hillHeight - 4 - feature.size;
        if (feature == TFFeature.hydraLair) {
            int offsetX = dx + 16;
            int offsetZ = dz + 16;
            int distance = (int) Math.sqrt(offsetX * offsetX + offsetZ * offsetZ);
            int hydraHeight = (int) (Math.cos(distance / (diameter / 1.5) * Math.PI) * (diameter / 1.5));
            hollowHeight = Math.max(hydraHeight - 4, hollowHeight);
        }
        hollowHeight = Math.max(0, hollowHeight);
        int hollowFloor = feature == TFFeature.hydraLair ? TFWorld.SEALEVEL : TFWorld.SEALEVEL - 3 - hollowHeight / 8;

        for (int y = 0; y < TFWorld.CHUNKHEIGHT; y++) {
            int index = base + y;
            if (hillHeight > 0 && y < TFWorld.SEALEVEL && blocks[index] != Blocks.stone) {
                blocks[index] = Blocks.stone;
            }
            if (y > hollowFloor && y < hollowFloor + hollowHeight) {
                blocks[index] = Blocks.air;
            }
        }
    }

    private static float edgeBlend(int dx, int dz, int boundary) {
        return Math.max(0, Math.max(Math.abs(dx), Math.abs(dz)) - boundary) / 8.0F;
    }

    private static int firstNonStone(Block[] blocks, int base) {
        for (int y = 0; y < SURFACE_LIMIT; y++) {
            if (blocks[base + y] != Blocks.stone) {
                return y;
            }
        }
        return -1;
    }

    private void flattenFeatureTerrain(Block[] blocks, TFFeature feature, int x, int z, int dx, int dz) {
        int base = columnIndex(x, z);
        int groundHeight = TFWorld.SEALEVEL + 1;
        int boundary = (feature.size * 2 + 1) * 8 - 8;
        float blend = edgeBlend(dx, dz, boundary);

        if (blend > 0.0F) {
            int originalHeight = firstNonStone(blocks, base);
            if (originalHeight >= 0) {
                groundHeight += (originalHeight - groundHeight) * blend;
            }
        }

        for (int y = 0; y < SURFACE_LIMIT; y++) {
            int index = base + y;
            if (y < groundHeight && (blocks[index] == Blocks.air || blocks[index] == Blocks.water)) {
                blocks[index] = Blocks.stone;
            }
            if (y >= groundHeight && blocks[index] != Blocks.water) {
                blocks[index] = Blocks.air;
            }
        }
    }

    private void carveYetiCave(Block[] blocks, TFFeature feature, int x, int z, int dx, int dz) {
        int base = columnIndex(x, z);
        int outerBoundary = (feature.size * 2 + 1) * 8 - 8;
        int caveBoundary = feature.size * 16 - 8;
        float blend = edgeBlend(dx, dz, outerBoundary);
        int offset = Math.min(Math.abs(dx), Math.abs(dz));

        int topHeight = TFWorld.SEALEVEL + 24;
        int ceilingHeight = TFWorld.SEALEVEL + 40 - offset * 4;
        if (dx >= -caveBoundary && dz >= -caveBoundary && dx <= caveBoundary && dz <= caveBoundary) {
            ceilingHeight = TFWorld.SEALEVEL + 16;
        }
        ceilingHeight -= offset / 6;
        ceilingHeight = Math.min(ceilingHeight, TFWorld.SEALEVEL + 16);

        int floorHeight = TFWorld.SEALEVEL - 1 + offset / 6;
        if (blend > 0.0F) {
            int originalHeight = firstNonStone(blocks, base);
            if (originalHeight >= 0) {
                topHeight += (originalHeight - topHeight) * blend;
                floorHeight += (originalHeight - floorHeight) * blend;
            }
        }

        for (int y = 0; y < SURFACE_LIMIT; y++) {
            int index = base + y;
            if (y < topHeight
                    && (blocks[index] == null || blocks[index] == Blocks.air || blocks[index] == Blocks.water)) {
                blocks[index] = Blocks.stone;
            }
            if (y > floorHeight && y < ceilingHeight) {
                blocks[index] = Blocks.air;
            }
            if (y == floorHeight && y < ceilingHeight && y < TFWorld.SEALEVEL + 3) {
                blocks[index] = Blocks.packed_ice;
            }
        }
    }

    private void generateTrollClouds(Block[] blocks, int chunkX, int chunkZ, int centerX, int centerZ) {
        FastRandom cloudRandom = new FastRandom(
                worldObj.getSeed() ^ (chunkX * 0x4f9939f508L) ^ (chunkZ * 0x1ef1565bd5L));

        int regionX = (chunkX + 8) >> 4;
        int regionZ = (chunkZ + 8) >> 4;
        long seed = (regionX * 3129871L) ^ (long) regionZ * 116129781L;
        seed = seed * seed * 42317861L + seed * 7L;

        int offsetX2 = (int) (seed >> 12 & 3L) * 5 - (int) (seed >> 15 & 3L) * 4;
        int offsetZ2 = (int) (seed >> 18 & 3L) * 4 - (int) (seed >> 21 & 3L) * 5;
        int offsetX3 = (int) (seed >> 9 & 3L) * 5 - (int) (seed >> 6 & 3L) * 4;
        int offsetZ3 = (int) (seed >> 3 & 3L) * 4 - (int) (seed & 3L) * 5;

        for (int cellX = 0; cellX < 4; cellX++) {
            for (int cellZ = 0; cellZ < 4; cellZ++) {
                int dx = cellX * 4 - centerX - 2;
                int dz = cellZ * 4 - centerZ - 2;
                double distance0 = Math.sqrt(dx * dx + dz * dz) / 4.0;
                double distance2 = Math.sqrt((dx + offsetX2) * (dx + offsetX2) + (dz + offsetZ2) * (dz + offsetZ2))
                        / 3.5;
                double distance3 = Math.sqrt((dx + offsetX3) * (dx + offsetX3) + (dz + offsetZ3) * (dz + offsetZ3))
                        / 4.5;
                double distance = Math.min(distance0, Math.min(distance2, distance3));

                float noise = cloudRandom.nextFloat();
                double edge = (distance - 7F) - noise * 3.0F;
                int top = 166 + (noise < 0.1F ? 1 : 0);
                int depth = 4 + (noise > 0.6F ? 1 : 0) + (noise > 0.9F ? 1 : 0);

                boolean fullCloud = distance < 7 || edge < 0.05F;
                boolean wispyCloud = distance < 8 || edge < 1F;
                if (!fullCloud && !wispyCloud) {
                    continue;
                }

                for (int subX = 0; subX < 4; subX++) {
                    for (int subZ = 0; subZ < 4; subZ++) {
                        int index = columnIndex(cellX * 4 + subX, cellZ * 4 + subZ) + top;
                        if (fullCloud) {
                            blocks[index] = TFBlocks.wispyCloud;
                            for (int d = 1; d < depth; d++) {
                                blocks[index - d] = TFBlocks.fluffyCloud;
                            }
                            blocks[index - depth] = TFBlocks.wispyCloud;
                        } else {
                            for (int d = 1; d < depth; d++) {
                                blocks[index - d] = TFBlocks.wispyCloud;
                            }
                        }
                    }
                }
            }
        }
    }

    public void addGlaciers(int chunkX, int chunkZ, Block[] blocks, byte[] metadata, BiomeGenBase[] biomes) {
        for (int z = 0; z < CHUNK_SIZE; z++) {
            for (int x = 0; x < CHUNK_SIZE; x++) {
                if (biomes[x + z * CHUNK_SIZE] != TFBiomeBase.glacier) {
                    continue;
                }

                int base = columnIndex(x, z);
                int ground = -1;
                for (int y = SURFACE_LIMIT - 1; y >= 0; y--) {
                    if (blocks[base + y] == Blocks.stone) {
                        ground = y;
                        blocks[base + y] = Blocks.gravel;
                        break;
                    }
                }

                // original code generated ice under y32 without stone
                if (ground < 0) {
                    continue;
                }
                int top = Math.min(SURFACE_LIMIT - 1, ground + GLACIER_HEIGHT + 1);
                for (int y = ground + 1; y <= top; y++) {
                    blocks[base + y] = Blocks.ice;
                }
            }
        }
    }

    public void addDarkForestCanopy2(int chunkX, int chunkZ, Block[] blocks, byte[] metadata) {
        int[] thicknessGrid = new int[NOISE_SAMPLES * NOISE_SAMPLES];
        boolean hasDarkForest = false;

        for (int z = 0; z < NOISE_SAMPLES; z++) {
            for (int x = 0; x < NOISE_SAMPLES; x++) {
                int count = 0;
                for (int offsetX = -1; offsetX <= 1; offsetX++) {
                    for (int offsetZ = -1; offsetZ <= 1; offsetZ++) {
                        BiomeGenBase biome = biomesForGeneration[x + offsetX + 2 + (z + offsetZ + 2) * BIOME_SAMPLES];
                        if (biome == TFBiomeBase.darkForest || biome == TFBiomeBase.darkForestCenter) {
                            count++;
                        }
                    }
                }
                thicknessGrid[x + z * NOISE_SAMPLES] = count;
                hasDarkForest |= count > 0;
            }
        }
        if (!hasDarkForest) {
            return;
        }

        TFFeature feature = TFFeature.getNearestFeature(chunkX, chunkZ, worldObj);
        boolean darkTower = feature == TFFeature.darkTower;
        int centerX = 0;
        int centerZ = 0;
        if (darkTower) {
            int[] center = TFFeature.getNearestCenter(chunkX, chunkZ, worldObj);
            centerX = center[0];
            centerZ = center[1];
        }

        double scale = 0.03125D;
        stoneNoise = canopyNoiseGenerator.generateNoiseOctaves(
                stoneNoise,
                chunkX * CHUNK_SIZE,
                chunkZ * CHUNK_SIZE,
                0,
                CHUNK_SIZE,
                CHUNK_SIZE,
                1,
                scale * 2D,
                scale * 2D,
                scale * 2D);

        for (int z = 0; z < CHUNK_SIZE; z++) {
            for (int x = 0; x < CHUNK_SIZE; x++) {
                int gridX = x / 4;
                int gridZ = z / 4;
                float xWeight = (x % 4) * 0.25F + 0.125F;
                float zWeight = (z % 4) * 0.25F + 0.125F;
                int grid = gridX + gridZ * NOISE_SAMPLES;

                float thickness = 0;
                thickness += thicknessGrid[grid] * (1F - xWeight) * (1F - zWeight);
                thickness += thicknessGrid[grid + 1] * xWeight * (1F - zWeight);
                thickness += thicknessGrid[grid + NOISE_SAMPLES] * (1F - xWeight) * zWeight;
                thickness += thicknessGrid[grid + NOISE_SAMPLES + 1] * xWeight * zWeight;
                thickness -= 4;

                if (darkTower) {
                    int dx = x - centerX;
                    int dz = z - centerZ;
                    int distance = (int) Math.sqrt(dx * dx + dz * dz);
                    if (distance < 24) {
                        thickness -= 24 - distance;
                    }
                }
                if (thickness <= 1) {
                    continue;
                }

                int base = columnIndex(x, z);
                int ground = -1;
                for (int y = SURFACE_LIMIT - 1; y >= 0; y--) {
                    if (blocks[base + y] == Blocks.water) {
                        break;
                    }
                    if (blocks[base + y] == Blocks.stone) {
                        ground = y;
                        break;
                    }
                }
                if (ground < 0) {
                    continue;
                }

                // lookup is intentional
                int noise = Math.min(3, (int) (stoneNoise[z | x << 4] / 1.25F));
                int canopyBottom = ground + 12 - (int) (thickness * 0.5F);
                int canopyTop = canopyBottom + (int) (thickness * 1.5F);
                canopyBottom -= noise;

                for (int y = canopyBottom; y < canopyTop; y++) {
                    blocks[base + y] = TFBlocks.darkleaves;
                    metadata[base + y] = 0;
                }
            }
        }
    }

    @Override
    public boolean chunkExists(int chunkX, int chunkZ) {
        return true;
    }

    @Override
    public void populate(IChunkProvider provider, int chunkX, int chunkZ) {
        boolean previousFallingState = BlockFalling.fallInstantly;
        BlockFalling.fallInstantly = true;
        try {
            populateChunk(chunkX, chunkZ);
        } finally {
            BlockFalling.fallInstantly = previousFallingState;
        }
    }

    private void populateChunk(int chunkX, int chunkZ) {
        int blockX = chunkX * CHUNK_SIZE;
        int blockZ = chunkZ * CHUNK_SIZE;
        BiomeGenBase biome = worldObj.getBiomeGenForCoords(blockX + CHUNK_SIZE, blockZ + CHUNK_SIZE);

        rand.setSeed(worldObj.getSeed());
        long seedX = (rand.nextLong() / 2L) * 2L + 1L;
        long seedZ = (rand.nextLong() / 2L) * 2L + 1L;
        rand.setSeed(chunkX * seedX + chunkZ * seedZ ^ worldObj.getSeed());

        boolean generatedStructure = majorFeatureGenerator.generateStructuresInChunk(worldObj, rand, chunkX, chunkZ);
        boolean decorationsAllowed = TFFeature.getNearestFeature(chunkX, chunkZ, worldObj).areChunkDecorationsEnabled;
        boolean disableLakes = generatedStructure || !decorationsAllowed;
        hollowTreeGenerator.generateStructuresInChunk(worldObj, rand, chunkX, chunkZ);

        if (!disableLakes && rand.nextInt(WATER_LAKE_CHANCE) == 0 && biome.theBiomeDecorator.generateLakes) {
            new WorldGenLakes(Blocks.water).generate(
                    worldObj,
                    rand,
                    blockX + rand.nextInt(CHUNK_SIZE) + 8,
                    rand.nextInt(TFWorld.CHUNKHEIGHT),
                    blockZ + rand.nextInt(CHUNK_SIZE) + 8);
        }

        if (!disableLakes && rand.nextInt(LAVA_LAKE_CHANCE) == 0) {
            int lakeX = blockX + rand.nextInt(CHUNK_SIZE) + 8;
            int lakeY = rand.nextInt(rand.nextInt(TFWorld.CHUNKHEIGHT - 8) + 8);
            int lakeZ = blockZ + rand.nextInt(CHUNK_SIZE) + 8;
            if (lakeY < TFWorld.SEALEVEL || rand.nextInt(10) == 0) {
                new WorldGenLakes(Blocks.lava).generate(worldObj, rand, lakeX, lakeY, lakeZ);
            }
        }

        for (int i = 0; i < DUNGEON_ATTEMPTS; i++) {
            new WorldGenDungeons().generate(
                    worldObj,
                    rand,
                    blockX + rand.nextInt(CHUNK_SIZE) + 8,
                    rand.nextInt(TFWorld.CHUNKHEIGHT),
                    blockZ + rand.nextInt(CHUNK_SIZE) + 8);
        }

        biome.decorate(worldObj, rand, blockX, blockZ);
        SpawnerAnimals.performWorldGenSpawning(
                worldObj,
                biome,
                blockX + CHUNK_CENTER_OFFSET,
                blockZ + CHUNK_CENTER_OFFSET,
                CHUNK_SIZE,
                CHUNK_SIZE,
                rand);
        generateSnowAndIce(blockX + CHUNK_CENTER_OFFSET, blockZ + CHUNK_CENTER_OFFSET);
    }

    private void generateSnowAndIce(int startX, int startZ) {
        for (int dx = 0; dx < CHUNK_SIZE; dx++) {
            for (int dz = 0; dz < CHUNK_SIZE; dz++) {
                int x = startX + dx;
                int z = startZ + dz;
                int y = worldObj.getPrecipitationHeight(x, z);
                if (worldObj.isBlockFreezable(x, y - 1, z)) {
                    worldObj.setBlock(x, y - 1, z, Blocks.ice, 0, 2);
                }
                if (worldObj.func_147478_e(x, y, z, true)) {
                    worldObj.setBlock(x, y, z, Blocks.snow_layer, 0, 2);
                }
            }
        }
    }

    @Override
    public boolean saveChunks(boolean flag, IProgressUpdate progress) {
        return true;
    }

    @Override
    public boolean canSave() {
        return true;
    }

    @Override
    public String makeString() {
        return "TwilightLevelSource";
    }

    @Override
    public List<SpawnListEntry> getPossibleCreatures(EnumCreatureType type, int x, int y, int z) {
        TFFeature feature = TFFeature.getFeatureForRegion(x >> 4, z >> 4, worldObj);
        if (feature != TFFeature.nothing) {
            if (isStructureConquered(x, y, z)) {
                return null;
            }
            int spawnListIndex = majorFeatureGenerator.getSpawnListIndexAt(x, y, z);
            if (spawnListIndex >= 0) {
                return feature.getSpawnableList(type, spawnListIndex);
            }
        }

        BiomeGenBase biome = worldObj.getBiomeGenForCoords(x, z);
        if (biome == null) {
            return null;
        }
        if (y < TFWorld.SEALEVEL && type == EnumCreatureType.monster && biome instanceof TFBiomeBase) {
            return ((TFBiomeBase) biome).getUndergroundSpawnableList();
        }
        return biome.getSpawnableList(type);
    }

    public boolean isBlockInStructureBB(int x, int y, int z) {
        return majorFeatureGenerator.hasStructureAt(x, y, z);
    }

    public void setStructureConquered(int x, int y, int z, boolean conquered) {
        majorFeatureGenerator.setStructureConquered(x, y, z, conquered);
    }

    public StructureBoundingBox getSBBAt(int x, int y, int z) {
        return majorFeatureGenerator.getSBBAt(x, y, z);
    }

    public boolean isBlockProtected(int x, int y, int z) {
        return majorFeatureGenerator.isBlockProtectedAt(x, y, z);
    }

    public boolean isStructureConquered(int x, int y, int z) {
        return majorFeatureGenerator.isStructureConquered(x, y, z);
    }

    public boolean isStructureLocked(int x, int y, int z, int lockIndex) {
        return majorFeatureGenerator.isStructureLocked(x, y, z, lockIndex);
    }

    public boolean isBlockInFullStructure(int x, int z) {
        return majorFeatureGenerator.isBlockInFullStructure(x, z);
    }

    public boolean isBlockNearFullStructure(int x, int z, int range) {
        return majorFeatureGenerator.isBlockNearFullStructure(x, z, range);
    }

    public StructureBoundingBox getFullSBBAt(int x, int z) {
        return majorFeatureGenerator.getFullSBBAt(x, z);
    }

    public StructureBoundingBox getFullSBBNear(int x, int z, int range) {
        return majorFeatureGenerator.getFullSBBNear(x, z, range);
    }

    @Override
    public int getLoadedChunkCount() {
        return 0;
    }

    @Override
    public void recreateStructures(int chunkX, int chunkZ) {
        majorFeatureGenerator.func_151539_a(this, worldObj, chunkX, chunkZ, null);
        hollowTreeGenerator.func_151539_a(this, worldObj, chunkX, chunkZ, null);
    }

    @Override
    public boolean unloadQueuedChunks() {
        return false;
    }

    @Override
    public void saveExtraData() {}

    @Override
    public ChunkPosition func_147416_a(World world, String name, int x, int y, int z) {
        return null;
    }
}
