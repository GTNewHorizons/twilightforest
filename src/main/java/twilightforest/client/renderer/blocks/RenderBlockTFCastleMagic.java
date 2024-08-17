package twilightforest.client.renderer.blocks;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import twilightforest.block.BlockTFCastleMagic;

public class RenderBlockTFCastleMagic implements ISimpleBlockRenderingHandler {

    private int renderID;

    public RenderBlockTFCastleMagic(int castleMagicBlockRenderID) {
        this.renderID = castleMagicBlockRenderID;
    }

    @Override
    public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer) {
        renderInvBlock(renderer, block, metadata);
    }

    public static void renderInvBlock(RenderBlocks renderblocks, Block block, int meta) {
        Tessellator tessellator = Tessellator.instance;

        GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

        renderInvBlock(renderblocks, block, meta, tessellator);

        GL11.glTranslatef(0.5F, 0.5F, 0.5F);

        block.setBlockBoundsForItemRender();
    }

    protected static void renderInvBlock(RenderBlocks renderblocks, Block block, int meta, Tessellator tessellator) {

        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, -1.0F, 0.0F);
        renderblocks.renderFaceYNeg(block, 0.0D, 0.0D, 0.0D, block.getIcon(0, meta));
        tessellator.setNormal(0.0F, 1.0F, 0.0F);
        renderblocks.renderFaceYPos(block, 0.0D, 0.0D, 0.0D, block.getIcon(1, meta));
        tessellator.setNormal(0.0F, 0.0F, -1.0F);
        renderblocks.renderFaceXPos(block, 0.0D, 0.0D, 0.0D, block.getIcon(2, meta));
        tessellator.setNormal(0.0F, 0.0F, 1.0F);
        renderblocks.renderFaceXNeg(block, 0.0D, 0.0D, 0.0D, block.getIcon(3, meta));
        tessellator.setNormal(-1.0F, 0.0F, 0.0F);
        renderblocks.renderFaceZNeg(block, 0.0D, 0.0D, 0.0D, block.getIcon(4, meta));
        tessellator.setNormal(1.0F, 0.0F, 0.0F);
        renderblocks.renderFaceZPos(block, 0.0D, 0.0D, 0.0D, block.getIcon(5, meta));
        tessellator.draw();

        float pixel = 1F / 16F;

        IIcon icon = BlockTFCastleMagic.getMagicIconFor(meta, 0, 0);

        int color = BlockTFCastleMagic.getMagicColorFor(meta);

        float red = (float) (color >> 16 & 255) / 255.0F;
        float grn = (float) (color >> 8 & 255) / 255.0F;
        float blu = (float) (color & 255) / 255.0F;

        renderblocks.enableAO = false;

        GL11.glDisable(GL11.GL_LIGHTING);

        tessellator.startDrawingQuads();
        tessellator.setBrightness(15 << 20 | 15 << 4); // full brightness
        tessellator.setColorOpaque_F(red, grn, blu);
        renderblocks.renderFaceYNeg(block, 0.0D, -pixel, 0.0D, icon);
        tessellator.setBrightness(15 << 20 | 15 << 4); // full brightness
        tessellator.setColorOpaque_F(red, grn, blu);
        renderblocks.renderFaceYPos(block, 0.0D, +pixel, 0.0D, icon);
        tessellator.setBrightness(15 << 20 | 15 << 4); // full brightness
        tessellator.setColorOpaque_F(red, grn, blu);
        renderblocks.renderFaceXPos(block, +pixel, 0.0D, 0.0D, icon);
        tessellator.setBrightness(15 << 20 | 15 << 4); // full brightness
        tessellator.setColorOpaque_F(red, grn, blu);
        renderblocks.renderFaceXNeg(block, -pixel, 0.0D, 0.0D, icon);
        tessellator.setBrightness(15 << 20 | 15 << 4); // full brightness
        tessellator.setColorOpaque_F(red, grn, blu);
        renderblocks.renderFaceZNeg(block, 0.0D, 0.0D, -pixel, icon);
        tessellator.setBrightness(15 << 20 | 15 << 4); // full brightness
        tessellator.setColorOpaque_F(red, grn, blu);
        renderblocks.renderFaceZPos(block, 0.0D, 0.0D, +pixel, icon);
        tessellator.draw();

        GL11.glEnable(GL11.GL_LIGHTING);

    }

    @Override
    public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId,
            RenderBlocks renderer) {
        renderer.clearOverrideBlockTexture();
        renderer.setRenderBounds(0, 0, 0, 1, 1, 1);
        renderer.renderStandardBlock(block, x, y, z);

        renderer.overrideBlockTexture = BlockTFCastleMagic.getMagicIconFor(x, y, z);

        int meta = world.getBlockMetadata(x, y, z);
        int color = BlockTFCastleMagic.getMagicColorFor(meta);
        float red = (float) (color >> 16 & 255) / 255.0F;
        float grn = (float) (color >> 8 & 255) / 255.0F;
        float blu = (float) (color & 255) / 255.0F;

        // if the block is a door or other clickable block(?), pulse the glyph a little
        // if (block == TFBlocks.castleDoor) {
        // TODO: Add code to do this
        // }

        Tessellator tessellator = Tessellator.instance;
        tessellator.setBrightness(15 << 20 | 15 << 4); // full brightness
        tessellator.setColorOpaque_F(red, grn, blu);

        renderer.enableAO = false;
        float pixel = 1 / 16F;

        if (renderer.renderAllFaces
                || block.shouldSideBeRendered(world, x, y - 1, z, 0) && world.isAirBlock(x, y - 1, z)) {
            renderer.renderFaceYNeg(block, x, (double) y - pixel, z, renderer.overrideBlockTexture);
        }
        if (renderer.renderAllFaces
                || block.shouldSideBeRendered(world, x, y + 1, z, 1) && world.isAirBlock(x, y + 1, z)) {
            renderer.renderFaceYPos(block, x, (double) y + pixel, z, renderer.overrideBlockTexture);
        }
        if (renderer.renderAllFaces
                || block.shouldSideBeRendered(world, x, y, z - 1, 2) && world.isAirBlock(x, y, z - 1)) {
            renderer.renderFaceZNeg(block, x, y, (double) z - pixel, renderer.overrideBlockTexture);
        }
        if (renderer.renderAllFaces
                || block.shouldSideBeRendered(world, x, y, z + 1, 3) && world.isAirBlock(x, y, z + 1)) {
            renderer.renderFaceZPos(block, x, y, (double) z + pixel, renderer.overrideBlockTexture);
        }
        if (renderer.renderAllFaces
                || block.shouldSideBeRendered(world, x - 1, y, z, 4) && world.isAirBlock(x - 1, y, z)) {
            renderer.renderFaceXNeg(block, (double) x - pixel, y, z, renderer.overrideBlockTexture);
        }
        if (renderer.renderAllFaces
                || block.shouldSideBeRendered(world, x + 1, y, z, 5) && world.isAirBlock(x + 1, y, z)) {
            renderer.renderFaceXPos(block, (double) x + pixel, y, z, renderer.overrideBlockTexture);
        }

        renderer.clearOverrideBlockTexture();
        return true;
    }

    @Override
    public boolean shouldRender3DInInventory(int modelId) {
        return true;
    }

    @Override
    public int getRenderId() {
        return this.renderID;
    }

}
