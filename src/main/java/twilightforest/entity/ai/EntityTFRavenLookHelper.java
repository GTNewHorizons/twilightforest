package twilightforest.entity.ai;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityLookHelper;
import net.minecraft.util.MathHelper;

public class EntityTFRavenLookHelper extends EntityLookHelper {

    private static final float IDLE_TURN_SPEED = 10.0F;
    private static final float MAX_MOVING_HEAD_YAW = 75.0F;
    private static final float RAVEN_MODEL_YAW_OFFSET = 30.0F;

    private final EntityLiving entity;
    private float yawSpeed;
    private float pitchSpeed;
    private boolean hasLookTarget;
    private double targetX;
    private double targetY;
    private double targetZ;

    public EntityTFRavenLookHelper(EntityLiving entity) {
        super(entity);
        this.entity = entity;
    }

    @Override
    public void setLookPositionWithEntity(Entity target, float yawSpeed, float pitchSpeed) {
        if (target == null) {
            return;
        }
        double y = target instanceof EntityLivingBase living ? target.posY + living.getEyeHeight()
                : (target.boundingBox.minY + target.boundingBox.maxY) * 0.5D;
        this.setLookPosition(target.posX, y, target.posZ, yawSpeed, pitchSpeed);
    }

    @Override
    public void setLookPosition(double x, double y, double z, float yawSpeed, float pitchSpeed) {
        this.targetX = x;
        this.targetY = y;
        this.targetZ = z;
        this.yawSpeed = Math.max(0.0F, yawSpeed);
        this.pitchSpeed = Math.max(0.0F, pitchSpeed);
        this.hasLookTarget = true;
    }

    @Override
    public void onUpdateLook() {
        if (this.hasLookTarget) {
            this.hasLookTarget = false;
            double dx = this.targetX - this.entity.posX;
            double dy = this.targetY - (this.entity.posY + this.entity.getEyeHeight());
            double dz = this.targetZ - this.entity.posZ;
            double horizontalDistance = Math.sqrt(dx * dx + dz * dz);

            // preserve the original raven models yaw offset while smoothing pitch too
            float desiredYaw = (float) Math.toDegrees(Math.atan2(dz, dx)) - RAVEN_MODEL_YAW_OFFSET;
            float desiredPitch = (float) -Math.toDegrees(Math.atan2(dy, horizontalDistance));
            this.entity.rotationYawHead = this.updateRotation(this.entity.rotationYawHead, desiredYaw, this.yawSpeed);
            this.entity.rotationPitch = this.updateRotation(this.entity.rotationPitch, desiredPitch, this.pitchSpeed);
        } else {
            this.entity.rotationYawHead = this
                    .updateRotation(this.entity.rotationYawHead, this.entity.renderYawOffset, IDLE_TURN_SPEED);
            this.entity.rotationPitch = this.updateRotation(this.entity.rotationPitch, 0.0F, IDLE_TURN_SPEED);
        }

        if (!this.entity.getNavigator().noPath()) {
            float yawOffset = MathHelper
                    .wrapAngleTo180_float(this.entity.rotationYawHead - this.entity.renderYawOffset);
            if (yawOffset < -MAX_MOVING_HEAD_YAW) {
                this.entity.rotationYawHead = this.entity.renderYawOffset - MAX_MOVING_HEAD_YAW;
            } else if (yawOffset > MAX_MOVING_HEAD_YAW) {
                this.entity.rotationYawHead = this.entity.renderYawOffset + MAX_MOVING_HEAD_YAW;
            }
        }
    }

    private float updateRotation(float current, float desired, float maxChange) {
        float delta = MathHelper.wrapAngleTo180_float(desired - current);
        float limited = Math.max(-maxChange, Math.min(maxChange, delta));
        return current + limited;
    }
}
