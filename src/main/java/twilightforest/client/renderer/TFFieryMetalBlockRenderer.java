package twilightforest.client.renderer;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;

import org.lwjgl.opengl.GL11;

import twilightforest.block.BlockTFCompressed;
import twilightforest.block.TFBlocks;
import twilightforest.client.renderer.blocks.RenderBlockTFFieryMetal;

public class TFFieryMetalBlockRenderer implements IItemRenderer {

    @Override
    public boolean handleRenderType(ItemStack item, ItemRenderType type) {
        return type == ItemRenderType.EQUIPPED;
    }

    public void renderInventoryItem(RenderBlocks renderer, ItemStack item) {
        int metadata = item.getItemDamage();
        renderInvSimpleBlock(renderer, TFBlocks.fieryMetalStorage, metadata);
    }

    @Override
    public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
        switch (type) {
            case EQUIPPED:
                GL11.glTranslatef(0.5F, 0.5F, 0.5F);
                renderInventoryItem((RenderBlocks) data[0], item);
                break;
            default:
                break;
        }
    }

    @Override
    public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
        return true;
    }

    public static void renderInvSimpleBlock(RenderBlocks renderblocks, Block par1Block, int meta) {
        Tessellator tessellator = Tessellator.instance;

        GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

        renderblocks.setRenderBounds(0, 0, 0, 1, 1, 1);
        RenderBlockTFFieryMetal.renderInvBlock(renderblocks, (BlockTFCompressed) par1Block, meta, tessellator);

        GL11.glTranslatef(0.5F, 0.5F, 0.5F);

        par1Block.setBlockBoundsForItemRender();
    }
}
