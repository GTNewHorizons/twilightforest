package twilightforest.entity.boss;

import java.util.List;
import java.util.UUID;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;

import twilightforest.TFAchievementPage;
import twilightforest.TwilightForestMod;

public class EntityTFLichMinion extends EntityZombie {

    EntityTFLich master;
    private UUID masterId;
    private int masterSearchTicks;

    public EntityTFLichMinion(World par1World) {
        super(par1World);
        this.master = null;
    }

    public EntityTFLichMinion(World world, double x, double y, double z) {
        this(world);
        this.setPosition(x, y, z);
    }

    public EntityTFLichMinion(World par1World, EntityTFLich entityTFLich) {
        super(par1World);
        this.master = entityTFLich;
        this.masterId = entityTFLich.getUniqueID();
    }

    /**
     * Called when we get attacked.
     *
     * If we are hit by a lich, still take the damage, but attack stronger and move faster.
     */
    @Override
    public boolean attackEntityFrom(DamageSource par1DamageSource, float par2) {
        EntityLivingBase prevTarget = getAttackTarget();

        if (super.attackEntityFrom(par1DamageSource, par2)) {
            if (par1DamageSource.getEntity() instanceof EntityTFLich) {
                // return to previous target
                setAttackTarget(prevTarget);
                if (prevTarget != null && prevTarget.isEntityAlive()) {
                    setRevengeTarget(prevTarget);
                }
                // but speed up
                addPotionEffect(new PotionEffect(Potion.moveSpeed.id, 200, 4));
                addPotionEffect(new PotionEffect(Potion.damageBoost.id, 200, 1));
            }
            return true;
        } else {
            return false;
        }
    }

    /**
     * Check and see if our master is dead. If so, die
     */
    @Override
    public void onLivingUpdate() {
        if (!this.worldObj.isRemote) {
            if (master == null) {
                if (masterSearchTicks++ % 10 == 0) findNewMaster();
                if (master == null && masterSearchTicks >= 100) setHealth(0);
            } else if (!master.isEntityAlive()) {
                setHealth(0);
            }
        }
        super.onLivingUpdate();
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound tag) {
        super.writeEntityToNBT(tag);
        writeMasterId(tag);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound tag) {
        super.readEntityFromNBT(tag);
        readMasterId(tag);
    }

    boolean isOwnedBy(EntityTFLich lich) {
        return master == lich || masterId != null && masterId.equals(lich.getUniqueID());
    }

    private void writeMasterId(NBTTagCompound tag) {
        if (masterId != null) {
            tag.setLong("LichMasterMost", masterId.getMostSignificantBits());
            tag.setLong("LichMasterLeast", masterId.getLeastSignificantBits());
        }
    }

    private void readMasterId(NBTTagCompound tag) {
        if (tag.hasKey("LichMasterMost", 99) && tag.hasKey("LichMasterLeast", 99)) {
            masterId = new UUID(tag.getLong("LichMasterMost"), tag.getLong("LichMasterLeast"));
        }
    }

    private void findNewMaster() {
        List<EntityTFLich> nearbyLiches = worldObj.getEntitiesWithinAABB(
                EntityTFLich.class,
                AxisAlignedBB.getBoundingBox(posX, posY, posZ, posX + 1, posY + 1, posZ + 1)
                        .expand(32.0D, 16.0D, 32.0D));

        for (EntityTFLich nearbyLich : nearbyLiches) {
            if (nearbyLich.isEntityAlive() && !nearbyLich.isShadowClone()
                    && (masterId != null ? masterId.equals(nearbyLich.getUniqueID())
                            : nearbyLich.wantsNewMinion(this))) {
                this.master = nearbyLich;
                this.masterId = nearbyLich.getUniqueID();

                // animate our new linkage!
                master.makeBlackMagicTrail(
                        posX,
                        posY + this.getEyeHeight(),
                        posZ,
                        master.posX,
                        master.posY + master.getEyeHeight(),
                        master.posZ);

                // become angry at our masters target
                setAttackTarget(master.getAttackTarget());

                // quit looking
                break;
            }
        }
    }

    /**
     * Trigger achievement when killed
     */
    @Override
    public void onDeath(DamageSource par1DamageSource) {
        super.onDeath(par1DamageSource);
        if (par1DamageSource.getEntity() instanceof EntityPlayer
                && ((EntityPlayer) par1DamageSource.getEntity()).dimension == TwilightForestMod.dimensionID) {
            ((EntityPlayer) par1DamageSource.getEntity()).triggerAchievement(TFAchievementPage.twilightHunter);
        }
    }

    // /**
    // * Initialize this creature.
    // */
    // @Override
    // public void initCreature()
    // {
    // this.addRandomArmor();
    // this.func_82162_bC();
    // }

    @Override
    protected void addRandomArmor() {
        float[] equipChances = new float[] { 0.0F, 0.25F, 0.75F, 1F };

        if (this.rand.nextFloat() < equipChances[2]) // this.worldObj.difficultySetting])
        {
            int var1 = this.rand.nextInt(2);
            float var2 = this.worldObj.difficultySetting == EnumDifficulty.HARD ? 0.1F : 0.25F;

            if (this.rand.nextFloat() < 0.07F) {
                ++var1;
            }

            if (this.rand.nextFloat() < 0.07F) {
                ++var1;
            }

            if (this.rand.nextFloat() < 0.07F) {
                ++var1;
            }

            for (int var3 = 3; var3 >= 0; --var3) {
                ItemStack var4 = this.func_130225_q(var3);

                if (var3 < 3 && this.rand.nextFloat() < var2) {
                    break;
                }

                if (var4 == null) {
                    Item var5 = getArmorItemForSlot(var3 + 1, var1);

                    if (var5 != null) {
                        this.setCurrentItemOrArmor(var3 + 1, new ItemStack(var5));
                    }
                }
            }
        }
    }

}
