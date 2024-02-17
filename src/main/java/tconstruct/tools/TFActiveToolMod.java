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
            // if (entity instanceof EntityPlayer && (((EntityPlayer) entity).isUsingItem())) return;
            // NBTTagCompound tags = stack.getTagCompound().getCompoundTag("InfiTool");
            // if (tags.hasKey("Moss")) {
            // int chance = tags.getInteger("Moss");
            // int check = world.canBlockSeeTheSky((int) entity.posX, (int) entity.posY, (int) entity.posZ) ? 350
            // : 1150;
            // // REGROWING AMMO :OOoo
            // if (tool instanceof IAmmo && random.nextInt(check * 3) < chance) // ammo regenerates at a much slower
            // // rate
            // {
            // IAmmo ammothing = (IAmmo) tool;
            // if (ammothing.getAmmoCount(stack) > 0) // must have ammo
            // ammothing.addAmmo(1, stack);
            // }
            // // selfrepairing tool. LAAAAAME
            // else if (random.nextInt(check) < chance) {
            // AbilityHelper.healTool(stack, 1, (EntityLivingBase) entity, true);
            // }
            // }
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
