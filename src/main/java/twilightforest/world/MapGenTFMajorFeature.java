package twilightforest.world;

import java.util.Collection;
import java.util.List;

import net.minecraft.world.gen.structure.MapGenStructure;
import net.minecraft.world.gen.structure.MapGenStructureData;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.gen.structure.StructureStart;

import cpw.mods.fml.common.ObfuscationReflectionHelper;
import twilightforest.TFFeature;
import twilightforest.structures.StructureTFComponent;
import twilightforest.structures.StructureTFMajorFeatureStart;

public class MapGenTFMajorFeature extends MapGenStructure {

    private static final String STRUCTURE_DATA_NAME = "TFFeature";

    @Override
    protected boolean canSpawnStructureAtCoords(int chunkX, int chunkZ) {
        return TFFeature.getFeatureDirectlyAt(chunkX, chunkZ, worldObj).isStructureEnabled;
    }

    @Override
    protected StructureStart getStructureStart(int chunkX, int chunkZ) {
        // preserve the original seed sequence
        long worldSeed = worldObj.getSeed();
        rand.setSeed(worldSeed);
        long xMultiplier = rand.nextLong();
        long zMultiplier = rand.nextLong();
        rand.setSeed((long) chunkX * xMultiplier ^ (long) chunkZ * zMultiplier ^ worldSeed);
        rand.nextInt();

        return new StructureTFMajorFeatureStart(worldObj, rand, chunkX, chunkZ);
    }

    @Override
    public String func_143025_a() {
        return STRUCTURE_DATA_NAME;
    }

    public int getSpawnListIndexAt(int x, int y, int z) {
        int highestIndex = -1;

        for (StructureStart start : getStructureStarts()) {
            if (!containsXZ(start, x, z)) {
                continue;
            }

            for (StructureComponent component : getStructureComponents(start)) {
                if (!containsXYZ(component, x, y, z)) {
                    continue;
                }

                int index = component instanceof StructureTFComponent
                        ? ((StructureTFComponent) component).spawnListIndex
                        : 0;
                highestIndex = Math.max(highestIndex, index);
            }
        }

        return highestIndex;
    }

    // retains previous behaviour
    public StructureBoundingBox getSBBAt(int x, int y, int z) {
        StructureBoundingBox found = null;

        for (StructureStart start : getStructureStarts()) {
            if (!containsXZ(start, x, z)) {
                continue;
            }

            for (StructureComponent component : getStructureComponents(start)) {
                if (containsXYZ(component, x, y, z)) {
                    found = component.getBoundingBox();
                }
            }
        }

        return found;
    }

    public boolean isBlockProtectedAt(int x, int y, int z) {
        for (StructureStart start : getStructureStarts()) {
            if (!containsXZ(start, x, z)) {
                continue;
            }

            for (StructureComponent component : getStructureComponents(start)) {
                if (!containsXYZ(component, x, y, z)) {
                    continue;
                }

                if (!(component instanceof StructureTFComponent)
                        || ((StructureTFComponent) component).isComponentProtected()) {
                    return true;
                }
            }
        }

        return false;
    }

    public void setStructureConquered(int x, int y, int z, boolean conquered) {
        MapGenStructureData data = null;

        for (StructureStart start : getStructureStarts()) {
            if (!containsXZ(start, x, z) || !(start instanceof StructureTFMajorFeatureStart)) {
                continue;
            }

            StructureTFMajorFeatureStart feature = (StructureTFMajorFeatureStart) start;
            if (feature.isConquered == conquered) {
                continue;
            }

            if (data == null) {
                data = getStructureData();
            }

            feature.isConquered = conquered;
            int chunkX = start.func_143019_e();
            int chunkZ = start.func_143018_f();
            data.func_143043_a(feature.func_143021_a(chunkX, chunkZ), chunkX, chunkZ);
        }

        if (data != null) {
            data.setDirty(true);
        }
    }

    public boolean isStructureConquered(int x, int y, int z) {
        for (StructureStart start : getStructureStarts()) {
            if (containsXZ(start, x, z) && start instanceof StructureTFMajorFeatureStart
                    && ((StructureTFMajorFeatureStart) start).isConquered) {
                return true;
            }
        }

        return false;
    }

    public boolean isStructureLocked(int x, int y, int z, int lockIndex) {
        if (lockIndex < 0) {
            return false;
        }

        for (StructureStart start : getStructureStarts()) {
            if (containsXZ(start, x, z) && start instanceof StructureTFMajorFeatureStart
                    && ((StructureTFMajorFeatureStart) start).isLocked(lockIndex)) {
                return true;
            }
        }

        return false;
    }

    public boolean isBlockInFullStructure(int x, int z) {
        return getFullSBBAt(x, z) != null;
    }

    public boolean isBlockNearFullStructure(int x, int z, int range) {
        return getFullSBBNear(x, z, range) != null;
    }

    public StructureBoundingBox getFullSBBAt(int x, int z) {
        return findIntersectingStructureBox(x, z, x, z);
    }

    public StructureBoundingBox getFullSBBNear(int x, int z, int range) {
        if (range < 0) {
            return null;
        }

        // keep the search box valid even if the requested range exceeds integer coords
        return findIntersectingStructureBox(
                clampToInt((long) x - range),
                clampToInt((long) z - range),
                clampToInt((long) x + range),
                clampToInt((long) z + range));
    }

    private StructureBoundingBox findIntersectingStructureBox(int minX, int minZ, int maxX, int maxZ) {
        for (StructureStart start : getStructureStarts()) {
            if (intersectsXZ(start, minX, minZ, maxX, maxZ)) {
                return start.getBoundingBox();
            }
        }

        return null;
    }

    private static boolean containsXZ(StructureStart start, int x, int z) {
        return intersectsXZ(start, x, z, x, z);
    }

    private static boolean intersectsXZ(StructureStart start, int minX, int minZ, int maxX, int maxZ) {
        return start != null && start.isSizeableStructure()
                && start.getBoundingBox() != null
                && start.getBoundingBox().intersectsWith(minX, minZ, maxX, maxZ);
    }

    private static boolean containsXYZ(StructureComponent component, int x, int y, int z) {
        return component != null && component.getBoundingBox() != null
                && component.getBoundingBox().isVecInside(x, y, z);
    }

    private static int clampToInt(long value) {
        return (int) Math.max(Integer.MIN_VALUE, Math.min(Integer.MAX_VALUE, value));
    }

    @SuppressWarnings("unchecked")
    private Collection<StructureStart> getStructureStarts() {
        return structureMap.values();
    }

    @SuppressWarnings("unchecked")
    private static List<StructureComponent> getStructureComponents(StructureStart start) {
        return start.getComponents();
    }

    private MapGenStructureData getStructureData() {
        // use the generators dim specific data, not global map storage
        return ObfuscationReflectionHelper.getPrivateValue(MapGenStructure.class, this, "field_143029_e");
    }
}
