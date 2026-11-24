package twilightforest.entity.ai;

import java.util.List;

import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.Item;

import twilightforest.entity.passive.EntityTFQuestRam;

/**
 * This is an AI task for the quest ram. When one of the items it wants comes within a close distance, i
 *
 * @author Ben
 *
 */
public class EntityAITFEatLoose extends EntityAIBase {

    /** The entity using this AI that is tempted by the player. */
    private final EntityTFQuestRam temptedQuestRam;

    private final Item temptID;

    private int delayTemptCounter;

    private EntityItem temptingItem;

    public EntityAITFEatLoose(EntityTFQuestRam entityTFQuestRam, Item blockID) {
        this.temptedQuestRam = entityTFQuestRam;
        this.temptID = blockID;
        this.setMutexBits(0);
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
        List<EntityItem> nearby = temptedQuestRam.worldObj
                .getEntitiesWithinAABB(EntityItem.class, temptedQuestRam.boundingBox.expand(2.0D, 2.0D, 2.0D));
        temptingItem = null;
        double bestDistance = Double.MAX_VALUE;
        for (EntityItem item : nearby) {
            if (isWanted(item)) {
                double distance = temptedQuestRam.getDistanceSqToEntity(item);
                if (distance < bestDistance) {
                    bestDistance = distance;
                    temptingItem = item;
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
        return isWanted(temptingItem) && temptedQuestRam.getDistanceSqToEntity(temptingItem) <= 6.25D;
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    @Override
    public void startExecuting() {}

    /**
     * Resets the task
     */
    @Override
    public void resetTask() {
        temptingItem = null;
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
        temptedQuestRam.getLookHelper()
                .setLookPositionWithEntity(temptingItem, 30.0F, temptedQuestRam.getVerticalFaceSpeed());
        if (temptedQuestRam.getDistanceSqToEntity(temptingItem) <= 6.25D) {
            int color = temptingItem.getEntityItem().getItemDamage();
            // consume one block, not the entire dropped stack
            if (--temptingItem.getEntityItem().stackSize <= 0) {
                temptingItem.setDead();
            } else {
                // mark the modified stack as watched so clients see the reduced count
                temptingItem.setEntityItemStack(temptingItem.getEntityItem());
            }
            temptedQuestRam.playLivingSound();
            temptedQuestRam.setColorPresent(color);
            temptedQuestRam.animateAddColor(color, 50);
            temptingItem = null;
        }
    }

    private boolean isWanted(EntityItem item) {
        return item != null && item.isEntityAlive()
                && item.getEntityItem() != null
                && item.getEntityItem().stackSize > 0
                && item.getEntityItem().getItem() == temptID
                && !temptedQuestRam.isColorPresent(item.getEntityItem().getItemDamage());
    }

}
