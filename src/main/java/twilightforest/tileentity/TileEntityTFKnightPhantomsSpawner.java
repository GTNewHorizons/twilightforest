package twilightforest.tileentity;

import net.minecraft.entity.EntityLiving;

import twilightforest.entity.TFCreatures;
import twilightforest.entity.boss.EntityTFKnightPhantom;

public class TileEntityTFKnightPhantomsSpawner extends TileEntityTFBossSpawner {

    private static final float ANGLE_BETWEEN_KNIGHTS = 60.0F;
    private static final float SPAWN_DISTANCE = 4.0F;

    public int knightsCount = 6;

    public TileEntityTFKnightPhantomsSpawner() {
        this.mobID = TFCreatures.getSpawnerNameFor("Knight Phantom");
    }

    @Override
    public boolean anyPlayerInRange() {
        return isPlayerInRange(9D, 2);
    }

    /**
     * Spawn the boss
     */
    @Override
    protected void spawnMyBoss() {
        for (int i = 0; i < knightsCount; i++) {

            // spawn creature
            EntityLiving myCreature = makeMyCreature();

            if (!(myCreature instanceof EntityTFKnightPhantom knight)) {
                continue;
            }

            float angle = ANGLE_BETWEEN_KNIGHTS * i;
            float distance = SPAWN_DISTANCE;

            double rx = xCoord + 0.5D + Math.cos((angle) * Math.PI / 180.0D) * distance;
            double ry = yCoord + 0.5D;
            double rz = zCoord + 0.5D + Math.sin((angle) * Math.PI / 180.0D) * distance;

            myCreature.setLocationAndAngles(rx, ry, rz, worldObj.rand.nextFloat() * 360F, 0.0F);

            // set creature's home to this
            initializeCreature(myCreature);

            knight.setNumber(i);

            // spawn it
            worldObj.spawnEntityInWorld(myCreature);
        }
    }

    /**
     * Any post-creation initialization goes here
     */
    @Override
    protected void initializeCreature(EntityLiving myCreature) {
        if (myCreature instanceof EntityTFKnightPhantom knight) {
            knight.setHomeArea(xCoord, yCoord, zCoord, 46);
        }
    }

}
