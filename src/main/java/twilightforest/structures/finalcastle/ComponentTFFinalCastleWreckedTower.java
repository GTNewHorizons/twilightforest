package twilightforest.structures.finalcastle;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureComponent;

import twilightforest.structures.StructureTFComponent;

public class ComponentTFFinalCastleWreckedTower extends ComponentTFFinalCastleDamagedTower {

    public ComponentTFFinalCastleWreckedTower() {}

    public ComponentTFFinalCastleWreckedTower(Random rand, int i, int x, int y, int z, int direction) {
        super(rand, i, x, y, z, direction);
    }

    @Override
    public void buildComponent(StructureComponent parent, List<StructureComponent> list, Random rand) {
        if (parent instanceof StructureTFComponent) {
            this.deco = ((StructureTFComponent) parent).deco;
        }

        // // add foundation
        // Foundation13 foundation = new Foundation13(rand, 4, this);
        // list.add(foundation);
        // foundation.buildComponent(this, list, rand);

        // add thorns
        ComponentTFFinalCastleFoundation13 thorns = new ComponentTFFinalCastleFoundation13Thorns(rand, 0, this);
        list.add(thorns);
        thorns.buildComponent(this, list, rand);

        // // add roof
        // StructureTFComponent roof = rand.nextBoolean() ? new Roof13Conical(rand, 4, this) : new
        // Roof13Crenellated(rand, 4, this);
        // list.add(roof);
        // roof.buildComponent(this, list, rand);

        // keep on building?
        // this.buildNonCriticalTowers(parent, list, rand);
    }

    @Override
    public int getGlyphMeta() {
        return 1;
    }

    @Override
    protected void determineBlockDestroyed(World world, ArrayList<DestroyArea> areas, int y, int x, int z) {
        for (DestroyArea area : areas) {
            if (area != null && area.isVecInside(x, y, z)) {
                return;
            }
        }
        world.setBlockToAir(x, y, z);
    }

    @Override
    protected ArrayList<DestroyArea> makeInitialDestroyList(Random rand) {
        ArrayList<DestroyArea> areas = new ArrayList<>(4);
        for (int i = 0; i < 4; i++) {
            DestroyArea area = DestroyArea
                    .createNonIntersecting(this.getBoundingBox(), rand, this.getBoundingBox().maxY - 1, areas);
            if (area != null) {
                areas.add(area);
            }
        }
        return areas;
    }
}
