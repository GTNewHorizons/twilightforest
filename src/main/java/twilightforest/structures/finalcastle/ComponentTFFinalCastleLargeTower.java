package twilightforest.structures.finalcastle;

import java.util.List;
import java.util.Random;

import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;

import twilightforest.block.TFBlocks;
import twilightforest.structures.StructureTFComponent;
import twilightforest.structures.lichtower.ComponentTFTowerWing;

public class ComponentTFFinalCastleLargeTower extends ComponentTFTowerWing {

    private static final int TOWER_SIZE = 13;
    private static final int TOWER_HEIGHT = 61;
    private static final int MIN_GLYPH_BRANCHES = 6;
    private static final int EXTRA_GLYPH_BRANCHES = 4;

    public ComponentTFFinalCastleLargeTower() {}

    public ComponentTFFinalCastleLargeTower(Random rand, int i, int x, int y, int z, int rotation) {
        super(i);
        this.setCoordBaseMode(rotation);
        this.size = TOWER_SIZE;
        this.height = TOWER_HEIGHT;
        this.boundingBox = StructureTFComponent.getComponentToAddBoundingBox(x, y, z, -6, 0, -6, 12, 60, 12, 0);
    }

    @Override
    public void buildComponent(StructureComponent parent, List<StructureComponent> list, Random rand) {
        if (parent != null && parent instanceof StructureTFComponent) {
            this.deco = ((StructureTFComponent) parent).deco;
        }
        // add crown
        ComponentTFFinalCastleRoof9Crenellated roof = new ComponentTFFinalCastleRoof9Crenellated(rand, 4, this);
        list.add(roof);
        roof.buildComponent(this, list, rand);
    }

    @Override
    public boolean addComponentParts(World world, Random rand, StructureBoundingBox sbb) {
        Random decoRNG = new Random(
                world.getSeed() + (this.boundingBox.minX * 321534781) ^ (this.boundingBox.minZ * 756839));

        fillWithRandomizedBlocks(world, sbb, 0, 0, 0, size - 1, height - 2, size - 1, false, rand, deco.randomBlocks);

        // add branching runes
        int numBranches = MIN_GLYPH_BRANCHES + decoRNG.nextInt(EXTRA_GLYPH_BRANCHES);
        for (int i = 0; i < numBranches; i++) {
            makeGlyphBranches(world, decoRNG, this.getGlyphMeta(), sbb);
        }

        // beard
        for (int i = 1; i < 4; i++) {
            fillWithRandomizedBlocks(
                    world,
                    sbb,
                    i,
                    0 - (i * 2),
                    i,
                    8 - i,
                    1 - (i * 2),
                    8 - i,
                    false,
                    rand,
                    deco.randomBlocks);
        }
        this.placeBlockAtCurrentPosition(world, deco.blockID, deco.blockMeta, 4, -7, 4, sbb);

        // door, first floor
        this.fillWithMetadataBlocks(
                world,
                sbb,
                0,
                1,
                1,
                0,
                4,
                3,
                TFBlocks.castleDoor,
                0,
                Blocks.air,
                this.getGlyphMeta(),
                false);

        this.placeSignAtCurrentPosition(world, 6, 1, 6, "Parkour area 1", "Unique monster?", sbb);

        return true;
    }

    public int getGlyphMeta() {
        return 0;
    }
}
