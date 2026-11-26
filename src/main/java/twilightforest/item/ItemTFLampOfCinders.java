package twilightforest.item;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.BlockEvent;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import twilightforest.TFAchievementPage;
import twilightforest.TwilightForestMod;
import twilightforest.block.TFBlocks;

public class ItemTFLampOfCinders extends ItemTF {

    private static final int FIRING_TIME = 12;

    public ItemTFLampOfCinders() {
        super();
        this.setCreativeTab(TFItems.creativeTab);
        this.maxStackSize = 1;
        this.setMaxDamage(1024);
    }

    /**
     * Properly register icon source
     */
    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister par1IconRegister) {
        this.itemIcon = par1IconRegister
                .registerIcon(TwilightForestMod.ID + ":" + this.getUnlocalizedName().substring(5));
    }

    @Override
    public ItemStack onItemRightClick(ItemStack par1ItemStack, World world, EntityPlayer player) {
        if (par1ItemStack.getItemDamage() < this.getMaxDamage()) {
            player.setItemInUse(par1ItemStack, this.getMaxItemUseDuration(par1ItemStack));
        } else {
            player.stopUsingItem();
        }
        return par1ItemStack;
    }

    /**
     * Callback for item usage. If the item does something special on right clicking, he will have one of those. Return
     * True if something happen and false if it don't. This is for ITEMS, not BLOCKS
     */
    @Override
    public boolean onItemUse(ItemStack par1ItemStack, EntityPlayer player, World world, int x, int y, int z, int side,
            float hitX, float hitY, float hitZ) {
        if (burnBlock(player, world, x, y, z)) {
            world.playSoundAtEntity(player, this.getSound(), 0.5F, 1.5F);

            // spawn flame particles
            for (int i = 0; i < 10; i++) {
                float dx = x + 0.5F + (itemRand.nextFloat() - itemRand.nextFloat()) * 0.75F;
                float dy = y + 0.5F + (itemRand.nextFloat() - itemRand.nextFloat()) * 0.75F;
                float dz = z + 0.5F + (itemRand.nextFloat() - itemRand.nextFloat()) * 0.75F;

                world.spawnParticle("smoke", dx, dy, dz, 0.0D, 0.0D, 0.0D);
                world.spawnParticle("flame", dx, dy, dz, 0.0D, 0.0D, 0.0D);
            }

            return true;
        } else {
            return false;
        }

    }

    private boolean burnBlock(EntityPlayer player, World world, int x, int y, int z) {
        if (world.getBlock(x, y, z) != TFBlocks.thorns) {
            return false;
        }
        if (world.isRemote) {
            return true;
        }
        if (!world.canMineBlock(player, x, y, z) || !player.canPlayerEdit(x, y, z, 1, player.getHeldItem())) {
            return false;
        }
        int metadata = world.getBlockMetadata(x, y, z);
        BlockEvent.BreakEvent event = new BlockEvent.BreakEvent(x, y, z, world, TFBlocks.thorns, metadata, player);
        if (MinecraftForge.EVENT_BUS.post(event)) {
            return false;
        }
        return world.setBlock(x, y, z, TFBlocks.burntThorns, metadata & 12, 2);
    }

    /**
     * called when the player releases the use item button. Args: itemstack, world, entityplayer, itemInUseCount
     */
    @Override
    public void onPlayerStoppedUsing(ItemStack stack, World world, EntityPlayer player, int useRemaining) {
        int useTime = this.getMaxItemUseDuration(stack) - useRemaining;
        if (useTime <= FIRING_TIME || stack.getItemDamage() + 1 >= this.getMaxDamage()) {
            return;
        }
        this.doBurnEffect(world, player);
        // using the charged lamp is the original progression trigger, even without nearby thorns
        if (!world.isRemote) {
            player.triggerAchievement(TFAchievementPage.twilightProgressTroll);
        }
    }

    private boolean doBurnEffect(World world, EntityPlayer player) {
        int px = MathHelper.floor_double(player.lastTickPosX);
        int py = MathHelper.floor_double(player.lastTickPosY + player.getEyeHeight());
        int pz = MathHelper.floor_double(player.lastTickPosZ);
        int range = 4;
        boolean burned = false;

        if (!world.isRemote) {
            for (int dx = -range; dx <= range; dx++) {
                for (int dy = -range; dy <= range; dy++) {
                    for (int dz = -range; dz <= range; dz++) {
                        burned |= this.burnBlock(player, world, px + dx, py + dy, pz + dz);
                    }
                }
            }
            if (burned) {
                world.playSoundAtEntity(player, this.getSound(), 1.5F, 0.8F);
            }
        }
        if (burned || world.isRemote) {
            for (int i = 0; i < 6; i++) {
                int rx = px + itemRand.nextInt(range) - itemRand.nextInt(range);
                int ry = py + itemRand.nextInt(2);
                int rz = pz + itemRand.nextInt(range) - itemRand.nextInt(range);
                world.playAuxSFXAtEntity(player, 2004, rx, ry, rz, 0);
            }
        }
        return burned;
    }

    public String getSound() {
        return "mob.ghast.fireball";
    }

    /**
     * returns the action that specifies what animation to play when the items is being used
     */
    @Override
    public EnumAction getItemUseAction(ItemStack par1ItemStack) {
        return EnumAction.bow;
    }

    /**
     * How long it takes to use or consume an item
     */
    @Override
    public int getMaxItemUseDuration(ItemStack par1ItemStack) {
        return 72000;
    }
}
