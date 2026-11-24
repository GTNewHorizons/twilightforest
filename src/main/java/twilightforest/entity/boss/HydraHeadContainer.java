package twilightforest.entity.boss;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.boss.EntityDragonPart;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.EnumDifficulty;

import twilightforest.TwilightForestMod;

/**
 * This class holds the state data for a single hydra head
 *
 * @author Ben
 *
 */
public class HydraHeadContainer {

    // balancing factors
    private static final int FLAME_BURN_FACTOR = 3;
    private static final int FLAME_DAMAGE = 19;
    private static final int BITE_DAMAGE = 48;
    private static final double NORMAL_FLAME_TRACKING_SPEED = 0.04D;
    private static final double HARD_FLAME_TRACKING_SPEED = 0.1D;
    private static final int NECK_SEGMENT_COUNT = 5;

    public static final int NEXT_AUTOMATIC = -1;

    public static final int STATE_IDLE = 0;
    public static final int STATE_BITE_BEGINNING = 1;
    public static final int STATE_BITE_READY = 2;
    public static final int STATE_BITE_BITING = 3;
    public static final int STATE_BITE_ENDING = 4;
    public static final int STATE_FLAME_BEGINNING = 5;
    public static final int STATE_FLAME_BREATHING = 6;
    public static final int STATE_FLAME_ENDING = 7;
    public static final int STATE_MORTAR_BEGINNING = 8;
    public static final int STATE_MORTAR_LAUNCH = 9;
    public static final int STATE_MORTAR_ENDING = 10;
    public static final int STATE_DYING = 11;
    public static final int STATE_DEAD = 12;
    public static final int STATE_ATTACK_COOLDOWN = 13;
    public static final int STATE_BORN = 14;
    public static final int STATE_ROAR_START = 15;
    public static final int STATE_ROAR_RAWR = 16;

    public static final int NUM_STATES = 17;

    public EntityTFHydraHead headEntity;
    public EntityTFHydraNeck necka;
    public EntityTFHydraNeck neckb;
    public EntityTFHydraNeck neckc;
    public EntityTFHydraNeck neckd;
    public EntityTFHydraNeck necke;
    private final EntityTFHydraNeck[] neckParts;

    public Entity targetEntity;

    public double targetX;
    public double targetY;
    public double targetZ;

    public int prevState;
    public int currentState;
    public int nextState = NEXT_AUTOMATIC;

    public boolean didRoar;

    public boolean isSecondaryAttacking;

    public int ticksNeeded;
    public int ticksProgress;

    public final int headNum;
    private int missingHeadTicks;

    public int damageTaken;
    private float fractionalDamage;
    public int respawnCounter;

    public final EntityTFHydra hydraObj;

    public int[] nextStates;
    public int[] stateDurations;
    public float[][] stateNeckLength;
    public float[][] stateXRotations;
    public float[][] stateYRotations;
    public float[][] stateMouthOpen;
    private double flameBreathTrackingSpeed = NORMAL_FLAME_TRACKING_SPEED;

    public HydraHeadContainer(EntityTFHydra hydra, int number, boolean startActive) {
        this.headNum = number;
        this.hydraObj = hydra;

        this.damageTaken = 0;
        this.respawnCounter = -1;
        this.didRoar = false;

        // is this a good place to initialize the necks?
        necka = new EntityTFHydraNeck(hydraObj, "neck" + headNum + "a", 2F, 2F);
        neckb = new EntityTFHydraNeck(hydraObj, "neck" + headNum + "b", 2F, 2F);
        neckc = new EntityTFHydraNeck(hydraObj, "neck" + headNum + "c", 2F, 2F);
        neckd = new EntityTFHydraNeck(hydraObj, "neck" + headNum + "d", 2F, 2F);
        necke = new EntityTFHydraNeck(hydraObj, "neck" + headNum + "e", 2F, 2F);
        this.neckParts = new EntityTFHydraNeck[] { necka, neckb, neckc, neckd, necke };

        // state variables, what state is next in the chain
        nextStates = new int[NUM_STATES];

        nextStates[STATE_IDLE] = STATE_IDLE;

        nextStates[STATE_BITE_BEGINNING] = STATE_BITE_READY;
        nextStates[STATE_BITE_READY] = STATE_BITE_BITING;
        nextStates[STATE_BITE_BITING] = STATE_BITE_ENDING;
        nextStates[STATE_BITE_ENDING] = STATE_ATTACK_COOLDOWN;

        nextStates[STATE_FLAME_BEGINNING] = STATE_FLAME_BREATHING;
        nextStates[STATE_FLAME_BREATHING] = STATE_FLAME_ENDING;
        nextStates[STATE_FLAME_ENDING] = STATE_ATTACK_COOLDOWN;

        nextStates[STATE_MORTAR_BEGINNING] = STATE_MORTAR_LAUNCH;
        nextStates[STATE_MORTAR_LAUNCH] = STATE_MORTAR_ENDING;
        nextStates[STATE_MORTAR_ENDING] = STATE_ATTACK_COOLDOWN;

        nextStates[STATE_ATTACK_COOLDOWN] = STATE_IDLE;

        nextStates[STATE_DYING] = STATE_DEAD;

        nextStates[STATE_DEAD] = STATE_DEAD;

        nextStates[STATE_BORN] = STATE_ROAR_START;
        nextStates[STATE_ROAR_START] = STATE_ROAR_RAWR;
        nextStates[STATE_ROAR_RAWR] = STATE_IDLE;

        // state durations, how long in each state
        stateDurations = new int[NUM_STATES];

        setupStateDurations();

        // state positions, where is each state positioned?
        stateNeckLength = new float[hydraObj.numHeads][NUM_STATES];
        stateXRotations = new float[hydraObj.numHeads][NUM_STATES];
        stateYRotations = new float[hydraObj.numHeads][NUM_STATES];
        stateMouthOpen = new float[hydraObj.numHeads][NUM_STATES];

        setupStateRotations();

        if (startActive) {
            this.prevState = STATE_IDLE;
            this.currentState = STATE_IDLE;
            this.nextState = NEXT_AUTOMATIC;
            this.ticksNeeded = 60;
            this.ticksProgress = 60;
        } else {
            this.prevState = STATE_DEAD;
            this.currentState = STATE_DEAD;
            this.nextState = NEXT_AUTOMATIC;
            this.ticksNeeded = 20;
            this.ticksProgress = 20;
        }
    }

    protected void setupStateDurations() {
        stateDurations[STATE_IDLE] = 10;

        stateDurations[STATE_BITE_BEGINNING] = 40;
        stateDurations[STATE_BITE_READY] = 80;
        stateDurations[STATE_BITE_BITING] = 7;
        stateDurations[STATE_BITE_ENDING] = 40;

        stateDurations[STATE_FLAME_BEGINNING] = 40;
        stateDurations[STATE_FLAME_BREATHING] = 100;
        stateDurations[STATE_FLAME_ENDING] = 30;

        stateDurations[STATE_MORTAR_BEGINNING] = 40;
        stateDurations[STATE_MORTAR_LAUNCH] = 25;
        stateDurations[STATE_MORTAR_ENDING] = 30;

        stateDurations[STATE_ATTACK_COOLDOWN] = 80;

        stateDurations[STATE_DYING] = 70;
        stateDurations[STATE_DEAD] = 20;

        stateDurations[STATE_BORN] = 20;

        stateDurations[STATE_ROAR_START] = 10;
        stateDurations[STATE_ROAR_RAWR] = 50;

    }

    protected void setupStateRotations() {
        setAnimation(0, STATE_IDLE, 60, 0, 7, 0);
        setAnimation(1, STATE_IDLE, 10, 60, 9, 0);
        setAnimation(2, STATE_IDLE, 10, -60, 9, 0);
        setAnimation(3, STATE_IDLE, 50, 90, 8, 0);
        setAnimation(4, STATE_IDLE, 50, -90, 8, 0);
        setAnimation(5, STATE_IDLE, -10, 90, 9, 0);
        setAnimation(6, STATE_IDLE, -10, -90, 9, 0);

        setAnimation(0, STATE_ATTACK_COOLDOWN, 60, 0, 7, 0);
        setAnimation(1, STATE_ATTACK_COOLDOWN, 10, 60, 9, 0);
        setAnimation(2, STATE_ATTACK_COOLDOWN, 10, -60, 9, 0);
        setAnimation(3, STATE_ATTACK_COOLDOWN, 50, 90, 8, 0);
        setAnimation(4, STATE_ATTACK_COOLDOWN, 50, -90, 8, 0);
        setAnimation(5, STATE_ATTACK_COOLDOWN, -10, 90, 9, 0);
        setAnimation(6, STATE_ATTACK_COOLDOWN, -10, -90, 9, 0);

        setAnimation(0, STATE_FLAME_BEGINNING, 50, 0, 8, 0.75F);
        setAnimation(1, STATE_FLAME_BEGINNING, 30, 45, 9, 0.75F);
        setAnimation(2, STATE_FLAME_BEGINNING, 30, -45, 9, 0.75F);
        setAnimation(3, STATE_FLAME_BEGINNING, 50, 90, 8, 0.75F);
        setAnimation(4, STATE_FLAME_BEGINNING, 50, -90, 8, 0.75F);
        setAnimation(5, STATE_FLAME_BEGINNING, -10, 90, 9, 0.75F);
        setAnimation(6, STATE_FLAME_BEGINNING, -10, -90, 9, 0.75F);

        setAnimation(0, STATE_FLAME_BREATHING, 45, 0, 8, 1);
        setAnimation(1, STATE_FLAME_BREATHING, 30, 60, 9, 1);
        setAnimation(2, STATE_FLAME_BREATHING, 30, -60, 9, 1);
        setAnimation(3, STATE_FLAME_BREATHING, 50, 90, 8, 1);
        setAnimation(4, STATE_FLAME_BREATHING, 50, -90, 8, 1);
        setAnimation(5, STATE_FLAME_BREATHING, -10, 90, 9, 1);
        setAnimation(6, STATE_FLAME_BREATHING, -10, -90, 9, 1);

        setAnimation(0, STATE_FLAME_ENDING, 60, 0, 7, 0);
        setAnimation(1, STATE_FLAME_ENDING, 10, 45, 9, 0);
        setAnimation(2, STATE_FLAME_ENDING, 10, -45, 9, 0);
        setAnimation(3, STATE_FLAME_ENDING, 50, 90, 8, 0);
        setAnimation(4, STATE_FLAME_ENDING, 50, -90, 8, 0);
        setAnimation(5, STATE_FLAME_ENDING, -10, 90, 9, 0);
        setAnimation(6, STATE_FLAME_ENDING, -10, -90, 9, 0);

        setAnimation(0, STATE_BITE_BEGINNING, -5, 60, 5, 0.25f);
        setAnimation(1, STATE_BITE_BEGINNING, -10, 60, 9, 0.25f);
        setAnimation(2, STATE_BITE_BEGINNING, -10, -60, 9, 0.25f);

        setAnimation(0, STATE_BITE_READY, -5, 60, 5, 1);
        setAnimation(1, STATE_BITE_READY, -10, 60, 9, 1);
        setAnimation(2, STATE_BITE_READY, -10, -60, 9, 1);

        setAnimation(0, STATE_BITE_BITING, -5, -30, 5, 0.2F);
        setAnimation(1, STATE_BITE_BITING, -10, -30, 5, 0.2F);
        setAnimation(2, STATE_BITE_BITING, -10, 30, 5, 0.2F);

        setAnimation(0, STATE_BITE_ENDING, 60, 0, 7, 0);
        setAnimation(1, STATE_BITE_ENDING, -10, 60, 9, 0);
        setAnimation(2, STATE_BITE_ENDING, -10, -60, 9, 0);

        setAnimation(0, STATE_MORTAR_BEGINNING, 50, 0, 8, 0.75F);
        setAnimation(1, STATE_MORTAR_BEGINNING, 30, 45, 9, 0.75F);
        setAnimation(2, STATE_MORTAR_BEGINNING, 30, -45, 9, 0.75F);
        setAnimation(3, STATE_MORTAR_BEGINNING, 50, 90, 8, 0.75F);
        setAnimation(4, STATE_MORTAR_BEGINNING, 50, -90, 8, 0.75F);
        setAnimation(5, STATE_MORTAR_BEGINNING, -10, 90, 9, 0.75F);
        setAnimation(6, STATE_MORTAR_BEGINNING, -10, -90, 9, 0.75F);

        setAnimation(0, STATE_MORTAR_LAUNCH, 45, 0, 8, 1);
        setAnimation(1, STATE_MORTAR_LAUNCH, 30, 60, 9, 1);
        setAnimation(2, STATE_MORTAR_LAUNCH, 30, -60, 9, 1);
        setAnimation(3, STATE_MORTAR_LAUNCH, 50, 90, 8, 1);
        setAnimation(4, STATE_MORTAR_LAUNCH, 50, -90, 8, 1);
        setAnimation(5, STATE_MORTAR_LAUNCH, -10, 90, 9, 1);
        setAnimation(6, STATE_MORTAR_LAUNCH, -10, -90, 9, 1);

        setAnimation(0, STATE_MORTAR_ENDING, 60, 0, 7, 0);
        setAnimation(1, STATE_MORTAR_ENDING, 10, 45, 9, 0);
        setAnimation(2, STATE_MORTAR_ENDING, 10, -45, 9, 0);
        setAnimation(3, STATE_MORTAR_ENDING, 50, 90, 8, 0);
        setAnimation(4, STATE_MORTAR_ENDING, 50, -90, 8, 0);
        setAnimation(5, STATE_MORTAR_ENDING, -10, 90, 9, 0);
        setAnimation(6, STATE_MORTAR_ENDING, -10, -90, 9, 0);

        setAnimation(0, STATE_DYING, -20, 0, 7, 0);
        setAnimation(1, STATE_DYING, -20, 60, 9, 0);
        setAnimation(2, STATE_DYING, -20, -60, 9, 0);
        setAnimation(3, STATE_DYING, -20, 90, 8, 0);
        setAnimation(4, STATE_DYING, -20, -90, 8, 0);
        setAnimation(5, STATE_DYING, -10, 90, 9, 0);
        setAnimation(6, STATE_DYING, -10, -90, 9, 0);

        setAnimation(0, STATE_DEAD, 0, 179, 4, 0);
        setAnimation(1, STATE_DEAD, 0, 179, 4, 0);
        setAnimation(2, STATE_DEAD, 0, -180, 4, 0);
        setAnimation(3, STATE_DEAD, 0, 179, 4, 0);
        setAnimation(4, STATE_DEAD, 0, -180, 4, 0);
        setAnimation(5, STATE_DEAD, 0, 179, 4, 0);
        setAnimation(6, STATE_DEAD, 0, -180, 4, 0);

        setAnimation(0, STATE_BORN, 60, 0, 7, 0);
        setAnimation(1, STATE_BORN, 10, 60, 9, 0);
        setAnimation(2, STATE_BORN, 10, -60, 9, 0);
        setAnimation(3, STATE_BORN, 50, 90, 8, 0);
        setAnimation(4, STATE_BORN, 50, -90, 8, 0);
        setAnimation(5, STATE_BORN, -10, 90, 9, 0);
        setAnimation(6, STATE_BORN, -10, -90, 9, 0);

        setAnimation(0, STATE_ROAR_START, 60, 0, 7, 0.25F);
        setAnimation(1, STATE_ROAR_START, 10, 60, 9, 0.25F);
        setAnimation(2, STATE_ROAR_START, 10, -60, 9, 0.25F);
        setAnimation(3, STATE_ROAR_START, 50, 90, 8, 0.25F);
        setAnimation(4, STATE_ROAR_START, 50, -90, 8, 0.25F);
        setAnimation(5, STATE_ROAR_START, -10, 90, 9, 0.25F);
        setAnimation(6, STATE_ROAR_START, -10, -90, 9, 0.25F);

        setAnimation(0, STATE_ROAR_RAWR, 60, 0, 9, 1);
        setAnimation(1, STATE_ROAR_RAWR, 10, 60, 11, 1);
        setAnimation(2, STATE_ROAR_RAWR, 10, -60, 11, 1);
        setAnimation(3, STATE_ROAR_RAWR, 50, 90, 10, 1);
        setAnimation(4, STATE_ROAR_RAWR, 50, -90, 10, 1);
        setAnimation(5, STATE_ROAR_RAWR, -10, 90, 11, 1);
        setAnimation(6, STATE_ROAR_RAWR, -10, -90, 11, 1);

    }

    protected void setAnimation(int head, int state, float xRotation, float yRotation, float neckLength,
            float mouthOpen) {
        this.stateXRotations[head][state] = xRotation;
        this.stateYRotations[head][state] = yRotation;
        this.stateNeckLength[head][state] = neckLength;
        this.stateMouthOpen[head][state] = mouthOpen; // this doesn't really need to be set per-head, more per-state
    }

    public EntityTFHydraNeck[] getNeckArray() {
        return this.neckParts.clone();
    }

    /**
     * Called once per tick as part of the hydra entity update loop.
     */
    public void onUpdate() {
        // neck parts are managed by the hydra, not by the worlds entity tick loop
        for (EntityTFHydraNeck neck : this.neckParts) {
            neck.onUpdate();
        }

        if (this.headEntity == null || this.headEntity.isDead) {
            this.headEntity = findNearbyHead("head" + this.headNum);
        }
        if (this.headEntity == null) {
            if (!hydraObj.worldObj.isRemote && ++missingHeadTicks >= 100 && hydraObj.isEntityAlive()) {
                int x = MathHelper.floor_double(hydraObj.posX);
                int y = MathHelper.floor_double(hydraObj.posY);
                int z = MathHelper.floor_double(hydraObj.posZ);
                if (hydraObj.worldObj.checkChunksExist(x - 16, y - 16, z - 16, x + 16, y + 16, z + 16)) {
                    spawnHead();
                    missingHeadTicks = 0;
                }
            }
            if (headEntity == null) return;
        }
        missingHeadTicks = 0;
        this.headEntity.width = this.headEntity.height = this.isActive() ? 4.0F : 1.0F;
        if (!this.hydraObj.worldObj.isRemote) {
            setDifficultyVariables();
            advanceRespawnCounter();
            advanceHeadState();
            setHeadPosition();
            setHeadFacing();
            executeAttacks();
        } else {
            clientAnimateHeadDeath();
        }

        setNeckPosition();
        if (this.hydraObj.worldObj.isRemote) {
            addMouthParticles();
        }
        playSounds();
    }

    boolean spawnHead() {
        if (headEntity != null && !headEntity.isDead) return true;
        EntityTFHydraHead existing = findNearbyHead("head" + headNum);
        if (existing != null) {
            headEntity = existing;
            return true;
        }
        EntityTFHydraHead head = new EntityTFHydraHead(hydraObj, "head" + headNum, 3F, 3F);
        head.setPosition(hydraObj.posX, hydraObj.posY, hydraObj.posZ);
        head.setState(currentState);
        if (!hydraObj.worldObj.spawnEntityInWorld(head)) return false;
        headEntity = head;
        return true;
    }

    NBTTagCompound writeState() {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setInteger("Previous", prevState);
        tag.setInteger("Current", currentState);
        tag.setInteger("Next", nextState);
        tag.setInteger("Duration", ticksNeeded);
        tag.setInteger("Progress", ticksProgress);
        tag.setInteger("Damage", damageTaken);
        tag.setFloat("FractionalDamage", fractionalDamage);
        tag.setInteger("Respawn", respawnCounter);
        tag.setBoolean("Roared", didRoar);
        tag.setBoolean("Secondary", isSecondaryAttacking);
        tag.setDouble("TargetX", targetX);
        tag.setDouble("TargetY", targetY);
        tag.setDouble("TargetZ", targetZ);
        return tag;
    }

    void readState(NBTTagCompound tag) {
        currentState = MathHelper.clamp_int(tag.getInteger("Current"), 0, NUM_STATES - 1);
        prevState = MathHelper.clamp_int(tag.getInteger("Previous"), 0, NUM_STATES - 1);
        nextState = MathHelper.clamp_int(tag.getInteger("Next"), NEXT_AUTOMATIC, NUM_STATES - 1);
        ticksNeeded = Math.max(1, tag.getInteger("Duration"));
        ticksProgress = MathHelper.clamp_int(tag.getInteger("Progress"), 0, ticksNeeded);
        damageTaken = Math.max(0, tag.getInteger("Damage"));
        fractionalDamage = MathHelper.clamp_float(tag.getFloat("FractionalDamage"), 0F, 1F);
        respawnCounter = Math.max(-1, tag.getInteger("Respawn"));
        didRoar = tag.getBoolean("Roared");
        isSecondaryAttacking = tag.getBoolean("Secondary");
        targetX = tag.getDouble("TargetX");
        targetY = tag.getDouble("TargetY");
        targetZ = tag.getDouble("TargetZ");
    }

    /**
     * If we are dead, decrement the respawn counter and respawn when it is empty.
     */
    protected void advanceRespawnCounter() {
        if (this.currentState == STATE_DEAD && respawnCounter > -1) {
            if (--this.respawnCounter <= 0) {
                this.setNextState(STATE_BORN);
                this.damageTaken = 0;
                this.fractionalDamage = 0.0F;
                this.endCurrentAction();
                this.respawnCounter = -1;
            }
        }
    }

    /**
     * We need to animate head death on the client
     */
    protected void clientAnimateHeadDeath() {
        // this will start the animation
        if (headEntity.getState() == HydraHeadContainer.STATE_DYING) {
            // several things, like head visibility animate off this
            this.headEntity.deathTime++;

            // make explosion particles and stuff
            if (headEntity.deathTime > 0) {
                if (headEntity.deathTime < 20) {
                    doExplosionOn(headEntity, true);
                } else if (headEntity.deathTime < 30) {
                    doExplosionOn(necka, false);
                } else if (headEntity.deathTime < 40) {
                    doExplosionOn(neckb, false);
                } else if (headEntity.deathTime < 50) {
                    doExplosionOn(neckc, false);
                } else if (headEntity.deathTime < 60) {
                    doExplosionOn(neckd, false);
                } else if (headEntity.deathTime < 70) {
                    doExplosionOn(necke, false);
                }
            }

            // turn necks red
            necka.hurtTime = 20;
            neckb.hurtTime = 20;
            neckc.hurtTime = 20;
            neckd.hurtTime = 20;
            necke.hurtTime = 20;

        } else {
            this.headEntity.deathTime = 0;
            this.headEntity.setHealth(
                    (float) this.headEntity.getEntityAttribute(SharedMonsterAttributes.maxHealth).getBaseValue());
        }
    }

    private void doExplosionOn(EntityTFHydraPart part, boolean large) {
        for (int i = 0; i < 10; ++i) {
            double var8 = part.getRNG().nextGaussian() * 0.02D;
            double var4 = part.getRNG().nextGaussian() * 0.02D;
            double var6 = part.getRNG().nextGaussian() * 0.02D;
            String particle = large && part.getRNG().nextInt(5) == 0 ? "largeexplode" : "explode";
            part.worldObj.spawnParticle(
                    particle,
                    part.posX + part.getRNG().nextFloat() * part.width * 2.0F - part.width,
                    part.posY + part.getRNG().nextFloat() * part.height,
                    part.posZ + part.getRNG().nextFloat() * part.width * 2.0F - part.width,
                    var8,
                    var4,
                    var6);
        }

    }

    /**
     * Move the state counter along This is only called on the server, the client just receives the resulting data
     */
    protected void advanceHeadState() {
        // check head state
        if (++ticksProgress >= this.ticksNeeded) {
            int myNext;

            // advance state
            if (this.nextState == NEXT_AUTOMATIC) {
                myNext = nextStates[this.currentState];
                if (myNext != currentState) {
                    // System.out.println("Automatically advancing head " + this.headNum + " to state " + myNext);

                    // when returning from a secondary attack, no attack cooldown
                    if (this.isSecondaryAttacking && myNext == HydraHeadContainer.STATE_ATTACK_COOLDOWN) {
                        this.isSecondaryAttacking = false;
                        myNext = HydraHeadContainer.STATE_IDLE;
                    }
                }
            } else {
                myNext = this.nextState;
                this.nextState = NEXT_AUTOMATIC;
                // System.out.println("Manually advancing head " + this.headNum + " to state " + myNext);
            }

            this.ticksNeeded = this.ticksProgress = stateDurations[myNext];
            this.ticksProgress = 0;

            this.prevState = this.currentState;
            this.currentState = myNext;
        }
        // set datawatcher so client can properly animate
        if (headEntity.getState() != this.currentState) {
            headEntity.setState(this.currentState);
        }

    }

    /**
     * Set the direction the head is facing. It mostly faces the targetEntity, but that depends on the state
     */
    protected void setHeadFacing() {
        if (this.headEntity == null) {
            return;
        }
        if (this.currentState == STATE_BITE_READY) {
            if (this.targetEntity != null && this.targetEntity.isEntityAlive()) {
                this.faceEntity(this.targetEntity, 5.0F, this.hydraObj.getVerticalFaceSpeed());
            }
            // the side heads bite along opposite sides of the body
            float yawOffset = MathHelper
                    .wrapAngleTo180_float(this.headEntity.rotationYaw - this.hydraObj.renderYawOffset);
            if (this.headNum == 2) {
                yawOffset = MathHelper.clamp_float(yawOffset, 60.0F, 90.0F);
            } else {
                yawOffset = MathHelper.clamp_float(yawOffset, -90.0F, -60.0F);
            }
            this.headEntity.rotationYaw = this.hydraObj.renderYawOffset + yawOffset;
            Vec3 look = this.headEntity.getLookVec();
            this.targetX = this.headEntity.posX + look.xCoord * 16.0D;
            this.targetY = this.headEntity.posY + 1.5D + look.yCoord * 16.0D;
            this.targetZ = this.headEntity.posZ + look.zCoord * 16.0D;
        } else if (this.currentState == STATE_BITE_BITING || this.currentState == STATE_BITE_ENDING) {
            if (this.targetEntity != null && this.targetEntity.isEntityAlive()) {
                this.faceEntity(this.targetEntity, 5.0F, this.hydraObj.getVerticalFaceSpeed());
            } else {
                this.faceVec(this.targetX, this.targetY, this.targetZ, 5.0F, this.hydraObj.getVerticalFaceSpeed());
            }
            this.headEntity.rotationPitch = MathHelper
                    .clamp_float(this.headEntity.rotationPitch + (float) (Math.PI / 4.0D), -90.0F, 90.0F);
        } else if (this.currentState == STATE_ROAR_RAWR) {
            this.faceVec(this.targetX, this.targetY, this.targetZ, 10.0F, this.hydraObj.getVerticalFaceSpeed());
        } else if (this.currentState == STATE_FLAME_BEGINNING || this.currentState == STATE_FLAME_BREATHING) {
            moveTargetCoordsTowardsTargetEntity(this.flameBreathTrackingSpeed);
            this.faceVec(this.targetX, this.targetY, this.targetZ, 5.0F, this.hydraObj.getVerticalFaceSpeed());
        } else if (this.isActive()) {
            if (this.targetEntity != null && this.targetEntity.isEntityAlive()) {
                this.faceEntity(this.targetEntity, 5.0F, this.hydraObj.getVerticalFaceSpeed());
            } else {
                faceIdle(1.5F, this.hydraObj.getVerticalFaceSpeed());
            }
        }
    }

    /**
     * Slowly track our target coords towards the target entity
     */
    protected void moveTargetCoordsTowardsTargetEntity(double distance) {
        if (this.targetEntity == null || !this.targetEntity.isEntityAlive() || distance <= 0.0D) {
            return;
        }
        double dx = this.targetEntity.posX - this.targetX;
        double dy = this.targetEntity.posY - this.targetY;
        double dz = this.targetEntity.posZ - this.targetZ;
        double distanceSq = dx * dx + dy * dy + dz * dz;
        if (distanceSq <= distance * distance) {
            this.targetX = this.targetEntity.posX;
            this.targetY = this.targetEntity.posY;
            this.targetZ = this.targetEntity.posZ;
        } else {
            double factor = distance / Math.sqrt(distanceSq);
            this.targetX += dx * factor;
            this.targetY += dy * factor;
            this.targetZ += dz * factor;
        }
    }

    /**
     * During certain states, animate particles coming out of the mouth
     */
    protected void addMouthParticles() {
        Vec3 vector = headEntity.getLookVec();

        double dist = 3.5;
        double px = headEntity.posX + vector.xCoord * dist;
        double py = headEntity.posY + 1 + vector.yCoord * dist;
        double pz = headEntity.posZ + vector.zCoord * dist;

        // hydraObj.worldObj.spawnParticle("mobSpell", px, py, pz, 0.3, 0.9, 0.1);

        if (headEntity.getState() == STATE_FLAME_BEGINNING) {
            headEntity.worldObj.spawnParticle(
                    "flame",
                    px + headEntity.getRNG().nextDouble() - 0.5,
                    py + headEntity.getRNG().nextDouble() - 0.5,
                    pz + headEntity.getRNG().nextDouble() - 0.5,
                    0,
                    0,
                    0);
            headEntity.worldObj.spawnParticle(
                    "smoke",
                    px + headEntity.getRNG().nextDouble() - 0.5,
                    py + headEntity.getRNG().nextDouble() - 0.5,
                    pz + headEntity.getRNG().nextDouble() - 0.5,
                    0,
                    0,
                    0);
        }

        if (headEntity.getState() == STATE_FLAME_BREATHING) {
            Vec3 look = headEntity.getLookVec();
            for (int i = 0; i < 5; i++) {
                double dx = look.xCoord;
                double dy = look.yCoord;
                double dz = look.zCoord;

                double spread = 5 + headEntity.getRNG().nextDouble() * 2.5;
                double velocity = 1.0 + headEntity.getRNG().nextDouble();

                // spread flame
                dx += headEntity.getRNG().nextGaussian() * 0.007499999832361937D * spread;
                dy += headEntity.getRNG().nextGaussian() * 0.007499999832361937D * spread;
                dz += headEntity.getRNG().nextGaussian() * 0.007499999832361937D * spread;
                dx *= velocity;
                dy *= velocity;
                dz *= velocity;

                TwilightForestMod.proxy.spawnParticle(headEntity.worldObj, "largeflame", px, py, pz, dx, dy, dz);
            }
        }

        if (headEntity.getState() == STATE_BITE_BEGINNING || headEntity.getState() == STATE_BITE_READY) {
            headEntity.worldObj.spawnParticle(
                    "splash",
                    px + headEntity.getRNG().nextDouble() - 0.5,
                    py + headEntity.getRNG().nextDouble() - 0.5,
                    pz + headEntity.getRNG().nextDouble() - 0.5,
                    0,
                    0,
                    0);
        }

        if (headEntity.getState() == STATE_MORTAR_BEGINNING) {
            headEntity.worldObj.spawnParticle(
                    "largesmoke",
                    px + headEntity.getRNG().nextDouble() - 0.5,
                    py + headEntity.getRNG().nextDouble() - 0.5,
                    pz + headEntity.getRNG().nextDouble() - 0.5,
                    0,
                    0,
                    0);
        }
    }

    /**
     * Generate any sounds we need to go along with head animations
     */
    protected void playSounds() {
        if (this.hydraObj.worldObj.isRemote || this.headEntity == null) {
            return;
        }
        int state = this.currentState;
        if (state == STATE_FLAME_BREATHING && this.headEntity.ticksExisted % 5 == 0) {
            this.headEntity.worldObj.playSoundEffect(
                    this.headEntity.posX + 0.5D,
                    this.headEntity.posY + 0.5D,
                    this.headEntity.posZ + 0.5D,
                    "mob.ghast.fireball",
                    0.5F + this.headEntity.getRNG().nextFloat(),
                    this.headEntity.getRNG().nextFloat() * 0.7F + 0.3F);
        }
        // one roar per roar animation, not one roar every server tick
        if (state == STATE_ROAR_RAWR && !this.didRoar) {
            this.headEntity.worldObj.playSoundEffect(
                    this.headEntity.posX + 0.5D,
                    this.headEntity.posY + 0.5D,
                    this.headEntity.posZ + 0.5D,
                    TwilightForestMod.ID + ":mob.hydra.roar",
                    1.25F,
                    this.headEntity.getRNG().nextFloat() * 0.3F + 0.7F);
            this.didRoar = true;
        } else if (state != STATE_ROAR_RAWR) {
            this.didRoar = false;
        }
        if (state == STATE_BITE_READY && this.ticksProgress == 60) {
            this.headEntity.worldObj.playSoundEffect(
                    this.headEntity.posX + 0.5D,
                    this.headEntity.posY + 0.5D,
                    this.headEntity.posZ + 0.5D,
                    TwilightForestMod.ID + ":mob.hydra.warn",
                    2.0F,
                    this.headEntity.getRNG().nextFloat() * 0.3F + 0.7F);
        }
    }

    /**
     * Put the neck entities into the proper place, based on the body and head positions Done on both server and client.
     */
    protected void setNeckPosition() {
        // set neck positions
        Vec3 vector = null;
        float neckRotation = 0;

        if (headNum == 0) {
            vector = Vec3.createVectorHelper(0, 3, -1);
            neckRotation = 0;
        }
        if (headNum == 1) {
            vector = Vec3.createVectorHelper(-1, 3, 3);
            neckRotation = 90;
        }
        if (headNum == 2) {
            vector = Vec3.createVectorHelper(1, 3, 3);
            neckRotation = -90;
        }
        if (headNum == 3) {
            vector = Vec3.createVectorHelper(-1, 3, 3);
            neckRotation = 135;
        }
        if (headNum == 4) {
            vector = Vec3.createVectorHelper(1, 3, 3);
            neckRotation = -135;
        }

        if (headNum == 5) {
            vector = Vec3.createVectorHelper(-1, 3, 5);
            neckRotation = 135;
        }
        if (headNum == 6) {
            vector = Vec3.createVectorHelper(1, 3, 5);
            neckRotation = -135;
        }

        vector.rotateAroundY((-(hydraObj.renderYawOffset + neckRotation) * 3.141593F) / 180F);
        setNeckPositon(
                hydraObj.posX + vector.xCoord,
                hydraObj.posY + vector.yCoord,
                hydraObj.posZ + vector.zCoord,
                hydraObj.renderYawOffset,
                0);
    }

    /**
     * Position the head object appropriately This is only called on the server, the client just receives the resulting
     * data
     */
    protected void setHeadPosition() {
        // set head positions
        Vec3 vector;
        double dx, dy, dz;

        // head1 is above
        // head2 is to the right
        // head3 is to the left
        // state tables are immutable after construction, dont rebuild them
        // for every head on every tick
        float neckLength = getCurrentNeckLength();
        float xRotation = getCurrentHeadXRotation();
        float yRotation = getCurrentHeadYRotation();

        float periodX = (headNum == 0 || headNum == 3) ? 20F : ((headNum == 1 || headNum == 4) ? 5.0f : 7.0F);
        float periodY = (headNum == 0 || headNum == 4) ? 10F : ((headNum == 1 || headNum == 6) ? 6.0f : 5.0F);

        float xSwing = MathHelper.sin(hydraObj.ticksExisted / periodX) * 3.0F;
        float ySwing = MathHelper.sin(hydraObj.ticksExisted / periodY) * 5.0F;

        if (!this.isActive()) {
            xSwing = ySwing = 0;
        }

        vector = Vec3.createVectorHelper(0, 0, neckLength); // -53 = 3.3125
        vector.rotateAroundX((xRotation * 3.141593F + xSwing) / 180F);
        vector.rotateAroundY((-(hydraObj.renderYawOffset + yRotation + ySwing) * 3.141593F) / 180F);

        dx = hydraObj.posX + vector.xCoord;
        dy = hydraObj.posY + vector.yCoord + 3;
        dz = hydraObj.posZ + vector.zCoord;

        headEntity.setPosition(dx, dy, dz);
        headEntity.setMouthOpen(getCurrentMouthOpen());

    }

    /**
     * Execute whatever effect we need. Deal damage with the bite, the breath weapon, or launch mortars when appropriate
     */
    protected void executeAttacks() {
        if (this.currentState == HydraHeadContainer.STATE_MORTAR_LAUNCH && this.ticksProgress % 10 == 0) {
            Entity lookTarget = getHeadLookTarget();

            if (lookTarget != null
                    && (lookTarget instanceof EntityTFHydraPart || lookTarget instanceof EntityDragonPart)) {
                // stop hurting yourself!
                this.endCurrentAction();
            } else {
                EntityTFHydraMortar mortar = new EntityTFHydraMortar(headEntity.worldObj, this.headEntity);

                Vec3 vector = headEntity.getLookVec();

                double dist = 3.5;
                double px = headEntity.posX + vector.xCoord * dist;
                double py = headEntity.posY + 1 + vector.yCoord * dist;
                double pz = headEntity.posZ + vector.zCoord * dist;

                mortar.setLocationAndAngles(px, py, pz, 0, 0);

                // launch blasting mortars if the player is hiding
                if (this.targetEntity != null && !headEntity.canEntityBeSeen(this.targetEntity)) {
                    // System.out.println("Launching blasting mortar at hiding target.");
                    mortar.setToBlasting();
                }

                headEntity.worldObj.playAuxSFXAtEntity(
                        null,
                        1008,
                        (int) headEntity.posX,
                        (int) headEntity.posY,
                        (int) headEntity.posZ,
                        0);

                headEntity.worldObj.spawnEntityInWorld(mortar);
            }
        }
        if (headEntity.getState() == STATE_BITE_BITING) {
            // damage nearby things
            List<Entity> nearbyList = headEntity.worldObj
                    .getEntitiesWithinAABBExcludingEntity(headEntity, headEntity.boundingBox.expand(0.0, 1.0, 0.0));

            for (Entity nearby : nearbyList) {
                if (nearby instanceof EntityLivingBase && !(nearby instanceof EntityTFHydraPart)
                        && !(nearby instanceof EntityTFHydra)
                        && !(nearby instanceof EntityDragonPart)) {
                    // bite it!
                    nearby.attackEntityFrom(DamageSource.causeMobDamage(hydraObj), BITE_DAMAGE);
                }
            }
        }

        if (headEntity.getState() == STATE_FLAME_BREATHING) {
            Entity target = getHeadLookTarget();

            if (target != null) {
                if (target instanceof EntityTFHydraPart || target instanceof EntityDragonPart) {
                    // stop hurting yourself!
                    this.endCurrentAction();

                    // System.out.println("Stopping breath from head " + headNum + " because I am about to hit" +
                    // target);
                } else if (!target.isImmuneToFire() && target.attackEntityFrom(DamageSource.inFire, FLAME_DAMAGE)) {
                    target.setFire(FLAME_BURN_FACTOR);
                }
            }
        }

    }

    protected void setDifficultyVariables() {
        // Per-head value prevents one world or difficulty from modifying
        // another Hydra's tracking speed through a shared static field.
        this.flameBreathTrackingSpeed = this.hydraObj.worldObj.difficultySetting == EnumDifficulty.HARD
                ? HARD_FLAME_TRACKING_SPEED
                : NORMAL_FLAME_TRACKING_SPEED;
    }

    /**
     * What, if anything, is the head currently looking at?
     */
    private Entity getHeadLookTarget() {
        Entity pointedEntity = null;
        double range = 30.0D;
        Vec3 srcVec = Vec3.createVectorHelper(headEntity.posX, headEntity.posY + 1.0, headEntity.posZ);
        Vec3 lookVec = headEntity.getLook(1.0F);
        Vec3 destVec = srcVec.addVector(lookVec.xCoord * range, lookVec.yCoord * range, lookVec.zCoord * range);
        MovingObjectPosition blockHit = headEntity.worldObj.rayTraceBlocks(srcVec, destVec);
        if (blockHit != null && blockHit.hitVec != null) {
            destVec = blockHit.hitVec;
        }
        double reach = srcVec.distanceTo(destVec);
        float var9 = 3.0F;
        List<Entity> possibleList = headEntity.worldObj.getEntitiesWithinAABBExcludingEntity(
                headEntity,
                headEntity.boundingBox.addCoord(lookVec.xCoord * reach, lookVec.yCoord * reach, lookVec.zCoord * reach)
                        .expand(var9, var9, var9));
        double hitDist = 0;

        for (Entity possibleEntity : possibleList) {
            if (!possibleEntity.isDead && possibleEntity.canBeCollidedWith()
                    && possibleEntity != hydraObj
                    && possibleEntity != headEntity
                    && possibleEntity != necka
                    && possibleEntity != neckb
                    && possibleEntity != neckc
                    && possibleEntity != neckd
                    && possibleEntity != necke) {
                float borderSize = possibleEntity.getCollisionBorderSize();
                AxisAlignedBB collisionBB = possibleEntity.boundingBox.expand(borderSize, borderSize, borderSize);
                MovingObjectPosition interceptPos = collisionBB.calculateIntercept(srcVec, destVec);

                if (collisionBB.isVecInside(srcVec)) {
                    if (0.0D < hitDist || hitDist == 0.0D) {
                        pointedEntity = possibleEntity;
                        hitDist = 0.0D;
                    }
                } else if (interceptPos != null) {
                    double possibleDist = srcVec.distanceTo(interceptPos.hitVec);

                    if (possibleDist < hitDist || hitDist == 0.0D) {
                        pointedEntity = possibleEntity;
                        hitDist = possibleDist;
                    }
                }
            }
        }
        return pointedEntity;
    }

    /**
     * What state should we go to next?
     */
    public void setNextState(int next) {
        this.nextState = next;
    }

    /**
     *
     */
    public void endCurrentAction() {
        this.ticksProgress = this.ticksNeeded;
    }

    /**
     * Search for nearby heads with the string as their name
     */
    private EntityTFHydraHead findNearbyHead(String string) {

        List<EntityTFHydraHead> nearbyHeads = hydraObj.worldObj.getEntitiesWithinAABB(
                EntityTFHydraHead.class,
                AxisAlignedBB.getBoundingBox(
                        hydraObj.posX,
                        hydraObj.posY,
                        hydraObj.posZ,
                        hydraObj.posX + 1,
                        hydraObj.posY + 1,
                        hydraObj.posZ + 1).expand(16.0D, 16.0D, 16.0D));

        EntityTFHydraHead legacy = null;
        for (EntityTFHydraHead nearbyHead : nearbyHeads) {
            if (nearbyHead.isDead || !string.equals(nearbyHead.getPartName())) continue;
            if (nearbyHead.belongsTo(hydraObj)) {
                nearbyHead.bindToHydra(hydraObj);
                return nearbyHead;
            }
            if (nearbyHead.hasNoOwner() && (legacy == null
                    || nearbyHead.getDistanceSqToEntity(hydraObj) < legacy.getDistanceSqToEntity(hydraObj))) {
                legacy = nearbyHead;
            }
        }
        if (legacy != null) legacy.bindToHydra(hydraObj);
        return legacy;
    }

    /**
     * The current neck length depends on the current state, and how far along are we from the previous state Other
     * factors include which head it is.
     */
    protected float getCurrentNeckLength() {
        float prevLength = stateNeckLength[this.headNum][this.prevState];
        float curLength = stateNeckLength[this.headNum][this.currentState];
        float progress = (float) ticksProgress / (float) ticksNeeded;

        return prevLength + (curLength - prevLength) * progress;
    }

    /**
     * The current x rotation depends on the current state, and how far along are we from the previous state Other
     * factors include which head it is.
     */
    protected float getCurrentHeadXRotation() {
        float prevRotation = stateXRotations[this.headNum][this.prevState];
        float currentRotation = stateXRotations[this.headNum][this.currentState];
        float progress = (float) ticksProgress / (float) ticksNeeded;

        return prevRotation + (currentRotation - prevRotation) * progress;
    }

    protected float getCurrentHeadYRotation() {
        float prevRotation = stateYRotations[this.headNum][this.prevState];
        float currentRotation = stateYRotations[this.headNum][this.currentState];
        float progress = (float) ticksProgress / (float) ticksNeeded;

        return prevRotation + (currentRotation - prevRotation) * progress;
    }

    protected float getCurrentMouthOpen() {
        float prevOpen = stateMouthOpen[this.headNum][this.prevState];
        float curOpen = stateMouthOpen[this.headNum][this.currentState];
        float progress = (float) ticksProgress / (float) ticksNeeded;

        return prevOpen + (curOpen - prevOpen) * progress;
    }

    /**
     * Sets the four neck positions ranging from the start position to the head position.
     *
     * @param hi         head index
     * @param startX
     * @param startY
     * @param startZ
     * @param startYaw
     * @param startPitch
     */
    protected void setNeckPositon(double startX, double startY, double startZ, float startYaw, float startPitch) {
        if (this.headEntity == null) {
            return;
        }
        double endX = this.headEntity.posX;
        double endY = this.headEntity.posY;
        double endZ = this.headEntity.posZ;
        float endYaw = this.headEntity.rotationYaw;
        float endPitch = this.headEntity.rotationPitch;

        endYaw = startYaw - MathHelper.wrapAngleTo180_float(startYaw - endYaw);
        endPitch = startPitch - MathHelper.wrapAngleTo180_float(startPitch - endPitch);

        if (endPitch > 0.0F) {
            Vec3 backward = Vec3.createVectorHelper(0.0D, 0.0D, -1.0D);
            backward.rotateAroundY((float) Math.toRadians(-endYaw));
            endX += backward.xCoord;
            endY += backward.yCoord;
            endZ += backward.zCoord;
        } else {
            Vec3 look = this.headEntity.getLookVec();
            endX -= look.xCoord;
            endY -= look.yCoord;
            endZ -= look.zCoord;
        }

        for (int i = 0; i < NECK_SEGMENT_COUNT; ++i) {
            float fraction = i / (float) (NECK_SEGMENT_COUNT - 1);
            EntityTFHydraNeck neck = this.neckParts[i];
            neck.setPosition(
                    endX + (startX - endX) * fraction,
                    endY + (startY - endY) * fraction,
                    endZ + (startZ - endZ) * fraction);
            neck.rotationYaw = endYaw + (startYaw - endYaw) * fraction;
            neck.rotationPitch = endPitch + (startPitch - endPitch) * fraction;
        }
    }

    /**
     * Face a vector in front of the hydra entity. This is used to give the heads something to look at when we don't
     * have an acutal target.
     */
    protected void faceIdle(float yawConstraint, float pitchConstraint) {
        // headEntity.rotationPitch = hydraObj.rotationPitch;
        // headEntity.rotationYaw = hydraObj.rotationYaw;

        float angle = (((hydraObj.rotationYaw) * 3.141593F) / 180F);
        float distance = 30.0F;

        double dx = hydraObj.posX - MathHelper.sin(angle) * distance;
        double dy = hydraObj.posY + 3.0;
        double dz = hydraObj.posZ + MathHelper.cos(angle) * distance;

        faceVec(dx, dy, dz, yawConstraint, pitchConstraint);
    }

    /**
     * Face this head towards an entity
     */
    public void faceEntity(Entity entity, float yawConstraint, float pitchConstraint) {
        if (entity == null || this.headEntity == null || entity.isDead) {
            return;
        }
        double eyeY = entity instanceof EntityLivingBase living ? living.posY + living.getEyeHeight()
                : (entity.boundingBox.minY + entity.boundingBox.maxY) * 0.5D;
        faceVec(entity.posX, eyeY, entity.posZ, yawConstraint, pitchConstraint);
        this.targetX = entity.posX;
        this.targetY = entity.posY;
        this.targetZ = entity.posZ;
    }

    /**
     * Face this head towards a specific Vector
     */
    public void faceVec(double xCoord, double yCoord, double zCoord, float yawConstraint, float pitchConstraint) {
        if (this.headEntity == null) {
            return;
        }
        double dx = xCoord - this.headEntity.posX;
        double dz = zCoord - this.headEntity.posZ;
        double dy = yCoord - (this.headEntity.posY + 1.0D);
        double horizontalDistance = MathHelper.sqrt_double(dx * dx + dz * dz);
        float desiredYaw = (float) Math.toDegrees(Math.atan2(dz, dx)) - 90.0F;
        float desiredPitch = (float) -Math.toDegrees(Math.atan2(dy, horizontalDistance));
        this.headEntity.rotationPitch = updateRotation(this.headEntity.rotationPitch, desiredPitch, pitchConstraint);
        this.headEntity.rotationYaw = updateRotation(this.headEntity.rotationYaw, desiredYaw, yawConstraint);
    }

    /**
     * Arguments: current rotation, intended rotation, max increment.
     */
    private float updateRotation(float current, float intended, float increment) {
        float delta = MathHelper.wrapAngleTo180_float(intended - current);

        if (delta > increment) {
            delta = increment;
        }

        if (delta < -increment) {
            delta = -increment;
        }

        return MathHelper.wrapAngleTo180_float(current + delta);
    }

    public Entity getTargetEntity() {
        return targetEntity;
    }

    public void setTargetEntity(Entity targetEntity) {
        this.targetEntity = targetEntity;
    }

    public void setHurtTime(int hurtTime) {
        if (headEntity != null) {
            headEntity.hurtTime = hurtTime;
        }
        necka.hurtTime = hurtTime;
        neckb.hurtTime = hurtTime;
        neckc.hurtTime = hurtTime;
        neckd.hurtTime = hurtTime;
        necke.hurtTime = hurtTime;
    }

    /**
     * At certain times, some of the heads are "dead" and hidden
     */
    public boolean shouldRenderHead() {
        return this.headEntity != null && this.headEntity.getState() != STATE_DEAD && this.headEntity.deathTime < 20;
    }

    /**
     * Same with the neck parts
     */
    public boolean shouldRenderNeck(int neckNum) {
        if (this.headEntity == null || neckNum < 0 || neckNum >= NECK_SEGMENT_COUNT) {
            return false;
        }
        int time = 30 + 10 * neckNum;
        return this.headEntity.getState() != STATE_DEAD && this.headEntity.deathTime < time;
    }

    /**
     * Is this head active, that is, not dying or dead?
     */
    public boolean isActive() {
        int state = this.hydraObj.worldObj.isRemote && this.headEntity != null ? this.headEntity.getState()
                : this.currentState;
        return state != STATE_DYING && state != STATE_DEAD;
    }

    /**
     * Add to our damage taken counter
     */
    public void addDamage(float damageAmount) {
        if (damageAmount <= 0.0F || !Float.isFinite(damageAmount)) {
            return;
        }
        // preserve the public integer counter while accumulating fractional hits
        this.fractionalDamage += damageAmount;
        int wholeDamage = (int) this.fractionalDamage;
        this.damageTaken += wholeDamage;
        this.fractionalDamage -= wholeDamage;
    }

    /**
     * How much damage has this head taken?
     */
    public int getDamageTaken() {
        return this.damageTaken;
    }

    public void setRespawnCounter(int count) {
        this.respawnCounter = count;
    }

}
