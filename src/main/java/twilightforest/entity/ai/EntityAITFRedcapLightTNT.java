package twilightforest.entity.ai;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.event.world.BlockEvent;

import twilightforest.entity.EntityTFRedcap;

public class EntityAITFRedcapLightTNT extends EntityAITFRedcapBase {

    private static final int SCAN_INTERVAL = 10;
    private static final int MAX_CHASE_TICKS = 100;
    private static final double IGNITION_RANGE_SQ = 2.4D * 2.4D;

    private final float pursueSpeed;
    private int scanCooldown;
    private int rePathCooldown;
    private int chaseTicks;
    private int tntX;
    private int tntY;
    private int tntZ;
    private boolean pathFailed;

    public EntityAITFRedcapLightTNT(EntityTFRedcap hostEntity, float speed) {
        this.entityObj = hostEntity;
        this.pursueSpeed = speed;
        this.setMutexBits(3);
    }

    @Override
    public boolean shouldExecute() {
        if (this.entityObj.worldObj.isRemote
                || !this.entityObj.worldObj.getGameRules().getGameRuleBooleanValue("mobGriefing")) {
            return false;
        }
        if (this.scanCooldown > 0) {
            --this.scanCooldown;
            return false;
        }
        this.scanCooldown = SCAN_INTERVAL;

        // avoid attracting redcaps to a fuse that has already been lit
        if (this.isLitTNTNearby(4)) {
            return false;
        }
        ChunkCoordinates nearbyTNT = this.findBlockTNTNearby(8);
        if (nearbyTNT == null) {
            return false;
        }
        this.tntX = nearbyTNT.posX;
        this.tntY = nearbyTNT.posY;
        this.tntZ = nearbyTNT.posZ;
        return true;
    }

    @Override
    public boolean continueExecuting() {
        return !this.entityObj.isDead && !this.pathFailed
                && this.chaseTicks < MAX_CHASE_TICKS
                && this.entityObj.worldObj.getGameRules().getGameRuleBooleanValue("mobGriefing")
                && this.entityObj.worldObj.blockExists(this.tntX, this.tntY, this.tntZ)
                && this.entityObj.worldObj.getBlock(this.tntX, this.tntY, this.tntZ) == Blocks.tnt;
    }

    @Override
    public void startExecuting() {
        this.chaseTicks = 0;
        this.rePathCooldown = 0;
        this.pathFailed = false;
        this.entityObj.setCurrentItemOrArmor(0, EntityTFRedcap.heldFlint.copy());
    }

    @Override
    public void resetTask() {
        this.entityObj.getNavigator().clearPathEntity();
        this.entityObj.setCurrentItemOrArmor(0, this.entityObj.getPick().copy());
        this.scanCooldown = 20;
        this.pathFailed = false;
    }

    @Override
    public void updateTask() {
        ++this.chaseTicks;
        this.entityObj.getLookHelper().setLookPosition(
                this.tntX + 0.5D,
                this.tntY + 0.5D,
                this.tntZ + 0.5D,
                30.0F,
                this.entityObj.getVerticalFaceSpeed());

        if (this.entityObj.getDistanceSq(this.tntX + 0.5D, this.tntY, this.tntZ + 0.5D) < IGNITION_RANGE_SQ) {
            this.igniteTNT();
            this.pathFailed = true;
            return;
        }

        // repath rather than performing a navigation request every tick
        if (--this.rePathCooldown <= 0 || this.entityObj.getNavigator().noPath()) {
            this.rePathCooldown = 10;
            this.pathFailed = !this.entityObj.getNavigator()
                    .tryMoveToXYZ(this.tntX + 0.5D, this.tntY, this.tntZ + 0.5D, this.pursueSpeed);
        }
    }

    private void igniteTNT() {
        if (!(this.entityObj.worldObj instanceof WorldServer world)
                || !world.getGameRules().getGameRuleBooleanValue("mobGriefing")
                || !world.blockExists(this.tntX, this.tntY, this.tntZ)
                || world.getBlock(this.tntX, this.tntY, this.tntZ) != Blocks.tnt) {
            return;
        }

        EntityPlayer actor = FakePlayerFactory.getMinecraft(world);
        if (!world.canMineBlock(actor, this.tntX, this.tntY, this.tntZ) || MinecraftForge.EVENT_BUS.post(
                new BlockEvent.BreakEvent(
                        this.tntX,
                        this.tntY,
                        this.tntZ,
                        world,
                        Blocks.tnt,
                        world.getBlockMetadata(this.tntX, this.tntY, this.tntZ),
                        actor))) {
            return;
        }

        // only spawn the primed TNT after successful removal, never duplicate protected blocks
        if (world.setBlock(this.tntX, this.tntY, this.tntZ, Blocks.air, 0, 3)) {
            Blocks.tnt.onBlockDestroyedByPlayer(world, this.tntX, this.tntY, this.tntZ, 1);
            this.entityObj.playLivingSound();
            this.entityObj.getNavigator().clearPathEntity();
        }
    }
}
