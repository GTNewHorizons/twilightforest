package twilightforest.structures.finalcastle;

import java.util.List;
import java.util.Random;

import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;

import twilightforest.structures.StructureTFComponent;

public class ComponentTFFinalCastleDungeonSteps extends StructureTFComponent {

    public ComponentTFFinalCastleDungeonSteps() {}

    public ComponentTFFinalCastleDungeonSteps(Random rand, int i, int x, int y, int z, int rotation) {
        this.spawnListIndex = 2; // dungeon monsters

        this.setCoordBaseMode(rotation);
        this.boundingBox = StructureTFComponent
                .getComponentToAddBoundingBox2(x, y, z, -2, -15, -3, 5, 15, 20, rotation);
    }

    @Override
    public void buildComponent(StructureComponent parent, List<StructureComponent> list, Random rand) {
        if (parent != null && parent instanceof StructureTFComponent) {
            this.deco = ((StructureTFComponent) parent).deco;
        }
    }

    /**
     * build more steps towards the specified direction
     */
    public ComponentTFFinalCastleDungeonSteps buildMoreStepsTowards(StructureComponent parent, List list, Random rand,
            int rotation) {

        int direction = (rotation + this.coordBaseMode) % 4;

        int sx = 2;
        int sy = 0;
        int sz = 17;

        switch (rotation) {
            case 0 -> sz -= 5;
            case 1 -> sx -= 5;
            case 2 -> sz += 5;
            case 3 -> sx += 6;
        }

        // find center of landing
        int dx = this.getXWithOffset(sx, sz);
        int dy = this.getYWithOffset(sy);
        int dz = this.getZWithOffset(sx, sz);

        // build a new stairway there
        ComponentTFFinalCastleDungeonSteps steps = new ComponentTFFinalCastleDungeonSteps(
                rand,
                this.componentType + 1,
                dx,
                dy,
                dz,
                direction);
        list.add(steps);
        steps.buildComponent(this, list, rand);

        return steps;
    }

    /**
     * build a new level under the exit
     */
    public ComponentTFFinalCastleDungeonEntrance buildLevelUnder(StructureComponent parent, List list, Random rand,
            int level) {
        // find center of landing
        int dx = this.getXWithOffset(2, 19);
        int dy = this.getYWithOffset(-7);
        int dz = this.getZWithOffset(2, 19);

        // build a new dungeon level under there
        ComponentTFFinalCastleDungeonEntrance room = new ComponentTFFinalCastleDungeonEntrance(
                rand,
                8,
                dx,
                dy,
                dz,
                this.coordBaseMode,
                level);
        list.add(room);
        room.buildComponent(this, list, rand);

        return room;
    }

    /**
     * build the boss room
     */
    public ComponentTFFinalCastleDungeonForgeRoom buildBossRoomUnder(StructureComponent parent, List list,
            Random rand) {
        // find center of landing
        int dx = this.getXWithOffset(2, 19);
        int dy = this.getYWithOffset(-31);
        int dz = this.getZWithOffset(2, 19);

        // build a new dungeon level under there
        ComponentTFFinalCastleDungeonForgeRoom room = new ComponentTFFinalCastleDungeonForgeRoom(
                rand,
                8,
                dx,
                dy,
                dz,
                this.coordBaseMode);
        list.add(room);
        room.buildComponent(this, list, rand);

        // System.out.println("Made dungeon boss room at " + dx + ", " + dy + ", " + dz + ".");

        return room;
    }

    @Override
    public boolean addComponentParts(World world, Random rand, StructureBoundingBox sbb) {
        for (int z = 0; z < 15; z++) {
            int y = 14 - z;
            this.fillWithMetadataBlocks(
                    world,
                    sbb,
                    0,
                    y,
                    z,
                    4,
                    y,
                    z,
                    deco.stairID,
                    getStairMeta(3),
                    deco.stairID,
                    getStairMeta(3),
                    false);
            this.fillWithAir(world, sbb, 0, y + 1, z, 4, y + 6, z);
        }
        this.fillWithAir(world, sbb, 0, 0, 15, 4, 5, 19);

        return true;
    }
}
