package twilightforest.client.renderer.entity;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import org.lwjgl.opengl.GL11;

import twilightforest.entity.boss.EntityTFFallingIce;

public class RenderTFFallingIce extends Render {

    private final RenderBlocks renderBlocks = new RenderBlocks();

    @Override
    public void doRender(Entity var1, double var2, double var4, double var6, float var8, float var9) {
        this.doRender((EntityTFFallingIce) var1, var2, var4, var6, var8, var9);
    }

    /**
     * Actually renders the given argument. This is a synthetic bridge method, always casting down its argument and then
     * handing it off to a worker function which does the actual work. In all probabilty, the class Render is generic
     * (Render<T extends Entity) and this method has signature public void func_76986_a(T entity, double d, double d1,
     * double d2, float f, float f1). But JAD is pre 1.5 so doesn't do that.
     */
    public void doRender(EntityTFFallingIce entity, double x, double y, double z, float p_147918_8_,
            float p_147918_9_) {
        World world = entity.worldObj;
        Block block = entity.getBlock();
        int i = MathHelper.floor_double(entity.posX);
        int j = MathHelper.floor_double(entity.posY);
        int k = MathHelper.floor_double(entity.posZ);

        GL11.glPushMatrix();
        GL11.glTranslatef((float) x, (float) y + 1.5F, (float) z);
        this.bindEntityTexture(entity);
        GL11.glDisable(GL11.GL_LIGHTING);

        GL11.glScalef(3, 3, 3);

        this.renderBlocks.setRenderBoundsFromBlock(block);
        this.renderBlocks.renderBlockSandFalling(block, world, i, j, k, 0);

        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glPopMatrix();
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity var1) {
        return TextureMap.locationBlocksTexture;

    }

}
