package tconstruct.tools;

import java.util.Random;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import tconstruct.library.ActiveToolMod;
import tconstruct.library.tools.ToolCore;
import twilightforest.TwilightForestMod;

public class TFActiveToolMod extends ActiveToolMod {

    Random random = new Random();

    /* Updating */
    @Override
    public void updateTool(ToolCore tool, ItemStack stack, World world, Entity entity) {
        if (!world.isRemote && entity instanceof EntityLivingBase
                && !((EntityLivingBase) entity).isSwingInProgress
                && stack.getTagCompound() != null) {
            NBTTagCompound tags = stack.getTagCompound().getCompoundTag("InfiTool");
            twilit(tool, stack, (EntityLivingBase) entity, tags);
        }
    }

    private void twilit(ToolCore tool, ItemStack stack, EntityLivingBase entity, NBTTagCompound tags) {
        if (tags.hasKey("IsInTF")) {
            boolean isInTF = entity.dimension == TwilightForestMod.dimensionID;
            boolean wasInTF = tags.getBoolean("IsInTF");
            if (isInTF != wasInTF) {
                if (isInTF) {
                    tags.setInteger("MiningSpeed", tags.getInteger("MiningSpeed") + 200);
                    tags.setInteger("Attack", tags.getInteger("Attack") - 2);
                    tags.setInteger("BaseAttack", tags.getInteger("BaseAttack") - 2);
                } else {
                    tags.setInteger("MiningSpeed", tags.getInteger("MiningSpeed") - 200);
                    tags.setInteger("Attack", tags.getInteger("Attack") + 2);
                    tags.setInteger("BaseAttack", tags.getInteger("BaseAttack") + 2);
                }
                tags.setBoolean("IsInTF", isInTF);

                StatCollector.translateToLocal("material.twilit.ability");
            }
        }
    }

}
