package twilightforest.entity;

import java.lang.ref.WeakReference;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.internal.FMLProxyPacket;
import twilightforest.TFGenericPacketHandler;
import twilightforest.TwilightForestMod;
import twilightforest.block.TFBlocks;
import twilightforest.item.ItemTFCubeOfAnnihilation;
import twilightforest.item.TFItems;

public class EntityTFCubeOfAnnihilation extends EntityThrowable {

    boolean hasHitObstacle = false;
    private WeakReference<ItemStack> launchingStack;

    public EntityTFCubeOfAnnihilation(World par1World) {
        super(par1World);

        this.setSize(1.1F, 1F);
        this.isImmuneToFire = true;

    }

    public EntityTFCubeOfAnnihilation(World par1World, double par2, double par4, double par6) {
        super(par1World, par2, par4, par6);
        this.setSize(1F, 1F);
        this.isImmuneToFire = true;
    }

    public EntityTFCubeOfAnnihilation(World par1World, EntityLivingBase par2EntityLiving) {
        super(par1World, par2EntityLiving);

        this.setSize(1F, 1F);
        this.isImmuneToFire = true;
        if (par2EntityLiving instanceof EntityPlayer player) {
            ItemStack equipped = player.getCurrentEquippedItem();
            if (equipped != null && equipped.getItem() == TFItems.cubeOfAnnihilation) {
                this.launchingStack = new WeakReference<>(equipped);
            }
        }
    }

    /**
     * How much this entity falls each tick
     */
    @Override
    protected float getGravityVelocity() {
        return 0F;
    }

    @Override
    protected void onImpact(MovingObjectPosition mop) {
        // only hit living things
        if (!this.worldObj.isRemote && mop.entityHit instanceof EntityLivingBase
                && mop.entityHit != this.getThrower()
                && this.getThrower() != null) {
            if (mop.entityHit.attackEntityFrom(DamageSource.causeThrownDamage(this, this.getThrower()), 10.0F)) {
                this.ticksExisted += 60;
            }
        }

        if (mop.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK
                && !this.worldObj.isAirBlock(mop.blockX, mop.blockY, mop.blockZ)) {

            // demolish some blocks
            if (!this.worldObj.isRemote) {
                this.affectBlocksInAABB(this.boundingBox.expand(0.2F, 0.2F, 0.2F), this.getThrower());
            }

        }

    }

    /**
     * Do our ball and chain effect on blocks we hit. Harvest/destroy/whatevs
     *
     * @param entity
     */
    private boolean affectBlocksInAABB(AxisAlignedBB par1AxisAlignedBB, EntityLivingBase entity) {
        // System.out.println("Destroying blocks in " + par1AxisAlignedBB);

        int minX = MathHelper.floor_double(par1AxisAlignedBB.minX);
        int minY = MathHelper.floor_double(par1AxisAlignedBB.minY);
        int minZ = MathHelper.floor_double(par1AxisAlignedBB.minZ);
        int maxX = MathHelper.floor_double(par1AxisAlignedBB.maxX);
        int maxY = MathHelper.floor_double(par1AxisAlignedBB.maxY);
        int maxZ = MathHelper.floor_double(par1AxisAlignedBB.maxZ);
        boolean hitBlock = false;
        for (int dx = minX; dx <= maxX; ++dx) {
            for (int dy = minY; dy <= maxY; ++dy) {
                for (int dz = minZ; dz <= maxZ; ++dz) {
                    Block block = this.worldObj.getBlock(dx, dy, dz);
                    int currentMeta = this.worldObj.getBlockMetadata(dx, dy, dz);

                    if (block != Blocks.air) {
                        if (canAnnihilate(dx, dy, dz, block, currentMeta)
                                && canRemoveBlock(dx, dy, dz, block, currentMeta)
                                && this.worldObj.setBlockToAir(dx, dy, dz)) {
                            this.worldObj.playSoundAtEntity(
                                    this,
                                    "random.fizz",
                                    0.125F,
                                    this.rand.nextFloat() * 0.25F + 0.75F);
                            this.sendAnnihilateBlockPacket(worldObj, dx, dy, dz);
                        } else {
                            // blocked or protected blocks force the cube back to its owner
                            this.hasHitObstacle = true;
                        }
                        hitBlock = true;
                    }
                }
            }
        }

        return hitBlock;
    }

    private boolean canRemoveBlock(int x, int y, int z, Block block, int metadata) {
        if (!(this.getThrower() instanceof EntityPlayer player)) {
            return false;
        }
        if (!this.worldObj.canMineBlock(player, x, y, z)
                || !player.canPlayerEdit(x, y, z, 1, player.getCurrentEquippedItem())) {
            return false;
        }
        net.minecraftforge.event.world.BlockEvent.BreakEvent event = new net.minecraftforge.event.world.BlockEvent.BreakEvent(
                x,
                y,
                z,
                this.worldObj,
                block,
                metadata,
                player);
        return !net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(event);
    }

    private boolean canAnnihilate(int dx, int dy, int dz, Block block, int meta) {
        // whitelist many castle blocks
        if (block == TFBlocks.deadrock || block == TFBlocks.castleBlock
                || (block == TFBlocks.castleMagic && meta != 3)
                || block == TFBlocks.forceField
                || block == TFBlocks.thorns) {
            return true;
        }

        return block.getExplosionResistance(this) < 8F && block.getBlockHardness(worldObj, dx, dy, dz) >= 0;
    }

    private void sendAnnihilateBlockPacket(World world, int x, int y, int z) {
        // send packet
        FMLProxyPacket message = TFGenericPacketHandler.makeAnnihilateBlockPacket(x, y, z);

        NetworkRegistry.TargetPoint targetPoint = new NetworkRegistry.TargetPoint(
                world.provider.dimensionId,
                x,
                y,
                z,
                64);

        TwilightForestMod.genericChannel.sendToAllAround(message, targetPoint);
    }

    /**
     * Skip most of the living update things
     */
    @Override
    public void onUpdate() {
        super.onUpdate();
        if (this.isDead) {
            return;
        }

        // all server side
        if (!this.worldObj.isRemote) {

            if (this.getThrower() == null) {
                this.setDead();
                return;
            }

            if (this.isReturning()) {
                // if we are returning, and are near enough to the player, then we are done
                List<Entity> list = this.worldObj.getEntitiesWithinAABBExcludingEntity(
                        this,
                        this.boundingBox.addCoord(this.motionX, this.motionY, this.motionZ).expand(1.0D, 1.0D, 1.0D));

                if (list.contains(this.getThrower())) {
                    // System.out.println("we have returned");
                    ItemStack original = this.launchingStack == null ? null : this.launchingStack.get();
                    if (original != null && original.getItem() == TFItems.cubeOfAnnihilation) {
                        ItemTFCubeOfAnnihilation.setCubeAsReturned(original);
                    } else if (this.getThrower() instanceof EntityPlayer player) {
                        ItemTFCubeOfAnnihilation.setCubeAsReturned(player);
                    }
                    this.setDead();
                    return;
                }
            }

            // always head towards either the point or towards the player
            Vec3 destPoint = Vec3.createVectorHelper(
                    this.getThrower().posX,
                    this.getThrower().posY + this.getThrower().getEyeHeight(),
                    this.getThrower().posZ);

            if (!this.isReturning()) {
                Vec3 look = this.getThrower().getLookVec();

                float dist = 16F;

                look.xCoord *= dist;
                look.yCoord *= dist;
                look.zCoord *= dist;

                destPoint.xCoord += look.xCoord;
                destPoint.yCoord += look.yCoord;
                destPoint.zCoord += look.zCoord;
            }

            // System.out.println("Dest point = " + destPoint);

            // set motions
            Vec3 velocity = Vec3.createVectorHelper(
                    this.posX - destPoint.xCoord,
                    (this.posY + this.height / 2F) - destPoint.yCoord,
                    this.posZ - destPoint.zCoord);

            this.motionX -= velocity.xCoord;
            this.motionY -= velocity.yCoord;
            this.motionZ -= velocity.zCoord;

            // normalize speed
            float currentSpeed = MathHelper.sqrt_double(
                    this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ);

            float maxSpeed = 0.5F;

            if (currentSpeed > maxSpeed) {
                this.motionX /= currentSpeed / maxSpeed;
                this.motionY /= currentSpeed / maxSpeed;
                this.motionZ /= currentSpeed / maxSpeed;
            } else {
                float slow = 0.5F;
                this.motionX *= slow;
                this.motionY *= slow;
                this.motionZ *= slow;
            }

            // demolish some blocks
            this.affectBlocksInAABB(this.boundingBox.expand(0.2F, 0.2F, 0.2F), this.getThrower());

        }

    }

    public boolean isReturning() {
        if (this.hasHitObstacle || this.getThrower() == null || !(this.getThrower() instanceof EntityPlayer player)) {
            return true;
        } else {

            return !player.isUsingItem();
        }
    }

    /**
     * Velocity
     */
    protected float func_70182_d() {
        return 1.5F;
    }

}
