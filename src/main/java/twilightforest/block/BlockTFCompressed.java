package twilightforest.block;

import static net.minecraftforge.common.util.ForgeDirection.UP;

import net.minecraft.block.BlockCompressed;
import net.minecraft.block.material.MapColor;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import twilightforest.TwilightForestMod;
import twilightforest.client.renderer.BetterIconFlipped;
import twilightforest.item.TFItems;

public class BlockTFCompressed extends BlockCompressed {

    public enum BlockType {
        ARCTIC_FUR,
        CARMINITE,
        FIERY_METAL,
        IRONWOOD,
        STEELEAF
    }

    public BlockType type;

    public IIcon[] fieryPattern;
    public IIcon[] fieryCore; // 0 - single, 1 - horizontal, 2 - vertical, 3 - cross, 4 - full

    protected BlockTFCompressed(BlockType blockType) {
        super(typeToMapColor(blockType));
        type = blockType;
        float hardness = 0;
        float resistance = 0;
        SoundType stepSound = null;
        String textureName = "";

        switch (type) {
            case ARCTIC_FUR:
                hardness = 0.8F;
                stepSound = soundTypeCloth;
                textureName = "arctic_fur_block";
                break;
            case CARMINITE:
                hardness = 5.0F;
                resistance = 10.0F;
                stepSound = soundTypeMetal;
                textureName = "carminite_block";
                break;
            case FIERY_METAL:
                this.setLightLevel(0.5F);
                hardness = 5.0F;
                resistance = 10.0F;
                stepSound = soundTypeMetal;
                textureName = "fiery_block";
                break;
            default:
            case IRONWOOD:
                hardness = 5.0F;
                resistance = 10.0F;
                stepSound = soundTypeMetal;
                textureName = "ironwood_block";
                break;
            case STEELEAF:
                hardness = 5.0F;
                resistance = 10.0F;
                stepSound = soundTypeGrass;
                textureName = "steeleaf_block";
                break;
        }

        this.setHardness(hardness);
        this.setResistance(resistance);
        this.setStepSound(stepSound);
        this.setBlockTextureName(TwilightForestMod.ID + ":" + textureName);

        this.setCreativeTab(TFItems.creativeTab);
    }

    private static MapColor typeToMapColor(BlockType blockType) {
        MapColor mapColor = null;
        switch (blockType) {
            case ARCTIC_FUR:
                mapColor = MapColor.clothColor;
                break;
            case CARMINITE:
                mapColor = MapColor.tntColor;
                break;
            case FIERY_METAL:
                mapColor = MapColor.netherrackColor;
                break;
            case IRONWOOD:
                mapColor = MapColor.ironColor;
                break;
            case STEELEAF:
                mapColor = MapColor.foliageColor;
                break;
        }
        return mapColor;
    }

    /**
     * Returns a bounding box from the pool of bounding boxes (this means this box can change after the pool has been
     * cleared to be reused)
     */
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World worldIn, int x, int y, int z) {
        if (this.type == BlockType.FIERY_METAL) return AxisAlignedBB.getBoundingBox(
                (double) x,
                (double) y,
                (double) z,
                (double) (x + 1),
                (double) (y + 0.99f),
                (double) (z + 1));
        else return super.getCollisionBoundingBoxFromPool(worldIn, x, y, z);
    }

    /**
     * Triggered whenever an entity collides with this block (enters into the block). Args: world, x, y, z, entity
     */
    public void onEntityCollidedWithBlock(World worldIn, int x, int y, int z, Entity entityIn) {
        if (this.type == BlockType.FIERY_METAL) {
            if (entityIn.isSneaking()) return;
            if (entityIn instanceof EntityPlayer && ((EntityPlayer) entityIn).inventory.armorInventory[0] != null
                    && ((EntityPlayer) entityIn).inventory.armorInventory[0].getItem() == TFItems.fieryBoots)
                return;
            if (entityIn instanceof EntityLivingBase && ((EntityLivingBase) entityIn).getEquipmentInSlot(1) != null
                    && ((EntityLivingBase) entityIn).getEquipmentInSlot(1).getItem() == TFItems.fieryBoots)
                return;
            entityIn.attackEntityFrom(DamageSource.inFire, 1);
        }
    }

    /**
     * Block's chance to react to an entity falling on it.
     */
    @Override
    public void onFallenUpon(World worldIn, int x, int y, int z, Entity entityIn, float fallDistance) {
        switch (type) {
            case ARCTIC_FUR:
                entityIn.fallDistance *= 0.25;
                break;
            case STEELEAF:
                entityIn.fallDistance *= 0.75;
                break;
            default:
                break;
        }
    }

    /**
     * Determines if this block can be used as the base of a beacon.
     *
     * @param world   The current world
     * @param x       X Position
     * @param y       Y Position
     * @param z       Z position
     * @param beaconX Beacons X Position
     * @param beaconY Beacons Y Position
     * @param beaconZ Beacons Z Position
     * @return True, to support the beacon, and make it active with this block.
     */
    public boolean isBeaconBase(IBlockAccess worldObj, int x, int y, int z, int beaconX, int beaconY, int beaconZ) {
        return true;
    }

    /**
     * Currently only called by fire when it is on top of this block. Returning true will prevent the fire from
     * naturally dying during updating. Also prevents firing from dying from rain.
     *
     * @param world    The current world
     * @param x        The blocks X position
     * @param y        The blocks Y position
     * @param z        The blocks Z position
     * @param metadata The blocks current metadata
     * @param side     The face that the fire is coming from
     * @return True if this block sustains fire, meaning it will never go out.
     */
    public boolean isFireSource(World world, int x, int y, int z, ForgeDirection side) {
        if (this.type == BlockType.FIERY_METAL && side == UP) return true;
        return false;
    }

    /**
     * How bright to render this block based on the light its receiving. Args: iBlockAccess, x, y, z
     */
    @SideOnly(Side.CLIENT)
    public int getMixedBrightnessForBlock(IBlockAccess worldIn, int x, int y, int z) {
        if (this.type == BlockType.FIERY_METAL) return 255;
        return super.getMixedBrightnessForBlock(worldIn, x, y, z);
    }

    /**
     * Returns a integer with hex for 0xrrggbb with this color multiplied against the blocks color. Note only called
     * when first determining what to render.
     */
    @SideOnly(Side.CLIENT)
    public int colorMultiplier(IBlockAccess worldIn, int x, int y, int z) {
        if (this.type == BlockType.FIERY_METAL) return 0xffffff;
        return super.colorMultiplier(worldIn, x, y, z);
    }

    public boolean canConnectFieryTo(IBlockAccess worldIn, int x, int y, int z) {
        return worldIn.getBlock(x, y, z) == TFBlocks.fieryMetalStorage;
    }

    /**
     * If this block doesn't render as an ordinary block it will return False (examples: signs, buttons, stairs, etc)
     */
    @Override
    public boolean renderAsNormalBlock() {
        return this.type != BlockType.FIERY_METAL;
    }

    /**
     * Is this block (a) opaque and (b) a full 1m cube? This determines whether or not to render the shared face of two
     * adjacent blocks and also whether the player can attach torches, redstone wire, etc to this block.
     */
    @Override
    public boolean isOpaqueCube() {
        return true;
    }

    /**
     * Return true if the block is a normal, solid cube. This determines indirect power state, entity ejection from
     * blocks, and a few others.
     *
     * @param world The current world
     * @param x     X Position
     * @param y     Y position
     * @param z     Z position
     * @return True if the block is a full cube
     */
    @Override
    public boolean isNormalCube(IBlockAccess world, int x, int y, int z) {
        return true;
    }

    /**
     * Checks if the block is a solid face on the given side, used by placement logic.
     *
     * @param world The current world
     * @param x     X Position
     * @param y     Y position
     * @param z     Z position
     * @param side  The side to check
     * @return True if the block is solid on the specified side.
     */
    @Override
    public boolean isSideSolid(IBlockAccess world, int x, int y, int z, ForgeDirection side) {
        if (this.type == BlockType.FIERY_METAL) return side == ForgeDirection.UP;
        return true;
    }

    /**
     * The type of render function that is called for this block
     */
    @Override
    public int getRenderType() {
        if (this.type == BlockType.FIERY_METAL) return TwilightForestMod.proxy.getFieryMetalBlockRenderID();
        else return super.getRenderType();
    }

    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister reg) {
        this.fieryPattern = new IIcon[4];
        this.fieryPattern[0] = reg.registerIcon(TwilightForestMod.ID + ":fiery_pattern");
        this.fieryPattern[1] = new BetterIconFlipped(this.fieryPattern[0], true, false);
        this.fieryPattern[2] = new BetterIconFlipped(this.fieryPattern[0], false, true);
        this.fieryPattern[3] = new BetterIconFlipped(this.fieryPattern[0], true, true);

        this.fieryCore = new IIcon[5];
        this.fieryCore[0] = reg.registerIcon(TwilightForestMod.ID + ":fiery_block_inner");
        this.fieryCore[1] = reg.registerIcon(TwilightForestMod.ID + ":fiery_block_inner_horizontal");
        this.fieryCore[2] = reg.registerIcon(TwilightForestMod.ID + ":fiery_block_inner_vertical");
        this.fieryCore[3] = reg.registerIcon(TwilightForestMod.ID + ":fiery_block_inner_cross");
        this.fieryCore[4] = reg.registerIcon(TwilightForestMod.ID + ":fiery_block_inner_full");
        super.registerBlockIcons(reg);
    }

}
