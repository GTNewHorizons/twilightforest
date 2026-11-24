package twilightforest.item;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import twilightforest.TwilightForestMod;
import twilightforest.block.BlockTFDoor;
import twilightforest.block.TFBlocks;

public class ItemTFDoor extends Item {

    private final int doorMaterial;

    public ItemTFDoor(int material) {
        this.doorMaterial = material;
        this.maxStackSize = 1;
        this.setCreativeTab(TFItems.creativeTab);
    }

    /**
     * Callback for item usage. If the item does something special on right clicking, he will have one of those. Return
     * True if something happen and false if it don't. This is for ITEMS, not BLOCKS
     */
    public boolean onItemUse(ItemStack p_77648_1_, EntityPlayer p_77648_2_, World p_77648_3_, int p_77648_4_,
            int p_77648_5_, int p_77648_6_, int p_77648_7_, float p_77648_8_, float p_77648_9_, float p_77648_10_) {
        if (p_77648_7_ != 1 || p_77648_1_.stackSize <= 0) {
            return false;
        } else {
            ++p_77648_5_;
            Block block;

            switch (this.doorMaterial) {
                default:
                    block = TFBlocks.doorTwilight;
                    break;
                case 1:
                    block = TFBlocks.doorCanopy;
                    break;
                case 2:
                    block = TFBlocks.doorMangrove;
                    break;
                case 3:
                    block = TFBlocks.doorDarkwood;
                    break;
                case 4:
                    block = TFBlocks.doorTime;
                    break;
                case 5:
                    block = TFBlocks.doorTrans;
                    break;
                case 6:
                    block = TFBlocks.doorMine;
                    break;
                case 7:
                    block = TFBlocks.doorSort;
                    break;
            }

            if (p_77648_2_.canPlayerEdit(p_77648_4_, p_77648_5_, p_77648_6_, p_77648_7_, p_77648_1_)
                    && p_77648_2_.canPlayerEdit(p_77648_4_, p_77648_5_ + 1, p_77648_6_, p_77648_7_, p_77648_1_)) {
                if (!block.canPlaceBlockAt(p_77648_3_, p_77648_4_, p_77648_5_, p_77648_6_)) {
                    return false;
                } else {
                    int i1 = MathHelper
                            .floor_double((double) ((p_77648_2_.rotationYaw + 180.0F) * 4.0F / 360.0F) - 0.5D) & 3;
                    if (!placeDoorBlockChecked(p_77648_3_, p_77648_4_, p_77648_5_, p_77648_6_, i1, block)) {
                        return false;
                    }
                    if (!p_77648_2_.capabilities.isCreativeMode) {
                        --p_77648_1_.stackSize;
                    }
                    return true;
                }
            } else {
                return false;
            }
        }
    }

    public static void placeDoorBlock(World p_150924_0_, int p_150924_1_, int p_150924_2_, int p_150924_3_,
            int p_150924_4_, Block p_150924_5_) {
        placeDoorBlockChecked(p_150924_0_, p_150924_1_, p_150924_2_, p_150924_3_, p_150924_4_, p_150924_5_);
    }

    private static boolean placeDoorBlockChecked(World p_150924_0_, int p_150924_1_, int p_150924_2_, int p_150924_3_,
            int p_150924_4_, Block p_150924_5_) {
        byte b0 = 0;
        byte b1 = 0;

        if (p_150924_4_ == 0) {
            b1 = 1;
        }

        if (p_150924_4_ == 1) {
            b0 = -1;
        }

        if (p_150924_4_ == 2) {
            b1 = -1;
        }

        if (p_150924_4_ == 3) {
            b0 = 1;
        }

        int i1 = (p_150924_0_.getBlock(p_150924_1_ - b0, p_150924_2_, p_150924_3_ - b1).isNormalCube() ? 1 : 0)
                + (p_150924_0_.getBlock(p_150924_1_ - b0, p_150924_2_ + 1, p_150924_3_ - b1).isNormalCube() ? 1 : 0);
        int j1 = (p_150924_0_.getBlock(p_150924_1_ + b0, p_150924_2_, p_150924_3_ + b1).isNormalCube() ? 1 : 0)
                + (p_150924_0_.getBlock(p_150924_1_ + b0, p_150924_2_ + 1, p_150924_3_ + b1).isNormalCube() ? 1 : 0);
        boolean flag = p_150924_0_.getBlock(p_150924_1_ - b0, p_150924_2_, p_150924_3_ - b1) == p_150924_5_
                || p_150924_0_.getBlock(p_150924_1_ - b0, p_150924_2_ + 1, p_150924_3_ - b1) == p_150924_5_;
        boolean flag1 = p_150924_0_.getBlock(p_150924_1_ + b0, p_150924_2_, p_150924_3_ + b1) == p_150924_5_
                || p_150924_0_.getBlock(p_150924_1_ + b0, p_150924_2_ + 1, p_150924_3_ + b1) == p_150924_5_;
        boolean flag2 = false;

        if (flag && !flag1) {
            flag2 = true;
        } else if (j1 > i1) {
            flag2 = true;
        }

        // keep both halves consistent if a placement fails after the lower half was written
        Block previousLower = p_150924_0_.getBlock(p_150924_1_, p_150924_2_, p_150924_3_);
        int previousMeta = p_150924_0_.getBlockMetadata(p_150924_1_, p_150924_2_, p_150924_3_);
        if (!p_150924_0_.setBlock(p_150924_1_, p_150924_2_, p_150924_3_, p_150924_5_, p_150924_4_, 2)) {
            return false;
        }
        if (!p_150924_0_.setBlock(p_150924_1_, p_150924_2_ + 1, p_150924_3_, p_150924_5_, 8 | (flag2 ? 1 : 0), 2)) {
            p_150924_0_.setBlock(p_150924_1_, p_150924_2_, p_150924_3_, previousLower, previousMeta, 3);
            return false;
        }
        p_150924_0_.notifyBlocksOfNeighborChange(p_150924_1_, p_150924_2_, p_150924_3_, p_150924_5_);
        p_150924_0_.notifyBlocksOfNeighborChange(p_150924_1_, p_150924_2_ + 1, p_150924_3_, p_150924_5_);
        return true;
    }

    /**
     * returns a list of items with the same ID, but different meta (eg: dye returns 16 items)
     */
    @Override
    public void getSubItems(Item par1, CreativeTabs par2CreativeTabs, List<ItemStack> par3List) {
        ItemStack istack = new ItemStack(par1, 1, 0);
        par3List.add(istack);
    }

    /**
     * Properly register icon source
     */
    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister par1IconRegister) {
        this.itemIcon = par1IconRegister
                .registerIcon(TwilightForestMod.ID + ":" + BlockTFDoor.names[doorMaterial] + "_door");
    }
}
