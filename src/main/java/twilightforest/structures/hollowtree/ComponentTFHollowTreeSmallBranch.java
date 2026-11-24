package twilightforest.structures.hollowtree;

import java.util.List;
import java.util.Random;

import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;

import twilightforest.block.TFBlocks;

public class ComponentTFHollowTreeSmallBranch extends ComponentTFHollowTreeMedBranch {

    private static final int LOG_METADATA = 12;
    private static final int MIN_LEAF_RADIUS = 1;
    private static final int LEAF_RADIUS_VARIATIONS = 2;
    private static final int MAX_LEAF_RADIUS = MIN_LEAF_RADIUS + LEAF_RADIUS_VARIATIONS - 1;
    private static final long LEAF_SEED_MULTIPLIER = 31L;

    public ComponentTFHollowTreeSmallBranch() {
        super();
    }

    protected ComponentTFHollowTreeSmallBranch(int i, int sx, int sy, int sz, double length, double angle, double tilt,
            boolean leafy) {
        super(i, sx, sy, sz, length, angle, tilt, leafy);

        resizeBoundingBox();
    }

    @Override
    protected void func_143011_b(net.minecraft.nbt.NBTTagCompound nbt) {
        super.func_143011_b(nbt);
        resizeBoundingBox();
    }

    private void resizeBoundingBox() {
        // medium branch padding is meant for its side shoots, a small branch only
        // needs its log line and the possible 2 block leaf cluster at the tip
        boundingBox = new StructureBoundingBox(
                Math.min(src.posX, dest.posX),
                Math.min(src.posY, dest.posY),
                Math.min(src.posZ, dest.posZ),
                Math.max(src.posX, dest.posX),
                Math.max(src.posY, dest.posY),
                Math.max(src.posZ, dest.posZ));
        if (leafy) {
            boundingBox.expandTo(
                    new StructureBoundingBox(
                            dest.posX - MAX_LEAF_RADIUS,
                            dest.posY - MAX_LEAF_RADIUS,
                            dest.posZ - MAX_LEAF_RADIUS,
                            dest.posX + MAX_LEAF_RADIUS,
                            dest.posY + MAX_LEAF_RADIUS,
                            dest.posZ + MAX_LEAF_RADIUS));
        }
    }

    @Override
    public void buildComponent(StructureComponent structurecomponent, List<StructureComponent> list, Random rand) {
        // small branches have no child components, leaves are generated with the branch blocks
    }

    /**
     * Add a leaf ball to the end
     */
    @Override
    public boolean addComponentParts(World world, Random random, StructureBoundingBox sbb) {
        ChunkCoordinates rSrc = toLocalCoordinates(src);
        ChunkCoordinates rDest = toLocalCoordinates(dest);

        drawBresehnam(
                world,
                sbb,
                rSrc.posX,
                rSrc.posY,
                rSrc.posZ,
                rDest.posX,
                rDest.posY,
                rDest.posZ,
                TFBlocks.log,
                LOG_METADATA);

        // with leaves!
        if (leafy) {
            // chunk rng differs between generation passes, derive the radius from stable branch coords
            long seed = world.getSeed();
            seed = seed * LEAF_SEED_MULTIPLIER + src.posX;
            seed = seed * LEAF_SEED_MULTIPLIER + src.posY;
            seed = seed * LEAF_SEED_MULTIPLIER + src.posZ;
            seed = seed * LEAF_SEED_MULTIPLIER + dest.posX;
            seed = seed * LEAF_SEED_MULTIPLIER + dest.posY;
            seed = seed * LEAF_SEED_MULTIPLIER + dest.posZ;

            int leafRadius = new Random(seed).nextInt(LEAF_RADIUS_VARIATIONS) + MIN_LEAF_RADIUS;
            makeLeafBlob(world, sbb, rDest.posX, rDest.posY, rDest.posZ, leafRadius);
        }

        return true;
    }
}
