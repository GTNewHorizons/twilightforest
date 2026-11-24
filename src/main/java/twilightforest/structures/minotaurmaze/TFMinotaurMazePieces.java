package twilightforest.structures.minotaurmaze;

import net.minecraft.world.gen.structure.MapGenStructureIO;

public final class TFMinotaurMazePieces {

    private TFMinotaurMazePieces() {}

    public static void registerPieces() {
        // these IDs are saved in structure nbt
        MapGenStructureIO.func_143031_a(ComponentTFMazeCorridor.class, "TFMMC");
        MapGenStructureIO.func_143031_a(ComponentTFMazeCorridorIronFence.class, "TFMMCIF");
        MapGenStructureIO.func_143031_a(ComponentTFMazeCorridorRoots.class, "TFMMCR");
        MapGenStructureIO.func_143031_a(ComponentTFMazeCorridorShrooms.class, "TFMMCS");
        MapGenStructureIO.func_143031_a(ComponentTFMazeDeadEnd.class, "TFMMDE");
        MapGenStructureIO.func_143031_a(ComponentTFMazeDeadEndChest.class, "TFMMDEC");
        MapGenStructureIO.func_143031_a(ComponentTFMazeDeadEndFountain.class, "TFMMDEF");
        MapGenStructureIO.func_143031_a(ComponentTFMazeDeadEndFountainLava.class, "TFMMDEFL");
        MapGenStructureIO.func_143031_a(ComponentTFMazeDeadEndPainting.class, "TFMMDEP");
        MapGenStructureIO.func_143031_a(ComponentTFMazeDeadEndRoots.class, "TFMMDER");
        MapGenStructureIO.func_143031_a(ComponentTFMazeDeadEndShrooms.class, "TFMMDES");
        MapGenStructureIO.func_143031_a(ComponentTFMazeDeadEndTorches.class, "TFMMDET");
        MapGenStructureIO.func_143031_a(ComponentTFMazeDeadEndTrappedChest.class, "TFMMDETC");
        MapGenStructureIO.func_143031_a(ComponentTFMazeEntranceShaft.class, "TFMMES");
        MapGenStructureIO.func_143031_a(ComponentTFMazeMound.class, "TFMMMound");
        MapGenStructureIO.func_143031_a(ComponentTFMazeMushRoom.class, "TFMMMR");
        MapGenStructureIO.func_143031_a(ComponentTFMazeRoom.class, "TFMMR");
        MapGenStructureIO.func_143031_a(ComponentTFMazeRoomBoss.class, "TFMMRB");
        MapGenStructureIO.func_143031_a(ComponentTFMazeRoomCollapse.class, "TFMMRC");
        MapGenStructureIO.func_143031_a(ComponentTFMazeRoomExit.class, "TFMMRE");
        MapGenStructureIO.func_143031_a(ComponentTFMazeRoomFountain.class, "TFMMRF");
        MapGenStructureIO.func_143031_a(ComponentTFMazeRoomSpawnerChests.class, "TFMMRSC");
        MapGenStructureIO.func_143031_a(ComponentTFMazeRoomVault.class, "TFMMRV");
        MapGenStructureIO.func_143031_a(ComponentTFMazeRuins.class, "TFMMRuins");
        MapGenStructureIO.func_143031_a(ComponentTFMazeUpperEntrance.class, "TFMMUE");
        MapGenStructureIO.func_143031_a(ComponentTFMinotaurMaze.class, "TFMMaze");
    }
}
