package twilightforest.item;

import java.util.List;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntitySkull;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import twilightforest.TwilightForestMod;
import twilightforest.block.TFBlocks;

public class ItemTFTrophy extends ItemArmor {

    private static final String[] trophyTypes = new String[] { "hydra", "naga", "lich", "ur-ghast", "snowQueen",
            "minoshroom", "knightPhantom", "alphaYeti", "questingRam" };
    public static final String[] trophyTextures = new String[] { "hydraTrophy", "nagaTrophy", "lichTrophy",
            "urGhastTrophy", "snowQueenTrophy", "minoshroomTrophy", "knightPhantomTrophy", "alphaYetiTrophy",
            "questingRamTrophy" };
    private static final String[] armorTextures = new String[] { "hydra4", "nagahead", "twilightlich64", "towerboss",
            "snowqueen", "minoshroomtaur", "phantomtrophy", "yetialpha", "questram" };
    public IIcon[] trophyIcons;

    public ItemTFTrophy() {
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
        for (int j = 0; j < trophyTypes.length; ++j) {
            par3List.add(new ItemStack(par1, 1, j));
        }
    }

    /**
     * Return an item rarity from EnumRarity
     * 
     * This is automatically uncommon
     */
    @Override
    @SideOnly(Side.CLIENT)
    public EnumRarity getRarity(ItemStack par1ItemStack) {
        return EnumRarity.rare;
    }

    /**
     * Callback for item usage. If the item does something special on right clicking, he will have one of those. Return
     * True if something happen and false if it don't. This is for ITEMS, not BLOCKS
     */
    public boolean onItemUse(ItemStack itemStack, EntityPlayer player, World world, int x, int y, int z, int direction,
            float par8, float par9, float par10) {
        if (direction == 0) {
            return false;
        } else if (!world.getBlock(x, y, z).getMaterial().isSolid()) {
            return false;
        } else {
            if (direction == 1) {
                ++y;
            }

            if (direction == 2) {
                --z;
            }

            if (direction == 3) {
                ++z;
            }

            if (direction == 4) {
                --x;
            }

            if (direction == 5) {
                ++x;
            }

            if (!player.canPlayerEdit(x, y, z, direction, itemStack)) {
                return false;
            } else if (!TFBlocks.trophy.canPlaceBlockAt(world, x, y, z)) {
                return false;
            } else {
                world.setBlock(x, y, z, TFBlocks.trophy, direction, 3);
                int skullRotate = 0;

                if (direction == 1) {
                    skullRotate = MathHelper.floor_double((double) (player.rotationYaw * 16.0F / 360.0F) + 0.5D) & 15;
                }

                TileEntity tileEntity = world.getTileEntity(x, y, z);

                if (tileEntity != null && tileEntity instanceof TileEntitySkull skull) {

                    // use NBT method to set skulltype in order to have 1.7.10 compatibility
                    NBTTagCompound tags = new NBTTagCompound();
                    skull.writeToNBT(tags);
                    tags.setByte("SkullType", (byte) (itemStack.getItemDamage() & 255));
                    skull.readFromNBT(tags);

                    // try {
                    // String skullName = "";
                    // ((TileEntitySkull)tileEntity).func_145905_a(itemStack.getItemDamage(), skullName);
                    // } catch (NoSuchMethodError ex) {
                    // // stop checking admin
                    // FMLLog.warning("[TwilightForest] Could not determine op status for adminOnlyPortals option,
                    // ignoring option.");
                    // TwilightForestMod.adminOnlyPortals = false;
                    // }
                    skull.func_145903_a(skullRotate);
                }

                --itemStack.stackSize;
                return true;
            }
        }
    }

    /**
     * Returns the unlocalized name of this item. This version accepts an ItemStack so different stacks can have
     * different names based on their damage or NBT.
     */
    public String getUnlocalizedName(ItemStack par1ItemStack) {
        int i = par1ItemStack.getItemDamage();

        if (i < 0 || i >= trophyTypes.length) {
            i = 0;
        }

        return super.getUnlocalizedName() + "." + trophyTypes[i];
    }

    /**
     * Properly register icon source
     */
    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister par1IconRegister) {
        this.trophyIcons = new IIcon[trophyTextures.length];

        for (int i = 0; i < trophyTextures.length; ++i) {
            this.trophyIcons[i] = par1IconRegister.registerIcon(TwilightForestMod.ID + ":" + trophyTextures[i]);
        }
    }

    /**
     * Gets an icon index based on an item's damage value
     */
    @SideOnly(Side.CLIENT)
    public IIcon getIconFromDamage(int par1) {
        if (par1 < 0 || par1 >= trophyTypes.length) {
            par1 = 0;
        }

        return this.trophyIcons[par1];
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
        return TwilightForestMod.MODEL_DIR + armorTextures[stack.getItemDamage()] + ".png";
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
        return TwilightForestMod.proxy.getTrophyArmorModel(itemStack.getItemDamage());
    }
}
