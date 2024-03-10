package twilightforest.world;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import gregtech.common.blocks.GT_Block_Ores_Abstract;
import gregtech.common.blocks.GT_TileEntity_Ores;

public class GTGenCaveStalactite extends TFGenCaveStalactite {

    /**
     * Initializes a stalactite builder
     */
    public GTGenCaveStalactite(Block blockType, int meta, float size, int maxLength, int minHeight) {
        super(blockType, meta, size, maxLength, minHeight);
    }

    @Override
    public boolean makeSpike(World world, Random random, int x, int y, int z, int maxLength) {

        int diameter = (int) (maxLength / 4.5); // diameter of the base

        // let's see...
        for (int dx = -diameter; dx <= diameter; dx++) {
            for (int dz = -diameter; dz <= diameter; dz++) {
                // determine how long this spike will be.
                int absx = Math.abs(dx);
                int absz = Math.abs(dz);
                int dist = (int) (Math.max(absx, absz) + (Math.min(absx, absz) * 0.5));
                int spikeLength = 0;

                if (dist == 0) {
                    spikeLength = maxLength;
                }

                if (dist > 0) {
                    spikeLength = random.nextInt((int) (maxLength / (dist + 0.25)));
                }

                int dir = hang ? -1 : 1;

                // check if we're generating over anything
                if (!world.getBlock(x + dx, y - dir, z + dz).getMaterial().isSolid()) {
                    spikeLength = 0;
                }

                for (int dy = 0; dy != (spikeLength * dir); dy += dir) {
                    world.setBlock(
                            x,
                            y,
                            z,
                            blockID,
                            GT_TileEntity_Ores.getHarvestData(
                                    (short) blockMeta,
                                    ((GT_Block_Ores_Abstract) blockID)
                                            .getBaseBlockHarvestLevel(blockMeta % 16000 / 1000)),
                            0);
                    TileEntity tTileEntity = world.getTileEntity(x, y, z);
                    if ((tTileEntity instanceof GT_TileEntity_Ores)) {
                        ((GT_TileEntity_Ores) tTileEntity).mMetaData = ((short) blockMeta);
                        ((GT_TileEntity_Ores) tTileEntity).mNatural = true;
                    }
                }
            }
        }

        return true;
    }

}
