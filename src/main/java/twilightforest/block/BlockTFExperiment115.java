package twilightforest.block;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import twilightforest.TwilightForestMod;
import twilightforest.item.TFItems;
import twilightforest.tileentity.TileEntityTFCake;

public class BlockTFExperiment115 extends Block {

    @SideOnly(Side.CLIENT)
    private IIcon iconTop;
    @SideOnly(Side.CLIENT)
    private IIcon iconBottom;
    @SideOnly(Side.CLIENT)
    private IIcon iconInner;

    protected BlockTFExperiment115() {
        super(Material.cake);
        this.setStepSound(soundTypeCloth);
        this.setHardness(0.5f);
        this.setTickRandomly(true);
        this.useNeighborBrightness = true;
    }

    @Override
    public boolean canProvidePower() {
        return true;
    }

    @Override
    public boolean canConnectRedstone(IBlockAccess world, int x, int y, int z, int side) {
        return side != -1 && world.getTileEntity(x, y, z) instanceof TileEntityTFCake cake && cake.getSprinkled();
    }

    @Override
    public int isProvidingWeakPower(IBlockAccess world, int x, int y, int z, int side) {
        return side != -1 && world.getTileEntity(x, y, z) instanceof TileEntityTFCake cake && cake.getSprinkled()
                ? Math.min(15, world.getBlockMetadata(x, y, z) * 2)
                : 0;
    }

    /**
     * Updates the blocks bounds based on its current state. Args: world, x, y, z
     */
    public void setBlockBoundsBasedOnState(IBlockAccess worldIn, int x, int y, int z) {
        int l = worldIn.getBlockMetadata(x, y, z);
        float f = 0.0625F;
        float fx = l > 2 ? 1.0F - f : 0.5F;
        float fy = 0.5F;
        float fz = l > 4 ? 1.0F - f : 0.5F;
        this.setBlockBounds(f, 0.0F, f, fx, fy, fz);
    }

    /**
     * Sets the block's bounds for rendering it as an item
     */
    public void setBlockBoundsForItemRender() {
        float f = 0.0625F;
        float f1 = 0.5F;
        this.setBlockBounds(f, 0.0F, f, 1.0F - f, f1, 1.0F - f);
    }

    /**
     * Returns a bounding box from the pool of bounding boxes (this means this box can change after the pool has been
     * cleared to be reused)
     */
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World worldIn, int x, int y, int z) {
        int l = worldIn.getBlockMetadata(x, y, z);
        float f = 0.0625F;
        float fx = l > 2 ? 1.0F - f : 0.5F;
        float fy = 0.5F;
        float fz = l > 4 ? 1.0F - f : 0.5F;
        return AxisAlignedBB.getBoundingBox(
                (double) x + f,
                (double) y,
                (double) z + f,
                (double) x + fx,
                (double) y + fy,
                (double) z + fz);
    }

    /**
     * Returns the bounding box of the wired rectangular prism to render.
     */
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getSelectedBoundingBoxFromPool(World worldIn, int x, int y, int z) {
        int l = worldIn.getBlockMetadata(x, y, z);
        float f = 0.0625F;
        float fx = l > 2 ? 1.0F : 0.5F + f;
        float fy = 0.5F;
        float fz = l > 4 ? 1.0F : 0.5F + f;
        return AxisAlignedBB
                .getBoundingBox((double) x, (double) y, (double) z, (double) x + fx, (double) y + fy, (double) z + fz);
    }

    /**
     * Gets the block's texture. Args: side, meta
     */
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta) {
        return side == 1 ? this.iconTop
                : (side == 0 ? this.iconBottom : (meta > 0 && side == 4 ? this.iconInner : this.blockIcon));
    }

    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister reg) {
        this.blockIcon = reg.registerIcon(TwilightForestMod.ID + ":experiment115/experiment115_side");
        this.iconInner = reg.registerIcon(TwilightForestMod.ID + ":experiment115/experiment115_inner");
        this.iconTop = reg.registerIcon(TwilightForestMod.ID + ":experiment115/experiment115_top");
        this.iconBottom = reg.registerIcon(TwilightForestMod.ID + ":experiment115/experiment115_bottom");
    }

    /**
     * If this block doesn't render as an ordinary block it will return False (examples: signs, buttons, stairs, etc)
     */
    public boolean renderAsNormalBlock() {
        return false;
    }

    /**
     * Is this block (a) opaque and (b) a full 1m cube? This determines whether or not to render the shared face of two
     * adjacent blocks and also whether the player can attach torches, redstone wire, etc to this block.
     */
    public boolean isOpaqueCube() {
        return false;
    }

    /**
     * The type of render function that is called for this block
     */
    @Override
    public int getRenderType() {
        return TwilightForestMod.proxy.getCakeBlockRenderID();
    }

    @Override
    public boolean hasTileEntity(final int metadata) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(World world, int metadata) {
        return new TileEntityTFCake();
    }

    /**
     * Called upon block activation (right click on the block.)
     */
    public boolean onBlockActivated(World worldIn, int x, int y, int z, EntityPlayer player, int side, float subX,
            float subY, float subZ) {
        if (worldIn.isRemote) return true;
        if (player.getHeldItem() != null) {
            if (player.getHeldItem().getItem() == TFItems.experiment115 && player.isSneaking()) {
                int l = worldIn.getBlockMetadata(x, y, z) + 1;
                if (l <= 8) {
                    worldIn.setBlockMetadataWithNotify(x, y, z, l, 2);
                    if (!player.capabilities.isCreativeMode) player.getHeldItem().stackSize--;
                }
                worldIn.notifyBlocksOfNeighborChange(x, y, z, this);
            } else {
                if (player.getHeldItem().getItem() == Items.redstone) {
                    if (!(worldIn.getTileEntity(x, y, z) instanceof TileEntityTFCake te)) return true;
                    int l = worldIn.getBlockMetadata(x, y, z);
                    if (l == 8 && !te.getSprinkled()) {
                        te.setSprinkled(true);
                        if (!player.capabilities.isCreativeMode) player.getHeldItem().stackSize--;
                    }
                    worldIn.notifyBlocksOfNeighborChange(x, y, z, this);
                } else this.eatCake(worldIn, x, y, z, player);
            }
        } else this.eatCake(worldIn, x, y, z, player);
        return true;
    }

    /**
     * Called when a player hits the block. Args: world, x, y, z, player
     */
    public void onBlockClicked(World worldIn, int x, int y, int z, EntityPlayer player) {
        this.eatCake(worldIn, x, y, z, player);
    }

    private void eatCake(World worldIn, int x, int y, int z, EntityPlayer player) {
        if (!worldIn.isRemote && player.canEat(false)) {
            player.getFoodStats().addStats(4, 0.3F);
            int l = worldIn.getBlockMetadata(x, y, z) - 1;

            if (l <= 0) worldIn.setBlockToAir(x, y, z);
            else worldIn.setBlockMetadataWithNotify(x, y, z, l, 2);
            worldIn.notifyBlocksOfNeighborChange(x, y, z, this);
        }
    }

    /**
     * Checks to see if its valid to put this block at the specified coordinates. Args: world, x, y, z
     */
    public boolean canPlaceBlockAt(World worldIn, int x, int y, int z) {
        return !super.canPlaceBlockAt(worldIn, x, y, z) ? false : this.canBlockStay(worldIn, x, y, z);
    }

    @Override
    public void updateTick(World worldIn, int x, int y, int z, Random random) {
        if (worldIn.getTileEntity(x, y, z) instanceof TileEntityTFCake te && te.getSprinkled()) {
            {
                int l = te.getBlockMetadata() + 1;
                if (l <= 8) {
                    worldIn.setBlockMetadataWithNotify(x, y, z, l, 2);
                    worldIn.notifyBlocksOfNeighborChange(x, y, z, this);
                }
            }
        }
    }

    /**
     * Lets the block know when one of its neighbor changes. Doesn't know which neighbor changed (coordinates passed are
     * their own) Args: x, y, z, neighbor Block
     */
    public void onNeighborBlockChange(World worldIn, int x, int y, int z, Block neighbor) {
        if (!this.canBlockStay(worldIn, x, y, z)) {
            worldIn.setBlockToAir(x, y, z);
        }
    }

    /**
     * Can this block stay at this position. Similar to canPlaceBlockAt except gets checked often with plants.
     */
    public boolean canBlockStay(World worldIn, int x, int y, int z) {
        return worldIn.getBlock(x, y - 1, z).getMaterial().isSolid();
    }

    /**
     * Returns the quantity of items to drop on block destruction.
     */
    public int quantityDropped(Random random) {
        return 0;
    }

    public Item getItemDropped(int meta, Random random, int fortune) {
        return null;
    }

    /**
     * Gets an item for the block being called on. Args: world, x, y, z
     */
    @SideOnly(Side.CLIENT)
    public Item getItem(World worldIn, int x, int y, int z) {
        return TFItems.experiment115;
    }

}
