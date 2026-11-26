package twilightforest.item;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import twilightforest.TwilightForestMod;
import twilightforest.entity.boss.EntityTFIceBomb;

public class ItemTFIceBomb extends ItemTF {

    private IIcon[] snowIcon = new IIcon[4];

    public ItemTFIceBomb() {
        super();
        this.setMaxStackSize(16);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister par1IconRegister) {
        this.itemIcon = par1IconRegister
                .registerIcon(TwilightForestMod.ID + ":" + this.getUnlocalizedName().substring(5));
        for (int i = 0; i < 4; i++) {
            this.snowIcon[i] = par1IconRegister.registerIcon(TwilightForestMod.ID + ":snow_" + i);
        }
    }

    public IIcon getSnowIcon(int index) {
        return this.snowIcon[Math.floorMod(index, this.snowIcon.length)];
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if (world.isRemote || stack.stackSize <= 0) {
            return stack;
        }
        if (!world.spawnEntityInWorld(new EntityTFIceBomb(world, player))) {
            return stack;
        }
        if (!player.capabilities.isCreativeMode) {
            --stack.stackSize;
        }
        world.playSoundAtEntity(player, "random.bow", 0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));
        return stack;
    }
}
