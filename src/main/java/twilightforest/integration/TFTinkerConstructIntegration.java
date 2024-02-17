package twilightforest.integration;

import static net.minecraft.util.EnumChatFormatting.DARK_GREEN;
import static net.minecraft.util.EnumChatFormatting.GOLD;
import static net.minecraft.util.EnumChatFormatting.GREEN;

import net.minecraft.block.Block;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.FillBucketEvent;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.Event.Result;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import mantle.world.WorldHelper;
import tconstruct.TConstruct;
import tconstruct.library.TConstructRegistry;
import tconstruct.library.client.TConstructClientRegistry;
import tconstruct.library.crafting.FluidType;
import tconstruct.library.crafting.LiquidCasting;
import tconstruct.library.crafting.PatternBuilder;
import tconstruct.library.crafting.Smeltery;
import tconstruct.library.tools.ToolCore;
import tconstruct.library.util.IPattern;
import tconstruct.library.weaponry.ArrowShaftMaterial;
import tconstruct.smeltery.TinkerSmeltery;
import tconstruct.tools.TFActiveToolMod;
import tconstruct.tools.TFToolEvents;
import tconstruct.tools.TinkerTools;
import tconstruct.tools.items.TFFletching;
import tconstruct.util.Reference;
import tconstruct.weaponry.TinkerWeaponry;
import twilightforest.TwilightForestMod;
import twilightforest.block.TFBlocks;
import twilightforest.item.TFItems;

public class TFTinkerConstructIntegration {

    public static Item fletching;

    public static Fluid fieryLiquidFluid;
    public static Fluid moltenFieryMetalFluid;
    public static Fluid moltenKnightmetalFluid;
    public static Block fieryLiquid;
    public static Block moltenFieryMetal;
    public static Block moltenKnightmetal;
    // public static Fluid[] fluids = new Fluid[2];
    public static Block[] fluidBlocks = new Block[3];
    public static FluidStack[] liquids;

    public static final class MaterialID {

        public static final int FieryMetal = 42;
        public static final int Knightmetal = 43;
        public static final int NagaScale = 44;
        public static final int Steeleaf = 45;
    }

    public static void registerTinkersConstructIntegration() {
        TFToolEvents toolEvents = new TFToolEvents();
        MinecraftForge.EVENT_BUS.register(toolEvents);
        FMLCommonHandler.instance().bus().register(toolEvents);

        TConstructRegistry.registerActiveToolMod(new TFActiveToolMod());

        // Adding materials requiring smeltery
        if (TinkerSmeltery.smeltery != null) {
            // Fiery liquid (blood, sweat and tears)
            fieryLiquidFluid = TinkerSmeltery.registerFluid("fieryliquid");
            fieryLiquid = fieryLiquidFluid.getBlock();
            FluidType.registerFluidType("FieryLiquid", fieryLiquid, 0, 600, fieryLiquidFluid, false);

            // Fiery metal
            moltenFieryMetalFluid = TinkerSmeltery.registerFluid("fierymetal");
            moltenFieryMetal = moltenFieryMetalFluid.getBlock();
            FluidType.registerFluidType("FieryMetal", TFBlocks.fieryMetalStorage, 0, 600, moltenFieryMetalFluid, true);

            Item materialItem = TFItems.fieryIngot;
            TConstructClientRegistry.addMaterialRenderMapping(MaterialID.FieryMetal, "tinker", "fierymetal", true);
            TConstructRegistry.addToolMaterial(
                    MaterialID.FieryMetal,
                    "FieryMetal",
                    2,
                    250,
                    600,
                    2,
                    1.3F,
                    0,
                    0f,
                    GOLD.toString(),
                    0xDADADA);
            PatternBuilder.instance.registerFullMaterial(
                    new ItemStack((Item) materialItem, 1, 0),
                    2,
                    "FieryMetal",
                    new ItemStack(TinkerTools.toolShard, 1, MaterialID.FieryMetal),
                    new ItemStack(TinkerTools.toolRod, 1, MaterialID.FieryMetal),
                    MaterialID.FieryMetal);

            // Knightmetal
            moltenKnightmetalFluid = TinkerSmeltery.registerFluid("knightmetal");
            moltenKnightmetal = moltenKnightmetalFluid.getBlock();
            FluidType.registerFluidType(
                    "Knightmetal",
                    TFBlocks.knightmetalStorage,
                    0,
                    600,
                    moltenKnightmetalFluid,
                    true);

            materialItem = TFItems.knightMetal;
            TConstructClientRegistry.addMaterialRenderMapping(MaterialID.Knightmetal, "tinker", "knightmetal", true);
            TConstructRegistry.addToolMaterial(
                    MaterialID.Knightmetal,
                    "Knightmetal",
                    2,
                    250,
                    600,
                    2,
                    1.3F,
                    0,
                    0f,
                    GREEN.toString(),
                    0xDADADA);
            PatternBuilder.instance.registerFullMaterial(
                    new ItemStack((Item) materialItem, 1, 0),
                    2,
                    "Knightmetal",
                    new ItemStack(TinkerTools.toolShard, 1, MaterialID.Knightmetal),
                    new ItemStack(TinkerTools.toolRod, 1, MaterialID.Knightmetal),
                    MaterialID.Knightmetal);

            LiquidCasting tableCasting = TConstructRegistry.getTableCasting();

            // Melting blocks
            Smeltery.addMelting(
                    TFBlocks.fieryMetalStorage,
                    0,
                    600,
                    new FluidStack(moltenFieryMetalFluid, TConstruct.blockLiquidValue));
            Smeltery.addMelting(
                    TFBlocks.knightmetalStorage,
                    0,
                    600,
                    new FluidStack(moltenKnightmetalFluid, TConstruct.blockLiquidValue));

            FluidType fieryMetal = FluidType.getFluidType(moltenFieryMetalFluid);
            FluidType knightmetal = FluidType.getFluidType(moltenKnightmetalFluid);

            // Melting ingots
            Smeltery.addMelting(fieryMetal, new ItemStack(TFItems.fieryIngot), 0, TConstruct.ingotLiquidValue);

            Smeltery.addMelting(knightmetal, new ItemStack(TFItems.knightMetal), 0, TConstruct.ingotLiquidValue);
            Smeltery.addMelting(knightmetal, new ItemStack(TFItems.armorShard), 0, TConstruct.nuggetLiquidValue);
            Smeltery.addMelting(knightmetal, new ItemStack(TFItems.shardCluster), 0, TConstruct.ingotLiquidValue);

            // Melting armor
            Smeltery.addMelting(
                    fieryMetal,
                    new ItemStack(TFItems.fieryBoots, 1, 0),
                    0,
                    TConstruct.ingotLiquidValue * 4);
            Smeltery.addMelting(fieryMetal, new ItemStack(TFItems.fieryHelm, 1, 0), 0, TConstruct.ingotLiquidValue * 5);
            Smeltery.addMelting(fieryMetal, new ItemStack(TFItems.fieryLegs, 1, 0), 0, TConstruct.ingotLiquidValue * 7);
            Smeltery.addMelting(
                    fieryMetal,
                    new ItemStack(TFItems.fieryPlate, 1, 0),
                    0,
                    TConstruct.ingotLiquidValue * 8);

            Smeltery.addMelting(
                    knightmetal,
                    new ItemStack(TFItems.knightlyBoots, 1, 0),
                    0,
                    TConstruct.ingotLiquidValue * 4);
            Smeltery.addMelting(
                    knightmetal,
                    new ItemStack(TFItems.knightlyHelm, 1, 0),
                    0,
                    TConstruct.ingotLiquidValue * 5);
            Smeltery.addMelting(
                    knightmetal,
                    new ItemStack(TFItems.knightlyLegs, 1, 0),
                    0,
                    TConstruct.ingotLiquidValue * 7);
            Smeltery.addMelting(
                    knightmetal,
                    new ItemStack(TFItems.knightlyPlate, 1, 0),
                    0,
                    TConstruct.ingotLiquidValue * 8);

            // Melting tools
            Smeltery.addMelting(fieryMetal, new ItemStack(TFItems.fieryPick, 1, 0), 0, TConstruct.ingotLiquidValue * 3);
            Smeltery.addMelting(
                    fieryMetal,
                    new ItemStack(TFItems.fierySword, 1, 0),
                    0,
                    TConstruct.ingotLiquidValue * 2);

            Smeltery.addMelting(
                    knightmetal,
                    new ItemStack(TFItems.knightlyAxe, 1, 0),
                    0,
                    TConstruct.ingotLiquidValue * 3);
            Smeltery.addMelting(
                    knightmetal,
                    new ItemStack(TFItems.knightlyPick, 1, 0),
                    0,
                    TConstruct.ingotLiquidValue * 3);
            Smeltery.addMelting(
                    knightmetal,
                    new ItemStack(TFItems.knightlySword, 1, 0),
                    0,
                    TConstruct.ingotLiquidValue * 2);

            // Melting misc.
            Smeltery.addMelting(
                    knightmetal,
                    new ItemStack(TFItems.knightmetalRing),
                    0,
                    TConstruct.ingotLiquidValue * 4);
            Smeltery.addMelting(
                    knightmetal,
                    new ItemStack(TFItems.chainBlock, 1, 0),
                    0,
                    TConstruct.ingotLiquidValue * 16);

            // Metal toolpart casting
            liquids = new FluidStack[] { new FluidStack(moltenFieryMetalFluid, 1),
                    new FluidStack(moltenKnightmetalFluid, 1) };
            int[] liquidDamage = new int[] { MaterialID.FieryMetal, MaterialID.Knightmetal }; // ItemStack
            // damage
            // value
            int fluidAmount;
            Fluid fs;

            for (int iter = 0; iter < TinkerTools.patternOutputs.length; iter++) {
                if (TinkerTools.patternOutputs[iter] != null) {
                    ItemStack cast = new ItemStack(TinkerSmeltery.metalPattern, 1, iter + 1);
                    ItemStack clay_cast = new ItemStack(TinkerSmeltery.clayPattern, 1, iter + 1);

                    for (int iterTwo = 0; iterTwo < liquids.length; iterTwo++) {
                        fs = liquids[iterTwo].getFluid();
                        fluidAmount = ((IPattern) TinkerSmeltery.metalPattern).getPatternCost(cast)
                                * TConstruct.ingotLiquidValue
                                / 2;
                        ItemStack metalCast = new ItemStack(TinkerTools.patternOutputs[iter], 1, liquidDamage[iterTwo]);
                        tableCasting.addCastingRecipe(metalCast, new FluidStack(fs, fluidAmount), cast, 50);
                        if (isValidClayCast(iter)) {
                            tableCasting
                                    .addCastingRecipe(metalCast, new FluidStack(fs, fluidAmount), clay_cast, true, 50);
                        }
                        Smeltery.addMelting(FluidType.getFluidType(fs), metalCast, 0, fluidAmount);
                    }
                }
            }

            ItemStack ingotcast = new ItemStack(TinkerSmeltery.metalPattern, 1, 0);
            ItemStack ingotcast_clay = new ItemStack(TinkerSmeltery.clayPattern, 1, 0);

            // Metal Casting
            tableCasting.addCastingRecipe(
                    new ItemStack(TFItems.fieryIngot, 1, 0),
                    new FluidStack(moltenFieryMetalFluid, TConstruct.ingotLiquidValue),
                    ingotcast,
                    false,
                    50); // Fiery Metal
            tableCasting.addCastingRecipe(
                    new ItemStack(TFItems.knightMetal, 1, 0),
                    new FluidStack(moltenKnightmetalFluid, TConstruct.ingotLiquidValue),
                    ingotcast,
                    false,
                    50); // Knightmetal

            // Clay Casting
            tableCasting.addCastingRecipe(
                    new ItemStack(TFItems.fieryIngot, 1, 0),
                    new FluidStack(moltenFieryMetalFluid, TConstruct.ingotLiquidValue),
                    ingotcast_clay,
                    true,
                    50); // Fiery Metal
            tableCasting.addCastingRecipe(
                    new ItemStack(TFItems.knightMetal, 1, 0),
                    new FluidStack(moltenKnightmetalFluid, TConstruct.ingotLiquidValue),
                    ingotcast_clay,
                    true,
                    50); // Knightmetal

            LiquidCasting basinCasting = TConstructRegistry.getBasinCasting();
            // Block Casting
            basinCasting.addCastingRecipe(
                    new ItemStack(TFBlocks.fieryMetalStorage),
                    new FluidStack(moltenFieryMetalFluid, TConstruct.blockLiquidValue),
                    null,
                    true,
                    100); // Fiery Metal
            basinCasting.addCastingRecipe(
                    new ItemStack(TFBlocks.knightmetalStorage),
                    new FluidStack(moltenKnightmetalFluid, TConstruct.blockLiquidValue),
                    null,
                    true,
                    100); // Knightmetal

            // Twilight Forest metal weaponry toolparts
            if (TConstruct.pulsar.isPulseLoaded("Tinkers' Weaponry")) {
                for (int i = 0; i < TinkerWeaponry.patternOutputs.length; i++) {
                    ItemStack cast = new ItemStack(TinkerWeaponry.metalPattern, 1, i);
                    for (int iterTwo = 0; iterTwo < liquids.length; iterTwo++) {
                        fs = liquids[iterTwo].getFluid();
                        fluidAmount = TinkerWeaponry.metalPattern.getPatternCost(cast) * TConstruct.ingotLiquidValue
                                / 2;
                        ItemStack metalCast = new ItemStack(TinkerWeaponry.patternOutputs[i], 1, liquidDamage[iterTwo]);
                        tableCasting.addCastingRecipe(metalCast, new FluidStack(fs, fluidAmount), cast, 50);
                        Smeltery.addMelting(FluidType.getFluidType(fs), metalCast, 0, fluidAmount);
                    }
                }
                // Register clay part casting for BowLimbs
                for (int iterTwo = 0; iterTwo < liquids.length; iterTwo++) {
                    fs = liquids[iterTwo].getFluid();
                    ItemStack clay_cast = new ItemStack(TinkerWeaponry.clayPattern, 1, 3);
                    fluidAmount = TinkerWeaponry.clayPattern.getPatternCost(clay_cast) * TConstruct.ingotLiquidValue
                            / 2;
                    tableCasting.addCastingRecipe(
                            new ItemStack(TinkerWeaponry.patternOutputs[3], 1, liquidDamage[iterTwo]),
                            new FluidStack(fs, fluidAmount),
                            clay_cast,
                            true,
                            50);
                }

                ItemStack cast = new ItemStack(TinkerSmeltery.metalPattern, 1, 25);
                ItemStack clay_cast = new ItemStack(TinkerSmeltery.clayPattern, 1, 25);

                for (int iterTwo = 0; iterTwo < liquids.length; iterTwo++) {
                    fs = liquids[iterTwo].getFluid();
                    fluidAmount = ((IPattern) TinkerSmeltery.metalPattern).getPatternCost(cast)
                            * TConstruct.ingotLiquidValue
                            / 2;
                    ItemStack metalCast = new ItemStack(TinkerWeaponry.arrowhead, 1, liquidDamage[iterTwo]);
                    tableCasting.addCastingRecipe(metalCast, new FluidStack(fs, fluidAmount), cast, 50);
                    tableCasting.addCastingRecipe(metalCast, new FluidStack(fs, fluidAmount), clay_cast, true, 50);
                    Smeltery.addMelting(FluidType.getFluidType(fs), metalCast, 0, fluidAmount);
                }

                TConstructRegistry.addBowMaterial(MaterialID.FieryMetal, 54, 5.2f);
                TConstructRegistry.addArrowMaterial(MaterialID.FieryMetal, 3.3F, 0.8F);

                TConstructRegistry.addCustomMaterial(
                        ArrowShaftMaterial.createMaterial(
                                6,
                                TinkerTools.toolRod,
                                MaterialID.FieryMetal,
                                1.0f,
                                1.0f,
                                0.15f,
                                0x866526));

                TConstructRegistry.addBowMaterial(MaterialID.Knightmetal, 54, 5.2f);
                TConstructRegistry.addArrowMaterial(MaterialID.Knightmetal, 3.3F, 0.8F);
            }

            TConstructRegistry.addDefaultToolPartMaterial(MaterialID.FieryMetal);
            TConstructRegistry.addDefaultToolPartMaterial(MaterialID.Knightmetal);

            // Remove certain things from NEI
            if (TwilightForestMod.isNeiLoaded) {
                TFNeiIntegration.hideItem(new ItemStack(TinkerWeaponry.patternOutputs[1], 1, MaterialID.Knightmetal));
                TFNeiIntegration.hideItem(new ItemStack(TinkerWeaponry.patternOutputs[3], 1, MaterialID.Knightmetal));
            }
        }

        // For materials that do not use casting

        // Naga Scale
        Item materialItem = TFItems.nagaScale;
        TConstructClientRegistry.addMaterialRenderMapping(MaterialID.NagaScale, "tinker", "nagascale", true);
        TConstructRegistry.addToolMaterial(
                MaterialID.NagaScale,
                "NagaScale",
                2,
                250,
                600,
                2,
                1.3F,
                0,
                0f,
                DARK_GREEN.toString(),
                0xDADADA);
        PatternBuilder.instance.registerFullMaterial(
                new ItemStack((Item) materialItem, 1, 0),
                2,
                "NagaScale",
                new ItemStack(TinkerTools.toolShard, 1, MaterialID.NagaScale),
                new ItemStack(TinkerTools.toolRod, 1, MaterialID.NagaScale),
                MaterialID.NagaScale);

        // Steeleaf
        materialItem = TFItems.steeleafIngot;
        TConstructClientRegistry.addMaterialRenderMapping(MaterialID.Steeleaf, "tinker", "steeleaf", true);
        TConstructRegistry.addToolMaterial(
                MaterialID.Steeleaf,
                "Steeleaf",
                2,
                250,
                600,
                2,
                1.3F,
                0,
                0f,
                DARK_GREEN.toString(),
                0xDADADA);
        PatternBuilder.instance.registerFullMaterial(
                new ItemStack((Item) materialItem, 1, 0),
                2,
                "Steeleaf",
                new ItemStack(TinkerTools.toolShard, 1, MaterialID.Steeleaf),
                new ItemStack(TinkerTools.toolRod, 1, MaterialID.Steeleaf),
                MaterialID.Steeleaf);

        for (int meta = 0; meta < TinkerTools.patternOutputs.length; meta++) {
            if (TinkerTools.patternOutputs[meta] != null) {
                TConstructRegistry.addPartMapping(
                        TinkerTools.woodPattern,
                        meta + 1,
                        MaterialID.NagaScale,
                        new ItemStack(TinkerTools.patternOutputs[meta], 1, MaterialID.NagaScale));
                TConstructRegistry.addPartMapping(
                        TinkerTools.woodPattern,
                        meta + 1,
                        MaterialID.Steeleaf,
                        new ItemStack(TinkerTools.patternOutputs[meta], 1, MaterialID.Steeleaf));
            }
        }

        // Twilight Forest weaponry toolparts
        if (TConstruct.pulsar.isPulseLoaded("Tinkers' Weaponry")) {
            for (int m = 0; m < TinkerWeaponry.patternOutputs.length; m++) {
                TConstructRegistry.addPartMapping(
                        TinkerWeaponry.woodPattern,
                        m,
                        MaterialID.NagaScale,
                        new ItemStack(TinkerWeaponry.patternOutputs[m], 1, MaterialID.NagaScale));
                TConstructRegistry.addPartMapping(
                        TinkerWeaponry.woodPattern,
                        m,
                        MaterialID.Steeleaf,
                        new ItemStack(TinkerWeaponry.patternOutputs[m], 1, MaterialID.Steeleaf));
            }

            TConstructRegistry.addPartMapping(
                    TinkerTools.woodPattern,
                    25,
                    MaterialID.NagaScale,
                    new ItemStack(TinkerWeaponry.arrowhead, 1, MaterialID.NagaScale));
            TConstructRegistry.addPartMapping(
                    TinkerTools.woodPattern,
                    25,
                    MaterialID.Steeleaf,
                    new ItemStack(TinkerWeaponry.arrowhead, 1, MaterialID.Steeleaf));

            TConstructRegistry.addBowMaterial(MaterialID.NagaScale, 35, 4.75f);
            TConstructRegistry.addArrowMaterial(MaterialID.NagaScale, 1.8F, 0.5F);

            TConstructRegistry.addCustomMaterial(
                    ArrowShaftMaterial
                            .createMaterial(4, TinkerTools.toolRod, MaterialID.NagaScale, 1.0f, 1.0f, 0.15f, 0x866526));

            TConstructRegistry.addBowMaterial(MaterialID.Steeleaf, 35, 4.75f);
            TConstructRegistry.addArrowMaterial(MaterialID.Steeleaf, 1.8F, 0.5F);

            TConstructRegistry.addCustomMaterial(
                    ArrowShaftMaterial
                            .createMaterial(5, TinkerTools.toolRod, MaterialID.Steeleaf, 1.0f, 1.0f, 0.15f, 0x866526));

            // Arrow Fletching Materials
            fletching = new TFFletching().setUnlocalizedName("tconstruct.Fletching");
            GameRegistry.registerItem(fletching, "fletching");

            TConstructRegistry.addToolRecipe(
                    TinkerWeaponry.arrowAmmo,
                    TinkerWeaponry.arrowhead,
                    TinkerWeaponry.partArrowShaft,
                    fletching);
            TConstructRegistry.addToolRecipe(
                    TinkerWeaponry.boltAmmo,
                    TinkerWeaponry.partBolt,
                    TinkerWeaponry.partBolt,
                    fletching);

            TConstructRegistry.addFletchingMaterial(
                    5,
                    2,
                    new ItemStack(TFItems.feather),
                    new ItemStack(fletching, 1, 0),
                    95F,
                    0.05F,
                    1.0f,
                    0xffffff); // Raven Feather

            TConstructRegistry.addFletchingMaterial(
                    6,
                    2,
                    new ItemStack(TFItems.steeleafIngot),
                    new ItemStack(fletching, 1, 1),
                    95F,
                    0.05F,
                    1.0f,
                    0xffffff); // Steeleaf

            ToolCore arrow = TinkerWeaponry.arrowAmmo;
            String pre = Reference.resource(arrow.getDefaultFolder()) + "/";

            String[] shaft = { "wood", "bone", "reed", "blaze", "nagascale", "steeleaf",
                    TinkerSmeltery.smeltery == null ? null : "fierymetal" };
            String[] fletching = { "feather", "leaf", "slime", "blueslime", "slimeleaf", "raven_feather", "steeleaf" };

            // we register different textures for the different parts per index
            for (int i = 0; i < 7; i++) {
                String handletex = pre + shaft[i] + arrow.getIconSuffix(2);
                String acctex = pre + fletching[i] + arrow.getIconSuffix(3);
                arrow.registerAlternatePartPaths(i, new String[] { null, null, handletex, acctex });
                TinkerWeaponry.boltAmmo.registerAlternatePartPaths(i, new String[] { null, null, null, acctex });
            }

            // for bolts too
            pre = Reference.resource(TinkerWeaponry.boltAmmo.getDefaultFolder()) + "/";
            for (int i = 0; i < 7; i++) {
                String acctex = pre + fletching[i] + TinkerWeaponry.boltAmmo.getIconSuffix(3);
                TinkerWeaponry.boltAmmo.registerAlternatePartPaths(i, new String[] { null, null, null, acctex });
            }
        }

        TConstructRegistry.addDefaultToolPartMaterial(MaterialID.NagaScale);
        TConstructRegistry.addDefaultShardMaterial(MaterialID.NagaScale);

        TConstructRegistry.addDefaultToolPartMaterial(MaterialID.Steeleaf);
        TConstructRegistry.addDefaultShardMaterial(MaterialID.Steeleaf);

    }

    @SubscribeEvent
    public void bucketFill(FillBucketEvent evt) {
        if (evt.current.getItem() == Items.bucket && evt.target.typeOfHit == MovingObjectType.BLOCK) {
            int hitX = evt.target.blockX;
            int hitY = evt.target.blockY;
            int hitZ = evt.target.blockZ;

            if (evt.entityPlayer != null
                    && !evt.entityPlayer.canPlayerEdit(hitX, hitY, hitZ, evt.target.sideHit, evt.current)) {
                return;
            }

            Block bID = evt.world.getBlock(hitX, hitY, hitZ);
            for (int id = 0; id < fluidBlocks.length; id++) {
                if (bID == fluidBlocks[id]) {
                    if (evt.entityPlayer.capabilities.isCreativeMode) {
                        WorldHelper.setBlockToAir(evt.world, hitX, hitY, hitZ);
                    } else {
                        WorldHelper.setBlockToAir(evt.world, hitX, hitY, hitZ);
                        evt.setResult(Result.ALLOW);
                        evt.result = new ItemStack(TinkerSmeltery.buckets, 1, 42 + id);
                    }
                }
            }
        }
    }

    public static boolean isValidClayCast(int meta) {
        return meta < 14 || meta == 22 || meta == 25;
    }
}
