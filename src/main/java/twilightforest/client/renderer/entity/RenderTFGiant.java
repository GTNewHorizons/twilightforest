package twilightforest.client.renderer.entity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import twilightforest.TwilightForestMod;

public class RenderTFGiant extends RenderBiped {

    private static final ResourceLocation textureLoc = new ResourceLocation("textures/entity/steve.png");
    private final ModelRenderer normalRightArm;
    private final ModelRenderer normalLeftArm;
    private final ModelRenderer slimRightArm;
    private final ModelRenderer slimLeftArm;

    public RenderTFGiant() {
        super(new ModelBiped(), 0.625F);

        normalRightArm = this.modelBipedMain.bipedRightArm;
        normalLeftArm = this.modelBipedMain.bipedLeftArm;
        slimRightArm = new ModelRenderer(this.modelBipedMain, 40, 16);
        slimRightArm.addBox(-2.0F, -2.0F, -2.0F, 3, 12, 4, 0);
        slimRightArm.setRotationPoint(-5.0F, 2.0F, 0.0F);
        slimLeftArm = new ModelRenderer(this.modelBipedMain, 40, 16);
        slimLeftArm.mirror = true;
        slimLeftArm.addBox(-1.0F, -2.0F, -2.0F, 3, 12, 4, 0);
        slimLeftArm.setRotationPoint(5.0F, 2.0F, 0.0F);
    }

    /**
     * Return our specific texture
     */
    @Override
    protected ResourceLocation getEntityTexture(Entity par1Entity) {
        ResourceLocation skin;
        EntityClientPlayerMP player = Minecraft.getMinecraft().thePlayer;
        if (player != null && player.getLocationSkin() != null) {
            skin = player.getLocationSkin();
        } else {
            skin = textureLoc;
        }
        if (TwilightForestMod.isSkinportLoaded && player != null) {
            skin = RenderTFGiantSkinportIntegration.getSkin(player, skin, textureLoc);
        }
        return skin;
    }

    /**
     * Allows the render to do any OpenGL state modifications necessary before the model is rendered. Args:
     * entityLiving, partialTickTime
     */
    protected void preRenderCallback(EntityLivingBase par1EntityLivingBase, float par2) {
        boolean slim = TwilightForestMod.isSkinportLoaded && Minecraft.getMinecraft().thePlayer != null
                && RenderTFGiantSkinportIntegration.isSlim(Minecraft.getMinecraft().thePlayer);
        this.modelBipedMain.bipedRightArm = slim ? slimRightArm : normalRightArm;
        this.modelBipedMain.bipedLeftArm = slim ? slimLeftArm : normalLeftArm;

        float scale = 4.0F;
        GL11.glScalef(scale, scale, scale);
    }
}
