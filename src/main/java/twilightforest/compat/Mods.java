package twilightforest.compat;

import java.util.function.Supplier;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModAPIManager;

public enum Mods {

    GTNH("dreamcraft"),
    skinport("skinport"),
    NEI("NotEnoughItems"),
    baubles("Baubles"),
    thaumcraft("thaumcraft"),
    gregtech_nh("gregtech_nh"),
    endlessids("endlessids"),
    ticon("TConstruct"),
    tgregworks("TGregworks"),
    EFR("etfuturum"),
    angelicaWeather(() -> Loader.isModLoaded("angelica") && ModAPIManager.INSTANCE.hasAPI("angelica|weather"));

    public final String modid;
    private final Supplier<Boolean> supplier;
    private Boolean loaded;

    Mods(String modid) {
        this.modid = modid;
        this.supplier = null;
    }

    Mods(Supplier<Boolean> supplier) {
        this.supplier = supplier;
        this.modid = null;
    }

    public boolean isLoaded() {
        if (loaded == null) {
            if (supplier != null) {
                loaded = supplier.get();
            } else if (modid != null) {
                loaded = Loader.isModLoaded(modid);
            } else loaded = false;
        }
        return loaded;
    }
}
