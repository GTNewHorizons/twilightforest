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
        this.boundingBox = new StructureBoundingBox(x, y - 2, z, x + width, y + height, z + length);
    }

    @Override
    public boolean addComponentParts(World world, Random random, StructureBoundingBox structureBoundingBox) {
        for (int x = 0; x < boundingBox.getXSize(); x++) {
            for (int y = 0; y < boundingBox.getYSize(); y++) {
                for (int z = 0; z < boundingBox.getZSize(); z++) {
                    Block currentBlock = this.getBlockAtCurrentPosition(world, x, y, z, structureBoundingBox);
                    if (currentBlock == Blocks.air) continue;
                    int currentMeta = this.getMetadataAtCurrentPosition(world, x, y, z, structureBoundingBox);
                    if (currentBlock == Blocks.stonebrick && currentMeta != 3) {
                        /*
                         * if(random.nextDouble() > WALL_INTEGRITY && y <= 10) this.placeBlockAtCurrentPosition(world,
                         * Blocks.air, 0, x, y, z, structureBoundingBox); else
                         */
                        if (random.nextBoolean()) this.placeBlockAtCurrentPosition(
                                world,
                                Blocks.stonebrick,
                                random.nextInt(2) + 1,
                                x,
                                y,
                                z,
                                structureBoundingBox);
                    }
                    if (currentBlock == Blocks.stone_slab && currentMeta == 0 && y <= 10) {
                        if (random.nextDouble() > WALL_INTEGRITY)
                            this.placeBlockAtCurrentPosition(world, Blocks.air, 0, x, y, z, structureBoundingBox);
                        else if (random.nextBoolean()) this.placeBlockAtCurrentPosition(
                                world,
                                Blocks.stone_slab,
                                3,
                                x,
                                y,
                                z,
                                structureBoundingBox);
                    }
                    if (currentBlock == Blocks.stone_slab && (currentMeta == 5 || currentMeta == 11)) {
                        if (random.nextDouble() < WALL_DECAY) this.placeBlockAtCurrentPosition(
                                world,
                                Blocks.stone_slab,
                                3 + (currentMeta - 5),
                                x,
                                y,
                                z,
                                structureBoundingBox);
                    }
                    if (currentBlock == Blocks.stone_brick_stairs) {
                        if (random.nextDouble() < WALL_DECAY) this.placeBlockAtCurrentPosition(
                                world,
                                Blocks.stone_stairs,
                                currentMeta,
                                x,
                                y,
                                z,
                                structureBoundingBox);
                    }
                    if (currentBlock == TFBlocks.nagastoneEtched) {
                        if (random.nextBoolean()) this.placeBlockAtCurrentPosition(
                                world,
                                random.nextBoolean() ? TFBlocks.nagastoneEtchedMossy
                                        : TFBlocks.nagastoneEtchedWeathered,
                                0,
                                x,
                                y,
                                z,
                                structureBoundingBox);
                    }
                    if (currentBlock == TFBlocks.nagastonePillar) {
                        if (random.nextBoolean()) {
                            if (random.nextBoolean()) this.placeBlockAtCurrentPosition(
                                    world,
                                    TFBlocks.nagastonePillarMossy,
                                    currentMeta,
                                    x,
                                    y,
                                    z,
                                    structureBoundingBox);
                            else this.placeBlockAtCurrentPosition(
                                    world,
                                    TFBlocks.nagastonePillarWeathered,
                                    currentMeta,
                                    x,
                                    y,
                                    z,
                                    structureBoundingBox);
                        }
                    }
                    if (currentBlock == TFBlocks.nagastoneStairsLeft) {
                        if (random.nextBoolean()) {
                            if (random.nextBoolean()) this.placeBlockAtCurrentPosition(
                                    world,
                                    TFBlocks.nagastoneStairsMossyLeft,
                                    currentMeta,
                                    x,
                                    y,
                                    z,
                                    structureBoundingBox);
                            else this.placeBlockAtCurrentPosition(
                                    world,
                                    TFBlocks.nagastoneStairsWeatheredLeft,
                                    currentMeta,
                                    x,
                                    y,
                                    z,
                                    structureBoundingBox);
                        }
                    }
                    if (currentBlock == TFBlocks.nagastoneStairsRight) {
                        if (random.nextBoolean()) {
                            if (random.nextBoolean()) this.placeBlockAtCurrentPosition(
                                    world,
                                    TFBlocks.nagastoneStairsMossyRight,
                                    currentMeta,
                                    x,
                                    y,
                                    z,
                                    structureBoundingBox);
                            else this.placeBlockAtCurrentPosition(
                                    world,
                                    TFBlocks.nagastoneStairsWeatheredRight,
                                    currentMeta,
                                    x,
                                    y,
                                    z,
                                    structureBoundingBox);
                        }
                    }
                }
            }
        }
        return true;
    }
}
