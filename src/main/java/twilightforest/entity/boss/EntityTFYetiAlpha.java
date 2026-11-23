package twilightforest.entity.boss;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIArrowAttack;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;

import twilightforest.TFAchievementPage;
import twilightforest.TFFeature;
import twilightforest.TwilightForestMod;
import twilightforest.block.TFBlocks;
import twilightforest.entity.ai.EntityAIStayNearHome;
import twilightforest.entity.ai.EntityAITFThrowRider;
import twilightforest.entity.ai.EntityAITFYetiRampage;
import twilightforest.entity.ai.EntityAITFYetiTired;
import twilightforest.item.TFItems;
import twilightforest.world.ChunkProviderTwilightForest;
import twilightforest.world.TFWorldChunkManager;
import twilightforest.world.WorldProviderTwilightForest;

public class EntityTFYetiAlpha extends EntityMob implements IRangedAttackMob {

    private static final int RAMPAGE_FLAG = 16;
    private static final int TIRED_FLAG = 17;
    private static final int COLLISION_BREAK_TICKS = 15;
    private static final int MAX_BREAK_EFFECTS_PER_PASS = 16;
    private static final int MAX_WORLD_HEIGHT = 256;

    private int collisionCounter;
    private boolean canRampage;

    public EntityTFYetiAlpha(World par1World) {
        super(par1World);
        this.setSize(3.8F, 5.0F);

        this.getNavigator().setAvoidsWater(true);
        this.tasks.addTask(1, new EntityAITFYetiTired(this, 100));
        this.tasks.addTask(2, new EntityAITFThrowRider(this, 1.0F));
        this.tasks.addTask(3, new EntityAIStayNearHome(this, 2.0F));
        this.tasks.addTask(4, new EntityAITFYetiRampage(this, 10, 180));

        this.tasks.addTask(5, new EntityAIArrowAttack(this, 1.0D, 40, 40, 40.0F));
        this.tasks.addTask(6, new EntityAIWander(this, 2.0F));
        this.tasks.addTask(7, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
        this.tasks.addTask(8, new EntityAILookIdle(this));
        this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, false));
        this.targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityPlayer.class, 0, false) {

            /**
             * Execute a one shot task or start executing a continuous task
             */
            @Override
            public void startExecuting() {
                this.taskOwner
                        .playSound(TwilightForestMod.ID + ":mob.yeti.alert", 4F, 0.75F + rand.nextFloat() * 0.25F);
                this.taskOwner.setAttackTarget(this.targetEntity);
                super.startExecuting();
            }
        });

        this.experienceValue = 317;

    }

    /**
     * Returns true if the newer Entity AI code should be run
     */
    @Override
    protected boolean isAIEnabled() {
        return true;
    }

    protected void entityInit() {
        super.entityInit();
        this.dataWatcher.addObject(RAMPAGE_FLAG, (byte) 0);
        this.dataWatcher.addObject(TIRED_FLAG, (byte) 0);
    }

    /**
     * Set monster attributes
     */
    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(200.0D); // max health
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.38D); // movement speed
        this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(1.0D);
        this.getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(40.0D);
    }

    /**
     * Called frequently so the entity can update its state every tick as required. For example, zombies and skeletons
     * use this to react to sunlight and start to burn.
     */
    @Override
    public void onLivingUpdate() {
        if (!this.worldObj.isRemote) {
            despawnIfInvalid();
            if (this.isDead) {
                return;
            }

            // Only the server controls passengers and block destruction.
            if (this.riddenByEntity != null && this.riddenByEntity.isSneaking()) {
                this.riddenByEntity.setSneaking(false);
            }
        }

        super.onLivingUpdate();

        if (this.riddenByEntity != null) {
            this.getLookHelper().setLookPositionWithEntity(this.riddenByEntity, 100.0F, 100.0F);
        }

        if (!this.worldObj.isRemote) {
            this.collisionCounter = this.isCollided ? this.collisionCounter + 1 : 0;
            if (this.collisionCounter >= COLLISION_BREAK_TICKS) {
                this.collisionCounter = 0;
                destroyBlocksInAABB(this.boundingBox);
            }
        }

        if (!this.worldObj.isRemote) {
            return;
        }

        if (this.isRampaging()) {
            float rotation = this.ticksExisted / 10.0F;
            for (int i = 0; i < 20; i++) {
                addSnowEffect(rotation + i * 50.0F, i + rotation);
            }
            this.limbSwingAmount += 0.6F;
        }

        if (this.isTired()) {
            for (int i = 0; i < 20; i++) {
                this.worldObj.spawnParticle(
                        "splash",
                        this.posX + (this.rand.nextDouble() - 0.5D) * this.width * 0.5D,
                        this.posY + this.getEyeHeight(),
                        this.posZ + (this.rand.nextDouble() - 0.5D) * this.width * 0.5D,
                        (this.rand.nextFloat() - 0.5F) * 0.75F,
                        0.0D,
                        (this.rand.nextFloat() - 0.5F) * 0.75F);
            }
        }
    }

    protected void despawnIfInvalid() {
        // check to see if we're valid
        if (!worldObj.isRemote && worldObj.difficultySetting == EnumDifficulty.PEACEFUL) {
            despawnMe();
        }
    }

    /**
     * Despawn the alpha yeti, and restore the boss spawner at our home location, if set
     */
    protected void despawnMe() {
        if (this.hasHome()) {
            ChunkCoordinates home = this.getHomePosition();
            this.worldObj.setBlock(home.posX, home.posY, home.posZ, TFBlocks.bossSpawner, 7, 2);
        }
        setDead();
    }

    private void addSnowEffect(float rotation, float hgt) {
        double px = 3F * Math.cos(rotation);
        double py = hgt % 5F;
        double pz = 3F * Math.sin(rotation);

        TwilightForestMod.proxy.spawnParticle(
                this.worldObj,
                "snowstuff",
                this.lastTickPosX + px,
                this.lastTickPosY + py,
                this.lastTickPosZ + pz,
                0,
                0,
                0);
    }

    /**
     * Called when a player interacts with a mob. e.g. gets milk from a cow, gets into the saddle on a pig.
     */
    @Override
    public boolean interact(EntityPlayer par1EntityPlayer) {
        if (super.interact(par1EntityPlayer)) {
            return true;
        }
        if (this.worldObj.isRemote || this.isDead || this.riddenByEntity != null) {
            return false;
        }
        // keep the existing rclick mounting interaction but never steal a rider
        // from another entity
        if (par1EntityPlayer.ridingEntity != null) {
            return false;
        }
        par1EntityPlayer.mountEntity(this);
        return true;
    }

    /**
     * Pick up things we attack!
     */
    @Override
    public boolean attackEntityAsMob(Entity par1Entity) {
        if (!this.worldObj.isRemote && this.riddenByEntity == null
                && par1Entity instanceof EntityLivingBase
                && par1Entity.ridingEntity == null
                && par1Entity.isEntityAlive()) {
            this.playSound(TwilightForestMod.ID + ":mob.yeti.grab", 4.0F, 0.75F + this.rand.nextFloat() * 0.25F);
            par1Entity.mountEntity(this);
        }
        return super.attackEntityAsMob(par1Entity);
    }

    /**
     * Called when the entity is attacked.
     */
    public boolean attackEntityFrom(DamageSource par1DamageSource, float par2) {
        // The ranged phase is deliberately immune to projectiles.
        if (!this.canRampage && !this.isTired() && par1DamageSource.isProjectile()) {
            return false;
        }
        boolean success = super.attackEntityFrom(par1DamageSource, par2);
        if (success && !this.worldObj.isRemote && this.isEntityAlive()) {
            this.canRampage = true;
        }
        return success;
    }

    /**
     * Drop 0-2 items of this living's type. @param par1 - Whether this entity has recently been hit by a player. @param
     * par2 - Level of Looting used to kill this mob.
     */
    @Override
    protected void dropFewItems(boolean flag, int looting) {
        Item fur = getDropItem();
        if (fur != null) {
            int drops = 6 + rand.nextInt(6 + looting);
            for (int d = 0; d < drops; d++) {
                this.dropItem(fur, 1);
            }

        }

        Item bombs = TFItems.iceBomb;
        int drops = 6 + rand.nextInt(6 + looting);
        for (int d = 0; d < drops; d++) {
            this.dropItem(bombs, 1);
        }

        // trophy
        this.entityDropItem(new ItemStack(TFItems.trophy, 1, 7), 0);

    }

    @Override
    protected Item getDropItem() {
        return TFItems.alphaFur;
    }

    /**
     * Put the player out in front of where we are
     */
    @Override
    public void updateRiderPosition() {
        if (this.riddenByEntity != null) {
            Vec3 riderPos = this.getRiderPosition();

            this.riddenByEntity.setPosition(riderPos.xCoord, riderPos.yCoord, riderPos.zCoord);
        }
    }

    /**
     * Returns the Y offset from the entity's position for any entity riding this one.
     */
    @Override
    public double getMountedYOffset() {
        return 5.75D;
    }

    /**
     * Used to both get a rider position and to push out of blocks
     */
    public Vec3 getRiderPosition() {
        if (this.riddenByEntity != null) {
            float distance = 0.4F;

            double var1 = Math.cos((this.rotationYaw + 90) * Math.PI / 180.0D) * distance;
            double var3 = Math.sin((this.rotationYaw + 90) * Math.PI / 180.0D) * distance;

            return Vec3.createVectorHelper(
                    this.posX + var1,
                    this.posY + this.getMountedYOffset() + this.riddenByEntity.getYOffset(),
                    this.posZ + var3);
        } else {
            return Vec3.createVectorHelper(this.posX, this.posY, this.posZ);
        }
    }

    /**
     * If a rider of this entity can interact with this entity. Should return true on the ridden entity if so.
     *
     * @return if the entity can be interacted with from a rider
     */
    public boolean canRiderInteract() {
        return true;
    }

    /**
     * Destroys all blocks that aren't associated with 'The End' inside the given bounding box.
     */
    public boolean destroyBlocksInAABB(AxisAlignedBB par1AxisAlignedBB) {
        if (this.worldObj.isRemote || !this.worldObj.getGameRules().getGameRuleBooleanValue("mobGriefing")) {
            return false;
        }

        int minX = MathHelper.floor_double(par1AxisAlignedBB.minX);
        int minY = Math.max(0, MathHelper.floor_double(par1AxisAlignedBB.minY));
        int minZ = MathHelper.floor_double(par1AxisAlignedBB.minZ);
        int maxX = MathHelper.floor_double(par1AxisAlignedBB.maxX);
        int maxY = Math.min(MAX_WORLD_HEIGHT - 1, MathHelper.floor_double(par1AxisAlignedBB.maxY));
        int maxZ = MathHelper.floor_double(par1AxisAlignedBB.maxZ);
        boolean wasBlocked = false;
        int effects = 0;

        for (int x = minX; x <= maxX; ++x) {
            for (int y = minY; y <= maxY; ++y) {
                for (int z = minZ; z <= maxZ; ++z) {
                    if (!this.worldObj.blockExists(x, y, z)) {
                        continue;
                    }
                    Block block = this.worldObj.getBlock(x, y, z);
                    if (block == Blocks.air) {
                        continue;
                    }
                    if (!canDestroyBlock(x, y, z, block)) {
                        wasBlocked = true;
                        continue;
                    }
                    // only send break effects after a successful change and cap
                    // packets during the large rampage collision scans
                    int metadata = effects < MAX_BREAK_EFFECTS_PER_PASS ? this.worldObj.getBlockMetadata(x, y, z) : 0;
                    if (this.worldObj.setBlock(x, y, z, Blocks.air, 0, 2) && effects++ < MAX_BREAK_EFFECTS_PER_PASS) {
                        this.worldObj.playAuxSFX(2001, x, y, z, Block.getIdFromBlock(block) + (metadata << 12));
                    }
                }
            }
        }
        return wasBlocked;
    }

    private boolean canDestroyBlock(int x, int y, int z, Block block) {
        return block != Blocks.air && block != Blocks.obsidian
                && block != Blocks.end_stone
                && block != Blocks.bedrock
                && this.worldObj.getTileEntity(x, y, z) == null
                && block.getBlockHardness(this.worldObj, x, y, z) >= 0.0F;
    }

    public void makeRandomBlockFall() {
        // begin turning blocks into falling blocks
        makeRandomBlockFall(30);
    }

    private void makeRandomBlockFall(int range) {
        // find a block nearby
        int bx = MathHelper.floor_double(this.posX) + this.getRNG().nextInt(range) - this.getRNG().nextInt(range);
        int bz = MathHelper.floor_double(this.posZ) + this.getRNG().nextInt(range) - this.getRNG().nextInt(range);
        int by = MathHelper.floor_double(this.posY + this.getEyeHeight());

        makeBlockFallAbove(bx, bz, by);
    }

    private void makeBlockFallAbove(int bx, int bz, int by) {
        if (this.worldObj.isRemote || !this.worldObj.getGameRules().getGameRuleBooleanValue("mobGriefing")
                || !this.worldObj.blockExists(bx, by, bz)
                || !this.worldObj.isAirBlock(bx, by, bz)) {
            return;
        }
        for (int y = by + 1; y < Math.min(by + 30, MAX_WORLD_HEIGHT); ++y) {
            if (!this.worldObj.blockExists(bx, y, bz)) {
                break;
            }
            if (!this.worldObj.isAirBlock(bx, y, bz)) {
                makeBlockFall(bx, y, bz);
                return;
            }
        }
    }

    public void makeNearbyBlockFall() {
        makeRandomBlockFall(15);
    }

    public void makeBlockAboveTargetFall() {
        if (this.getAttackTarget() != null) {

            int bx = MathHelper.floor_double(this.getAttackTarget().posX);
            int bz = MathHelper.floor_double(this.getAttackTarget().posZ);
            int by = MathHelper.floor_double(this.getAttackTarget().posY + this.getAttackTarget().getEyeHeight());

            makeBlockFallAbove(bx, bz, by);
        }

    }

    private void makeBlockFall(int bx, int by, int bz) {
        if (this.worldObj.isRemote || !this.worldObj.getGameRules().getGameRuleBooleanValue("mobGriefing")
                || by < 1
                || by >= MAX_WORLD_HEIGHT
                || !this.worldObj.blockExists(bx, by, bz)) {
            return;
        }
        Block block = this.worldObj.getBlock(bx, by, bz);
        if (!canDestroyBlock(bx, by, bz, block)) {
            return;
        }
        int metadata = this.worldObj.getBlockMetadata(bx, by, bz);
        if (!this.worldObj.setBlock(bx, by, bz, Blocks.packed_ice)) {
            return;
        }
        this.worldObj.playAuxSFX(2001, bx, by, bz, Block.getIdFromBlock(block) + (metadata << 12));
        this.worldObj.spawnEntityInWorld(new EntityTFFallingIce(this.worldObj, bx, by - 3, bz));
    }

    /**
     * Attack the specified entity using a ranged attack.
     */
    public void attackEntityWithRangedAttack(EntityLivingBase target, float par2) {
        if (this.worldObj.isRemote || this.isDead
                || target == null
                || !target.isEntityAlive()
                || this.canRampage
                || this.isRampaging()
                || this.isTired()) {
            return;
        }

        EntityTFIceBomb ice = new EntityTFIceBomb(this.worldObj, this);
        double dx = target.posX - ice.posX;
        double dy = target.posY + target.getEyeHeight() - 1.1D - ice.posY;
        double dz = target.posZ - ice.posZ;
        double horizontalDistance = MathHelper.sqrt_double(dx * dx + dz * dz);
        ice.setThrowableHeading(dx, dy + horizontalDistance * 0.2D, dz, 0.75F, 12.0F);

        this.playSound(TwilightForestMod.ID + ":mob.yeti.pant", 1.0F, 1.0F / (this.getRNG().nextFloat() * 0.4F + 0.8F));
        this.worldObj.spawnEntityInWorld(ice);
    }

    public boolean canDespawn() {
        return false;
    }

    public boolean canRampage() {
        return this.canRampage;
    }

    /**
     * Set whether this yeti is currently in rampage mode.
     */
    public void setRampaging(boolean par1) {
        byte newState = (byte) (par1 ? 1 : 0);
        if (!this.worldObj.isRemote && this.getDataWatcher().getWatchableObjectByte(RAMPAGE_FLAG) != newState) {
            this.getDataWatcher().updateObject(RAMPAGE_FLAG, newState);
            if (par1) {
                this.playSound(TwilightForestMod.ID + ":mob.yeti.roar", 4.0F, 0.75F + this.rand.nextFloat() * 0.25F);
            }
        }
    }

    /**
     * Return whether this yeti is currently in rampage mode.
     */
    public boolean isRampaging() {
        return this.getDataWatcher().getWatchableObjectByte(RAMPAGE_FLAG) == 1;
    }

    /**
     * Set whether this yeti is currently tired.
     */
    public void setTired(boolean par1) {
        byte newState = (byte) (par1 ? 1 : 0);
        if (!this.worldObj.isRemote && this.getDataWatcher().getWatchableObjectByte(TIRED_FLAG) != newState) {
            this.getDataWatcher().updateObject(TIRED_FLAG, newState);
        }
        if (par1) {
            this.canRampage = false;
        }
    }

    /**
     * Return whether this yeti is currently tired.
     */
    public boolean isTired() {
        return this.getDataWatcher().getWatchableObjectByte(TIRED_FLAG) == 1;
    }

    /**
     * Called when the mob is falling. Calculates and applies fall damage.
     */
    protected void fall(float par1) {
        super.fall(par1);

        if (this.isRampaging()) {
            // make jump effects
            this.playSound(
                    TwilightForestMod.ID + ":mob.yeti.pant",
                    1.0F,
                    1.0F / (this.getRNG().nextFloat() * 0.4F + 0.8F));

            int i = MathHelper.floor_double(this.posX);
            int j = MathHelper.floor_double(this.posY - 0.20000000298023224D - (double) this.yOffset);
            int k = MathHelper.floor_double(this.posZ);

            this.worldObj.playAuxSFX(2006, i, j, k, 20);
            this.worldObj.playAuxSFX(2006, i, j, k, 30);

            // hit everything around
            if (!this.worldObj.isRemote) {
                hitNearbyEntities();
            }

        }
    }

    private void hitNearbyEntities() {
        List<Entity> nearby = this.worldObj
                .getEntitiesWithinAABBExcludingEntity(this, this.boundingBox.expand(5.0D, 0.0D, 5.0D));
        for (Entity entity : nearby) {
            if (entity instanceof EntityLivingBase && entity != this.riddenByEntity
                    && entity.isEntityAlive()
                    && entity.attackEntityFrom(DamageSource.causeMobDamage(this), 5.0F)) {
                entity.motionY += 0.4D;
            }
        }
    }

    /**
     * Trigger achievement when killed
     */
    @Override
    public void onDeath(DamageSource par1DamageSource) {
        super.onDeath(par1DamageSource);
        if (!this.worldObj.isRemote && par1DamageSource.getEntity() instanceof EntityPlayer
                && ((EntityPlayer) par1DamageSource.getEntity()).dimension == TwilightForestMod.dimensionID) {
            ((EntityPlayer) par1DamageSource.getEntity()).triggerAchievement(TFAchievementPage.twilightHunter);
            ((EntityPlayer) par1DamageSource.getEntity()).triggerAchievement(TFAchievementPage.twilightProgressYeti);
        }

        // mark the lair as defeated
        if (!worldObj.isRemote) {
            int dx = MathHelper.floor_double(this.posX);
            int dy = MathHelper.floor_double(this.posY);
            int dz = MathHelper.floor_double(this.posZ);

            if (worldObj.provider instanceof WorldProviderTwilightForest) {
                ChunkProviderTwilightForest chunkProvider = ((WorldProviderTwilightForest) worldObj.provider)
                        .getChunkProvider();
                TFFeature nearbyFeature = ((TFWorldChunkManager) worldObj.provider.worldChunkMgr)
                        .getFeatureAt(dx, dz, worldObj);

                if (nearbyFeature == TFFeature.yetiCave) {
                    chunkProvider.setStructureConquered(dx, dy, dz, true);
                }
            }
        }
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound nbttagcompound) {
        super.writeEntityToNBT(nbttagcompound);
        ChunkCoordinates home = this.getHomePosition();
        if (home != null) {
            nbttagcompound.setTag("Home", newDoubleNBTList(home.posX, home.posY, home.posZ));
        }
        nbttagcompound.setBoolean("HasHome", this.hasHome());
        nbttagcompound.setBoolean("CanRampage", this.canRampage);
        nbttagcompound.setBoolean("Rampaging", this.isRampaging());
        nbttagcompound.setBoolean("Tired", this.isTired());
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound nbttagcompound) {
        super.readEntityFromNBT(nbttagcompound);
        if (nbttagcompound.hasKey("Home", 9)) {
            NBTTagList home = nbttagcompound.getTagList("Home", 6);
            if (home.tagCount() >= 3) {
                this.setHomeArea(
                        (int) home.func_150309_d(0),
                        (int) home.func_150309_d(1),
                        (int) home.func_150309_d(2),
                        30);
            }
        }
        if (!nbttagcompound.getBoolean("HasHome")) {
            this.detachHome();
        }
        boolean tired = nbttagcompound.getBoolean("Tired") || nbttagcompound.getBoolean("Rampaging");
        this.setRampaging(false);
        this.setTired(tired);
        this.canRampage = !tired && nbttagcompound.getBoolean("CanRampage");
    }

    /**
     * Returns the sound this mob makes while it's alive.
     */
    @Override
    protected String getLivingSound() {
        return TwilightForestMod.ID + ":mob.yeti.growl";
    }

    /**
     * Basically a public getter for living sounds
     */
    public String getTrophySound() {
        return this.getLivingSound();
    }

    /**
     * Returns the sound this mob makes when it is hurt.
     */
    @Override
    protected String getHurtSound() {
        return TwilightForestMod.ID + ":mob.yeti.hurt";
    }

    /**
     * Returns the sound this mob makes on death.
     */
    @Override
    protected String getDeathSound() {
        return TwilightForestMod.ID + ":mob.yeti.death";
    }

}
