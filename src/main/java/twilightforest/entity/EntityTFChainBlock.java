package twilightforest.entity;

import java.lang.ref.WeakReference;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityMultiPart;
import net.minecraft.entity.boss.EntityDragonPart;
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

import twilightforest.item.ItemTFChainBlock;
import twilightforest.item.TFItems;

public class EntityTFChainBlock extends EntityThrowable implements IEntityMultiPart {

    private static final int MAX_SMASH = 12;
    private static final int MAX_CHAIN = 16;
    private boolean isReturning = false;
    private int blocksSmashed = 0;

    // where are we headed?
    private double velX;
    private double velY;
    private double velZ;

    // for the client to show the chain
    private EntityLivingBase attachedTo;
    private WeakReference<ItemStack> launchingStack;

    public EntityTFGoblinChain chain1;
    public EntityTFGoblinChain chain2;
    public EntityTFGoblinChain chain3;
    public EntityTFGoblinChain chain4;
    public EntityTFGoblinChain chain5;
    public Entity[] partsArray;

    public EntityTFChainBlock(World par1World) {
        super(par1World);

        this.setSize(0.6F, 0.6F);

        this.initializeChainParts();
    }

    public EntityTFChainBlock(World par1World, double par2, double par4, double par6) {
        super(par1World, par2, par4, par6);
        this.setSize(0.6F, 0.6F);
        this.initializeChainParts();
    }

    public EntityTFChainBlock(World par1World, EntityLivingBase par2EntityLiving) {
        super(par1World, par2EntityLiving);

        this.setSize(0.6F, 0.6F);
        this.initializeChainParts();

        this.isReturning = false;
        if (par2EntityLiving instanceof EntityPlayer player) {
            ItemStack equipped = player.getCurrentEquippedItem();
            if (equipped != null && equipped.getItem() == TFItems.chainBlock) {
                this.launchingStack = new WeakReference<>(equipped);
            }
        }
    }

    private void initializeChainParts() {
        this.partsArray = new Entity[] { chain1 = new EntityTFGoblinChain(this), chain2 = new EntityTFGoblinChain(this),
                chain3 = new EntityTFGoblinChain(this), chain4 = new EntityTFGoblinChain(this),
                chain5 = new EntityTFGoblinChain(this) };
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        this.dataWatcher.addObject(16, 0);
    }

    /**
     * Similar to setArrowHeading, it's point the throwable entity to a x, y, z direction.
     */
    public void setThrowableHeading(double x, double y, double z, float speed, float accuracy) {
        super.setThrowableHeading(x, y, z, speed, accuracy);

        // save velocity
        this.velX = this.motionX;
        this.velY = this.motionY;
        this.velZ = this.motionZ;
    }

    /**
     * How much this entity falls each tick
     */
    @Override
    protected float getGravityVelocity() {
        return 0.05F;
    }

    @Override
    protected void onImpact(MovingObjectPosition mop) {
        // only hit living things
        if (!this.worldObj.isRemote && mop.entityHit instanceof EntityLivingBase
                && mop.entityHit != this.getThrower()
                && this.getThrower() != null) {
            if (mop.entityHit.attackEntityFrom(DamageSource.causeThrownDamage(this, this.getThrower()), 10.0F)) {
                // age when we hit a monster so that we go back to the player faster
                this.ticksExisted += 60;
            }
        }

        if (mop.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK
                && !this.worldObj.isAirBlock(mop.blockX, mop.blockY, mop.blockZ)) {

            // clang!
            if (!this.isReturning) {
                this.worldObj.playSoundAtEntity(this, "random.anvil_land", 0.125f, this.rand.nextFloat());
            }

            if (!this.worldObj.isRemote && this.blocksSmashed < MAX_SMASH) {
                if (this.worldObj.getBlock(mop.blockX, mop.blockY, mop.blockZ)
                        .getBlockHardness(worldObj, mop.blockX, mop.blockY, mop.blockZ) > 0.3F) {
                    // riccochet
                    double bounce = 0.6;
                    this.velX *= bounce;
                    this.velY *= bounce;
                    this.velZ *= bounce;

                    switch (mop.sideHit) {
                        case 0 -> {
                            if (this.velY > 0) {
                                this.velY *= -bounce;
                            }
                        }
                        case 1 -> {
                            if (this.velY < 0) {
                                this.velY *= -bounce;
                            }
                        }
                        case 2 -> {
                            if (this.velZ > 0) {
                                this.velZ *= -bounce;
                            }
                        }
                        case 3 -> {
                            if (this.velZ < 0) {
                                this.velZ *= -bounce;
                            }
                        }
                        case 4 -> {
                            if (this.velX > 0) {
                                this.velX *= -bounce;
                            }
                        }
                        case 5 -> {
                            if (this.velX < 0) {
                                this.velX *= -bounce;
                            }
                        }
                    }
                }

                // demolish some blocks
                this.affectBlocksInAABB(this.boundingBox, this.getThrower());
            }

            // head back to owner
            if (!this.worldObj.isRemote) {
                this.isReturning = true;
            }

            // if we have smashed enough, add to ticks so that we go back faster
            if (this.blocksSmashed >= MAX_SMASH && this.ticksExisted < 60) {
                this.ticksExisted += 60;
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
                for (int dz = minZ; dz <= maxZ && this.blocksSmashed < MAX_SMASH; ++dz) {
                    Block block = this.worldObj.getBlock(dx, dy, dz);
                    int currentMeta = this.worldObj.getBlockMetadata(dx, dy, dz);

                    if (block != Blocks.air && block.getExplosionResistance(this) < 7F
                            && block.getBlockHardness(worldObj, dx, dy, dz) >= 0) {

                        if (!(entity instanceof EntityPlayer player) || !this.worldObj.canMineBlock(player, dx, dy, dz)
                                || !player.canPlayerEdit(dx, dy, dz, 1, player.getCurrentEquippedItem())) {
                            continue;
                        }
                        net.minecraftforge.event.world.BlockEvent.BreakEvent event = new net.minecraftforge.event.world.BlockEvent.BreakEvent(
                                dx,
                                dy,
                                dz,
                                this.worldObj,
                                block,
                                currentMeta,
                                player);
                        if (net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(event)) {
                            continue;
                        }
                        if (!this.worldObj.setBlockToAir(dx, dy, dz)) {
                            continue;
                        }
                        if (block.canHarvestBlock(player, currentMeta)) {
                            block.harvestBlock(this.worldObj, player, dx, dy, dz, currentMeta);
                        }
                        worldObj.playAuxSFX(2001, dx, dy, dz, Block.getIdFromBlock(block) + (currentMeta << 12));
                        ++this.blocksSmashed;
                        hitBlock = true;
                    }
                }
            }
        }

        return hitBlock;
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
        if (!this.worldObj.isRemote && this.getThrower() != null) {
            int ownerId = this.getThrower().getEntityId();
            if (this.dataWatcher.getWatchableObjectInt(16) != ownerId) {
                this.dataWatcher.updateObject(16, ownerId);
            }
        }

        // chains are probably always null on the server
        if (this.chain1 != null) {
            chain1.onUpdate();
            chain2.onUpdate();
            chain3.onUpdate();
            chain4.onUpdate();
            chain5.onUpdate();
        }

        if (this.getThrower() == null && !this.worldObj.isRemote) {
            this.setDead();
            return;
        }

        if (!this.worldObj.isRemote && this.getThrower() != null) {
            float distToPlayer = this.getDistanceToEntity(this.getThrower());
            // return if far enough away
            if (!this.isReturning && distToPlayer > MAX_CHAIN) {
                this.isReturning = true;
            }

            // despawn if close enough
            if (this.isReturning && distToPlayer < 1F) {
                // System.out.println("we have returned after smashing " + this.blocksSmashed + " blocks");
                ItemStack original = this.launchingStack == null ? null : this.launchingStack.get();
                if (original != null && original.getItem() == TFItems.chainBlock) {
                    ItemTFChainBlock.setChainAsReturned(original);
                } else if (this.getThrower() instanceof EntityPlayer player) {
                    ItemTFChainBlock.setChainAsReturned(player);
                }
                this.setDead();
                return;
            }
        }

        // if we are returning, set course for the player
        if (this.isReturning && !this.worldObj.isRemote && this.getThrower() != null) {

            EntityLivingBase returnTo = this.getThrower();

            Vec3 back = Vec3.createVectorHelper(
                    returnTo.posX - this.posX,
                    returnTo.posY + (double) returnTo.getEyeHeight() - 1.200000023841858D - this.posY,
                    returnTo.posZ - this.posZ).normalize();

            float age = Math.min(this.ticksExisted * 0.03F, 1.0F);

            // separate the return velocity from the normal bouncy velocity
            this.motionX = this.velX * (1.0 - age) + (back.xCoord * 2F * age);
            this.motionY = this.velY * (1.0 - age) + (back.yCoord * 2F * age) - this.getGravityVelocity();
            this.motionZ = this.velZ * (1.0 - age) + (back.zCoord * 2F * age);

        }

        // use the synced thrower ID, not an arbitrary nearby player in multiplayer
        if (this.worldObj.isRemote && (this.attachedTo == null || this.attachedTo.isDead)) {
            Entity owner = this.worldObj.getEntityByID(this.dataWatcher.getWatchableObjectInt(16));
            this.attachedTo = owner instanceof EntityLivingBase ? (EntityLivingBase) owner : null;
        }

        // set chain positions, client only
        if (this.worldObj.isRemote && this.attachedTo != null) {
            // interpolate chain position
            Vec3 handVec = this.attachedTo.getLookVec();

            handVec.rotateAroundY(-0.4F);

            double sx = this.attachedTo.posX + handVec.xCoord;
            double sy = this.attachedTo.posY + handVec.yCoord - 0.6F;
            double sz = this.attachedTo.posZ + handVec.zCoord;

            double ox = sx - this.posX;
            double oy = sy - this.posY - 0.25F;
            double oz = sz - this.posZ;

            this.chain1.setPosition(sx - ox * 0.05, sy - oy * 0.05, sz - oz * 0.05);
            this.chain2.setPosition(sx - ox * 0.25, sy - oy * 0.25, sz - oz * 0.25);
            this.chain3.setPosition(sx - ox * 0.45, sy - oy * 0.45, sz - oz * 0.45);
            this.chain4.setPosition(sx - ox * 0.65, sy - oy * 0.65, sz - oz * 0.65);
            this.chain5.setPosition(sx - ox * 0.85, sy - oy * 0.85, sz - oz * 0.85);
        }
    }

    /**
     * Velocity
     */
    protected float func_70182_d() {
        return 1.5F;
    }

    @Override
    public World func_82194_d() {
        return this.worldObj;
    }

    @Override
    public boolean attackEntityFromPart(EntityDragonPart p_70965_1_, DamageSource p_70965_2_, float p_70965_3_) {
        return false;
    }

    /**
     * We need to do this for the bounding boxes on the parts to become active
     */
    @Override
    public Entity[] getParts() {
        return partsArray;
    }

}
