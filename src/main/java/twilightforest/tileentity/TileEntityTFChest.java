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

    /**
     * Performs the check for adjacent chests to determine if this chest is double or not.
     */
    @Override
    public void checkForAdjacentChests() {
        if (!this.adjacentChestChecked) {
            this.adjacentChestChecked = true;
            this.adjacentChestZNeg = null;
            this.adjacentChestXPos = null;
            this.adjacentChestXNeg = null;
            this.adjacentChestZPos = null;

            if (this.func_145977_a(this.xCoord - 1, this.yCoord, this.zCoord)) {
                this.adjacentChestXNeg = (TileEntityTFChest) this.worldObj
                        .getTileEntity(this.xCoord - 1, this.yCoord, this.zCoord);
            }

            if (this.func_145977_a(this.xCoord + 1, this.yCoord, this.zCoord)) {
                this.adjacentChestXPos = (TileEntityTFChest) this.worldObj
                        .getTileEntity(this.xCoord + 1, this.yCoord, this.zCoord);
            }

            if (this.func_145977_a(this.xCoord, this.yCoord, this.zCoord - 1)) {
                this.adjacentChestZNeg = (TileEntityTFChest) this.worldObj
                        .getTileEntity(this.xCoord, this.yCoord, this.zCoord - 1);
            }

            if (this.func_145977_a(this.xCoord, this.yCoord, this.zCoord + 1)) {
                this.adjacentChestZPos = (TileEntityTFChest) this.worldObj
                        .getTileEntity(this.xCoord, this.yCoord, this.zCoord + 1);
            }

            if (this.adjacentChestZNeg instanceof TileEntityTFChest) {
                ((TileEntityTFChest) this.adjacentChestZNeg).func_145978_a(this, 0);
            }

            if (this.adjacentChestZPos instanceof TileEntityTFChest) {
                ((TileEntityTFChest) this.adjacentChestZPos).func_145978_a(this, 2);
            }

            if (this.adjacentChestXPos instanceof TileEntityTFChest) {
                ((TileEntityTFChest) this.adjacentChestXPos).func_145978_a(this, 1);
            }

            if (this.adjacentChestXNeg instanceof TileEntityTFChest) {
                ((TileEntityTFChest) this.adjacentChestXNeg).func_145978_a(this, 3);
            }
        }
    }

    private boolean func_145977_a(int p_145977_1_, int p_145977_2_, int p_145977_3_) {
        if (this.worldObj == null) {
            return false;
        } else {
            Block block = this.worldObj.getBlock(p_145977_1_, p_145977_2_, p_145977_3_);
            Block thisBlock = this.getBlockType();
            return block instanceof BlockTFChest && thisBlock instanceof BlockTFChest
                    && ((BlockTFChest) block).getWoodType() == ((BlockTFChest) thisBlock).getWoodType();
        }
    }

    private void func_145978_a(TileEntityTFChest p_145978_1_, int p_145978_2_) {
        if (p_145978_1_.isInvalid()) {
            this.adjacentChestChecked = false;
        } else if (this.adjacentChestChecked) {
            switch (p_145978_2_) {
                case 0:
                    if (this.adjacentChestZPos != p_145978_1_) {
                        this.adjacentChestChecked = false;
                    }

                    break;
                case 1:
                    if (this.adjacentChestXNeg != p_145978_1_) {
                        this.adjacentChestChecked = false;
                    }

                    break;
                case 2:
                    if (this.adjacentChestZNeg != p_145978_1_) {
                        this.adjacentChestChecked = false;
                    }

                    break;
                case 3:
                    if (this.adjacentChestXPos != p_145978_1_) {
                        this.adjacentChestChecked = false;
                    }
            }
        }
    }

}
