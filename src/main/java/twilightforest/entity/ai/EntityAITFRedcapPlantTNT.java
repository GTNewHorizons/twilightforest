package twilightforest.entity.ai;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.BlockSnapshot;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.event.ForgeEventFactory;

import twilightforest.entity.EntityTFRedcap;

public class EntityAITFRedcapPlantTNT extends EntityAITFRedcapBase {

    private int plantCooldown;

    public EntityAITFRedcapPlantTNT(EntityTFRedcap redcap) {
        this.entityObj = redcap;
        this.setMutexBits(3);
    }

    @Override
    public boolean shouldExecute() {
        if (this.plantCooldown > 0) {
            --this.plantCooldown;
            return false;
        }
        if (this.entityObj.worldObj.isRemote
                || !this.entityObj.worldObj.getGameRules().getGameRuleBooleanValue("mobGriefing")
                || this.entityObj.getTntLeft() <= 0) {
            return false;
        }

        EntityLivingBase target = this.entityObj.getAttackTarget();
        if (target == null || !target.isEntityAlive()
                || this.entityObj.getDistanceSqToEntity(target) >= 25.0D
                || this.isTargetLookingAtMe(target)
                || this.isLitTNTNearby(8)) {
            return false;
        }
        return this.findBlockTNTNearby(5) == null;
    }

    @Override
    public void startExecuting() {
        this.plantCooldown = 40;
        this.entityObj.setCurrentItemOrArmor(0, EntityTFRedcap.heldTNT.copy());
        this.tryPlantTNT();
    }

    @Override
    public boolean continueExecuting() {
        return false;
    }

    @Override
    public void resetTask() {
        this.entityObj.setCurrentItemOrArmor(0, this.entityObj.getPick().copy());
    }

    private void tryPlantTNT() {
        if (!(this.entityObj.worldObj instanceof WorldServer world)
                || !world.getGameRules().getGameRuleBooleanValue("mobGriefing")
                || this.entityObj.getTntLeft() <= 0) {
            return;
        }

        int x = MathHelper.floor_double(this.entityObj.posX);
        int y = MathHelper.floor_double(this.entityObj.posY);
        int z = MathHelper.floor_double(this.entityObj.posZ);
        if (!world.blockExists(x, y, z) || !world.isAirBlock(x, y, z)
                || world.isBlockIndirectlyGettingPowered(x, y, z)) {
            return;
        }

        EntityPlayer actor = FakePlayerFactory.getMinecraft(world);
        ItemStack tnt = EntityTFRedcap.heldTNT.copy();
        if (!actor.canPlayerEdit(x, y, z, 1, tnt) || !world.canMineBlock(actor, x, y, z)) {
            return;
        }

        BlockSnapshot snapshot = BlockSnapshot.getBlockSnapshot(world, x, y, z);
        if (!world.setBlock(x, y, z, Blocks.tnt, 0, 3)) {
            return;
        }
        if (ForgeEventFactory.onPlayerBlockPlace(actor, snapshot, ForgeDirection.UP).isCanceled()) {
            snapshot.restore(true, false);
            return;
        }

        // inventory is consumed only after world placement and protection checks succeed
        this.entityObj.setTntLeft(this.entityObj.getTntLeft() - 1);
        this.entityObj.playLivingSound();
    }
}
