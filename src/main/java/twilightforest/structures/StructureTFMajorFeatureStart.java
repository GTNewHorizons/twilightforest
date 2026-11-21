package twilightforest.structures;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.gen.structure.MapGenStructureIO;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.gen.structure.StructureStart;
import net.minecraft.world.gen.structure.StructureStrongholdPieces;

import twilightforest.TFFeature;
import twilightforest.biomes.TFBiomeBase;
import twilightforest.integration.structures.TFMajorFeatureProviders;
import twilightforest.structures.courtyard.ComponentTFNagaCourtyardMain;
import twilightforest.structures.courtyard.TFNagaCourtyardPieces;
import twilightforest.structures.darktower.ComponentTFDarkTowerMain;
import twilightforest.structures.darktower.TFDarkTowerPieces;
import twilightforest.structures.finalcastle.ComponentTFFinalCastleMain;
import twilightforest.structures.finalcastle.TFFinalCastlePieces;
import twilightforest.structures.hollowtree.StructureTFHollowTreeStart;
import twilightforest.structures.hollowtree.TFHollowTreePieces;
import twilightforest.structures.icetower.ComponentTFIceTowerMain;
import twilightforest.structures.icetower.TFIceTowerPieces;
import twilightforest.structures.lichtower.ComponentTFTowerMain;
import twilightforest.structures.lichtower.TFLichTowerPieces;
import twilightforest.structures.minotaurmaze.ComponentTFMazeRuins;
import twilightforest.structures.minotaurmaze.TFMinotaurMazePieces;
import twilightforest.structures.mushroomtower.ComponentTFMushroomTowerMain;
import twilightforest.structures.mushroomtower.TFMushroomTowerPieces;
import twilightforest.structures.stronghold.ComponentTFStrongholdEntrance;
import twilightforest.structures.stronghold.TFStrongholdPieces;
import twilightforest.structures.trollcave.ComponentTFTrollCaveMain;
import twilightforest.structures.trollcave.TFTrollCavePieces;
import twilightforest.world.TFWorld;
import twilightforest.world.TFWorldChunkManager;

public class StructureTFMajorFeatureStart extends StructureStart {

    private static final int STRONGHOLD_Y_OFFSET = -33;
    private static final int DARK_FOREST_TOWER_Y_OFFSET = 4;

    public static int NUM_LOCKS = 4;

    public TFFeature feature;
    public boolean isConquered;
    public byte[] lockBytes = new byte[NUM_LOCKS];

    static {
        // do not rename, it will break!!
        MapGenStructureIO.registerStructure(StructureTFMajorFeatureStart.class, "TFFeature");
        MapGenStructureIO.registerStructure(StructureTFHollowTreeStart.class, "TFHollowTree");

        TFStrongholdPieces.registerPieces();
        TFNagaCourtyardPieces.registerPieces();
        TFMinotaurMazePieces.registerPieces();
        TFDarkTowerPieces.registerPieces();
        TFLichTowerPieces.registerPieces();
        TFIceTowerPieces.registerPieces();
        TFMushroomTowerPieces.registerPieces();
        TFHollowTreePieces.registerPieces();
        TFTrollCavePieces.registerPieces();
        TFFinalCastlePieces.registerFinalCastlePieces();

        MapGenStructureIO.func_143031_a(ComponentTFHedgeMaze.class, "TFHedge");
        MapGenStructureIO.func_143031_a(ComponentTFHillMaze.class, "TFHillMaze");
        MapGenStructureIO.func_143031_a(ComponentTFHollowHill.class, "TFHill");
        MapGenStructureIO.func_143031_a(ComponentTFHydraLair.class, "TFHydra");
        MapGenStructureIO.func_143031_a(ComponentTFQuestGrove.class, "TFQuest1");
        MapGenStructureIO.func_143031_a(ComponentTFYetiCave.class, "TFYeti");
    }

    // used when loading structures from nbt
    public StructureTFMajorFeatureStart() {}

    public StructureTFMajorFeatureStart(World world, Random random, int chunkX, int chunkZ) {
        // preserve the original initilisation and call order for worldgen compatibility
        StructureStrongholdPieces.prepareStructurePieces();

        int x = (chunkX << 4) + 8;
        int z = (chunkZ << 4) + 8;
        int y = TFWorld.SEALEVEL + 1;

        feature = TFFeature.getFeatureDirectlyAt(chunkX, chunkZ, world);
        StructureComponent root = makeFirstComponent(world, random, feature, x, y, z);
        if (root == null) {
            return;
        }

        components.add(root);
        root.buildComponent(root, components, random);
        updateBoundingBox();

        if (root instanceof StructureStrongholdPieces.Stairs2) {
            completeStronghold((StructureStrongholdPieces.Stairs2) root, random);
        }

        if (root instanceof ComponentTFTowerMain || root instanceof ComponentTFDarkTowerMain) {
            moveToAvgGroundLevel(world, x, z);
        }
    }

    private void completeStronghold(StructureStrongholdPieces.Stairs2 entrance, Random random) {
        List<?> pendingPieces = entrance.field_75026_c;

        while (pendingPieces != null && !pendingPieces.isEmpty()) {
            int index = random.nextInt(pendingPieces.size());
            StructureComponent piece = (StructureComponent) pendingPieces.remove(index);
            piece.buildComponent(entrance, components, random);
        }

        updateBoundingBox();
        offsetStructure(STRONGHOLD_Y_OFFSET);
    }

    public StructureComponent makeFirstComponent(World world, Random random, TFFeature feature, int x, int y, int z) {
        StructureComponent override = TFMajorFeatureProviders.getTFMajorFeature(world, random, feature, x, y, z);
        if (override != null) {
            return override;
        }

        if (feature == TFFeature.nagaCourtyard) {
            return new ComponentTFNagaCourtyardMain(world, random, 0, x, y, z);
        }
        if (feature == TFFeature.hedgeMaze) {
            return new ComponentTFHedgeMaze(world, random, 0, x, y, z);
        }
        if (feature == TFFeature.hill1) {
            return new ComponentTFHollowHill(world, random, 0, 1, x, y, z);
        }
        if (feature == TFFeature.hill2) {
            return new ComponentTFHollowHill(world, random, 0, 2, x, y, z);
        }
        if (feature == TFFeature.hill3) {
            return new ComponentTFHollowHill(world, random, 0, 3, x, y, z);
        }
        if (feature == TFFeature.lichTower) {
            return new ComponentTFTowerMain(world, random, 0, x, y, z);
        }
        if (feature == TFFeature.questGrove) {
            return new ComponentTFQuestGrove(world, random, 0, x, y, z);
        }
        if (feature == TFFeature.hydraLair) {
            return new ComponentTFHydraLair(world, random, 0, x, y, z);
        }
        if (feature == TFFeature.labyrinth) {
            return new ComponentTFMazeRuins(world, random, 0, x, y, z);
        }
        if (feature == TFFeature.darkTower) {
            return new ComponentTFDarkTowerMain(world, random, 0, x, y - 1, z);
        }
        if (feature == TFFeature.tfStronghold) {
            return new ComponentTFStrongholdEntrance(world, random, 0, x, y, z);
        }
        if (feature == TFFeature.iceTower) {
            return new ComponentTFIceTowerMain(world, random, 0, x, y, z);
        }
        if (feature == TFFeature.mushroomTower) {
            return new ComponentTFMushroomTowerMain(world, random, 0, x, y, z);
        }
        if (feature == TFFeature.yetiCave) {
            return new ComponentTFYetiCave(world, random, 0, x, y, z);
        }
        if (feature == TFFeature.trollCave) {
            return new ComponentTFTrollCaveMain(world, random, 0, x, y, z);
        }
        if (feature == TFFeature.finalCastle) {
            return new ComponentTFFinalCastleMain(world, random, 0, x, y, z);
        }

        return null;
    }

    @Override
    public boolean isSizeableStructure() {
        return feature != null && feature.isStructureEnabled && !components.isEmpty();
    }

    // apply the biome height used by litch and dark towers
    protected void moveToAvgGroundLevel(World world, int x, int z) {
        if (!(world.getWorldChunkManager() instanceof TFWorldChunkManager)) {
            return;
        }

        BiomeGenBase biome = world.getBiomeGenForCoords(x, z);
        if (biome == null) {
            return;
        }

        int offsetY = (int) ((biome.rootHeight + biome.heightVariation) * 8);
        if (biome == TFBiomeBase.darkForest) {
            offsetY += DARK_FOREST_TOWER_Y_OFFSET;
        }

        if (offsetY > 0) {
            offsetStructure(offsetY);
        }
    }

    // move the bounding box and component by the same amount
    private void offsetStructure(int offsetY) {
        if (boundingBox != null) {
            boundingBox.offset(0, offsetY, 0);
        }

        for (StructureComponent component : components) {
            if (component != null && component.getBoundingBox() != null) {
                component.getBoundingBox().offset(0, offsetY, 0);
            }
        }
    }

    @Override
    public void func_143022_a(NBTTagCompound nbt) {
        super.func_143022_a(nbt);
        nbt.setBoolean("Conquered", isConquered);
        nbt.setInteger("FeatureID", feature != null ? feature.featureID : TFFeature.nothing.featureID);
        nbt.setByteArray("Locks", lockBytes != null ? lockBytes : new byte[NUM_LOCKS]);
    }

    @Override
    public void func_143017_b(NBTTagCompound nbt) {
        super.func_143017_b(nbt);
        isConquered = nbt.getBoolean("Conquered");

        int featureID = nbt.getInteger("FeatureID");
        feature = featureID >= 0 && featureID < TFFeature.featureList.length ? TFFeature.featureList[featureID] : null;
        if (feature == null) {
            feature = TFFeature.nothing;
        }

        byte[] savedLocks = nbt.getByteArray("Locks");
        lockBytes = savedLocks.length >= NUM_LOCKS ? savedLocks : Arrays.copyOf(savedLocks, NUM_LOCKS);
    }

    public boolean isLocked(int lockIndex) {
        return lockBytes != null && lockIndex >= 0 && lockIndex < lockBytes.length && lockBytes[lockIndex] != 0;
    }
}
