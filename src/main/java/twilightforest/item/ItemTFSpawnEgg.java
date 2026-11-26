package twilightforest.item;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemMonsterPlacer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Facing;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import twilightforest.TwilightForestMod;
import twilightforest.entity.TFCreatures;
import twilightforest.entity.TFEntityEggInfo;

public class ItemTFSpawnEgg extends ItemMonsterPlacer {

    protected ItemTFSpawnEgg() {
        super();
        this.setHasSubtypes(true);
        this.setCreativeTab(TFItems.creativeTab);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public int getColorFromItemStack(ItemStack itemStack, int par2) {
        TFEntityEggInfo info = TFCreatures.entityEggs.get(itemStack.getItemDamage());
        return info != null ? (par2 == 0 ? info.primaryColor : info.secondaryColor) : 16777215;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public String getItemStackDisplayName(ItemStack itemStack) {
        String prefix = (String.valueOf(StatCollector.translateToLocal(this.getUnlocalizedName() + ".name"))).trim();
        String entityname = TFCreatures.getStringFromID(itemStack.getItemDamage());

        if (entityname != null) {
            prefix = prefix + " "
                    + StatCollector
                            .translateToLocal(String.format("entity.%s.%s.name", TwilightForestMod.ID, entityname));
        }

        return prefix;
    }

    /**
     * Callback for item usage. If the item does something special on right clicking, he will have one of those. Return
     * True if something happen and false if it don't. This is for ITEMS, not BLOCKS
     */
    @Override
    public boolean onItemUse(ItemStack itemStack, EntityPlayer player, World world, int par4, int par5, int par6,
            int par7, float par8, float par9, float par10) {
        if (world.isRemote) {
            return true;
        } else {
            Block var11 = world.getBlock(par4, par5, par6);
            par4 += Facing.offsetsXForSide[par7];
            par5 += Facing.offsetsYForSide[par7];
            par6 += Facing.offsetsZForSide[par7];
            if (!player.canPlayerEdit(par4, par5, par6, par7, itemStack)
                    || !world.canMineBlock(player, par4, par5, par6)) {
                return false;
            }
            double var12 = 0.0D;

            if (par7 == 1 && (var11 == Blocks.fence || var11 == Blocks.nether_brick_fence)) {
                var12 = 0.5D;
            }

            Entity entity = spawnCreature(
                    world,
                    itemStack.getItemDamage(),
                    (double) par4 + 0.5D,
                    (double) par5 + var12,
                    (double) par6 + 0.5D);

            if (entity != null) {
                if (entity instanceof EntityLiving && itemStack.hasDisplayName()) {
                    ((EntityLiving) entity).setCustomNameTag(itemStack.getDisplayName());
                }

                if (!player.capabilities.isCreativeMode) {
                    --itemStack.stackSize;
                }
            }
            return entity != null;
        }
    }

    /**
     * Spawns the creature specified by the egg's type in the location specified by the last three parameters.
     * Parameters: world, entityID, x, y, z.
     */
    public static Entity spawnCreature(World world, int entityID, double x, double y, double z) {
        if (world.isRemote || !TFCreatures.entityEggs.containsKey(entityID)) {
            return null;
        }

        Entity entity = TFCreatures.createEntityByID(entityID, world);
        if (!(entity instanceof EntityLiving living)) {
            return null;
        }
        entity.setLocationAndAngles(x, y, z, world.rand.nextFloat() * 360.0F, 0.0F);
        living.onSpawnWithEgg(null);
        if (!world.spawnEntityInWorld(entity)) {
            return null;
        }
        living.playLivingSound();
        return entity;
    }

    // returns a list of items with the same ID, but different meta (eg: dye returns 16 items)
    @Override
    public void getSubItems(Item item, CreativeTabs par2CreativeTabs, List<ItemStack> itemList) {

        for (TFEntityEggInfo var5 : TFCreatures.entityEggs.values()) {
            itemList.add(new ItemStack(item, 1, var5.spawnedID));
        }
    }

    // Gets an icon index based on an item's damage value and the given render pass
    public IIcon getIconFromDamageForRenderPass(int par1, int par2) {
        return Items.spawn_egg.getIconFromDamageForRenderPass(par1, par2);
    }

    // Properly register icon source
    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister par1IconRegister) {
        // don't load anything
    }
}
