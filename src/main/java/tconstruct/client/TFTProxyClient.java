package tconstruct.client;

import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import org.w3c.dom.Document;

import mantle.lib.client.MantleClientRegistry;
import tconstruct.TConstruct;
import tconstruct.common.TProxyCommon;
import tconstruct.library.crafting.ToolBuilder;
import tconstruct.tools.TinkerTools;
import tconstruct.tools.items.TFManualInfo;
import twilightforest.integration.TFTinkerConstructIntegration;
import twilightforest.item.TFItems;

public class TFTProxyClient extends TProxyCommon {

    public void initialize() {
        readManuals();
    }

    public static Document twilightMaterials;
    public static TFManualInfo manualData;

    public void readManuals() {
        initManualIcons();
        // if (!Loader.isModLoaded("dreamcraft")) {
        readTinkersConstructManuals();
        // }
    }

    private void readTinkersConstructManuals() {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        String CurrentLanguage = Minecraft.getMinecraft().getLanguageManager().getCurrentLanguage().getLanguageCode();
        Document twilightMaterials_cl = readManual(
                "/assets/tinker/manuals/" + CurrentLanguage + "/twilightMaterials.xml",
                dbFactory);

        twilightMaterials = twilightMaterials_cl != null ? twilightMaterials_cl
                : readManual("/assets/tinker/manuals/en_US/twilightMaterials.xml", dbFactory);

        manualData = new TFManualInfo();
    }

    Document readManual(String location, DocumentBuilderFactory dbFactory) {
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
        MantleClientRegistry
                .registerManualIcon("ravenfletching", new ItemStack(TFTinkerConstructIntegration.fletching));
        MantleClientRegistry.registerManualIcon("ravenfeather", new ItemStack(TFItems.feather));
        MantleClientRegistry.registerManualIcon("nagascale", new ItemStack(TFItems.nagaScale));
        MantleClientRegistry.registerManualIcon("steeleaf", new ItemStack(TFItems.steeleafIngot));
        MantleClientRegistry.registerManualIcon("fierybucket", new ItemStack(TFTinkerConstructIntegration.buckets));
        MantleClientRegistry.registerManualIcon("fieryblood", new ItemStack(TFItems.fieryBlood));
        MantleClientRegistry.registerManualIcon("fierytears", new ItemStack(TFItems.fieryTears));
        MantleClientRegistry.registerManualIcon("fieryingot", new ItemStack(TFItems.fieryIngot));
        MantleClientRegistry.registerManualIcon("knightlyingot", new ItemStack(TFItems.knightMetal));

        MantleClientRegistry.registerManualIcon("bone", new ItemStack(Items.bone));
        MantleClientRegistry.registerManualIcon("obsidian", new ItemStack(Blocks.obsidian));
        MantleClientRegistry.registerManualIcon("netherrack", new ItemStack(Blocks.netherrack));
    }

    public void initManualRecipes() {}

    void initManualPages() {}
}
