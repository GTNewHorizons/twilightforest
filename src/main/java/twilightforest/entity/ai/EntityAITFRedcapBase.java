package twilightforest.entity.ai;

import java.util.List;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.MathHelper;

import twilightforest.entity.EntityTFRedcap;

public abstract class EntityAITFRedcapBase extends EntityAIBase {

    protected EntityTFRedcap entityObj;

    // returns whether the redcap is within the 120-degree forward cone of its target
    public boolean isTargetLookingAtMe(EntityLivingBase attackTarget) {
        if (attackTarget == null || !attackTarget.isEntityAlive()) {
            return false;
        }

        double dx = this.entityObj.posX - attackTarget.posX;
        double dz = this.entityObj.posZ - attackTarget.posZ;
        double distanceSq = dx * dx + dz * dz;
        if (distanceSq < 1.0E-6D) {
            return true;
        }

        double yaw = Math.toRadians(attackTarget.rotationYaw);
        double facingDot = -Math.sin(yaw) * dx + Math.cos(yaw) * dz;
        return facingDot > Math.sqrt(distanceSq) * 0.5D;
    }

    // finds the closest unlit TNT at a redcap reachable height without loading chunks
    public ChunkCoordinates findBlockTNTNearby(int range) {
        if (range < 0) {
            return null;
        }

        int baseX = MathHelper.floor_double(this.entityObj.posX);
        int baseY = MathHelper.floor_double(this.entityObj.posY);
        int baseZ = MathHelper.floor_double(this.entityObj.posZ);
        int verticalRange = Math.min(range, 2);
        double bestDistance = Double.POSITIVE_INFINITY;
        ChunkCoordinates closest = null;

        // a ground mob probably cant reach tnt 8 blocks above or below it
        for (int y = -verticalRange; y <= verticalRange; y++) {
            for (int x = -range; x <= range; x++) {
                for (int z = -range; z <= range; z++) {
                    int blockX = baseX + x;
                    int blockY = baseY + y;
                    int blockZ = baseZ + z;
                    double distance = this.entityObj.getDistanceSq(blockX + 0.5D, blockY, blockZ + 0.5D);
                    if (distance >= bestDistance || !this.entityObj.worldObj.blockExists(blockX, blockY, blockZ)) {
                        continue;
                    }
                    if (this.entityObj.worldObj.getBlock(blockX, blockY, blockZ) == Blocks.tnt) {
                        bestDistance = distance;
                        closest = new ChunkCoordinates(blockX, blockY, blockZ);
                    }
                }
            }
        }
        return closest;
    }

    // returns whether any already lit TNT is close enough to make planting unsafe
    public boolean isLitTNTNearby(int range) {
        AxisAlignedBB box = this.entityObj.boundingBox.expand(range, range, range);
        List<EntityTNTPrimed> primed = this.entityObj.worldObj.getEntitiesWithinAABB(EntityTNTPrimed.class, box);
        return !primed.isEmpty();
    }
}
