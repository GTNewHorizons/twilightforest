package twilightforest;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapData;

import twilightforest.world.ChunkProviderTwilightForest;
import twilightforest.world.TFWorldChunkManager;
import twilightforest.world.WorldProviderTwilightForest;

public class TFMagicMapData extends MapData {

    private static final byte FEATURE_DATA_BYTE = 18;
    private static final String FEATURES_TAG = "features";
    private static final int FEATURE_BYTES = 3;
    private static final int MAP_MARKER_LIMIT = 64;
    private static final byte MAP_ROTATION = 8;

    public static final byte CONQ_OFFSET = 80;
    public List<MapCoord> featuresVisibleOnMap = new ArrayList<>();

    public TFMagicMapData(String mapName) {
        super(mapName);
    }

    /**
     * reads in data from the NBTTagCompound into this MapDataBase
     */
    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        this.featuresVisibleOnMap.clear();
        byte[] storedFeatures = data.getByteArray(FEATURES_TAG);
        if (storedFeatures.length > 0) {
            updateMPMapData(storedFeatures);
        }
        // else {
        // System.out.println("Can't find feature storage for " + this.mapName);
        // }
    }

    /**
     * write data to NBTTagCompound from this MapDataBase, similar to Entities and TileEntities
     */
    @Override
    public void writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        if (!featuresVisibleOnMap.isEmpty()) {
            data.setByteArray(FEATURES_TAG, makeFeatureStorageArray());
        } else {
            data.removeTag(FEATURES_TAG);
        }
    }

    /**
     * Adds a twilight forest feature to the map.
     */
    public void addFeatureToMap(TFFeature feature, int x, int z, boolean conquered) {
        if (feature == null) {
            return;
        }

        // check the full width coords before converting to bytes, otherwise distant features wrap onto the map
        int relativeX = (x - this.xCenter) >> this.scale;
        int relativeZ = (z - this.zCenter) >> this.scale;
        if (relativeX < -MAP_MARKER_LIMIT || relativeX > MAP_MARKER_LIMIT
                || relativeZ < -MAP_MARKER_LIMIT
                || relativeZ > MAP_MARKER_LIMIT) {
            return;
        }

        byte markerIcon = (byte) (feature.featureID + (conquered ? CONQ_OFFSET : 0));
        byte mapX = (byte) (relativeX << 1);
        byte mapZ = (byte) (relativeZ << 1);

        // look for a feature already at those coordinates
        for (MapCoord existing : featuresVisibleOnMap) {
            if (existing.centerX == mapX && existing.centerZ == mapZ) {
                // refresh the icon so newly conquered or reactivated structures are displayed correctly
                if (existing.iconSize != markerIcon) {
                    existing.iconSize = markerIcon;
                    markDirty();
                }
                return;
            }
        }

        // if we didn't find it, add it
        featuresVisibleOnMap.add(new MapCoord(markerIcon, mapX, mapZ, MAP_ROTATION));
        markDirty();
    }

    /**
     * Checks existing features against the feature cache and removes/changes wrong ones. Does not add features.
     */
    public void checkExistingFeatures(World world) {
        if (world == null || !(world.getWorldChunkManager() instanceof TFWorldChunkManager manager)
                || !(world.provider instanceof WorldProviderTwilightForest provider)) {
            return;
        }

        ChunkProviderTwilightForest chunkProvider = provider.getChunkProvider();
        if (chunkProvider == null) {
            return;
        }

        boolean changed = false;
        for (Iterator<MapCoord> iterator = featuresVisibleOnMap.iterator(); iterator.hasNext();) {
            MapCoord coord = iterator.next();
            // System.out.printf("Existing feature at %d, %d.\n", coord.centerX, coord.centerZ);
            if (coord.iconSize == 0) {
                iterator.remove();
                changed = true;
                continue;
            }

            // map icons use twice the map pixel coordinate, division also handles scale zero safely
            int worldX = this.xCenter + Math.floorDiv(coord.centerX * (1 << this.scale), 2);
            int worldZ = this.zCenter + Math.floorDiv(coord.centerZ * (1 << this.scale), 2);
            int featureId = manager.getFeatureID(worldX, worldZ, world);
            byte newIcon = (byte) (featureId
                    + (chunkProvider.isStructureConquered(worldX, 0, worldZ) ? CONQ_OFFSET : 0));
            if (coord.iconSize != newIcon) {
                coord.iconSize = newIcon;
                changed = true;
            }
        }
        if (changed) {
            markDirty();
        }
    }

    /**
     * Updates the client's map with information from other players in MP
     */
    @Override
    public void updateMPMapData(byte[] data) {
        if (data == null || data.length == 0) {
            return;
        }
        if (data[0] != FEATURE_DATA_BYTE) {
            super.updateMPMapData(data);
            return;
        }
        if ((data.length - 1) % FEATURE_BYTES != 0) {
            return;
        }

        // this is feature data, we can handle that directly
        featuresVisibleOnMap.clear();
        for (int offset = 1; offset < data.length; offset += FEATURE_BYTES) {
            byte markerIcon = data[offset];
            byte mapX = data[offset + 1];
            byte mapZ = data[offset + 2];
            featuresVisibleOnMap.add(new MapCoord(markerIcon, mapX, mapZ, MAP_ROTATION));
        }
        markDirty();
    }

    /**
     * This makes a byte array that we store our feature data in to send with map update packets
     */
    public byte[] makeFeatureStorageArray() {
        byte[] storage = new byte[featuresVisibleOnMap.size() * FEATURE_BYTES + 1];
        storage[0] = FEATURE_DATA_BYTE;
        for (int i = 0; i < featuresVisibleOnMap.size(); i++) {
            MapCoord coord = featuresVisibleOnMap.get(i);
            int offset = i * FEATURE_BYTES + 1;
            storage[offset] = coord.iconSize;
            storage[offset + 1] = coord.centerX;
            storage[offset + 2] = coord.centerZ;
        }
        return storage;
    }
}
