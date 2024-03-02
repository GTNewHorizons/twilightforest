package twilightforest.integration;

import static net.minecraft.util.EnumChatFormatting.DARK_GREEN;
import static net.minecraft.util.EnumChatFormatting.GOLD;
import static net.minecraft.util.EnumChatFormatting.GREEN;

import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidContainerRegistry.FluidContainerData;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;

import org.w3c.dom.Document;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.registry.GameRegistry;
import mantle.blocks.BlockUtils;
import mantle.lib.client.MantleClientRegistry;
import tconstruct.TConstruct;
import tconstruct.blocks.TFFieryEssence;
import tconstruct.library.TConstructRegistry;
import tconstruct.library.client.TConstructClientRegistry;
import tconstruct.library.crafting.FluidType;
import tconstruct.library.crafting.LiquidCasting;
import tconstruct.library.crafting.PatternBuilder;
import tconstruct.library.crafting.Smeltery;
import tconstruct.library.crafting.ToolBuilder;
import tconstruct.library.tools.ToolCore;
import tconstruct.library.util.IPattern;
import tconstruct.library.weaponry.ArrowShaftMaterial;
import tconstruct.smeltery.TinkerSmeltery;
import tconstruct.smeltery.items.TFFilledBucket;
import tconstruct.tools.TFActiveToolMod;
import tconstruct.tools.TFToolEvents;
import tconstruct.tools.TinkerTools;
import tconstruct.tools.items.TFFletching;
import tconstruct.tools.items.TFManual;
import tconstruct.tools.items.TFManualInfo;
import tconstruct.tools.items.TFMaterialItem;
import tconstruct.util.Reference;
import tconstruct.util.config.PHConstruct;
import tconstruct.weaponry.TinkerWeaponry;
import twilightforest.block.TFBlocks;
import twilightforest.item.TFItems;

public class TFTinkerConstructIntegration {

    public static boolean hasClay = GameRegistry.findItem("TConstruct", "clayPattern") != null;

    public static Item fletching;
    public static Item materials;
    public static Item buckets;
    public static Item manualBook;

    public static Fluid fieryEssenceFluid;
    public static Fluid moltenFieryMetalFluid;
    public static Fluid moltenKnightmetalFluid;
    public static Block fieryEssence;
    public static Block moltenFieryMetal;
    public static Block moltenKnightmetal;
    public static Fluid[] fluids = new Fluid[3];
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
            // Register buckets
            buckets = new TFFilledBucket(BlockUtils.getBlockFromItem(buckets));
            GameRegistry.registerItem(buckets, "buckets");

            // Register nuggets and rods
            materials = new TFMaterialItem().setUnlocalizedName("tconstruct.Materials");
            GameRegistry.registerItem(materials, "materials");

            String[] materialStrings = { "nuggetFiery", "nuggetKnightmetal" };

            for (int i = 0; i < materialStrings.length; i++) {
                TConstructRegistry.addItemStackToDirectory(materialStrings[i], new ItemStack(materials, 1, i));
            }

            OreDictionary.registerOre("nuggetFiery", new ItemStack(TinkerTools.materials, 1, 0));
            OreDictionary.registerOre("nuggetKnightmetal", new ItemStack(TinkerTools.materials, 1, 1));

            GameRegistry.addRecipe(
                    new ItemStack(TFItems.fieryIngot, 1, 0),
                    new Object[] { "###", "###", "###", '#', new ItemStack(materials, 1, 0) });
            GameRegistry.addRecipe(
                    new ItemStack(TFItems.knightMetal, 1, 0),
                    new Object[] { "###", "###", "###", '#', new ItemStack(materials, 1, 1) });

            GameRegistry.addShapelessRecipe(new ItemStack(materials, 9, 0), new Object[] { TFItems.fieryIngot });
            GameRegistry.addShapelessRecipe(new ItemStack(materials, 9, 1), new Object[] { TFItems.knightMetal });

            String[] matNames = { "FieryMetal", "Knightmetal" };
            for (int i = 0; i < matNames.length; i++) {
                OreDictionary.registerOre(matNames[i].toLowerCase() + "Rod", new ItemStack(TinkerTools.toolRod, 1, i));
                OreDictionary.registerOre("rod" + matNames[i], new ItemStack(TinkerTools.toolRod, 1, i));
            }

            // Fiery liquid (blood, sweat and tears)
            fieryEssenceFluid = new Fluid("fiery_essence");
            if (!FluidRegistry.registerFluid(fieryEssenceFluid))
                fieryEssenceFluid = FluidRegistry.getFluid("fiery_essence");
            fieryEssence = new TFFieryEssence(fieryEssenceFluid, Material.lava)
                    .setCreativeTab(TConstructRegistry.blockTab).setBlockName("fiery_essence");
            GameRegistry.registerBlock(fieryEssence, "fiery_essence");
            fieryEssenceFluid.setBlock(fieryEssence);
            FluidType.registerFluidType("FieryEssence", fieryEssence, 0, 1000, fieryEssenceFluid, false);
            Smeltery.addSmelteryFuel(fieryEssenceFluid, 1000, 80);

            // Fiery metal
            moltenFieryMetalFluid = TinkerSmeltery.registerFluid("fierymetal");
            moltenFieryMetal = moltenFieryMetalFluid.getBlock().setCreativeTab(TConstructRegistry.blockTab);
            FluidType.registerFluidType("FieryMetal", TFBlocks.fieryMetalStorage, 0, 600, moltenFieryMetalFluid, true);

            Item materialItem = TFItems.fieryIngot;
            TConstructClientRegistry.addMaterialRenderMapping(MaterialID.FieryMetal, "tinker", "fierymetal", true);
            TConstructRegistry.addToolMaterial(
                    MaterialID.FieryMetal,
                    "FieryMetal",
                    3,
                    720, // TiC2 value untouched
                    720, // TiC2 value untouched
                    3,
                    1.2F, // Is 0.8 in TiC2, but in TiC1 metals' modifiers are always ≥1.0, so I used a netherrack value
                          // to go with the fiery theme
                    0,
                    0f,
                    GOLD.toString(),
                    0x3C2323);
            TConstructRegistry.toolMaterialStrings.put(
                    StatCollector.translateToLocal("material.fierymetal"),
                    TConstructRegistry.toolMaterials.get(MaterialID.FieryMetal)); // For the tutorial book to work
                                                                                  // properly on languages other than
                                                                                  // English
            PatternBuilder.instance.registerFullMaterial(
                    new ItemStack((Item) materialItem, 1, 0),
                    2,
                    "FieryMetal",
                    new ItemStack(TinkerTools.toolShard, 1, MaterialID.FieryMetal),
                    new ItemStack(TinkerTools.toolRod, 1, MaterialID.FieryMetal),
                    MaterialID.FieryMetal);

            // Knightmetal
            moltenKnightmetalFluid = TinkerSmeltery.registerFluid("knightmetal");
            moltenKnightmetal = moltenKnightmetalFluid.getBlock().setCreativeTab(TConstructRegistry.blockTab);
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
                    4,
                    900, // TiC2 value untouched
                    700, // TiC2 value untouched
                    3, // The converted value was 2.75. Might have to decrease later
                    1.25F, // TiC2 value untouched
                    0,
                    0f,
                    GREEN.toString(),
                    0xDBEEC1);
            TConstructRegistry.toolMaterialStrings.put(
                    StatCollector.translateToLocal("material.knightmetal"),
                    TConstructRegistry.toolMaterials.get(MaterialID.Knightmetal)); // For the tutorial book to work
                                                                                   // properly on languages other than
                                                                                   // English
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

            FluidType fieryEssenceFluidType = FluidType.getFluidType(fieryEssenceFluid);
            FluidType fieryMetalFluidType = FluidType.getFluidType(moltenFieryMetalFluid);
            FluidType knightmetalFluidType = FluidType.getFluidType(moltenKnightmetalFluid);

            // Fiery essence
            Smeltery.addMelting(
                    fieryEssenceFluidType,
                    new ItemStack(TFItems.fieryTears),
                    -1000,
                    TConstruct.ingotLiquidValue);
            Smeltery.addMelting(
                    fieryEssenceFluidType,
                    new ItemStack(TFItems.fieryBlood),
                    -1000,
                    TConstruct.ingotLiquidValue);

            // Melting nuggets
            Smeltery.addMelting(fieryMetalFluidType, new ItemStack(materials, 1, 0), 0, TConstruct.nuggetLiquidValue);

            Smeltery.addMelting(knightmetalFluidType, new ItemStack(materials, 1, 1), 0, TConstruct.nuggetLiquidValue);
            Smeltery.addMelting(
                    knightmetalFluidType,
                    new ItemStack(TFItems.armorShard),
                    0,
                    TConstruct.nuggetLiquidValue);

            // Melting ingots
            Smeltery.addMelting(fieryMetalFluidType, new ItemStack(TFItems.fieryIngot), 0, TConstruct.ingotLiquidValue);

            Smeltery.addMelting(
                    knightmetalFluidType,
                    new ItemStack(TFItems.knightMetal),
                    0,
                    TConstruct.ingotLiquidValue);
            Smeltery.addMelting(
                    knightmetalFluidType,
                    new ItemStack(TFItems.shardCluster),
                    0,
                    TConstruct.ingotLiquidValue);

            // Melting armor
            Smeltery.addMelting(
                    fieryMetalFluidType,
                    new ItemStack(TFItems.fieryBoots, 1, 0),
                    0,
                    TConstruct.ingotLiquidValue * 4);
            Smeltery.addMelting(
                    fieryMetalFluidType,
                    new ItemStack(TFItems.fieryHelm, 1, 0),
                    0,
                    TConstruct.ingotLiquidValue * 5);
            Smeltery.addMelting(
                    fieryMetalFluidType,
                    new ItemStack(TFItems.fieryLegs, 1, 0),
                    0,
                    TConstruct.ingotLiquidValue * 7);
            Smeltery.addMelting(
                    fieryMetalFluidType,
                    new ItemStack(TFItems.fieryPlate, 1, 0),
                    0,
                    TConstruct.ingotLiquidValue * 8);

            Smeltery.addMelting(
                    knightmetalFluidType,
                    new ItemStack(TFItems.knightlyBoots, 1, 0),
                    0,
                    TConstruct.ingotLiquidValue * 4);
            Smeltery.addMelting(
                    knightmetalFluidType,
                    new ItemStack(TFItems.knightlyHelm, 1, 0),
                    0,
                    TConstruct.ingotLiquidValue * 5);
            Smeltery.addMelting(
                    knightmetalFluidType,
                    new ItemStack(TFItems.knightlyLegs, 1, 0),
                    0,
                    TConstruct.ingotLiquidValue * 7);
            Smeltery.addMelting(
                    knightmetalFluidType,
                    new ItemStack(TFItems.knightlyPlate, 1, 0),
                    0,
                    TConstruct.ingotLiquidValue * 8);

            // Melting tools
            Smeltery.addMelting(
                    fieryMetalFluidType,
                    new ItemStack(TFItems.fieryPick, 1, 0),
                    0,
                    TConstruct.ingotLiquidValue * 3);
            Smeltery.addMelting(
                    fieryMetalFluidType,
                    new ItemStack(TFItems.fierySword, 1, 0),
                    0,
                    TConstruct.ingotLiquidValue * 2);

            Smeltery.addMelting(
                    knightmetalFluidType,
                    new ItemStack(TFItems.knightlyAxe, 1, 0),
                    0,
                    TConstruct.ingotLiquidValue * 3);
            Smeltery.addMelting(
                    knightmetalFluidType,
                    new ItemStack(TFItems.knightlyPick, 1, 0),
                    0,
                    TConstruct.ingotLiquidValue * 3);
            Smeltery.addMelting(
                    knightmetalFluidType,
                    new ItemStack(TFItems.knightlySword, 1, 0),
                    0,
                    TConstruct.ingotLiquidValue * 2);

            // Melting misc.
            Smeltery.addMelting(
                    knightmetalFluidType,
                    new ItemStack(TFItems.knightmetalRing),
                    0,
                    TConstruct.ingotLiquidValue * 4);
            Smeltery.addMelting(
                    knightmetalFluidType,
                    new ItemStack(TFItems.chainBlock, 1, 0),
                    0,
                    TConstruct.ingotLiquidValue * 16);

            // Alloy recipes
            Smeltery.addAlloyMixing(
                    new FluidStack(moltenFieryMetalFluid, 1),
                    new FluidStack(fieryEssenceFluid, 1),
                    new FluidStack(TinkerSmeltery.moltenIronFluid, 1));

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
                    ItemStack clay_cast = hasClay ? new ItemStack(TinkerSmeltery.clayPattern, 1, iter + 1) : null;
                    fluidAmount = ((IPattern) TinkerSmeltery.metalPattern).getPatternCost(cast)
                            * TConstruct.ingotLiquidValue
                            / 2;

                    for (int iterTwo = 0; iterTwo < liquids.length; iterTwo++) {
                        fs = liquids[iterTwo].getFluid();
                        ItemStack metalCast = new ItemStack(TinkerTools.patternOutputs[iter], 1, liquidDamage[iterTwo]);
                        tableCasting.addCastingRecipe(metalCast, new FluidStack(fs, fluidAmount), cast, 50);
                        if (hasClay && isValidClayCast(iter)) {
                            tableCasting
                                    .addCastingRecipe(metalCast, new FluidStack(fs, fluidAmount), clay_cast, true, 50);
                        }
                        Smeltery.addMelting(FluidType.getFluidType(fs), metalCast, 0, fluidAmount);
                    }

                    tableCasting.addCastingRecipe(
                            new ItemStack(TinkerTools.patternOutputs[iter], 1, MaterialID.FieryMetal),
                            new FluidStack(fieryEssenceFluid, fluidAmount),
                            new ItemStack(TinkerTools.patternOutputs[iter], 1, TinkerTools.MaterialID.Iron),
                            true,
                            50);
                }
            }

            // Fluids registry
            fluids[0] = fieryEssenceFluid;
            fluids[1] = moltenFieryMetalFluid;
            fluids[2] = moltenKnightmetalFluid;
            fluidBlocks[0] = fieryEssence;
            fluidBlocks[1] = moltenFieryMetal;
            fluidBlocks[2] = moltenKnightmetal;

            ItemStack bucket = new ItemStack(Items.bucket);
            for (int i = 0; i < fluids.length; i++) {
                // Buckets casting
                tableCasting.addCastingRecipe(
                        new ItemStack(buckets, 1, i),
                        new FluidStack(fluids[i], FluidContainerRegistry.BUCKET_VOLUME),
                        bucket,
                        true,
                        10);

                // Bucket container filling
                FluidContainerRegistry.registerFluidContainer(
                        new FluidContainerData(new FluidStack(fluids[i], 1000), new ItemStack(buckets, 1, i), bucket));
            }

            // Fiery essence container filling
            FluidContainerRegistry.registerFluidContainer(
                    new FluidContainerData(
                            new FluidStack(fieryEssenceFluid, TConstruct.ingotLiquidValue),
                            new ItemStack(TFItems.fieryTears),
                            new ItemStack(Items.glass_bottle)));
            FluidContainerRegistry.registerFluidContainer(
                    new FluidContainerData(
                            new FluidStack(fieryEssenceFluid, TConstruct.ingotLiquidValue),
                            new ItemStack(TFItems.fieryBlood),
                            new ItemStack(Items.glass_bottle)));

            ItemStack ingotcast = new ItemStack(TinkerSmeltery.metalPattern, 1, 0);
            ItemStack ingotcast_clay = hasClay ? new ItemStack(TinkerSmeltery.clayPattern, 1, 0) : null;

            // Patterns casting
            Item[] ingots = { TFItems.fieryIngot, TFItems.ironwoodIngot, TFItems.knightMetal };
            for (Item ingot : ingots) {
                tableCasting.addCastingRecipe(
                        ingotcast,
                        new FluidStack(TinkerSmeltery.moltenAlubrassFluid, TConstruct.ingotLiquidValue),
                        new ItemStack(ingot),
                        false,
                        50);
                if (!PHConstruct.removeGoldCastRecipes) tableCasting.addCastingRecipe(
                        ingotcast,
                        new FluidStack(TinkerSmeltery.moltenGoldFluid, TConstruct.ingotLiquidValue * 2),
                        new ItemStack(ingot),
                        false,
                        50);
            }

            // Nuggets Casting
            ItemStack nuggetcast = new ItemStack(TinkerSmeltery.metalPattern, 1, 27);
            ItemStack nuggetcast_clay = hasClay ? new ItemStack(TinkerSmeltery.clayPattern, 1, 27) : null;
            for (int i = 0; i < materialStrings.length; i++) {
                tableCasting.addCastingRecipe(
                        nuggetcast,
                        new FluidStack(TinkerSmeltery.moltenAlubrassFluid, TConstruct.ingotLiquidValue),
                        new ItemStack(materials, 1, i),
                        false,
                        50);
                if (!PHConstruct.removeGoldCastRecipes) tableCasting.addCastingRecipe(
                        nuggetcast,
                        new FluidStack(TinkerSmeltery.moltenGoldFluid, TConstruct.ingotLiquidValue * 2),
                        new ItemStack(materials, 1, i),
                        false,
                        50);
            }
            tableCasting.addCastingRecipe(
                    new ItemStack(materials, 1, 0),
                    new FluidStack(fieryEssenceFluid, TConstruct.nuggetLiquidValue),
                    new ItemStack(TinkerTools.materials, 1, 19),
                    true,
                    50); // Iron -> Fiery Metal
            tableCasting.addCastingRecipe(
                    new ItemStack(materials, 1, 0),
                    new FluidStack(moltenFieryMetalFluid, TConstruct.nuggetLiquidValue),
                    nuggetcast,
                    40); // Fiery Metal
            tableCasting.addCastingRecipe(
                    new ItemStack(materials, 1, 1),
                    new FluidStack(moltenKnightmetalFluid, TConstruct.nuggetLiquidValue),
                    nuggetcast,
                    40); // Knightmetal
            if (hasClay) {
                tableCasting.addCastingRecipe(
                        new ItemStack(materials, 1, 0),
                        new FluidStack(moltenFieryMetalFluid, TConstruct.nuggetLiquidValue),
                        nuggetcast_clay,
                        true,
                        40); // Fiery Metal
                tableCasting.addCastingRecipe(
                        new ItemStack(materials, 1, 1),
                        new FluidStack(moltenKnightmetalFluid, TConstruct.nuggetLiquidValue),
                        nuggetcast_clay,
                        true,
                        40); // Knightmetal
            }

            // Ingots Casting
            tableCasting.addCastingRecipe(
                    new ItemStack(TFItems.fieryBlood),
                    new FluidStack(fieryEssenceFluid, TConstruct.ingotLiquidValue),
                    new ItemStack(Items.glass_bottle),
                    true,
                    50); // Fiery Essence
            tableCasting.addCastingRecipe(
                    new ItemStack(TFItems.fieryIngot),
                    new FluidStack(fieryEssenceFluid, TConstruct.ingotLiquidValue),
                    new ItemStack(Items.iron_ingot),
                    true,
                    50); // Iron -> Fiery Metal
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
            if (hasClay) {
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
            }

            LiquidCasting basinCasting = TConstructRegistry.getBasinCasting();

            // Block Casting
            basinCasting.addCastingRecipe(
                    new ItemStack(TFBlocks.fieryMetalStorage),
                    new FluidStack(fieryEssenceFluid, TConstruct.blockLiquidValue),
                    new ItemStack(Blocks.iron_block),
                    true,
                    100); // Iron -> Fiery Metal
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
                    fluidAmount = TinkerWeaponry.metalPattern.getPatternCost(cast) * TConstruct.ingotLiquidValue / 2;
                    for (int iterTwo = 0; iterTwo < liquids.length; iterTwo++) {
                        fs = liquids[iterTwo].getFluid();
                        ItemStack metalCast = new ItemStack(TinkerWeaponry.patternOutputs[i], 1, liquidDamage[iterTwo]);
                        tableCasting.addCastingRecipe(metalCast, new FluidStack(fs, fluidAmount), cast, 50);
                        Smeltery.addMelting(FluidType.getFluidType(fs), metalCast, 0, fluidAmount);
                    }

                    tableCasting.addCastingRecipe(
                            new ItemStack(TinkerWeaponry.patternOutputs[i], 1, MaterialID.FieryMetal),
                            new FluidStack(fieryEssenceFluid, fluidAmount),
                            new ItemStack(TinkerWeaponry.patternOutputs[i], 1, TinkerTools.MaterialID.Iron),
                            true,
                            50);
                }
                // Register clay part casting for BowLimbs
                if (hasClay) {
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
                }

                ItemStack cast = new ItemStack(TinkerSmeltery.metalPattern, 1, 25);
                ItemStack clay_cast = hasClay ? new ItemStack(TinkerSmeltery.clayPattern, 1, 25) : null;
                fluidAmount = ((IPattern) TinkerSmeltery.metalPattern).getPatternCost(cast)
                        * TConstruct.ingotLiquidValue
                        / 2;

                for (int iterTwo = 0; iterTwo < liquids.length; iterTwo++) {
                    fs = liquids[iterTwo].getFluid();
                    ItemStack metalCast = new ItemStack(TinkerWeaponry.arrowhead, 1, liquidDamage[iterTwo]);
                    tableCasting.addCastingRecipe(metalCast, new FluidStack(fs, fluidAmount), cast, 50);
                    if (hasClay)
                        tableCasting.addCastingRecipe(metalCast, new FluidStack(fs, fluidAmount), clay_cast, true, 50);
                    Smeltery.addMelting(FluidType.getFluidType(fs), metalCast, 0, fluidAmount);
                }

                tableCasting.addCastingRecipe(
                        new ItemStack(TinkerWeaponry.arrowhead, 1, MaterialID.FieryMetal),
                        new FluidStack(fieryEssenceFluid, fluidAmount),
                        new ItemStack(TinkerWeaponry.arrowhead, 1, TinkerTools.MaterialID.Iron),
                        true,
                        50);

                TConstructRegistry.addBowMaterial(MaterialID.FieryMetal, 54, 5.2f);
                TConstructRegistry.addArrowMaterial(MaterialID.FieryMetal, 3.3F, 0.8F);

                TConstructRegistry.addCustomMaterial(
                        ArrowShaftMaterial.createMaterial(
                                6,
                                TinkerTools.toolRod,
                                MaterialID.FieryMetal,
                                1.2f, // Blaze rod stats
                                2.7f, // Blaze rod stats * 3. That's a metal after all
                                0.08f, // Blaze rod stats
                                0xfff32d));

                TConstructRegistry.addBowMaterial(MaterialID.Knightmetal, 54, 5.2f);
                TConstructRegistry.addArrowMaterial(MaterialID.Knightmetal, 3.3F, 0.8F);
            }

            TConstructRegistry.addDefaultToolPartMaterial(MaterialID.FieryMetal);
            TConstructRegistry.addDefaultToolPartMaterial(MaterialID.Knightmetal);
        }

        // Register rods
        String[] matNames = { "NagaScale", "Steeleaf" };
        for (int i = 0; i < matNames.length; i++) {
            OreDictionary.registerOre(matNames[i].toLowerCase() + "Rod", new ItemStack(TinkerTools.toolRod, 1, i));
            OreDictionary.registerOre("rod" + matNames[i], new ItemStack(TinkerTools.toolRod, 1, i));
        }

        // For materials that do not use casting

        // Naga Scale
        Item materialItem = TFItems.nagaScale;
        TConstructClientRegistry.addMaterialRenderMapping(MaterialID.NagaScale, "tinker", "nagascale", true);
        TConstructRegistry.addToolMaterial(
                MaterialID.NagaScale,
                "NagaScale",
                1,
                460, // TiC2 value untouched
                890, // TiC2 value untouched
                2,
                0.5F, // Wasn't originally intended to use as a handle + mining level is like that of a stone
                0,
                0f,
                DARK_GREEN.toString(),
                0x53763B);
        TConstructRegistry.toolMaterialStrings.put(
                StatCollector.translateToLocal("material.nagascale"),
                TConstructRegistry.toolMaterials.get(MaterialID.NagaScale)); // For the tutorial book to work properly
                                                                             // on languages other than English
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
                180, // TiC2 value untouched
                700, // TiC2 value untouched
                3, // Converted value - 2.75
                0.8F, // Left untouched since a handle made of leaves is not a very bright idea + it gives imbalance
                      // regenerating power
                0,
                0f,
                DARK_GREEN.toString(),
                0x1F3716);
        TConstructRegistry.toolMaterialStrings.put(
                StatCollector.translateToLocal("material.steeleaf"),
                TConstructRegistry.toolMaterials.get(MaterialID.Steeleaf)); // For the tutorial book to work properly on
                                                                            // languages other than English
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
                    ArrowShaftMaterial.createMaterial(
                            4,
                            TinkerTools.toolRod,
                            MaterialID.NagaScale,
                            1.4f, // TiC2 value untouched
                            1.8f, // Bone stats. Scales are close to bones after all
                            0.02f, // Bone stats
                            0x53763B));

            TConstructRegistry.addBowMaterial(MaterialID.Steeleaf, 35, 4.75f);
            TConstructRegistry.addArrowMaterial(MaterialID.Steeleaf, 1.8F, 0.5F);

            TConstructRegistry.addCustomMaterial(
                    ArrowShaftMaterial.createMaterial(
                            5,
                            TinkerTools.toolRod,
                            MaterialID.Steeleaf,
                            0.6f, // TiC2 value untouched. Not a good idea to make arrows out of leaves, but they do
                                  // regenerate
                            0.5f, // Reed stats. Leaves are very light
                            0.15f, // Stick stats. I don't think that it should break more or less often
                            0x1F3716));

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

        // Register books
        manualBook = new TFManual();
        GameRegistry.registerItem(manualBook, "manualBook");
        readManuals();

        GameRegistry.addShapelessRecipe(
                new ItemStack(manualBook),
                new ItemStack(TinkerTools.blankPattern, 1, 0),
                new ItemStack(Items.paper),
                new ItemStack(TFItems.magicMapFocus));
    }

    public static boolean isValidClayCast(int meta) {
        return meta < 14 || meta == 22 || meta == 25;
    }

    // Registering documents
    public static Document twilightMaterials;
    public static TFManualInfo manualData;

    public static void readManuals() {
        initManualIcons();
        // if (!Loader.isModLoaded("dreamcraft")) {
        readTinkersConstructManuals();
        // }
    }

    private static void readTinkersConstructManuals() {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        String CurrentLanguage = Minecraft.getMinecraft().getLanguageManager().getCurrentLanguage().getLanguageCode();
        Document twilightMaterials_cl = readManual(
                "/assets/tinker/manuals/" + CurrentLanguage + "/twilightMaterials.xml",
                dbFactory);

        twilightMaterials = twilightMaterials_cl != null ? twilightMaterials_cl
                : readManual("/assets/tinker/manuals/en_US/twilightMaterials.xml", dbFactory);

        manualData = new TFManualInfo();
    }

    static Document readManual(String location, DocumentBuilderFactory dbFactory) {
        try {
            InputStream stream = TConstruct.class.getResourceAsStream(location);
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(stream);
            doc.getDocumentElement().normalize();
            return doc;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void initManualIcons() {
        // ToolIcons
        MantleClientRegistry.registerManualIcon(
                "nagapick",
                ToolBuilder.instance.buildTool(
                        new ItemStack(TinkerTools.pickaxeHead, 1, 44),
                        new ItemStack(TinkerTools.toolRod, 1, 0),
                        new ItemStack(TinkerTools.binding, 1, 44),
                        ""));
        MantleClientRegistry.registerManualIcon(
                "leafaxe",
                ToolBuilder.instance.buildTool(
                        new ItemStack(TinkerTools.hatchetHead, 1, 45),
                        new ItemStack(TinkerTools.toolRod, 1, 0),
                        null,
                        ""));
        MantleClientRegistry.registerManualIcon(
                "fierysword",
                ToolBuilder.instance.buildTool(
                        new ItemStack(TinkerTools.swordBlade, 1, 42),
                        new ItemStack(TinkerTools.toolRod, 1, 42),
                        new ItemStack(TinkerTools.wideGuard, 1, 42),
                        ""));
        MantleClientRegistry.registerManualIcon(
                "knightlyshovel",
                ToolBuilder.instance.buildTool(
                        new ItemStack(TinkerTools.shovelHead, 1, 43),
                        new ItemStack(TinkerTools.toolRod, 1, 0),
                        null,
                        ""));

        // Items
        MantleClientRegistry.registerManualIcon("ravenfletching", new ItemStack(fletching));
        MantleClientRegistry.registerManualIcon("ravenfeather", new ItemStack(TFItems.feather));
        MantleClientRegistry.registerManualIcon("nagascale", new ItemStack(TFItems.nagaScale));
        MantleClientRegistry.registerManualIcon("steeleaf", new ItemStack(TFItems.steeleafIngot));
        MantleClientRegistry.registerManualIcon("fierybucket", new ItemStack(buckets));
        MantleClientRegistry.registerManualIcon("fieryblood", new ItemStack(TFItems.fieryBlood));
        MantleClientRegistry.registerManualIcon("fierytears", new ItemStack(TFItems.fieryTears));
        MantleClientRegistry.registerManualIcon("fieryingot", new ItemStack(TFItems.fieryIngot));
        MantleClientRegistry.registerManualIcon("knightlyingot", new ItemStack(TFItems.knightMetal));

        MantleClientRegistry.registerManualIcon("bone", new ItemStack(Items.bone));
        MantleClientRegistry.registerManualIcon("obsidian", new ItemStack(Blocks.obsidian));
        MantleClientRegistry.registerManualIcon("netherrack", new ItemStack(Blocks.netherrack));
    }
}
