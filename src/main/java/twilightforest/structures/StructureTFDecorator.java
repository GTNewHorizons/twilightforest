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
        if (deco instanceof StructureDecoratorDarkTower) {
            return DECO_DARK_TOWER;
        }
        if (deco instanceof StructureDecoratorIceTower) {
            return DECO_ICE_TOWER;
        }
        if (deco instanceof StructureDecoratorMushroomTower) {
            return DECO_MUSHROOM_TOWER;
        }
        if (deco instanceof StructureTFDecoratorStronghold) {
            return DECO_STRONGHOLD;
        }
        if (deco instanceof StructureTFDecoratorCastle) {
            return DECO_CASTLE;
        }

        return "";
    }

    public static StructureTFDecorator getDecoFor(String decoString) {
        switch (decoString) {
            case DECO_DARK_TOWER:
                return new StructureDecoratorDarkTower();
            case DECO_ICE_TOWER:
                return new StructureDecoratorIceTower();
            case DECO_MUSHROOM_TOWER:
                return new StructureDecoratorMushroomTower();
            case DECO_STRONGHOLD:
                return new StructureTFDecoratorStronghold();
            case DECO_CASTLE:
                return new StructureTFDecoratorCastle();
            default:
                return new StructureTFDecorator();
        }
    }
}
