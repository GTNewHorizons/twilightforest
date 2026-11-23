package twilightforest.entity;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import twilightforest.TwilightForestMod;

public class EntityTFSlideBlock extends Entity implements IEntityAdditionalSpawnData {

    private static final int WARMUP_TIME = 20;
    private Block myBlock;
    private int myMeta;
    private short slideTime;
    private boolean canDropItem = true;

    float moveX;
    float moveY;
    float moveZ;

    public EntityTFSlideBlock(World world) {
        super(world);
        this.preventEntitySpawning = true;
        this.entityCollisionReduction = 1F;
        this.setSize(0.98F, 0.98F);
        // this.yOffset = this.height / 2.0F;
    }

    public EntityTFSlideBlock(World world, double x, double y, double z, Block block, int meta) {
        super(world);

        this.myBlock = block;
        this.myMeta = meta;
        this.preventEntitySpawning = true;
        this.entityCollisionReduction = 1F;
        this.setSize(0.98F, 0.98F);
        // this.yOffset = this.height / 2.0F;
        this.setPosition(x, y, z);
        this.motionX = 0.0D;
        this.motionY = 0.0D;
        this.motionZ = 0.0D;
        this.prevPosX = x;
        this.prevPosY = y;
        this.prevPosZ = z;

        this.determineMoveDirection();
    }

    /**
     * Called when creating the entity to determine which direction the block will slide. The logic here is a little
     * tricky
     */
    private void determineMoveDirection() {
        this.moveX = 0F;
        this.moveY = 0F;
        this.moveZ = 0F;

        int bx = MathHelper.floor_double(this.posX);
        int by = MathHelper.floor_double(this.posY);
        int bz = MathHelper.floor_double(this.posZ);

        if ((this.myMeta & 12) == 4) {
            // horizontal blocks will go up or down if there is a block on one side and air on the other
            if (!this.worldObj.isAirBlock(bx, by + 1, bz) && this.worldObj.isAirBlock(bx, by - 1, bz)) {
                this.moveY = -1F;
            } else if (!this.worldObj.isAirBlock(bx, by - 1, bz) && this.worldObj.isAirBlock(bx, by + 1, bz)) {
                this.moveY = 1F;
            } else if (!this.worldObj.isAirBlock(bx, by, bz + 1) && this.worldObj.isAirBlock(bx, by, bz - 1)) { // then
                // try Z
                this.moveZ = -1F;
            } else if (!this.worldObj.isAirBlock(bx, by, bz - 1) && this.worldObj.isAirBlock(bx, by, bz + 1)) {
                this.moveZ = 1F;
            } else if (this.worldObj.isAirBlock(bx, by - 1, bz)) { // if no wall, travel towards open air
                this.moveY = -1F;
            } else if (this.worldObj.isAirBlock(bx, by + 1, bz)) {
                this.moveY = 1F;
            } else if (this.worldObj.isAirBlock(bx, by, bz - 1)) {
                this.moveZ = -1F;
            } else if (this.worldObj.isAirBlock(bx, by, bz + 1)) {
                this.moveZ = 1F;
            }
        } else if ((this.myMeta & 12) == 8) {
            // horizontal blocks will go up or down if there is a block on one side and air on the other
            if (!this.worldObj.isAirBlock(bx, by + 1, bz) && this.worldObj.isAirBlock(bx, by - 1, bz)) {
                this.moveY = -1F;
            } else if (!this.worldObj.isAirBlock(bx, by - 1, bz) && this.worldObj.isAirBlock(bx, by + 1, bz)) {
                this.moveY = 1F;
            } else if (!this.worldObj.isAirBlock(bx + 1, by, bz) && this.worldObj.isAirBlock(bx - 1, by, bz)) { // then
                // try X
                this.moveX = -1F;
            } else if (!this.worldObj.isAirBlock(bx - 1, by, bz) && this.worldObj.isAirBlock(bx + 1, by, bz)) {
                this.moveX = 1F;
            } else if (this.worldObj.isAirBlock(bx, by - 1, bz)) { // if no wall, travel towards open air
                this.moveY = -1F;
            } else if (this.worldObj.isAirBlock(bx, by + 1, bz)) {
                this.moveY = 1F;
            } else if (this.worldObj.isAirBlock(bx - 1, by, bz)) {
                this.moveX = -1F;
            } else if (this.worldObj.isAirBlock(bx + 1, by, bz)) {
                this.moveX = 1F;
            }
        } else if ((this.myMeta & 12) == 0) {
            // vertical blocks priority is -x, +x, -z, +z
            if (!this.worldObj.isAirBlock(bx + 1, by, bz) && this.worldObj.isAirBlock(bx - 1, by, bz)) {
                this.moveX = -1F;
            } else if (!this.worldObj.isAirBlock(bx - 1, by, bz) && this.worldObj.isAirBlock(bx + 1, by, bz)) {
                this.moveX = 1F;
            } else if (!this.worldObj.isAirBlock(bx, by, bz + 1) && this.worldObj.isAirBlock(bx, by, bz - 1)) {
                this.moveZ = -1F;
            } else if (!this.worldObj.isAirBlock(bx, by, bz - 1) && this.worldObj.isAirBlock(bx, by, bz + 1)) {
                this.moveZ = 1F;
            } else if (this.worldObj.isAirBlock(bx - 1, by, bz)) { // if no wall, travel towards open air
                this.moveX = -1F;
            } else if (this.worldObj.isAirBlock(bx + 1, by, bz)) {
                this.moveX = 1F;
            } else if (this.worldObj.isAirBlock(bx, by, bz - 1)) {
                this.moveZ = -1F;
            } else if (this.worldObj.isAirBlock(bx, by, bz + 1)) {
                this.moveZ = 1F;
            }

        }

    }

    @Override
    protected void entityInit() {}

    /**
     * returns if this entity triggers Block.onEntityWalking on the blocks they walk on. used for spiders and wolves to
     * prevent them from trampling crops
     */
    protected boolean canTriggerWalking() {
        return false;
    }

    /**
     * Returns true if other Entities should be prevented from moving through this Entity.
     */
    public boolean canBeCollidedWith() {
        return !this.isDead;
    }

    @SideOnly(Side.CLIENT)
    public float getShadowSize() {
        return 0.0F;
    }

    /**
     * Called to update the entity's position/logic.
     */
    @Override
    public void onUpdate() {
        if (myBlock == null || myBlock.getMaterial() == Material.air) {
            setDead();
            return;
        }

        prevPosX = posX;
        prevPosY = posY;
        prevPosZ = posZ;
        ++slideTime;

        if (slideTime > WARMUP_TIME) {
            motionX += moveX * 0.04D;
            motionY += moveY * 0.04D;
            motionZ += moveZ * 0.04D;
            moveEntity(motionX, motionY, motionZ);
        }
        motionX *= 0.98D;
        motionY *= 0.98D;
        motionZ *= 0.98D;

        // client prediction must reverse too, otherwise the render drifts from the server
        if (slideTime == WARMUP_TIME + 40) {
            motionX = motionY = motionZ = 0.0D;
            moveX = -moveX;
            moveY = -moveY;
            moveZ = -moveZ;
        }

        if (worldObj.isRemote) {
            return;
        }
        if (slideTime % 5 == 0) {
            worldObj.playSoundEffect(
                    posX,
                    posY,
                    posZ,
                    TwilightForestMod.ID + ":random.slider",
                    1.0F,
                    0.9F + rand.nextFloat() * 0.4F);
        }

        int x = MathHelper.floor_double(posX);
        int y = MathHelper.floor_double(posY);
        int z = MathHelper.floor_double(posZ);
        if (slideTime == 1) {
            // only consume the original block and never dupe it if the removal fails
            if (worldObj.getBlock(x, y, z) != myBlock || worldObj.getBlockMetadata(x, y, z) != myMeta
                    || !worldObj.setBlockToAir(x, y, z)) {
                setDead();
                return;
            }
        }

        // contact causes damage and knockback while the sliding block is active
        damageKnockbackEntities(worldObj.getEntitiesWithinAABBExcludingEntity(this, boundingBox));

        if (isCollided || isStopped()) {
            setDead();
            if (!worldObj.canPlaceEntityOnSide(myBlock, x, y, z, true, 1, null, null)
                    || !worldObj.setBlock(x, y, z, myBlock, myMeta, 3)) {
                dropSlidingBlock();
            }
        } else if ((slideTime > 100 && (y < 1 || y > 256)) || slideTime > 600) {
            dropSlidingBlock();
            setDead();
        }
    }

    private void dropSlidingBlock() {
        if (canDropItem) {
            entityDropItem(new ItemStack(myBlock, 1, myBlock.damageDropped(myMeta)), 0.0F);
        }
    }

    /**
     * Pushes all entities inside the list out and damages them
     */
    private void damageKnockbackEntities(List<Entity> entities) {
        for (Entity entity : entities) {
            if (entity instanceof EntityLivingBase living) {
                entity.attackEntityFrom(DamageSource.generic, 5.0F);
                double dx = (posX - entity.posX) * 2.0D;
                double dz = (posZ - entity.posZ) * 2.0D;
                living.knockBack(this, 5.0F, dx, dz);
                living.velocityChanged = true;
            }
        }
    }

    /**
     * Returns a boundingBox used to collide the entity with other entities and blocks. This enables the entity to be
     * pushable on contact, like boats or minecarts.
     */
    public AxisAlignedBB getCollisionBox(Entity p_70114_1_) {
        return null;
    }

    private boolean isStopped() {
        return this.moveX == 0 && this.moveY == 0 && this.moveZ == 0;
    }

    /**
     * Return whether this entity should be rendered as on fire.
     */
    @SideOnly(Side.CLIENT)
    public boolean canRenderOnFire() {
        return false;
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound nbtTagCompound) {
        this.myBlock = Block.getBlockById(nbtTagCompound.getInteger("TileID"));
        this.myMeta = nbtTagCompound.getByte("Meta");
        this.slideTime = nbtTagCompound.getShort("Time");
        this.moveX = nbtTagCompound.getFloat("MoveX");
        this.moveY = nbtTagCompound.getFloat("MoveY");
        this.moveZ = nbtTagCompound.getFloat("MoveZ");
        this.canDropItem = !nbtTagCompound.hasKey("CanDropItem") || nbtTagCompound.getBoolean("CanDropItem");

    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound nbtTagCompound) {
        nbtTagCompound.setInteger("TileID", Block.getIdFromBlock(this.myBlock));
        nbtTagCompound.setByte("Meta", (byte) this.myMeta);
        nbtTagCompound.setShort("Time", this.slideTime);
        nbtTagCompound.setFloat("MoveX", this.moveX);
        nbtTagCompound.setFloat("MoveY", this.moveY);
        nbtTagCompound.setFloat("MoveZ", this.moveZ);
        nbtTagCompound.setBoolean("CanDropItem", canDropItem);
    }

    public Block getBlock() {
        return this.myBlock;
    }

    public int getMeta() {
        return this.myMeta;
    }

    /**
     * Called by the server when constructing the spawn packet. Data should be added to the provided stream.
     *
     * @param buffer The packet data stream
     */
    @Override
    public void writeSpawnData(ByteBuf buffer) {
        int blockData = Block.getIdFromBlock(this.myBlock) + (this.myMeta << 16);

        buffer.writeInt(blockData);
        // direction and age are essential for matching the motion and reversal
        buffer.writeFloat(moveX);
        buffer.writeFloat(moveY);
        buffer.writeFloat(moveZ);
        buffer.writeShort(slideTime);

        // System.out.println("Wrote additional spawn data as " + blockData);
    }

    /**
     * Called by the client when it receives a Entity spawn packet. Data should be read out of the stream in the same
     * way as it was written.
     *
     * @param data The packet data stream
     */
    @Override
    public void readSpawnData(ByteBuf additionalData) {
        int blockData = additionalData.readInt();

        this.myBlock = Block.getBlockById(blockData & 65535);
        this.myMeta = blockData >> 16;
        moveX = additionalData.readFloat();
        moveY = additionalData.readFloat();
        moveZ = additionalData.readFloat();
        slideTime = additionalData.readShort();

        // System.out.println("Read additional spawn data as " + blockData + " so my block is " +
        // this.myBlock);

    }

    /**
     * Returns true if this entity should push and be pushed by other entities when colliding.
     */
    public boolean canBePushed() {
        return false;
    }

    public boolean isPushedByWater() {
        return false;
    }
}
