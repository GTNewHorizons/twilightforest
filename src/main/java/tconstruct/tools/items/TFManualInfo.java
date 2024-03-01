package tconstruct.tools.items;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

import org.w3c.dom.Document;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import mantle.books.BookData;
import mantle.books.BookDataStore;
import twilightforest.integration.TFTinkerConstructIntegration;

public class TFManualInfo {

    BookData twilightMaterials = new BookData();

    public TFManualInfo() {
        Side side = FMLCommonHandler.instance().getEffectiveSide();
        twilightMaterials = initManual(
                twilightMaterials,
                "tconstruct.manual.twilightmaterials",
                "\u00a7o" + StatCollector.translateToLocal("manualTF.tooltip"),
                side == Side.CLIENT ? TFTinkerConstructIntegration.twilightMaterials : null,
                "tinker:tinkerbook_diary");
    }

    public BookData initManual(BookData data, String unlocName, String toolTip, Document xmlDoc, String itemImage) {
        data.unlocalizedName = unlocName;
        data.toolTip = unlocName;
        data.modID = "TConstruct";
        data.itemImage = new ResourceLocation(data.modID, itemImage);
        data.doc = xmlDoc;
        BookDataStore.addBook(data);
        return data;
    }
}
