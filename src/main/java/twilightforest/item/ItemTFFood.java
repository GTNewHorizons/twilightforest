package twilightforest.item;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import twilightforest.TwilightForestMod;
import twilightforest.block.TFBlocks;

public class ItemTFFood extends ItemFood {

    protected Item looksLike;
    protected boolean isSoup;

    /**
     * Soup constructor
     */
    public ItemTFFood(int par2) {
        super(par2, false);
        this.setCreativeTab(TFItems.creativeTab);
        this.setSoup(true);
    }

    public ItemTFFood(int par2, float par3, boolean par4) {
        super(par2, par3, par4);
        this.setCreativeTab(TFItems.creativeTab);
    }

    public Item getLooksLike() {
        return looksLike;
    }

    /**
     * Set this food to look like the specified item
     */
    public ItemTFFood setLooksLike(Item itemLike) {
        this.looksLike = itemLike;
        return this;
    }

    /**
     * Gets an icon index based on an item's damage value and the given render pass
     */
    public IIcon getIconFromDamage(int par1) {
        if (this.looksLike != null) {
            return this.looksLike.getIconFromDamage(0);
        } else {
            return super.getIconFromDamage(par1);
        }
    }

    /**
     * Properly register icon source
     */
    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister par1IconRegister) {
        // only try to load if we don't have a lookalike
        if (this.looksLike == null) {
            this.itemIcon = par1IconRegister
                    .registerIcon(TwilightForestMod.ID + ":" + this.getUnlocalizedName().substring(5));
        }
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side,
            float hitX, float hitY, float hitZ) {
        if (!player.isSneaking() || stack.getItem() != TFItems.experiment115) {
            return super.onItemUse(stack, player, world, x, y, z, side, hitX, hitY, hitZ);
        }
        if (stack.stackSize <= 0) {
            return false;
        }

        boolean stacking = world.getBlock(x, y, z) == TFBlocks.experiment115;
        int placeY = stacking ? y : y + 1;
        if ((!stacking && (side != 1 || !world.isAirBlock(x, placeY, z)))
                || (stacking && world.getBlockMetadata(x, y, z) >= 8)
                || !world.canMineBlock(player, x, placeY, z)
                || !player.canPlayerEdit(x, placeY, z, side, stack)) {
            return false;
        }
        if (world.isRemote) {
            return true;
        }

        boolean placed = stacking ? world.setBlockMetadataWithNotify(x, y, z, world.getBlockMetadata(x, y, z) + 1, 2)
                : world.setBlock(x, placeY, z, TFBlocks.experiment115, 1, 2);
        if (!placed) {
            return false;
        }
        world.notifyBlocksOfNeighborChange(x, placeY, z, world.getBlock(x, placeY, z));
        if (!player.capabilities.isCreativeMode) {
            --stack.stackSize;
        }
        return true;
    }

    public boolean isSoup() {
        return isSoup;
    }

    public void setSoup(boolean isSoup) {
        this.isSoup = isSoup;
        this.setMaxStackSize(isSoup ? 1 : 64);
    }

    /**
     * Give a bowl back if we are soup
     */
    @Override
    public ItemStack onEaten(ItemStack stack, World world, EntityPlayer player) {
        ItemStack eaten = super.onEaten(stack, world, player);
        return this.isSoup ? new ItemStack(Items.bowl) : eaten;
    }

}
