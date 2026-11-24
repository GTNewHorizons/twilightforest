package twilightforest.structures.minotaurmaze;

import java.util.Random;

import net.minecraft.entity.item.EntityPainting;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;

public class ComponentTFMazeDeadEndPainting extends ComponentTFMazeDeadEnd {

    private boolean paintingPlaced;

    public ComponentTFMazeDeadEndPainting() {
        super();
    }

    public ComponentTFMazeDeadEndPainting(int i, int x, int y, int z, int rotation) {
        super(i, x, y, z, rotation);
    }

    @Override
    public boolean addComponentParts(World world, Random rand, StructureBoundingBox sbb) {
        // normal doorway
        super.addComponentParts(world, rand, sbb);

        // torches
        this.placeBlockAtCurrentPosition(world, Blocks.torch, 0, 1, 3, 3, sbb);
        this.placeBlockAtCurrentPosition(world, Blocks.torch, 0, 4, 3, 3, sbb);

        // place the painting only when its anchor is in the chunk currently generating
        if (!paintingPlaced) {
            int x = getXWithOffset(2, 4);
            int y = getYWithOffset(2);
            int z = getZWithOffset(2, 4);
            if (sbb.isVecInside(x, y, z)) {
                // try the inward facing side first, the art must have a solid backing
                for (int i = 0; i < 4; i++) {
                    int direction = (getCoordBaseMode() + 2 + i) & 3;
                    EntityPainting painting = new EntityPainting(world, x, y, z, direction);
                    if (painting.onValidSurface() && world.spawnEntityInWorld(painting)) {
                        paintingPlaced = true;
                        break;
                    }
                }
            }
        }

        return true;
    }

    @Override
    protected void func_143012_a(NBTTagCompound nbt) {
        super.func_143012_a(nbt);
        nbt.setBoolean("tfPaintingPlaced", paintingPlaced);
    }

    @Override
    protected void func_143011_b(NBTTagCompound nbt) {
        super.func_143011_b(nbt);
        paintingPlaced = nbt.getBoolean("tfPaintingPlaced");
    }
}
