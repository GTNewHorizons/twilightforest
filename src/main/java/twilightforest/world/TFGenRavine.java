package twilightforest.world;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.gen.MapGenBase;

import twilightforest.TFFeature;
import twilightforest.block.TFBlocks;

public class TFGenRavine extends MapGenBase {

    private static final int CHUNK_SIZE = 16;
    private static final int RAVINE_CHANCE = 127;
    private static final float PI_APPROXIMATION = 3.141593F;

    private final float[] verticalStretch = new float[TFWorld.CHUNKHEIGHT];
    private final FastRandom ravineRNG = new FastRandom(0L);

    public TFGenRavine() {
        this.rand = new FastRandom(0L);
    }

    protected void generateRavine(long seed, int chunkX, int chunkZ, Block[] blockStorage, double x, double y, double z,
            float width, float yaw, float pitch, int step, int totalSteps, double verticalScale) {
        ravineRNG.setSeed(seed);
        FastRandom random = ravineRNG;
        double chunkCenterX = chunkX * CHUNK_SIZE + 8;
        double chunkCenterZ = chunkZ * CHUNK_SIZE + 8;
        float yawVelocity = 0.0F;
        float pitchVelocity = 0.0F;

        if (totalSteps <= 0) {
            int rangeInBlocks = range * CHUNK_SIZE - CHUNK_SIZE;
            totalSteps = rangeInBlocks - random.nextInt(rangeInBlocks / 4);
        }

        boolean singleNode = false;
        if (step == -1) {
            step = totalSteps / 2;
            singleNode = true;
        }

        float verticalWidth = 1.0F;
        for (int height = 0; height < TFWorld.CHUNKHEIGHT; height++) {
            if (height == 0 || random.nextInt(3) == 0) {
                verticalWidth = 1.0F + random.nextFloat() * random.nextFloat() * 1.0F;
            }
            verticalStretch[height] = verticalWidth * verticalWidth;
        }

        for (; step < totalSteps; step++) {
            double horizontalRadius = 1.5D
                    + (double) (MathHelper.sin(((float) step * PI_APPROXIMATION) / (float) totalSteps) * width * 1.0F);
            double verticalRadius = horizontalRadius * verticalScale;
            horizontalRadius *= (double) random.nextFloat() * 0.25D + 0.75D;
            verticalRadius *= (double) random.nextFloat() * 0.25D + 0.75D;

            float cosPitch = MathHelper.cos(pitch);
            float sinPitch = MathHelper.sin(pitch);
            x += MathHelper.cos(yaw) * cosPitch;
            y += sinPitch;
            z += MathHelper.sin(yaw) * cosPitch;
            pitch *= 0.7F;
            pitch += pitchVelocity * 0.05F;
            yaw += yawVelocity * 0.05F;
            pitchVelocity *= 0.8F;
            yawVelocity *= 0.5F;
            pitchVelocity += (random.nextFloat() - random.nextFloat()) * random.nextFloat() * 2.0F;
            yawVelocity += (random.nextFloat() - random.nextFloat()) * random.nextFloat() * 4F;

            if (!singleNode && random.nextInt(4) == 0) {
                continue;
            }

            double distanceX = x - chunkCenterX;
            double distanceZ = z - chunkCenterZ;
            double remainingSteps = totalSteps - step;
            double maxReach = width + 2.0F + 16F;
            if ((distanceX * distanceX + distanceZ * distanceZ) - remainingSteps * remainingSteps
                    > maxReach * maxReach) {
                return;
            }

            if (x < chunkCenterX - 16D - horizontalRadius * 2D || z < chunkCenterZ - 16D - horizontalRadius * 2D
                    || x > chunkCenterX + 16D + horizontalRadius * 2D
                    || z > chunkCenterZ + 16D + horizontalRadius * 2D) {
                continue;
            }

            int minX = MathHelper.floor_double(x - horizontalRadius) - chunkX * CHUNK_SIZE - 1;
            int maxX = (MathHelper.floor_double(x + horizontalRadius) - chunkX * CHUNK_SIZE) + 1;
            int minY = MathHelper.floor_double(y - verticalRadius) - 1;
            int maxY = MathHelper.floor_double(y + verticalRadius) + 1;
            int minZ = MathHelper.floor_double(z - horizontalRadius) - chunkZ * CHUNK_SIZE - 1;
            int maxZ = (MathHelper.floor_double(z + horizontalRadius) - chunkZ * CHUNK_SIZE) + 1;

            if (minX < 0) {
                minX = 0;
            }
            if (maxX > CHUNK_SIZE) {
                maxX = CHUNK_SIZE;
            }
            if (minY < 1) {
                minY = 1;
            }
            if (maxY > TFWorld.CHUNKHEIGHT - 8) {
                maxY = TFWorld.CHUNKHEIGHT - 8;
            }
            if (minZ < 0) {
                minZ = 0;
            }
            if (maxZ > CHUNK_SIZE) {
                maxZ = CHUNK_SIZE;
            }

            boolean hitWater = false;
            for (int blockX = minX; !hitWater && blockX < maxX; blockX++) {
                for (int blockZ = minZ; !hitWater && blockZ < maxZ; blockZ++) {
                    for (int blockY = maxY + 1; !hitWater && blockY >= minY - 1; blockY--) {
                        int index = (blockX * CHUNK_SIZE + blockZ) * TFWorld.CHUNKHEIGHT + blockY;
                        if (blockY < 0 || blockY >= TFWorld.CHUNKHEIGHT) {
                            continue;
                        }
                        if (blockStorage[index] == Blocks.water || blockStorage[index] == Blocks.flowing_water) {
                            hitWater = true;
                        }
                        if (blockY != minY - 1 && blockX != minX
                                && blockX != maxX - 1
                                && blockZ != minZ
                                && blockZ != maxZ - 1) {
                            blockY = minY;
                        }
                    }
                }
            }

            if (hitWater) {
                continue;
            }

            for (int blockX = minX; blockX < maxX; blockX++) {
                double normalizedX = (((double) (blockX + chunkX * CHUNK_SIZE) + 0.5D) - x) / horizontalRadius;
                nextColumn: for (int blockZ = minZ; blockZ < maxZ; blockZ++) {
                    double normalizedZ = (((double) (blockZ + chunkZ * CHUNK_SIZE) + 0.5D) - z) / horizontalRadius;
                    // keep the legacy 1 block offset between sampled height and storage index
                    int index = (blockX * CHUNK_SIZE + blockZ) * TFWorld.CHUNKHEIGHT + maxY;
                    boolean hitGrass = false;
                    if (normalizedX * normalizedX + normalizedZ * normalizedZ >= 1.0D) {
                        continue;
                    }

                    int blockY = maxY - 1;
                    do {
                        if (blockY < minY) {
                            continue nextColumn;
                        }
                        double normalizedY = (((double) blockY + 0.5D) - y) / verticalRadius;
                        if ((normalizedX * normalizedX + normalizedZ * normalizedZ) * (double) verticalStretch[blockY]
                                + (normalizedY * normalizedY) / 6D < 1.0D) {
                            Block currentBlock = blockStorage[index];
                            if (currentBlock == Blocks.grass) {
                                hitGrass = true;
                            }
                            if (currentBlock == Blocks.stone || currentBlock == TFBlocks.trollSteinn
                                    || currentBlock == Blocks.dirt
                                    || currentBlock == Blocks.grass) {
                                blockStorage[index] = Blocks.air;
                                if (hitGrass && blockStorage[index - 1] == Blocks.dirt) {
                                    blockStorage[index - 1] = this.worldObj.getBiomeGenForCoords(
                                            blockX + chunkX * CHUNK_SIZE,
                                            blockZ + chunkZ * CHUNK_SIZE).topBlock;
                                }
                            }
                        }
                        index--;
                        blockY--;
                    } while (true);
                }
            }

            if (singleNode) {
                break;
            }
        }
    }

    @Override
    protected void func_151538_a(World world, int centerChunkX, int centerChunkZ, int currentChunkX, int currentChunkZ,
            Block[] blockStorage) {
        if (rand.nextInt(RAVINE_CHANCE) != 0) {
            return;
        }
        if (!TFFeature.getNearestFeature(currentChunkX, currentChunkZ, world).areChunkDecorationsEnabled) {
            return;
        }

        double x = centerChunkX * CHUNK_SIZE + rand.nextInt(CHUNK_SIZE);
        double y = rand.nextInt(rand.nextInt(40) + 8) + 20;
        double z = centerChunkZ * CHUNK_SIZE + rand.nextInt(CHUNK_SIZE);
        float yaw = rand.nextFloat() * PI_APPROXIMATION * 2.0F;
        float pitch = ((rand.nextFloat() - 0.5F) * 2.0F) / 8F;
        float width = (rand.nextFloat() * 2.0F + rand.nextFloat()) * 2.0F;
        generateRavine(
                rand.nextLong(),
                currentChunkX,
                currentChunkZ,
                blockStorage,
                x,
                y,
                z,
                width,
                yaw,
                pitch,
                0,
                0,
                3D);
    }
}
