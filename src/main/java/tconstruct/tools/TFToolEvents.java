package tconstruct.tools;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.nbt.NBTTagCompound;

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
        boolean hasTwilit = twilitMaterials.contains(toolTag.getInteger("Head"))
                || twilitMaterials.contains(toolTag.getInteger("Handle"))
                || twilitMaterials.contains(toolTag.getInteger("Accessory"))
                || twilitMaterials.contains(toolTag.getInteger("Extra"))
                || (event.tool instanceof AmmoItem && toolTag.getInteger("Accessory") == 5);
        if (hasTwilit) {
            toolTag.setBoolean("IsInTF", true);
            toolTag.setInteger("MiningSpeed", toolTag.getInteger("MiningSpeed") + 200);
        }
    }

}
