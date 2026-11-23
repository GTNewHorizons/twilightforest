package twilightforest.entity;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.BlockSnapshot;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.event.ForgeEventFactory;

public class EntityTFNatureBolt extends EntityThrowable {

    public EntityTFNatureBolt(World par1World, double par2, double par4, double par6) {
        super(par1World, par2, par4, par6);
    }

    public EntityTFNatureBolt(World par1World, EntityLivingBase par2EntityLiving) {
        super(par1World, par2EntityLiving);
    }

    public EntityTFNatureBolt(World par1World) {
        super(par1World);
    }

    @Override
    public void onUpdate() {
        super.onUpdate();

        if (worldObj.isRemote) {
            makeTrail();
        }
    }

    /**
     * How much this entity falls each tick
     */
    @Override
    protected float getGravityVelocity() {
        return 0.003F;
    }

    /**
     * Make sparkly trail
     */
    public void makeTrail() {
        for (int i = 0; i < 5; i++) {
            double dx = posX + 0.5 * (rand.nextDouble() - rand.nextDouble());
            double dy = posY + 0.5 * (rand.nextDouble() - rand.nextDouble());
            double dz = posZ + 0.5 * (rand.nextDouble() - rand.nextDouble());
            worldObj.spawnParticle("happyVillager", dx, dy, dz, 0.0D, 0.0D, 0.0D);
        }
    }

    /**
     * What happens when we hit something?
     */
    @Override
    protected void onImpact(MovingObjectPosition hit) {
        if (worldObj.isRemote) {
            for (int i = 0; i < 8; i++) {
                worldObj.spawnParticle(
                        "blockcrack_" + Block.getIdFromBlock(Blocks.leaves) + "_0",
                        posX,
                        posY,
                        posZ,
                        rand.nextGaussian() * 0.05D,
                        rand.nextDouble() * 0.2D,
                        rand.nextGaussian() * 0.05D);
            }
            return;
        }

        if (isDead || hit == null) {
            return;
        }
        setDead();

        if (hit.entityHit instanceof EntityLivingBase living) {
            if (living.attackEntityFrom(DamageSource.causeIndirectMagicDamage(this, getThrower()), 2.0F)) {
                int duration = switch (worldObj.difficultySetting) {
                    case PEACEFUL -> 0;
                    case EASY -> 1;
                    case NORMAL -> 3;
                    case HARD -> 7;
                };
                if (duration > 0) {
                    living.addPotionEffect(new PotionEffect(Potion.poison.id, duration * 20, 0));
                }
            }
            return;
        }

        // never impersonate the attacked player to perform an environmental change
        // a forge fake player lets protection systems apply their permissions
        if (hit.typeOfHit != MovingObjectPosition.MovingObjectType.BLOCK
                || !worldObj.getGameRules().getGameRuleBooleanValue("mobGriefing")
                || !(worldObj instanceof WorldServer server)) {
            return;
        }

        int x = hit.blockX;
        int y = hit.blockY;
        int z = hit.blockZ;
        Block block = worldObj.getBlock(x, y, z);
        EntityPlayer actor = FakePlayerFactory.getMinecraft(server);
        if (block.getMaterial() == Material.grass) {
            ItemStack bonemeal = new ItemStack(Items.dye, 1, 15);
            if (actor.canPlayerEdit(x, y, z, hit.sideHit, bonemeal) && worldObj.canMineBlock(actor, x, y, z)) {
                Items.dye.onItemUse(bonemeal, actor, worldObj, x, y, z, hit.sideHit, 0, 0, 0);
            }
        } else if (block.getMaterial().isSolid() && canReplaceBlock(worldObj, x, y, z)) {
            ItemStack leaves = new ItemStack(Blocks.leaves, 1, 2);
            if (!actor.canPlayerEdit(x, y, z, hit.sideHit, leaves) || !worldObj.canMineBlock(actor, x, y, z)) {
                return;
            }
            BlockSnapshot snapshot = BlockSnapshot.getBlockSnapshot(worldObj, x, y, z);
            if (worldObj.setBlock(x, y, z, Blocks.leaves, 2, 3) && ForgeEventFactory
                    .onPlayerBlockPlace(actor, snapshot, ForgeDirection.getOrientation(hit.sideHit)).isCanceled()) {
                snapshot.restore(true, false);
            }
        }
    }

    /**
     * This is surprisingly difficult to determine
     */
    private boolean canReplaceBlock(World worldObj, int dx, int dy, int dz) {
        Block block = worldObj.getBlock(dx, dy, dz);
        float hardness = block.getBlockHardness(worldObj, dx, dy, dz);

        return hardness >= 0 && hardness < 50F;
    }

    public void setTarget(EntityLivingBase attackTarget) {}

}
