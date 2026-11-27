package twilightforest.entity.ai;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.util.Vec3;

public class EntityAIStayNearHome extends EntityAIBase {

    private final EntityCreature entity;
    private final float speed;

    public EntityAIStayNearHome(EntityCreature entityTFYetiAlpha, float sp) {
        this.entity = entityTFYetiAlpha;
        this.speed = sp;
        this.setMutexBits(1);

    }

    @Override
    public boolean shouldExecute() {
        return entity.hasHome() && !entity.isWithinHomeDistanceCurrentPosition();
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    public boolean continueExecuting() {
        return entity.hasHome() && !entity.isWithinHomeDistanceCurrentPosition() && !entity.getNavigator().noPath();
    }

    @Override
    public void startExecuting() {
        double x = entity.getHomePosition().posX + 0.5D;
        double y = entity.getHomePosition().posY;
        double z = entity.getHomePosition().posZ + 0.5D;
        if (entity.getDistanceSq(x, y, z) > 256.0D) {
            Vec3 step = RandomPositionGenerator
                    .findRandomTargetBlockTowards(entity, 14, 3, Vec3.createVectorHelper(x, y, z));
            if (step != null && entity.getNavigator().tryMoveToXYZ(step.xCoord, step.yCoord, step.zCoord, speed)) {
                return;
            }
        }
        entity.getNavigator().tryMoveToXYZ(x, y, z, speed);
    }
}
