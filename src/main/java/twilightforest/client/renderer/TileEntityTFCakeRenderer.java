package twilightforest.client.renderer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

import twilightforest.TwilightForestMod;
import twilightforest.tileentity.TileEntityTFCake;

public class TileEntityTFCakeRenderer extends TileEntitySpecialRenderer {

    private static final String TEXTURE_DIR = TwilightForestMod.ID + ":textures/blocks/experiment115/";
    private static final ResourceLocation textureSide = new ResourceLocation(TEXTURE_DIR + "experiment115_side.png");
    private static final ResourceLocation textureInner = new ResourceLocation(TEXTURE_DIR + "experiment115_inner.png");
    private static final ResourceLocation textureBottom = new ResourceLocation(
            TEXTURE_DIR + "experiment115_bottom.png");
    private static final ResourceLocation textureTop = new ResourceLocation(TEXTURE_DIR + "experiment115_top.png");
    private static final ResourceLocation textureSpinkle = new ResourceLocation(
            TEXTURE_DIR + "experiment115_sprinkle.png");

    public TileEntityTFCakeRenderer() {

    }

    @Override
    public void renderTileEntityAt(TileEntity tileentity, double d, double d1, double d2, float fl) {
        renderTileEntityCakeAt((TileEntityTFCake) tileentity, d, d1, d2, fl);
    }

    /**
     * Render a tasty cake!
     */
    private void renderTileEntityCakeAt(TileEntityTFCake tileentity, double x, double y, double z, float fl) {
        int l = tileentity.getBlockMetadata();
        float f = 0.0625F;
        float f1 = (l + 1) / 9.0f;
        final Tessellator tessellator = Tessellator.instance;

        Minecraft.getMinecraft().getTextureManager().bindTexture(textureSide);
        tessellator.startDrawingQuads();

        switch (l) {
            case 1:
                // Face x-
                tessellator.setNormal(-1.0f, 0.0f, 0.0f);
                tessellator.addVertexWithUV(x + f, y, z + 0.5d, 0.5d, 1.0d);
                tessellator.addVertexWithUV(x + f, y + 0.5d, z + 0.5d, 0.5d, 0.5d);
                tessellator.addVertexWithUV(x + f, y + 0.5d, z + f, f, 0.5d);
                tessellator.addVertexWithUV(x + f, y, z + f, f, 1.0d);

                tessellator.draw();
                tessellator.startDrawingQuads();
                Minecraft.getMinecraft().getTextureManager().bindTexture(textureInner);

                // Face z+
                tessellator.setNormal(0.0f, 0.0f, 1.0f);
                tessellator.addVertexWithUV(x + 0.5d, y, z + 0.5d, 0.5d, 1.0d);
                tessellator.addVertexWithUV(x + 0.5d, y + 0.5d, z + 0.5d, 0.5d, 0.5d);
                tessellator.addVertexWithUV(x + f, y + 0.5d, z + 0.5d, f, 0.5d);
                tessellator.addVertexWithUV(x + f, y, z + 0.5d, f, 1.0d);

                // Diagonal facet
                tessellator.setNormal(0.5f, 0.0f, -0.5f);
                tessellator.addVertexWithUV(x + f, y, z + f, 1 - f, 1.0d);
                tessellator.addVertexWithUV(x + f, y + 0.5d, z + f, 1 - f, 0.5d);
                tessellator.addVertexWithUV(x + 0.5d, y + 0.5d, z + 0.5d, 0.5d, 0.5d);
                tessellator.addVertexWithUV(x + 0.5d, y, z + 0.5d, 0.5d, 1.0d);

                // Drawing triangles
                tessellator.draw();
                tessellator.startDrawing(4);
                Minecraft.getMinecraft().getTextureManager().bindTexture(textureBottom);

                // Bottom
                tessellator.setNormal(0.0f, -1.0f, 0.0f);
                tessellator.addVertexWithUV(x + f, y, z + 0.5d, f, 0.5d);
                tessellator.addVertexWithUV(x + f, y, z + f, f, f);
                tessellator.addVertexWithUV(x + 0.5d, y, z + 0.5d, 0.5d, 0.5d);

                tessellator.draw();
                tessellator.startDrawing(4);
                Minecraft.getMinecraft().getTextureManager().bindTexture(textureTop);

                // Top
                tessellator.setNormal(0.0f, 1.0f, 0.0f);
                tessellator.addVertexWithUV(x + f, y + 0.5d, z + f, f, f);
                tessellator.addVertexWithUV(x + f, y + 0.5d, z + 0.5d, f, 0.5d);
                tessellator.addVertexWithUV(x + 0.5d, y + 0.5d, z + 0.5d, 0.5d, 0.5d);

                if (tileentity.getSprinkled()) {
                    tessellator.draw();
                    tessellator.startDrawing(4);
                    Minecraft.getMinecraft().getTextureManager().bindTexture(textureSpinkle);

                    tessellator.setNormal(0.0f, 1.0f, 0.0f);
                    tessellator.setColorOpaque_F(f1, f1, f1);
                    tessellator.addVertexWithUV(x + f, y + 0.5d, z + f, f, f);
                    tessellator.addVertexWithUV(x + f, y + 0.5d, z + 0.5d, f, 0.5d);
                    tessellator.addVertexWithUV(x + 0.5d, y + 0.5d, z + 0.5d, 0.5d, 0.5d);
                }
                break;
            case 2:
                // Face x-
                tessellator.setNormal(-1.0f, 0.0f, 0.0f);
                tessellator.addVertexWithUV(x + f, y, z + 0.5d, 0.5d, 1.0d);
                tessellator.addVertexWithUV(x + f, y + 0.5d, z + 0.5d, 0.5d, 0.5d);
                tessellator.addVertexWithUV(x + f, y + 0.5d, z + f, f, 0.5d);
                tessellator.addVertexWithUV(x + f, y, z + f, f, 1.0d);

                // Face z-
                tessellator.setNormal(0.0f, 0.0f, -1.0f);
                tessellator.addVertexWithUV(x + f, y, z + f, 1.0d - f, 1.0d);
                tessellator.addVertexWithUV(x + f, y + 0.5d, z + f, 1.0d - f, 0.5d);
                tessellator.addVertexWithUV(x + 0.5d, y + 0.5d, z + f, 0.5d, 0.5d);
                tessellator.addVertexWithUV(x + 0.5d, y, z + f, 0.5d, 1.0d);

                tessellator.draw();
                tessellator.startDrawingQuads();
                Minecraft.getMinecraft().getTextureManager().bindTexture(textureInner);

                // Face z+
                tessellator.setNormal(0.0f, 0.0f, 1.0f);
                tessellator.addVertexWithUV(x + 0.5d, y, z + 0.5d, 0.5d, 1.0d);
                tessellator.addVertexWithUV(x + 0.5d, y + 0.5d, z + 0.5d, 0.5d, 0.5d);
                tessellator.addVertexWithUV(x + f, y + 0.5d, z + 0.5d, f, 0.5d);
                tessellator.addVertexWithUV(x + f, y, z + 0.5d, f, 1.0d);

                // Face x+
                tessellator.setNormal(1.0f, 0.0f, 0.0f);
                tessellator.addVertexWithUV(x + 0.5d, y, z + f, 1.0d - f, 1.0d);
                tessellator.addVertexWithUV(x + 0.5d, y + 0.5d, z + f, 1.0d - f, 0.5d);
                tessellator.addVertexWithUV(x + 0.5d, y + 0.5d, z + 0.5d, 0.5d, 0.5d);
                tessellator.addVertexWithUV(x + 0.5d, y, z + 0.5d, 0.5d, 1.0d);

                tessellator.draw();
                tessellator.startDrawingQuads();
                Minecraft.getMinecraft().getTextureManager().bindTexture(textureBottom);

                // Bottom
                tessellator.setNormal(0.0f, -1.0f, 0.0f);
                tessellator.addVertexWithUV(x + f, y, z + 0.5d, f, 0.5d);
                tessellator.addVertexWithUV(x + f, y, z + f, f, f);
                tessellator.addVertexWithUV(x + 0.5d, y, z + f, 0.5d, f);
                tessellator.addVertexWithUV(x + 0.5d, y, z + 0.5d, 0.5d, 0.5d);

                tessellator.draw();
                tessellator.startDrawingQuads();
                Minecraft.getMinecraft().getTextureManager().bindTexture(textureTop);

                // Top
                tessellator.setNormal(0.0f, 1.0f, 0.0f);
                tessellator.addVertexWithUV(x + f, y + 0.5d, z + f, f, f);
                tessellator.addVertexWithUV(x + f, y + 0.5d, z + 0.5d, f, 0.5d);
                tessellator.addVertexWithUV(x + 0.5d, y + 0.5d, z + 0.5d, 0.5d, 0.5d);
                tessellator.addVertexWithUV(x + 0.5d, y + 0.5d, z + f, 0.5d, f);

                if (tileentity.getSprinkled()) {
                    tessellator.draw();
                    tessellator.startDrawingQuads();
                    Minecraft.getMinecraft().getTextureManager().bindTexture(textureSpinkle);

                    tessellator.setNormal(0.0f, 1.0f, 0.0f);
                    tessellator.setColorOpaque_F(f1, f1, f1);
                    tessellator.addVertexWithUV(x + f, y + 0.5d, z + f, f, f);
                    tessellator.addVertexWithUV(x + f, y + 0.5d, z + 0.5d, f, 0.5d);
                    tessellator.addVertexWithUV(x + 0.5d, y + 0.5d, z + 0.5d, 0.5d, 0.5d);
                    tessellator.addVertexWithUV(x + 0.5d, y + 0.5d, z + f, 0.5d, f);
                }
                break;
            case 3:
                // Face x-
                tessellator.setNormal(-1.0f, 0.0f, 0.0f);
                tessellator.addVertexWithUV(x + f, y, z + 0.5d, 0.5d, 1.0d);
                tessellator.addVertexWithUV(x + f, y + 0.5d, z + 0.5d, 0.5d, 0.5d);
                tessellator.addVertexWithUV(x + f, y + 0.5d, z + f, f, 0.5d);
                tessellator.addVertexWithUV(x + f, y, z + f, f, 1.0d);

                // Face z-
                tessellator.setNormal(0.0f, 0.0f, -1.0f);
                tessellator.addVertexWithUV(x + f, y, z + f, 1.0d - f, 1.0d);
                tessellator.addVertexWithUV(x + f, y + 0.5d, z + f, 1.0d - f, 0.5d);
                tessellator.addVertexWithUV(x + 1.0d - f, y + 0.5d, z + f, f, 0.5d);
                tessellator.addVertexWithUV(x + 1.0d - f, y, z + f, f, 1.0d);

                tessellator.draw();
                tessellator.startDrawingQuads();
                Minecraft.getMinecraft().getTextureManager().bindTexture(textureInner);

                // Face z+
                tessellator.setNormal(0.0f, 0.0f, 1.0f);
                tessellator.addVertexWithUV(x + 0.5d, y, z + 0.5d, 0.5d, 1.0d);
                tessellator.addVertexWithUV(x + 0.5d, y + 0.5d, z + 0.5d, 0.5d, 0.5d);
                tessellator.addVertexWithUV(x + f, y + 0.5d, z + 0.5d, f, 0.5d);
                tessellator.addVertexWithUV(x + f, y, z + 0.5d, f, 1.0d);

                // Diagonal facet
                tessellator.setNormal(0.5f, 0.0f, 0.5f);
                tessellator.addVertexWithUV(x + 1.0d - f, y, z + f, 1.0d - f, 1.0d);
                tessellator.addVertexWithUV(x + 1.0d - f, y + 0.5d, z + f, 1.0d - f, 0.5d);
                tessellator.addVertexWithUV(x + 0.5d, y + 0.5d, z + 0.5d, 0.5d, 0.5d);
                tessellator.addVertexWithUV(x + 0.5d, y, z + 0.5d, 0.5d, 1.0d);

                tessellator.draw();
                tessellator.startDrawingQuads();
                Minecraft.getMinecraft().getTextureManager().bindTexture(textureBottom);

                // Bottom
                tessellator.setNormal(0.0f, -1.0f, 0.0f);
                tessellator.addVertexWithUV(x + f, y, z + 0.5d, f, 0.5d);
                tessellator.addVertexWithUV(x + f, y, z + f, f, f);
                tessellator.addVertexWithUV(x + 1.0d - f, y, z + f, 1.0d - f, f);
                tessellator.addVertexWithUV(x + 0.5d, y, z + 0.5d, 0.5d, 0.5d);

                tessellator.draw();
                tessellator.startDrawingQuads();
                Minecraft.getMinecraft().getTextureManager().bindTexture(textureTop);

                // Top
                tessellator.setNormal(0.0f, 1.0f, 0.0f);
                tessellator.addVertexWithUV(x + f, y + 0.5d, z + f, f, f);
                tessellator.addVertexWithUV(x + f, y + 0.5d, z + 0.5d, f, 0.5d);
                tessellator.addVertexWithUV(x + 0.5d, y + 0.5d, z + 0.5d, 0.5d, 0.5d);
                tessellator.addVertexWithUV(x + 1.0d - f, y + 0.5d, z + f, 1.0d - f, f);

                if (tileentity.getSprinkled()) {
                    tessellator.draw();
                    tessellator.startDrawingQuads();
                    Minecraft.getMinecraft().getTextureManager().bindTexture(textureSpinkle);

                    tessellator.setNormal(0.0f, 1.0f, 0.0f);
                    tessellator.setColorOpaque_F(f1, f1, f1);
                    tessellator.addVertexWithUV(x + f, y + 0.5d, z + f, f, f);
                    tessellator.addVertexWithUV(x + f, y + 0.5d, z + 0.5d, f, 0.5d);
                    tessellator.addVertexWithUV(x + 0.5d, y + 0.5d, z + 0.5d, 0.5d, 0.5d);
                    tessellator.addVertexWithUV(x + 1.0d - f, y + 0.5d, z + f, 1.0d - f, f);
                }
                break;
            case 4:
                // Face x-
                tessellator.setNormal(-1.0f, 0.0f, 0.0f);
                tessellator.addVertexWithUV(x + f, y, z + 0.5d, 0.5d, 1.0d);
                tessellator.addVertexWithUV(x + f, y + 0.5d, z + 0.5d, 0.5d, 0.5d);
                tessellator.addVertexWithUV(x + f, y + 0.5d, z + f, f, 0.5d);
                tessellator.addVertexWithUV(x + f, y, z + f, f, 1.0d);

                // Face x+
                tessellator.setNormal(1.0f, 0.0f, 0.0f);
                tessellator.addVertexWithUV(x + 1.0d - f, y, z + f, 1.0d - f, 1.0d);
                tessellator.addVertexWithUV(x + 1.0d - f, y + 0.5d, z + f, 1.0d - f, 0.5d);
                tessellator.addVertexWithUV(x + 1.0d - f, y + 0.5d, z + 0.5d, 0.5d, 0.5d);
                tessellator.addVertexWithUV(x + 1.0d - f, y, z + 0.5d, 0.5d, 1.0d);

                // Face z-
                tessellator.setNormal(0.0f, 0.0f, -1.0f);
                tessellator.addVertexWithUV(x + f, y, z + f, 1.0d - f, 1.0d);
                tessellator.addVertexWithUV(x + f, y + 0.5d, z + f, 1.0d - f, 0.5d);
                tessellator.addVertexWithUV(x + 1.0d - f, y + 0.5d, z + f, f, 0.5d);
                tessellator.addVertexWithUV(x + 1.0d - f, y, z + f, f, 1.0d);

                tessellator.draw();
                tessellator.startDrawingQuads();
                Minecraft.getMinecraft().getTextureManager().bindTexture(textureInner);

                // Face z+
                tessellator.setNormal(0.0f, 0.0f, 1.0f);
                tessellator.addVertexWithUV(x + 1.0d - f, y, z + 0.5d, 1.0d - f, 1.0d);
                tessellator.addVertexWithUV(x + 1.0d - f, y + 0.5d, z + 0.5d, 1.0d - f, 0.5d);
                tessellator.addVertexWithUV(x + f, y + 0.5d, z + 0.5d, f, 0.5d);
                tessellator.addVertexWithUV(x + f, y, z + 0.5d, f, 1.0d);

                tessellator.draw();
                tessellator.startDrawingQuads();
                Minecraft.getMinecraft().getTextureManager().bindTexture(textureBottom);

                // Bottom
                tessellator.setNormal(0.0f, -1.0f, 0.0f);
                tessellator.addVertexWithUV(x + f, y, z + 0.5d, f, 0.5d);
                tessellator.addVertexWithUV(x + f, y, z + f, f, f);
                tessellator.addVertexWithUV(x + 1.0d - f, y, z + f, 1.0d - f, f);
                tessellator.addVertexWithUV(x + 1.0d - f, y, z + 0.5d, 1.0d - f, 0.5d);

                tessellator.draw();
                tessellator.startDrawingQuads();
                Minecraft.getMinecraft().getTextureManager().bindTexture(textureTop);

                // Top
                tessellator.setNormal(0.0f, 1.0f, 0.0f);
                tessellator.addVertexWithUV(x + f, y + 0.5d, z + f, f, f);
                tessellator.addVertexWithUV(x + f, y + 0.5d, z + 0.5d, f, 0.5d);
                tessellator.addVertexWithUV(x + 1.0d - f, y + 0.5d, z + 0.5d, 1.0d - f, 0.5d);
                tessellator.addVertexWithUV(x + 1.0d - f, y + 0.5d, z + f, 1.0d - f, f);

                if (tileentity.getSprinkled()) {
                    tessellator.draw();
                    tessellator.startDrawingQuads();
                    Minecraft.getMinecraft().getTextureManager().bindTexture(textureSpinkle);

                    tessellator.setNormal(0.0f, 1.0f, 0.0f);
                    tessellator.setColorOpaque_F(f1, f1, f1);
                    tessellator.addVertexWithUV(x + f, y + 0.5d, z + f, f, f);
                    tessellator.addVertexWithUV(x + f, y + 0.5d, z + 0.5d, f, 0.5d);
                    tessellator.addVertexWithUV(x + 1.0d - f, y + 0.5d, z + 0.5d, 1.0d - f, 0.5d);
                    tessellator.addVertexWithUV(x + 1.0d - f, y + 0.5d, z + f, 1.0d - f, f);
                }
                break;
            case 5:
                // Face x-
                tessellator.setNormal(-1.0f, 0.0f, 0.0f);
                tessellator.addVertexWithUV(x + f, y, z + 0.5d, 0.5d, 1.0d);
                tessellator.addVertexWithUV(x + f, y + 0.5d, z + 0.5d, 0.5d, 0.5d);
                tessellator.addVertexWithUV(x + f, y + 0.5d, z + f, f, 0.5d);
                tessellator.addVertexWithUV(x + f, y, z + f, f, 1.0d);

                // Face x+
                tessellator.setNormal(1.0f, 0.0f, 0.0f);
                tessellator.addVertexWithUV(x + 1.0d - f, y, z + f, 1.0d - f, 1.0d);
                tessellator.addVertexWithUV(x + 1.0d - f, y + 0.5d, z + f, 1.0d - f, 0.5d);
                tessellator.addVertexWithUV(x + 1.0d - f, y + 0.5d, z + 1.0d - f, f, 0.5d);
                tessellator.addVertexWithUV(x + 1.0d - f, y, z + 1.0d - f, f, 1.0d);

                // Face z-
                tessellator.setNormal(0.0f, 0.0f, -1.0f);
                tessellator.addVertexWithUV(x + f, y, z + f, 1.0d - f, 1.0d);
                tessellator.addVertexWithUV(x + f, y + 0.5d, z + f, 1.0d - f, 0.5d);
                tessellator.addVertexWithUV(x + 1.0d - f, y + 0.5d, z + f, f, 0.5d);
                tessellator.addVertexWithUV(x + 1.0d - f, y, z + f, f, 1.0d);

                tessellator.draw();
                tessellator.startDrawingQuads();
                Minecraft.getMinecraft().getTextureManager().bindTexture(textureInner);

                // Face z+
                tessellator.setNormal(0.0f, 0.0f, 1.0f);
                tessellator.addVertexWithUV(x + 0.5d, y, z + 0.5d, 0.5d, 1.0d);
                tessellator.addVertexWithUV(x + 0.5d, y + 0.5d, z + 0.5d, 0.5d, 0.5d);
                tessellator.addVertexWithUV(x + f, y + 0.5d, z + 0.5d, f, 0.5d);
                tessellator.addVertexWithUV(x + f, y, z + 0.5d, f, 1.0d);

                // Diagonal facet
                tessellator.setNormal(-0.5f, 0.0f, 0.5f);
                tessellator.addVertexWithUV(x + 1.0d - f, y, z + 1.0d - f, 1.0d - f, 1.0d);
                tessellator.addVertexWithUV(x + 1.0d - f, y + 0.5d, z + 1.0d - f, 1.0d - f, 0.5d);
                tessellator.addVertexWithUV(x + 0.5d, y + 0.5d, z + 0.5d, 0.5d, 0.5d);
                tessellator.addVertexWithUV(x + 0.5d, y, z + 0.5d, 0.5d, 1.0d);

                tessellator.draw();
                tessellator.startDrawing(9);
                Minecraft.getMinecraft().getTextureManager().bindTexture(textureBottom);

                // Bottom
                tessellator.setNormal(0.0f, -1.0f, 0.0f);
                tessellator.addVertexWithUV(x + 0.5d, y, z + 0.5d, 0.5d, 0.5d);
                tessellator.addVertexWithUV(x + f, y, z + 0.5d, f, 0.5d);
                tessellator.addVertexWithUV(x + f, y, z + f, f, f);
                tessellator.addVertexWithUV(x + 1.0d - f, y, z + f, 1.0d - f, f);
                tessellator.addVertexWithUV(x + 1.0d - f, y, z + 1.0d - f, 1.0d - f, 1.0d - f);

                tessellator.draw();
                tessellator.startDrawing(9);
                Minecraft.getMinecraft().getTextureManager().bindTexture(textureTop);

                // Top
                tessellator.setNormal(0.0f, 1.0f, 0.0f);
                tessellator.addVertexWithUV(x + 0.5d, y + 0.5d, z + 0.5d, 0.5d, 0.5d);
                tessellator.addVertexWithUV(x + 1.0d - f, y + 0.5d, z + 1.0d - f, 1.0d - f, 1.0d - f);
                tessellator.addVertexWithUV(x + 1.0d - f, y + 0.5d, z + f, 1.0d - f, f);
                tessellator.addVertexWithUV(x + f, y + 0.5d, z + f, f, f);
                tessellator.addVertexWithUV(x + f, y + 0.5d, z + 0.5d, f, 0.5d);

                if (tileentity.getSprinkled()) {
                    tessellator.draw();
                    tessellator.startDrawing(9);
                    Minecraft.getMinecraft().getTextureManager().bindTexture(textureSpinkle);

                    tessellator.setNormal(0.0f, 1.0f, 0.0f);
                    tessellator.setColorOpaque_F(f1, f1, f1);
                    tessellator.addVertexWithUV(x + 0.5d, y + 0.5d, z + 0.5d, 0.5d, 0.5d);
                    tessellator.addVertexWithUV(x + 1.0d - f, y + 0.5d, z + 1.0d - f, 1.0d - f, 1.0d - f);
                    tessellator.addVertexWithUV(x + 1.0d - f, y + 0.5d, z + f, 1.0d - f, f);
                    tessellator.addVertexWithUV(x + f, y + 0.5d, z + f, f, f);
                    tessellator.addVertexWithUV(x + f, y + 0.5d, z + 0.5d, f, 0.5d);
                }
                break;
            case 6:
                // Face x-
                tessellator.setNormal(-1.0f, 0.0f, 0.0f);
                tessellator.addVertexWithUV(x + f, y, z + 0.5d, 0.5d, 1.0d);
                tessellator.addVertexWithUV(x + f, y + 0.5d, z + 0.5d, 0.5d, 0.5d);
                tessellator.addVertexWithUV(x + f, y + 0.5d, z + f, f, 0.5d);
                tessellator.addVertexWithUV(x + f, y, z + f, f, 1.0d);

                // Face x+
                tessellator.setNormal(1.0f, 0.0f, 0.0f);
                tessellator.addVertexWithUV(x + 1.0d - f, y, z + f, 1.0d - f, 1.0d);
                tessellator.addVertexWithUV(x + 1.0d - f, y + 0.5d, z + f, 1.0d - f, 0.5d);
                tessellator.addVertexWithUV(x + 1.0d - f, y + 0.5d, z + 1.0d - f, f, 0.5d);
                tessellator.addVertexWithUV(x + 1.0d - f, y, z + 1.0d - f, f, 1.0d);

                // Face z-
                tessellator.setNormal(0.0f, 0.0f, -1.0f);
                tessellator.addVertexWithUV(x + f, y, z + f, 1.0d - f, 1.0d);
                tessellator.addVertexWithUV(x + f, y + 0.5d, z + f, 1.0d - f, 0.5d);
                tessellator.addVertexWithUV(x + 1.0d - f, y + 0.5d, z + f, f, 0.5d);
                tessellator.addVertexWithUV(x + 1.0d - f, y, z + f, f, 1.0d);

                // Face z+
                tessellator.setNormal(0.0f, 0.0f, 1.0f);
                tessellator.addVertexWithUV(x + 1.0d - f, y, z + 1.0d - f, 1.0d - f, 1.0d);
                tessellator.addVertexWithUV(x + 1.0d - f, y + 0.5d, z + 1.0d - f, 1.0d - f, 0.5d);
                tessellator.addVertexWithUV(x + 0.5d, y + 0.5d, z + 1.0d - f, 0.5d, 0.5d);
                tessellator.addVertexWithUV(x + 0.5d, y, z + 1.0d - f, 0.5d, 1.0d);

                tessellator.draw();
                tessellator.startDrawingQuads();
                Minecraft.getMinecraft().getTextureManager().bindTexture(textureInner);

                // Inner Face z+
                tessellator.setNormal(0.0f, 0.0f, 1.0f);
                tessellator.addVertexWithUV(x + 0.5d, y, z + 0.5d, 0.5d, 1.0d);
                tessellator.addVertexWithUV(x + 0.5d, y + 0.5d, z + 0.5d, 0.5d, 0.5d);
                tessellator.addVertexWithUV(x + f, y + 0.5d, z + 0.5d, f, 0.5d);
                tessellator.addVertexWithUV(x + f, y, z + 0.5d, f, 1.0d);

                // Inner Face x-
                tessellator.setNormal(-1.0f, 0.0f, 0.0f);
                tessellator.addVertexWithUV(x + 0.5d, y, z + 1.0d - f, 1.0d - f, 1.0d);
                tessellator.addVertexWithUV(x + 0.5d, y + 0.5d, z + 1.0d - f, 1.0d - f, 0.5d);
                tessellator.addVertexWithUV(x + 0.5d, y + 0.5d, z + 0.5d, 0.5d, 0.5d);
                tessellator.addVertexWithUV(x + 0.5d, y, z + 0.5d, 0.5d, 1.0d);

                tessellator.draw();
                tessellator.startDrawing(9);
                Minecraft.getMinecraft().getTextureManager().bindTexture(textureBottom);

                // Bottom
                tessellator.setNormal(0.0f, -1.0f, 0.0f);
                tessellator.addVertexWithUV(x + 0.5d, y, z + 0.5d, 0.5d, 0.5d);
                tessellator.addVertexWithUV(x + f, y, z + 0.5d, f, 0.5d);
                tessellator.addVertexWithUV(x + f, y, z + f, f, f);
                tessellator.addVertexWithUV(x + 1.0d - f, y, z + f, 1.0d - f, f);
                tessellator.addVertexWithUV(x + 1.0d - f, y, z + 1.0d - f, 1.0d - f, 1.0d - f);
                tessellator.addVertexWithUV(x + 0.5d, y, z + 1.0d - f, 0.5d, 1.0d - f);

                tessellator.draw();
                tessellator.startDrawing(9);
                Minecraft.getMinecraft().getTextureManager().bindTexture(textureTop);

                // Top
                tessellator.setNormal(0.0f, 1.0f, 0.0f);
                tessellator.addVertexWithUV(x + 0.5d, y + 0.5d, z + 0.5d, 0.5d, 0.5d);
                tessellator.addVertexWithUV(x + 0.5d, y + 0.5d, z + 1.0d - f, 0.5d, 1.0d - f);
                tessellator.addVertexWithUV(x + 1.0d - f, y + 0.5d, z + 1.0d - f, 1.0d - f, 1.0d - f);
                tessellator.addVertexWithUV(x + 1.0d - f, y + 0.5d, z + f, 1.0d - f, f);
                tessellator.addVertexWithUV(x + f, y + 0.5d, z + f, f, f);
                tessellator.addVertexWithUV(x + f, y + 0.5d, z + 0.5d, f, 0.5d);

                if (tileentity.getSprinkled()) {
                    tessellator.draw();
                    tessellator.startDrawing(9);
                    Minecraft.getMinecraft().getTextureManager().bindTexture(textureSpinkle);

                    tessellator.setNormal(0.0f, 1.0f, 0.0f);
                    tessellator.setColorOpaque_F(f1, f1, f1);
                    tessellator.addVertexWithUV(x + 0.5d, y + 0.5d, z + 0.5d, 0.5d, 0.5d);
                    tessellator.addVertexWithUV(x + 0.5d, y + 0.5d, z + 1.0d - f, 0.5d, 1.0d - f);
                    tessellator.addVertexWithUV(x + 1.0d - f, y + 0.5d, z + 1.0d - f, 1.0d - f, 1.0d - f);
                    tessellator.addVertexWithUV(x + 1.0d - f, y + 0.5d, z + f, 1.0d - f, f);
                    tessellator.addVertexWithUV(x + f, y + 0.5d, z + f, f, f);
                    tessellator.addVertexWithUV(x + f, y + 0.5d, z + 0.5d, f, 0.5d);
                }
                break;
            case 7:
                // Face x-
                tessellator.setNormal(-1.0f, 0.0f, 0.0f);
                tessellator.addVertexWithUV(x + f, y, z + 0.5d, 0.5d, 1.0d);
                tessellator.addVertexWithUV(x + f, y + 0.5d, z + 0.5d, 0.5d, 0.5d);
                tessellator.addVertexWithUV(x + f, y + 0.5d, z + f, f, 0.5d);
                tessellator.addVertexWithUV(x + f, y, z + f, f, 1.0d);

                // Face x+
                tessellator.setNormal(1.0f, 0.0f, 0.0f);
                tessellator.addVertexWithUV(x + 1.0d - f, y, z + f, 1.0d - f, 1.0d);
                tessellator.addVertexWithUV(x + 1.0d - f, y + 0.5d, z + f, 1.0d - f, 0.5d);
                tessellator.addVertexWithUV(x + 1.0d - f, y + 0.5d, z + 1.0d - f, f, 0.5d);
                tessellator.addVertexWithUV(x + 1.0d - f, y, z + 1.0d - f, f, 1.0d);

                // Face z-
                tessellator.setNormal(0.0f, 0.0f, -1.0f);
                tessellator.addVertexWithUV(x + f, y, z + f, 1.0d - f, 1.0d);
                tessellator.addVertexWithUV(x + f, y + 0.5d, z + f, 1.0d - f, 0.5d);
                tessellator.addVertexWithUV(x + 1.0d - f, y + 0.5d, z + f, f, 0.5d);
                tessellator.addVertexWithUV(x + 1.0d - f, y, z + f, f, 1.0d);

                // Face z+
                tessellator.setNormal(0.0f, 0.0f, 1.0f);
                tessellator.addVertexWithUV(x + 1.0d - f, y, z + 1.0d - f, 1.0d - f, 1.0d);
                tessellator.addVertexWithUV(x + 1.0d - f, y + 0.5d, z + 1.0d - f, 1.0d - f, 0.5d);
                tessellator.addVertexWithUV(x + f, y + 0.5d, z + 1.0d - f, f, 0.5d);
                tessellator.addVertexWithUV(x + f, y, z + 1.0d - f, f, 1.0d);

                tessellator.draw();
                tessellator.startDrawingQuads();
                Minecraft.getMinecraft().getTextureManager().bindTexture(textureInner);

                // Inner Face z+
                tessellator.setNormal(0.0f, 0.0f, 1.0f);
                tessellator.addVertexWithUV(x + 0.5d, y, z + 0.5d, 0.5d, 1.0d);
                tessellator.addVertexWithUV(x + 0.5d, y + 0.5d, z + 0.5d, 0.5d, 0.5d);
                tessellator.addVertexWithUV(x + f, y + 0.5d, z + 0.5d, f, 0.5d);
                tessellator.addVertexWithUV(x + f, y, z + 0.5d, f, 1.0d);

                // Diagonal facet
                tessellator.setNormal(-0.5f, 0.0f, -0.5f);
                tessellator.addVertexWithUV(x + f, y, z + 1.0d - f, 1.0d - f, 1.0d);
                tessellator.addVertexWithUV(x + f, y + 0.5d, z + 1.0d - f, 1.0d - f, 0.5d);
                tessellator.addVertexWithUV(x + 0.5d, y + 0.5d, z + 0.5d, 0.5d, 0.5d);
                tessellator.addVertexWithUV(x + 0.5d, y, z + 0.5d, 0.5d, 1.0d);

                tessellator.draw();
                tessellator.startDrawing(9);
                Minecraft.getMinecraft().getTextureManager().bindTexture(textureBottom);

                // Bottom
                tessellator.setNormal(0.0f, -1.0f, 0.0f);
                tessellator.addVertexWithUV(x + 0.5d, y, z + 0.5d, 0.5d, 0.5d);
                tessellator.addVertexWithUV(x + f, y, z + 0.5d, f, 0.5d);
                tessellator.addVertexWithUV(x + f, y, z + f, f, f);
                tessellator.addVertexWithUV(x + 1.0d - f, y, z + f, 1.0d - f, f);
                tessellator.addVertexWithUV(x + 1.0d - f, y, z + 1.0d - f, 1.0d - f, 1.0d - f);
                tessellator.addVertexWithUV(x + 0.5d, y, z + 1.0d - f, 0.5d, 1.0d - f);
                tessellator.addVertexWithUV(x + f, y, z + 1.0d - f, f, 1.0d - f);

                tessellator.draw();
                tessellator.startDrawing(9);
                Minecraft.getMinecraft().getTextureManager().bindTexture(textureTop);

                // Top
                tessellator.setNormal(0.0f, 1.0f, 0.0f);
                tessellator.addVertexWithUV(x + 0.5d, y + 0.5d, z + 0.5d, 0.5d, 0.5d);
                tessellator.addVertexWithUV(x + f, y + 0.5d, z + 1.0d - f, f, 1.0d - f);
                tessellator.addVertexWithUV(x + 0.5d, y + 0.5d, z + 1.0d - f, 0.5d, 1.0d - f);
                tessellator.addVertexWithUV(x + 1.0d - f, y + 0.5d, z + 1.0d - f, 1.0d - f, 1.0d - f);
                tessellator.addVertexWithUV(x + 1.0d - f, y + 0.5d, z + f, 1.0d - f, f);
                tessellator.addVertexWithUV(x + f, y + 0.5d, z + f, f, f);
                tessellator.addVertexWithUV(x + f, y + 0.5d, z + 0.5d, f, 0.5d);

                if (tileentity.getSprinkled()) {
                    tessellator.draw();
                    tessellator.startDrawing(9);
                    Minecraft.getMinecraft().getTextureManager().bindTexture(textureSpinkle);

                    tessellator.setNormal(0.0f, 1.0f, 0.0f);
                    tessellator.setColorOpaque_F(f1, f1, f1);
                    tessellator.addVertexWithUV(x + 0.5d, y + 0.5d, z + 0.5d, 0.5d, 0.5d);
                    tessellator.addVertexWithUV(x + f, y + 0.5d, z + 1.0d - f, f, 1.0d - f);
                    tessellator.addVertexWithUV(x + 0.5d, y + 0.5d, z + 1.0d - f, 0.5d, 1.0d - f);
                    tessellator.addVertexWithUV(x + 1.0d - f, y + 0.5d, z + 1.0d - f, 1.0d - f, 1.0d - f);
                    tessellator.addVertexWithUV(x + 1.0d - f, y + 0.5d, z + f, 1.0d - f, f);
                    tessellator.addVertexWithUV(x + f, y + 0.5d, z + f, f, f);
                    tessellator.addVertexWithUV(x + f, y + 0.5d, z + 0.5d, f, 0.5d);
                }
                break;
            case 8:
                // Face x-
                tessellator.setNormal(-1.0f, 0.0f, 0.0f);
                tessellator.addVertexWithUV(x + f, y, z + 1.0d - f, 1.0d - f, 1.0d);
                tessellator.addVertexWithUV(x + f, y + 0.5d, z + 1.0d - f, 1.0d - f, 0.5d);
                tessellator.addVertexWithUV(x + f, y + 0.5d, z + f, f, 0.5d);
                tessellator.addVertexWithUV(x + f, y, z + f, f, 1.0d);

                // Face x+
                tessellator.setNormal(1.0f, 0.0f, 0.0f);
                tessellator.addVertexWithUV(x + 1.0d - f, y, z + f, 1.0d - f, 1.0d);
                tessellator.addVertexWithUV(x + 1.0d - f, y + 0.5d, z + f, 1.0d - f, 0.5d);
                tessellator.addVertexWithUV(x + 1.0d - f, y + 0.5d, z + 1.0d - f, f, 0.5d);
                tessellator.addVertexWithUV(x + 1.0d - f, y, z + 1.0d - f, f, 1.0d);

                // Face z-
                tessellator.setNormal(0.0f, 0.0f, -1.0f);
                tessellator.addVertexWithUV(x + f, y, z + f, 1.0d - f, 1.0d);
                tessellator.addVertexWithUV(x + f, y + 0.5d, z + f, 1.0d - f, 0.5d);
                tessellator.addVertexWithUV(x + 1.0d - f, y + 0.5d, z + f, f, 0.5d);
                tessellator.addVertexWithUV(x + 1.0d - f, y, z + f, f, 1.0d);

                // Face z+
                tessellator.setNormal(0.0f, 0.0f, 1.0f);
                tessellator.addVertexWithUV(x + 1.0d - f, y, z + 1.0d - f, 1.0d - f, 1.0d);
                tessellator.addVertexWithUV(x + 1.0d - f, y + 0.5d, z + 1.0d - f, 1.0d - f, 0.5d);
                tessellator.addVertexWithUV(x + f, y + 0.5d, z + 1.0d - f, f, 0.5d);
                tessellator.addVertexWithUV(x + f, y, z + 1.0d - f, f, 1.0d);

                tessellator.draw();
                tessellator.startDrawingQuads();
                Minecraft.getMinecraft().getTextureManager().bindTexture(textureBottom);

                // Bottom
                tessellator.setNormal(0.0f, -1.0f, 0.0f);
                tessellator.addVertexWithUV(x + f, y, z + f, f, f);
                tessellator.addVertexWithUV(x + 1.0d - f, y, z + f, 1.0d - f, f);
                tessellator.addVertexWithUV(x + 1.0d - f, y, z + 1.0d - f, 1.0d - f, 1.0d - f);
                tessellator.addVertexWithUV(x + f, y, z + 1.0d - f, f, 1.0d - f);

                tessellator.draw();
                tessellator.startDrawingQuads();
                Minecraft.getMinecraft().getTextureManager().bindTexture(textureTop);

                // Top
                tessellator.setNormal(0.0f, 1.0f, 0.0f);
                tessellator.addVertexWithUV(x + f, y + 0.5d, z + 1.0d - f, f, 1.0d - f);
                tessellator.addVertexWithUV(x + 1.0d - f, y + 0.5d, z + 1.0d - f, 1.0d - f, 1.0d - f);
                tessellator.addVertexWithUV(x + 1.0d - f, y + 0.5d, z + f, 1.0d - f, f);
                tessellator.addVertexWithUV(x + f, y + 0.5d, z + f, f, f);

                if (tileentity.getSprinkled()) {
                    tessellator.draw();
                    tessellator.startDrawingQuads();
                    Minecraft.getMinecraft().getTextureManager().bindTexture(textureSpinkle);

                    tessellator.setNormal(0.0f, 1.0f, 0.0f);
                    tessellator.setColorOpaque_F(f1, f1, f1);
                    tessellator.addVertexWithUV(x + f, y + 0.5d, z + 1.0d - f, f, 1.0d - f);
                    tessellator.addVertexWithUV(x + 1.0d - f, y + 0.5d, z + 1.0d - f, 1.0d - f, 1.0d - f);
                    tessellator.addVertexWithUV(x + 1.0d - f, y + 0.5d, z + f, 1.0d - f, f);
                    tessellator.addVertexWithUV(x + f, y + 0.5d, z + f, f, f);
                }
                break;
            default:

                break;
        }
        tessellator.draw();
    }

}
