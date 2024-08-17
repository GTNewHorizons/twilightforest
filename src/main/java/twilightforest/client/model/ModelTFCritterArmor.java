package twilightforest.client.model;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;

public class ModelTFCritterArmor extends ModelBiped {

    ModelRenderer critterBody;

    public ModelTFCritterArmor(int critter, float expand) {
        super(expand);
        this.bipedHead = new ModelRenderer(this, 0, 0);
        this.bipedHead.setRotationPoint(0, 0, 0);

        this.critterBody = new ModelRenderer(this, 0, 0);
        this.critterBody.setRotationPoint(0, 0, 0);

        this.bipedHead.showModel = true;
        this.bipedHeadwear.showModel = false;
        this.bipedBody.showModel = false;
        this.bipedRightArm.showModel = false;
        this.bipedLeftArm.showModel = false;
        this.bipedRightLeg.showModel = false;
        this.bipedLeftLeg.showModel = false;

        switch (critter) {
            case 0 -> { // Firefly
                ModelTFFirefly fireflyModel = new ModelTFFirefly();
                critterBody.addChild(fireflyModel.fatbody);
                critterBody.addChild(fireflyModel.skinnybody);
                critterBody.addChild(fireflyModel.legs);
                critterBody.rotateAngleY = (float) Math.PI; // 180 degrees
            }
            case 1 -> { // Cicada
                ModelTFCicada cicadaModel = new ModelTFCicada();
                critterBody.addChild(cicadaModel.fatbody);
                critterBody.addChild(cicadaModel.skinnybody);
                critterBody.addChild(cicadaModel.legs);
                critterBody.addChild(cicadaModel.eye1);
                critterBody.addChild(cicadaModel.eye2);
                critterBody.addChild(cicadaModel.wings);
                critterBody.rotateAngleY = (float) Math.PI; // 180 degrees
            }
            case 2 -> { // Moonworm
                ModelTFMoonworm moonwormModel = new ModelTFMoonworm();
                critterBody.addChild(moonwormModel.head);
                critterBody.addChild(moonwormModel.Shape1);
                critterBody.addChild(moonwormModel.Shape2);
                critterBody.addChild(moonwormModel.Shape3);
                critterBody.rotateAngleY = (float) Math.PI * 3 / 2; // 270 degrees
            }
        }
        critterBody.offsetY = -1.0f;
        critterBody.offsetZ = 0.0f;
        bipedHead.addChild(critterBody);
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
