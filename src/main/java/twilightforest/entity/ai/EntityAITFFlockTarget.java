package twilightforest.entity.ai;

import java.util.List;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAITarget;

public class EntityAITFFlockTarget extends EntityAITarget {

    private final EntityCreature flockCreature;
    private EntityLivingBase flockTarget;

    public EntityAITFFlockTarget(EntityCreature par1EntityLiving, boolean b) {
        super(par1EntityLiving, b);
        this.flockCreature = par1EntityLiving;
        this.setMutexBits(1);
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    @Override
    public boolean shouldExecute() {
        if (flockCreature.getAttackTarget() != null || flockCreature.getRNG().nextInt(10) != 0) {
            return false;
        }
        List<? extends EntityLivingBase> nearby = flockCreature.worldObj
                .getEntitiesWithinAABB(flockCreature.getClass(), flockCreature.boundingBox.expand(16.0D, 4.0D, 16.0D));
        flockTarget = null;
        double nearest = Double.MAX_VALUE;
        for (EntityLivingBase member : nearby) {
            if (member == flockCreature || !member.isEntityAlive()) {
                continue;
            }
            // an allys current target is more useful than an old revenge target
            EntityLivingBase candidate = member instanceof EntityCreature ? ((EntityCreature) member).getAttackTarget()
                    : null;
            if (candidate == null) {
                candidate = member.getAITarget();
            }
            if (candidate != null && candidate.isEntityAlive() && isSuitableTarget(candidate, true)) {
                double distance = flockCreature.getDistanceSqToEntity(candidate);
                if (distance < nearest) {
                    nearest = distance;
                    flockTarget = candidate;
                }
            }
        }
        return flockTarget != null;
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    @Override
    public void startExecuting() {
        if (flockTarget != null && flockTarget.isEntityAlive()) {
            taskOwner.setAttackTarget(flockTarget);
            super.startExecuting();
        }
    }
}
