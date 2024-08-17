package twilightforest.block;

import net.minecraft.block.BlockChest;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import twilightforest.TwilightForestMod;
import twilightforest.item.TFItems;
import twilightforest.tileentity.TileEntityTFChest;

public class BlockTFChest extends BlockChest {

    public enum WoodType {
        TWILIGHT,
        CANOPY,
        MANGROVE,
        DARKWOOD,
        TIME,
        TRANS,
        MINING,
        SORT,
        NULL
    }

    private WoodType material = WoodType.CANOPY;

    protected BlockTFChest(int type, WoodType material) {
        super(type);
        this.material = material;
        this.setHardness(2.5F);
        this.setStepSound(soundTypeWood);
        this.setCreativeTab(TFItems.creativeTab);
    }

    public WoodType getWoodType() {
        return material;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister reg) {
        this.blockIcon = reg
                .registerIcon(TwilightForestMod.ID + ":wood/planks_" + BlockTFWood.names[material.ordinal()] + "_0");
    }

    /**
     * The type of render function that is called for this block
     */
    @Override
    public int getRenderType() {
        return TwilightForestMod.proxy.getChestBlockRenderID();
    }

    /**
     * Returns a new instance of a block's tile entity class. Called on placing the block.
     */
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        TileEntityTFChest tileentitychest = new TileEntityTFChest();
        return tileentitychest;
    }

    /**
     * Called when a tile entity on a side of this block changes is created or is destroyed.
     * 
     * @param world The world
     * @param x     The x position of this block instance
     * @param y     The y position of this block instance
     * @param z     The z position of this block instance
     * @param tileX The x position of the tile that changed
     * @param tileY The y position of the tile that changed
     * @param tileZ The z position of the tile that changed
     */
    public void onNeighborChange(IBlockAccess world, int x, int y, int z, int tileX, int tileY, int tileZ) {
        super.onNeighborChange(world, x, y, z, tileX, tileY, tileZ);
        TileEntityChest tileentitychest = (TileEntityChest) world.getTileEntity(x, y, z);

        if (tileentitychest != null) {
            tileentitychest.updateContainingBlockInfo();
        }
    }

}
