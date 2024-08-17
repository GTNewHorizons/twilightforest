package twilightforest.client.renderer.blocks;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.world.IBlockAccess;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import twilightforest.block.BlockTFSpiralBricks;
import twilightforest.block.BlockTFSpiralBricks.RenderMode;

public class RenderBlockTFSpiralBricks implements ISimpleBlockRenderingHandler {

    final int renderID;

    public RenderBlockTFSpiralBricks(int blockComplexRenderID) {
        this.renderID = blockComplexRenderID;
    }

    @Override
    public void renderInventoryBlock(Block block, int metadata, int modelID, RenderBlocks renderer) {
        renderInvSpiralBricks(renderer, block, metadata);

    }

    @Override
    public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId,
            RenderBlocks renderer) {
        return renderSpiralBricks(renderer, world, x, y, z, block);
    }

    @Override
    public boolean shouldRender3DInInventory(int modelId) {
        return true;
    }

    @Override
    public int getRenderId() {
        return renderID;
    }

    public static boolean renderSpiralBricks(RenderBlocks renderblocks, IBlockAccess world, int x, int y, int z,
            Block block) {
        float pixel = 0.0625F;
        BlockTFSpiralBricks currentBlock = (BlockTFSpiralBricks) block;

        switch (world.getBlockMetadata(x, y, z)) {
            default:
            case 0:
                currentBlock.SetRenderMode(RenderMode.DWX);

                renderblocks.uvRotateTop = 2;
                renderblocks.uvRotateBottom = 0;
                renderblocks.uvRotateEast = 0;
                renderblocks.uvRotateWest = 0;
                renderblocks.uvRotateNorth = 0;
                renderblocks.uvRotateSouth = 0;

                // x1
                renderblocks.setRenderBounds(1.0F - 3 * pixel, 3 * pixel, 0.0F, 1.0F, 1.0F, 0.5F);
                renderblocks.renderStandardBlock(block, x, y, z);
                renderblocks.setRenderBounds(1.0F - 3 * pixel, 0.0F, 0.5F, 1.0F, 1.0F, 1.0F);
                renderblocks.renderStandardBlock(block, x, y, z);

                // x2
                renderblocks.setRenderBounds(1.0F - 6 * pixel, 6 * pixel, pixel / 4, 1.0F - 3 * pixel, 1.0F, 0.5F);
                renderblocks.renderStandardBlock(block, x, y, z);
                renderblocks.setRenderBounds(
                        1.0F - 6 * pixel,
                        3 * pixel,
                        0.5F,
                        1.0F - 3 * pixel,
                        1.0F,
                        1.0F - pixel * 3 / 4);
                renderblocks.renderStandardBlock(block, x, y, z);

                // x3
                renderblocks.setRenderBounds(1.0F - 9 * pixel, 9 * pixel, pixel * 5 / 4, 1.0F - 6 * pixel, 1.0F, 0.5F);
                renderblocks.renderStandardBlock(block, x, y, z);
                renderblocks.setRenderBounds(
                        1.0F - 9 * pixel,
                        6 * pixel,
                        0.5F,
                        1.0F - 6 * pixel,
                        1.0F,
                        1.0F - pixel * 7 / 4);
                renderblocks.renderStandardBlock(block, x, y, z);

                // x4
                renderblocks
                        .setRenderBounds(1.0F - 12 * pixel, 12 * pixel, pixel * 9 / 4, 1.0F - 9 * pixel, 1.0F, 0.5F);
                renderblocks.renderStandardBlock(block, x, y, z);
                renderblocks.setRenderBounds(
                        1.0F - 12 * pixel,
                        9 * pixel,
                        0.5F,
                        1.0F - 9 * pixel,
                        1.0F,
                        1.0F - pixel * 11 / 4);
                renderblocks.renderStandardBlock(block, x, y, z);

                // x5
                renderblocks
                        .setRenderBounds(1.0F - 15 * pixel, 15 * pixel, pixel * 13 / 4, 1.0F - 12 * pixel, 1.0F, 0.5F);
                renderblocks.renderStandardBlock(block, x, y, z);
                renderblocks.setRenderBounds(
                        1.0F - 15 * pixel,
                        12 * pixel,
                        0.5F,
                        1.0F - 12 * pixel,
                        1.0F,
                        1.0F - pixel * 15 / 4);
                renderblocks.renderStandardBlock(block, x, y, z);

                currentBlock.SetRenderMode(RenderMode.DWY);

                renderblocks.uvRotateTop = 0;
                renderblocks.uvRotateBottom = 0;
                renderblocks.uvRotateEast = 0;
                renderblocks.uvRotateWest = 0;
                renderblocks.uvRotateNorth = 0;
                renderblocks.uvRotateSouth = 0;

                // y1
                renderblocks.setRenderBounds(0.0F, 0.0F, 0.0F, 1.0F, 3 * pixel, 0.5F);
                renderblocks.renderStandardBlock(block, x, y, z);
                renderblocks.setRenderBounds(0.0F, 0.0F, 0.5F, 1.0F - 3 * pixel, 3 * pixel, 1.0F);
                renderblocks.renderStandardBlock(block, x, y, z);

                // y2
                renderblocks.setRenderBounds(0.0F, 3 * pixel, pixel / 2, 1.0F - 3 * pixel, 6 * pixel, 0.5F);
                renderblocks.renderStandardBlock(block, x, y, z);
                renderblocks.setRenderBounds(0.0F, 3 * pixel, 0.5F, 1.0F - 6 * pixel, 6 * pixel, 1.0F - pixel / 2);
                renderblocks.renderStandardBlock(block, x, y, z);

                // y3
                renderblocks.setRenderBounds(0.0F, 6 * pixel, pixel * 3 / 2, 1.0F - 6 * pixel, 9 * pixel, 0.5F);
                renderblocks.renderStandardBlock(block, x, y, z);
                renderblocks.setRenderBounds(0.0F, 6 * pixel, 0.5F, 1.0F - 9 * pixel, 9 * pixel, 1.0F - pixel * 3 / 2);
                renderblocks.renderStandardBlock(block, x, y, z);

                // y4
                renderblocks.setRenderBounds(0.0F, 9 * pixel, pixel * 5 / 2, 1.0F - 9 * pixel, 12 * pixel, 0.5F);
                renderblocks.renderStandardBlock(block, x, y, z);
                renderblocks
                        .setRenderBounds(0.0F, 9 * pixel, 0.5F, 1.0F - 12 * pixel, 12 * pixel, 1.0F - pixel * 5 / 2);
                renderblocks.renderStandardBlock(block, x, y, z);

                // y5
                renderblocks.setRenderBounds(0.0F, 12 * pixel, pixel * 7 / 2, 1.0F - 12 * pixel, 15 * pixel, 0.5F);
                renderblocks.renderStandardBlock(block, x, y, z);
                renderblocks
                        .setRenderBounds(0.0F, 12 * pixel, 0.5F, 1.0F - 15 * pixel, 15 * pixel, 1.0F - pixel * 7 / 2);
                renderblocks.renderStandardBlock(block, x, y, z);

                currentBlock.SetRenderMode(RenderMode.DWXY);

                renderblocks.uvRotateTop = 0;
                renderblocks.uvRotateBottom = 0;
                renderblocks.uvRotateEast = 0;
                renderblocks.uvRotateWest = 0;
                renderblocks.uvRotateNorth = 0;
                renderblocks.uvRotateSouth = 0;

                // xy
                renderblocks.setRenderBounds(0.0F, 15 * pixel, pixel * 17 / 4, pixel, 1.0F, 1.0F - pixel * 19 / 4);
                renderblocks.renderStandardBlock(block, x, y, z);
                break;
            case 1:
                currentBlock.SetRenderMode(RenderMode.DEX);

                renderblocks.uvRotateTop = 1;
                renderblocks.uvRotateBottom = 0;
                renderblocks.uvRotateEast = 0;
                renderblocks.uvRotateWest = 0;
                renderblocks.uvRotateNorth = 0;
                renderblocks.uvRotateSouth = 0;

                // x1
                renderblocks.setRenderBounds(0.0F, 0.0F, 0.0F, 3 * pixel, 1.0F, 0.5F);
                renderblocks.renderStandardBlock(block, x, y, z);
                renderblocks.setRenderBounds(0.0F, 3 * pixel, 0.5F, 3 * pixel, 1.0F, 1.0F);
                renderblocks.renderStandardBlock(block, x, y, z);

                // x2
                renderblocks.setRenderBounds(3 * pixel, 3 * pixel, pixel * 3 / 4, 6 * pixel, 1.0F, 0.5F);
                renderblocks.renderStandardBlock(block, x, y, z);
                renderblocks.setRenderBounds(3 * pixel, 6 * pixel, 0.5F, 6 * pixel, 1.0F, 1.0F - pixel / 4);
                renderblocks.renderStandardBlock(block, x, y, z);

                // x3
                renderblocks.setRenderBounds(6 * pixel, 6 * pixel, pixel * 7 / 4, 9 * pixel, 1.0F, 0.5F);
                renderblocks.renderStandardBlock(block, x, y, z);
                renderblocks.setRenderBounds(6 * pixel, 9 * pixel, 0.5F, 9 * pixel, 1.0F, 1.0F - pixel * 5 / 4);
                renderblocks.renderStandardBlock(block, x, y, z);

                // x4
                renderblocks.setRenderBounds(9 * pixel, 9 * pixel, pixel * 11 / 4, 12 * pixel, 1.0F, 0.5F);
                renderblocks.renderStandardBlock(block, x, y, z);
                renderblocks.setRenderBounds(9 * pixel, 12 * pixel, 0.5F, 12 * pixel, 1.0F, 1.0F - pixel * 9 / 4);
                renderblocks.renderStandardBlock(block, x, y, z);

                // x5
                renderblocks.setRenderBounds(12 * pixel, 12 * pixel, pixel * 15 / 4, 15 * pixel, 1.0F, 0.5F);
                renderblocks.renderStandardBlock(block, x, y, z);
                renderblocks.setRenderBounds(12 * pixel, 15 * pixel, 0.5F, 15 * pixel, 1.0F, 1.0F - pixel * 13 / 4);
                renderblocks.renderStandardBlock(block, x, y, z);

                currentBlock.SetRenderMode(RenderMode.DEY);

                renderblocks.uvRotateTop = 0;
                renderblocks.uvRotateBottom = 0;
                renderblocks.uvRotateEast = 0;
                renderblocks.uvRotateWest = 0;
                renderblocks.uvRotateNorth = 0;
                renderblocks.uvRotateSouth = 0;

                // y1
                renderblocks.setRenderBounds(3 * pixel, 0.0F, 0.0F, 1.0F, 3 * pixel, 0.5F);
                renderblocks.renderStandardBlock(block, x, y, z);
                renderblocks.setRenderBounds(0.0F, 0.0F, 0.5F, 1.0F, 3 * pixel, 1.0F);
                renderblocks.renderStandardBlock(block, x, y, z);

                // y2
                renderblocks.setRenderBounds(6 * pixel, 3 * pixel, pixel / 2, 1.0F, 6 * pixel, 0.5F);
                renderblocks.renderStandardBlock(block, x, y, z);
                renderblocks.setRenderBounds(3 * pixel, 3 * pixel, 0.5F, 1.0F, 6 * pixel, 1.0F - pixel / 2);
                renderblocks.renderStandardBlock(block, x, y, z);

                // y3
                renderblocks.setRenderBounds(9 * pixel, 6 * pixel, pixel * 3 / 2, 1.0F, 9 * pixel, 0.5F);
                renderblocks.renderStandardBlock(block, x, y, z);
                renderblocks.setRenderBounds(6 * pixel, 6 * pixel, 0.5F, 1.0F, 9 * pixel, 1.0F - pixel * 3 / 2);
                renderblocks.renderStandardBlock(block, x, y, z);

                // y4
                renderblocks.setRenderBounds(12 * pixel, 9 * pixel, pixel * 5 / 2, 1.0F, 12 * pixel, 0.5F);
                renderblocks.renderStandardBlock(block, x, y, z);
                renderblocks.setRenderBounds(9 * pixel, 9 * pixel, 0.5F, 1.0F, 12 * pixel, 1.0F - pixel * 5 / 2);
                renderblocks.renderStandardBlock(block, x, y, z);

                // y5
                renderblocks.setRenderBounds(15 * pixel, 12 * pixel, pixel * 7 / 2, 1.0F, 15 * pixel, 0.5F);
                renderblocks.renderStandardBlock(block, x, y, z);
                renderblocks.setRenderBounds(12 * pixel, 12 * pixel, 0.5F, 1.0F, 15 * pixel, 1.0F - pixel * 7 / 2);
                renderblocks.renderStandardBlock(block, x, y, z);

                currentBlock.SetRenderMode(RenderMode.DEXY);

                renderblocks.uvRotateTop = 0;
                renderblocks.uvRotateBottom = 0;
                renderblocks.uvRotateEast = 0;
                renderblocks.uvRotateWest = 0;
                renderblocks.uvRotateNorth = 0;
                renderblocks.uvRotateSouth = 0;

                // xy
                renderblocks.setRenderBounds(15 * pixel, 15 * pixel, pixel * 17 / 4, 1.0F, 1.0F, 1.0F - pixel * 19 / 4);
                renderblocks.renderStandardBlock(block, x, y, z);
                break;
            case 2:
                currentBlock.SetRenderMode(RenderMode.DNZ);

                renderblocks.uvRotateTop = 0;
                renderblocks.uvRotateBottom = 0;
                renderblocks.uvRotateEast = 0;
                renderblocks.uvRotateWest = 0;
                renderblocks.uvRotateNorth = 0;
                renderblocks.uvRotateSouth = 0;

                // z1
                renderblocks.setRenderBounds(0.0F, 3 * pixel, 1.0F - 3 * pixel, 0.5F, 1.0F, 1.0F);
                renderblocks.renderStandardBlock(block, x, y, z);
                renderblocks.setRenderBounds(0.5F, 3 * pixel, 1.0F - 3 * pixel, 1.0F, 1.0F, 1.0F);
                renderblocks.renderStandardBlock(block, x, y, z);

                // z2
                renderblocks.setRenderBounds(pixel * 3 / 4, 3 * pixel, 1.0F - 6 * pixel, 0.5F, 1.0F, 1.0F - 3 * pixel);
                renderblocks.renderStandardBlock(block, x, y, z);
                renderblocks
                        .setRenderBounds(0.5F, 6 * pixel, 1.0F - 6 * pixel, 1.0F - pixel / 4, 1.0F, 1.0F - 3 * pixel);
                renderblocks.renderStandardBlock(block, x, y, z);

                // z3
                renderblocks.setRenderBounds(pixel * 7 / 4, 6 * pixel, 1.0F - 9 * pixel, 0.5F, 1.0F, 1.0F - 6 * pixel);
                renderblocks.renderStandardBlock(block, x, y, z);
                renderblocks.setRenderBounds(
                        0.5F,
                        9 * pixel,
                        1.0F - 9 * pixel,
                        1.0F - pixel * 5 / 4,
                        1.0F,
                        1.0F - 6 * pixel);
                renderblocks.renderStandardBlock(block, x, y, z);

                // z4
                renderblocks
                        .setRenderBounds(pixel * 11 / 4, 9 * pixel, 1.0F - 12 * pixel, 0.5F, 1.0F, 1.0F - 9 * pixel);
                renderblocks.renderStandardBlock(block, x, y, z);
                renderblocks.setRenderBounds(
                        0.5F,
                        12 * pixel,
                        1.0F - 12 * pixel,
                        1.0F - pixel * 9 / 4,
                        1.0F,
                        1.0F - 9 * pixel);
                renderblocks.renderStandardBlock(block, x, y, z);

                // z5
                renderblocks
                        .setRenderBounds(pixel * 15 / 4, 12 * pixel, 1.0F - 15 * pixel, 0.5F, 1.0F, 1.0F - 12 * pixel);
                renderblocks.renderStandardBlock(block, x, y, z);
                renderblocks.setRenderBounds(
                        0.5F,
                        15 * pixel,
                        1.0F - 15 * pixel,
                        1.0F - pixel * 13 / 4,
                        1.0F,
                        1.0F - 12 * pixel);
                renderblocks.renderStandardBlock(block, x, y, z);

                currentBlock.SetRenderMode(RenderMode.DNY);

                renderblocks.uvRotateTop = 1;
                renderblocks.uvRotateBottom = 0;
                renderblocks.uvRotateEast = 0;
                renderblocks.uvRotateWest = 0;
                renderblocks.uvRotateNorth = 0;
                renderblocks.uvRotateSouth = 0;

                // y1
                renderblocks.setRenderBounds(0.0F, 0.0F, 0.0F, 0.5F, 3 * pixel, 1.0F);
                renderblocks.renderStandardBlock(block, x, y, z);
                renderblocks.setRenderBounds(0.5F, 0.0F, 0.0F, 1.0F, 3 * pixel, 1.0F);
                renderblocks.renderStandardBlock(block, x, y, z);

                // y2
                renderblocks.setRenderBounds(pixel / 2, 3 * pixel, 0.0F, 0.5F, 6 * pixel, 1.0F - 6 * pixel);
                renderblocks.renderStandardBlock(block, x, y, z);
                renderblocks.setRenderBounds(0.5F, 3 * pixel, 0.0F, 1.0F - pixel / 2, 6 * pixel, 1.0F - 3 * pixel);
                renderblocks.renderStandardBlock(block, x, y, z);

                // y3
                renderblocks.setRenderBounds(pixel * 3 / 2, 6 * pixel, 0.0F, 0.5F, 9 * pixel, 1.0F - 9 * pixel);
                renderblocks.renderStandardBlock(block, x, y, z);
                renderblocks.setRenderBounds(0.5F, 6 * pixel, 0.0F, 1.0F - pixel * 3 / 2, 9 * pixel, 1.0F - 6 * pixel);
                renderblocks.renderStandardBlock(block, x, y, z);

                // y4
                renderblocks.setRenderBounds(pixel * 5 / 2, 9 * pixel, 0.0F, 0.5F, 12 * pixel, 1.0F - 12 * pixel);
                renderblocks.renderStandardBlock(block, x, y, z);
                renderblocks.setRenderBounds(0.5F, 9 * pixel, 0.0F, 1.0F - pixel * 5 / 2, 12 * pixel, 1.0F - 9 * pixel);
                renderblocks.renderStandardBlock(block, x, y, z);

                // y5
                renderblocks.setRenderBounds(pixel * 7 / 2, 12 * pixel, 0.0F, 0.5F, 15 * pixel, 1.0F - 15 * pixel);
                renderblocks.renderStandardBlock(block, x, y, z);
                renderblocks
                        .setRenderBounds(0.5F, 12 * pixel, 0.0F, 1.0F - pixel * 7 / 2, 15 * pixel, 1.0F - 12 * pixel);
                renderblocks.renderStandardBlock(block, x, y, z);

                currentBlock.SetRenderMode(RenderMode.DNZY);

                renderblocks.uvRotateTop = 0;
                renderblocks.uvRotateBottom = 0;
                renderblocks.uvRotateEast = 0;
                renderblocks.uvRotateWest = 0;
                renderblocks.uvRotateNorth = 0;
                renderblocks.uvRotateSouth = 0;

                // zy
                renderblocks.setRenderBounds(pixel * 19 / 4, 15 * pixel, 0.0F, 1.0F - pixel * 17 / 4, 1.0F, pixel);
                renderblocks.renderStandardBlock(block, x, y, z);
                break;
            case 3:
                currentBlock.SetRenderMode(RenderMode.DSZ);

                renderblocks.uvRotateTop = 3;
                renderblocks.uvRotateBottom = 0;
                renderblocks.uvRotateEast = 0;
                renderblocks.uvRotateWest = 0;
                renderblocks.uvRotateNorth = 0;
                renderblocks.uvRotateSouth = 0;

                // z1
                renderblocks.setRenderBounds(0.0F, 3 * pixel, 0.0F, 0.5F, 1.0F, 3 * pixel);
                renderblocks.renderStandardBlock(block, x, y, z);
                renderblocks.setRenderBounds(0.5F, 0.0F, 0.0F, 1.0F, 1.0F, 3 * pixel);
                renderblocks.renderStandardBlock(block, x, y, z);

                // z2
                renderblocks.setRenderBounds(pixel / 4, 6 * pixel, 3 * pixel, 0.5F, 1.0F, 6 * pixel);
                renderblocks.renderStandardBlock(block, x, y, z);
                renderblocks.setRenderBounds(0.5F, 3 * pixel, 3 * pixel, 1.0F - pixel * 3 / 4, 1.0F, 6 * pixel);
                renderblocks.renderStandardBlock(block, x, y, z);

                // z3
                renderblocks.setRenderBounds(pixel * 5 / 4, 9 * pixel, 6 * pixel, 0.5F, 1.0F, 9 * pixel);
                renderblocks.renderStandardBlock(block, x, y, z);
                renderblocks.setRenderBounds(0.5F, 6 * pixel, 6 * pixel, 1.0F - pixel * 7 / 4, 1.0F, 9 * pixel);
                renderblocks.renderStandardBlock(block, x, y, z);

                // z4
                renderblocks.setRenderBounds(pixel * 9 / 4, 12 * pixel, 9 * pixel, 0.5F, 1.0F, 12 * pixel);
                renderblocks.renderStandardBlock(block, x, y, z);
                renderblocks.setRenderBounds(0.5F, 9 * pixel, 9 * pixel, 1.0F - pixel * 11 / 4, 1.0F, 12 * pixel);
                renderblocks.renderStandardBlock(block, x, y, z);

                // z5
                renderblocks.setRenderBounds(pixel * 13 / 4, 15 * pixel, 12 * pixel, 0.5F, 1.0F, 15 * pixel);
                renderblocks.renderStandardBlock(block, x, y, z);
                renderblocks.setRenderBounds(0.5F, 12 * pixel, 12 * pixel, 1.0F - pixel * 15 / 4, 1.0F, 15 * pixel);
                renderblocks.renderStandardBlock(block, x, y, z);

                currentBlock.SetRenderMode(RenderMode.DSY);

                renderblocks.uvRotateTop = 0;
                renderblocks.uvRotateBottom = 0;
                renderblocks.uvRotateEast = 0;
                renderblocks.uvRotateWest = 0;
                renderblocks.uvRotateNorth = 0;
                renderblocks.uvRotateSouth = 0;

                // y1
                renderblocks.setRenderBounds(0.0F, 0.0F, 0.0F, 0.5F, 3 * pixel, 1.0F);
                renderblocks.renderStandardBlock(block, x, y, z);
                renderblocks.setRenderBounds(0.5F, 0.0F, 3 * pixel, 1.0F, 3 * pixel, 1.0F);
                renderblocks.renderStandardBlock(block, x, y, z);

                // y2
                renderblocks.setRenderBounds(pixel / 2, 3 * pixel, 3 * pixel, 0.5F, 6 * pixel, 1.0F);
                renderblocks.renderStandardBlock(block, x, y, z);
                renderblocks.setRenderBounds(0.5F, 3 * pixel, 6 * pixel, 1.0F - pixel / 2, 6 * pixel, 1.0F);
                renderblocks.renderStandardBlock(block, x, y, z);

                // y3
                renderblocks.setRenderBounds(pixel * 3 / 2, 6 * pixel, 6 * pixel, 0.5F, 9 * pixel, 1.0F);
                renderblocks.renderStandardBlock(block, x, y, z);
                renderblocks.setRenderBounds(0.5F, 6 * pixel, 9 * pixel, 1.0F - pixel * 3 / 2, 9 * pixel, 1.0F);
                renderblocks.renderStandardBlock(block, x, y, z);

                // y4
                renderblocks.setRenderBounds(pixel * 5 / 2, 9 * pixel, 9 * pixel, 0.5F, 12 * pixel, 1.0F);
                renderblocks.renderStandardBlock(block, x, y, z);
                renderblocks.setRenderBounds(0.5F, 9 * pixel, 12 * pixel, 1.0F - pixel * 5 / 2, 12 * pixel, 1.0F);
                renderblocks.renderStandardBlock(block, x, y, z);

                // y5
                renderblocks.setRenderBounds(pixel * 7 / 2, 12 * pixel, 12 * pixel, 0.5F, 15 * pixel, 1.0F);
                renderblocks.renderStandardBlock(block, x, y, z);
                renderblocks.setRenderBounds(0.5F, 12 * pixel, 15 * pixel, 1.0F - pixel * 7 / 2, 15 * pixel, 1.0F);
                renderblocks.renderStandardBlock(block, x, y, z);

                currentBlock.SetRenderMode(RenderMode.DSZY);

                renderblocks.uvRotateTop = 0;
                renderblocks.uvRotateBottom = 0;
                renderblocks.uvRotateEast = 0;
                renderblocks.uvRotateWest = 0;
                renderblocks.uvRotateNorth = 0;
                renderblocks.uvRotateSouth = 0;

                // zy
                renderblocks.setRenderBounds(pixel * 19 / 4, 15 * pixel, 15 * pixel, 1.0F - pixel * 17 / 4, 1.0F, 1.0F);
                renderblocks.renderStandardBlock(block, x, y, z);
                break;
            case 4:
                currentBlock.SetRenderMode(RenderMode.UWX);

                renderblocks.uvRotateTop = 0;
                renderblocks.uvRotateBottom = 1;
                renderblocks.uvRotateEast = 0;
                renderblocks.uvRotateWest = 0;
                renderblocks.uvRotateNorth = 0;
                renderblocks.uvRotateSouth = 0;

                // x1
                renderblocks.setRenderBounds(1.0F - 3 * pixel, 0.0F, 0.0F, 1.0F, 1.0F, 0.5F);
                renderblocks.renderStandardBlock(block, x, y, z);
                renderblocks.setRenderBounds(1.0F - 3 * pixel, 0.0F, 0.5F, 1.0F, 1.0F - 3 * pixel, 1.0F);
                renderblocks.renderStandardBlock(block, x, y, z);

                // x2
                renderblocks.setRenderBounds(
                        1.0F - 6 * pixel,
                        0.0F,
                        pixel * 1 / 4,
                        1.0F - 3 * pixel,
                        1.0F - 3 * pixel,
                        0.5F);
                renderblocks.renderStandardBlock(block, x, y, z);
                renderblocks.setRenderBounds(
                        1.0F - 6 * pixel,
                        0.0F,
                        0.5F,
                        1.0F - 3 * pixel,
                        1.0F - 6 * pixel,
                        1.0F - pixel * 3 / 4);
                renderblocks.renderStandardBlock(block, x, y, z);

                // x3
                renderblocks.setRenderBounds(
                        1.0F - 9 * pixel,
                        0.0F,
                        pixel * 5 / 4,
                        1.0F - 6 * pixel,
                        1.0F - 3 * pixel,
                        0.5F);
                renderblocks.renderStandardBlock(block, x, y, z);
                renderblocks.setRenderBounds(
                        1.0F - 9 * pixel,
                        0.0F,
                        0.5F,
                        1.0F - 6 * pixel,
                        1.0F - 9 * pixel,
                        1.0F - pixel * 7 / 4);
                renderblocks.renderStandardBlock(block, x, y, z);

                // x4
                renderblocks.setRenderBounds(
                        1.0F - 12 * pixel,
                        0.0F,
                        pixel * 9 / 4,
                        1.0F - 9 * pixel,
                        1.0F - 6 * pixel,
                        0.5F);
                renderblocks.renderStandardBlock(block, x, y, z);
                renderblocks.setRenderBounds(
                        1.0F - 12 * pixel,
                        0.0F,
                        0.5F,
                        1.0F - 9 * pixel,
                        1.0F - 12 * pixel,
                        1.0F - pixel * 11 / 4);
                renderblocks.renderStandardBlock(block, x, y, z);

                // x5
                renderblocks.setRenderBounds(
                        1.0F - 15 * pixel,
                        0.0F,
                        pixel * 13 / 4,
                        1.0F - 12 * pixel,
                        1.0F - 9 * pixel,
                        0.5F);
                renderblocks.renderStandardBlock(block, x, y, z);
                renderblocks.setRenderBounds(
                        1.0F - 15 * pixel,
                        0.0F,
                        0.5F,
                        1.0F - 12 * pixel,
                        1.0F - 15 * pixel,
                        1.0F - pixel * 15 / 4);
                renderblocks.renderStandardBlock(block, x, y, z);

                currentBlock.SetRenderMode(RenderMode.UWY);

                renderblocks.uvRotateTop = 0;
                renderblocks.uvRotateBottom = 0;
                renderblocks.uvRotateEast = 0;
                renderblocks.uvRotateWest = 0;
                renderblocks.uvRotateNorth = 0;
                renderblocks.uvRotateSouth = 0;

                // y1
                renderblocks.setRenderBounds(0.0F, 1.0F - 3 * pixel, 0.0F, 1.0F - 3 * pixel, 1.0F, 0.5F);
                renderblocks.renderStandardBlock(block, x, y, z);
                renderblocks.setRenderBounds(0.0F, 1.0F - 3 * pixel, 0.5F, 1.0F, 1.0F, 1.0F);
                renderblocks.renderStandardBlock(block, x, y, z);

                // y2
                renderblocks.setRenderBounds(0.0F, 1.0F - 6 * pixel, pixel, 1.0F - 9 * pixel, 1.0F, 0.5F);
                renderblocks.renderStandardBlock(block, x, y, z);
                renderblocks.setRenderBounds(0.0F, 1.0F - 6 * pixel, 0.5F, 1.0F - 3 * pixel, 1.0F, 1.0F - pixel);
                renderblocks.renderStandardBlock(block, x, y, z);

                // y3
                renderblocks.setRenderBounds(0.0F, 1.0F - 9 * pixel, pixel * 2, 1.0F - 12 * pixel, 1.0F, 0.5F);
                renderblocks.renderStandardBlock(block, x, y, z);
                renderblocks.setRenderBounds(0.0F, 1.0F - 9 * pixel, 0.5F, 1.0F - 6 * pixel, 1.0F, 1.0F - pixel * 2);
                renderblocks.renderStandardBlock(block, x, y, z);

                // y4
                renderblocks.setRenderBounds(0.0F, 1.0F - 12 * pixel, pixel * 3, 1.0F - 15 * pixel, 1.0F, 0.5F);
                renderblocks.renderStandardBlock(block, x, y, z);
                renderblocks.setRenderBounds(0.0F, 1.0F - 12 * pixel, 0.5F, 1.0F - 9 * pixel, 1.0F, 1.0F - pixel * 3);
                renderblocks.renderStandardBlock(block, x, y, z);

                // y5
                renderblocks.setRenderBounds(0.0F, 1.0F - 15 * pixel, pixel * 4, 1.0F - 15 * pixel, 1.0F, 0.5F);
                renderblocks.renderStandardBlock(block, x, y, z);
                renderblocks.setRenderBounds(0.0F, 1.0F - 15 * pixel, 0.5F, 1.0F - 12 * pixel, 1.0F, 1.0F - pixel * 4);
                renderblocks.renderStandardBlock(block, x, y, z);

                currentBlock.SetRenderMode(RenderMode.UWXY);

                renderblocks.uvRotateTop = 0;
                renderblocks.uvRotateBottom = 0;
                renderblocks.uvRotateEast = 0;
                renderblocks.uvRotateWest = 0;
                renderblocks.uvRotateNorth = 0;
                renderblocks.uvRotateSouth = 0;

                // xy
                renderblocks.setRenderBounds(0.0F, 0.0F, pixel * 17 / 4, pixel, pixel, 1.0F - pixel * 19 / 4);
                renderblocks.renderStandardBlock(block, x, y, z);
                break;
            case 5:
                currentBlock.SetRenderMode(RenderMode.UEX);

                renderblocks.uvRotateTop = 0;
                renderblocks.uvRotateBottom = 2;
                renderblocks.uvRotateEast = 0;
                renderblocks.uvRotateWest = 0;
                renderblocks.uvRotateNorth = 0;
                renderblocks.uvRotateSouth = 0;

                // x1
                renderblocks.setRenderBounds(0.0F, 0.0F, 0.0F, 3 * pixel, 1.0F - 3 * pixel, 0.5F);
                renderblocks.renderStandardBlock(block, x, y, z);
                renderblocks.setRenderBounds(0.0F, 0.0F, 0.5F, 3 * pixel, 1.0F, 1.0F);
                renderblocks.renderStandardBlock(block, x, y, z);

                // x2
                renderblocks.setRenderBounds(3 * pixel, 0.0F, pixel * 3 / 4, 6 * pixel, 1.0F - 6 * pixel, 0.5F);
                renderblocks.renderStandardBlock(block, x, y, z);
                renderblocks.setRenderBounds(3 * pixel, 0.0F, 0.5F, 6 * pixel, 1.0F - 3 * pixel, 1.0F - pixel / 4);
                renderblocks.renderStandardBlock(block, x, y, z);

                // x3
                renderblocks.setRenderBounds(6 * pixel, 0.0F, pixel * 7 / 4, 9 * pixel, 1.0F - 9 * pixel, 0.5F);
                renderblocks.renderStandardBlock(block, x, y, z);
                renderblocks.setRenderBounds(6 * pixel, 0.0F, 0.5F, 9 * pixel, 1.0F - 3 * pixel, 1.0F - pixel * 5 / 4);
                renderblocks.renderStandardBlock(block, x, y, z);

                // x4
                renderblocks.setRenderBounds(9 * pixel, 0.0F, pixel * 11 / 4, 12 * pixel, 1.0F - 12 * pixel, 0.5F);
                renderblocks.renderStandardBlock(block, x, y, z);
                renderblocks.setRenderBounds(9 * pixel, 0.0F, 0.5F, 12 * pixel, 1.0F - 6 * pixel, 1.0F - pixel * 9 / 4);
                renderblocks.renderStandardBlock(block, x, y, z);

                // x5
                renderblocks.setRenderBounds(12 * pixel, 0.0F, pixel * 15 / 4, 15 * pixel, 1.0F - 15 * pixel, 0.5F);
                renderblocks.renderStandardBlock(block, x, y, z);
                renderblocks
                        .setRenderBounds(12 * pixel, 0.0F, 0.5F, 15 * pixel, 1.0F - 9 * pixel, 1.0F - pixel * 13 / 4);
                renderblocks.renderStandardBlock(block, x, y, z);

                currentBlock.SetRenderMode(RenderMode.UEY);

                renderblocks.uvRotateTop = 0;
                renderblocks.uvRotateBottom = 0;
                renderblocks.uvRotateEast = 0;
                renderblocks.uvRotateWest = 0;
                renderblocks.uvRotateNorth = 0;
                renderblocks.uvRotateSouth = 0;

                // y1
                renderblocks.setRenderBounds(0.0F, 1.0F - 3 * pixel, 0.0F, 1.0F, 1.0F, 0.5F);
                renderblocks.renderStandardBlock(block, x, y, z);
                renderblocks.setRenderBounds(3 * pixel, 1.0F - 3 * pixel, 0.5F, 1.0F, 1.0F, 1.0F);
                renderblocks.renderStandardBlock(block, x, y, z);

                // y2
                renderblocks.setRenderBounds(3 * pixel, 1.0F - 6 * pixel, pixel, 1.0F, 1.0F - 3 * pixel, 0.5F);
                renderblocks.renderStandardBlock(block, x, y, z);
                renderblocks.setRenderBounds(9 * pixel, 1.0F - 6 * pixel, 0.5F, 1.0F, 1.0F - 3 * pixel, 1.0F - pixel);
                renderblocks.renderStandardBlock(block, x, y, z);

                // y3
                renderblocks.setRenderBounds(6 * pixel, 1.0F - 9 * pixel, pixel * 2, 1.0F, 1.0F - 6 * pixel, 0.5F);
                renderblocks.renderStandardBlock(block, x, y, z);
                renderblocks
                        .setRenderBounds(12 * pixel, 1.0F - 9 * pixel, 0.5F, 1.0F, 1.0F - 6 * pixel, 1.0F - pixel * 2);
                renderblocks.renderStandardBlock(block, x, y, z);

                // y4
                renderblocks.setRenderBounds(9 * pixel, 1.0F - 12 * pixel, pixel * 3, 1.0F, 1.0F - 9 * pixel, 0.5F);
                renderblocks.renderStandardBlock(block, x, y, z);
                renderblocks
                        .setRenderBounds(15 * pixel, 1.0F - 12 * pixel, 0.5F, 1.0F, 1.0F - 9 * pixel, 1.0F - pixel * 3);
                renderblocks.renderStandardBlock(block, x, y, z);

                // y5
                renderblocks.setRenderBounds(12 * pixel, 1.0F - 15 * pixel, pixel * 4, 1.0F, 1.0F - 12 * pixel, 0.5F);
                renderblocks.renderStandardBlock(block, x, y, z);
                renderblocks.setRenderBounds(
                        15 * pixel,
                        1.0F - 15 * pixel,
                        0.5F,
                        1.0F,
                        1.0F - 12 * pixel,
                        1.0F - pixel * 4);
                renderblocks.renderStandardBlock(block, x, y, z);

                currentBlock.SetRenderMode(RenderMode.UEXY);

                renderblocks.uvRotateTop = 0;
                renderblocks.uvRotateBottom = 0;
                renderblocks.uvRotateEast = 0;
                renderblocks.uvRotateWest = 0;
                renderblocks.uvRotateNorth = 0;
                renderblocks.uvRotateSouth = 0;

                // xy
                renderblocks.setRenderBounds(15 * pixel, 0.0F, pixel * 17 / 4, 1.0F, pixel, 1.0F - pixel * 19 / 4);
                renderblocks.renderStandardBlock(block, x, y, z);
                break;
            case 6:
                currentBlock.SetRenderMode(RenderMode.UNZ);

                renderblocks.uvRotateTop = 0;
                renderblocks.uvRotateBottom = 0;
                renderblocks.uvRotateEast = 0;
                renderblocks.uvRotateWest = 0;
                renderblocks.uvRotateNorth = 0;
                renderblocks.uvRotateSouth = 0;

                // z1
                renderblocks.setRenderBounds(0.0F, 0.0F, 1.0F - 3 * pixel, 0.5F, 1.0F, 1.0F);
                renderblocks.renderStandardBlock(block, x, y, z);
                renderblocks.setRenderBounds(0.5F, 0.0F, 1.0F - 3 * pixel, 1.0F, 1.0F, 1.0F);
                renderblocks.renderStandardBlock(block, x, y, z);

                // z2
                renderblocks.setRenderBounds(
                        pixel * 3 / 4,
                        0.0F,
                        1.0F - 6 * pixel,
                        0.5F,
                        1.0F - 6 * pixel,
                        1.0F - 3 * pixel);
                renderblocks.renderStandardBlock(block, x, y, z);
                renderblocks.setRenderBounds(
                        0.5F,
                        0.0F,
                        1.0F - 6 * pixel,
                        1.0F - pixel * 1 / 4,
                        1.0F - 3 * pixel,
                        1.0F - 3 * pixel);
                renderblocks.renderStandardBlock(block, x, y, z);

                // z3
                renderblocks.setRenderBounds(
                        pixel * 7 / 4,
                        0.0F,
                        1.0F - 9 * pixel,
                        0.5F,
                        1.0F - 9 * pixel,
                        1.0F - 6 * pixel);
                renderblocks.renderStandardBlock(block, x, y, z);
                renderblocks.setRenderBounds(
                        0.5F,
                        0.0F,
                        1.0F - 9 * pixel,
                        1.0F - pixel * 5 / 4,
                        1.0F - 3 * pixel,
                        1.0F - 6 * pixel);
                renderblocks.renderStandardBlock(block, x, y, z);

                // z4
                renderblocks.setRenderBounds(
                        pixel * 11 / 4,
                        0.0F,
                        1.0F - 12 * pixel,
                        0.5F,
                        1.0F - 12 * pixel,
                        1.0F - 9 * pixel);
                renderblocks.renderStandardBlock(block, x, y, z);
                renderblocks.setRenderBounds(
                        0.5F,
                        0.0F,
                        1.0F - 12 * pixel,
                        1.0F - pixel * 9 / 4,
                        1.0F - 6 * pixel,
                        1.0F - 9 * pixel);
                renderblocks.renderStandardBlock(block, x, y, z);

                // z5
                renderblocks.setRenderBounds(
                        pixel * 15 / 4,
                        0.0F,
                        1.0F - 15 * pixel,
                        0.5F,
                        1.0F - 15 * pixel,
                        1.0F - 12 * pixel);
                renderblocks.renderStandardBlock(block, x, y, z);
                renderblocks.setRenderBounds(
                        0.5F,
                        0.0F,
                        1.0F - 15 * pixel,
                        1.0F - pixel * 13 / 4,
                        1.0F - 9 * pixel,
                        1.0F - 12 * pixel);
                renderblocks.renderStandardBlock(block, x, y, z);

                currentBlock.SetRenderMode(RenderMode.UNY);

                renderblocks.uvRotateTop = 0;
                renderblocks.uvRotateBottom = 0;
                renderblocks.uvRotateEast = 0;
                renderblocks.uvRotateWest = 0;
                renderblocks.uvRotateNorth = 0;
                renderblocks.uvRotateSouth = 0;

                // y1
                renderblocks.setRenderBounds(0.0F, 1.0F - 3 * pixel, 0.0F, 0.5F, 1.0F, 1.0F - 3 * pixel);
                renderblocks.renderStandardBlock(block, x, y, z);
                renderblocks.setRenderBounds(0.5F, 1.0F - 3 * pixel, 0.0F, 1.0F, 1.0F, 1.0F - 3 * pixel);
                renderblocks.renderStandardBlock(block, x, y, z);

                // y2
                renderblocks.setRenderBounds(pixel, 1.0F - 6 * pixel, 0.0F, 0.5F, 1.0F - 3 * pixel, 1.0F - 3 * pixel);
                renderblocks.renderStandardBlock(block, x, y, z);
                renderblocks.setRenderBounds(
                        0.5F,
                        1.0F - 6 * pixel,
                        0.0F,
                        1.0F - pixel,
                        1.0F - 3 * pixel,
                        1.0F - 9 * pixel);
                renderblocks.renderStandardBlock(block, x, y, z);

                // y3
                renderblocks
                        .setRenderBounds(pixel * 2, 1.0F - 9 * pixel, 0.0F, 0.5F, 1.0F - 6 * pixel, 1.0F - 6 * pixel);
                renderblocks.renderStandardBlock(block, x, y, z);
                renderblocks.setRenderBounds(
                        0.5F,
                        1.0F - 9 * pixel,
                        0.0F,
                        1.0F - pixel * 2,
                        1.0F - 6 * pixel,
                        1.0F - 12 * pixel);
                renderblocks.renderStandardBlock(block, x, y, z);

                // y4
                renderblocks
                        .setRenderBounds(pixel * 3, 1.0F - 12 * pixel, 0.0F, 0.5F, 1.0F - 9 * pixel, 1.0F - 9 * pixel);
                renderblocks.renderStandardBlock(block, x, y, z);
                renderblocks.setRenderBounds(
                        0.5F,
                        1.0F - 12 * pixel,
                        0.0F,
                        1.0F - pixel * 3,
                        1.0F - 9 * pixel,
                        1.0F - 15 * pixel);
                renderblocks.renderStandardBlock(block, x, y, z);

                // y5
                renderblocks.setRenderBounds(
                        pixel * 4,
                        1.0F - 15 * pixel,
                        0.0F,
                        0.5F,
                        1.0F - 12 * pixel,
                        1.0F - 12 * pixel);
                renderblocks.renderStandardBlock(block, x, y, z);
                renderblocks.setRenderBounds(
                        0.5F,
                        1.0F - 15 * pixel,
                        0.0F,
                        1.0F - pixel * 4,
                        1.0F - 12 * pixel,
                        1.0F - 15 * pixel);
                renderblocks.renderStandardBlock(block, x, y, z);

                currentBlock.SetRenderMode(RenderMode.UNZY);

                renderblocks.uvRotateTop = 0;
                renderblocks.uvRotateBottom = 0;
                renderblocks.uvRotateEast = 0;
                renderblocks.uvRotateWest = 0;
                renderblocks.uvRotateNorth = 0;
                renderblocks.uvRotateSouth = 0;

                // zy
                renderblocks.setRenderBounds(pixel * 19 / 4, 0.0F, 0.0F, 1.0F - pixel * 17 / 4, pixel, pixel);
                renderblocks.renderStandardBlock(block, x, y, z);
                break;
            case 7:
                currentBlock.SetRenderMode(RenderMode.USZ);

                renderblocks.uvRotateTop = 0;
                renderblocks.uvRotateBottom = 3;
                renderblocks.uvRotateEast = 0;
                renderblocks.uvRotateWest = 0;
                renderblocks.uvRotateNorth = 0;
                renderblocks.uvRotateSouth = 0;

                // z1
                renderblocks.setRenderBounds(0.0F, 0.0F, 0.0F, 0.5F, 1.0F, 3 * pixel);
                renderblocks.renderStandardBlock(block, x, y, z);
                renderblocks.setRenderBounds(0.5F, 0.0F, 0.0F, 1.0F, 1.0F - 3 * pixel, 3 * pixel);
                renderblocks.renderStandardBlock(block, x, y, z);

                // z2
                renderblocks.setRenderBounds(pixel / 4, 0.0F, 3 * pixel, 0.5F, 1.0F - 3 * pixel, 6 * pixel);
                renderblocks.renderStandardBlock(block, x, y, z);
                renderblocks.setRenderBounds(0.5F, 0.0F, 3 * pixel, 1.0F - pixel * 3 / 4, 1.0F - 6 * pixel, 6 * pixel);
                renderblocks.renderStandardBlock(block, x, y, z);

                // z3
                renderblocks.setRenderBounds(pixel * 5 / 4, 0.0F, 6 * pixel, 0.5F, 1.0F - 3 * pixel, 9 * pixel);
                renderblocks.renderStandardBlock(block, x, y, z);
                renderblocks.setRenderBounds(0.5F, 0.0F, 6 * pixel, 1.0F - pixel * 7 / 4, 1.0F - 9 * pixel, 9 * pixel);
                renderblocks.renderStandardBlock(block, x, y, z);

                // z4
                renderblocks.setRenderBounds(pixel * 9 / 4, 0.0F, 9 * pixel, 0.5F, 1.0F - 6 * pixel, 12 * pixel);
                renderblocks.renderStandardBlock(block, x, y, z);
                renderblocks
                        .setRenderBounds(0.5F, 0.0F, 9 * pixel, 1.0F - pixel * 11 / 4, 1.0F - 12 * pixel, 12 * pixel);
                renderblocks.renderStandardBlock(block, x, y, z);

                // z5
                renderblocks.setRenderBounds(pixel * 13 / 4, 0.0F, 12 * pixel, 0.5F, 1.0F - 9 * pixel, 15 * pixel);
                renderblocks.renderStandardBlock(block, x, y, z);
                renderblocks
                        .setRenderBounds(0.5F, 0.0F, 12 * pixel, 1.0F - pixel * 15 / 4, 1.0F - 15 * pixel, 15 * pixel);
                renderblocks.renderStandardBlock(block, x, y, z);

                currentBlock.SetRenderMode(RenderMode.USY);

                renderblocks.uvRotateTop = 0;
                renderblocks.uvRotateBottom = 0;
                renderblocks.uvRotateEast = 0;
                renderblocks.uvRotateWest = 0;
                renderblocks.uvRotateNorth = 0;
                renderblocks.uvRotateSouth = 0;

                // y1
                renderblocks.setRenderBounds(0.0F, 1.0F - 3 * pixel, 3 * pixel, 0.5F, 1.0F, 1.0F);
                renderblocks.renderStandardBlock(block, x, y, z);
                renderblocks.setRenderBounds(0.5F, 1.0F - 3 * pixel, 0.0F, 1.0F, 1.0F, 1.0F);
                renderblocks.renderStandardBlock(block, x, y, z);

                // y2
                renderblocks.setRenderBounds(pixel, 1.0F - 6 * pixel, 9 * pixel, 0.5F, 1.0F - 3 * pixel, 1.0F);
                renderblocks.renderStandardBlock(block, x, y, z);
                renderblocks.setRenderBounds(0.5F, 1.0F - 6 * pixel, 3 * pixel, 1.0F - pixel, 1.0F - 3 * pixel, 1.0F);
                renderblocks.renderStandardBlock(block, x, y, z);

                // y3
                renderblocks.setRenderBounds(pixel * 2, 1.0F - 9 * pixel, 12 * pixel, 0.5F, 1.0F - 6 * pixel, 1.0F);
                renderblocks.renderStandardBlock(block, x, y, z);
                renderblocks
                        .setRenderBounds(0.5F, 1.0F - 9 * pixel, 6 * pixel, 1.0F - pixel * 2, 1.0F - 6 * pixel, 1.0F);
                renderblocks.renderStandardBlock(block, x, y, z);

                // y4
                renderblocks.setRenderBounds(pixel * 3, 1.0F - 12 * pixel, 15 * pixel, 0.5F, 1.0F - 9 * pixel, 1.0F);
                renderblocks.renderStandardBlock(block, x, y, z);
                renderblocks
                        .setRenderBounds(0.5F, 1.0F - 12 * pixel, 9 * pixel, 1.0F - pixel * 3, 1.0F - 9 * pixel, 1.0F);
                renderblocks.renderStandardBlock(block, x, y, z);

                // y5
                renderblocks.setRenderBounds(pixel * 4, 1.0F - 15 * pixel, 15 * pixel, 0.5F, 1.0F - 12 * pixel, 1.0F);
                renderblocks.renderStandardBlock(block, x, y, z);
                renderblocks.setRenderBounds(
                        0.5F,
                        1.0F - 15 * pixel,
                        12 * pixel,
                        1.0F - pixel * 4,
                        1.0F - 12 * pixel,
                        1.0F);
                renderblocks.renderStandardBlock(block, x, y, z);

                currentBlock.SetRenderMode(RenderMode.USZY);

                renderblocks.uvRotateTop = 0;
                renderblocks.uvRotateBottom = 0;
                renderblocks.uvRotateEast = 0;
                renderblocks.uvRotateWest = 0;
                renderblocks.uvRotateNorth = 0;
                renderblocks.uvRotateSouth = 0;

                // zy
                renderblocks.setRenderBounds(pixel * 19 / 4, 0.0F, 15 * pixel, 1.0F - pixel * 17 / 4, pixel, 1.0F);
                renderblocks.renderStandardBlock(block, x, y, z);
                break;
        }

        block.setBlockBoundsForItemRender();
        return true;
    }

    public static void renderInvSpiralBricks(RenderBlocks renderblocks, Block block, int meta) {
        Tessellator tessellator = Tessellator.instance;

        GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

        float pixel = 0.0625F;

        BlockTFSpiralBricks currentBlock = (BlockTFSpiralBricks) block;
        currentBlock.SetRenderMode(RenderMode.DEXInv);

        renderblocks.uvRotateTop = 3;
        renderblocks.uvRotateBottom = 0;
        renderblocks.uvRotateEast = 0;
        renderblocks.uvRotateWest = 0;
        renderblocks.uvRotateNorth = 0;
        renderblocks.uvRotateSouth = 0;

        // z1
        renderblocks.setRenderBounds(0.0F, 3 * pixel, 0.0F, 0.5F, 1.0F, 3 * pixel);
        renderInvBlock(renderblocks, block, meta, tessellator);
        renderblocks.setRenderBounds(0.5F, 0.0F, 0.0F, 1.0F, 1.0F, 3 * pixel);
        renderInvBlock(renderblocks, block, meta, tessellator);

        // z2
        renderblocks.setRenderBounds(pixel / 4, 6 * pixel, 3 * pixel, 0.5F, 1.0F, 6 * pixel);
        renderInvBlock(renderblocks, block, meta, tessellator);
        renderblocks.setRenderBounds(0.5F, 3 * pixel, 3 * pixel, 1.0F - pixel * 3 / 4, 1.0F, 6 * pixel);
        renderInvBlock(renderblocks, block, meta, tessellator);

        // z3
        renderblocks.setRenderBounds(pixel * 5 / 4, 9 * pixel, 6 * pixel, 0.5F, 1.0F, 9 * pixel);
        renderInvBlock(renderblocks, block, meta, tessellator);
        renderblocks.setRenderBounds(0.5F, 6 * pixel, 6 * pixel, 1.0F - pixel * 7 / 4, 1.0F, 9 * pixel);
        renderInvBlock(renderblocks, block, meta, tessellator);

        // z4
        renderblocks.setRenderBounds(pixel * 9 / 4, 12 * pixel, 9 * pixel, 0.5F, 1.0F, 12 * pixel);
        renderInvBlock(renderblocks, block, meta, tessellator);
        renderblocks.setRenderBounds(0.5F, 9 * pixel, 9 * pixel, 1.0F - pixel * 11 / 4, 1.0F, 12 * pixel);
        renderInvBlock(renderblocks, block, meta, tessellator);

        // z5
        renderblocks.setRenderBounds(pixel * 13 / 4, 15 * pixel, 12 * pixel, 0.5F, 1.0F, 15 * pixel);
        renderInvBlock(renderblocks, block, meta, tessellator);
        renderblocks.setRenderBounds(0.5F, 12 * pixel, 12 * pixel, 1.0F - pixel * 15 / 4, 1.0F, 15 * pixel);
        renderInvBlock(renderblocks, block, meta, tessellator);

        currentBlock.SetRenderMode(RenderMode.DEYInv);

        renderblocks.uvRotateTop = 0;
        renderblocks.uvRotateBottom = 0;
        renderblocks.uvRotateEast = 0;
        renderblocks.uvRotateWest = 0;
        renderblocks.uvRotateNorth = 0;
        renderblocks.uvRotateSouth = 0;

        // y1
        renderblocks.setRenderBounds(0.0F, 0.0F, 0.0F, 0.5F, 3 * pixel, 1.0F);
        renderInvBlock(renderblocks, block, meta, tessellator);
        renderblocks.setRenderBounds(0.5F, 0.0F, 3 * pixel, 1.0F, 3 * pixel, 1.0F);
        renderInvBlock(renderblocks, block, meta, tessellator);

        // y2
        renderblocks.setRenderBounds(pixel / 2, 3 * pixel, 3 * pixel, 0.5F, 6 * pixel, 1.0F);
        renderInvBlock(renderblocks, block, meta, tessellator);
        renderblocks.setRenderBounds(0.5F, 3 * pixel, 6 * pixel, 1.0F - pixel / 2, 6 * pixel, 1.0F);
        renderInvBlock(renderblocks, block, meta, tessellator);

        // y3
        renderblocks.setRenderBounds(pixel * 3 / 2, 6 * pixel, 6 * pixel, 0.5F, 9 * pixel, 1.0F);
        renderInvBlock(renderblocks, block, meta, tessellator);
        renderblocks.setRenderBounds(0.5F, 6 * pixel, 9 * pixel, 1.0F - pixel * 3 / 2, 9 * pixel, 1.0F);
        renderInvBlock(renderblocks, block, meta, tessellator);

        // y4
        renderblocks.setRenderBounds(pixel * 5 / 2, 9 * pixel, 9 * pixel, 0.5F, 12 * pixel, 1.0F);
        renderInvBlock(renderblocks, block, meta, tessellator);
        renderblocks.setRenderBounds(0.5F, 9 * pixel, 12 * pixel, 1.0F - pixel * 5 / 2, 12 * pixel, 1.0F);
        renderInvBlock(renderblocks, block, meta, tessellator);

        // y5
        renderblocks.setRenderBounds(pixel * 7 / 2, 12 * pixel, 12 * pixel, 0.5F, 15 * pixel, 1.0F);
        renderInvBlock(renderblocks, block, meta, tessellator);
        renderblocks.setRenderBounds(0.5F, 12 * pixel, 15 * pixel, 1.0F - pixel * 7 / 2, 15 * pixel, 1.0F);
        renderInvBlock(renderblocks, block, meta, tessellator);

        currentBlock.SetRenderMode(RenderMode.DEXY);

        renderblocks.uvRotateTop = 1;
        renderblocks.uvRotateBottom = 0;
        renderblocks.uvRotateEast = 0;
        renderblocks.uvRotateWest = 0;
        renderblocks.uvRotateNorth = 0;
        renderblocks.uvRotateSouth = 0;

        // zy
        renderblocks.setRenderBounds(pixel * 19 / 4, 15 * pixel, 15 * pixel, 1.0F - pixel * 17 / 4, 1.0F, 1.0F);
        renderInvBlock(renderblocks, block, meta, tessellator);

        block.setBlockBoundsForItemRender();
    }

    protected static void renderInvBlock(RenderBlocks renderblocks, Block par1Block, int meta,
            Tessellator tessellator) {
        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, -1.0F, 0.0F);
        renderblocks.renderFaceYNeg(par1Block, 0.0D, 0.0D, 0.0D, par1Block.getIcon(0, meta));
        tessellator.setNormal(0.0F, 1.0F, 0.0F);
        renderblocks.renderFaceYPos(par1Block, 0.0D, 0.0D, 0.0D, par1Block.getIcon(1, meta));
        tessellator.setNormal(0.0F, 0.0F, -1.0F);
        renderblocks.renderFaceXPos(par1Block, 0.0D, 0.0D, 0.0D, par1Block.getIcon(2, meta));
        tessellator.setNormal(0.0F, 0.0F, 1.0F);
        renderblocks.renderFaceXNeg(par1Block, 0.0D, 0.0D, 0.0D, par1Block.getIcon(3, meta));
        tessellator.setNormal(-1.0F, 0.0F, 0.0F);
        renderblocks.renderFaceZNeg(par1Block, 0.0D, 0.0D, 0.0D, par1Block.getIcon(4, meta));
        tessellator.setNormal(1.0F, 0.0F, 0.0F);
        renderblocks.renderFaceZPos(par1Block, 0.0D, 0.0D, 0.0D, par1Block.getIcon(5, meta));
        tessellator.draw();
    }

}
