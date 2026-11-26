package twilightforest.entity.ai;

import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.init.Blocks;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;

import twilightforest.entity.EntityTFRovingCube;

public class EntityAICubeMoveToRedstoneSymbols extends EntityAIBase {

    private final EntityTFRovingCube myCube;
    private double xPosition;
    private double yPosition;
    private double zPosition;
    private final double speed;
    private int searchCooldown;

    public EntityAICubeMoveToRedstoneSymbols(EntityTFRovingCube entityTFRovingCube, double d) {
        this.myCube = entityTFRovingCube;
        this.speed = d;
        this.setMutexBits(1);
    }

    @Override
    public boolean shouldExecute() {
        if (!myCube.getNavigator().noPath() || --searchCooldown > 0) {
            return false;
        }
        // existing symbols should be revisited
        if (myCube.hasFoundSymbol && isRedstoneSymbol(myCube.symbolX, myCube.symbolY, myCube.symbolZ)) {
            double distance = myCube.getDistanceSq(myCube.symbolX + 0.5D, myCube.symbolY, myCube.symbolZ + 0.5D);
            if (distance <= 1.0D) {
                searchCooldown = 60;
                return false;
            }
            xPosition = myCube.symbolX;
            yPosition = myCube.symbolY;
            zPosition = myCube.symbolZ;
            searchCooldown = 40;
            return true;
        }
        myCube.hasFoundSymbol = false;
        searchCooldown = 60 + myCube.getRNG().nextInt(20);
        Vec3 symbol = searchForRedstoneSymbol(myCube, 16, 5);
        if (symbol == null) {
            return false;
        }
        xPosition = symbol.xCoord;
        yPosition = symbol.yCoord;
        zPosition = symbol.zCoord;
        myCube.hasFoundSymbol = true;
        myCube.symbolX = (int) xPosition;
        myCube.symbolY = (int) yPosition;
        myCube.symbolZ = (int) zPosition;
        return true;
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    public boolean continueExecuting() {
        return myCube.hasFoundSymbol && !myCube.getNavigator().noPath()
                && isRedstoneSymbol(myCube.symbolX, myCube.symbolY, myCube.symbolZ);
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void startExecuting() {
        myCube.getNavigator().tryMoveToXYZ(xPosition + 0.5D, yPosition, zPosition + 0.5D, speed);
    }

    /**
     * Search the area for a redstone circle (8 redstone dust around a blank square)
     */
    private Vec3 searchForRedstoneSymbol(EntityTFRovingCube cube, int xzRange, int yRange) {
        int cx = MathHelper.floor_double(cube.posX);
        int cy = MathHelper.floor_double(cube.posY);
        int cz = MathHelper.floor_double(cube.posZ);
        // search nearby cells first so the cube chooses a useful reachable symbol
        for (int radius = 0; radius <= xzRange; radius++) {
            for (int x = cx - radius; x <= cx + radius; x++) {
                for (int z = cz - radius; z <= cz + radius; z++) {
                    if (Math.max(Math.abs(x - cx), Math.abs(z - cz)) != radius) {
                        continue;
                    }
                    for (int yOffset = 0; yOffset <= yRange; yOffset++) {
                        if (isRedstoneSymbol(x, cy + yOffset, z)) {
                            return Vec3.createVectorHelper(x, cy + yOffset, z);
                        }
                        if (yOffset != 0 && isRedstoneSymbol(x, cy - yOffset, z)) {
                            return Vec3.createVectorHelper(x, cy - yOffset, z);
                        }
                    }
                }
            }
        }
        return null;
    }

    private boolean isRedstoneSymbol(int x, int y, int z) {
        // check the complete 3x3 is loaded before reading the wire blocks
        if (!myCube.worldObj.blockExists(x - 1, y, z - 1) || !myCube.worldObj.blockExists(x - 1, y, z + 1)
                || !myCube.worldObj.blockExists(x + 1, y, z - 1)
                || !myCube.worldObj.blockExists(x + 1, y, z + 1)
                || !myCube.worldObj.isAirBlock(x, y, z)) {
            return false;
        }
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                if ((dx != 0 || dz != 0) && myCube.worldObj.getBlock(x + dx, y, z + dz) != Blocks.redstone_wire) {
                    return false;
                }
            }
        }
        return true;
    }

}
