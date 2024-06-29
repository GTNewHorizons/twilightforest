package twilightforest.world;

import net.minecraft.block.Block;
import net.minecraft.world.World;

import gregtech.api.GregTech_API;
import gregtech.common.blocks.GT_TileEntity_Ores;

public class GTGenCaveStalactite extends TFGenCaveStalactite {

    boolean isSmallOre;
    boolean air;

    /**
     * Initializes a stalactite builder
     */
    public GTGenCaveStalactite(int meta, float size, int maxLength, int minHeight, boolean isSmallOre, boolean air) {
        super(GregTech_API.sBlockOres1, meta, size, maxLength, minHeight);
        this.isSmallOre = isSmallOre;
        this.air = air;
    }

    @Override
    protected void setBlockAndMetadata(World world, int x, int y, int z, Block block, int meta) {
        GT_TileEntity_Ores.setOreBlock(world, x, y, z, blockMeta, isSmallOre, air);
    }

}
