package tconstruct.smeltery.items;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBucket;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.fluids.BlockFluidFinite;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mantle.world.WorldHelper;
import tconstruct.TConstruct;
import twilightforest.integration.TFTinkerConstructIntegration;

public class TFFilledBucket extends ItemBucket {

    public TFFilledBucket(Block b) {
        super(b);
        setUnlocalizedName("tconstruct.bucket");
        setContainerItem(Items.bucket);
        this.setHasSubtypes(true);
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        boolean wannabeFull = false;
        MovingObjectPosition position = this.getMovingObjectPositionFromPlayer(world, player, wannabeFull);

        if (position != null) {
            if (position.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
                int clickX = position.blockX;
                int clickY = position.blockY;
                int clickZ = position.blockZ;

                if (!world.canMineBlock(player, clickX, clickY, clickZ)) {
                    return stack;
                }

                if (position.sideHit == 0) {
                    --clickY;
                }

                if (position.sideHit == 1) {
                    ++clickY;
                }

                if (position.sideHit == 2) {
                    --clickZ;
                }

                if (position.sideHit == 3) {
                    ++clickZ;
                }

                if (position.sideHit == 4) {
                    --clickX;
                }

                if (position.sideHit == 5) {
                    ++clickX;
                }

                if (!player.canPlayerEdit(clickX, clickY, clickZ, position.sideHit, stack)) {
                    return stack;
                }

                if (this.tryPlaceContainedLiquid(world, clickX, clickY, clickZ, stack.getItemDamage())
                        && !player.capabilities.isCreativeMode) {
                    return new ItemStack(Items.bucket);
                }
            }
        }
        return stack;
    }

    public boolean tryPlaceContainedLiquid(World world, int clickX, int clickY, int clickZ, int type) {
        if (!WorldHelper.isAirBlock(world, clickX, clickY, clickZ)
                && world.getBlock(clickX, clickY, clickZ).getMaterial().isSolid()) {
            return false;
        } else {
            try {
                if (TFTinkerConstructIntegration.fluidBlocks[type] == null) return false;

                int metadata = 0;
                if (TFTinkerConstructIntegration.fluidBlocks[type] instanceof BlockFluidFinite) metadata = 7;

                world.setBlock(clickX, clickY, clickZ, TFTinkerConstructIntegration.fluidBlocks[type], metadata, 3); // TODO:
                                                                                                                     // Merge
                // liquids
            } catch (ArrayIndexOutOfBoundsException ex) {
                TConstruct.logger.warn("AIOBE occured when placing bucket into world; " + ex);
                return false;
            }

            return true;
        }
    }

    @Override
    public void getSubItems(Item b, CreativeTabs tab, List list) {
        for (int i = 0; i < icons.length; i++) list.add(new ItemStack(b, 1, i));
    }

    public IIcon[] icons;

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIconFromDamage(int meta) {
        return icons[meta];
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister iconRegister) {
        this.icons = new IIcon[textureNames.length];

        for (int i = 0; i < this.icons.length; ++i) {
            this.icons[i] = iconRegister.registerIcon("tinker:materials/bucket_" + textureNames[i]);
        }
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        int arr = MathHelper.clamp_int(stack.getItemDamage(), 0, materialNames.length);
        return getUnlocalizedName() + "." + materialNames[arr];
    }

    public static final String[] materialNames = new String[] { "FieryEssence", "FieryMetal", "Knightmetal" };

    public static final String[] textureNames = new String[] { "fiery_essence", "fiery_metal", "knightmetal" };
}
