package twilightforest.structures.darktower;

import java.util.List;
import java.util.Random;

import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;

import twilightforest.structures.StructureTFComponent;
import twilightforest.structures.lichtower.ComponentTFTowerWing;

public class ComponentTFDarkTowerBridge extends ComponentTFTowerWing {

    public ComponentTFDarkTowerBridge() {
        super();
    }

    int dSize;
    int dHeight;

    protected ComponentTFDarkTowerBridge(int i, int x, int y, int z, int pSize, int pHeight, int direction) {
        super(i, x, y, z, 5, 5, direction);

        this.dSize = pSize;
        this.dHeight = pHeight;
    }

    @Override
    public void buildComponent(StructureComponent parent, List<StructureComponent> list, Random rand) {
        if (parent != null && parent instanceof StructureTFComponent) {
            this.deco = ((StructureTFComponent) parent).deco;
        }
        makeTowerWing(list, rand, this.getComponentType(), 4, 1, 2, dSize, dHeight, 0);
    }

    public boolean makeTowerWing(List<StructureComponent> list, Random rand, int index, int x, int y, int z,
            int wingSize, int wingHeight, int rotation) {
        // kill too-small towers
        if (wingHeight < 6) {
            return false;
        }

        int direction = (getCoordBaseMode() + rotation) % 4;
        int[] dx = offsetTowerCoords(x, y, z, wingSize, direction);

        if (dx[1] + wingHeight > 255) {
            // end of the world!
            return false;
        }

        ComponentTFTowerWing wing = new ComponentTFDarkTowerWing(
                index,
                dx[0],
                dx[1],
                dx[2],
                wingSize,
                wingHeight,
                direction);
        // check to see if it intersects something already there
        StructureComponent intersect = StructureComponent.findIntersecting(list, wing.getBoundingBox());
        if (intersect == null || intersect == this) {
            list.add(wing);
            wing.buildComponent(this, list, rand);
            addOpening(x, y, z, rotation);
            return true;
        } else {
            // System.out.println("Planned wing intersects with " + intersect);
            return false;
        }
    }

    @Override
    public boolean addComponentParts(World world, Random rand, StructureBoundingBox sbb) {

        // make walls
        fillWithBlocks(world, sbb, 0, 0, 0, size - 1, height - 1, size - 1, deco.blockID, deco.blockID, false);

        // accents
        for (int x = 0; x < size; x++) {
            this.placeBlockAtCurrentPosition(world, deco.accentID, deco.accentMeta, x, 0, 0, sbb);
            this.placeBlockAtCurrentPosition(world, deco.accentID, deco.accentMeta, x, height - 1, 0, sbb);
            this.placeBlockAtCurrentPosition(world, deco.accentID, deco.accentMeta, x, 0, size - 1, sbb);
            this.placeBlockAtCurrentPosition(world, deco.accentID, deco.accentMeta, x, height - 1, size - 1, sbb);
        }

        // nullify sky light
        nullifySkyLightForBoundingBox(world);

        // clear inside
        fillWithAir(world, sbb, 0, 1, 1, size - 1, height - 2, size - 2);

        return true;
    }

    /**
     * Gets the bounding box of the tower wing we would like to make.
     * 
     * @return
     */
    public StructureBoundingBox getWingBB() {
        int[] dest = offsetTowerCoords(4, 1, 2, dSize, this.getCoordBaseMode());
        return StructureTFComponent.getComponentToAddBoundingBox(
                dest[0],
                dest[1],
                dest[2],
                0,
                0,
                0,
                dSize - 1,
                dHeight - 1,
                dSize - 1,
                this.getCoordBaseMode());
    }

}
