package twilightforest;

import com.github.bsideup.jabel.Desugar;

@Desugar
public record Coord2D(int x, int z) {

    public Coord2D add(Coord2D other) {
        return new Coord2D(x + other.x, z + other.z);
    }

    public Coord2D spiralNext() {
        return spiralNext(1);
    }

    /**
     * Next step in an outward spiral. Positive step size is CCW, negative CW. Step size 0 is invalid and returns null.
     * First step from (0,0) is always in the (1,0) direction. Looks expensive to call a million times, actually isn't.
     */
    public Coord2D spiralNext(int step) {
        if (step == 0) {
            return null;
        }
        if (step == Integer.MIN_VALUE) {
            throw new IllegalArgumentException("Spiral step magnitude exceeds the integer range");
        }
        if (step > 0) {
            return spiralNextCCW(step);
        }

        // a spiral is the reflection of the spiral across the X axis
        Coord2D next = new Coord2D(x, -z).spiralNextCCW(-step);
        return new Coord2D(next.x, -next.z);
    }

    private Coord2D spiralNextCCW(int step) {
        // always called with positive step
        if (x == 0 && z == 0) {
            return new Coord2D(step, 0);
        }
        if (x == z) {
            return x > 0 ? new Coord2D(x - step, z) : new Coord2D(x + step, z);
        }
        if (x == -z) {
            // step into next radius here
            return x > 0 ? new Coord2D(x + step, z) : new Coord2D(x, z - step);
        }
        if (x > z) {
            return x > -z ? new Coord2D(x, z + step) : new Coord2D(x + step, z);
        }
        return x > -z ? new Coord2D(x - step, z) : new Coord2D(x, z - step);
    }

    public int rad() {
        // saturate instead of overflowing when one coordinate is Integer.MIN_VALUE
        return (int) Math.min(Integer.MAX_VALUE, Math.max(Math.abs((long) x), Math.abs((long) z)));
    }
}
