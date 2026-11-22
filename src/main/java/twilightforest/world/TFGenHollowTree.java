package twilightforest.world;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.Direction;
import net.minecraft.world.World;

import twilightforest.TFTreasure;
import twilightforest.block.BlockTFRoots;
import twilightforest.block.TFBlocks;
import twilightforest.entity.TFCreatures;

public class TFGenHollowTree extends TFGenerator {

    private static final int LEAF_DUNGEON_CHANCE = 8;
    private static final int MIN_HEIGHT = 32;
    private static final int HEIGHT_VARIATION = 64;
    private static final int MIN_DIAMETER = 1;
    private static final int DIAMETER_VARIATION = 4;
    private static final int CROWN_RADIUS_MULTIPLIER = 4;
    private static final int CROWN_CLEARANCE_PADDING = 8;
    private static final int CROWN_BRANCH_PADDING = 4;
    private static final int ROOT_DEPTH = 4;

    protected Block treeBlock = TFBlocks.log;
    protected int treeMeta = 0;
    protected int branchMeta = 12;
    protected Block leafBlock = TFBlocks.leaves;
    protected int leafMeta = 0;
    protected Block rootBlock = TFBlocks.root;
    protected int rootMeta = BlockTFRoots.ROOT_META;

    public TFGenHollowTree() {
        this(false);
    }

    public TFGenHollowTree(boolean par1) {
        super(par1);
    }

    @Override
    public boolean generate(World world, Random random, int x, int y, int z) {

        int height = random.nextInt(HEIGHT_VARIATION) + MIN_HEIGHT;
        int diameter = random.nextInt(DIAMETER_VARIATION) + MIN_DIAMETER;

        // do we have enough height?
        // crown branches can extend higher than the trunk itself
        int crownRadius = crownClearanceRadius(diameter);
        if (y < ROOT_DEPTH || y + height + 2 * crownRadius + CROWN_BRANCH_PADDING
                >= Math.min(TFWorld.MAXHEIGHT, world.getHeight())) {
            // System.out.println("Failed with hollow tree of height " + height);
            return false;
        }
        // System.out.println("Succeeded with hollow tree of height " + height);

        // // are we going to hit something?
        // for (int dx = -diameter; dx <= diameter; dx++)
        // {
        // for (int dz = -diameter; dz <= diameter; dz++)
        // {
        // for (int dy = 1; dy <= height; dy++)
        // {
        // int whatsThere = world.getBlock(dx + x, dy + y, dz + z);
        // if(whatsThere != 0 && whatsThere != Blocks.leaves)
        // {
        // System.out.println("Failed tree due to things at the base at height " + dy);
        // return false;
        // }
        // }
        // }
        // }
        // check if we're on dirt or grass
        Material materialUnder = world.getBlock(x, y - 1, z).getMaterial();
        if (materialUnder != Material.grass && materialUnder != Material.ground) {
            return false;
        }

        // check the top too
        if (!hasClearCrown(world, x, y, z, height, crownRadius)) {
            return false;
        }

        // make a tree!

        // build the trunk
        buildTrunk(world, random, x, y, z, diameter, height);

        // fireflies
        addTrunkInsects(world, random, x, y, z, diameter, height, true);

        // cicadas
        addTrunkInsects(world, random, x, y, z, diameter, height, false);

        // build the crown
        buildFullCrown(world, random, x, y, z, diameter, height);

        // 4-6 branches on the way up (the original count is inclusive)
        int numBranches = random.nextInt(3) + 3;
        for (int i = 0; i <= numBranches; i++) {
            int branchHeight = (int) (height * random.nextDouble() * 0.9) + (height / 10);
            double branchRotation = random.nextDouble();
            makeSmallBranch(world, random, x, y, z, diameter, branchHeight, 4, branchRotation, 0.35D, true);
        }

        // 4-5 roots at the bottom
        buildBranchRing(world, random, x, y, z, diameter, 3, 2, 6, 0, 0.75D, 0, 3, 5, 3, false);

        // several more taproots
        buildBranchRing(world, random, x, y, z, diameter, 1, 2, 8, 0, 0.9D, 0, 3, 5, 3, false);

        return true;
    }

    private static int crownClearanceRadius(int diameter) {
        return diameter * CROWN_RADIUS_MULTIPLIER + CROWN_CLEARANCE_PADDING;
    }

    private static boolean hasClearCrown(World world, int x, int y, int z, int height, int radius) {
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                for (int dy = height - radius; dy <= height + radius; dy++) {
                    int bx = x + dx;
                    int by = y + dy;
                    int bz = z + dz;
                    Block block = world.getBlock(bx, by, bz);
                    if (block != Blocks.air && !block.isLeaves(world, bx, by, bz)) {
                        // System.out.println("Failed tree due to things at the top")
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private void addTrunkInsects(World world, Random random, int x, int y, int z, int diameter, int height,
            boolean fireflies) {
        int count = random.nextInt(3 * diameter) + 5;
        // preserve the inclusive loop,  determines existing insect counts and rng order
        for (int i = 0; i <= count; i++) {
            int insectHeight = (int) (height * random.nextDouble() * 0.9) + (height / 10);
            double insectAngle = random.nextDouble();
            if (fireflies) {
                addFirefly(world, x, y, z, diameter, insectHeight, insectAngle);
            } else {
                addCicada(world, x, y, z, diameter, insectHeight, insectAngle);
            }
        }
    }

    private static boolean isProtected(Block block) {
        return block == Blocks.bedrock || block == Blocks.obsidian || block == TFBlocks.shield;
    }

    protected final void placeRootOrTrunkBlock(World world, int x, int y, int z, Block block, int metadata) {
        if (!isProtected(world.getBlock(x, y, z))) {
            setBlockAndMetadata(world, x, y, z, block, metadata);
        }
    }

    /**
     * Build the crown of the tree
     *
     * @param diameter
     * @param height
     */
    protected void buildFullCrown(World world, Random random, int x, int y, int z, int diameter, int height) {
        int crownRadius = diameter * CROWN_RADIUS_MULTIPLIER + CROWN_BRANCH_PADDING;
        int bvar = diameter + 2;

        // okay, let's do 3-5 main branches starting at the bottom of the crown
        buildBranchRing(
                world,
                random,
                x,
                y,
                z,
                diameter,
                height - crownRadius,
                0,
                crownRadius,
                0,
                0.35D,
                0,
                bvar,
                bvar + 2,
                2,
                true);

        // then, let's do 3-5 medium branches at the crown middle
        buildBranchRing(
                world,
                random,
                x,
                y,
                z,
                diameter,
                height - (crownRadius / 2),
                0,
                crownRadius,
                0,
                0.28D,
                0,
                bvar,
                bvar + 2,
                1,
                true);

        // finally, let's do 2-4 main branches at the crown top
        buildBranchRing(world, random, x, y, z, diameter, height, 0, crownRadius, 0, 0.15D, 0, 2, 4, 2, true);

        // and extra finally, let's do 3-6 medium branches going straight up
        buildBranchRing(
                world,
                random,
                x,
                y,
                z,
                diameter,
                height,
                0,
                (crownRadius / 2),
                0,
                0.05D,
                0,
                bvar,
                bvar + 2,
                1,
                true);

        // this glass sphere approximates where we want our crown
        // drawBlob(x, y + height, z, (byte)crownRadius, (byte)Blocks.glass, false);
    }

    /**
     * Build the crown of the tree. This builds a smaller crown, since the large ones were causing some performance
     * issues
     *
     * @param height
     */
    protected void buildWeakCrown(World world, Random random, int x, int y, int z, int diameter, int height) {
        int crownRadius = 8;
        int bvar = 2;

        // 3-5 medium branches starting at the bottom of the crown
        buildBranchRing(
                world,
                random,
                x,
                y,
                z,
                diameter,
                height - crownRadius,
                0,
                crownRadius,
                0,
                0.35D,
                0,
                bvar,
                bvar + 2,
                1,
                true);

        // 3-5 medium branches at the crown middle
        buildBranchRing(
                world,
                random,
                x,
                y,
                z,
                diameter,
                height - (crownRadius / 2),
                0,
                crownRadius,
                0,
                0.28D,
                0,
                bvar,
                bvar + 2,
                1,
                true);

        // 2-4 medium branches at the crown top
        buildBranchRing(world, random, x, y, z, diameter, height, 0, crownRadius, 0, 0.15D, 0, 2, 4, 1, true);

        // 3-6 medium branches going straight up
        buildBranchRing(
                world,
                random,
                x,
                y,
                z,
                diameter,
                height,
                0,
                (crownRadius / 2),
                0,
                0.05D,
                0,
                bvar,
                bvar + 2,
                1,
                true);
    }

    /**
     * Build a ring of branches around the tree size 0 = small, 1 = med, 2 = large, 3 = root
     */
    protected void buildBranchRing(World world, Random random, int x, int y, int z, int diameter, int branchHeight,
            int heightVar, int length, int lengthVar, double tilt, double tiltVar, int minBranches, int maxBranches,
            int size, boolean leafy) {
        // let's do this!
        // preserve the original inclusive branch count and angular spacing
        int branchCount = random.nextInt(maxBranches - minBranches) + minBranches + 1;
        double branchRotation = 1.0 / branchCount;
        double branchOffset = random.nextDouble();

        for (int i = 0; i < branchCount; i++) {
            int dHeight;
            if (heightVar > 0) {
                dHeight = branchHeight - heightVar + random.nextInt(2 * heightVar);
            } else {
                dHeight = branchHeight;
            }

            switch (size) {
                case 1:
                    makeMedBranch(
                            world,
                            random,
                            x,
                            y,
                            z,
                            diameter,
                            dHeight,
                            length,
                            i * branchRotation + branchOffset,
                            tilt,
                            leafy);
                    break;
                case 2:
                    makeLargeBranch(
                            world,
                            random,
                            x,
                            y,
                            z,
                            diameter,
                            dHeight,
                            length,
                            i * branchRotation + branchOffset,
                            tilt,
                            leafy);
                    break;
                case 3:
                    makeRoot(
                            world,
                            random,
                            x,
                            y,
                            z,
                            diameter,
                            dHeight,
                            length,
                            i * branchRotation + branchOffset,
                            tilt);
                    break;
                default:
                    makeSmallBranch(
                            world,
                            random,
                            x,
                            y,
                            z,
                            diameter,
                            dHeight,
                            length,
                            i * branchRotation + branchOffset,
                            tilt,
                            leafy);
            }
        }
    }

    /**
     * This function builds the hollow trunk of the tree
     */
    protected void buildTrunk(World world, Random random, int x, int y, int z, int diameter, int height) {

        buildTrunkBase(world, x, y, z, diameter);
        int hollow = diameter / 2;

        // build the trunk upwards
        for (int dx = -diameter; dx <= diameter; dx++) {
            for (int dz = -diameter; dz <= diameter; dz++) {
                // determine how far we are from the center
                int dist = trunkDistance(dx, dz);
                for (int dy = 0; dy <= height; dy++) {
                    // make a trunk!
                    if (dist <= diameter && dist > hollow) {
                        placeRootOrTrunkBlock(world, dx + x, dy + y, dz + z, treeBlock, treeMeta);
                    }

                    // fill it with lava!
                    if (dist <= hollow) {
                        // just kidding!
                        // world.setBlock(dx + x, dy + y, dz + z, Blocks.lava);
                    }

                    // how about a ladder? is that okay?
                    if (dist == hollow && dx == hollow) {
                        // putBlockAndMetadata(dx + x, dy + y, dz + z, Blocks.ladder, 4, true);
                        placeRootOrTrunkBlock(world, dx + x, dy + y, dz + z, Blocks.vine, 8);
                    }
                }
            }
        }
    }

    // Fill below the trunk before placing any above-ground logs, so stumps and full trees share the same base.
    protected final void buildTrunkBase(World world, int x, int y, int z, int diameter) {
        int hollow = diameter / 2;

        // go down 4 squares and fill in extra trunk as needed, in case we're on uneven terrain
        for (int dx = -diameter; dx <= diameter; dx++) {
            for (int dz = -diameter; dz <= diameter; dz++) {
                // determine how far we are from the center
                int dist = trunkDistance(dx, dz);
                if (dist > diameter) {
                    continue;
                }

                for (int dy = -ROOT_DEPTH; dy < 0; dy++) {
                    int bx = x + dx;
                    int by = y + dy;
                    int bz = z + dz;
                    if (hasAirAround(world, bx, by, bz)) {
                        placeRootOrTrunkBlock(world, bx, by, bz, treeBlock, dist > hollow ? treeMeta : branchMeta);
                    } else {
                        placeRootOrTrunkBlock(world, bx, by, bz, rootBlock, rootMeta);
                    }
                }
            }
        }
    }

    protected static int trunkDistance(int dx, int dz) {
        int ax = Math.abs(dx);
        int az = Math.abs(dz);
        return (int) (Math.max(ax, az) + (Math.min(ax, az) * 0.5));
    }

    /**
     * Make a branch!
     */
    protected void makeMedBranch(World world, Random random, int x, int y, int z, int diameter, int branchHeight,
            double length, double angle, double tilt, boolean leafy) {
        int[] src = translate(x, y + branchHeight, z, diameter, angle, 0.5);
        makeMedBranch(world, random, src[0], src[1], src[2], length, angle, tilt, leafy);

    }

    /**
     * Make a branch!
     */
    protected void makeMedBranch(World world, Random random, int sx, int sy, int sz, double length, double angle,
            double tilt, boolean leafy) {
        int[] dest = translate(sx, sy, sz, length, angle, tilt);

        // System.out.println("making a branch at angle " + angle);

        drawBresehnam(world, sx, sy, sz, dest[0], dest[1], dest[2], treeBlock, branchMeta);

        // with leaves!

        if (leafy) {
            /*
             * int numLeafBalls = random.nextInt(2) + 1; for(int i = 0; i <= numLeafBalls; i++) { double slength =
             * random.nextDouble() * 0.6 + 0.2; int[] bdst = translate(src[0], src[1], src[2], slength, angle, tilt);
             * drawBlob(bdst[0], bdst[1], bdst[2], 2, leafBlock, false); }
             */

            // and a blob at the end
            drawLeafBlob(world, dest[0], dest[1], dest[2], (byte) 2, leafBlock, leafMeta);
        }

        // and several small branches

        int numShoots = random.nextInt(2) + 1;
        double angleInc = 0.8 / numShoots;

        for (int i = 0; i <= numShoots; i++) {

            double angleVar = (angleInc * i) - 0.4;
            double outVar = (random.nextDouble() * 0.8) + 0.2;
            double tiltVar = (random.nextDouble() * 0.75) + 0.15;

            int[] bsrc = translate(sx, sy, sz, length * outVar, angle, tilt);
            double slength = length * 0.4;

            makeSmallBranch(world, random, bsrc[0], bsrc[1], bsrc[2], slength, angle + angleVar, tilt * tiltVar, leafy);
        }
    }

    /**
     * Make a small branch with a leaf blob at the end
     */
    protected void makeSmallBranch(World world, Random random, int sx, int sy, int sz, double length, double angle,
            double tilt, boolean leafy) {
        int[] dest = translate(sx, sy, sz, length, angle, tilt);

        drawBresehnam(world, sx, sy, sz, dest[0], dest[1], dest[2], treeBlock, branchMeta);

        // System.out.println("making a branch at angle " + angle);
        if (leafy) {
            byte leafRad = (byte) (random.nextInt(2) + 1);
            drawLeafBlob(world, dest[0], dest[1], dest[2], leafRad, leafBlock, leafMeta);
        }
    }

    /**
     * Make a small branch at a certain height
     */
    protected void makeSmallBranch(World world, Random random, int x, int y, int z, int diameter, int branchHeight,
            double length, double angle, double tilt, boolean leafy) {
        int[] src = translate(x, y + branchHeight, z, diameter, angle, 0.5);
        makeSmallBranch(world, random, src[0], src[1], src[2], length, angle, tilt, leafy);
    }

    /**
     * Make a root
     */
    protected void makeRoot(World world, Random random, int x, int y, int z, int diameter, int branchHeight,
            double length, double angle, double tilt) {
        ChunkCoordinates src = translateCoords(x, y + branchHeight, z, diameter, angle, 0.5);
        ChunkCoordinates dest = translateCoords(src.posX, src.posY, src.posZ, length, angle, tilt);

        ChunkCoordinates[] lineArray = getBresehnamArrayCoords(src, dest);
        boolean stillAboveGround = true;
        for (ChunkCoordinates coord : lineArray) {
            if (stillAboveGround && hasAirAround(world, coord.posX, coord.posY, coord.posZ)) {
                placeRootOrTrunkBlock(world, coord.posX, coord.posY, coord.posZ, treeBlock, branchMeta);
                placeRootOrTrunkBlock(world, coord.posX, coord.posY - 1, coord.posZ, treeBlock, branchMeta);
            } else {
                placeRootOrTrunkBlock(world, coord.posX, coord.posY, coord.posZ, rootBlock, rootMeta);
                placeRootOrTrunkBlock(world, coord.posX, coord.posY - 1, coord.posZ, rootBlock, rootMeta);
                stillAboveGround = false;
            }
        }
    }

    /**
     * Make a large, branching "base" branch in a specific location.
     *
     * The large branch will have 1-4 medium branches and several small branches too
     */
    protected void makeLargeBranch(World world, Random random, int sx, int sy, int sz, double length, double angle,
            double tilt, boolean leafy) {
        int[] dest = translate(sx, sy, sz, length, angle, tilt);

        // draw the main branch
        drawBresehnam(world, sx, sy, sz, dest[0], dest[1], dest[2], treeBlock, branchMeta);

        // reinforce it
        // drawBresehnam(src[0], src[1] + 1, src[2], dest[0], dest[1], dest[2], treeBlock, true);
        int reinforcements = random.nextInt(3);
        for (int i = 0; i <= reinforcements; i++) {
            int vx = (i & 2) == 0 ? 1 : 0;
            int vy = (i & 1) == 0 ? 1 : -1;
            int vz = (i & 2) == 0 ? 0 : 1;
            drawBresehnam(world, sx + vx, sy + vy, sz + vz, dest[0], dest[1], dest[2], treeBlock, branchMeta);
        }

        if (leafy) {
            // add a leaf blob at the end
            drawLeafBlob(world, dest[0], dest[1] + 1, dest[2], (byte) 3, leafBlock, leafMeta);
        }

        // go about halfway out and make a few medium branches.
        // the number of medium branches we can support depends on the length of the big branch
        // every other branch switches sides
        int numMedBranches = random.nextInt(Math.max(1, (int) (length / 6))) + random.nextInt(2) + 1;

        for (int i = 0; i <= numMedBranches; i++) {

            double outVar = (random.nextDouble() * 0.3) + 0.3;
            double angleVar = random.nextDouble() * 0.225 * ((i & 1) == 0 ? 1.0 : -1.0);
            int[] bsrc = translate(sx, sy, sz, length * outVar, angle, tilt);

            makeMedBranch(world, random, bsrc[0], bsrc[1], bsrc[2], length * 0.6, angle + angleVar, tilt, leafy);
        }

        // make 1-2 small ones near the base
        int numSmallBranches = random.nextInt(2) + 1;
        for (int i = 0; i <= numSmallBranches; i++) {

            double outVar = (random.nextDouble() * 0.25) + 0.25;
            double angleVar = random.nextDouble() * 0.25 * ((i & 1) == 0 ? 1.0 : -1.0);
            int[] bsrc = translate(sx, sy, sz, length * outVar, angle, tilt);

            makeSmallBranch(
                    world,
                    random,
                    bsrc[0],
                    bsrc[1],
                    bsrc[2],
                    Math.max(length * 0.3, 2),
                    angle + angleVar,
                    tilt,
                    leafy);
        }

        if (random.nextInt(LEAF_DUNGEON_CHANCE) == 0) {
            makeLeafDungeon(world, random, dest[0], dest[1] + 1, dest[2]);
        }

    }

    private void makeLeafDungeon(World world, Random random, int x, int y, int z) {
        // make leaves
        drawLeafBlob(world, x, y, z, 4, leafBlock, leafMeta);
        // wood support
        drawBlob(world, x, y, z, 3, treeBlock, branchMeta);
        // air
        drawBlob(world, x, y, z, 2, Blocks.air, 0);

        // spawner
        world.setBlock(x + 0, y + 1, z + 0, Blocks.mob_spawner, 0, 2);
        TileEntityMobSpawner ms = (TileEntityMobSpawner) world.getTileEntity(x + 0, y + 1, z + 0);
        if (ms != null) {
            ms.func_145881_a().setEntityName(TFCreatures.getSpawnerNameFor("Swarm Spider"));
        }

        // treasure chests?
        makeLeafDungeonChest(world, random, x, y, z);
    }

    private void makeLeafDungeonChest(World world, Random random, int x, int y, int z) {
        // which direction is this chest in?
        int dir = random.nextInt(4);

        x += 2 * Direction.offsetX[dir];
        z += 2 * Direction.offsetZ[dir];

        TFTreasure.tree_cache.generate(world, random, x, y - 1, z);
    }

    /**
     * Make a large, branching "base" branch off of the tree
     */
    protected void makeLargeBranch(World world, Random random, int x, int y, int z, int diameter, int branchHeight,
            double length, double angle, double tilt, boolean leafy) {
        int[] src = translate(x, y + branchHeight, z, diameter, angle, 0.5);
        makeLargeBranch(world, random, src[0], src[1], src[2], length, angle, tilt, leafy);
    }

    /**
     * Add a firefly at the specified height and angle.
     *
     * @param depth how far up the tree
     * @param angle from 0 - 1 rotation around the tree
     */
    protected void addFirefly(World world, int x, int y, int z, int diameter, int fHeight, double fAngle) {
        placeInsect(world, x, y, z, diameter, fHeight, fAngle, TFBlocks.firefly, 0);
    }

    protected void addCicada(World world, int x, int y, int z, int diameter, int fHeight, double fAngle) {
        placeInsect(world, x, y, z, diameter, fHeight, fAngle, TFBlocks.cicada, 1);
    }

    private void placeInsect(World world, int x, int y, int z, int diameter, int height, double angle, Block insect,
            int defaultMetadata) {
        int[] position = translate(x, y + height, z, diameter + 1, angle, 0.5);
        int metadata = insectMetadata(angle, defaultMetadata);
        if (insect.canPlaceBlockAt(world, position[0], position[1], position[2])) {
            setBlockAndMetadata(world, position[0], position[1], position[2], insect, metadata);
        }
    }

    private static int insectMetadata(double angle, int defaultMetadata) {
        angle %= 1.0;
        if (angle > 0.875 || angle <= 0.125) {
            return 3;
        } else if (angle > 0.125 && angle <= 0.375) {
            return 1;
        } else if (angle > 0.375 && angle <= 0.625) {
            return 4;
        } else if (angle > 0.625 && angle <= 0.875) {
            return 2;
        }
        return defaultMetadata;
    }

}
