package twilightforest.structures.lichtower;

public class ComponentTFTowerRoofPointyOverhang extends ComponentTFTowerRoofPointy {

    public ComponentTFTowerRoofPointyOverhang() {
        super();
    }

    public ComponentTFTowerRoofPointyOverhang(int i, ComponentTFTowerWing wing) {
        super(i, wing); // same facing but it doesn't matter

        size = wing.size + 2; // assuming only square towers and roofs right now
        height = size;

        // the highest corner slabs can extend one level above the final solid layer
        makeOverhangBB(wing);
        boundingBox.maxY++;
    }
}
