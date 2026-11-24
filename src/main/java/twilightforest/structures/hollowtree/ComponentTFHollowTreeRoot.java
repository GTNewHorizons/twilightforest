package twilightforest.structures.hollowtree;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;

import twilightforest.block.TFBlocks;
import twilightforest.world.TFGenerator;

public class ComponentTFHollowTreeRoot extends ComponentTFHollowTreeMedBranch {

    private static final String NBT_GROUND_LEVEL = "rootGroundLevel";

    private static final int UNSET_GROUND_LEVEL = -1;
    private static final int MISSING_DIRT_LEVEL = 256;
    private static final int ROOT_START_HEIGHT = 5;
    private static final int ROOT_LOWER_LAYER_OFFSET = -1;
    private static final int ROOT_METADATA = 0;
    private static final int LOG_METADATA = 12;

    private int groundLevel = UNSET_GROUND_LEVEL;

    public ComponentTFHollowTreeRoot() {
        super();
    }

    public ComponentTFHollowTreeRoot(int i, int sx, int sy, int sz, double length, double angle, double tilt,
            boolean leafy) {
        super(i, sx, sy, sz, length, angle, tilt, leafy);
        this.boundingBox = createRootBoundingBox();
    }

    // root generation draws two lines, so the lower one must be included in the bounds
    private StructureBoundingBox createRootBoundingBox() {
        return new StructureBoundingBox(
                Math.min(src.posX, dest.posX),
                Math.min(src.posY, dest.posY) + ROOT_LOWER_LAYER_OFFSET,
                Math.min(src.posZ, dest.posZ),
                Math.max(src.posX, dest.posX),
                Math.max(src.posY, dest.posY),
                Math.max(src.posZ, dest.posZ));
    }

    @Override
    protected void func_143012_a(NBTTagCompound nbt) {
        super.func_143012_a(nbt);
        nbt.setInteger(NBT_GROUND_LEVEL, groundLevel);
    }

    @Override
    protected void func_143011_b(NBTTagCompound nbt) {
        super.func_143011_b(nbt);
        groundLevel = nbt.hasKey(NBT_GROUND_LEVEL) ? nbt.getInteger(NBT_GROUND_LEVEL) : UNSET_GROUND_LEVEL;

        // the medium branch expands its bounds for foliage on load, roots need only two of their own lines
        this.boundingBox = createRootBoundingBox();
    }

    @Override
    public boolean addComponentParts(World world, Random random, StructureBoundingBox sbb) {
        // align the root to the sampled dirt level before generating either layer
        if (groundLevel < 0) {
            int sampledLevel = getSampledDirtLevel(world, sbb);

            // getSampledDirtLevel returns 256 when no dirt, stone, or grass is found
            if (sampledLevel < 0 || sampledLevel >= MISSING_DIRT_LEVEL) {
                return true;
            }

            groundLevel = sampledLevel;
            src.posY = groundLevel + ROOT_START_HEIGHT;
            // keep the original extents and expand for the newly sampled ground height
            // shrinking here could strand blocks in a chunk not yet generated
            this.boundingBox.expandTo(createRootBoundingBox());
        }

        ChunkCoordinates rSrc = toLocalCoordinates(src);
        ChunkCoordinates rDest = toLocalCoordinates(dest);

        drawRootLine(
                world,
                sbb,
                rSrc.posX,
                rSrc.posY,
                rSrc.posZ,
                rDest.posX,
                rDest.posY,
                rDest.posZ,
                TFBlocks.root,
                ROOT_METADATA);
        drawRootLine(
                world,
                sbb,
                rSrc.posX,
                rSrc.posY + ROOT_LOWER_LAYER_OFFSET,
                rSrc.posZ,
                rDest.posX,
                rDest.posY + ROOT_LOWER_LAYER_OFFSET,
                rDest.posZ,
                TFBlocks.root,
                ROOT_METADATA);

        return true;
    }

    /**
     * Draws a line
     */
    protected void drawRootLine(World world, StructureBoundingBox sbb, int x1, int y1, int z1, int x2, int y2, int z2,
            Block blockValue, int metaValue) {
        ChunkCoordinates[] lineCoords = TFGenerator.getBresehnamArrayCoords(x1, y1, z1, x2, y2, z2);

        for (ChunkCoordinates coords : lineCoords) {
            int worldX = getXWithOffset(coords.posX, coords.posZ);
            int worldY = getYWithOffset(coords.posY);
            int worldZ = getZWithOffset(coords.posX, coords.posZ);

            if (!sbb.isVecInside(worldX, worldY, worldZ)) {
                continue;
            }

            Block block = world.getBlock(worldX, worldY, worldZ);

            // three choices here
            if (block == null || !block.isNormalCube(world, worldX, worldY, worldZ)
                    || block.getMaterial() == Material.grass) {
                // air, other non-solid, or grass, make wood block
                placeBlockAtCurrentPosition(
                        world,
                        TFBlocks.log,
                        LOG_METADATA,
                        coords.posX,
                        coords.posY,
                        coords.posZ,
                        sbb);
            } else if (block.getMaterial() == Material.wood) {
                // wood, do nothing
            } else {
                // solid, make root block
                placeBlockAtCurrentPosition(world, blockValue, metaValue, coords.posX, coords.posY, coords.posZ, sbb);
            }
        }
    }
}
