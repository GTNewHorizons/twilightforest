package twilightforest.block;

import static net.minecraftforge.common.util.ForgeDirection.EAST;
import static net.minecraftforge.common.util.ForgeDirection.NORTH;
import static net.minecraftforge.common.util.ForgeDirection.SOUTH;
import static net.minecraftforge.common.util.ForgeDirection.WEST;

import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import twilightforest.TwilightForestMod;
import twilightforest.item.TFItems;

public abstract class BlockTFCritter extends Block {

    protected Item dropItem = Items.glowstone_dust;
    protected int dropMeta = 0;

    protected BlockTFCritter() {
        super(Material.circuits);
        this.setHardness(0.0F);
        this.setCreativeTab(TFItems.creativeTab);

        this.stepSound = new StepSoundTFInsect("squish", 0.25F, 0.6F);
    }

    /**
     * Triggered whenever an entity collides with this block (enters into the block). Args: world, x, y, z, entity
     */
    public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity) {
        // squish
        if (!world.isRemote && (entity instanceof IProjectile)) {
            int metadata = world.getBlockMetadata(x, y, z);
            world.playAuxSFX(2001, x, y, z, Block.getIdFromBlock(this) + (metadata << 12));
            world.setBlockToAir(x, y, z);
            double dx = this.getBlockBoundsMinX()
                    + world.rand.nextDouble() * (this.getBlockBoundsMaxX() - this.getBlockBoundsMinX());
            double dy = this.getBlockBoundsMinY()
                    + world.rand.nextDouble() * (this.getBlockBoundsMaxY() - this.getBlockBoundsMinY());
            double dz = this.getBlockBoundsMinZ()
                    + world.rand.nextDouble() * (this.getBlockBoundsMaxZ() - this.getBlockBoundsMinZ());
            EntityItem entityitem = new EntityItem(world, x + dx, y + dy, z + dz, new ItemStack(dropItem, 1, dropMeta));
            entityitem.delayBeforeCanPickup = 10;
            world.spawnEntityInWorld(entityitem);
        }
    }

    // Updates the blocks bounds based on its current state. Args: world, x, y, z
    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z) {
        int facing = world.getBlockMetadata(x, y, z) & 7;
        float wide = 0.15F;
        switch (facing) {
            case 1 -> setBlockBounds(0.0F, 0.2F, 0.5F - wide, wide * 2.0F, 0.8F, 0.5F + wide);
            case 2 -> setBlockBounds(1.0F - wide * 2.0F, 0.2F, 0.5F - wide, 1.0F, 0.8F, 0.5F + wide);
            case 3 -> setBlockBounds(0.5F - wide, 0.2F, 0.0F, 0.5F + wide, 0.8F, wide * 2.0F);
            case 4 -> setBlockBounds(0.5F - wide, 0.2F, 1.0F - wide * 2.0F, 0.5F + wide, 0.8F, 1.0F);
            case 5 -> setBlockBounds(0.5F - wide, 0.0F, 0.2F, 0.5F + wide, wide * 2.0F, 0.8F);
            case 6 -> setBlockBounds(0.5F - wide, 1.0F - wide * 2.0F, 0.2F, 0.5F + wide, 1.0F, 0.8F);
            default -> {
                float f1 = 0.1F;
                setBlockBounds(0.5F - f1, 0.0F, 0.5F - f1, 0.5F + f1, 0.6F, 0.5F + f1);
            }
        }
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
        return null;
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

    // The type of render function that is called for this block
    @Override
    public int getRenderType() {
        return TwilightForestMod.proxy.getCritterBlockRenderID();
    }

    @Override
    public boolean canPlaceBlockAt(World world, int x, int y, int z) {
        if (canPlaceAt(world, x - 1, y, z)) {
            return true;
        } else if (canPlaceAt(world, x + 1, y, z)) {
            return true;
        } else if (canPlaceAt(world, x, y, z - 1)) {
            return true;
        } else if (canPlaceAt(world, x, y, z + 1)) {
            return true;
        } else if (canPlaceAt(world, x, y - 1, z)) {
            return true;
        } else if (canPlaceAt(world, x, y + 1, z)) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public int onBlockPlaced(World world, int x, int y, int z, int placementFacing, float par6, float par7, float par8,
            int meta) {
        switch (placementFacing) {
            case 0 -> {
                if (this.canPlaceAt(world, x, y + 1, z)) {
                    meta = 6;
                }
            }
            case 1 -> {
                if (this.canPlaceAt(world, x, y - 1, z)) {
                    meta = 5;
                }
            }
            case 2 -> {
                if (world.isSideSolid(x, y, z + 1, NORTH, true)) {
                    meta = 4;
                }
            }
            case 3 -> {
                if (world.isSideSolid(x, y, z - 1, SOUTH, true)) {
                    meta = 3;
                }
            }
            case 4 -> {
                if (world.isSideSolid(x + 1, y, z, WEST, true)) {
                    meta = 2;
                }
            }
            case 5 -> {
                if (world.isSideSolid(x - 1, y, z, EAST, true)) {
                    meta = 1;
                }
            }
        }
        return meta;
    }

    // Called whenever the block is added into the world. Args: world, x, y, z
    @Override
    public void onBlockAdded(World world, int x, int y, int z) {
        if (world.getBlockMetadata(x, y, z) == 0) {
            if (canPlaceAt(world, x - 1, y, z)) {
                world.setBlockMetadataWithNotify(x, y, z, 1, 2);
            } else if (canPlaceAt(world, x + 1, y, z)) {
                world.setBlockMetadataWithNotify(x, y, z, 2, 2);
            } else if (canPlaceAt(world, x, y, z - 1)) {
                world.setBlockMetadataWithNotify(x, y, z, 3, 2);
            } else if (canPlaceAt(world, x, y, z + 1)) {
                world.setBlockMetadataWithNotify(x, y, z, 4, 2);
            } else if (canPlaceAt(world, x, y - 1, z)) {
                world.setBlockMetadataWithNotify(x, y, z, 5, 2);
            } else if (canPlaceAt(world, x, y + 1, z)) {
                world.setBlockMetadataWithNotify(x, y, z, 6, 2);
            }
        }

        dropCritterIfCantStay(world, x, y, z);

        // for fireflies, schedule a lighting update
        int meta = world.getBlockMetadata(x, y, z);
        if (meta == 0) {
            world.scheduleBlockUpdate(x, y, z, this, tickRate(world));
        }
    }

    public boolean dropCritterIfCantStay(World world, int x, int y, int z) {
        if (!canPlaceBlockAt(world, x, y, z)) {
            dropBlockAsItem(world, x, y, z, world.getBlockMetadata(x, y, z), 0);
            world.setBlockToAir(x, y, z);
            return false;
        } else {
            return true;
        }
    }

    /**
     * Lets the block know when one of its neighbor changes. Doesn't know which neighbor changed (coordinates passed are
     * their own) Args: x, y, z, neighbor blockID
     */
    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, Block blockID) {
        if (dropCritterIfCantStay(world, x, y, z)) {
            int facing = world.getBlockMetadata(x, y, z) & 7;
            boolean flag = false;

            switch (facing) {
                case 1 -> {
                    if (!canPlaceAt(world, x - 1, y, z)) {
                        flag = true;
                    }
                }
                case 2 -> {
                    if (!canPlaceAt(world, x + 1, y, z)) {
                        flag = true;
                    }
                }
                case 3 -> {
                    if (!canPlaceAt(world, x, y, z - 1)) {
                        flag = true;
                    }
                }
                case 4 -> {
                    if (!canPlaceAt(world, x, y, z + 1)) {
                        flag = true;
                    }
                }
                case 5 -> {
                    if (!canPlaceAt(world, x, y - 1, z)) {
                        flag = true;
                    }
                }
                case 6 -> {
                    if (!canPlaceAt(world, x, y + 1, z)) {
                        flag = true;
                    }
                }
            }

            if (flag) {
                dropBlockAsItem(world, x, y, z, 0, 0);
                world.setBlockToAir(x, y, z);
            }
        }
    }

    // We can place fireflies on any normal block or on leaves.
    public boolean canPlaceAt(World world, int x, int y, int z) {
        Block block = world.getBlock(x, y, z);
        Material blockMaterial = block.getMaterial();
        return block.isNormalCube(world, x, y, z) || blockMaterial == Material.leaves
                || blockMaterial == Material.cactus;
    }

    /**
     * Called throughout the code as a replacement for block instanceof BlockContainer Moving this to the Block base
     * class allows for mods that wish to extend vinella blocks, and also want to have a tile entity on that block, may.
     * 
     * Return true from this function to specify this block has a tile entity.
     * 
     * @param metadata Metadata of the current block
     * @return True if block has a tile entity, false otherwise
     */
    @Override
    public boolean hasTileEntity(int metadata) {
        return true;
    }

    /**
     * Called throughout the code as a replacement for BlockContainer.getBlockEntity Return the same thing you would
     * from that function. This will fall back to BlockContainer.getBlockEntity if this block is a BlockContainer.
     * 
     * @param metadata The Metadata of the current block
     * @return A instance of a class extending TileEntity
     */
    @Override
    public abstract TileEntity createTileEntity(World world, int metadata);

    /**
     * returns a list of blocks with the same ID, but different meta (eg: wood returns 4 blocks)
     */
    @Override
    public void getSubBlocks(Item item, CreativeTabs par2CreativeTabs, List<ItemStack> itemList) {
        // itemList.add(new ItemStack(item, 1, 0));
        // We're using an item instead
    }

    @Override
    public Item getItemDropped(int meta, Random random, int fortune) {
        return TFItems.critter;
    }

    /**
     * Determines the damage on the item the block drops. Used in cloth and wood.
     */
    @Override
    public int damageDropped(int par1) {
        int metadata = 0;
        if (this == TFBlocks.cicada) metadata = 1;
        else if (this == TFBlocks.moonworm) metadata = 2;
        return metadata;
    }

    /**
     * Gets an item for the block being called on. Args: world, x, y, z
     */
    @SideOnly(Side.CLIENT)
    public Item getItem(World p_149694_1_, int p_149694_2_, int p_149694_3_, int p_149694_4_) {
        return TFItems.critter;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public int getDamageValue(World worldIn, int x, int y, int z) {
        return this.damageDropped(0);
    }

    /**
     * Called upon block activation (right click on the block.)
     */
    public boolean onBlockActivated(World worldIn, int x, int y, int z, EntityPlayer player, int side, float subX,
            float subY, float subZ) {
        if (player.isSneaking() && player.inventory.armorInventory[3] == null) {
            player.inventory.armorInventory[3] = new ItemStack(TFItems.critter, 1, this.damageDropped(0));
            if (!player.capabilities.isCreativeMode) worldIn.setBlockToAir(x, y, z);
            return true;
        }
        return false;
    }

    /**
     * Properly register icon source
     */
    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister par1IconRegister) {
        this.blockIcon = par1IconRegister
                .registerIcon(TwilightForestMod.ID + ":" + this.getUnlocalizedName().substring(5));
    }
}
