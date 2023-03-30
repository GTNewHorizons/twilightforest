package twilightforest.item;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Facing;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

import twilightforest.block.BlockTFPlant;
import twilightforest.block.TFBlocks;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemBlockTFPlant extends ItemBlock {

    public ItemBlockTFPlant(Block block) {
        super(block);
        setHasSubtypes(true);
        setMaxDamage(0);
    }

    /**
     * Gets an icon index based on an item's damage value
     */
    @Override
    public IIcon getIconFromDamage(int par1) {
        return TFBlocks.plant.getIcon(2, par1);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public int getColorFromItemStack(ItemStack par1ItemStack, int par2) {
        return TFBlocks.plant.getRenderColor(par1ItemStack.getItemDamage());
    }

    @Override
    public String getUnlocalizedName(ItemStack itemstack) {
        int meta = itemstack.getItemDamage();
        return super.getUnlocalizedName() + "." + meta;
    }

    /**
     * Returns the metadata of the block which this Item (ItemBlock) can place
     */
    @Override
    public int getMetadata(int i) {
        return i;
    }

    @SideOnly(Side.CLIENT)

    /**
     * Returns true if the given ItemBlock can be placed on the given side of the given block position.
     */
    @Override
    public boolean func_150936_a(World par1World, int x, int y, int z, int direction, EntityPlayer par6EntityPlayer,
            ItemStack par7ItemStack) {
        int meta = par7ItemStack.getItemDamage();

        if ((meta == BlockTFPlant.META_ROOT_STRAND || meta == BlockTFPlant.META_TORCHBERRY) && direction == 0
                && BlockTFPlant.canPlaceRootBelow(par1World, x, y, z)) {
            return true;
        } else {
            return super.func_150936_a(par1World, x, y, z, direction, par6EntityPlayer, par7ItemStack);
        }
    }

    /**
     * Callback for item usage. If the item does something special on right clicking, he will have one of those. Return
     * True if something happen and false if it don't. This is for ITEMS, not BLOCKS !
     */
    @Override
    public boolean onItemUse(ItemStack itemStack, EntityPlayer player, World world, int x, int y, int z, int direction,
            float par8, float par9, float par10) {
        int meta = itemStack.getItemDamage();

        if (meta == BlockTFPlant.META_ROOT_STRAND || meta == BlockTFPlant.META_TORCHBERRY) {
            // hanging
            return onItemUseRoot(itemStack, player, world, x, y, z, direction);
        } else {
            // regular flower type behavior
            return super.onItemUse(itemStack, player, world, x, y, z, direction, par8, par9, par10);
        }
    }

    /**
     * Callback for item usage. If the item does something special on right clicking, he will have one of those. Return
     * True if something happen and false if it don't. This is for ITEMS, not BLOCKS !
     */
    public boolean onItemUseRoot(ItemStack itemStack, EntityPlayer player, World world, int x, int y, int z,
            int direction) {
        Block blockThereId = world.getBlock(x, y, z);

        if (blockThereId == Blocks.snow) {
            direction = 1;
        } else if (!blockThereId.getMaterial().isReplaceable()) {
            // int var11 = par3World.getBlockId(par4, par5, par6);
            x += Facing.offsetsXForSide[direction];
            y += Facing.offsetsYForSide[direction];
            z += Facing.offsetsZForSide[direction];
        }

        if (!player.canPlayerEdit(x, y, z, direction, itemStack)) {
            return false;
        } else if (itemStack.stackSize == 0) {
            return false;
        } else {
            if (BlockTFPlant.canPlaceRootBelow(world, x, y + 1, z)) {
                Block plantBlock = TFBlocks.plant;

                if (world
                        .setBlock(x, y, z, plantBlock, itemStack.getItem().getMetadata(itemStack.getItemDamage()), 3)) {
                    if (world.getBlock(x, y, z) == plantBlock) {
                        // plantBlock.onBlockPlaced(world, x, y, z, direction);
                        plantBlock.onBlockPlacedBy(world, x, y, z, player, itemStack);
                    }

                    world.playSoundEffect(
                            x + 0.5F,
                            y + 0.5F,
                            z + 0.5F,
                            plantBlock.stepSound.getBreakSound(),
                            (plantBlock.stepSound.getVolume() + 1.0F) / 2.0F,
                            plantBlock.stepSound.getPitch() * 0.8F);
                    --itemStack.stackSize;
                }
            }

            return true;
        }
    }

}
