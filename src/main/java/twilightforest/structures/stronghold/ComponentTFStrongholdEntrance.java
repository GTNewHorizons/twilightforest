package twilightforest.structures.stronghold;

import java.util.List;
import java.util.Random;

import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;

import cpw.mods.fml.common.FMLLog;

public class ComponentTFStrongholdEntrance extends StructureTFStrongholdComponent {

    public TFStrongholdPieces lowerPieces;

    public ComponentTFStrongholdEntrance() {}

    public ComponentTFStrongholdEntrance(World world, Random rand, int i, int x, int y, int z) {
        super(i, 0, x, y - 10, z);

        this.deco = new StructureTFDecoratorStronghold();

        lowerPieces = new TFStrongholdPieces();
    }

    /**
     * Initiates construction of the Structure Component picked, at the current Location of StructGen
     */
    @Override
    public void buildComponent(StructureComponent parent, List<StructureComponent> pieceList, Random random) {
        super.buildComponent(parent, pieceList, random);

        // make a random component in each direction
        lowerPieces.prepareStructurePieces();
        addNewComponent(parent, pieceList, random, 0, 4, 1, 18);
        lowerPieces.prepareStructurePieces();
        if (listContainsBossRoom(pieceList)) {
            lowerPieces.markBossRoomUsed();
        }
        addNewComponent(parent, pieceList, random, 1, -1, 1, 13);
        lowerPieces.prepareStructurePieces();
        if (listContainsBossRoom(pieceList)) {
            lowerPieces.markBossRoomUsed();
        }
        addNewComponent(parent, pieceList, random, 2, 13, 1, -1);
        lowerPieces.prepareStructurePieces();
        if (listContainsBossRoom(pieceList)) {
            lowerPieces.markBossRoomUsed();
        }
        addNewComponent(parent, pieceList, random, 3, 18, 1, 4);
        if (!listContainsBossRoom(pieceList)) {
            FMLLog.fine("[TwilightForest]  Did not find boss room from exit 3 - EPIC FAIL");
        }
        // add the upper stronghold
        StructureTFStrongholdComponent accessChamber = new ComponentTFStrongholdAccessChamber(
                2,
                this.getCoordBaseMode(),
                boundingBox.minX + 8,
                boundingBox.minY + 7,
                boundingBox.minZ + 4);
        pieceList.add(accessChamber);
        accessChamber.buildComponent(this, pieceList, random);

    }

    private static boolean listContainsBossRoom(List<? extends StructureComponent> list) {
        for (StructureComponent component : list) {
            if (component instanceof ComponentTFStrongholdBossRoom) {
                return true;
            }
        }

        return false;
    }

    /**
     * Make a bounding box for this room
     */
    public StructureBoundingBox generateBoundingBox(int facing, int x, int y, int z) {
        return StructureBoundingBox.getComponentToAddBoundingBox(x, y, z, -1, -1, 0, 18, 7, 18, facing);
    }

    /**
     * Generate the blocks that go here
     */
    @Override
    public boolean addComponentParts(World world, Random rand, StructureBoundingBox sbb) {
        placeStrongholdWalls(world, sbb, 0, 0, 0, 17, 6, 17, rand, deco.randomBlocks);

        // statues
        placeCornerStatue(world, 5, 1, 5, 0, sbb);
        placeCornerStatue(world, 5, 1, 12, 1, sbb);
        placeCornerStatue(world, 12, 1, 5, 2, sbb);
        placeCornerStatue(world, 12, 1, 12, 3, sbb);

        // statues
        this.placeWallStatue(world, 9, 1, 16, 0, sbb);
        this.placeWallStatue(world, 1, 1, 9, 1, sbb);
        this.placeWallStatue(world, 8, 1, 1, 2, sbb);
        this.placeWallStatue(world, 16, 1, 8, 3, sbb);

        // doors
        this.placeDoors(world, rand, sbb);

        return true;
    }

}
