package twilightforest.entity.ai;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;

import twilightforest.entity.boss.EntityTFYetiAlpha;

public class EntityAITFYetiRampage extends EntityAIBase {

    private final EntityTFYetiAlpha yeti;
    private final int timeout;
    private final int duration;
    private int remainingCooldown;
    private int remainingDuration;
    private boolean started;

    public EntityAITFYetiRampage(EntityTFYetiAlpha yeti, int timeout, int duration) {
        this.yeti = yeti;
        this.timeout = Math.max(0, timeout);
        this.duration = Math.max(1, duration);
        this.remainingCooldown = this.timeout;
        this.setMutexBits(5);
    }

    @Override
    public boolean shouldExecute() {
        EntityLivingBase target = this.yeti.getAttackTarget();
        if (!this.yeti.isEntityAlive() || target == null || !target.isEntityAlive() || !this.yeti.canRampage()) {
            return false;
        }
        if (this.remainingCooldown > 0) {
            --this.remainingCooldown;
        }
        return this.remainingCooldown <= 0;
    }

    @Override
    public void startExecuting() {
        this.remainingDuration = this.duration;
        this.started = true;
        this.yeti.setRampaging(true);
    }

    @Override
    public boolean continueExecuting() {
        return this.yeti.isEntityAlive() && this.remainingDuration > 0;
    }

    @Override
    public void updateTask() {
        if (this.remainingDuration <= 0) {
            return;
        }
        --this.remainingDuration;
        EntityLivingBase target = this.yeti.getAttackTarget();
        if (target != null && target.isEntityAlive()) {
            this.yeti.getLookHelper().setLookPositionWithEntity(target, 10.0F, this.yeti.getVerticalFaceSpeed());
        }

        if (this.yeti.onGround) {
            this.yeti.motionX = 0.0D;
            this.yeti.motionZ = 0.0D;
            this.yeti.motionY = 0.4D;
        }

        if (!this.yeti.worldObj.getGameRules().getGameRuleBooleanValue("mobGriefing")) {
            return;
        }

        if (this.remainingDuration % 4 == 0) {
            this.yeti.destroyBlocksInAABB(this.yeti.boundingBox.expand(1, 2, 1).offset(0, 2, 0));
        }
        if (this.remainingDuration > 0 && this.remainingDuration % 20 == 0) {
            this.yeti.makeRandomBlockFall();
        }
        if (target != null && target.isEntityAlive()
                && this.remainingDuration > 0
                && this.remainingDuration % 40 == 0) {
            this.yeti.makeBlockAboveTargetFall();
        }
        if (this.remainingDuration > 0 && this.remainingDuration < 40 && this.remainingDuration % 10 == 0) {
            this.yeti.makeNearbyBlockFall();
        }
    }

    @Override
    public void resetTask() {
        this.remainingCooldown = this.timeout;
        this.remainingDuration = 0;
        if (this.started) {
            this.yeti.setRampaging(false);
            if (this.yeti.isEntityAlive()) {
                this.yeti.setTired(true);
            }
            this.started = false;
        }
    }
}
