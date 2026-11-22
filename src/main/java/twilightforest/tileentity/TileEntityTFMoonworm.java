package twilightforest.tileentity;

public class TileEntityTFMoonworm extends TileEntityTFCritter {

    private static final int FULL_ROTATION = 360;
    private static final int ROTATION_STEP = 90;
    private static final int ROTATION_DELAY = 200;

    public int yawDelay;
    public int currentYaw;
    public int desiredYaw;

    public TileEntityTFMoonworm() {
        super();

        currentYaw = -1;
    }

    /**
     * Allows the entity to update its state. Overridden in most subclasses, e.g. the mob spawner uses this to count
     * ticks and creates a new spawn inside its implementation.
     */
    @Override
    public void updateEntity() {
        super.updateEntity();

        if (currentYaw == -1) {
            currentYaw = worldObj.rand.nextInt(4) * ROTATION_STEP;
        }

        if (yawDelay > 0) {
            yawDelay--;
        } else {
            if (desiredYaw == 0) {
                // make it rotate!
                yawDelay = ROTATION_DELAY + worldObj.rand.nextInt(ROTATION_DELAY);
                desiredYaw = worldObj.rand.nextInt(4) * ROTATION_STEP;
            }

            currentYaw++;

            if (currentYaw > FULL_ROTATION) {
                currentYaw = 0;
            }

            if (currentYaw == desiredYaw) {
                desiredYaw = 0;
            }
        }
    }

}
