package twilightforest.structures;

import java.util.Random;

import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;

import twilightforest.block.TFBlocks;

public class ComponentTFNagaCourtyard extends StructureTFComponent {

    static int RADIUS = 46;
    static int DIAMETER = 2 * RADIUS + 1;
    private static final int PILLAR_HEIGHT = 8;

    public ComponentTFNagaCourtyard() {
        super();
        // TODO Auto-generated constructor stub
    }

    public ComponentTFNagaCourtyard(World world, Random rand, int i, int x, int y, int z) {
        super(i);

        this.setCoordBaseMode(0);

        this.boundingBox = StructureTFComponent
                .getComponentToAddBoundingBox(x, y, z, -RADIUS, -1, -RADIUS, RADIUS * 2, 10, RADIUS * 2, 0);

    }

    /**
     * At the moment, this just draws the whole courtyard as a giant 93x93 block every time. We may break this up
     * properly later.
     *
     * TODO: what I just said
     */
    @Override
    public boolean addComponentParts(World world, Random rand, StructureBoundingBox sbb) {
        // add a broken up courtyard
        for (int fx = 0; fx <= DIAMETER; fx++) {
            for (int fz = 0; fz <= DIAMETER; fz++) {
                if (rand.nextInt(3) == 0) {
                    placeBlockAtCurrentPosition(world, Blocks.double_stone_slab, 0, fx, 0, fz, sbb);

                    // put some half slabs around
                    if (rand.nextInt(20) == 0) {
                        placeBlockAtCurrentPosition(world, Blocks.stone_slab, 0, fx, 1, fz, sbb);
                    } else {
                        placeBlockAtCurrentPosition(world, Blocks.air, 0, fx, 1, fz, sbb); // clear out grass or flowers
                    }
                } else {
                    placeBlockAtCurrentPosition(world, Blocks.grass, 0, fx, 0, fz, sbb);
                }
            }
        }

        // fence! except not a fence, more of a wall
        for (int fx = 0; fx <= DIAMETER; fx++) {
            for (int y = 0; y < 4; y++) {
                randomBrick(world, rand, fx, y, DIAMETER, sbb);
                randomBrick(world, rand, fx, y, 0, sbb);
            }
            placeOppositeZEdges(world, Blocks.stone_slab, 5, fx, 4, sbb);

            // make nagastone pattern!
            switch (fx % 23) {
                case 2:
                case 5:
                case 9:
                case 13:
                case 17:
                    placeOppositeZEdges(world, TFBlocks.nagastoneBody, 4, fx, 3, sbb);
                    placeOppositeZEdges(world, TFBlocks.nagastoneBody, 4, fx, 2, sbb);
                    placeOppositeZEdges(world, TFBlocks.nagastoneBody, 4, fx, 1, sbb);
                    break;
                case 3:
                    placeOppositeZEdges(world, TFBlocks.nagastoneHead, 8, fx, 3, sbb);
                    placeOppositeZEdges(world, TFBlocks.nagastoneBody, 9, fx, 1, sbb);
                    break;
                case 4:
                case 8:
                    placeOppositeZEdges(world, TFBlocks.nagastoneBody, 9, fx, 1, sbb);
                    break;
                case 6:
                case 10:
                    placeOppositeZEdges(world, TFBlocks.nagastoneBody, 9, fx, 3, sbb);
                    break;
                case 7:
                case 11:
                case 15:
                case 19:
                case 22:
                    placeOppositeZEdges(world, TFBlocks.nagastoneBody, 5, fx, 3, sbb);
                    placeOppositeZEdges(world, TFBlocks.nagastoneBody, 5, fx, 2, sbb);
                    placeOppositeZEdges(world, TFBlocks.nagastoneBody, 5, fx, 1, sbb);
                    break;
                case 14:
                case 18:
                    placeOppositeZEdges(world, TFBlocks.nagastoneBody, 8, fx, 3, sbb);
                    break;
                case 16:
                case 20:
                    placeOppositeZEdges(world, TFBlocks.nagastoneBody, 8, fx, 1, sbb);
                    break;
                case 21:
                    placeOppositeZEdges(world, TFBlocks.nagastoneHead, 9, fx, 3, sbb);
                    placeOppositeZEdges(world, TFBlocks.nagastoneBody, 8, fx, 1, sbb);
                    break;
            }

            // if (fx % 2 == 0) {
            // placeBlockAtCurrentPosition(world, Blocks.stone_slab, 5, fx, 4, DIAMETER, sbb);
            // placeBlockAtCurrentPosition(world, Blocks.stonebrick, 0, fx, 4, 0, sbb);
            // }
            // else
            // {
            // placeBlockAtCurrentPosition(world, Blocks.stone_slab, 5, fx, 4, 0, sbb);
            // placeBlockAtCurrentPosition(world, Blocks.stonebrick, 0, fx, 4, DIAMETER, sbb);
            // }
        }

        for (int fz = 0; fz <= DIAMETER; fz++) {
            for (int y = 0; y < 4; y++) {
                randomBrick(world, rand, DIAMETER, y, fz, sbb);
                randomBrick(world, rand, 0, y, fz, sbb);
            }
            placeOppositeXEdges(world, Blocks.stone_slab, 5, fz, 4, sbb);

            // make nagastone pattern!
            switch (fz % 23) {
                case 2:
                case 5:
                case 9:
                case 13:
                case 17:
                    placeOppositeXEdges(world, TFBlocks.nagastoneBody, 7, fz, 3, sbb);
                    placeOppositeXEdges(world, TFBlocks.nagastoneBody, 7, fz, 2, sbb);
                    placeOppositeXEdges(world, TFBlocks.nagastoneBody, 7, fz, 1, sbb);
                    break;
                case 3:
                    placeBlockAtCurrentPosition(world, TFBlocks.nagastoneHead, 11, DIAMETER, 3, fz, sbb);
                    placeBlockAtCurrentPosition(world, TFBlocks.nagastoneHead, 10, 0, 3, fz, sbb);
                    placeBlockAtCurrentPosition(world, TFBlocks.nagastoneBody, 11, DIAMETER, 1, fz, sbb);
                    placeBlockAtCurrentPosition(world, TFBlocks.nagastoneBody, 10, 0, 1, fz, sbb);
                    break;
                case 4:
                case 8:
                    placeOppositeXEdges(world, TFBlocks.nagastoneBody, 10, fz, 1, sbb);
                    break;
                case 6:
                case 10:
                    placeOppositeXEdges(world, TFBlocks.nagastoneBody, 10, fz, 3, sbb);
                    break;
                case 7:
                case 11:
                case 15:
                case 19:
                case 22:
                    placeOppositeXEdges(world, TFBlocks.nagastoneBody, 6, fz, 3, sbb);
                    placeOppositeXEdges(world, TFBlocks.nagastoneBody, 6, fz, 2, sbb);
                    placeOppositeXEdges(world, TFBlocks.nagastoneBody, 6, fz, 1, sbb);
                    break;
                case 14:
                case 18:
                    placeOppositeXEdges(world, TFBlocks.nagastoneBody, 11, fz, 3, sbb);
                    break;
                case 16:
                case 20:
                    placeOppositeXEdges(world, TFBlocks.nagastoneBody, 11, fz, 1, sbb);
                    break;
                case 21:
                    placeOppositeXEdges(world, TFBlocks.nagastoneHead, 10, fz, 3, sbb);
                    placeOppositeXEdges(world, TFBlocks.nagastoneBody, 11, fz, 1, sbb);
                    break;
            }

            // if (fz % 2 == 0) {
            // placeBlockAtCurrentPosition(world, Blocks.stone_slab, 5, DIAMETER, 4, fz, sbb);
            // placeBlockAtCurrentPosition(world, Blocks.stonebrick, 0, 0, 4, fz, sbb);
            // }
            // else
            // {
            // placeBlockAtCurrentPosition(world, Blocks.stone_slab, 5, 0, 4, fz, sbb);
            // placeBlockAtCurrentPosition(world, Blocks.stonebrick, 0, DIAMETER, 4, fz, sbb);
            // }
        }

        // make a new rand here because we keep getting different results and this actually matters... or should the
        // pillars be different StructureComponents?
        Random pillarRand = new Random(world.getSeed() + this.boundingBox.minX * this.boundingBox.minZ);

        // pick a few spots and make pillars
        for (int i = 0; i < 20; i++) {
            int rx = 2 + pillarRand.nextInt(DIAMETER - 4);
            int rz = 2 + pillarRand.nextInt(DIAMETER - 4);

            makePillar(world, pillarRand, rx, 1, rz, sbb);
        }

        // naga spawner seems important
        placeBlockAtCurrentPosition(world, TFBlocks.bossSpawner, 0, RADIUS + 1, 2, RADIUS + 1, sbb);

        return true;
    }

    /**
     * Make a pillar out of stone brick
     */
    public boolean makePillar(World world, Random rand, int x, int y, int z, StructureBoundingBox sbb) {
        // make the base
        for (int dz = -1; dz <= 1; dz++) {
            for (int dx = -1; dx <= 1; dx++) {
                if (dx != 0 || dz != 0) {
                    placeBlockAtCurrentPosition(world, Blocks.stone_slab, 0, x + dx, y, z + dz, sbb);
                }
            }
        }

        // make the pillar
        for (int i = 0; i < PILLAR_HEIGHT; i++) {
            randomBrick(world, rand, x, y + i, z, sbb);
            if (i > 0 && rand.nextInt(2) == 0) {
                // vines?
                switch (rand.nextInt(4)) {
                    case 0:
                        placeBlockAtCurrentPosition(world, Blocks.vine, 8, x - 1, y + i, z + 0, sbb);
                        break;
                    case 1:
                        placeBlockAtCurrentPosition(world, Blocks.vine, 2, x + 1, y + i, z + 0, sbb);
                        break;
                    case 2:
                        placeBlockAtCurrentPosition(world, Blocks.vine, 4, x + 0, y + i, z + 1, sbb);
                        break;
                    case 3:
                        placeBlockAtCurrentPosition(world, Blocks.vine, 1, x + 0, y + i, z - 1, sbb);
                        break;
                }
            } else if (i > 0 && rand.nextInt(4) == 0) {
                // fireflies!
                switch (rand.nextInt(4)) {
                    case 0:
                        placeBlockAtCurrentPosition(world, TFBlocks.firefly, 0, x - 1, y + i, z + 0, sbb);
                        break;
                    case 1:
                        placeBlockAtCurrentPosition(world, TFBlocks.firefly, 0, x + 1, y + i, z + 0, sbb);
                        break;
                    case 2:
                        placeBlockAtCurrentPosition(world, TFBlocks.firefly, 0, x + 0, y + i, z + 1, sbb);
                        break;
                    case 3:
                        placeBlockAtCurrentPosition(world, TFBlocks.firefly, 0, x + 0, y + i, z - 1, sbb);
                        break;
                }
            }
        }

        // top?
        for (int dz = -1; dz <= 1; dz++) {
            for (int dx = -1; dx <= 1; dx++) {
                int meta = dx == 0 && dz == 0 ? 5 : 0;
                placeBlockAtCurrentPosition(world, Blocks.stone_slab, meta, x + dx, y + PILLAR_HEIGHT, z + dz, sbb);
            }
        }

        return true;
    }

    // keep far side placements first, chunk clipping and block update order depend on it
    private void placeOppositeZEdges(World world, net.minecraft.block.Block block, int meta, int x, int y,
            StructureBoundingBox sbb) {
        placeBlockAtCurrentPosition(world, block, meta, x, y, DIAMETER, sbb);
        placeBlockAtCurrentPosition(world, block, meta, x, y, 0, sbb);
    }

    private void placeOppositeXEdges(World world, net.minecraft.block.Block block, int meta, int z, int y,
            StructureBoundingBox sbb) {
        placeBlockAtCurrentPosition(world, block, meta, DIAMETER, y, z, sbb);
        placeBlockAtCurrentPosition(world, block, meta, 0, y, z, sbb);
    }

    /**
     * Places a random stone brick at the specified location
     */
    public void randomBrick(World world, Random rand, int x, int y, int z, StructureBoundingBox sbb) {
        placeBlockAtCurrentPosition(world, Blocks.stonebrick, rand.nextInt(3), x, y, z, sbb);
    }

}
