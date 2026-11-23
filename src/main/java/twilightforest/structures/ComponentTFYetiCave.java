package twilightforest.structures;

import java.util.Random;

import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;

import twilightforest.entity.boss.EntityTFYetiAlpha;
import twilightforest.world.TFWorld;

public class ComponentTFYetiCave extends ComponentTFHollowHill {

    private static final int STALACTITE_COUNT = 128;
    private static final int YETI_HOME_RADIUS = 30;
    private static final int CAVE_HEIGHT = 20;

    private boolean yetiPlaced;

    public ComponentTFYetiCave() {
        super();
    }

    public ComponentTFYetiCave(World world, Random rand, int i, int x, int y, int z) {
        super(world, rand, i, 2, x, y + 2, z);
    }

    @Override
    protected void func_143012_a(NBTTagCompound tag) {
        super.func_143012_a(tag);
        tag.setBoolean("yetiPlaced", yetiPlaced);
    }

    @Override
    protected void func_143011_b(NBTTagCompound tag) {
        super.func_143011_b(tag);
        yetiPlaced = tag.getBoolean("yetiPlaced");
    }

    /**
     *
     * @param cx
     * @param cz
     * @return true if the coordinates would be inside the hill on the "floor" of the hill
     */
    @Override
    boolean isInHill(int cx, int cz) {
        // yeti cave is square
        return cx < radius * 2 && cx > 0 && cz < radius * 2 && cz > 0;
    }

    /**
     * @return true if the coordinates are inside the hill in 3D
     */
    @Override
    boolean isInHill(int mapX, int mapY, int mapZ) {
        // yeti cave is square and extends 20 blocks above sea level
        return mapX > boundingBox.minX && mapX < boundingBox.minX + radius * 2
                && mapZ > boundingBox.minZ
                && mapZ < boundingBox.minZ + radius * 2
                && mapY > TFWorld.SEALEVEL
                && mapY < TFWorld.SEALEVEL + CAVE_HEIGHT;
    }

    /**
     * Add in all the blocks we're adding.
     */
    @Override
    public boolean addComponentParts(World world, Random rand, StructureBoundingBox sbb) {
        // fill in features

        // // ore or glowing stalactites! (smaller, less plentiful)
        // for (int i = 0; i < sn; i++)
        // {
        // int[] dest = getCoordsInHill2D(rand);
        // generateOreStalactite(world, dest[0], 1, dest[1], sbb);
        // }
        // stone stalactites!
        generateRandomBlockStalactites(world, rand, STALACTITE_COUNT, Blocks.stone, 1.0F, true, 1, sbb);
        // ice stalactites!
        generateRandomBlockStalactites(world, rand, STALACTITE_COUNT, Blocks.ice, 1.0F, true, 1, sbb);
        // packed ice stalactites!
        generateRandomBlockStalactites(world, rand, STALACTITE_COUNT, Blocks.packed_ice, 0.9F, true, 1, sbb);

        // spawn alpha yeti
        if (!yetiPlaced) {
            int bx = getXWithOffset(radius, radius);
            int by = getYWithOffset(0);
            int bz = getZWithOffset(radius, radius);

            if (sbb.isVecInside(bx, by, bz)) {
                EntityTFYetiAlpha yeti = new EntityTFYetiAlpha(world);
                yeti.setPosition(bx, by, bz);
                yeti.setHomeArea(bx, by, bz, YETI_HOME_RADIUS);

                if (world.spawnEntityInWorld(yeti)) {
                    yetiPlaced = true;
                }
            }
        }
        return true;
    }
}
