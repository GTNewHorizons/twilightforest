package twilightforest.entity.ai;

import java.util.List;

import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.Item;

import twilightforest.entity.passive.EntityTFQuestRam;

public class EntityAITFFindLoose extends EntityAIBase {

    private static final double SEARCH_HORIZONTAL = 16.0D;
    private static final double SEARCH_VERTICAL = 4.0D;
    private static final double EAT_DISTANCE_SQ = 6.25D;
    private static final int MAX_PURSUIT_TICKS = 200;
    private static final int FAILED_ITEM_RETRY_TICKS = 100;

    /** The entity using this AI that is tempted by the player. */
    private final EntityTFQuestRam temptedEntity;

    private final Item temptID;
    private final float pursueSpeed;

    private int delayTemptCounter;

    private EntityItem temptingItem;
    private int pathCooldown;
    private int pursuitTicks;
    private EntityItem failedItem;
    private long retryFailedItemAt;

    public EntityAITFFindLoose(EntityTFQuestRam entityTFQuestRam, float speed, Item blockID) {
        this.temptedEntity = entityTFQuestRam;
        this.pursueSpeed = speed;
        this.temptID = blockID;
        this.setMutexBits(3);
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    @Override
    public boolean shouldExecute() {
        if (delayTemptCounter > 0) {
            --delayTemptCounter;
            return false;
        }
        List<EntityItem> nearby = temptedEntity.worldObj.getEntitiesWithinAABB(
                EntityItem.class,
                temptedEntity.boundingBox.expand(SEARCH_HORIZONTAL, SEARCH_VERTICAL, SEARCH_HORIZONTAL));
        temptingItem = null;
        double nearestDistance = Double.MAX_VALUE;
        if (temptedEntity.worldObj.getTotalWorldTime() >= retryFailedItemAt) {
            failedItem = null;
        }
        for (EntityItem item : nearby) {
            if (item != failedItem && isWanted(item)) {
                double distance = temptedEntity.getDistanceSqToEntity(item);
                if (distance > EAT_DISTANCE_SQ && distance < nearestDistance) {
                    temptingItem = item;
                    nearestDistance = distance;
                }
            }
        }
        return temptingItem != null;
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    @Override
    public boolean continueExecuting() {
        if (!isWanted(temptingItem) || temptedEntity.getDistanceSqToEntity(temptingItem) <= EAT_DISTANCE_SQ
                || !temptedEntity.boundingBox.expand(SEARCH_HORIZONTAL, SEARCH_VERTICAL, SEARCH_HORIZONTAL)
                        .intersectsWith(temptingItem.boundingBox)) {
            return false;
        }
        if (pursuitTicks >= MAX_PURSUIT_TICKS || (pursuitTicks > 0 && temptedEntity.getNavigator().noPath())) {
            abandonItem();
            return false;
        }
        return true;
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    @Override
    public void startExecuting() {
        pathCooldown = 0;
        pursuitTicks = 0;
    }

    /**
     * Resets the task
     */
    @Override
    public void resetTask() {
        temptingItem = null;
        temptedEntity.getNavigator().clearPathEntity();
        delayTemptCounter = 20;
    }

    /**
     * Updates the task
     */
    @Override
    public void updateTask() {
        if (!isWanted(temptingItem)) {
            return;
        }
        ++pursuitTicks;
        temptedEntity.getLookHelper()
                .setLookPositionWithEntity(temptingItem, 30.0F, temptedEntity.getVerticalFaceSpeed());
        if (--pathCooldown <= 0) {
            pathCooldown = 10;
            if (!temptedEntity.getNavigator()
                    .tryMoveToXYZ(temptingItem.posX, temptingItem.posY, temptingItem.posZ, pursueSpeed)) {
                abandonItem();
            }
        }
    }

    private void abandonItem() {
        // let wandering or another wool target take priority after a failed pursuit
        failedItem = temptingItem;
        retryFailedItemAt = temptedEntity.worldObj.getTotalWorldTime() + FAILED_ITEM_RETRY_TICKS;
        temptingItem = null;
        temptedEntity.getNavigator().clearPathEntity();
    }

    private boolean isWanted(EntityItem item) {
        return item != null && item.isEntityAlive()
                && item.getEntityItem() != null
                && item.getEntityItem().stackSize > 0
                && item.getEntityItem().getItem() == temptID
                && temptedEntity.wantsColor(item.getEntityItem().getItemDamage());
    }
}
