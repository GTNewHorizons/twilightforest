package twilightforest.structures.courtyard;

import java.util.Random;

import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;

import twilightforest.block.BlockTFNagastone2.Facing;
import twilightforest.structures.StructureTFComponent;
import twilightforest.tileentity.TileEntityTFNagastoneEtched;
import twilightforest.tileentity.TileEntityTFNagastoneEtched.Direction;

public class ComponentTFNagaCourtyardRotatedAbstract extends StructureTFComponent {

    protected int horizontalColumnsOrient4;
    protected int horizontalColumnsOrient8;

    protected int rotatedStairs0;
    protected int rotatedStairs1;
    protected int rotatedStairs2;
    protected int rotatedStairs3;
    protected int rotatedStairs4;
    protected int rotatedStairs5;
    protected int rotatedStairs6;
    protected int rotatedStairs7;

    protected TileEntityTFNagastoneEtched.Direction EtchedNagastoneNorth;
    protected TileEntityTFNagastoneEtched.Direction EtchedNagastoneSouth;
    protected TileEntityTFNagastoneEtched.Direction EtchedNagastoneWest;
    protected TileEntityTFNagastoneEtched.Direction EtchedNagastoneEast;

    protected Facing NagastoneNorth;
    protected Facing NagastoneSouth;
    protected Facing NagastoneWest;
    protected Facing NagastoneEast;

    public ComponentTFNagaCourtyardRotatedAbstract() {
        super();
    }

    public ComponentTFNagaCourtyardRotatedAbstract(int i, int x, int y, int z, int rotation) {
        super(i);
        this.coordBaseMode = rotation;

        switch (coordBaseMode) {
            default:
            case 0:
                horizontalColumnsOrient4 = 8;
                horizontalColumnsOrient8 = 4;

                rotatedStairs0 = 3;
                rotatedStairs1 = 2;
                rotatedStairs2 = 0;
                rotatedStairs3 = 1;
                rotatedStairs4 = 7;
                rotatedStairs5 = 6;
                rotatedStairs6 = 4;
                rotatedStairs7 = 5;

                EtchedNagastoneNorth = Direction.EAST;
                EtchedNagastoneSouth = Direction.WEST;
                EtchedNagastoneWest = Direction.NORTH;
                EtchedNagastoneEast = Direction.SOUTH;

                NagastoneNorth = Facing.WEST;
                NagastoneSouth = Facing.EAST;
                NagastoneWest = Facing.SOUTH;
                NagastoneEast = Facing.NORTH;
                break;
            case 1:
                horizontalColumnsOrient4 = 4;
                horizontalColumnsOrient8 = 8;

                rotatedStairs0 = 0;
                rotatedStairs1 = 1;
                rotatedStairs2 = 2;
                rotatedStairs3 = 3;
                rotatedStairs4 = 4;
                rotatedStairs5 = 5;
                rotatedStairs6 = 6;
                rotatedStairs7 = 7;

                EtchedNagastoneNorth = Direction.NORTH;
                EtchedNagastoneSouth = Direction.SOUTH;
                EtchedNagastoneWest = Direction.WEST;
                EtchedNagastoneEast = Direction.EAST;

                NagastoneNorth = Facing.NORTH;
                NagastoneSouth = Facing.SOUTH;
                NagastoneWest = Facing.WEST;
                NagastoneEast = Facing.EAST;
                break;
            case 2:
                horizontalColumnsOrient4 = 8;
                horizontalColumnsOrient8 = 4;

                rotatedStairs0 = 2;
                rotatedStairs1 = 3;
                rotatedStairs2 = 1;
                rotatedStairs3 = 0;
                rotatedStairs4 = 6;
                rotatedStairs5 = 7;
                rotatedStairs6 = 5;
                rotatedStairs7 = 4;

                EtchedNagastoneNorth = Direction.WEST;
                EtchedNagastoneSouth = Direction.EAST;
                EtchedNagastoneWest = Direction.SOUTH;
                EtchedNagastoneEast = Direction.NORTH;

                NagastoneNorth = Facing.EAST;
                NagastoneSouth = Facing.WEST;
                NagastoneWest = Facing.NORTH;
                NagastoneEast = Facing.SOUTH;
                break;
            case 3:
                horizontalColumnsOrient4 = 4;
                horizontalColumnsOrient8 = 8;

                rotatedStairs0 = 1;
                rotatedStairs1 = 0;
                rotatedStairs2 = 3;
                rotatedStairs3 = 2;
                rotatedStairs4 = 5;
                rotatedStairs5 = 4;
                rotatedStairs6 = 7;
                rotatedStairs7 = 6;

                EtchedNagastoneNorth = Direction.SOUTH;
                EtchedNagastoneSouth = Direction.NORTH;
                EtchedNagastoneWest = Direction.EAST;
                EtchedNagastoneEast = Direction.WEST;

                NagastoneNorth = Facing.SOUTH;
                NagastoneSouth = Facing.NORTH;
                NagastoneWest = Facing.EAST;
                NagastoneEast = Facing.WEST;
                break;
        }
    }

    @Override
    public boolean addComponentParts(World p_74875_1_, Random p_74875_2_, StructureBoundingBox p_74875_3_) {
        // TODO Auto-generated method stub
        return false;
    }
}
