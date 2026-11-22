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
        if (this.adjacentChestChecked) {
            return;
        }

        this.adjacentChestChecked = true;
        // preserve the vanilla adjacency lookup and neighbour notification order
        this.adjacentChestZNeg = null;
        this.adjacentChestXPos = null;
        this.adjacentChestXNeg = null;
        this.adjacentChestZPos = null;

        this.adjacentChestXNeg = getAdjacentChest(xCoord - 1, yCoord, zCoord);
        this.adjacentChestXPos = getAdjacentChest(xCoord + 1, yCoord, zCoord);
        this.adjacentChestZNeg = getAdjacentChest(xCoord, yCoord, zCoord - 1);
        this.adjacentChestZPos = getAdjacentChest(xCoord, yCoord, zCoord + 1);

        if (this.adjacentChestZNeg instanceof TileEntityTFChest chest) {
            chest.func_145978_a(this, 0);
        }
        if (this.adjacentChestZPos instanceof TileEntityTFChest chest) {
            chest.func_145978_a(this, 2);
        }
        if (this.adjacentChestXPos instanceof TileEntityTFChest chest) {
            chest.func_145978_a(this, 1);
        }
        if (this.adjacentChestXNeg instanceof TileEntityTFChest chest) {
            chest.func_145978_a(this, 3);
        }
    }

    private TileEntityTFChest getAdjacentChest(int x, int y, int z) {
        if (worldObj == null) {
            return null;
        }

        Block adjacentBlock = worldObj.getBlock(x, y, z);
        Block block = getBlockType();
        if (!(adjacentBlock instanceof BlockTFChest) || !(block instanceof BlockTFChest)
                || ((BlockTFChest) adjacentBlock).getWoodType() != ((BlockTFChest) block).getWoodType()) {
            return null;
        }

        return worldObj.getTileEntity(x, y, z) instanceof TileEntityTFChest chest ? chest : null;
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
