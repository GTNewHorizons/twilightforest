package twilightforest.structures.stronghold;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;

import twilightforest.block.TFBlocks;

public class StructureTFStrongholdShield extends StructureTFStrongholdComponent {

    public StructureTFStrongholdShield() {
        super();
    }

    public StructureTFStrongholdShield(int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
        super(0, 0, minX, minY, minZ);
        this.boundingBox = new StructureBoundingBox(minX, minY, minZ, maxX, maxY, maxZ);
        this.spawnListIndex = -1;
    }

    @Override
    public StructureBoundingBox generateBoundingBox(int facing, int x, int y, int z) {
        return null;
    }

    @Override
    public boolean addComponentParts(World world, Random random, StructureBoundingBox sbb) {
        Block shieldBlock = TFBlocks.shield;
        // structureBoundingBox sizes count blocks, fill coordinates require inclusive endpoints
        int maxX = boundingBox.getXSize() - 1;
        int maxY = boundingBox.getYSize() - 1;
        int maxZ = boundingBox.getZSize() - 1;

        // Facing.facings

        // +x
        this.fillWithMetadataBlocks(world, sbb, maxX, 0, 0, maxX, maxY, maxZ, shieldBlock, 4, shieldBlock, 4, false);
        // -x
        this.fillWithMetadataBlocks(world, sbb, 0, 0, 0, 0, maxY, maxZ, shieldBlock, 5, shieldBlock, 5, false);
        // +z
        this.fillWithMetadataBlocks(world, sbb, 0, 0, maxZ, maxX, maxY, maxZ, shieldBlock, 2, shieldBlock, 2, false);
        // -z
        this.fillWithMetadataBlocks(world, sbb, 0, 0, 0, maxX, maxY, 0, shieldBlock, 3, shieldBlock, 3, false);
        // bottom (negative Y face)
        this.fillWithMetadataBlocks(world, sbb, 0, 0, 0, maxX, 0, maxZ, shieldBlock, 1, shieldBlock, 1, false);
        // top (positive Y face)
        this.fillWithMetadataBlocks(world, sbb, 0, maxY, 0, maxX, maxY, maxZ, shieldBlock, 0, shieldBlock, 0, false);

        return true;
    }

}
