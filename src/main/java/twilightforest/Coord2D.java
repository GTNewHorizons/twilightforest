package twilightforest;

import com.github.bsideup.jabel.Desugar;

@Desugar
public record Coord2D(int x, int z) {

    public Coord2D add(Coord2D other) {
        return new Coord2D(this.x + other.x, this.z + other.z);
    }

    public Coord2D spiralNext() {
        return spiralNext(1);
    }

    /**
     * Next step in an outward spiral. Positive step size is CCW, negative CW. Step size 0 is invalid and returns null.
     * First step from (0,0) is always in the (1,0) direction. Looks expensive to call a million times, actually isn't.
     */
    public Coord2D spiralNext(int step) {
        if (step > 0) return spiralNextCCW(step);
        else if (step < 0) return spiralNextCW(-step);
        return null;
    }

    private Coord2D spiralNextCCW(int step) {
        // always called with positive step
        int x = this.x;
        int z = this.z;

        if (x == 0 && z == 0) return new Coord2D(step, 0);

        if (x == z) {
            if (x > 0) return new Coord2D(x - step, z);
            else return new Coord2D(x + step, z);
        }

        if (x == -z) {
            if (x > 0) return new Coord2D(x + step, z); // step into next radius here
            else return new Coord2D(x, z - step);
        }

        if (x > z) {
            if (x > -z) return new Coord2D(x, z + step);
            else return new Coord2D(x + step, z);
        } else {
            if (x > -z) return new Coord2D(x - step, z);
            else return new Coord2D(x, z - step);
        }
    }

    private Coord2D spiralNextCW(int step) {
        // always called with positive step
        int x = this.x;
        int z = this.z;

        if (x == 0 && z == 0) return new Coord2D(step, 0);

        if (x == z) {
            if (x > 0) return new Coord2D(x + step, z); // step into next radius here
            else return new Coord2D(x, z + step);
        }

        if (x == -z) {
            if (x > 0) return new Coord2D(x - step, z);
            else return new Coord2D(x + step, z);
        }

        if (x > z) {
            if (x > -z) return new Coord2D(x, z - step);
            else return new Coord2D(x - step, z);
        } else {
            if (x > -z) return new Coord2D(x + step, z);
            else return new Coord2D(x, z + step);
        }
    }

    public int rad() {
        return Math.max(Math.abs(this.x), Math.abs(this.z));
    }
}
