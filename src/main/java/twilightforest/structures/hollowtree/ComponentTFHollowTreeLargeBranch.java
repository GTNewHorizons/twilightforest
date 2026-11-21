package twilightforest.structures.hollowtree;

import java.util.List;
import java.util.Random;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;

import twilightforest.block.TFBlocks;
import twilightforest.world.TFGenerator;

public class ComponentTFHollowTreeLargeBranch extends ComponentTFHollowTreeMedBranch {

    private static final int LEAF_DUNGEON_CHANCE = 8;
    private static final int LEAF_DUNGEON_RADIUS = 4;
    private static final int DUNGEON_COMPONENT_OFFSET = 1;
    private static final int MEDIUM_BRANCH_COMPONENT_OFFSET = 2;

    private static final int MEDIUM_BRANCH_RANDOM_DIVISOR = 6;
    private static final int MEDIUM_BRANCH_BASE_DIVISOR = 8;
    private static final double MEDIUM_BRANCH_ORIGIN_MIN = 0.3D;
    private static final double MEDIUM_BRANCH_ORIGIN_VARIATION = 0.3D;
    private static final double MEDIUM_BRANCH_ANGLE_VARIATION = 0.225D;
    private static final double MEDIUM_BRANCH_LENGTH_RATIO = 0.6D;

    private static final int LOG_METADATA = 12;
    private static final int REINFORCEMENT_COUNT = 4;
    private static final int SMALL_BRANCH_COUNT_VARIATIONS = 2;
    private static final int MIN_SMALL_BRANCH_COUNT = 1;
    private static final float SMALL_BRANCH_ORIGIN_MIN = 0.25F;
    private static final float SMALL_BRANCH_ORIGIN_VARIATION = 0.25F;
    private static final float SMALL_BRANCH_ANGLE_VARIATION = 0.25F;
    private static final float SMALL_BRANCH_LENGTH_RATIO = 0.3F;
    private static final float MIN_SMALL_BRANCH_LENGTH = 2.0F;
    private static final int END_LEAF_RADIUS = 3;

    private static final String NBT_HAS_LEAF_DUNGEON = "hasLeafDungeon";

    public boolean hasLeafDungeon = false;

    public ComponentTFHollowTreeLargeBranch() {
        super();
    }

    protected ComponentTFHollowTreeLargeBranch(int i, int sx, int sy, int sz, double length, double angle, double tilt,
            boolean leafy) {
        super(i, sx, sy, sz, length, angle, tilt, leafy);
    }

    @Override
    protected void func_143012_a(NBTTagCompound nbt) {
        super.func_143012_a(nbt);
        nbt.setBoolean(NBT_HAS_LEAF_DUNGEON, hasLeafDungeon);
    }

    @Override
    protected void func_143011_b(NBTTagCompound nbt) {
        super.func_143011_b(nbt);
        hasLeafDungeon = nbt.getBoolean(NBT_HAS_LEAF_DUNGEON);
    }

    /**
     * Add other structure components to this one if needed
     */
    @Override
    public void buildComponent(StructureComponent structurecomponent, List list, Random rand) {
        int index = getComponentType();

        // go about halfway out and make a few medium branches.
        // the number of medium branches we can support depends on the length of the big branch
        // every other branch switches sides
        int randomRange = Math.max(1, (int) (length / MEDIUM_BRANCH_RANDOM_DIVISOR));
        int numMedBranches = rand.nextInt(randomRange) + (int) (length / MEDIUM_BRANCH_BASE_DIVISOR);

        for (int i = 0; i < numMedBranches; i++) {
            double outVar = rand.nextDouble() * MEDIUM_BRANCH_ORIGIN_VARIATION + MEDIUM_BRANCH_ORIGIN_MIN;
            double angleVar = rand.nextDouble() * MEDIUM_BRANCH_ANGLE_VARIATION * ((i & 1) == 0 ? 1.0D : -1.0D);
            ChunkCoordinates branchSource = TFGenerator
                    .translateCoords(src.posX, src.posY, src.posZ, length * outVar, angle, tilt);

            makeMedBranch(
                    list,
                    rand,
                    index + MEDIUM_BRANCH_COMPONENT_OFFSET + i,
                    branchSource.posX,
                    branchSource.posY,
                    branchSource.posZ,
                    length * MEDIUM_BRANCH_LENGTH_RATIO,
                    angle + angleVar,
                    tilt,
                    leafy);
        }

        hasLeafDungeon = rand.nextInt(LEAF_DUNGEON_CHANCE) == 0;
        if (hasLeafDungeon) {
            makeLeafDungeon(list, rand, index + DUNGEON_COMPONENT_OFFSET, dest.posX, dest.posY, dest.posZ);
        }
    }

    public void makeLeafDungeon(List list, Random rand, int index, int x, int y, int z) {
        ComponentTFHollowTreeLeafDungeon dungeon = new ComponentTFHollowTreeLeafDungeon(
                index,
                x,
                y,
                z,
                LEAF_DUNGEON_RADIUS);
        list.add(dungeon);
        dungeon.buildComponent(this, list, rand);
    }

    public void makeMedBranch(List list, Random rand, int index, int x, int y, int z, double branchLength,
            double branchRotation, double branchAngle, boolean leafy) {
        ComponentTFHollowTreeMedBranch branch = new ComponentTFHollowTreeMedBranch(
                index,
                x,
                y,
                z,
                branchLength,
                branchRotation,
                branchAngle,
                leafy);
        list.add(branch);
        branch.buildComponent(this, list, rand);
    }

    /**
     * Draw this branch
     */
    @Override
    public boolean addComponentParts(World world, Random rand, StructureBoundingBox sbb) {
        ChunkCoordinates rsrc = toLocalCoordinates(src);
        ChunkCoordinates rdest = toLocalCoordinates(dest);

        // main branch
        drawBresehnam(
                world,
                sbb,
                rsrc.posX,
                rsrc.posY,
                rsrc.posZ,
                rdest.posX,
                rdest.posY,
                rdest.posZ,
                TFBlocks.log,
                LOG_METADATA);

        // reinforce it
        // four unique offsets
        for (int i = 0; i < REINFORCEMENT_COUNT; i++) {
            int vx = (i & 2) == 0 ? 1 : 0;
            int vy = (i & 1) == 0 ? 1 : -1;
            int vz = (i & 2) == 0 ? 0 : 1;
            drawBresehnam(
                    world,
                    sbb,
                    rsrc.posX + vx,
                    rsrc.posY + vy,
                    rsrc.posZ + vz,
                    rdest.posX,
                    rdest.posY,
                    rdest.posZ,
                    TFBlocks.log,
                    LOG_METADATA);
        }

        // make 1-2 small branches near the base
        Random decoRNG = createDecorationRandom(world);
        int numSmallBranches = decoRNG.nextInt(SMALL_BRANCH_COUNT_VARIATIONS) + MIN_SMALL_BRANCH_COUNT;
        for (int i = 0; i < numSmallBranches; i++) {
            double outVar = decoRNG.nextFloat() * SMALL_BRANCH_ORIGIN_VARIATION + SMALL_BRANCH_ORIGIN_MIN;
            double angleVar = decoRNG.nextFloat() * SMALL_BRANCH_ANGLE_VARIATION * ((i & 1) == 0 ? 1.0F : -1.0F);
            ChunkCoordinates branchSource = TFGenerator
                    .translateCoords(rsrc.posX, rsrc.posY, rsrc.posZ, length * outVar, angle, tilt);

            drawSmallBranch(
                    world,
                    sbb,
                    branchSource.posX,
                    branchSource.posY,
                    branchSource.posZ,
                    Math.max(length * SMALL_BRANCH_LENGTH_RATIO, MIN_SMALL_BRANCH_LENGTH),
                    angle + angleVar,
                    tilt,
                    leafy);
        }

        if (leafy && !hasLeafDungeon) {
            // leaf blob at the end
            makeLeafBlob(world, sbb, rdest.posX, rdest.posY, rdest.posZ, END_LEAF_RADIUS);
        }

        return true;
    }
}
