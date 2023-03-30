package twilightforest.block;

import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLeaves;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import twilightforest.TwilightForestMod;
import twilightforest.item.TFItems;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockTFMagicLeaves extends BlockLeaves {

    int oakColor = 0x48B518;
    int canopyColor = 0x609860;
    int mangroveColor = 0x80A755;

    public static final int META_TIME = 0;
    public static final int META_TRANS = 1;
    public static final int META_MINE = 2;
    public static final int META_SORT = 3;

    public static IIcon SPR_TIMELEAVES;
    public static IIcon SPR_TIMELEAVES_OPAQUE;
    public static IIcon SPR_TIMEFX;
    public static IIcon SPR_TRANSLEAVES;
    public static IIcon SPR_TRANSLEAVES_OPAQUE;
    public static IIcon SPR_TRANSFX;
    public static IIcon SPR_SORTLEAVES;
    public static IIcon SPR_SORTLEAVES_OPAQUE;
    public static IIcon SPR_SORTFX;

    private static final int SAPLING_ITEM_META_OFFSET = 5;

    protected BlockTFMagicLeaves() {
        super();
        this.setHardness(0.2F);
        this.setLightOpacity(2);
        this.setStepSound(Block.soundTypeGrass);
        this.setCreativeTab(TFItems.creativeTab);
    }

    /**
     * Returns the color this block should be rendered. Used by leaves.
     */
    @Override
    public int getRenderColor(int par1) {
        return switch (par1 & 3) {
            case META_TIME -> 106 << 16 | 156 << 8 | 23;
            case META_TRANS -> 108 << 16 | 204 << 8 | 234;
            case META_MINE -> 252 << 16 | 241 << 8 | 68;
            case META_SORT -> 54 << 16 | 76 << 8 | 3;
            default -> 16777215;
        };
    }

    /**
     * Returns a integer with hex for 0xrrggbb with this color multiplied against the blocks color. Note only called
     * when first determining what to render.
     */
    @Override
    public int colorMultiplier(IBlockAccess world, int x, int y, int z) {
        int leafType = world.getBlockMetadata(x, y, z) & 0x03;

        int red = 0;
        int green = 0;
        int blue = 0;

        int fade;
        float spring;
        float fall;

        switch (leafType) {
            case META_TIME -> {
                fade = x * 16 + y * 16 + z * 16;
                if ((fade & 256) != 0) {
                    fade = 255 - (fade & 255);
                }
                fade &= 255;
                spring = (255 - fade) / 255F;
                fall = fade / 255F;
                red = (int) (spring * 106 + fall * 251);
                green = (int) (spring * 156 + fall * 108);
                blue = (int) (spring * 23 + fall * 27);
            }
            case META_TRANS -> {
                fade = x * 27 + y * 63 + z * 39;
                if ((fade & 256) != 0) {
                    fade = 255 - (fade & 255);
                }
                fade &= 255;
                spring = (255 - fade) / 255F;
                fall = fade / 255F;
                red = (int) (spring * 108 + fall * 96);
                green = (int) (spring * 204 + fall * 107);
                blue = (int) (spring * 234 + fall * 121);
            }
            case META_MINE -> {
                fade = x * 31 + y * 33 + z * 32;
                if ((fade & 256) != 0) {
                    fade = 255 - (fade & 255);
                }
                fade &= 255;
                spring = (255 - fade) / 255F;
                fall = fade / 255F;
                red = (int) (spring * 252 + fall * 237);
                green = (int) (spring * 241 + fall * 172);
                blue = (int) (spring * 68 + fall * 9);
            }
            case META_SORT -> {
                fade = x * 63 + y * 63 + z * 63;
                if ((fade & 256) != 0) {
                    fade = 255 - (fade & 255);
                }
                fade &= 255;
                spring = (255 - fade) / 255F;
                fall = fade / 255F;
                red = (int) (spring * 54 + fall * 168);
                green = (int) (spring * 76 + fall * 199);
                blue = (int) (spring * 3 + fall * 43);
            }
        }
        return red << 16 | green << 8 | blue;
    }

    /**
     * The type of render function that is called for this block
     */
    @Override
    public int getRenderType() {
        return TwilightForestMod.proxy.getMagicLeavesBlockRenderID();
    }

    /**
     * Returns which pass should this block be rendered on. 0 for solids and 1 for alpha
     */
    @Override
    public int getRenderBlockPass() {
        return 0;
    }

    @Override
    public boolean isOpaqueCube() {
        return Blocks.leaves.isOpaqueCube();
    }

    @Override
    public IIcon getIcon(int side, int meta) {
        if (Blocks.leaves.isOpaqueCube()) {
            return switch (meta & 0x03) {
                default -> SPR_TIMELEAVES_OPAQUE;
                case 1 -> SPR_TRANSLEAVES_OPAQUE;
                case 3 -> SPR_SORTLEAVES_OPAQUE;
            };
        } else {
            return switch (meta & 0x03) {
                default -> SPR_TIMELEAVES;
                case 1 -> SPR_TRANSLEAVES;
                case 3 -> SPR_SORTLEAVES;
            };
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister par1IconRegister) {
        BlockTFMagicLeaves.SPR_TIMELEAVES = par1IconRegister.registerIcon(TwilightForestMod.ID + ":time_leaves");
        BlockTFMagicLeaves.SPR_TIMELEAVES_OPAQUE = par1IconRegister
                .registerIcon(TwilightForestMod.ID + ":time_leaves_opaque");
        BlockTFMagicLeaves.SPR_TRANSLEAVES = par1IconRegister.registerIcon(TwilightForestMod.ID + ":trans_leaves");
        BlockTFMagicLeaves.SPR_TRANSLEAVES_OPAQUE = par1IconRegister
                .registerIcon(TwilightForestMod.ID + ":trans_leaves_opaque");
        BlockTFMagicLeaves.SPR_SORTLEAVES = par1IconRegister.registerIcon(TwilightForestMod.ID + ":sort_leaves");
        BlockTFMagicLeaves.SPR_SORTLEAVES_OPAQUE = par1IconRegister
                .registerIcon(TwilightForestMod.ID + ":sort_leaves_opaque");
    }

    /**
     * returns a list of blocks with the same ID, but different meta (eg: wood returns 4 blocks)
     */
    @Override
    public void getSubBlocks(Item item, CreativeTabs par2CreativeTabs, List<ItemStack> itemList) {
        itemList.add(new ItemStack(item, 1, 0));
        itemList.add(new ItemStack(item, 1, 1));
        itemList.add(new ItemStack(item, 1, 2));
        itemList.add(new ItemStack(item, 1, 3));
    }

    /**
     * A randomly called display update to be able to add particles or other items for display
     */
    @Override
    public void randomDisplayTick(World world, int x, int y, int z, Random rand) {
        int meta = world.getBlockMetadata(x, y, z);

        if ((meta & 3) == META_TRANS) {
            for (int i = 0; i < 1; ++i) {
                this.sparkleRunes(world, x, y, z, rand);
            }
        }
    }

    /**
     * The leaf sparkles.
     */
    private void sparkleRunes(World world, int x, int y, int z, Random rand) {
        double offset = 0.0625D;

        int side = rand.nextInt(5) + 1;
        double rx = x + rand.nextFloat();
        double ry = y + rand.nextFloat();
        double rz = z + rand.nextFloat();

        if (side == 0 && world.isAirBlock(x, y + 1, z)) {
            ry = y + 1 + offset;
        }

        if (side == 1 && world.isAirBlock(x, y - 1, z)) {
            ry = y - offset;
        }

        if (side == 2 && world.isAirBlock(x, y, z + 1)) {
            rz = z + 1 + offset;
        }

        if (side == 3 && world.isAirBlock(x, y, z - 1)) {
            rz = z - offset;
        }

        if (side == 4 && world.isAirBlock(x + 1, y, z)) {
            rx = x + 1 + offset;
        }

        if (side == 5 && world.isAirBlock(x - 1, y, z)) {
            rx = x - offset;
        }

        if (rx < x || rx > x + 1 || ry < y || ry > y + 1 || rz < z || rz > z + 1) {
            TwilightForestMod.proxy.spawnParticle(world, "leafrune", rx, ry, rz, 0.0D, 0.0D, 0.0D);
        }
    }

    /**
     * Localisation Function, [leafblock-meta & 3] will be added to the unlocalised name
     */
    @Override
    public String[] func_150125_e() {
        return new String[] { "time", "trans", "mine", "sort" };
    }

    /**
     * returns the items that will be dropped
     */
    @Override
    public Item getItemDropped(int p_149650_1_, Random p_149650_2_, int p_149650_3_) {
        return Item.getItemFromBlock(TFBlocks.sapling);
    }

    /**
     * returns the meta of the item that will be dropped
     */
    @Override
    public int damageDropped(int metadata) {
        return (metadata & 3) + SAPLING_ITEM_META_OFFSET;
    }

    /**
     * DROPCHANCE for Saplings, higher Values returned here will get LOWER chance overall! For comparison: Vanilla
     * Trees: 40 Vanilla Dark Oak: 80 Thaumcraft Greatwood: ~200 Thaumcraft Silverwood: ~250
     */
    @Override
    protected int func_150123_b(int metadata) {
        return 150;
    }

    /**
     * Additional Drops can be spawned here chance is 200, in vanilla code, same logic as the previous function, used
     * for apples in vanilla
     */
    @Override
    @SuppressWarnings("ALL")
    protected void func_150124_c(World world, int x, int y, int z, int metadata, int chance) {
        if (world.rand.nextInt(chance) == 0) initial: switch (metadata & 3) {
            case META_TIME:
                this.dropBlockAsItem(world, x, y, z, new ItemStack(Items.clock, 1, 0));
                break initial;
            case META_TRANS:
                this.dropBlockAsItem(world, x, y, z, new ItemStack(TFBlocks.firefly, 2, 0));
                break initial;
            case META_MINE:
                switch (world.rand.nextInt(6)) {
                    case 0:
                        this.dropBlockAsItem(world, x, y, z, new ItemStack(Blocks.iron_ore, 1, 0));
                        break initial;
                    case 1:
                        this.dropBlockAsItem(world, x, y, z, new ItemStack(Blocks.gold_ore, 1, 0));
                        break initial;
                    case 2:
                        this.dropBlockAsItem(world, x, y, z, new ItemStack(Blocks.diamond_ore, 1, 0));
                        break initial;
                    case 3:
                        this.dropBlockAsItem(world, x, y, z, new ItemStack(Blocks.emerald_ore, 1, 0));
                        break initial;
                    case 4:
                        this.dropBlockAsItem(world, x, y, z, new ItemStack(Blocks.coal_ore, 1, 0));
                        break initial;
                    case 5:
                        this.dropBlockAsItem(world, x, y, z, new ItemStack(Blocks.redstone_ore, 1, 0));
                        break initial;
                }
            case META_SORT:
                this.dropBlockAsItem(world, x, y, z, new ItemStack(Blocks.chest, 1, 0));
                break initial;
        }
    }
}
