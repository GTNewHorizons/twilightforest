package twilightforest.structures.trollcave;

import java.util.List;
import java.util.Random;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;

import twilightforest.block.TFBlocks;
import twilightforest.entity.EntityTFArmoredGiant;
import twilightforest.entity.EntityTFGiantMiner;
import twilightforest.structures.StructureTFComponent;

public class ComponentTFCloudCastle extends StructureTFComponent {

    private static final int GIANT_BLOCK_SHIFT = 2;
    private static final int CLOUD_TREE_Y = 168;

    private boolean minerPlaced;
    private boolean warriorPlaced;

    public ComponentTFCloudCastle() {}

    public ComponentTFCloudCastle(int index, int x, int y, int z) {
        super(index);
        this.setCoordBaseMode(0);

        // adjust x, y, z
        x = (x >> GIANT_BLOCK_SHIFT) << GIANT_BLOCK_SHIFT;
        y = (y >> GIANT_BLOCK_SHIFT) << GIANT_BLOCK_SHIFT;
        z = (z >> GIANT_BLOCK_SHIFT) << GIANT_BLOCK_SHIFT;

        // spawn list!
        this.spawnListIndex = 1;

        this.boundingBox = StructureTFComponent.getComponentToAddBoundingBox(x, y, z, -8, 0, -8, 16, 16, 16, 0);
    }

    /**
     * Save to NBT
     */
    @Override
    protected void func_143012_a(NBTTagCompound par1NBTTagCompound) {
        super.func_143012_a(par1NBTTagCompound);

        par1NBTTagCompound.setBoolean("minerPlaced", this.minerPlaced);
        par1NBTTagCompound.setBoolean("warriorPlaced", this.warriorPlaced);
    }

    /**
     * Load from NBT
     */
    @Override
    protected void func_143011_b(NBTTagCompound par1NBTTagCompound) {
        super.func_143011_b(par1NBTTagCompound);
        this.minerPlaced = par1NBTTagCompound.getBoolean("minerPlaced");
        this.warriorPlaced = par1NBTTagCompound.getBoolean("warriorPlaced");

    }

    @Override
    public void buildComponent(StructureComponent parent, List<StructureComponent> list, Random rand) {
        // up to two trees
        // tree in x direction
        boolean plus = rand.nextBoolean();
        int offset = rand.nextInt(5) - rand.nextInt(5);
        ComponentTFCloudTree treeX = new ComponentTFCloudTree(
                this.getComponentType() + 1,
                boundingBox.minX + (plus ? 16 : -16),
                CLOUD_TREE_Y,
                boundingBox.minZ - 8 + (offset * 4));
        addTree(treeX, list, rand);

        // tree in z direction
        plus = rand.nextBoolean();
        offset = rand.nextInt(5) - rand.nextInt(5);
        ComponentTFCloudTree treeZ = new ComponentTFCloudTree(
                this.getComponentType() + 1,
                boundingBox.minX - 8 + (offset * 4),
                CLOUD_TREE_Y,
                boundingBox.minZ + (plus ? 16 : -16));
        addTree(treeZ, list, rand);

    }

    private void addTree(ComponentTFCloudTree tree, List<StructureComponent> components, Random rand) {
        components.add(tree);
        tree.buildComponent(this, components, rand);
    }

    @Override
    public boolean addComponentParts(World world, Random rand, StructureBoundingBox sbb) {
        // make haus
        this.fillWithMetadataBlocks(
                world,
                sbb,
                0,
                -4,
                0,
                15,
                -1,
                15,
                TFBlocks.fluffyCloud,
                0,
                TFBlocks.fluffyCloud,
                0,
                false);
        this.fillWithMetadataBlocks(
                world,
                sbb,
                0,
                0,
                0,
                15,
                11,
                15,
                TFBlocks.giantCobble,
                0,
                TFBlocks.giantCobble,
                0,
                false);
        this.fillWithMetadataBlocks(
                world,
                sbb,
                0,
                12,
                0,
                15,
                15,
                15,
                TFBlocks.giantLog,
                0,
                TFBlocks.giantLog,
                0,
                false);

        // clear inside
        this.fillWithAir(world, sbb, 4, 0, 4, 11, 11, 11);

        // clear door
        this.fillWithAir(world, sbb, 0, 0, 4, 4, 7, 7);

        // add giants
        spawnMiner(world, sbb);
        spawnWarrior(world, sbb);
        return true;
    }

    private void spawnMiner(World world, StructureBoundingBox sbb) {
        if (minerPlaced) return;

        int x = getXWithOffset(6, 6);
        int y = getYWithOffset(0);
        int z = getZWithOffset(6, 6);
        if (!sbb.isVecInside(x, y, z)) return;

        EntityTFGiantMiner miner = new EntityTFGiantMiner(world);
        miner.setPosition(x, y, z);
        miner.makeNonDespawning();
        // retry on another generation pass if the entity wasnt accepted by the world
        minerPlaced = world.spawnEntityInWorld(miner);
    }

    private void spawnWarrior(World world, StructureBoundingBox sbb) {
        if (warriorPlaced) return;

        int x = getXWithOffset(9, 9);
        int y = getYWithOffset(0);
        int z = getZWithOffset(9, 9);
        if (!sbb.isVecInside(x, y, z)) return;

        EntityTFArmoredGiant warrior = new EntityTFArmoredGiant(world);
        warrior.setPosition(x, y, z);
        warrior.makeNonDespawning();
        warriorPlaced = world.spawnEntityInWorld(warrior);
    }
}
