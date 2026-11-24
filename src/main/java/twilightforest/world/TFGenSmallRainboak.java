package twilightforest.world;

public class TFGenSmallRainboak extends TFGenSmallTwilightOak {

    private static final int RAINBOAK_LEAF_META = 3;

    public TFGenSmallRainboak() {
        this(false);
    }

    public TFGenSmallRainboak(boolean notify) {
        super(notify);
        this.leafMeta = RAINBOAK_LEAF_META;
    }
}
