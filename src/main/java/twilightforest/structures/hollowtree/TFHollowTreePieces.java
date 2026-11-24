package twilightforest.structures.hollowtree;

import java.util.Random;

import net.minecraft.world.World;
import net.minecraft.world.gen.structure.MapGenStructureIO;
import net.minecraft.world.gen.structure.StructureBoundingBox;

import twilightforest.structures.StructureTFComponent;

public class TFHollowTreePieces {

    private TFHollowTreePieces() {}

    public static void registerPieces() {
        MapGenStructureIO.func_143031_a(ComponentTFHollowTreeLargeBranch.class, "TFHTLB");
        MapGenStructureIO.func_143031_a(ComponentTFHollowTreeMedBranch.class, "TFHTMB");
        MapGenStructureIO.func_143031_a(ComponentTFHollowTreeSmallBranch.class, "TFHTSB");
        MapGenStructureIO.func_143031_a(ComponentTFHollowTreeTrunk.class, "TFHTTr");
        MapGenStructureIO.func_143031_a(ComponentTFLeafSphere.class, "TFHTLS");
        MapGenStructureIO.func_143031_a(ComponentTFHollowTreeRoot.class, "TFHTRo");
        MapGenStructureIO.func_143031_a(LegacyHollowTreeStartPiece.class, "TFHTLSt");
        MapGenStructureIO.func_143031_a(ComponentTFHollowTreeLeafDungeon.class, "TFHTLD");

    }

    public static final class LegacyHollowTreeStartPiece extends StructureTFComponent {

        public LegacyHollowTreeStartPiece() {
            super();
        }

        @Override
        public boolean addComponentParts(World world, Random rand, StructureBoundingBox sbb) {
            return true;
        }
    }
}
