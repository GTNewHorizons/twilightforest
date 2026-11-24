package twilightforest.world;

import java.util.Random;

import net.minecraft.block.material.Material;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;

import twilightforest.block.BlockTFRoots;
import twilightforest.block.TFBlocks;

public class TFGenCanopyOak extends TFGenCanopyTree {

    private static final int MAX_BRANCH_SPREAD = 5;
    private static final int CANOPY_HEIGHT_ABOVE_TRUNK = 5;

    public TFGenCanopyOak() {
        this(false);
    }

    public TFGenCanopyOak(boolean notify) {
        super(notify);
        this.treeBlock = TFBlocks.log;
        this.treeMeta = 0;
        this.branchMeta = 12;
        this.leafBlock = TFBlocks.leaves;
        this.leafMeta = 0;
        this.rootBlock = TFBlocks.root;
        this.rootMeta = BlockTFRoots.ROOT_META;
    }

    @Override
    public boolean generate(World world, Random random, int x, int y, int z) {
        // check if we're on dirt or grass
        Material materialUnder = world.getBlock(x, y - 1, z).getMaterial();
        if ((materialUnder != Material.grass && materialUnder != Material.ground) || y >= world.getHeight() - 12) {
            return false;
        }

        // determine a height
        int treeHeight = minHeight;
        if (random.nextInt(chanceAddFirstFive) == 0) {
            treeHeight += random.nextInt(5);

            if (random.nextInt(chanceAddSecondFive) == 0) {
                treeHeight += random.nextInt(5);
            }
        }

        // the highest branch leaves can extend five blocks above the trunk.
        if ((long) y + treeHeight + CANOPY_HEIGHT_ABOVE_TRUNK >= world.getHeight()) {
            return false;
        }

        // okay build a tree! Go up to the height
        buildTrunk(world, x, y, z, treeHeight);

        // make 12 - 20 branches
        int numBranches = 12 + random.nextInt(9);
        float branchAngle = random.nextFloat();
        for (int branch = 0; branch < numBranches; branch++) {
            float branchTilt = 0.15F + (random.nextFloat() * 0.35F);
            buildBranch(world, x, y, z, treeHeight - 10 + (branch / 2), 5, branchAngle, branchTilt, false, random);

            branchAngle += (random.nextFloat() * 0.4F);
            if (branchAngle > 1.0F) {
                branchAngle -= 1.0F;
            }
        }

        makeRoots(world, random, x, y, z);
        makeRoots(world, random, x + 1, y, z);
        makeRoots(world, random, x, y, z + 1);
        makeRoots(world, random, x + 1, y, z + 1);
        return true;
    }

    private void makeRoots(World world, Random random, int x, int y, int z) {
        // root bulb
        if (hasAirAround(world, x, y - 1, z)) {
            this.setBlockAndMetadata(world, x, y - 1, z, treeBlock, treeMeta);
        } else {
            this.setBlockAndMetadata(world, x, y - 1, z, rootBlock, rootMeta);
        }

        // roots!
        int numRoots = 1 + random.nextInt(2);
        float offset = random.nextFloat();
        for (int root = 0; root < numRoots; root++) {
            buildRoot(world, x, y, z, offset, root);
        }
    }

    private void buildTrunk(World world, int x, int y, int z, int treeHeight) {
        for (int dy = 0; dy < treeHeight; dy++) {
            this.setBlockAndMetadata(world, x, y + dy, z, this.treeBlock, this.treeMeta);
            this.setBlockAndMetadata(world, x + 1, y + dy, z, this.treeBlock, this.treeMeta);
            this.setBlockAndMetadata(world, x, y + dy, z + 1, this.treeBlock, this.treeMeta);
            this.setBlockAndMetadata(world, x + 1, y + dy, z + 1, this.treeBlock, this.treeMeta);
        }

        drawLeafBlob(world, x, y + treeHeight, z, 3, leafBlock, leafMeta);
    }

    /**
     * Build a branch with a flat blob of leaves at the end.
     *
     * @param height
     * @param length
     * @param angle
     * @param tilt
     */
    @Override
    void buildBranch(World world, int x, int y, int z, int height, double length, double angle, double tilt,
            boolean trunk, Random treeRNG) {
        ChunkCoordinates source = new ChunkCoordinates(x, y + height, z);
        ChunkCoordinates destination = translateCoords(source.posX, source.posY, source.posZ, length, angle, tilt);

        // constrain branch spread
        destination.posX = limitBranchSpread(destination.posX, x);
        destination.posZ = limitBranchSpread(destination.posZ, z);

        drawBresehnam(
                world,
                source.posX,
                source.posY,
                source.posZ,
                destination.posX,
                destination.posY,
                destination.posZ,
                treeBlock,
                trunk ? treeMeta : branchMeta);

        // do this here until that bug with the lighting is fixed
        if (trunk) {
            // add a firefly (torch) to the trunk
            addFirefly(world, x, y, z, 3 + treeRNG.nextInt(7), treeRNG.nextDouble());
        }

        int blobSize = 2;// + treeRNG.nextInt(2);
        drawLeafBlob(world, destination.posX, destination.posY, destination.posZ, blobSize, leafBlock, leafMeta);

        // makeLeafCircle(world, dest.posX, dest.posY - 1, dest.posZ, 3, leafBlock, leafMeta, true);
        // makeLeafCircle(world, dest.posX, dest.posY, dest.posZ, 4, leafBlock, leafMeta, true);
        // makeLeafCircle(world, dest.posX, dest.posY + 1, dest.posZ, 2, leafBlock, leafMeta, true);

        setBlockAndMetadata(world, destination.posX + 1, destination.posY, destination.posZ, treeBlock, branchMeta);
        setBlockAndMetadata(world, destination.posX - 1, destination.posY, destination.posZ, treeBlock, branchMeta);
        setBlockAndMetadata(world, destination.posX, destination.posY, destination.posZ + 1, treeBlock, branchMeta);
        setBlockAndMetadata(world, destination.posX, destination.posY, destination.posZ - 1, treeBlock, branchMeta);
    }

    private static int limitBranchSpread(int coordinate, int origin) {
        if (coordinate - origin < -MAX_BRANCH_SPREAD) {
            return origin - MAX_BRANCH_SPREAD;
        }
        if (coordinate - origin > MAX_BRANCH_SPREAD) {
            return origin + MAX_BRANCH_SPREAD;
        }
        return coordinate;
    }
}
