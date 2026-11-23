package twilightforest.structures.minotaurmaze;

import java.util.List;
import java.util.Random;

import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;

import twilightforest.block.TFBlocks;
import twilightforest.structures.StructureTFComponent;

public class ComponentTFMazeUpperEntrance extends StructureTFComponent {

    public ComponentTFMazeUpperEntrance() {
        super();
    }

    public ComponentTFMazeUpperEntrance(int i, Random rand, int x, int y, int z) {
        super(i);
        this.coordBaseMode = rand.nextInt(4);

        this.boundingBox = new StructureBoundingBox(x, y, z, x + 15, y + 5, z + 15);
    }

    /**
     * Initiates construction of the Structure Component picked, at the current Location of StructGen
     */
    @Override
    public void buildComponent(StructureComponent structurecomponent, List list, Random random) {}

    @Override
    public boolean addComponentParts(World world, Random rand, StructureBoundingBox sbb) {

        // ceiling
        this.randomlyFillWithBlocks(world, sbb, rand, 0.7F, 0, 5, 0, 15, 5, 15, TFBlocks.mazestone, Blocks.air, true);

        this.fillWithMetadataBlocks(world, sbb, 0, 0, 0, 15, 0, 15, TFBlocks.mazestone, 6, Blocks.air, 0, false);
        this.fillWithMetadataBlocks(world, sbb, 0, 1, 0, 15, 1, 15, TFBlocks.mazestone, 3, Blocks.air, 0, true);
        this.fillWithMetadataBlocks(world, sbb, 0, 2, 0, 15, 3, 15, TFBlocks.mazestone, 1, Blocks.air, 0, true);
        this.fillWithMetadataBlocks(world, sbb, 0, 4, 0, 15, 4, 15, TFBlocks.mazestone, 3, Blocks.air, 0, true);
        this.randomlyFillWithBlocks(world, sbb, rand, 0.2F, 0, 0, 0, 15, 5, 15, Blocks.gravel, Blocks.air, true);

        // doorways
        fillWithBlocks(world, sbb, 6, 1, 0, 9, 4, 0, Blocks.fence, Blocks.air, false);
        fillWithAir(world, sbb, 7, 1, 0, 8, 3, 0);
        fillWithBlocks(world, sbb, 6, 1, 15, 9, 4, 15, Blocks.fence, Blocks.air, false);
        fillWithAir(world, sbb, 7, 1, 15, 8, 3, 15);
        fillWithBlocks(world, sbb, 0, 1, 6, 0, 4, 9, Blocks.fence, Blocks.air, false);
        fillWithAir(world, sbb, 0, 1, 7, 0, 3, 8);
        fillWithBlocks(world, sbb, 15, 1, 6, 15, 4, 9, Blocks.fence, Blocks.air, false);
        fillWithAir(world, sbb, 15, 1, 7, 15, 3, 8);

        // random holes
        // this.randomlyRareFillWithBlocks(world, sbb, 0, 1, 0, 15, 4, 15, 0, false);
        // this.randomlyRareFillWithBlocks(world, sbb, 0, 3, 0, 15, 4, 15, 0, true);
        // this.randomlyRareFillWithBlocks(world, sbb, 0, 4, 0, 15, 4, 15, 0, true);
        this.fillWithAir(world, sbb, 1, 1, 1, 14, 4, 14);

        // entrance pit
        this.fillWithMetadataBlocks(world, sbb, 5, 1, 5, 10, 1, 10, TFBlocks.mazestone, 3, Blocks.air, 0, false);
        this.fillWithMetadataBlocks(world, sbb, 5, 4, 5, 10, 4, 10, TFBlocks.mazestone, 3, Blocks.air, 0, false);
        this.randomlyFillWithBlocks(world, sbb, rand, 0.7F, 5, 2, 5, 10, 3, 10, Blocks.iron_bars, Blocks.air, false);
        // this.fillWithMetadataBlocks(world, sbb, 5, 2, 5, 10, 3, 10, Blocks.iron_bars, 0, Blocks.air, 0, false);

        this.fillWithAir(world, sbb, 6, 0, 6, 9, 4, 9);

        // int var8 = this.getXWithOffset(0, 0);
        // int var9 = this.getYWithOffset(0);
        // int var10 = this.getZWithOffset(0, 0);
        //
        // System.out.println("Drawing entrance at " + var8 + ", " + var9 + ", " + var10);

        return true;
    }

}
