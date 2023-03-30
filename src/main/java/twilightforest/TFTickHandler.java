package twilightforest;

import java.util.Random;
import java.util.UUID;

import net.minecraft.block.material.Material;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraftforge.event.entity.item.ItemTossEvent;

import twilightforest.biomes.TFBiomeBase;
import twilightforest.block.BlockTFPortal;
import twilightforest.block.TFBlocks;
import twilightforest.world.ChunkProviderTwilightForest;
import twilightforest.world.WorldProviderTwilightForest;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.PlayerTickEvent;
import cpw.mods.fml.common.network.internal.FMLProxyPacket;

/**
 * This class listens for ticks in the world. If the player is near a diamond in the water, this class attempts to open
 * a portal.
 * 
 * @author Ben
 *
 */
public class TFTickHandler {

    private final Item portalItem;

    public TFTickHandler(Item portalItemIn) {
        this.portalItem = portalItemIn;
    }

    /**
     * On the tick, we check for eligible portals
     */
    @SubscribeEvent
    public void playerTick(PlayerTickEvent event) {

        EntityPlayer player = event.player;
        World world = player.worldObj;

        // check the player for being in a forbidden progression area, only every 20 ticks
        if (!world.isRemote && event.phase == TickEvent.Phase.END
                && world.getWorldTime() % 20 == 0
                && world.getGameRules().getGameRuleBooleanValue(TwilightForestMod.ENFORCED_PROGRESSION_RULE)) {
            if (world.provider instanceof WorldProviderTwilightForest && !player.capabilities.isCreativeMode) {
                checkBiomeForProgression(player, world);
            }
        }

        // check and send nearby forbidden structures, every 100 ticks or so
        if (!world.isRemote && event.phase == TickEvent.Phase.END
                && world.getWorldTime() % 100 == 0
                && world.getGameRules().getGameRuleBooleanValue(TwilightForestMod.ENFORCED_PROGRESSION_RULE)) {
            if (world.provider instanceof WorldProviderTwilightForest) {
                if (!player.capabilities.isCreativeMode) {
                    checkForLockedStructuresSendPacket(player, world);
                } else {
                    sendAllClearPacket(world, player);
                }
            }
        }
    }

    private void sendStructureProtectionPacket(World world, EntityPlayer player, StructureBoundingBox sbb) {
        // send packet
        FMLProxyPacket message = TFGenericPacketHandler.makeStructureProtectionPacket(sbb);
        if (player instanceof EntityPlayerMP) {
            TwilightForestMod.genericChannel.sendTo(message, (EntityPlayerMP) player);
            // System.out.println("Sent structure protection");
        }
    }

    private void sendAllClearPacket(World world, EntityPlayer player) {
        FMLProxyPacket message = TFGenericPacketHandler.makeStructureProtectionClearPacket();
        if (player instanceof EntityPlayerMP) {
            TwilightForestMod.genericChannel.sendTo(message, (EntityPlayerMP) player);
        }
    }

    private boolean checkForLockedStructuresSendPacket(EntityPlayer player, World world) {
        ChunkProviderTwilightForest chunkProvider = ((WorldProviderTwilightForest) world.provider).getChunkProvider();

        int px = MathHelper.floor_double(player.posX);
        int py = MathHelper.floor_double(player.posY);
        int pz = MathHelper.floor_double(player.posZ);

        if (chunkProvider != null && chunkProvider.isBlockNearFullStructure(px, pz, 100)) {

            StructureBoundingBox fullSBB = chunkProvider.getFullSBBNear(px, pz, 100);

            TFFeature nearFeature = TFFeature
                    .getFeatureForRegion(fullSBB.getCenterX() >> 4, fullSBB.getCenterZ() >> 4, world);

            if (!nearFeature.hasProtectionAura || nearFeature.doesPlayerHaveRequiredAchievement(player)) {
                sendAllClearPacket(world, player);
                return false;
            } else {
                sendStructureProtectionPacket(world, player, fullSBB);
                return true;
            }

        } else {
            return false;
        }
    }

    /**
     * Check what biome the player is in, and see if current progression allows that biome. If not, take appropriate
     * action
     */
    private void checkBiomeForProgression(EntityPlayer player, World world) {
        BiomeGenBase currentBiome = world
                .getBiomeGenForCoords(MathHelper.floor_double(player.posX), MathHelper.floor_double(player.posZ));

        if (currentBiome instanceof TFBiomeBase tfBiome) {

            boolean dangerousBiome = !tfBiome.doesPlayerHaveRequiredAchievement(player);
            if (dangerousBiome) {
                tfBiome.enforceProgession(player, world);
            }
        }
    }

    @SubscribeEvent
    public void onItemDroppedByPlayer(ItemTossEvent event) {
        if (!TwilightForestMod.disablePortalCreation && !event.isCanceled()
                && this.portalItem != null
                && event.entityItem.getEntityItem().getItem() == this.portalItem) {
            // Check if player is opped
            if (TwilightForestMod.adminOnlyPortals && !MinecraftServer.getServer().getConfigurationManager()
                    .func_152596_g(event.player.getGameProfile())) {
                return;
            }
            event.player.joinEntityItemWithWorld(new PortalEntityItem(event.entityItem, event.player));
            event.setCanceled(true);
        }
    }

    private static class PortalEntityItem extends EntityItem {

        // the UUID of the player that dropped this portal item
        private final UUID uuidPlayerWhoDroppedThis;

        public PortalEntityItem(EntityItem entityItemToReplace, EntityPlayer player) {
            super(
                    entityItemToReplace.worldObj,
                    entityItemToReplace.posX,
                    entityItemToReplace.posY,
                    entityItemToReplace.posZ,
                    entityItemToReplace.getEntityItem());
            this.delayBeforeCanPickup = entityItemToReplace.delayBeforeCanPickup;
            this.motionX = entityItemToReplace.motionX;
            this.motionY = entityItemToReplace.motionY;
            this.motionZ = entityItemToReplace.motionZ;
            this.uuidPlayerWhoDroppedThis = player.getUniqueID();
        }

        @Override
        public void onUpdate() {
            if (!this.worldObj.isRemote && this.ticksExisted % 20 == 0) if (this.worldObj.provider.dimensionId == 0
                    || this.worldObj.provider.dimensionId == TwilightForestMod.dimensionID
                    || TwilightForestMod.allowPortalsInOtherDimensions) {
                        if (this.worldObj.isMaterialInBB(this.boundingBox, Material.water)) {
                            // make sparkles in the area
                            Random rand = new Random();
                            for (int k = 0; k < 2; k++) {
                                double d = rand.nextGaussian() * 0.02D;
                                double d1 = rand.nextGaussian() * 0.02D;
                                double d2 = rand.nextGaussian() * 0.02D;
                                this.worldObj.spawnParticle("spell", this.posX, this.posY + 0.2, this.posZ, d, d1, d2);
                            }
                            int dx = MathHelper.floor_double(this.posX);
                            int dy = MathHelper.floor_double(this.posY);
                            int dz = MathHelper.floor_double(this.posZ);
                            // try to make a portal
                            if (((BlockTFPortal) TFBlocks.portal).tryToCreatePortal(this.worldObj, dx, dy, dz)) {
                                // func_152378_a = getPlayerEntityByUUID
                                final EntityPlayer player = this.worldObj.func_152378_a(this.uuidPlayerWhoDroppedThis);
                                if (player != null) {
                                    player.triggerAchievement(TFAchievementPage.twilightPortal);
                                }
                                this.setDead();
                            }
                        }
                    }
            super.onUpdate();
        }

    }

}
