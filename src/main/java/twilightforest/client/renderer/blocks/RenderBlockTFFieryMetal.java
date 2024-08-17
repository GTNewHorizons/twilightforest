package twilightforest.client.renderer.blocks;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import twilightforest.block.BlockTFCompressed;

public class RenderBlockTFFieryMetal implements ISimpleBlockRenderingHandler {

    final int renderID;

    public RenderBlockTFFieryMetal(int blockComplexRenderID) {
        this.renderID = blockComplexRenderID;
    }

    @Override
    public void renderInventoryBlock(Block block, int metadata, int modelID, RenderBlocks renderer) {
        renderInvJar(renderer, block, metadata);
    }

    @Override
    public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId,
            RenderBlocks renderer) {
        return renderGlowingEdgeBlock(renderer, world, x, y, z, (BlockTFCompressed) block);
    }

    @Override
    public boolean shouldRender3DInInventory(int modelId) {
        return true;
    }

    @Override
    public int getRenderId() {
        return renderID;
    }

    public static boolean renderGlowingEdgeBlock(RenderBlocks renderblocks, IBlockAccess world, int x, int y, int z,
            BlockTFCompressed block) {
        float p = 1F / 16F;
        float m = (1.0f - p) / 2;
        float s = 0.001f;

        Tessellator tessellator = Tessellator.instance;
        tessellator.setBrightness(15 << 20 | 15 << 4); // full brightness
        tessellator.setColorOpaque_F(1.0f, 1.0f, 1.0f);

        renderblocks.enableAO = false;

        // middle
        renderblocks.renderAllFaces = true;
        renderblocks.setRenderBounds(p, p, p, 1.0f - p, 1.0f - p, 1.0f - p);
        renderInnerPart(renderblocks, world, x, y, z, block);
        renderblocks.renderAllFaces = false;

        for (int rx = -1; rx <= 1; rx++) {
            for (int ry = -1; ry <= 1; ry++) {
                for (int rz = -1; rz <= 1; rz++) {
                    int moveAxis = Math.abs(rx) + Math.abs(ry) + Math.abs(rz);
                    if (moveAxis == 1 || moveAxis == 2) if (block.canConnectFieryTo(world, x + rx, y + ry, z + rz)) {
                        if (moveAxis == 2) {
                            int neighbours = 0;
                            if (rx != 0) neighbours += (block.canConnectFieryTo(world, x + rx, y, z)) ? 1 : 0;
                            if (ry != 0) neighbours += (block.canConnectFieryTo(world, x, y + ry, z)) ? 1 : 0;
                            if (rz != 0) neighbours += (block.canConnectFieryTo(world, x, y, z + rz)) ? 1 : 0;
                            if (neighbours != 2) continue;
                        }
                        renderblocks.renderAllFaces = true;
                        float minX = (rx == 0) ? p : m + m * rx;
                        float minY = (ry == 0) ? p : m + m * ry;
                        float minZ = (rz == 0) ? p : m + m * rz;
                        float maxX = (rx == 0) ? 1.0f - p : m + m * rx + p;
                        float maxY = (ry == 0) ? 1.0f - p : m + m * ry + p;
                        float maxZ = (rz == 0) ? 1.0f - p : m + m * rz + p;
                        renderblocks.setRenderBounds(minX, minY, minZ, maxX, maxY, maxZ);
                        renderInnerPart(renderblocks, world, x, y, z, block);
                        renderblocks.renderAllFaces = false;
                    } else {
                        // edge
                        if (moveAxis == 1) {
                            renderblocks.setRenderBounds(
                                    (world.getBlock(x, y, z).isBlockSolid(world, x, y, z, 0) ? 0 : s),
                                    (world.getBlock(x, y, z).isBlockSolid(world, x, y, z, 0) ? 0 : s),
                                    (world.getBlock(x, y, z).isBlockSolid(world, x, y, z, 0) ? 0 : s),
                                    1 - (world.getBlock(x, y, z).isBlockSolid(world, x, y, z, 0) ? 0 : s),
                                    1 - (world.getBlock(x, y, z).isBlockSolid(world, x, y, z, 0) ? 0 : s),
                                    1 - (world.getBlock(x, y, z).isBlockSolid(world, x, y, z, 0) ? 0 : s));
                            renderblocks.renderFromInside = true;
                            boolean flipTextures = (x + y + z) % 2 == 0;
                            if (rx != 0) {
                                if (rx == -1) renderblocks
                                        .renderFaceXNeg(block, x, y, z, block.fieryPattern[flipTextures ? 0 : 1]);
                                else renderblocks
                                        .renderFaceXPos(block, x, y, z, block.fieryPattern[flipTextures ? 3 : 2]);
                            } else {
                                if (ry != 0) {
                                    if (ry == -1) renderblocks
                                            .renderFaceYNeg(block, x, y, z, block.fieryPattern[flipTextures ? 2 : 0]);
                                    else renderblocks
                                            .renderFaceYPos(block, x, y, z, block.fieryPattern[flipTextures ? 3 : 1]);
                                } else {
                                    if (rz == -1) renderblocks
                                            .renderFaceZNeg(block, x, y, z, block.fieryPattern[flipTextures ? 1 : 0]);
                                    else renderblocks
                                            .renderFaceZPos(block, x, y, z, block.fieryPattern[flipTextures ? 2 : 3]);
                                }
                            }
                            renderblocks.renderFromInside = false;
                        }
                    }
                }
            }
        }

        block.setBlockBoundsForItemRender();

        renderblocks.enableAO = false;
        return true;
    }

    public static void renderInnerPart(RenderBlocks renderblocks, IBlockAccess world, int x, int y, int z,
            BlockTFCompressed block) {
        boolean neighbourU = block.canConnectFieryTo(world, x, y + 1, z);
        boolean neighbourD = block.canConnectFieryTo(world, x, y - 1, z);
        boolean neighbourN = block.canConnectFieryTo(world, x + 1, y, z);
        boolean neighbourS = block.canConnectFieryTo(world, x - 1, y, z);
        boolean neighbourE = block.canConnectFieryTo(world, x, y, z + 1);
        boolean neighbourW = block.canConnectFieryTo(world, x, y, z - 1);
        boolean neighbourUN = neighbourU && neighbourN && block.canConnectFieryTo(world, x + 1, y + 1, z);
        boolean neighbourUS = neighbourU && neighbourS && block.canConnectFieryTo(world, x - 1, y + 1, z);
        boolean neighbourUE = neighbourU && neighbourE && block.canConnectFieryTo(world, x, y + 1, z + 1);
        boolean neighbourUW = neighbourU && neighbourW && block.canConnectFieryTo(world, x, y + 1, z - 1);
        boolean neighbourDN = neighbourD && neighbourN && block.canConnectFieryTo(world, x + 1, y - 1, z);
        boolean neighbourDS = neighbourD && neighbourS && block.canConnectFieryTo(world, x - 1, y - 1, z);
        boolean neighbourDE = neighbourD && neighbourE && block.canConnectFieryTo(world, x, y - 1, z + 1);
        boolean neighbourDW = neighbourD && neighbourW && block.canConnectFieryTo(world, x, y - 1, z - 1);
        boolean neighbourNE = neighbourN && neighbourE && block.canConnectFieryTo(world, x + 1, y, z + 1);
        boolean neighbourNW = neighbourN && neighbourW && block.canConnectFieryTo(world, x + 1, y, z - 1);
        boolean neighbourSE = neighbourS && neighbourE && block.canConnectFieryTo(world, x - 1, y, z + 1);
        boolean neighbourSW = neighbourS && neighbourW && block.canConnectFieryTo(world, x - 1, y, z - 1);
        boolean neighboursUD = neighbourU || neighbourD;
        boolean neighboursNS = neighbourN || neighbourS;
        boolean neighboursEW = neighbourE || neighbourW;
        IIcon icon;

        // X faces
        if (neighboursUD) {
            if (neighboursEW) {
                if (neighbourUE || neighbourUW || neighbourDE || neighbourDW) icon = block.fieryCore[4];
                else icon = block.fieryCore[3];
            } else icon = block.fieryCore[2];
        } else {
            if (neighboursEW) icon = block.fieryCore[1];
            else icon = block.fieryCore[0];
        }
        renderblocks.renderFaceXPos(block, x, y, z, icon);
        renderblocks.renderFaceXNeg(block, x, y, z, icon);

        // Z faces
        if (neighboursUD) {
            if (neighboursNS) {
                if (neighbourUN || neighbourUS || neighbourDN || neighbourDS) icon = block.fieryCore[4];
                else icon = block.fieryCore[3];
            } else icon = block.fieryCore[2];
        } else {
            if (neighboursNS) icon = block.fieryCore[1];
            else icon = block.fieryCore[0];
        }
        renderblocks.renderFaceZPos(block, x, y, z, icon);
        renderblocks.renderFaceZNeg(block, x, y, z, icon);

        // Y faces
        if (neighboursEW) {
            if (neighboursNS) {
                if (neighbourNE || neighbourNW || neighbourSE || neighbourSW) icon = block.fieryCore[4];
                else icon = block.fieryCore[3];
            } else icon = block.fieryCore[2];
        } else {
            if (neighboursNS) icon = block.fieryCore[1];
            else icon = block.fieryCore[0];
        }
        renderblocks.renderFaceYPos(block, x, y, z, icon);
        renderblocks.renderFaceYNeg(block, x, y, z, icon);
    }

    public static void renderInvJar(RenderBlocks renderblocks, Block par1Block, int meta) {
        Tessellator tessellator = Tessellator.instance;

        GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

        float p = 1F / 16F;

        // middle
        renderblocks.setRenderBounds(p, p, p, 1 - p, 1 - p, 1 - p);
        renderInvBlock(renderblocks, par1Block, meta, tessellator);

        // outline
        renderblocks.setRenderBounds(0, 0, 0, 1, 1, 1);
        renderInvOutline(renderblocks, (BlockTFCompressed) par1Block, meta, tessellator);

        GL11.glTranslatef(0.5F, 0.5F, 0.5F);

        par1Block.setBlockBoundsForItemRender();
    }

    public static void renderInvBlock(RenderBlocks renderblocks, Block par1Block, int meta, Tessellator tessellator) {
        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, 1.0F, 0.0F);
        renderblocks.renderFaceYNeg(par1Block, 0.0D, 0.0D, 0.0D, par1Block.getIcon(0, meta));
        tessellator.setNormal(0.0F, 1.0F, 0.0F);
        renderblocks.renderFaceYPos(par1Block, 0.0D, 0.0D, 0.0D, par1Block.getIcon(1, meta));
        tessellator.setNormal(0.0F, 1.0F, 0.0F);
        renderblocks.renderFaceXPos(par1Block, 0.0D, 0.0D, 0.0D, par1Block.getIcon(2, meta));
        tessellator.setNormal(0.0F, 1.0F, 0.0F);
        renderblocks.renderFaceXNeg(par1Block, 0.0D, 0.0D, 0.0D, par1Block.getIcon(3, meta));
        tessellator.setNormal(0.0F, 1.0F, 0.0F);
        renderblocks.renderFaceZNeg(par1Block, 0.0D, 0.0D, 0.0D, par1Block.getIcon(4, meta));
        tessellator.setNormal(0.0F, 1.0F, 0.0F);
        renderblocks.renderFaceZPos(par1Block, 0.0D, 0.0D, 0.0D, par1Block.getIcon(5, meta));
        tessellator.draw();
    }

    protected static void renderInvOutline(RenderBlocks renderblocks, BlockTFCompressed block, int meta,
            Tessellator tessellator) {
        renderblocks.renderFromInside = true;
        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, 1.0F, 0.0F);
        renderblocks.renderFaceYNeg(block, 0.0D, 0.0D, 0.0D, block.fieryPattern[2]);
        tessellator.setNormal(0.0F, 1.0F, 0.0F);
        renderblocks.renderFaceYPos(block, 0.0D, 0.0D, 0.0D, block.fieryPattern[3]);
        tessellator.setNormal(0.0F, 1.0F, 0.0F);
        renderblocks.renderFaceXPos(block, 0.0D, 0.0D, 0.0D, block.fieryPattern[0]);
        tessellator.setNormal(0.0F, 1.0F, 0.0F);
        renderblocks.renderFaceXNeg(block, 0.0D, 0.0D, 0.0D, block.fieryPattern[3]);
        tessellator.setNormal(0.0F, 1.0F, 0.0F);
        renderblocks.renderFaceZNeg(block, 0.0D, 0.0D, 0.0D, block.fieryPattern[2]);
        tessellator.setNormal(0.0F, 1.0F, 0.0F);
        renderblocks.renderFaceZPos(block, 0.0D, 0.0D, 0.0D, block.fieryPattern[1]);
        tessellator.draw();
        renderblocks.renderFromInside = false;
    }

}
