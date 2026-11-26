package twilightforest.tileentity;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.EntityLiving;
import net.minecraft.nbt.NBTTagCompound;

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

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        tag.setInteger("KnightsCount", knightsCount);
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        if (tag.hasKey("KnightsCount", 99)) {
            knightsCount = Math.max(1, Math.min(6, tag.getInteger("KnightsCount")));
        }
    }

    /**
     * Spawn the boss
     */
    @Override
    protected boolean spawnMyBoss() {
        List<EntityLiving> spawned = new ArrayList<>();
        for (int i = 0; i < knightsCount; i++) {

            // spawn creature
            EntityLiving myCreature = makeMyCreature();

            if (!(myCreature instanceof EntityTFKnightPhantom knight)) {
                for (EntityLiving creature : spawned) {
                    creature.setDead();
                }
                return false;
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
            if (!worldObj.spawnEntityInWorld(myCreature)) {
                // keep the spawner if the group couldnt be created, duplicating
                for (EntityLiving creature : spawned) {
                    creature.setDead();
                }
                return false;
            }
            spawned.add(myCreature);
        }
        return !spawned.isEmpty();
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
