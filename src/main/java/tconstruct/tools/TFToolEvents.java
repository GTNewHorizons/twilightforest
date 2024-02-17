package tconstruct.tools;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;

import com.mojang.realmsclient.gui.ChatFormatting;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import tconstruct.library.event.ToolCraftEvent;
import tconstruct.library.weaponry.AmmoItem;
import twilightforest.integration.TFTinkerConstructIntegration.MaterialID;

public class TFToolEvents {

    @SubscribeEvent
    public void craftTool(ToolCraftEvent.NormalTool event) {
        NBTTagCompound toolTag = event.toolTag.getCompoundTag("InfiTool");
        List<Integer> twilitMaterials = new ArrayList<Integer>();
        twilitMaterials.add(MaterialID.FieryMetal);
        twilitMaterials.add(MaterialID.Knightmetal);
        twilitMaterials.add(MaterialID.NagaScale);
        twilitMaterials.add(MaterialID.Steeleaf);
        boolean hasTwilit = false;
        int twilitID = 5;
        if (twilitMaterials.contains(toolTag.getInteger("Head"))) {
            hasTwilit = true;
            twilitID = toolTag.getInteger("Head");
        } else {
            if (twilitMaterials.contains(toolTag.getInteger("Handle"))) {
                hasTwilit = true;
                twilitID = toolTag.getInteger("Handle");
            } else {
                if (twilitMaterials.contains(toolTag.getInteger("Accessory"))) {
                    hasTwilit = true;
                    twilitID = toolTag.getInteger("Accessory");
                } else {
                    if (twilitMaterials.contains(toolTag.getInteger("Extra"))) {
                        hasTwilit = true;
                        twilitID = toolTag.getInteger("Extra");
                    } else {
                        if (event.tool instanceof AmmoItem && toolTag.getInteger("Accessory") == 5) {
                            hasTwilit = true;
                        }
                    }
                }
            }
        }
        if (hasTwilit) {
            toolTag.setBoolean("IsInTF", true);
            toolTag.setInteger("MiningSpeed", toolTag.getInteger("MiningSpeed") + 200);
            toolTag.setInteger("TwilitID", twilitID);
        }
    }

    @SubscribeEvent
    public void tooltip(ItemTooltipEvent event) {
        if (event.itemStack == null || event.itemStack.getItem() == null
                || !event.itemStack.hasTagCompound()
                || !event.itemStack.getTagCompound().hasKey("InfiTool")
                || !event.itemStack.getTagCompound().getCompoundTag("InfiTool").hasKey("IsInTF"))
            return;

        event.toolTip.add(
                1,
                "" + colorFromID(event.itemStack.getTagCompound().getCompoundTag("InfiTool").getInteger("TwilitID"))
                        + StatCollector.translateToLocal("material.twilit.ability"));
    }

    private ChatFormatting colorFromID(int materialID) {
        ChatFormatting cf;
        switch (materialID) {
            default:
                cf = ChatFormatting.DARK_GRAY;
                break;
            case MaterialID.FieryMetal:
                cf = ChatFormatting.GOLD;
                break;
            case MaterialID.Knightmetal:
                cf = ChatFormatting.GREEN;
                break;
            case MaterialID.NagaScale:
            case MaterialID.Steeleaf:
                cf = ChatFormatting.DARK_GREEN;
                break;
        }
        return cf;
    }

}
