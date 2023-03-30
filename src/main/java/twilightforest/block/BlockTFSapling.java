package twilightforest.block;

import java.util.List;
import java.util.Random;

import net.minecraft.block.BlockSapling;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

import twilightforest.TwilightForestMod;
import twilightforest.item.TFItems;
import twilightforest.world.TFGenCanopyTree;
import twilightforest.world.TFGenDarkCanopyTree;
import twilightforest.world.TFGenHollowTree;
import twilightforest.world.TFGenLargeRainboak;
import twilightforest.world.TFGenMangroveTree;
import twilightforest.world.TFGenMinersTree;
import twilightforest.world.TFGenSmallRainboak;
import twilightforest.world.TFGenSmallTwilightOak;
import twilightforest.world.TFGenSortingTree;
import twilightforest.world.TFGenTreeOfTime;
import twilightforest.world.TFGenTreeOfTransformation;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockTFSapling extends BlockSapling {

    private IIcon[] icons;
    private String[] iconNames = new String[] { "sapling_oak", "sapling_canopy", "sapling_mangrove", "sapling_darkwood",
            "sapling_hollow_oak", "sapling_time", "sapling_transformation", "sapling_mining", "sapling_sorting",
            "sapling_rainboak" };

    protected BlockTFSapling() {
        super();
        float var3 = 0.4F;
        this.setBlockBounds(0.5F - var3, 0.0F, 0.5F - var3, 0.5F + var3, var3 * 2.0F, 0.5F + var3);
        this.setCreativeTab(TFItems.creativeTab);
    }

    /**
     * Ticks the block if it's been scheduled
     */
    @Override
    public void updateTick(World world, int x, int y, int z, Random rand) {
        if (!world.isRemote) {
            // this.checkFlowerChange(par1World, x, y, z);

            if (world.getBlockLightValue(x, y + 1, z) >= 9 && rand.nextInt(7) == 0) {
                this.func_149878_d(world, x, y, z, rand);
            }
        }
    }

    /**
     * Attempts to grow a sapling into a tree
     */
    @Override
    public void func_149878_d(World world, int x, int y, int z, Random rand) {
        int meta = world.getBlockMetadata(x, y, z);
        WorldGenerator treeGenerator = null;
        int var8 = 0;
        int var9 = 0;
        boolean largeTree = false;

        treeGenerator = switch (meta) {
            case 1 -> new TFGenCanopyTree(true);
            case 2 -> new TFGenMangroveTree(true);
            case 3 -> new TFGenDarkCanopyTree(true);
            case 4 -> new TFGenHollowTree(true);
            case 5 -> new TFGenTreeOfTime(true);
            case 6 -> new TFGenTreeOfTransformation(true);
            case 7 -> new TFGenMinersTree(true);
            case 8 -> new TFGenSortingTree(true);
            case 9 -> rand.nextInt(7) == 0 ? new TFGenLargeRainboak(true) : new TFGenSmallRainboak(true);
            default -> new TFGenSmallTwilightOak(true);
        };

        if (largeTree) {
            world.setBlock(x + var8, y, z + var9, Blocks.air, 0, 4);
            world.setBlock(x + var8 + 1, y, z + var9, Blocks.air, 0, 4);
            world.setBlock(x + var8, y, z + var9 + 1, Blocks.air, 0, 4);
            world.setBlock(x + var8 + 1, y, z + var9 + 1, Blocks.air, 0, 4);
        } else {
            world.setBlock(x, y, z, Blocks.air, 0, 4);
        }

        if (!treeGenerator.generate(world, rand, x + var8, y, z + var9)) {
            if (largeTree) {
                world.setBlock(x + var8, y, z + var9, this, meta, 4);
                world.setBlock(x + var8 + 1, y, z + var9, this, meta, 4);
                world.setBlock(x + var8, y, z + var9 + 1, this, meta, 4);
                world.setBlock(x + var8 + 1, y, z + var9 + 1, this, meta, 4);
            } else {
                world.setBlock(x, y, z, this, meta, 4);
            }
        }
    }

    /**
     * From the specified side and block metadata retrieves the blocks texture. Args: side, metadata
     */
    @Override
    public IIcon getIcon(int side, int metadata) {
        if (metadata < this.icons.length) {
            return this.icons[metadata];
        } else {
            return null;
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister par1IconRegister) {
        this.icons = new IIcon[iconNames.length];

        for (int i = 0; i < this.icons.length; ++i) {
            this.icons[i] = par1IconRegister.registerIcon(TwilightForestMod.ID + ":" + iconNames[i]);
        }
    }

    /**
     * Determines the damage on the item the block drops. Used in cloth and wood.
     */
    @Override
    public int damageDropped(int par1) {
        return par1;
    }

    @Override
    @SideOnly(Side.CLIENT)

    /**
     * returns a list of blocks with the same ID, but different meta (eg: wood returns 4 blocks)
     */
    public void getSubBlocks(Item item, CreativeTabs par2CreativeTabs, List<ItemStack> itemList) {
        itemList.add(new ItemStack(item, 1, 0));
        itemList.add(new ItemStack(item, 1, 1));
        itemList.add(new ItemStack(item, 1, 2));
        itemList.add(new ItemStack(item, 1, 3));
        itemList.add(new ItemStack(item, 1, 4));
        itemList.add(new ItemStack(item, 1, 5));
        itemList.add(new ItemStack(item, 1, 6));
        itemList.add(new ItemStack(item, 1, 7));
        itemList.add(new ItemStack(item, 1, 8));
        itemList.add(new ItemStack(item, 1, 9));
    }

}
