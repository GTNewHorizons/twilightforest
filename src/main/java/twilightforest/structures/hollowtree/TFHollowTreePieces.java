package twilightforest.structures.hollowtree;

import net.minecraft.world.gen.structure.MapGenStructureIO;
import net.minecraft.world.gen.structure.StructureComponent;

public class TFHollowTreePieces {

    @SuppressWarnings("unchecked")
    public static void registerPieces() {
        MapGenStructureIO.func_143031_a(ComponentTFHollowTreeLargeBranch.class, "TFHTLB");
        MapGenStructureIO.func_143031_a(ComponentTFHollowTreeMedBranch.class, "TFHTMB");
        MapGenStructureIO.func_143031_a(ComponentTFHollowTreeSmallBranch.class, "TFHTSB");
        MapGenStructureIO.func_143031_a(ComponentTFHollowTreeTrunk.class, "TFHTTr");
        MapGenStructureIO.func_143031_a(ComponentTFLeafSphere.class, "TFHTLS");
        MapGenStructureIO.func_143031_a(ComponentTFHollowTreeRoot.class, "TFHTRo");
        // This was of a broken type, but removing it breaks worldgen on old worlds with weird crashes
        MapGenStructureIO.func_143031_a(
                (Class<? extends StructureComponent>) (Class<?>) StructureTFHollowTreeStart.class,
                "TFHTLSt");
        MapGenStructureIO.func_143031_a(ComponentTFHollowTreeLeafDungeon.class, "TFHTLD");

    }
}
