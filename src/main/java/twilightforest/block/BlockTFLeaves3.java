package twilightforest.block;

import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLeaves;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.ColorizerFoliage;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import twilightforest.item.TFItems;

public class BlockTFLeaves3 extends BlockLeaves {

    public static final String[] names = new String[] { "thorn", "beanstalk" };

    protected BlockTFLeaves3() {
        super();
        this.setStepSound(Block.soundTypeGrass);
        this.setCreativeTab(TFItems.creativeTab);
    }

    public String[] func_150125_e() {
        return names;
    }

    /**
     * Returns the color this block should be rendered. Used by leaves.
     */
    @SideOnly(Side.CLIENT)
    public int getRenderColor(int meta) {
        return (meta & 3) == 1 ? ColorizerFoliage.getFoliageColorPine()
                : ((meta & 3) == 2 ? ColorizerFoliage.getFoliageColorBirch() : super.getRenderColor(meta));
    }

    /**
     * Returns a integer with hex for 0xrrggbb with this color multiplied against the blocks color. Note only called
     * when first determining what to render.
     */
    @SideOnly(Side.CLIENT)
    public int colorMultiplier(IBlockAccess world, int x, int y, int z) {
        int meta = world.getBlockMetadata(x, y, z);
        return (meta & 3) == 1 ? ColorizerFoliage.getFoliageColorPine()
                : ((meta & 3) == 2 ? ColorizerFoliage.getFoliageColorBirch() : super.colorMultiplier(world, x, y, z));
    }

    public int damageDropped(int p_149692_1_) {
        return super.damageDropped(p_149692_1_) + 4;
    }

    @Override
    public boolean isOpaqueCube() {
        return Blocks.leaves.isOpaqueCube();
    }

    public int getDamageValue(World world, int x, int y, int z) {
        return world.getBlockMetadata(x, y, z) & 3;
    }

    public int quantityDropped(Random rand) {
        return 0;
    }

    @Override
    public boolean shouldSideBeRendered(IBlockAccess world, int x, int y, int z, int side) {
        return Blocks.leaves.shouldSideBeRendered(world, x, y, z, side);
    }

    @Override
    public Item getItemDropped(int par1, Random rand, int par3) {
        return TFItems.magicBeans;
    }

    @Override
    public IIcon getIcon(int i, int j) {
        return Blocks.leaves.getIcon(i, 0 & 3);
    }

    @Override
    public void getSubBlocks(Item item, CreativeTabs par2CreativeTabs, List<ItemStack> itemList) {
        for (int meta = 0; meta < names.length; meta++) {
            itemList.add(new ItemStack(item, 1, meta));
        }
    }

    @Override
    public boolean canBeReplacedByLeaves(IBlockAccess world, int x, int y, int z) {
        return true;
    }

}
