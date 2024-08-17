package twilightforest.client.model;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;

import org.lwjgl.opengl.GL11;

public class ModelTFTrophyArmor extends ModelBiped {

    private int boss = 0;
    private int time = 0;

    public ModelTFTrophyArmor(int boss, float expand) {
        super(expand);
        this.boss = boss;

        this.bipedHead = new ModelRenderer(this, 0, 0);
        this.bipedHead.setRotationPoint(0, 0, 0);

        this.bipedHead.showModel = true;
        this.bipedHeadwear.showModel = false;
        this.bipedBody.showModel = false;
        this.bipedRightArm.showModel = false;
        this.bipedLeftArm.showModel = false;
        this.bipedRightLeg.showModel = false;
        this.bipedLeftLeg.showModel = false;

        switch (boss) {
            case 0 -> { // Hydra
                ModelTFHydraHead hydraHeadModel = new ModelTFHydraHead();
                hydraHeadModel.head.offsetY = -1.0f;
                hydraHeadModel.head.offsetZ = 1.1f;
                bipedHead.addChild(hydraHeadModel.head);
            }
            case 1 -> { // Naga
                ModelTFNaga nagaHeadModel = new ModelTFNaga();
                nagaHeadModel.head.offsetY = -0.25f;
                bipedHead.addChild(nagaHeadModel.head);
            }
            case 2 -> { // Lich
                ModelTFLich lichModel = new ModelTFLich();
                bipedHead = lichModel.bipedHead;
                bipedHeadwear = lichModel.bipedHeadwear;
                bipedHeadwear.showModel = true;
            }
            case 3 -> { // Ur-Ghast
                ModelTFTowerBoss urGhastModel = new ModelTFTowerBoss();
                urGhastModel.body.offsetY = -1f;
                bipedHead.addChild(urGhastModel.body);
            }
            case 4 -> { // Snow Queen
                ModelTFSnowQueen snowQueenModel = new ModelTFSnowQueen();
                bipedHead = snowQueenModel.bipedHead;
                bipedHeadwear = snowQueenModel.bipedHeadwear;
                bipedHeadwear.showModel = true;
            }
            case 5 -> { // Minoshroom
                ModelTFMinoshroom minoshroomModel = new ModelTFMinoshroom();
                bipedHead = minoshroomModel.bipedHead;
            }
            case 6 -> { // Knight Phantom
                ModelTFKnightlyArmor knightPhantomArmorModel = new ModelTFKnightlyArmor(0, 0.5F);
                bipedHead = knightPhantomArmorModel.bipedHead;
                ModelRenderer skeletonHead = new ModelRenderer(this, 0, 16);
                skeletonHead.addBox(-4.0F, -8.0F, -4.0F, 8, 8, 8);
                skeletonHead.setRotationPoint(0.0F, 0.0F, 0.0F);
                bipedHead.addChild(skeletonHead);
            }
            case 7 -> { // Alpha Yeti
                ModelTFYetiAlpha alphaYetiModel = new ModelTFYetiAlpha().trophySetup();
                alphaYetiModel.bipedBody.offsetY = 0.75f;
                bipedHead.addChild(alphaYetiModel.bipedBody);
            }
            case 8 -> { // Questing Ram
                ModelTFQuestRam questingRamModel = new ModelTFQuestRam();
                questingRamModel.head.offsetY = 0.525f;
                questingRamModel.head.offsetZ = 0.75f;
                bipedHead.addChild(questingRamModel.head);
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

        float scale = 1.0f;
        float dX = 0.0f;
        float dY = 0.0f;
        float dZ = 0.0f;

        switch (boss) {
            case 0 -> { // Hydra
                scale = 0.3f * 1.06f;
                dX = 0.0f;
                dY = 0.0f;
                dZ = 0.0f;
            }
            case 1 -> { // Naga
                scale = 0.5f * 1.06f;
                dX = 0.0f;
                dY = 0.005f;
                dZ = 0.0f;
            }
            case 2, 4 -> { // Lich, Snow Queen
                scale = 1.06f;
                dX = 0.0f;
                dY = 0.005f;
                dZ = 0.0f;
            }
            case 3 -> { // Ur-Ghast
                bipedHead = new ModelRenderer(this, 0, 0);
                ModelTFTowerBoss urGhastModel = new ModelTFTowerBoss();
                urGhastModel.body.offsetY = -1f;
                urGhastModel.setRotationAngles(0.0F, 0, time, 0, 0.0F, 0.0625F, par1Entity);
                time++;
                bipedHead.addChild(urGhastModel.body);
                scale = 0.5f * 1.06f;
                dX = 0.0f;
                dY = 0.005f;
                dZ = 0.0f;
            }
            case 5 -> { // Minoshroom
                scale = 1.06f;
                dX = 0.0f;
                dY = 0.005f;
                dZ = 0.565f;
            }
            case 6 -> { // Knight Phantom
                scale = 1.0f;
                dX = 0.0f;
                dY = 0.0f;
                dZ = 0.0f;
            }
            case 7 -> { // Alpha Yeti
                scale = 0.23f;
                dX = 0.0f;
                dY = 0.005f;
                dZ = 0.0f;
            }
            case 8 -> { // Questing Ram
                scale = 0.75f;
                dX = 0.0f;
                dY = 0.0f;
                dZ = 0.0f;
            }
        }

        GL11.glScalef(scale, scale, scale);
        GL11.glTranslatef(dX, dY, dZ);

        super.render(par1Entity, par2, par3, par4, par5, par6, par7);

        GL11.glScalef(1 / scale, 1 / scale, 1 / scale);
        GL11.glTranslatef(-dX, -dY, -dZ);
    }

}
