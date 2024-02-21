package tconstruct.tools.items;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mantle.books.BookData;
import mantle.client.gui.GuiManual;
import mantle.items.abstracts.CraftingItem;
import tconstruct.TConstruct;
import tconstruct.library.TConstructRegistry;
import twilightforest.integration.TFTinkerConstructIntegration;

public class TFManual extends CraftingItem {

    static String[] name = new String[] { "twilightMaterials" };
    static String[] textureName = new String[] { "tinkerbook_twilight" };

    public TFManual() {
        super(name, textureName, "", "tinker", TConstructRegistry.materialTab);
        setUnlocalizedName("tconstruct.manual");
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if (world.isRemote) {
            openBook(stack, world, player);
        }
        return stack;
    }

    @SideOnly(Side.CLIENT)
    public void openBook(ItemStack stack, World world, EntityPlayer player) {
        player.openGui(TConstruct.instance, mantle.client.MProxyClient.manualGuiID, world, 0, 0, 0);
        FMLClientHandler.instance().displayGuiScreen(player, new GuiManual(stack, getData(stack)));
    }

    private BookData getData(ItemStack stack) {
        return TFTinkerConstructIntegration.manualData.twilightMaterials;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean par4) {
        list.add("\u00a7o" + StatCollector.translateToLocal("manualTF.tooltip"));
    }
}
