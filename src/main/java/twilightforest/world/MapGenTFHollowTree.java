package twilightforest.world;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.util.ReportedException;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.gen.MapGenBase;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureStart;

import twilightforest.TFFeature;
import twilightforest.TwilightForestMod;
import twilightforest.biomes.TFBiomeBase;
import twilightforest.structures.hollowtree.StructureTFHollowTreeStart;

public class MapGenTFHollowTree extends MapGenBase {

    private static final int CHUNK_SIZE = 16;
    private static final int CHUNK_POPULATION_OFFSET = CHUNK_SIZE / 2;
    private static final int CHUNK_MAX_OFFSET = CHUNK_SIZE - 1;

    public MapGenTFHollowTree() {
        rand = new FastRandom(0L);
    }

    // public static final int SPAWN_CHANCE = 48;
    /**
     * Used to store a list of all structures that have been recursively generated. Used so that during recursive
     * generation, the structure generator can avoid generating structures that intersect ones that have already been
     * placed.
     */
    protected Map<Long, StructureStart> structureMap = new HashMap<>();

    /**
     * Generate recursively
     */
    @Override
    protected void func_151538_a(World world, final int chunkX, final int chunkZ, int centerX, int centerZ,
            Block[] blockData) {
        long key = ChunkCoordIntPair.chunkXZ2Int(chunkX, chunkZ);
        if (structureMap.containsKey(key)) {
            return;
        }

        // keep the random draw in the same position as vanilla structure preparation
        rand.nextInt();

        try {
            if (canSpawnStructureAtCoords(chunkX, chunkZ)) {
                structureMap.put(key, getStructureStart(chunkX, chunkZ));
            }
        } catch (Throwable throwable) {
            CrashReport crashReport = CrashReport.makeCrashReport(throwable, "Exception preparing hollow tree");
            CrashReportCategory category = crashReport.makeCategory("Feature being prepared");
            category.addCrashSectionCallable(
                    "Is feature chunk",
                    () -> MapGenTFHollowTree.this.canSpawnStructureAtCoords(chunkX, chunkZ) ? "True" : "False");
            category.addCrashSection("Chunk location", String.format("%d,%d", chunkX, chunkZ));
            category.addCrashSectionCallable(
                    "Chunk pos hash",
                    () -> String.valueOf(ChunkCoordIntPair.chunkXZ2Int(chunkX, chunkZ)));
            category.addCrashSectionCallable(
                    "Structure type",
                    () -> MapGenTFHollowTree.this.getClass().getCanonicalName());
            throw new ReportedException(crashReport);
        }
    }

    public boolean generateStructuresInChunk(World world, Random rand, int chunkX, int chunkZ) {
        // Structure population uses the vanilla +8 offset, not the raw chunk boundary.
        int mapX = (chunkX << 4) + CHUNK_POPULATION_OFFSET;
        int mapZ = (chunkZ << 4) + CHUNK_POPULATION_OFFSET;
        boolean generated = false;

        for (StructureStart structureStart : structureMap.values()) {
            if (!structureStart.isSizeableStructure() || !structureStart.getBoundingBox()
                    .intersectsWith(mapX, mapZ, mapX + CHUNK_MAX_OFFSET, mapZ + CHUNK_MAX_OFFSET)) {
                continue;
            }

            structureStart.generateStructure(
                    world,
                    rand,
                    new StructureBoundingBox(mapX, mapZ, mapX + CHUNK_MAX_OFFSET, mapZ + CHUNK_MAX_OFFSET));
            generated = true;
        }

        return generated;
    }

    /** A list of all the biomes twilight oaks can spawn in. */
    public static List<BiomeGenBase> oakSpawnBiomes = Arrays.asList(
            TFBiomeBase.twilightForest,
            TFBiomeBase.twilightForest2,
            TFBiomeBase.mushrooms,
            TFBiomeBase.tfSwamp,
            TFBiomeBase.clearing,
            TFBiomeBase.oakSavanna,
            TFBiomeBase.fireflyForest,
            TFBiomeBase.deepMushrooms,
            TFBiomeBase.enchantedForest,
            TFBiomeBase.fireSwamp);

    protected boolean canSpawnStructureAtCoords(int chunkX, int chunkZ) {
        int chance = TwilightForestMod.twilightOakChance;
        return chance > 0 && rand.nextInt(chance) == 0
                && TFFeature.getNearestFeature(chunkX, chunkZ, worldObj).areChunkDecorationsEnabled
                && worldObj.getWorldChunkManager().areBiomesViable(
                        chunkX * CHUNK_SIZE + CHUNK_POPULATION_OFFSET,
                        chunkZ * CHUNK_SIZE + CHUNK_POPULATION_OFFSET,
                        0,
                        oakSpawnBiomes);
    }

    protected StructureStart getStructureStart(int chunkX, int chunkZ) {
        return new StructureTFHollowTreeStart(worldObj, rand, chunkX, chunkZ);
    }
}
