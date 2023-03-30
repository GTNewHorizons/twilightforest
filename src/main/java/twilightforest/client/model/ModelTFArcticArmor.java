package twilightforest.client.model;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;

public class ModelTFArcticArmor extends ModelBiped {

    public ModelTFArcticArmor(int part, float expand) {
        super(expand);

        ModelRenderer rightHood = new ModelRenderer(this, 0, 0);
        rightHood.addBox(-1.0F, -2.0F, -1.0F, 1, 4, 1, expand);
        rightHood.setRotationPoint(-2.5F, -3.0F, -5.0F);
        this.bipedHead.addChild(rightHood);

        ModelRenderer leftHood = new ModelRenderer(this, 0, 0);
        leftHood.addBox(0.0F, -2.0F, -1.0F, 1, 4, 1, expand);
        leftHood.setRotationPoint(2.5F, -3.0F, -5.0F);
        this.bipedHead.addChild(leftHood);

        ModelRenderer topHood = new ModelRenderer(this, 24, 0);
        topHood.addBox(-2.0F, -1.0F, -1.0F, 4, 1, 1, expand);
        topHood.setRotationPoint(0.0F, -5.5F, -5.0F);
        this.bipedHead.addChild(topHood);

        ModelRenderer bottomHood = new ModelRenderer(this, 24, 0);
        bottomHood.addBox(-2.0F, -1.0F, -1.0F, 4, 1, 1, expand);
        bottomHood.setRotationPoint(0.0F, 0.5F, -5.0F);
        this.bipedHead.addChild(bottomHood);

        switch (part) {
            case 0 -> { // helmet
                this.bipedHead.showModel = true;
                this.bipedHeadwear.showModel = false;
                this.bipedBody.showModel = false;
                this.bipedRightArm.showModel = false;
                this.bipedLeftArm.showModel = false;
                this.bipedRightLeg.showModel = false;
                this.bipedLeftLeg.showModel = false;
            }
            case 1 -> { // chest
                this.bipedHead.showModel = false;
                this.bipedHeadwear.showModel = false;
                this.bipedBody.showModel = true;
                this.bipedRightArm.showModel = true;
                this.bipedLeftArm.showModel = true;
                this.bipedRightLeg.showModel = false;
                this.bipedLeftLeg.showModel = false;
            }
            case 2 -> { // pants
                this.bipedHead.showModel = false;
                this.bipedHeadwear.showModel = false;
                this.bipedBody.showModel = true;
                this.bipedRightArm.showModel = false;
                this.bipedLeftArm.showModel = false;
                this.bipedRightLeg.showModel = true;
                this.bipedLeftLeg.showModel = true;
            }
            case 3 -> { // boots
                this.bipedHead.showModel = false;
                this.bipedHeadwear.showModel = false;
                this.bipedBody.showModel = false;
                this.bipedRightArm.showModel = false;
                this.bipedLeftArm.showModel = false;
                this.bipedRightLeg.showModel = true;
                this.bipedLeftLeg.showModel = true;
            }
        }

    }

    /**
     * Sets the models various rotation angles then renders the model.
     */
    @Override
    public void render(Entity par1Entity, float par2, float par3, float par4, float par5, float par6, float par7) {

        if (par1Entity != null) {
            this.isSneak = par1Entity.isSneaking();
        }

        if (par1Entity != null && par1Entity instanceof EntityLivingBase) {
            this.heldItemRight = ((EntityLivingBase) par1Entity).getHeldItem() != null ? 1 : 0;
        }

        super.render(par1Entity, par2, par3, par4, par5, par6, par7);
    }

}
