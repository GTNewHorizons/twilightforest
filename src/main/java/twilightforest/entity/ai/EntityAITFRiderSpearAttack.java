package twilightforest.entity.ai;

import net.minecraft.entity.ai.EntityAIBase;

import twilightforest.entity.EntityTFGoblinKnightLower;
import twilightforest.entity.EntityTFGoblinKnightUpper;

public class EntityAITFRiderSpearAttack extends EntityAIBase {

    private final EntityTFGoblinKnightLower entity;

    public EntityAITFRiderSpearAttack(EntityTFGoblinKnightLower knight) {
        this.entity = knight;
        this.setMutexBits(7);
    }

    @Override
    public boolean shouldExecute() {
        return this.isRiderDoingSpearAttack();
    }

    @Override
    public boolean continueExecuting() {
        return this.isRiderDoingSpearAttack();
    }

    public boolean isRiderDoingSpearAttack() {
        if (!this.entity.isEntityAlive() || !(this.entity.riddenByEntity instanceof EntityTFGoblinKnightUpper rider)
                || !rider.isEntityAlive()) {
            return false;
        }

        // the riders telegraph must also suspend the mounts regular attacks
        return rider.heavySpearTimer > 0 && rider.heavySpearTimer < 50;
    }
}
