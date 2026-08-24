package twilightforest.tileentity;

import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntityChest;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import twilightforest.block.BlockTFChest;
import twilightforest.block.BlockTFChest.WoodType;

public class TileEntityTFChest extends TileEntityChest {

    public WoodType cachedMaterial = WoodType.NULL;

    public TileEntityTFChest() {
        super();
    }

    @SideOnly(Side.CLIENT)
    public TileEntityTFChest(WoodType material) {
        super();
        this.cachedMaterial = material;
    }

    @Override
    protected boolean func_145977_a(int x, int y, int z) {
        if (this.worldObj == null) return false;
        Block block = this.worldObj.getBlock(x, y, z);
        return block instanceof BlockTFChest otherBlock && getBlockType() instanceof BlockTFChest thisBlock
                && otherBlock.getWoodType() == thisBlock.getWoodType();
    }
}
