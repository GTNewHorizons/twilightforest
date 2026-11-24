package twilightforest.entity;

import net.minecraft.util.DamageSource;

public final class TFDamageSources {

    // Based on DamageSource.magic (what the ice bomb was using before now).
    public static DamageSource iceBomb = new DamageSource("iceBomb").setDamageBypassesArmor().setMagicDamage();

    private TFDamageSources() {}
}
