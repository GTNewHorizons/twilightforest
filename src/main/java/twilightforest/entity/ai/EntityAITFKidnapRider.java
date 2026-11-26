package twilightforest.entity.ai;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.util.Vec3;

public class EntityAITFKidnapRider extends EntityAIBase {

    private final EntityCreature theEntityCreature;
    private final float speed;
    private double randPosX;
    private double randPosY;
    private double randPosZ;

    public EntityAITFKidnapRider(EntityCreature par1EntityCreature, float par2) {
        this.theEntityCreature = par1EntityCreature;
        this.speed = par2;
        this.setMutexBits(1);
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    @Override
    public boolean shouldExecute() {
        if (theEntityCreature.riddenByEntity == null || !theEntityCreature.riddenByEntity.isEntityAlive()
                || theEntityCreature.getRNG().nextInt(5) != 0) {
            return false;
        }
        Vec3 next = RandomPositionGenerator.findRandomTarget(theEntityCreature, 8, 4);
        if (next == null) {
            return false;
        }
        randPosX = next.xCoord;
        randPosY = next.yCoord;
        randPosZ = next.zCoord;
        return true;
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    @Override
    public void startExecuting() {
        this.theEntityCreature.getNavigator().tryMoveToXYZ(this.randPosX, this.randPosY, this.randPosZ, this.speed);
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    @Override
    public boolean continueExecuting() {
        return theEntityCreature.riddenByEntity != null && theEntityCreature.riddenByEntity.isEntityAlive()
                && !theEntityCreature.getNavigator().noPath();
    }
}
