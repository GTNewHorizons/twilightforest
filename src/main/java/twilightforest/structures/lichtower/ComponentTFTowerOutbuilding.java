package twilightforest.structures.lichtower;

import java.util.List;
import java.util.Random;

import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;

public class ComponentTFTowerOutbuilding extends ComponentTFTowerWing {

    public ComponentTFTowerOutbuilding() {
        super();
        // TODO Auto-generated constructor stub
    }

    protected ComponentTFTowerOutbuilding(int i, int x, int y, int z, int pSize, int pHeight, int direction) {
        super(i, x, y, z, pSize, pHeight, direction);
        // TODO Auto-generated constructor stub
    }

    /**
     * NO BEARDS!
     */
    @Override
    public void makeABeard(StructureComponent parent, List<StructureComponent> list, Random rand) {}

    /**
     * Outbuildings should not make new wings close to the ground.
     */
    @Override
    public boolean makeTowerWing(List<StructureComponent> list, Random rand, int index, int x, int y, int z,
            int wingSize, int wingHeight, int direction) {
        return y > 7 && super.makeTowerWing(list, rand, index, x, y, z, wingSize, wingHeight, direction);
    }

    @Override
    public boolean addComponentParts(World world, Random rand, StructureBoundingBox sbb) {
        for (int x = 0; x < size; x++) {
            for (int z = 0; z < size; z++) {
                func_151554_b(world, Blocks.cobblestone, 0, x, -1, z, sbb);
            }
        }
        return super.addComponentParts(world, rand, sbb);
    }
}
