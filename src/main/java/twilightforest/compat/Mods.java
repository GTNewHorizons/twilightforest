package twilightforest.compat;

import java.util.function.Supplier;

import cpw.mods.fml.common.Loader;

public enum Mods {

    GTNH("dreamcraft"),
    skinport("skinport"),
    NEI("NotEnoughItems"),
    baubles("baubles"),
    thaumcraft("thaumcraft"),
    endlessids("endlessids"),
    ticon("TConstruct"),
    tgregworks("TGregworks"),
    EFR("etfuturum");

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
