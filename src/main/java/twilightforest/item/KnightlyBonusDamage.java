package twilightforest.item;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;

// shared bonus damage, deliberately stateless
final class KnightlyBonusDamage {

    private static final int BONUS_DAMAGE = 2;

    private KnightlyBonusDamage() {}

    static void apply(EntityLivingBase target, EntityLivingBase attacker, boolean armoredTarget) {
        if (target.worldObj.isRemote || (target.getTotalArmorValue() > 0) != armoredTarget
                || !(attacker instanceof EntityPlayer player)) {
            return;
        }
        player.onEnchantmentCritical(target);
        // keep the original extra hit mechanic, including its ability to bypass resistance
        target.lastDamage = 0;
        target.attackEntityFrom(DamageSource.causeMobDamage(attacker), BONUS_DAMAGE);
    }
}
