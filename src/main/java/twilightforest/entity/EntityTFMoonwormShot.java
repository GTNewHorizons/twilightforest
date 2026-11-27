package twilightforest.entity;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.world.World;
import net.minecraftforge.common.util.BlockSnapshot;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.event.ForgeEventFactory;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import twilightforest.block.TFBlocks;
import twilightforest.item.TFItems;

public class EntityTFMoonwormShot extends EntityThrowable {

    public EntityTFMoonwormShot(World par1World, double par2, double par4, double par6) {
        super(par1World, par2, par4, par6);
    }

    public EntityTFMoonwormShot(World par1World, EntityLivingBase par2EntityLiving) {
        super(par1World, par2EntityLiving);
    }

    public EntityTFMoonwormShot(World par1World) {
        super(par1World);
    }

    protected float func_40077_c() {
        return 0.5F;
    }

    /**
     * Gets how bright this entity is.
     */
    @Override
    public float getBrightness(float par1) {
        return 1.0F;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int getBrightnessForRender(float par1) {
        return 15728880;
    }

    public void makeTrail() {}

    /**
     * Returns true if other Entities should be prevented from moving through this Entity.
     */
    @Override
    public boolean canBeCollidedWith() {
        return true;
    }

    /**
     * We need to set this so that the player can attack and reflect the bolt
     */
    @Override
    public float getCollisionBorderSize() {
        return 1.0F;
    }

    /**
     * How much this entity falls each tick
     */
    @Override
    protected float getGravityVelocity() {
        return 0.03F;
    }

    private void placeMoonworm(EntityPlayer player, MovingObjectPosition impact) {
        int x = impact.blockX;
        int y = impact.blockY;
        int z = impact.blockZ;
        int side = impact.sideHit;
        Block currentBlock = this.worldObj.getBlock(x, y, z);
        if (currentBlock == TFBlocks.moonworm) {
            return;
        }

        if (currentBlock == Blocks.snow) {
            side = 1;
        } else if (currentBlock != Blocks.vine && currentBlock != Blocks.tallgrass
                && currentBlock != Blocks.deadbush
                && (currentBlock == Blocks.air || !currentBlock.isReplaceable(this.worldObj, x, y, z))) {
                    switch (side) {
                        case 0 -> --y;
                        case 1 -> ++y;
                        case 2 -> --z;
                        case 3 -> ++z;
                        case 4 -> --x;
                        case 5 -> ++x;
                        default -> {
                            return;
                        }
                    }
                }

        // the fired shot already paid its charge, this stack is for placement permissions only
        ItemStack placementStack = new ItemStack(TFItems.moonwormQueen);
        if (!player.canPlayerEdit(x, y, z, side, placementStack) || !this.worldObj.canMineBlock(player, x, y, z)
                || !this.worldObj
                        .canPlaceEntityOnSide(TFBlocks.moonworm, x, y, z, false, side, player, placementStack)) {
            return;
        }

        BlockSnapshot snapshot = BlockSnapshot.getBlockSnapshot(this.worldObj, x, y, z);
        int metadata = TFBlocks.moonworm.onBlockPlaced(this.worldObj, x, y, z, side, 0, 0, 0, 0);
        if (!this.worldObj.setBlock(x, y, z, TFBlocks.moonworm, metadata, 3)) {
            return;
        }
        if (ForgeEventFactory.onPlayerBlockPlace(player, snapshot, ForgeDirection.getOrientation(side)).isCanceled()) {
            snapshot.restore(true, false);
            return;
        }
        if (this.worldObj.getBlock(x, y, z) != TFBlocks.moonworm) {
            return;
        }

        TFBlocks.moonworm.onBlockPlacedBy(this.worldObj, x, y, z, player, placementStack);
        this.worldObj.playSoundEffect(
                x + 0.5D,
                y + 0.5D,
                z + 0.5D,
                "mob.slime.big",
                TFBlocks.moonworm.stepSound.getVolume() / 2.0F,
                TFBlocks.moonworm.stepSound.getPitch() * 0.8F);
    }

    @Override
    protected void onImpact(MovingObjectPosition impact) {
        if (this.isDead) {
            return;
        }

        if (!this.worldObj.isRemote) {
            if (impact.typeOfHit == MovingObjectType.BLOCK && this.getThrower() instanceof EntityPlayer player) {
                this.placeMoonworm(player, impact);
            }
            if (impact.entityHit != null) {
                // preserve the projectiles existing zero damage interaction with entities
                impact.entityHit.attackEntityFrom(DamageSource.causeThrownDamage(this, this.getThrower()), 0);
            }
            this.setDead();
            return;
        }

        for (int i = 0; i < 8; ++i) {
            this.worldObj.spawnParticle(
                    "blockcrack_" + Block.getIdFromBlock(TFBlocks.moonworm) + "_0",
                    this.posX,
                    this.posY,
                    this.posZ,
                    0.0D,
                    0.0D,
                    0.0D);
        }
    }

}
