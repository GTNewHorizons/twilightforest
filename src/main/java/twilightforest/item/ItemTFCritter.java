package twilightforest.item;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import twilightforest.TwilightForestMod;
import twilightforest.block.BlockTFCritter;
import twilightforest.block.BlockTFFirefly;
import twilightforest.block.TFBlocks;

public class ItemTFCritter extends ItemArmor {

    private static final String[] critterTypes = new String[] { "firefly", "cicada", "moonworm" };
    private static final Block[] critterBlocks = new Block[] { TFBlocks.firefly, TFBlocks.cicada, TFBlocks.moonworm };
    public static final String[] critterTextures = new String[] { "firefly-tiny", "cicada-model", "moonworm" };
    public IIcon[] critterIcons;

    public ItemTFCritter() {
        super(TFItems.ARMOR_DECORATIVE, 0, 0);
        this.setCreativeTab(TFItems.creativeTab);
        this.setMaxDamage(0);
        this.setHasSubtypes(true);
        this.maxStackSize = 64;
    }

    /**
     * returns a list of items with the same ID, but different meta (eg: dye returns 16 items)
     */
    public void getSubItems(Item par1, CreativeTabs par2CreativeTabs, List<ItemStack> par3List) {
        for (int j = 0; j < critterTypes.length; ++j) {
            par3List.add(new ItemStack(par1, 1, j));
        }
    }

    /**
     * Callback for item usage. If the item does something special on right clicking, he will have one of those. Return
     * True if something happen and false if it don't. This is for ITEMS, not BLOCKS
     */
    public boolean onItemUse(ItemStack itemStack, EntityPlayer player, World world, int x, int y, int z, int direction,
            float par8, float par9, float par10) {
        if (!world.getBlock(x, y, z).getMaterial().isSolid()) {
            return false;
        } else {
            switch (direction) {
                default:
                case 0:
                    --y;
                    break;
                case 1:
                    ++y;
                    break;
                case 2:
                    --z;
                    break;
                case 3:
                    ++z;
                    break;
                case 4:
                    --x;
                    break;
                case 5:
                    ++x;
                    break;
            }

            if (!player.canPlayerEdit(x, y, z, direction, itemStack)) {
                return false;
            } else {
                BlockTFCritter block = new BlockTFFirefly();
                if (!block.canPlaceBlockAt(world, x, y, z)) {
                    return false;
                } else {
                    if (world.setBlock(
                            x,
                            y,
                            z,
                            critterBlocks[itemStack.getItemDamage() % critterTypes.length],
                            6 - direction,
                            3)) {
                        world.playSound(x, y, z, "mob.slime.big", 0.25F, 0.6F, true);
                        --itemStack.stackSize;
                        return true;
                    }
                    return false;
                }
            }
        }
    }

    /**
     * Returns the unlocalized name of this item. This version accepts an ItemStack so different stacks can have
     * different names based on their damage or NBT.
     */
    public String getUnlocalizedName(ItemStack par1ItemStack) {
        return critterBlocks[par1ItemStack.getItemDamage() % critterTypes.length].getUnlocalizedName();
    }

    /**
     * Properly register icon source
     */
    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister par1IconRegister) {
        this.critterIcons = new IIcon[critterTypes.length];

        for (int i = 0; i < critterTypes.length; ++i) {
            this.critterIcons[i] = critterBlocks[i].getIcon(3, 0);
        }
    }

    /**
     * Gets an icon index based on an item's damage value
     */
    @SideOnly(Side.CLIENT)
    public IIcon getIconFromDamage(int par1) {
        if (par1 < 0 || par1 >= critterTypes.length) {
            par1 = 0;
        }

        return this.critterIcons[par1];
    }

    /**
     * Returns 0 for /terrain.png, 1 for /gui/items.png
     */
    @Override
    @SideOnly(Side.CLIENT)
    public int getSpriteNumber() {
        return 0; // Since we're using a block texture, not an item one
    }

    /**
     * Called by RenderBiped and RenderPlayer to determine the armor texture that should be use for the currently
     * equiped item. This will only be called on instances of ItemArmor.
     *
     * Returning null from this function will use the default value.
     *
     * @param stack  ItemStack for the equpt armor
     * @param entity The entity wearing the armor
     * @param slot   The slot the armor is in
     * @param type   The subtype, can be null or "overlay"
     * @return Path of texture to bind, or null to use default
     */
    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, int slot, String type) {
        return TwilightForestMod.MODEL_DIR + critterTextures[stack.getItemDamage()] + ".png";
    }

    /**
     * Override this method to have an item handle its own armor rendering.
     * 
     * @param entityLiving The entity wearing the armor
     * @param itemStack    The itemStack to render the model of
     * @param armorSlot    0=head, 1=torso, 2=legs, 3=feet
     * 
     * @return A ModelBiped to render instead of the default
     */
    @SideOnly(Side.CLIENT)
    public ModelBiped getArmorModel(EntityLivingBase entityLiving, ItemStack itemStack, int armorSlot) {
        return TwilightForestMod.proxy.getCritterArmorModel(itemStack.getItemDamage());
    }
}
