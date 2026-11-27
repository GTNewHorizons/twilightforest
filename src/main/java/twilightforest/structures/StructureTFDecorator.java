package twilightforest.structures;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.gen.structure.StructureComponent;

import twilightforest.structures.darktower.StructureDecoratorDarkTower;
import twilightforest.structures.icetower.StructureDecoratorIceTower;
import twilightforest.structures.mushroomtower.StructureDecoratorMushroomTower;
import twilightforest.structures.stronghold.StructureTFDecoratorStronghold;

/**
 * Stores information about what blocks to use in constructing this structure
 *
 * @author Ben
 *
 */
public class StructureTFDecorator {

    private static final String DECO_DARK_TOWER = "DecoDarkTower";
    private static final String DECO_ICE_TOWER = "DecoIceTower";
    private static final String DECO_MUSHROOM_TOWER = "DecoMushroomTower";
    private static final String DECO_STRONGHOLD = "DecoStronghold";
    private static final String DECO_CASTLE = "DecoCastle";

    public Block blockID = Blocks.stone;
    public int blockMeta;

    public Block accentID = Blocks.cobblestone;
    public int accentMeta;

    public Block stairID;
    public int stairMeta; // probably not useful

    public Block fenceID;
    public int fenceMeta;

    public Block pillarID;
    public int pillarMeta;

    public Block platformID;
    public int platformMeta;

    public Block floorID;
    public int floorMeta;

    public Block roofID;
    public int roofMeta;

    public StructureComponent.BlockSelector randomBlocks = new StructureTFStrongholdStones();

    public static String getDecoString(StructureTFDecorator deco) {
        return switch (deco) {
            case StructureDecoratorDarkTower tower -> DECO_DARK_TOWER;
            case StructureDecoratorIceTower tower -> DECO_ICE_TOWER;
            case StructureDecoratorMushroomTower tower -> DECO_MUSHROOM_TOWER;
            case StructureTFDecoratorStronghold stronghold -> DECO_STRONGHOLD;
            case StructureTFDecoratorCastle castle -> DECO_CASTLE;
            case null, default -> "";
        };
    }

    public static StructureTFDecorator getDecoFor(String decoString) {
        if (decoString == null) return new StructureTFDecorator();
        return switch (decoString) {
            case DECO_DARK_TOWER -> new StructureDecoratorDarkTower();
            case DECO_ICE_TOWER -> new StructureDecoratorIceTower();
            case DECO_MUSHROOM_TOWER -> new StructureDecoratorMushroomTower();
            case DECO_STRONGHOLD -> new StructureTFDecoratorStronghold();
            case DECO_CASTLE -> new StructureTFDecoratorCastle();
            default -> new StructureTFDecorator();
        };
    }
}
