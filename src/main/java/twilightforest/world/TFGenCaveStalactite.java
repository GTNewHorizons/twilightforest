package twilightforest.world;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

public class TFGenCaveStalactite extends TFGenerator {

    public static TFGenCaveStalactite diamond = new TFGenCaveStalactite(Blocks.diamond_ore, 0.5F, 4, 16);
    public static TFGenCaveStalactite lapis = new TFGenCaveStalactite(Blocks.lapis_ore, 0.8F, 8, 1);
    public static TFGenCaveStalactite emerald = new TFGenCaveStalactite(Blocks.emerald_ore, 0.5F, 3, 12);
    public static TFGenCaveStalactite gold = new TFGenCaveStalactite(Blocks.gold_ore, 0.6F, 6, 1);
    public static TFGenCaveStalactite redstone = new TFGenCaveStalactite(Blocks.redstone_ore, 0.8F, 8, 1);
    public static TFGenCaveStalactite iron = new TFGenCaveStalactite(Blocks.iron_ore, 0.7F, 8, 1);
    public static TFGenCaveStalactite coal = new TFGenCaveStalactite(Blocks.coal_ore, 0.8F, 12, 1);
    public static TFGenCaveStalactite glowstone = new TFGenCaveStalactite(Blocks.glowstone, 0.5F, 8, 1);

    private static List<TFGenCaveStalactite> hill3 = new ArrayList<TFGenCaveStalactite>();
    private static List<TFGenCaveStalactite> hill2 = new ArrayList<TFGenCaveStalactite>();
    private static List<TFGenCaveStalactite> hill1 = new ArrayList<TFGenCaveStalactite>();

    public Block blockID;
    public int blockMeta;
    public boolean hang;
    public float sizeFactor;
    public int maxLength;
    public int minHeight;

    /**
     * Initializes a stalactite builder. Actually also makes stalagmites
     * 
     * @param size
     * @param stone
     */
    public TFGenCaveStalactite(Block blockType, float size, boolean down) {
        this(blockType, 0, size, down);
    }

    /**
     * Initializes a stalactite builder. Actually also makes stalagmites
     * 
     * @param size
     * @param stone
     */
    public TFGenCaveStalactite(Block blockType, int meta, float size, boolean down) {
        this.blockID = blockType;
        this.blockMeta = meta;
        this.sizeFactor = size;
        this.maxLength = -1;
        this.minHeight = -1;
        this.hang = down;
    }

    /**
     * Initializes a stalactite builder
     */
    public TFGenCaveStalactite(Block blockType, float size, int maxLength, int minHeight) {
        this(blockType, 0, size, maxLength, minHeight);
    }

    /**
     * Initializes a stalactite builder
     */
    public TFGenCaveStalactite(Block blockType, int meta, float size, int maxLength, int minHeight) {
        this.blockID = blockType;
        this.blockMeta = meta;
        this.sizeFactor = size;
        this.maxLength = maxLength;
        this.minHeight = minHeight;
        this.hang = true;
    }

    /**
     * !!!REMOVES ALL THE STALACTITES. ONLY CALL IF YOUR MOD OVERWRITES ORES ENTIRELY!!!
     */
    public static void removeAllStalactites() {
        hill3.clear();
        hill2.clear();
        hill1.clear();
    }

    /**
     * For other mods to add stalactites made of their blocks
     * 
     * @param block     Block to generate stalactite of. Most likely ore
     * @param size      How much space between ceiling and floor stalactite takes. From 0.0f to 1.0f
     * @param maxLength Maximum stalactite length. For when you want to make it big, but not too big
     * @param minHeight Minimum stalactite length. For when you want to make it small, but not too small
     * @param hillLevel Level of the hill for the resource to spawn in. From 1 to 3. Resources from low-level hills will
     *                  spawn in high level hills as well. Examples: 3 level - diamond, lapis, emerald; 2 level - gold,
     *                  redstone; 1 level - iron, coal, glowstone
     * @param weight    How often should resource generate comparing to other resources of the same level. I.e. the
     *                  larger this number is, the more often this resource will generate. Examples: diamond, lapis - 2;
     *                  emerald - 1 | redstone - 2, gold - 1 | iron, coal - 2, glowstone - 1
     */
    public static void addNewStalactite(Block block, float size, int maxLength, int minHeight, int hillLevel,
            int weight) {
        addNewStalactite(block, 0, size, maxLength, minHeight, hillLevel, weight);
    }

    /**
     * For other mods to add stalactites made of their blocks
     * 
     * @param block     Block to generate stalactite of. Most likely ore
     * @param meta      Block metadata in case it needs one
     * @param size      How much space between ceiling and floor stalactite takes. From 0.0f to 1.0f
     * @param maxLength Maximum stalactite length. For when you want to make it big, but not too big
     * @param minHeight Minimum stalactite length. For when you want to make it small, but not too small
     * @param hillLevel Level of the hill for the resource to spawn in. From 1 to 3. Resources from low-level hills will
     *                  spawn in high level hills as well. Examples: 3 level - diamond, lapis, emerald; 2 level - gold,
     *                  redstone; 1 level - iron, coal, glowstone
     * @param weight    How often should resource generate comparing to other resources of the same level. I.e. the
     *                  larger this number is, the more often this resource will generate. Examples: diamond, lapis - 2;
     *                  emerald - 1 | redstone - 2, gold - 1 | iron, coal - 2, glowstone - 1
     */
    public static void addNewStalactite(Block block, int meta, float size, int maxLength, int minHeight, int hillLevel,
            int weight) {
        addStalactite(new TFGenCaveStalactite(block, meta, size, maxLength, minHeight), hillLevel, weight);
    }

    /**
     * For other mods to add stalactites made of their blocks
     * 
     * @param block     Block to generate stalactite of. Most likely ore
     * @param meta      Block metadata in case it needs one
     * @param size      How much space between ceiling and floor stalactite takes. From 0.0f to 1.0f
     * @param hillLevel Level of the hill for the resource to spawn in. From 1 to 3. Resources from low-level hills will
     *                  spawn in high level hills as well. Examples: 3 level - diamond, lapis, emerald; 2 level - gold,
     *                  redstone; 1 level - iron, coal, glowstone
     * @param weight    How often should resource generate comparing to other resources of the same level. I.e. the
     *                  larger this number is, the more often this resource will generate. Examples: diamond, lapis - 2;
     *                  emerald - 1 | redstone - 2, gold - 1 | iron, coal - 2, glowstone - 1
     */
    public static void addNewStalactite(Block block, int meta, float size, int hillLevel, int weight) {
        addNewStalactite(block, meta, size, -1, -1, hillLevel, weight);
    }

    /**
     * For other mods to add stalactites made of their blocks
     * 
     * @param block     Block to generate stalactite of. Most likely ore
     * @param size      How much space between ceiling and floor stalactite takes. From 0.0f to 1.0f
     * @param hillLevel Level of the hill for the resource to spawn in. From 1 to 3. Resources from low-level hills will
     *                  spawn in high level hills as well. Examples: 3 level - diamond, lapis, emerald; 2 level - gold,
     *                  redstone; 1 level - iron, coal, glowstone
     * @param weight    How often should resource generate comparing to other resources of the same level. I.e. the
     *                  larger this number is, the more often this resource will generate. Examples: diamond, lapis - 2;
     *                  emerald - 1 | redstone - 2, gold - 1 | iron, coal - 2, glowstone - 1
     */
    public static void addNewStalactite(Block block, float size, int hillLevel, int weight) {
        addNewStalactite(block, 0, size, -1, -1, hillLevel, weight);
    }

    public static void addStalactite(TFGenCaveStalactite stalactite, int hillLevel, int weight) {
        for (int i = 0; i < weight; i++) switch (hillLevel) {
            default:
            case 1:
                hill1.add(stalactite);
                break;
            case 2:
                hill2.add(stalactite);
                break;
            case 3:
                hill3.add(stalactite);
                break;
        }
    }

    public static void registerVanillaStalactites() {
        addStalactite(diamond, 3, 2);
        addStalactite(lapis, 3, 2);
        addStalactite(emerald, 3, 1);

        addStalactite(redstone, 2, 2);
        addStalactite(gold, 2, 1);

        addStalactite(iron, 1, 2);
        addStalactite(coal, 1, 2);
        addStalactite(glowstone, 1, 1);
    }

    /**
     * Makes a random stalactite appropriate to the cave size
     * 
     * All include iron, coal and glowstone.
     * 
     * Gold and redstone appears in size 2 and larger caves.
     * 
     * Diamonds and lapis only appear in size 3 and larger caves.
     */
    public static TFGenCaveStalactite makeRandomOreStalactite(Random rand, int hillSize) {
        if (hillSize >= 3 || (hillSize >= 2 && rand.nextInt(5) == 0)) {
            if (rand.nextInt(13) <= 4) // To keep vanilla generation chances
                return hill3.get(rand.nextInt(hill3.size()));
        }

        if (hillSize >= 2 || (hillSize >= 1 && rand.nextInt(5) == 0)) {
            if (rand.nextInt(6) <= 2) // To keep vanilla generation chances
                return hill2.get(rand.nextInt(hill2.size()));
        }

        // fall through to size 1
        return hill1.get(rand.nextInt(hill1.size()));
    }

    /**
     * Generates a stalactite at the specified location. The coordinates should be inside a cave. This will return false
     * if it can't find a valid ceiling and floor, or if there are other errors.
     */
    public boolean generate(World world, Random random, int x, int y, int z) {
        int ceiling = Integer.MAX_VALUE;
        int floor = -1;

        // find a ceiling
        for (int ty = y; ty < TFWorld.CHUNKHEIGHT; ty++) {
            Material m = world.getBlock(x, ty, z).getMaterial();
            // if we're in air, continue
            if (m == Material.air) {
                continue;
            }
            // if we get something that's not cave material, fail!
            if (m != Material.ground && m != Material.rock) {
                return false;
            }
            // okay, we found a valid ceiling.
            ceiling = ty;
            break;
        }
        // if we didn't find a ceiling, fail.
        if (ceiling == Integer.MAX_VALUE) {
            return false;
        }

        // find a floor
        for (int ty = y; ty > 4; ty--) {
            Material m = world.getBlock(x, ty, z).getMaterial();
            // if we're in air, continue
            if (m == Material.air) {
                continue;
            }
            // if we get something that's not cave material, fail!
            // actually stalactites can hang above water or lava
            if (m != Material.ground && m != Material.rock
                    && (!hang && m != Material.water)
                    && (!hang && m != Material.lava)) {
                return false;
            }
            // okay, we found a valid floor.
            floor = ty;
            break;
        }

        int length = (int) ((ceiling - floor) * this.sizeFactor * random.nextFloat());

        // check max length
        if (this.maxLength > -1 && length > this.maxLength) {
            length = this.maxLength;
        }

        // check minimum height
        if (this.minHeight > -1 && ceiling - floor - length < this.minHeight) {
            return false;
        }

        return makeSpike(world, random, x, hang ? ceiling : floor, z, length);
    }

    public boolean makeSpike(World world, Random random, int x, int y, int z, int maxLength) {

        int diameter = (int) (maxLength / 4.5); // diameter of the base

        // let's see...
        for (int dx = -diameter; dx <= diameter; dx++) {
            for (int dz = -diameter; dz <= diameter; dz++) {
                // determine how long this spike will be.
                int absx = Math.abs(dx);
                int absz = Math.abs(dz);
                int dist = (int) (Math.max(absx, absz) + (Math.min(absx, absz) * 0.5));
                int spikeLength = 0;

                if (dist == 0) {
                    spikeLength = maxLength;
                }

                if (dist > 0) {
                    spikeLength = random.nextInt((int) (maxLength / (dist + 0.25)));
                }

                int dir = hang ? -1 : 1;

                // check if we're generating over anything
                if (!world.getBlock(x + dx, y - dir, z + dz).getMaterial().isSolid()) {
                    spikeLength = 0;
                }

                for (int dy = 0; dy != (spikeLength * dir); dy += dir) {
                    setBlockAndMetadata(world, x + dx, y + dy, z + dz, blockID, blockMeta);
                }
            }
        }

        return true;
    }

}
