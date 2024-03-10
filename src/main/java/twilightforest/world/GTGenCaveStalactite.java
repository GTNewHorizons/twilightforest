package twilightforest.world;

import gregtech.common.blocks.GT_TileEntity_Ores;
import net.minecraft.block.Block;
import net.minecraft.world.World;

public class GTGenCaveStalactite extends TFGenCaveStalactite {
	
	boolean isSmallOre;
    boolean air;

    /**
     * Initializes a stalactite builder
     */
    public GTGenCaveStalactite(Block blockType, int meta, float size, int maxLength, int minHeight, boolean isSmallOre, boolean air) {
        super(blockType, meta, size, maxLength, minHeight);
        this.isSmallOre = isSmallOre;
        this.air = air;
    }
    
    @Override
    public void setBlock(World world, int x, int y, int z) {
    	GT_TileEntity_Ores.setOreBlock(world, x, y, z, blockMeta, isSmallOre, air);
    }

}
