package twilightforest.entity.ai;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.Vec3;

import cpw.mods.fml.common.network.internal.FMLProxyPacket;
import twilightforest.TFGenericPacketHandler;
import twilightforest.TwilightForestMod;

public class EntityAITFThrowRider extends EntityAIBase {

    private static final int MIN_HOLD_TICKS = 10;
    private static final int RECOVERY_TICKS = 40;
    private static final double THROW_SPEED = 2.0D;
    private static final double THROW_UPWARD_SPEED = 0.9D;

    private final EntityCreature creature;
    private EntityLivingBase heldRider;
    private int holdTicks;
    private int recoveryTicks;

    public EntityAITFThrowRider(EntityCreature creature, float ignoredSpeed) {
        this.creature = creature;
        this.setMutexBits(1);
    }

    @Override
    public boolean shouldExecute() {
        if (this.creature.worldObj.isRemote || !this.creature.isEntityAlive()
                || !(this.creature.riddenByEntity instanceof EntityLivingBase rider)
                || !rider.isEntityAlive()) {
            this.heldRider = null;
            this.holdTicks = 0;
            return false;
        }

        if (this.heldRider != rider) {
            this.heldRider = rider;
            this.holdTicks = 0;
        }
        if (++this.holdTicks < MIN_HOLD_TICKS) {
            return false;
        }
        return this.creature.getRNG().nextInt(5) == 0;
    }

    @Override
    public void startExecuting() {
        this.recoveryTicks = 0;
        if (this.heldRider == null || this.creature.riddenByEntity != this.heldRider
                || !this.heldRider.isEntityAlive()
                || this.creature.worldObj.isRemote) {
            return;
        }

        EntityLivingBase rider = this.heldRider;
        rider.mountEntity(null);
        if (rider.ridingEntity != null) {
            // another mod may veto dismounting, never launch a still mounted entity
            return;
        }

        // use horizontal facing so looking straight up/down cannot suppress the throw
        Vec3 look = this.creature.getLookVec();
        double horizontalSq = look.xCoord * look.xCoord + look.zCoord * look.zCoord;
        double dx;
        double dz;
        if (horizontalSq > 1.0E-6D) {
            double factor = THROW_SPEED / Math.sqrt(horizontalSq);
            dx = look.xCoord * factor;
            dz = look.zCoord * factor;
        } else {
            double yaw = Math.toRadians(this.creature.rotationYaw);
            dx = -Math.sin(yaw) * THROW_SPEED;
            dz = Math.cos(yaw) * THROW_SPEED;
        }

        rider.addVelocity(dx, THROW_UPWARD_SPEED, dz);
        if (rider instanceof EntityPlayerMP player) {
            FMLProxyPacket packet = TFGenericPacketHandler
                    .makeThrowPlayerPacket((float) dx, (float) THROW_UPWARD_SPEED, (float) dz);
            TwilightForestMod.genericChannel.sendTo(packet, player);
        }

        this.creature.playSound(
                TwilightForestMod.ID + ":mob.yeti.throw",
                4.0F,
                0.75F + this.creature.getRNG().nextFloat() * 0.25F);
        this.heldRider = null;
        this.holdTicks = 0;
        this.recoveryTicks = RECOVERY_TICKS;
    }

    @Override
    public boolean continueExecuting() {
        return !this.creature.isDead && this.recoveryTicks > 0;
    }

    @Override
    public void updateTask() {
        if (this.recoveryTicks > 0) {
            --this.recoveryTicks;
        }
    }

    @Override
    public void resetTask() {
        this.recoveryTicks = 0;
        this.heldRider = null;
        this.holdTicks = 0;
    }
}
