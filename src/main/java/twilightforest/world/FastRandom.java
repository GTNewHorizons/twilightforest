package twilightforest.world;

import java.util.Random;

/**
 * Drop-in replacement for Random that uses a long instead of AtomicLong. Significantly reducing overhead. Not
 * thread-safe; intended for single-threaded usage.
 */
public class FastRandom extends Random {

    private static final long MULTIPLIER = 0x5DEECE66DL;
    private static final long ADDEND = 0xBL;
    private static final long MASK = (1L << 48) - 1;

    private long seed;
    private double nextNextGaussian;
    private boolean haveNextNextGaussian;

    public FastRandom(long seed) {
        super(0);
        setSeed(seed);
    }

    @Override
    public void setSeed(long seed) {
        this.seed = (seed ^ MULTIPLIER) & MASK;
        this.haveNextNextGaussian = false;
    }

    @Override
    protected int next(int bits) {
        seed = (seed * MULTIPLIER + ADDEND) & MASK;
        return (int) (seed >>> (48 - bits));
    }

    @Override
    public double nextGaussian() {
        if (haveNextNextGaussian) {
            haveNextNextGaussian = false;
            return nextNextGaussian;
        }
        double v1, v2, s;
        do {
            v1 = 2 * nextDouble() - 1;
            v2 = 2 * nextDouble() - 1;
            s = v1 * v1 + v2 * v2;
        } while (s >= 1 || s == 0);
        double multiplier = StrictMath.sqrt(-2 * StrictMath.log(s) / s);
        nextNextGaussian = v2 * multiplier;
        haveNextNextGaussian = true;
        return v1 * multiplier;
    }
}
