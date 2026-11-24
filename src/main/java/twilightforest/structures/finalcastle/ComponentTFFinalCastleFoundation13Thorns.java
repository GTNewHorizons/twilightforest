package twilightforest.structures.finalcastle;

import java.util.Random;

import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;

import twilightforest.block.TFBlocks;
import twilightforest.structures.StructureTFComponent;

// Foundation that makes thorns go all through the tower
public class ComponentTFFinalCastleFoundation13Thorns extends ComponentTFFinalCastleFoundation13 {

    public ComponentTFFinalCastleFoundation13Thorns() {}

    public ComponentTFFinalCastleFoundation13Thorns(Random rand, int i, StructureTFComponent sideTower) {
        super(rand, i, sideTower);

        this.boundingBox = new StructureBoundingBox(
                sideTower.getBoundingBox().minX - 5,
                Math.min(60, sideTower.getBoundingBox().maxY - 1),
                sideTower.getBoundingBox().minZ - 5,
                sideTower.getBoundingBox().maxX + 5,
                sideTower.getBoundingBox().maxY + 6,
                sideTower.getBoundingBox().maxZ + 5);
    }

    @Override
    public boolean addComponentParts(World world, Random rand, StructureBoundingBox sbb) {
        // thorns
        Random decoRNG = new Random(
                world.getSeed() + (this.boundingBox.minX * 321534781) ^ (this.boundingBox.minZ * 756839));

        for (int i = 0; i < 4; i++) {
            this.makeThornVine(world, decoRNG, i, sbb);
        }
        return true;
    }

    private void makeThornVine(World world, Random decoRNG, int rotation, StructureBoundingBox sbb) {

        int x = 3 + decoRNG.nextInt(13);
        int z = 3 + decoRNG.nextInt(13);

        int startY = this.boundingBox.getYSize() <= 2 ? 7 : this.boundingBox.getYSize() - 1;
        int y = startY;

        int twist = decoRNG.nextInt(4);
        int twistMod = 3 + decoRNG.nextInt(3);

        while (this.getBlockIDRotated(world, x, y, z, rotation, sbb) != TFBlocks.deadrock
                && this.getYWithOffset(y) > 60) {
            this.placeBlockRotated(world, TFBlocks.thorns, 0, x, y, z, rotation, sbb);
            // twist vines around the center block
            switch (twist) {
                case 0 -> {
                    this.placeBlockRotated(world, TFBlocks.thorns, 0, x + 1, y, z, rotation, sbb);
                    this.placeBlockRotated(world, TFBlocks.thorns, 0, x, y, z + 1, rotation, sbb);
                    this.placeBlockRotated(world, TFBlocks.thorns, 0, x + 1, y, z + 1, rotation, sbb);
                }
                case 1 -> {
                    this.placeBlockRotated(world, TFBlocks.thorns, 0, x + 1, y, z, rotation, sbb);
                    this.placeBlockRotated(world, TFBlocks.thorns, 0, x, y, z - 1, rotation, sbb);
                    this.placeBlockRotated(world, TFBlocks.thorns, 0, x + 1, y, z - 1, rotation, sbb);
                }
                case 2 -> {
                    this.placeBlockRotated(world, TFBlocks.thorns, 0, x - 1, y, z, rotation, sbb);
                    this.placeBlockRotated(world, TFBlocks.thorns, 0, x, y, z - 1, rotation, sbb);
                    this.placeBlockRotated(world, TFBlocks.thorns, 0, x - 1, y, z - 1, rotation, sbb);
                }
                case 3 -> {
                    this.placeBlockRotated(world, TFBlocks.thorns, 0, x - 1, y, z, rotation, sbb);
                    this.placeBlockRotated(world, TFBlocks.thorns, 0, x, y, z + 1, rotation, sbb);
                    this.placeBlockRotated(world, TFBlocks.thorns, 0, x - 1, y, z + 1, rotation, sbb);
                }
            }

            int oldLocalY = y - startY + 7;
            if (Math.abs(oldLocalY % twistMod) == 1) {
                makeThornBranch(world, x, y, oldLocalY, z, rotation, sbb);
            }

            // Keep the original twist frequency independent of the bounding box.
            if (oldLocalY % twistMod == 0) {
                twist++;
                twist = twist % 4;
            }
            y--;
        }
    }

    private void makeThornBranch(World world, int x, int y, int oldLocalY, int z, int rotation,
            StructureBoundingBox sbb) {
        // preserve branch choices from the old Y origin
        Random rand = new Random(world.getSeed() + (x * 321534781) ^ (oldLocalY * 756839) + z);

        // pick a direction
        int dir = rand.nextInt(4);

        // initialize direction variables
        int dx = 0;
        int dz = 0;

        switch (dir) {
            case 0 -> dx = +1;
            case 1 -> dz = +1;
            case 2 -> dx = -1;
            case 3 -> dz = -1;
        }

        // how far do we branch?
        int dist = 2 + rand.nextInt(3);

        // check to make sure there's room
        int destX = x + (dist * dx);
        int destZ = z + (dist * dz);

        if (destX > 0 && destX < this.boundingBox.getXSize() && destZ > 0 && destZ < this.boundingBox.getZSize()) {
            for (int i = 0; i < dist; i++) {
                // go out that far
                int branchMeta = ((dir + rotation + this.coordBaseMode) % 2 == 0) ? 5 : 9;
                if (i > 0) {
                    this.placeBlockRotated(
                            world,
                            TFBlocks.thorns,
                            branchMeta,
                            x + (dx * i),
                            y,
                            z + (dz * i),
                            rotation,
                            sbb);
                }
                // go up that far
                this.placeBlockRotated(world, TFBlocks.thorns, 1, destX, y + i, destZ, rotation, sbb);
                // go back half that far
                if (i > (dist / 2)) {
                    this.placeBlockRotated(
                            world,
                            TFBlocks.thorns,
                            branchMeta,
                            x + (dx * i),
                            y + dist - 1,
                            z + (dz * i),
                            rotation,
                            sbb);
                }
            }
        }
    }
}
