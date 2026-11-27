package twilightforest.structures.courtyard;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;

import twilightforest.block.TFBlocks;
import twilightforest.structures.StructureTFComponent;

public class ComponentTFNagaCourtyardDecorator extends StructureTFComponent {

    static final float WALL_DECAY = 0.1f;
    static final float WALL_INTEGRITY = 0.9f;

    public ComponentTFNagaCourtyardDecorator() {
        super();
    }

    public ComponentTFNagaCourtyardDecorator(int i, int x, int y, int z, int width, int height, int length) {
        super(i);
        this.coordBaseMode = 0;
        this.boundingBox = new StructureBoundingBox(x, y - 2, z, x + width - 1, y + height - 1, z + length - 1);
    }

    @Override
    public boolean addComponentParts(World world, Random random, StructureBoundingBox structureBoundingBox) {
        // visit only positions in this chunk, the original scans the entire courtyard each time
        int minX = Math.max(0, structureBoundingBox.minX - boundingBox.minX);
        int minY = Math.max(0, structureBoundingBox.minY - boundingBox.minY);
        int minZ = Math.max(0, structureBoundingBox.minZ - boundingBox.minZ);
        int maxX = Math.min(boundingBox.getXSize() - 1, structureBoundingBox.maxX - boundingBox.minX);
        int maxY = Math.min(boundingBox.getYSize() - 1, structureBoundingBox.maxY - boundingBox.minY);
        int maxZ = Math.min(boundingBox.getZSize() - 1, structureBoundingBox.maxZ - boundingBox.minZ);

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    Block block = this.getBlockAtCurrentPosition(world, x, y, z, structureBoundingBox);
                    if (block != Blocks.air) {
                        int metadata = this.getMetadataAtCurrentPosition(world, x, y, z, structureBoundingBox);
                        weatherBlock(world, random, structureBoundingBox, x, y, z, block, metadata);
                    }
                }
            }
        }
        return true;
    }

    private void weatherBlock(World world, Random random, StructureBoundingBox box, int x, int y, int z, Block block,
            int metadata) {
        if (block == Blocks.stonebrick) {
            if (metadata != 3) {
                /*
                 * if(random.nextDouble() > WALL_INTEGRITY && y <= 10) this.placeBlockAtCurrentPosition(world,
                 * Blocks.air, 0, x, y, z, structureBoundingBox); else
                 */
                if (random.nextBoolean()) {
                    this.placeBlockAtCurrentPosition(world, Blocks.stonebrick, random.nextInt(2) + 1, x, y, z, box);
                }
            }
        } else if (block == Blocks.stone_slab) {
            weatherSlab(world, random, box, x, y, z, metadata);
        } else if (block == Blocks.stone_brick_stairs) {
            if (random.nextDouble() < WALL_DECAY) {
                this.placeBlockAtCurrentPosition(world, Blocks.stone_stairs, metadata, x, y, z, box);
            }
        } else if (block == TFBlocks.nagastoneEtched) {
            if (random.nextBoolean()) {
                Block replacement = random.nextBoolean() ? TFBlocks.nagastoneEtchedMossy
                        : TFBlocks.nagastoneEtchedWeathered;
                this.placeBlockAtCurrentPosition(world, replacement, 0, x, y, z, box);
            }
        } else if (block == TFBlocks.nagastonePillar) {
            weatherNagastone(
                    world,
                    random,
                    box,
                    x,
                    y,
                    z,
                    metadata,
                    TFBlocks.nagastonePillarMossy,
                    TFBlocks.nagastonePillarWeathered);
        } else if (block == TFBlocks.nagastoneStairsLeft) {
            weatherNagastone(
                    world,
                    random,
                    box,
                    x,
                    y,
                    z,
                    metadata,
                    TFBlocks.nagastoneStairsMossyLeft,
                    TFBlocks.nagastoneStairsWeatheredLeft);
        } else if (block == TFBlocks.nagastoneStairsRight) {
            weatherNagastone(
                    world,
                    random,
                    box,
                    x,
                    y,
                    z,
                    metadata,
                    TFBlocks.nagastoneStairsMossyRight,
                    TFBlocks.nagastoneStairsWeatheredRight);
        }
    }

    private void weatherSlab(World world, Random random, StructureBoundingBox box, int x, int y, int z, int metadata) {
        if (metadata == 0 && y <= 10) {
            if (random.nextDouble() > WALL_INTEGRITY) {
                this.placeBlockAtCurrentPosition(world, Blocks.air, 0, x, y, z, box);
            } else if (random.nextBoolean()) {
                this.placeBlockAtCurrentPosition(world, Blocks.stone_slab, 3, x, y, z, box);
            }
        } else if ((metadata == 5 || metadata == 11) && random.nextDouble() < WALL_DECAY) {
            this.placeBlockAtCurrentPosition(world, Blocks.stone_slab, 3 + (metadata - 5), x, y, z, box);
        }
    }

    private void weatherNagastone(World world, Random random, StructureBoundingBox box, int x, int y, int z,
            int metadata, Block mossy, Block weathered) {
        if (random.nextBoolean()) {
            this.placeBlockAtCurrentPosition(world, random.nextBoolean() ? mossy : weathered, metadata, x, y, z, box);
        }
    }
}
