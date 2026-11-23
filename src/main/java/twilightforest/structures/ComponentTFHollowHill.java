package twilightforest.structures;

import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;

import twilightforest.TFTreasure;
import twilightforest.entity.TFCreatures;
import twilightforest.world.TFGenCaveStalactite;

public class ComponentTFHollowHill extends StructureTFComponent {

    private static final int[] STALACTITE_COUNTS = { 0, 128, 256, 512 };
    private static final int[] SPAWNER_COUNTS = { 0, 1, 4, 9 };
    private static final int[] TREASURE_COUNTS = { 0, 2, 6, 12 };

    int hillSize;
    int radius;

    public ComponentTFHollowHill() {}

    public ComponentTFHollowHill(World world, Random rand, int i, int size, int x, int y, int z) {
        super(i);

        setCoordBaseMode(0);

        // get the size of this hill?
        hillSize = size;
        radius = calculateRadius(hillSize);

        // can we determine the size here?
        boundingBox = StructureTFComponent
                .getComponentToAddBoundingBox(x, y, z, -radius, -3, -radius, radius * 2, 10, radius * 2, 0);
    }

    /**
     * Save to NBT
     */
    @Override
    protected void func_143012_a(NBTTagCompound nbt) {
        super.func_143012_a(nbt);

        nbt.setInteger("hillSize", hillSize);
    }

    /**
     * Load from NBT
     */
    @Override
    protected void func_143011_b(NBTTagCompound nbt) {
        super.func_143011_b(nbt);
        hillSize = nbt.getInteger("hillSize");
        radius = calculateRadius(hillSize);
    }

    private static int calculateRadius(int size) {
        return ((size * 2 + 1) * 8) - 6;
    }

    /**
     * Add on any other components we need. In this case we add the maze below the hill
     */
    @SuppressWarnings("rawtypes")
    @Override
    public void buildComponent(StructureComponent structurecomponent, List list, Random random) {

        // // add a maze
        // ComponentTFHillMaze maze = new ComponentTFHillMaze(1, boundingBox.minX + ((boundingBox.maxX -
        // boundingBox.minX) / 2), boundingBox.minY - 20, boundingBox.minZ + ((boundingBox.maxZ - boundingBox.minZ) /
        // 2), hillSize);
        // list.add(maze);
        // maze.buildComponent(this, list, random);

    }

    /**
     * Add in all the blocks we're adding.
     */
    @Override
    public boolean addComponentParts(World world, Random rand, StructureBoundingBox sbb) {
        // int area = (int)(Math.PI * radius * radius);
        // int sn = area / 16; // number of stalactites (there will actually be around twice this number)
        int sn = STALACTITE_COUNTS[hillSize]; // number of stalactites mga = {0, 3, 9, 18}
        int mg = SPAWNER_COUNTS[hillSize]; // number of monster generators mga = {0, 3, 9, 18} (reduced due to "natural"
        // spawning)
        int tc = TREASURE_COUNTS[hillSize]; // number of treasure chests tca = {0, 2, 6, 12}

        // fill in features

        // monster generators!
        for (int i = 0; i < mg; i++) {
            int[] dest = getCoordsInHill2D(rand);
            String mobID = getMobID(rand);

            placeSpawnerAtCurrentPosition(world, rand, dest[0], rand.nextInt(4), dest[1], mobID, sbb);
            // placeMobSpawner(dest[0], hy + rand.nextInt(4), dest[1]);
        }
        // treasure chests!!
        for (int i = 0; i < tc; i++) {
            int[] dest = getCoordsInHill2D(rand);
            generateTreasureChest(world, dest[0], 0, dest[1], sbb);
        }

        // ore or glowing stalactites! (smaller, less plentiful)
        generateRandomOreStalactites(world, rand, sn, 1, sbb);
        // stone stalactites!
        generateRandomBlockStalactites(world, rand, sn, Blocks.stone, 1.0F, true, 1, sbb);
        // stone stalagmites!
        generateRandomBlockStalactites(world, rand, sn, Blocks.stone, 0.9F, false, 1, sbb);

        // level 3 hills get 2 mid-air wraith spawners
        // if (hillSize == 3) {
        // int[] dest = getEmptyCoordsInHill(hy + 10, 20);
        // placeWraithSpawner(dest[0], hy + 10, dest[1]);
        // dest = getEmptyCoordsInHill(hy + 10, 20);
        // placeWraithSpawner(dest[0], hy + 10, dest[1]);
        // }
        return true;
    }

    // dont combine ore and block passes, their random calls remain ordered
    protected void generateRandomOreStalactites(World world, Random rand, int count, int y, StructureBoundingBox sbb) {
        for (int i = 0; i < count; i++) {
            int[] dest = getCoordsInHill2D(rand);
            generateOreStalactite(world, dest[0], y, dest[1], sbb);
        }
    }

    protected void generateRandomBlockStalactites(World world, Random rand, int count, Block block, float length,
            boolean upwards, int y, StructureBoundingBox sbb) {
        for (int i = 0; i < count; i++) {
            int[] dest = getCoordsInHill2D(rand);
            generateBlockStalactite(world, block, length, upwards, dest[0], y, dest[1], sbb);
        }
    }

    /**
     * Make an RNG and attempt to use it to place a treasure chest
     */
    protected void generateTreasureChest(World world, int x, int y, int z, StructureBoundingBox sbb) {
        // generate an RNG for this chest
        // TODO: MOAR RANDOM!
        Random chestRNG = new Random(world.getSeed() + x * z);

        // try placing it
        placeTreasureAtCurrentPosition(
                world,
                chestRNG,
                x,
                y,
                z,
                this.hillSize == 3 ? TFTreasure.hill3 : (this.hillSize == 2 ? TFTreasure.hill2 : TFTreasure.hill1),
                sbb);

        // make something for it to stand on, if necessary
        func_151554_b(world, Blocks.cobblestone, 0, x, y - 1, z, sbb);
    }

    /**
     * Generate a random ore stalactite
     */
    protected void generateOreStalactite(World world, int x, int y, int z, StructureBoundingBox sbb) {
        // are the coordinates in our bounding box?
        int dx = getXWithOffset(x, z);
        int dy = getYWithOffset(y);
        int dz = getZWithOffset(x, z);
        if (canGenerateStalactite(world, sbb, dx, dy, dz)) {
            // generate an RNG for this stalactite
            // TODO: MOAR RANDOM!
            Random stalRNG = new Random(world.getSeed() + dx * dz);

            // make the actual stalactite
            TFGenCaveStalactite stalactite = makeRandomOreStalactite(stalRNG);
            stalactite.generate(world, stalRNG, dx, dy, dz);
        }
    }

    protected int getHillSize() {
        return hillSize;
    }

    protected TFGenCaveStalactite makeRandomOreStalactite(Random stalRNG) {
        return TFGenCaveStalactite.makeRandomOreStalactite(stalRNG, hillSize);
    }

    /**
     * Make a random stone stalactite
     */
    protected void generateBlockStalactite(World world, Block blockToGenerate, float length, boolean up, int x, int y,
            int z, StructureBoundingBox sbb) {
        // are the coordinates in our bounding box?
        int dx = getXWithOffset(x, z);
        int dy = getYWithOffset(y);
        int dz = getZWithOffset(x, z);
        if (canGenerateStalactite(world, sbb, dx, dy, dz)) {
            // generate an RNG for this stalactite
            // TODO: MOAR RANDOM!
            Random stalRNG = new Random(world.getSeed() + dx * dz);

            if (hillSize == 1) {
                length *= 1.9F;
            }

            // make the actual stalactite
            new TFGenCaveStalactite(blockToGenerate, length, up).generate(world, stalRNG, dx, dy, dz);
        }
    }

    private boolean canGenerateStalactite(World world, StructureBoundingBox sbb, int x, int y, int z) {
        return sbb.isVecInside(x, y, z) && world.getBlock(x, y, z) != Blocks.mob_spawner;
    }

    /**
     *
     * @param cx
     * @param cz
     * @return true if the coordinates would be inside the hill on the "floor" of the hill
     */
    boolean isInHill(int cx, int cz) {
        int dx = radius - cx;
        int dz = radius - cz;
        return dx * dx + dz * dz < radius * radius;
    }

    /**
     * @return true if the coordinates are inside the hill in 3D
     */
    boolean isInHill(int mapX, int mapY, int mapZ) {
        int dx = boundingBox.minX + radius - mapX;
        int dy = (boundingBox.minY - mapY) * 2; // hill is half as high as it is wide, thus we just double y distance
        // from center. I *think* that
        // math works!
        int dz = boundingBox.minZ + radius - mapZ;
        int dist = dx * dx + dy * dy + dz * dz;
        return dist < radius * radius;
    }

    int[] getCoordsInHill2D(Random rand) {
        return getCoordsInHill2D(rand, radius);
    }

    /**
     *
     * @return a two element array containing some coordinates in the hill
     */
    int[] getCoordsInHill2D(Random rand, int rad) {
        int rx, rz;
        do {
            rx = rand.nextInt(2 * rad);
            rz = rand.nextInt(2 * rad);
        } while (!isInHill(rx, rz));

        return new int[] { rx, rz };
    }

    /**
     * Gets the id of a mob appropriate to the current hill size.
     */
    protected String getMobID(Random rand) {
        return getMobID(rand, hillSize);
    }

    /**
     * Gets the id of a mob appropriate to the specified hill size.
     *
     * @param level
     * @return
     */
    protected String getMobID(Random rand, int level) {
        return switch (level) {
            case 1 -> getLevel1Mob(rand);
            case 2 -> getLevel2Mob(rand);
            case 3 -> getLevel3Mob(rand);
            default ->
                // aaah, default: spider!
                "Spider";
        };
    }

    /**
     * Returns a mob string appropriate for a level 1 hill
     */
    public String getLevel1Mob(Random rand) {
        return switch (rand.nextInt(10)) {
            case 0, 1, 2 -> TFCreatures.getSpawnerNameFor("Swarm Spider");
            case 3, 4, 5 -> "Spider";
            case 6, 7 -> "Zombie";
            case 8 -> "Silverfish";
            case 9 -> TFCreatures.getSpawnerNameFor("Redcap");
            default -> TFCreatures.getSpawnerNameFor("Swarm Spider");
        };
    }

    /**
     * Returns a mob string appropriate for a level 2 hill
     */
    public String getLevel2Mob(Random rand) {
        return switch (rand.nextInt(10)) {
            case 0, 1, 2 -> TFCreatures.getSpawnerNameFor("Redcap");
            case 3, 4, 5 -> "Zombie";
            case 6, 7 -> "Skeleton";
            case 8 -> TFCreatures.getSpawnerNameFor("Swarm Spider");
            case 9 -> "CaveSpider";
            default -> TFCreatures.getSpawnerNameFor("Redcap");
        };
    }

    /**
     * Returns a mob string appropriate for a level 3 hill. The level 3 also has 2 mid-air wraith spawners.
     */
    public String getLevel3Mob(Random rand) {
        return switch (rand.nextInt(11)) {
            case 0 -> TFCreatures.getSpawnerNameFor("Slime Beetle");
            case 1 -> TFCreatures.getSpawnerNameFor("Fire Beetle");
            case 2 -> TFCreatures.getSpawnerNameFor("Pinch Beetle");
            case 3, 4, 5 -> "Skeleton";
            case 6, 7, 8 -> "CaveSpider";
            case 9 -> "Creeper";
            default -> TFCreatures.getSpawnerNameFor("Twilight Wraith");
        };
    }
}
