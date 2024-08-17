package twilightforest.client.renderer;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import twilightforest.TwilightForestMod;
import twilightforest.client.model.ModelTFHydraHead;
import twilightforest.client.model.ModelTFKnightPhantom2;
import twilightforest.client.model.ModelTFKnightlyArmor;
import twilightforest.client.model.ModelTFLich;
import twilightforest.client.model.ModelTFMinoshroom;
import twilightforest.client.model.ModelTFNaga;
import twilightforest.client.model.ModelTFQuestRam;
import twilightforest.client.model.ModelTFSnowQueen;
import twilightforest.client.model.ModelTFTowerBoss;
import twilightforest.client.model.ModelTFYetiAlpha;
import twilightforest.tileentity.TileEntityTFTrophy;

public class TileEntityTFTrophyRenderer extends TileEntitySpecialRenderer {

    private ModelTFHydraHead hydraHeadModel;
    private static final ResourceLocation textureLocHydra = new ResourceLocation(
            TwilightForestMod.MODEL_DIR + "hydra4.png");
    private ModelTFNaga nagaHeadModel;
    private static final ResourceLocation textureLocNaga = new ResourceLocation(
            TwilightForestMod.MODEL_DIR + "nagahead.png");
    private ModelTFLich lichModel;
    private static final ResourceLocation textureLocLich = new ResourceLocation(
            TwilightForestMod.MODEL_DIR + "twilightlich64.png");
    private ModelTFTowerBoss urGhastModel;
    private static final ResourceLocation textureLocUrGhast = new ResourceLocation(
            TwilightForestMod.MODEL_DIR + "towerboss.png");
    private ModelTFSnowQueen snowQueenModel;
    private static final ResourceLocation textureLocSnowQueen = new ResourceLocation(
            TwilightForestMod.MODEL_DIR + "snowqueen.png");
    private ModelTFMinoshroom minoshroomModel;
    private static final ResourceLocation textureLocMinoshroom = new ResourceLocation(
            TwilightForestMod.MODEL_DIR + "minoshroomtaur.png");
    private ModelTFKnightPhantom2 knightPhantomModel;
    private ModelTFKnightlyArmor knightPhantomArmorModel;
    private static final ResourceLocation textureLocKnightPhantom = new ResourceLocation(
            TwilightForestMod.MODEL_DIR + "phantomskeleton.png");
    private static final ResourceLocation textureLocKnightPhantomArmor = new ResourceLocation(
            TwilightForestMod.ARMOR_DIR + "phantom_1.png");
    private ModelTFYetiAlpha alphaYetiModel;
    private static final ResourceLocation textureLocAlphaYeti = new ResourceLocation(
            TwilightForestMod.MODEL_DIR + "yetialpha.png");
    private ModelTFQuestRam questingRamModel;
    private static final ResourceLocation textureLocQuestingRam = new ResourceLocation(
            TwilightForestMod.MODEL_DIR + "questram.png");
    private static final ResourceLocation textureLocQuestingRamLines = new ResourceLocation(
            TwilightForestMod.MODEL_DIR + "questram_lines.png");

    public TileEntityTFTrophyRenderer() {
        hydraHeadModel = new ModelTFHydraHead();
        nagaHeadModel = new ModelTFNaga();
        lichModel = new ModelTFLich();
        urGhastModel = new ModelTFTowerBoss();
        snowQueenModel = new ModelTFSnowQueen();
        minoshroomModel = new ModelTFMinoshroom();
        knightPhantomModel = new ModelTFKnightPhantom2();
        knightPhantomArmorModel = new ModelTFKnightlyArmor(0, 0.5F);
        alphaYetiModel = new ModelTFYetiAlpha().trophySetup();
        questingRamModel = new ModelTFQuestRam();
    }

    @Override
    public void renderTileEntityAt(TileEntity tileentity, double x, double y, double z, float partialTime) {
        TileEntityTFTrophy trophy = (TileEntityTFTrophy) tileentity;

        GL11.glPushMatrix();
        GL11.glDisable(GL11.GL_CULL_FACE);

        int meta = trophy.getBlockMetadata() & 7;

        float rotation = (float) (trophy.func_145906_b() * 360) / 16.0F;
        boolean onGround = true;

        // wall mounted?
        if (meta != 1) {
            switch (meta) {
                case 2 -> onGround = false;
                case 3 -> {
                    onGround = false;
                    rotation = 180.0F;
                }
                case 4 -> {
                    onGround = false;
                    rotation = 270.0F;
                }
                default -> {
                    onGround = false;
                    rotation = 90.0F;
                }
            }
        }

        GL11.glTranslatef((float) x + 0.5F, (float) y + 0.5F, (float) z + 0.5F);

        switch (trophy.func_145904_a()) {
            case 0 -> renderHydraHead(rotation, onGround);
            case 1 -> renderNagaHead(rotation, onGround);
            case 2 -> renderLichHead(rotation, onGround);
            case 3 -> renderUrGhastHead(trophy, rotation, onGround, partialTime);
            case 4 -> renderSnowQueenHead(rotation, onGround);
            case 5 -> renderMinoshroomHead(rotation, onGround);
            case 6 -> renderKnightPhantomHead(rotation, onGround);
            case 7 -> renderAlphaYetiHead(rotation, onGround);
            case 8 -> renderQuestingRamHead(rotation, onGround);
        }

        GL11.glPopMatrix();

    }

    /**
     * Render a hydra head
     */
    private void renderHydraHead(float rotation, boolean onGround) {

        GL11.glScalef(0.25f, 0.25f, 0.25f);

        this.bindTexture(textureLocHydra);

        GL11.glScalef(1f, -1f, -1f);

        // we seem to be getting a 180 degree rotation here
        GL11.glRotatef(rotation, 0F, 1F, 0F);
        GL11.glRotatef(180F, 0F, 1F, 0F);

        GL11.glTranslatef(0, onGround ? 1F : -0F, 1.5F);

        // open mouth?
        hydraHeadModel.openMouthForTrophy(onGround ? 0F : 0.25F);

        // render the hydra head
        hydraHeadModel.render(null, 0.0F, 0.0F, 0.0F, rotation, 0.0F, 0.0625F);
    }

    private void renderNagaHead(float rotation, boolean onGround) {

        GL11.glTranslatef(0, -0.125F, 0);

        GL11.glScalef(0.25f, 0.25f, 0.25f);

        this.bindTexture(textureLocNaga);

        GL11.glScalef(1f, -1f, -1f);

        // we seem to be getting a 180 degree rotation here
        GL11.glRotatef(rotation, 0F, 1F, 0F);
        GL11.glRotatef(180F, 0F, 1F, 0F);

        GL11.glTranslatef(0, onGround ? 1F : -0F, onGround ? 0F : 1F);

        // render the naga head
        nagaHeadModel.render(null, 0.0F, 0.0F, 0.0F, rotation, 0.0F, 0.0625F);
    }

    private void renderLichHead(float rotation, boolean onGround) {

        GL11.glTranslatef(0, 1, 0);

        // GL11.glScalef(0.5f, 0.5f, 0.5f);

        this.bindTexture(textureLocLich);

        GL11.glScalef(1f, -1f, -1f);

        // we seem to be getting a 180 degree rotation here
        GL11.glRotatef(rotation, 0F, 1F, 0F);
        GL11.glRotatef(180F, 0F, 1F, 0F);

        GL11.glTranslatef(0, onGround ? 1.75F : 1.5F, onGround ? 0F : 0.24F);

        // render the lich head
        lichModel.bipedHead.render(0.0625F);
        lichModel.bipedHeadwear.render(0.0625F);
    }

    private void renderUrGhastHead(TileEntityTFTrophy trophy, float rotation, boolean onGround, float partialTime) {

        GL11.glTranslatef(0, 1, 0);

        GL11.glScalef(0.5f, 0.5f, 0.5f);

        this.bindTexture(textureLocUrGhast);

        GL11.glScalef(1f, -1f, -1f);

        // we seem to be getting a 180 degree rotation here
        GL11.glRotatef(rotation, 0F, 1F, 0F);
        GL11.glRotatef(180F, 0F, 1F, 0F);

        GL11.glTranslatef(0, onGround ? 1F : 1F, onGround ? 0F : 0F);

        // render the ur-ghast head
        urGhastModel.render(null, 0.0F, 0, trophy.ticksExisted + partialTime, 0, 0.0F, 0.0625F);
    }

    private void renderSnowQueenHead(float rotation, boolean onGround) {

        GL11.glTranslatef(0, 1, 0);

        // GL11.glScalef(0.5f, 0.5f, 0.5f);

        this.bindTexture(textureLocSnowQueen);

        GL11.glScalef(1f, -1f, -1f);

        // we seem to be getting a 180 degree rotation here
        GL11.glRotatef(rotation, 0F, 1F, 0F);
        GL11.glRotatef(180F, 0F, 1F, 0F);

        GL11.glTranslatef(0, onGround ? 1.5F : 1.25F, onGround ? 0F : 0.24F);

        // render the snow queen head
        snowQueenModel.bipedHead.render(0.0625F);
        snowQueenModel.bipedHeadwear.render(0.0625F);
    }

    private void renderMinoshroomHead(float rotation, boolean onGround) {

        // GL11.glScalef(0.5f, 0.5f, 0.5f);

        this.bindTexture(textureLocMinoshroom);

        GL11.glScalef(1f, -1f, -1f);

        // we seem to be getting a 180 degree rotation here
        GL11.glRotatef(rotation, 0F, 1F, 0F);
        GL11.glRotatef(180F, 0F, 1F, 0F);

        GL11.glTranslatef(0, -0.625f, 0.57f);

        GL11.glTranslatef(0, onGround ? 1.5F : 1.25F, onGround ? 0F : 0.24F);

        // render the minoshroom head
        minoshroomModel.bipedHead.render(0.0625F);
        minoshroomModel.bipedHeadwear.render(0.0625F);
    }

    private void renderKnightPhantomHead(float rotation, boolean onGround) {

        float scale = 0.89f;
        GL11.glScalef(scale, scale, scale);

        GL11.glTranslatef(0, 0.97f, 0);

        this.bindTexture(textureLocKnightPhantom);

        GL11.glScalef(1f, -1f, -1f);

        // we seem to be getting a 180 degree rotation here
        GL11.glRotatef(rotation, 0F, 1F, 0F);
        GL11.glRotatef(180F, 0F, 1F, 0F);

        GL11.glTranslatef(0, onGround ? 1.5F : 1.22F, onGround ? 0F : 0.28F);

        // render the knight phantom head
        knightPhantomModel.bipedHead.render(0.0625F);

        this.bindTexture(textureLocKnightPhantomArmor);

        // render the knight phantom armor
        knightPhantomArmorModel.bipedHead.render(0.0625F);
        knightPhantomArmorModel.bipedHeadwear.render(0.0625F);
    }

    private void renderAlphaYetiHead(float rotation, boolean onGround) {

        float scale = 0.2f;
        GL11.glScalef(scale, scale, scale);

        GL11.glTranslatef(0, -1.8f, 0);

        this.bindTexture(textureLocAlphaYeti);

        GL11.glScalef(1f, -1f, -1f);

        // we seem to be getting a 180 degree rotation here
        GL11.glRotatef(rotation, 0F, 1F, 0F);
        GL11.glRotatef(180F, 0F, 1F, 0F);

        GL11.glTranslatef(0, onGround ? 1.5F : 0.2F, onGround ? 0F : 1.385F);

        // render the alpha yeti head
        alphaYetiModel.bipedBody.render(0.0625F);
    }

    private void renderQuestingRamHead(float rotation, boolean onGround) {

        float scale = 0.65f;
        GL11.glScalef(scale, scale, scale);

        this.bindTexture(textureLocQuestingRam);

        GL11.glScalef(1f, -1f, -1f);

        // we seem to be getting a 180 degree rotation here
        GL11.glRotatef(rotation, 0F, 1F, 0F);
        GL11.glRotatef(180F, 0F, 1F, 0F);

        GL11.glTranslatef(0, -0.25f, 0.67f);

        GL11.glTranslatef(0, onGround ? 1.5F : 1.175F, onGround ? 0.05F : 0.41F);

        // render the questing ram head
        questingRamModel.head.render(0.0625F);
    }

}
