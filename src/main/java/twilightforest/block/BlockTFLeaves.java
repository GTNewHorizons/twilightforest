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

import twilightforest.item.TFItems;

public class BlockTFLeaves extends BlockLeaves {

    int oakColor = 0x48B518;
    int canopyColor = 0x609860;
    int mangroveColor = 0x80A755;

    public static final String[] unlocalizedNameArray = new String[] { "twilightoak", "canopy", "mangrove",
            "rainboak" };

    protected BlockTFLeaves() {
        super();
        this.setHardness(0.2F);
        this.setLightOpacity(2);
        this.setStepSound(Block.soundTypeGrass);
        this.setCreativeTab(TFItems.creativeTab);
    }

    @Override
    public int getBlockColor() {
        double var1 = 0.5D;
        double var3 = 1.0D;
        return ColorizerFoliage.getFoliageColor(var1, var3);
    }

    /**
     * Returns the color this block should be rendered. Used by leaves.
     */
    @Override
    public int getRenderColor(int par1) {
        return (par1 & 3) == 1 ? canopyColor : ((par1 & 3) == 2 ? mangroveColor : oakColor);
    }

    /**
     * Returns a integer with hex for 0xrrggbb with this color multiplied against the blocks color. Note only called
     * when first determining what to render.
     */
    @Override
    public int colorMultiplier(IBlockAccess world, int x, int y, int z) {
        int meta = world.getBlockMetadata(x, y, z);

        int red = 0;
        int green = 0;
        int blue = 0;

        for (int var9 = -1; var9 <= 1; ++var9) {
            for (int var10 = -1; var10 <= 1; ++var10) {
                int var11 = world.getBiomeGenForCoords(x + var10, z + var9).getBiomeFoliageColor(x, y, z);
                red += (var11 & 16711680) >> 16;
                green += (var11 & 65280) >> 8;
                blue += var11 & 255;
            }
        }

        int normalColor = (red / 9 & 0xFF) << 16 | (green / 9 & 0xFF) << 8 | blue / 9 & 0xFF;

        switch (meta & 3) {
            case 1 -> {
                // canopy colorizer
                return ((normalColor & 0xFEFEFE) + 0x469A66) / 2;
            }
            // return ((normalColor & 0xFEFEFE) + 0x009822) / 2;
            case 2 -> {
                // mangrove colors
                return ((normalColor & 0xFEFEFE) + 0xC0E694) / 2;
            }
            case 3 -> {
                // RAINBOW!
                red = x * 32 + y * 16;
                if ((red & 256) != 0) {
                    red = 255 - (red & 255);
                }
                red &= 255;
                blue = y * 32 + z * 16;
                if ((blue & 256) != 0) {
                    blue = 255 - (blue & 255);
                }
                blue ^= 255;
                green = x * 16 + z * 32;
                if ((green & 256) != 0) {
                    green = 255 - (green & 255);
                }
                green &= 255;
                return red << 16 | blue << 8 | green;
            }
            default -> {
                return normalColor;
            }
        }
    }

    @Override
    public boolean isOpaqueCube() {
        return Blocks.leaves.isOpaqueCube();
    }

    @Override
    public IIcon getIcon(int i, int j) {
        return Blocks.leaves.getIcon(i, (j & 3) == 3 ? 0 : j);
    }

    @Override
    public boolean shouldSideBeRendered(IBlockAccess world, int x, int y, int z, int side) {
        return Blocks.leaves.shouldSideBeRendered(world, x, y, z, side);
    }

    @Override
    public void getSubBlocks(Item item, CreativeTabs par2CreativeTabs, List<ItemStack> itemList) {
        itemList.add(new ItemStack(item, 1, 0));
        itemList.add(new ItemStack(item, 1, 1));
        itemList.add(new ItemStack(item, 1, 2));
        itemList.add(new ItemStack(item, 1, 3));

    }

    @Override
    public int quantityDropped(Random rand) {
        return rand.nextInt(40) == 0 ? 1 : 0;
    }

    @Override
    public Item getItemDropped(int meta, Random rand, int par3) {
        return Item.getItemFromBlock(TFBlocks.sapling);
    }

    /**
     * Determines the damage on the item the block drops. Used in cloth and wood.
     */
    @Override
    public int damageDropped(int par1) {
        int leafType = par1 & 3;

        return leafType;
    }

    /**
     * Drops the block items with a specified chance of dropping the specified items
     */
    @Override
    public void dropBlockAsItemWithChance(World world, int x, int y, int z, int meta, float par6, int par7) {
        if (!world.isRemote) {
            byte chance = 40;

            if ((meta & 3) == 2) {
                chance = 20;
            }

            if (world.rand.nextInt(chance) == 0) {
                Item item = this.getItemDropped(meta, world.rand, par7);
                this.dropBlockAsItem(world, x, y, z, new ItemStack(item, 1, this.getSaplingMeta(meta)));
            }
        }
    }

    public int getSaplingMeta(int leafMeta) {
        int leafType = leafMeta & 3;

        return leafType == 3 ? 9 : leafType;
    }

    @Override
    public String[] func_150125_e() {
        return unlocalizedNameArray;
    }
}
