package twilightforest.entity.ai;

import net.minecraft.entity.ai.EntityAIBase;

import twilightforest.entity.boss.EntityTFYetiAlpha;

public class EntityAITFYetiTired extends EntityAIBase {

    private final EntityTFYetiAlpha yeti;
    private final int tiredDuration;
    private int tiredTimer;

    public EntityAITFYetiTired(EntityTFYetiAlpha yeti, int duration) {
        this.yeti = yeti;
        this.tiredDuration = Math.max(1, duration);
        this.setMutexBits(5);
    }

    @Override
    public boolean shouldExecute() {
        return this.yeti.isEntityAlive() && this.yeti.isTired();
    }

    @Override
    public boolean continueExecuting() {
        return this.yeti.isEntityAlive() && this.yeti.isTired() && this.tiredTimer < this.tiredDuration;
    }

    @Override
    public boolean isInterruptible() {
        return false;
    }

    @Override
    public void startExecuting() {
        this.tiredTimer = 0;
    }

    @Override
    public void resetTask() {
        this.tiredTimer = 0;
        this.yeti.setTired(false);
    }

    @Override
    public void updateTask() {
        if (this.tiredTimer < this.tiredDuration) {
            ++this.tiredTimer;
        }
    }
}
