package twilightforest.entity.boss;

import java.util.UUID;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityTFHydraPart extends EntityLiving {

    public EntityTFHydra hydraObj;
    private UUID hydraId;

    public EntityTFHydraPart(World world) {
        super(world);
        isImmuneToFire = true;
    }

    public EntityTFHydraPart(EntityTFHydra hydra, String s, float f, float f1) {
        super(hydra.worldObj);
        setSize(f, f1);
        bindToHydra(hydra);
        setPartName(s);

        // texture = TwilightForestMod.MODEL_DIR + "hydra4.png";
        isImmuneToFire = true;

    }

    @Override
    protected void entityInit() {
        super.entityInit();
        dataWatcher.addObject(17, "");
        dataWatcher.addObject(20, -1);
    }

    public void bindToHydra(EntityTFHydra hydra) {
        hydraObj = hydra;
        if (!worldObj.isRemote) {
            hydraId = hydra.getUniqueID();
            dataWatcher.updateObject(20, hydra.getEntityId());
        }
    }

    public boolean belongsTo(EntityTFHydra hydra) {
        return worldObj.isRemote ? dataWatcher.getWatchableObjectInt(20) == hydra.getEntityId()
                : hydraId != null && hydraId.equals(hydra.getUniqueID());
    }

    public boolean hasNoOwner() {
        return !worldObj.isRemote && hydraId == null && hydraObj == null;
    }

    public String getPartName() {
        return dataWatcher.getWatchableObjectString(17);
    }

    public void setPartName(String name) {
        dataWatcher.updateObject(17, name);
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound nbttagcompound) {
        super.writeEntityToNBT(nbttagcompound);
        nbttagcompound.setString("PartName", getPartName());
        if (hydraId != null) {
            nbttagcompound.setLong("HydraMost", hydraId.getMostSignificantBits());
            nbttagcompound.setLong("HydraLeast", hydraId.getLeastSignificantBits());
        }
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound nbttagcompound) {
        super.readEntityFromNBT(nbttagcompound);
        setPartName(nbttagcompound.getString("PartName"));
        if (nbttagcompound.hasKey("HydraMost", 99) && nbttagcompound.hasKey("HydraLeast", 99)) {
            hydraId = new UUID(nbttagcompound.getLong("HydraMost"), nbttagcompound.getLong("HydraLeast"));
        }
    }

    /**
     * We need this to display bounding boxes
     */
    @Override
    public void onUpdate() {
        if (this.hydraObj != null && (this.hydraObj.isDead || this.hydraObj.deathTime > 190)) {
            setDead();
            return;
        }

        // just die if we've been alive 60 seconds and there's still no body
        if (!worldObj.isRemote && this.hydraObj == null && this.ticksExisted > 1200) {
            setDead();
            return;
        }

        super.onEntityUpdate();

        lastTickPosX = posX;
        lastTickPosY = posY;
        lastTickPosZ = posZ;

        if (this.newPosRotationIncrements > 0) {
            double var1 = this.posX + (this.newPosX - this.posX) / this.newPosRotationIncrements;
            double var3 = this.posY + (this.newPosY - this.posY) / this.newPosRotationIncrements;
            double var5 = this.posZ + (this.newPosZ - this.posZ) / this.newPosRotationIncrements;
            double var7 = MathHelper.wrapAngleTo180_double(this.newRotationYaw - this.rotationYaw);
            this.rotationYaw = (float) (this.rotationYaw + var7 / this.newPosRotationIncrements);
            this.rotationPitch = (float) (this.rotationPitch
                    + (this.newRotationPitch - this.rotationPitch) / this.newPosRotationIncrements);
            --this.newPosRotationIncrements;
            this.setPosition(var1, var3, var5);
            this.setRotation(this.rotationYaw, this.rotationPitch);
        }

        // System.out.println("Updating " + this + " with angles " + rotationYawHead + ", " +
        // rotationPitch);

        this.rotationYawHead = this.rotationYaw;
        this.prevRotationYawHead = this.prevRotationYaw;

        for (; rotationYaw - prevRotationYaw < -180F; prevRotationYaw -= 360F) {}
        for (; rotationYaw - prevRotationYaw >= 180F; prevRotationYaw += 360F) {}
        for (; renderYawOffset - prevRenderYawOffset < -180F; prevRenderYawOffset -= 360F) {}
        for (; renderYawOffset - prevRenderYawOffset >= 180F; prevRenderYawOffset += 360F) {}
        for (; rotationPitch - prevRotationPitch < -180F; prevRotationPitch -= 360F) {}
        for (; rotationPitch - prevRotationPitch >= 180F; prevRotationPitch += 360F) {}
        for (; rotationYawHead - prevRotationYawHead < -180F; prevRotationYawHead -= 360F) {}
        for (; rotationYawHead - prevRotationYawHead >= 180F; prevRotationYawHead += 360F) {}

    }

    /**
     * Set monster attributes
     */
    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(1000D); // max health
    }

    @Override
    public boolean attackEntityFrom(DamageSource damagesource, float i) {
        if (hydraObj != null) {
            return hydraObj.attackEntityFromPart(this, damagesource, i);
        } else {
            return false;
        }
    }

    public boolean isEntityEqual(Entity entity) {
        return this == entity || hydraObj == entity;
    }

    /**
     * Sets the rotation of the entity
     */
    @Override
    protected void setRotation(float par1, float par2) {
        this.rotationYaw = par1 % 360.0F;
        this.rotationPitch = par2 % 360.0F;
    }
}
