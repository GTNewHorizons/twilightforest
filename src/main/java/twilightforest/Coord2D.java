package twilightforest;

import com.github.bsideup.jabel.Desugar;

@Desugar
public record Coord2D(int x, int z) {

    public Coord2D add(Coord2D other) {
        return new Coord2D(this.x + other.x, this.z + other.z);
    }
}
