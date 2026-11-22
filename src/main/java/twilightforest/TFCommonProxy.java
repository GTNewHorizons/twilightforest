package twilightforest;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import cpw.mods.fml.common.network.IGuiHandler;
import twilightforest.client.GuiTFCinderFurnace;
import twilightforest.client.GuiTFGoblinCrafting;
import twilightforest.inventory.ContainerTFCinderFurnace;
import twilightforest.inventory.ContainerTFUncrafting;

public class TFCommonProxy implements IGuiHandler {

    private static final int NO_RENDER_ID = 0;

    /**
     * Called during the pre-load step. Register stuff here. Obviously most stuff in the common category will be just
     * registered in the mod file
     */
    public void doPreLoadRegistration() {}

    /**
     * Called during the load step. Register stuff here. Obviously most stuff in the common category will be just
     * registered in the mod file
     */
    public void doOnLoadRegistration() {}

    public int getCritterBlockRenderID() {
        return NO_RENDER_ID;
    }

    public int getPlantBlockRenderID() {
        return NO_RENDER_ID;
    }

    public int getCakeBlockRenderID() {
        return NO_RENDER_ID;
    }

    public int getChestBlockRenderID() {
        return NO_RENDER_ID;
    }

    public int getComplexBlockRenderID() {
        return NO_RENDER_ID;
    }

    public int getNagastoneBlockRenderID() {
        return NO_RENDER_ID;
    }

    public int getNewNagastoneBlockRenderID() {
        return NO_RENDER_ID;
    }

    public int getNagastoneEtchedStairsBlockRenderID() {
        return NO_RENDER_ID;
    }

    public int getSpiralBricksBlockRenderID() {
        return NO_RENDER_ID;
    }

    public int getMagicLeavesBlockRenderID() {
        return NO_RENDER_ID;
    }

    public int getPedestalBlockRenderID() {
        return NO_RENDER_ID;
    }

    public int getThornsBlockRenderID() {
        return NO_RENDER_ID;
    }

    public int getKnightmetalBlockRenderID() {
        return NO_RENDER_ID;
    }

    public int getFieryMetalBlockRenderID() {
        return NO_RENDER_ID;
    }

    public int getHugeLilyPadBlockRenderID() {
        return NO_RENDER_ID;
    }

    public int getCastleMagicBlockRenderID() {
        return NO_RENDER_ID;
    }

    public int registerArmorRenderID(String prefix) {
        return NO_RENDER_ID;
    }

    public World getClientWorld() {
        return null;
    }

    /**
     * Spawns a particle. This is my copy of RenderGlobal.spawnParticle where I implement custom particles. Null op
     * except on the client.
     */
    public void spawnParticle(World world, String particleType, double x, double y, double z, double velX, double velY,
            double velZ) {}

    @Override
    public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
        if (id == TwilightForestMod.GUI_ID_UNCRAFTING) {
            return new ContainerTFUncrafting(player.inventory, world, x, y, z);
        } else if (id == TwilightForestMod.GUI_ID_FURNACE) {
            return new ContainerTFCinderFurnace(player.inventory, world, x, y, z);
        } else {
            return null;
        }
    }

    @Override
    public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
        if (id == TwilightForestMod.GUI_ID_UNCRAFTING) {
            return new GuiTFGoblinCrafting(player.inventory, world, x, y, z);
        } else if (id == TwilightForestMod.GUI_ID_FURNACE) {
            return new GuiTFCinderFurnace(player.inventory, world, x, y, z);
        } else {
            return null;
        }
    }

    public ModelBiped getKnightlyArmorModel(int armorSlot) {
        return null;
    }

    public ModelBiped getPhantomArmorModel(int armorSlot) {
        return null;
    }

    public ModelBiped getYetiArmorModel(int armorSlot) {
        return null;
    }

    public ModelBiped getArcticArmorModel(int armorSlot) {
        return null;
    }

    public ModelBiped getFieryArmorModel(int armorSlot) {
        return null;
    }

    public ModelBiped getTrophyArmorModel(int boss) {
        return null;
    }

    public ModelBiped getCritterArmorModel(int critter) {
        return null;
    }

    public void doBlockAnnihilateEffect(World worldObj, int blockX, int blockY, int blockZ) {}

    public boolean checkForSound(ChunkCoordinates chunkcoordinates) {
        return true;
    }

    public void stopSound(World worldIn, int x, int y, int z) {}

    public void playSound(World worldObj, ChunkCoordinates chunkcoordinates, ResourceLocation soundResource) {}

}
