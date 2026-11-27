package twilightforest.entity.ai;

import net.minecraft.entity.ai.EntityAIBase;

import twilightforest.entity.EntityTFRovingCube;

/**
 *
 * This is a task that runs when we are near a symbol and have stopped pathfinding, but are not centered on the symbol.
 *
 * The goal of this task is to center on a symbol.
 *
 * @author benma_000
 *
 */
public class EntityAICubeCenterOnSymbol extends EntityAIBase {

    private final EntityTFRovingCube myCube;
    private double xPosition;
    private double yPosition;
    private double zPosition;
    private final double speed;

    public EntityAICubeCenterOnSymbol(EntityTFRovingCube entityTFRovingCube, double d) {
        this.myCube = entityTFRovingCube;
        this.xPosition = this.myCube.symbolX;
        this.yPosition = this.myCube.symbolY;
        this.zPosition = this.myCube.symbolZ;
        this.speed = d;
        this.setMutexBits(1);
    }

    @Override
    public boolean shouldExecute() {
        if (!myCube.hasFoundSymbol || !myCube.getNavigator().noPath()) {
            return false;
        }
        xPosition = myCube.symbolX;
        yPosition = myCube.symbolY;
        zPosition = myCube.symbolZ;
        double distance = distanceFromSymbol();
        return distance > 0.1D && distance < 10.0D;
    }

    // Returns whether an in-progress EntityAIBase should continue executing
    public boolean continueExecuting() {
        return myCube.hasFoundSymbol && myCube.getNavigator().noPath()
                && myCube.symbolX == xPosition
                && myCube.symbolY == yPosition
                && myCube.symbolZ == zPosition
                && distanceFromSymbol() > 0.1D
                && distanceFromSymbol() < 10.0D;
    }

    @Override
    public void updateTask() {
        myCube.getMoveHelper().setMoveTo(xPosition + 0.5D, yPosition, zPosition + 0.5D, speed);
    }

    public double distanceFromSymbol() {
        double dx = this.xPosition - this.myCube.posX + 0.5F;
        double dy = this.yPosition - this.myCube.posY;
        double dz = this.zPosition - this.myCube.posZ + 0.5F;
        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }

}
