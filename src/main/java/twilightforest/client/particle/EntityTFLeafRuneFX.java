package twilightforest.client.particle;

import net.minecraft.client.particle.EntityEnchantmentTableParticleFX;
import net.minecraft.world.World;

public class EntityTFLeafRuneFX extends EntityEnchantmentTableParticleFX {

    public EntityTFLeafRuneFX(World world, double x, double y, double z, double velX, double velY, double velZ) {
        super(world, x, y, z, velX, velY, velZ);

        this.particleScale = this.rand.nextFloat() + 1F;
        this.particleMaxAge += 10;
        this.particleGravity = 0.003F + rand.nextFloat() * 0.006F;

        this.noClip = false;
    }

    /**
     * Called to update the entity's position/logic.
     */
    public void onUpdate() {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;

        this.moveEntity(this.motionX, this.motionY, this.motionZ);
        this.motionY -= this.particleGravity;

        if (this.particleAge++ >= this.particleMaxAge) {
            this.setDead();
        }
    }
}
