package twilightforest.block;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import twilightforest.item.TFItems;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockTFPlanks extends Block {

    public IIcon[] icons;
    public static String[] textureNames = new String[] { "twilight_oak", "canopy", "mangrove", "darkwood", "timewood",
            "transwood", "minewood", "sortingwood" };

    public BlockTFPlanks() {
        super(Material.wood);
        this.setHardness(2.0f);
        this.setCreativeTab(TFItems.creativeTab);
        this.setStepSound(Block.soundTypeWood);
    }

    // TODO: Add the random texture variations from TF 1.12
    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta) {
        return meta >= textureNames.length ? icons[0] : icons[meta];
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister iconRegister) {
        this.icons = new IIcon[textureNames.length];
        for (int i = 0; i < this.icons.length; ++i) {
            this.icons[i] = iconRegister.registerIcon("twilightforest:" + textureNames[i] + "_planks");
        }
    }

    public int getFlammability(IBlockAccess world, int x, int y, int z, int metadata, ForgeDirection face) {
        return this.getFlammability(world, x, y, z, face);
    }

    public int getFireSpreadSpeed(World world, int x, int y, int z, int metadata, ForgeDirection face) {
        return this.getFireSpreadSpeed(world, x, y, z, face);
    }

    @Override
    public int damageDropped(int meta) {
        return meta;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubBlocks(Item item, CreativeTabs creativeTab, List<ItemStack> itemList) {
        for (int i = 0; i < textureNames.length; i++) {
            itemList.add(new ItemStack(item, 1, i));
        }
    }

}
