package twilightforest.client.renderer;

import net.minecraft.block.Block;
import net.minecraft.client.model.ModelChest;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import twilightforest.TwilightForestMod;
import twilightforest.block.BlockTFChest;
import twilightforest.block.BlockTFChest.WoodType;
import twilightforest.client.model.ModelTFChest;
import twilightforest.client.model.ModelTFLargeChest;
import twilightforest.tileentity.TileEntityTFChest;

@SideOnly(Side.CLIENT)
public class TileEntityTFChestRenderer extends TileEntitySpecialRenderer {

    public static final String[] names = { "twilight", "canopy", "mangrove", "darkwood", "time", "trans", "mining",
            "sort" };
    private static final ResourceLocation[] doubleTextures = new ResourceLocation[names.length];
    private static final ResourceLocation[] textures = new ResourceLocation[names.length];
    private ModelChest modelChest = new ModelTFChest();
    private ModelChest modelLargeChest = new ModelTFLargeChest();

    public TileEntityTFChestRenderer() {
        for (int i = 0; i < names.length; i++) {
            doubleTextures[i] = new ResourceLocation(TwilightForestMod.MODEL_DIR + "chest/" + names[i] + "_double.png");
            textures[i] = new ResourceLocation(TwilightForestMod.MODEL_DIR + "chest/" + names[i] + ".png");
        }
    }

    public void renderTileEntityAt(TileEntityChest tileEntity, double x, double y, double z, float f) {
        int i;

        if (!tileEntity.hasWorldObj()) {
            i = 0;
        } else {
            Block block = tileEntity.getBlockType();
            i = tileEntity.getBlockMetadata();

            if (block instanceof BlockTFChest && i == 0) {
                try {
                    ((BlockTFChest) block).func_149954_e(
                            tileEntity.getWorldObj(),
                            tileEntity.xCoord,
                            tileEntity.yCoord,
                            tileEntity.zCoord);
                } catch (ClassCastException e) {
                    FMLLog.severe(
                            "Attempted to render a chest at %d,  %d, %d that was not a chest",
                            tileEntity.xCoord,
                            tileEntity.yCoord,
                            tileEntity.zCoord);
                }
                i = tileEntity.getBlockMetadata();
            }

            ((TileEntityTFChest) tileEntity).checkForAdjacentChests();
        }

        WoodType type = WoodType.CANOPY;
        if (tileEntity instanceof TileEntityTFChest) {
            if (((TileEntityTFChest) tileEntity).cachedMaterial == WoodType.NULL) {
                Block block = tileEntity.getWorldObj()
                        .getBlock(tileEntity.xCoord, tileEntity.yCoord, tileEntity.zCoord);
                if (block instanceof BlockTFChest) type = ((BlockTFChest) block).getWoodType();
            } else type = ((TileEntityTFChest) tileEntity).cachedMaterial;
        }

        if (tileEntity.adjacentChestZNeg == null && tileEntity.adjacentChestXNeg == null) {
            ModelChest modelchest;

            if (tileEntity.adjacentChestXPos == null && tileEntity.adjacentChestZPos == null) {
                modelchest = this.modelChest;
                this.bindTexture(textures[type.ordinal()]);
            } else {
                modelchest = this.modelLargeChest;
                this.bindTexture(doubleTextures[type.ordinal()]);
            }

            GL11.glPushMatrix();
            GL11.glEnable(GL12.GL_RESCALE_NORMAL);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            GL11.glTranslatef((float) x, (float) y + 1.0F, (float) z + 1.0F);
            GL11.glScalef(1.0F, -1.0F, -1.0F);
            GL11.glTranslatef(0.5F, 0.5F, 0.5F);
            short short1 = 0;

            if (i == 2) {
                short1 = 180;
            }

            if (i == 3) {
                short1 = 0;
            }

            if (i == 4) {
                short1 = 90;
            }

            if (i == 5) {
                short1 = -90;
            }

            if (i == 2 && tileEntity.adjacentChestXPos != null) {
                GL11.glTranslatef(1.0F, 0.0F, 0.0F);
            }

            if (i == 5 && tileEntity.adjacentChestZPos != null) {
                GL11.glTranslatef(0.0F, 0.0F, -1.0F);
            }

            GL11.glRotatef((float) short1, 0.0F, 1.0F, 0.0F);
            GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
            float f1 = tileEntity.prevLidAngle + (tileEntity.lidAngle - tileEntity.prevLidAngle) * f;
            float f2;

            if (tileEntity.adjacentChestZNeg != null) {
                f2 = tileEntity.adjacentChestZNeg.prevLidAngle
                        + (tileEntity.adjacentChestZNeg.lidAngle - tileEntity.adjacentChestZNeg.prevLidAngle) * f;

                if (f2 > f1) {
                    f1 = f2;
                }
            }

            if (tileEntity.adjacentChestXNeg != null) {
                f2 = tileEntity.adjacentChestXNeg.prevLidAngle
                        + (tileEntity.adjacentChestXNeg.lidAngle - tileEntity.adjacentChestXNeg.prevLidAngle) * f;

                if (f2 > f1) {
                    f1 = f2;
                }
            }

            f1 = 1.0F - f1;
            f1 = 1.0F - f1 * f1 * f1;
            modelchest.chestLid.rotateAngleX = -(f1 * (float) Math.PI / 2.0F);
            modelchest.renderAll();
            GL11.glDisable(GL12.GL_RESCALE_NORMAL);
            GL11.glPopMatrix();
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        }
    }

    public void renderTileEntityAt(TileEntity p_147500_1_, double p_147500_2_, double p_147500_4_, double p_147500_6_,
            float p_147500_8_) {
        this.renderTileEntityAt((TileEntityTFChest) p_147500_1_, p_147500_2_, p_147500_4_, p_147500_6_, p_147500_8_);
    }
}
