package twilightforest;

import com.github.bsideup.jabel.Desugar;

@Desugar
public record Coord2D(int x, int z) {

    public Coord2D add(Coord2D other) {
        return new Coord2D(this.x + other.x, this.z + other.z);
    }

    public Coord2D spiralNext() {
        // next step in a CCW outward spiral
        // looks expensive to call a million times, actually isn't
        int x = this.x;
        int z = this.z;

        if (x == 0 && z == 0) return new Coord2D(1, 0);

        if (x == z) {
            if (x > 0) return new Coord2D(x - 1, z);
            else return new Coord2D(x + 1, z);
        }

        if (x == -z) {
            if (x > 0) return new Coord2D(x + 1, z); // step into next radius here
            else return new Coord2D(x, z - 1);
        }

        if (x > z) {
            if (x > -z) return new Coord2D(x, z + 1);
            else return new Coord2D(x + 1, z);
        } else {
            if (x > -z) return new Coord2D(x - 1, z);
            else return new Coord2D(x, z - 1);
        }
    }

    public int rad() {
        return Math.max(Math.abs(this.x), Math.abs(this.z));
    }
}
