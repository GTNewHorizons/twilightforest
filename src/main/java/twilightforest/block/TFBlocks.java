package twilightforest.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockSlab;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;

import cpw.mods.fml.common.registry.GameRegistry;
import twilightforest.TwilightForestMod;
import twilightforest.item.ItemBlockTFMeta;
import twilightforest.item.ItemBlockTFNagastone;

public class TFBlocks {

    public static Block log;
    public static Block planks;
    public static Block doubleWoodenSlab;
    public static Block woodenSlab;
    public static Block woodenStairsTwilight;
    public static Block woodenStairsCanopy;
    public static Block woodenStairsMangrove;
    public static Block woodenStairsDarkwood;
    public static Block woodenStairsTime;
    public static Block woodenStairsTrans;
    public static Block woodenStairsMine;
    public static Block woodenStairsSort;
    public static Block doorTwilight;
    public static Block doorCanopy;
    public static Block doorMangrove;
    public static Block doorDarkwood;
    public static Block doorTime;
    public static Block doorTrans;
    public static Block doorMine;
    public static Block doorSort;
    public static Block trapdoorTwilight;
    public static Block trapdoorCanopy;
    public static Block trapdoorMangrove;
    public static Block trapdoorDarkwood;
    public static Block trapdoorTime;
    public static Block trapdoorTrans;
    public static Block trapdoorMine;
    public static Block trapdoorSort;
    public static Block leaves;
    public static Block firefly;
    public static Block portal;
    public static Block mazestone;
    public static Block hedge;
    public static Block bossSpawner;
    public static Block fireflyJar;
    public static Block plant;
    public static Block cicada;
    public static Block root;
    public static Block uncraftingTable;
    public static Block fireJet;
    public static Block oldNagastone;
    public static Block nagastoneBody;
    public static Block nagastoneHead;
    public static Block nagastoneEtched;
    public static Block nagastoneEtchedMossy;
    public static Block nagastoneEtchedWeathered;
    public static Block nagastoneStairsLeft;
    public static Block nagastoneStairsRight;
    public static Block nagastoneStairsMossyLeft;
    public static Block nagastoneStairsMossyRight;
    public static Block nagastoneStairsWeatheredLeft;
    public static Block nagastoneStairsWeatheredRight;
    public static Block nagastonePillar;
    public static Block nagastonePillarMossy;
    public static Block nagastonePillarWeathered;
    public static Block spiralStoneBricks;
    public static Block sapling;
    public static Block magicLog;
    public static Block magicLogSpecial;
    public static Block magicLeaves;
    public static Block moonworm;
    public static Block towerWood;
    public static Block towerDevice;
    // public static Block towerAntenna;
    public static Block towerTranslucent;
    public static Block experiment115;
    public static Block trophy;
    public static Block shield;
    public static Block trophyPedestal;
    public static Block auroraBlock;
    public static Block underBrick;
    public static Block thorns;
    public static Block burntThorns;
    public static Block thornRose;
    public static Block leaves3;
    public static Block deadrock;
    public static Block darkleaves;
    public static Block auroraPillar;
    public static BlockSlab auroraSlab;
    public static BlockSlab auroraDoubleSlab;
    public static Block trollSteinn;
    public static Block wispyCloud;
    public static Block fluffyCloud;
    public static Block giantCobble;
    public static Block giantLog;
    public static Block giantLeaves;
    public static Block giantObsidian;
    public static Block uberousSoil;
    public static Block hugeStalk;
    public static Block hugeGloomBlock;
    public static Block trollVidr;
    public static Block unripeTrollBer;
    public static Block trollBer;
    public static Block knightmetalStorage;
    public static Block arcticFurStorage;
    public static Block carminiteStorage;
    public static Block fieryMetalStorage;
    public static Block ironwoodStorage;
    public static Block steeleafStorage;
    public static Block hugeLilyPad;
    public static Block hugeWaterLily;
    public static Block slider;
    public static Block castleBlock;
    public static Block castleMagic;
    public static Block forceField;
    public static Block cinderFurnace;
    public static Block cinderFurnaceLit;
    public static Block cinderLog;
    public static Block castleDoor;
    public static Block castleDoorVanished;
    public static Block castleUnlock;

    public static void registerBlocks() {

        log = (new BlockTFLog()).setBlockName("TFLog");
        planks = (new BlockTFWood()).setBlockName("TFPlanks");
        doubleWoodenSlab = new BlockTFWoodSlab(true).setBlockName("TFWoodSlabDouble");
        woodenSlab = new BlockTFWoodSlab(false).setBlockName("TFWoodSlab");
        woodenStairsTwilight = (new BlockTFWoodStairs(0)).setBlockName("TFWoodStairsTwilight");
        woodenStairsCanopy = (new BlockTFWoodStairs(1)).setBlockName("TFWoodStairsCanopy");
        woodenStairsMangrove = (new BlockTFWoodStairs(2)).setBlockName("TFWoodStairsMangrove");
        woodenStairsDarkwood = (new BlockTFWoodStairs(3)).setBlockName("TFWoodStairsDarkwood");
        woodenStairsTime = (new BlockTFWoodStairs(4)).setBlockName("TFWoodStairsTime");
        woodenStairsTrans = (new BlockTFWoodStairs(5)).setBlockName("TFWoodStairsTrans");
        woodenStairsMine = (new BlockTFWoodStairs(6)).setBlockName("TFWoodStairsMine");
        woodenStairsSort = (new BlockTFWoodStairs(7)).setBlockName("TFWoodStairsSort");
        doorTwilight = (new BlockTFDoor(0)).setBlockName("TFDoorTwilight");
        doorCanopy = (new BlockTFDoor(1)).setBlockName("TFDoorCanopy");
        doorMangrove = (new BlockTFDoor(2)).setBlockName("TFDoorMangrove");
        doorDarkwood = (new BlockTFDoor(3)).setBlockName("TFDoorDarkwood");
        doorTime = (new BlockTFDoor(4)).setBlockName("TFDoorTime");
        doorTrans = (new BlockTFDoor(5)).setBlockName("TFDoorTrans");
        doorMine = (new BlockTFDoor(6)).setBlockName("TFDoorMine");
        doorSort = (new BlockTFDoor(7)).setBlockName("TFDoorSort");
        trapdoorTwilight = (new BlockTFTrapDoor(0)).setBlockName("TFTrapDoorTwilight");
        trapdoorCanopy = (new BlockTFTrapDoor(1)).setBlockName("TFTrapDoorCanopy");
        trapdoorMangrove = (new BlockTFTrapDoor(2)).setBlockName("TFTrapDoorMangrove");
        trapdoorDarkwood = (new BlockTFTrapDoor(3)).setBlockName("TFTrapDoorDarkwood");
        trapdoorTime = (new BlockTFTrapDoor(4)).setBlockName("TFTrapDoorTime");
        trapdoorTrans = (new BlockTFTrapDoor(5)).setBlockName("TFTrapDoorTrans");
        trapdoorMine = (new BlockTFTrapDoor(6)).setBlockName("TFTrapDoorMine");
        trapdoorSort = (new BlockTFTrapDoor(7)).setBlockName("TFTrapDoorSort");
        leaves = (new BlockTFLeaves()).setBlockName("TFLeaves").setBlockTextureName("leaves_oak");
        firefly = (new BlockTFFirefly()).setBlockName("TFFirefly");
        cicada = (new BlockTFCicada()).setBlockName("TFCicada");
        portal = (new BlockTFPortal()).setBlockName("TFPortal");
        mazestone = (new BlockTFMazestone()).setBlockName("TFMazestone");
        hedge = (new BlockTFHedge()).setBlockName("TFHedge");
        bossSpawner = (new BlockTFBossSpawner()).setBlockName("TFBossSpawner");
        fireflyJar = (new BlockTFFireflyJar()).setBlockName("TFFireflyJar");
        plant = (new BlockTFPlant()).setBlockName("TFPlant");
        root = (new BlockTFRoots()).setBlockName("TFRoots");
        uncraftingTable = (new BlockTFUncraftingTable()).setBlockName("TFUncraftingTable");
        fireJet = (new BlockTFFireJet()).setBlockName("TFFireJet");
        oldNagastone = (new BlockTFNagastone()).setBlockName("TFNagastone");
        nagastoneBody = (new BlockTFNagastone2(false)).setBlockName("TFNagastoneBody");
        nagastoneHead = (new BlockTFNagastone2(true)).setBlockName("TFNagastoneHead");
        nagastoneEtched = (new BlockTFNagastoneEtched(NagastoneType.NORMAL)).setBlockName("TFNagastoneEtched");
        nagastoneEtchedMossy = (new BlockTFNagastoneEtched(NagastoneType.MOSSY)).setBlockName("TFNagastoneEtchedMossy");
        nagastoneEtchedWeathered = (new BlockTFNagastoneEtched(NagastoneType.WEATHERED))
                .setBlockName("TFNagastoneEtchedWeathered");
        nagastoneStairsLeft = (new BlockTFNagastoneStairs(nagastoneEtched, false))
                .setBlockName("TFNagastoneStairsLeft");
        nagastoneStairsRight = (new BlockTFNagastoneStairs(nagastoneEtched, true))
                .setBlockName("TFNagastoneStairsRight");
        nagastoneStairsMossyLeft = (new BlockTFNagastoneStairs(nagastoneEtchedMossy, false))
                .setBlockName("TFNagastoneStairsMossyLeft");
        nagastoneStairsMossyRight = (new BlockTFNagastoneStairs(nagastoneEtchedMossy, true))
                .setBlockName("TFNagastoneStairsMossyRight");
        nagastoneStairsWeatheredLeft = (new BlockTFNagastoneStairs(nagastoneEtchedWeathered, false))
                .setBlockName("TFNagastoneStairsWeatheredLeft");
        nagastoneStairsWeatheredRight = (new BlockTFNagastoneStairs(nagastoneEtchedWeathered, true))
                .setBlockName("TFNagastoneStairsWeatheredRight");
        nagastonePillar = (new BlockTFNagastonePillar(NagastoneType.NORMAL)).setBlockName("TFNagastonePillar");
        nagastonePillarMossy = (new BlockTFNagastonePillar(NagastoneType.MOSSY)).setBlockName("TFNagastonePillarMossy");
        nagastonePillarWeathered = (new BlockTFNagastonePillar(NagastoneType.WEATHERED))
                .setBlockName("TFNagastonePillarWeathered");
        spiralStoneBricks = (new BlockTFSpiralBricks()).setBlockName("TFSpiralBricks");
        sapling = (new BlockTFSapling()).setBlockName("TFSapling");
        magicLog = (new BlockTFMagicLog()).setBlockName("TFMagicLog");
        magicLogSpecial = (new BlockTFMagicLogSpecial()).setBlockName("TFMagicLogSpecial");
        magicLeaves = (new BlockTFMagicLeaves()).setBlockName("TFMagicLeaves");
        moonworm = (new BlockTFMoonworm()).setBlockName("TFMoonworm");
        towerWood = (new BlockTFTowerWood()).setBlockName("TFTowerStone");
        towerDevice = (new BlockTFTowerDevice()).setBlockName("TFTowerDevice");
        towerTranslucent = (new BlockTFTowerTranslucent()).setBlockName("TFTowerTranslucent");
        experiment115 = (new BlockTFExperiment115()).setBlockName("TFExperiment115");
        trophy = (new BlockTFTrophy()).setBlockName("TFTrophy");
        shield = (new BlockTFShield()).setBlockName("TFShield");
        trophyPedestal = (new BlockTFTrophyPedestal()).setBlockName("TFTrophyPedestal");
        auroraBlock = (new BlockTFAuroraBrick()).setBlockName("TFAuroraBrick");
        underBrick = (new BlockTFUnderBrick()).setBlockName("TFUnderBrick");
        thorns = (new BlockTFThorns()).setBlockName("TFThorns");
        burntThorns = (new BlockTFBurntThorns()).setBlockName("TFBurntThorns");
        thornRose = (new BlockTFThornRose()).setBlockName("TFThornRose")
                .setBlockTextureName(TwilightForestMod.ID + ":thornRose");
        leaves3 = (new BlockTFLeaves3()).setBlockName("TFLeaves3").setBlockTextureName("leaves_oak");
        deadrock = (new BlockTFDeadrock()).setBlockName("TFDeadrock");
        darkleaves = (new BlockTFDarkLeaves()).setBlockName("DarkLeaves");
        auroraPillar = (new BlockTFAuroraPillar()).setBlockName("AuroraPillar");
        auroraSlab = (BlockSlab) (new BlockTFAuroraSlab(false)).setBlockName("AuroraSlab");
        auroraDoubleSlab = (BlockSlab) (new BlockTFAuroraSlab(true)).setBlockName("AuroraDoubleSlab");
        trollSteinn = (new BlockTFTrollSteinn()).setBlockName("TrollSteinn");
        wispyCloud = (new BlockTFWispyCloud()).setBlockName("WispyCloud");
        fluffyCloud = (new BlockTFFluffyCloud()).setBlockName("FluffyCloud");
        giantCobble = (new BlockTFGiantCobble()).setBlockName("GiantCobble");
        giantLog = (new BlockTFGiantLog()).setBlockName("GiantLog");
        giantLeaves = (new BlockTFGiantLeaves()).setBlockName("GiantLeaves");
        giantObsidian = (new BlockTFGiantObsidian()).setBlockName("GiantObsidian");
        uberousSoil = (new BlockTFUberousSoil()).setBlockName("UberousSoil");
        hugeStalk = (new BlockTFHugeStalk()).setBlockName("HugeStalk");
        hugeGloomBlock = (new BlockTFHugeGloomBlock()).setBlockName("HugeGloomBlock");
        trollVidr = (new BlockTFTrollRoot()).setBlockName("TrollVidr");
        unripeTrollBer = (new BlockTFUnripeTorchCluster()).setBlockName("UnripeTrollBer");
        trollBer = (new BlockTFRipeTorchCluster()).setBlockName("TrollBer");
        knightmetalStorage = (new BlockTFKnightmetalBlock()).setBlockName("KnightmetalBlock");
        arcticFurStorage = (new BlockTFCompressed(BlockTFCompressed.BlockType.ARCTIC_FUR))
                .setBlockName("ArcticFurBlock");
        carminiteStorage = (new BlockTFCompressed(BlockTFCompressed.BlockType.CARMINITE))
                .setBlockName("CarminiteBlock");
        fieryMetalStorage = (new BlockTFCompressed(BlockTFCompressed.BlockType.FIERY_METAL)).setBlockName("FieryBlock");
        ironwoodStorage = (new BlockTFCompressed(BlockTFCompressed.BlockType.IRONWOOD)).setBlockName("IronwoodBlock");
        steeleafStorage = (new BlockTFCompressed(BlockTFCompressed.BlockType.STEELEAF)).setBlockName("SteeleafBlock");
        hugeLilyPad = (new BlockTFHugeLilyPad()).setBlockName("HugeLilyPad");
        hugeWaterLily = (new BlockTFHugeWaterLily()).setBlockName("HugeWaterLily")
                .setBlockTextureName(TwilightForestMod.ID + ":huge_waterlily");
        slider = (new BlockTFSlider()).setBlockName("Slider");
        castleBlock = (new BlockTFCastleBlock()).setBlockName("CastleBrick");
        castleMagic = (new BlockTFCastleMagic()).setBlockName("CastleMagic");
        forceField = (new BlockTFForceField()).setBlockName("ForceField");
        cinderFurnace = (new BlockTFCinderFurnace(false)).setBlockName("CinderFurnaceIdle");
        cinderFurnaceLit = (new BlockTFCinderFurnace(true)).setBlockName("CinderFurnaceLit");
        cinderLog = (new BlockTFCinderLog()).setBlockName("CinderLog");
        castleDoor = (new BlockTFCastleDoor(false)).setBlockName("CastleDoor");
        castleDoorVanished = (new BlockTFCastleDoor(true)).setBlockName("CastleDoorVanished");
        castleUnlock = (new BlockTFCastleUnlock()).setBlockName("CastleUnlock");

        // register blocks with their pickup values
        registerMyBlock(log);
        registerMyBlock(planks);
        registerMyBlock(
                woodenSlab,
                twilightforest.item.ItemBlockTFWoodenSlab.class,
                ((BlockSlab) woodenSlab),
                ((BlockSlab) doubleWoodenSlab),
                false);
        registerMyBlock(
                doubleWoodenSlab,
                twilightforest.item.ItemBlockTFWoodenSlab.class,
                ((BlockSlab) woodenSlab),
                ((BlockSlab) doubleWoodenSlab),
                true);
        registerMyBlock(woodenStairsTwilight);
        registerMyBlock(woodenStairsCanopy);
        registerMyBlock(woodenStairsMangrove);
        registerMyBlock(woodenStairsDarkwood);
        registerMyBlock(woodenStairsTime);
        registerMyBlock(woodenStairsTrans);
        registerMyBlock(woodenStairsMine);
        registerMyBlock(woodenStairsSort);
        registerMyBlock(doorTwilight);
        registerMyBlock(doorCanopy);
        registerMyBlock(doorMangrove);
        registerMyBlock(doorDarkwood);
        registerMyBlock(doorTime);
        registerMyBlock(doorTrans);
        registerMyBlock(doorMine);
        registerMyBlock(doorSort);
        registerMyBlock(trapdoorTwilight);
        registerMyBlock(trapdoorCanopy);
        registerMyBlock(trapdoorMangrove);
        registerMyBlock(trapdoorDarkwood);
        registerMyBlock(trapdoorTime);
        registerMyBlock(trapdoorTrans);
        registerMyBlock(trapdoorMine);
        registerMyBlock(trapdoorSort);
        registerMyBlock(root);
        registerMyBlock(leaves);
        registerMyBlock(firefly, ItemBlock.class);
        registerMyBlock(cicada, ItemBlock.class);
        registerMyBlock(portal, ItemBlock.class);
        registerMyBlock(mazestone);
        registerMyBlock(hedge);
        registerMyBlock(bossSpawner);
        registerMyBlock(fireflyJar, ItemBlock.class);
        registerMyBlock(plant, twilightforest.item.ItemBlockTFPlant.class);
        registerMyBlock(uncraftingTable, ItemBlock.class);
        registerMyBlock(fireJet);
        registerMyBlock(oldNagastone, ItemBlockTFNagastone.class);
        registerMyBlock(nagastoneBody, ItemBlockTFNagastone.class);
        registerMyBlock(nagastoneHead, ItemBlockTFNagastone.class);
        registerMyBlock(nagastoneEtched);
        registerMyBlock(nagastoneEtchedMossy);
        registerMyBlock(nagastoneEtchedWeathered);
        registerMyBlock(nagastoneStairsLeft);
        registerMyBlock(nagastoneStairsRight);
        registerMyBlock(nagastoneStairsMossyLeft);
        registerMyBlock(nagastoneStairsMossyRight);
        registerMyBlock(nagastoneStairsWeatheredLeft);
        registerMyBlock(nagastoneStairsWeatheredRight);
        registerMyBlock(nagastonePillar);
        registerMyBlock(nagastonePillarMossy);
        registerMyBlock(nagastonePillarWeathered);
        registerMyBlock(spiralStoneBricks);
        registerMyBlock(sapling);
        registerMyBlock(moonworm, ItemBlock.class);
        registerMyBlock(magicLog);
        registerMyBlock(magicLeaves);
        registerMyBlock(magicLogSpecial);
        registerMyBlock(towerWood);
        registerMyBlock(towerDevice);
        registerMyBlock(towerTranslucent);
        registerMyBlock(experiment115);
        registerMyBlock(trophy);
        registerMyBlock(shield);
        registerMyBlock(trophyPedestal);
        registerMyBlock(auroraBlock, ItemBlock.class);
        registerMyBlock(underBrick);
        registerMyBlock(
                thorns,
                twilightforest.item.ItemBlockTFThorns.class,
                thorns,
                ((BlockTFThorns) thorns).getNames());
        registerMyBlock(burntThorns, ItemBlock.class);
        registerMyBlock(thornRose, ItemBlock.class);
        registerMyBlock(leaves3);
        registerMyBlock(deadrock, twilightforest.item.ItemBlockTFDeadrock.class, deadrock, BlockTFDeadrock.names);
        registerMyBlock(darkleaves, ItemBlock.class);
        registerMyBlock(auroraPillar, ItemBlock.class);
        registerMyBlock(
                auroraSlab,
                twilightforest.item.ItemBlockTFAuroraSlab.class,
                ((BlockSlab) auroraSlab),
                ((BlockSlab) auroraDoubleSlab),
                false);
        registerMyBlock(
                auroraDoubleSlab,
                twilightforest.item.ItemBlockTFAuroraSlab.class,
                ((BlockSlab) auroraSlab),
                ((BlockSlab) auroraDoubleSlab),
                true);
        registerMyBlock(trollSteinn, ItemBlock.class);
        registerMyBlock(wispyCloud, ItemBlock.class);
        registerMyBlock(fluffyCloud, ItemBlock.class);
        registerMyBlock(giantCobble, ItemBlock.class);
        registerMyBlock(giantLog, ItemBlock.class);
        registerMyBlock(giantLeaves, ItemBlock.class);
        registerMyBlock(giantObsidian, ItemBlock.class);
        registerMyBlock(uberousSoil, ItemBlock.class);
        registerMyBlock(hugeStalk, ItemBlock.class);
        registerMyBlock(hugeGloomBlock, ItemBlock.class);
        registerMyBlock(trollVidr, ItemBlock.class);
        registerMyBlock(unripeTrollBer, ItemBlock.class);
        registerMyBlock(trollBer, ItemBlock.class);
        registerMyBlock(knightmetalStorage, ItemBlock.class);
        registerMyBlock(arcticFurStorage, ItemBlock.class);
        registerMyBlock(carminiteStorage, ItemBlock.class);
        registerMyBlock(fieryMetalStorage, ItemBlock.class);
        registerMyBlock(ironwoodStorage, ItemBlock.class);
        registerMyBlock(steeleafStorage, ItemBlock.class);
        registerMyBlock(hugeLilyPad, twilightforest.item.ItemBlockTFHugeLilyPad.class);
        registerMyBlock(hugeWaterLily, twilightforest.item.ItemBlockTFHugeWaterLily.class);
        registerMyBlock(slider);
        registerMyBlock(castleBlock);
        registerMyBlock(castleMagic);
        registerMyBlock(forceField);
        registerMyBlock(cinderFurnace, ItemBlock.class);
        registerMyBlock(cinderFurnaceLit, ItemBlock.class);
        registerMyBlock(cinderLog);
        registerMyBlock(castleDoor);
        registerMyBlock(castleDoorVanished);

        // fire info
        Blocks.fire.setFireInfo(log, 5, 5);
        Blocks.fire.setFireInfo(planks, 5, 20);
        Blocks.fire.setFireInfo(woodenSlab, 5, 20);
        Blocks.fire.setFireInfo(doubleWoodenSlab, 5, 20);
        Blocks.fire.setFireInfo(woodenStairsTwilight, 5, 20);
        Blocks.fire.setFireInfo(woodenStairsCanopy, 5, 20);
        Blocks.fire.setFireInfo(woodenStairsMangrove, 5, 20);
        Blocks.fire.setFireInfo(woodenStairsDarkwood, 5, 20);
        Blocks.fire.setFireInfo(woodenStairsTime, 5, 20);
        Blocks.fire.setFireInfo(woodenStairsTrans, 5, 20);
        Blocks.fire.setFireInfo(woodenStairsMine, 5, 20);
        Blocks.fire.setFireInfo(woodenStairsSort, 5, 20);
        Blocks.fire.setFireInfo(leaves, 30, 60);
        Blocks.fire.setFireInfo(leaves3, 30, 60);
    }

    private static void registerMyBlock(Block block, Class<? extends ItemBlock> pickup, BlockSlab singleSlab,
            BlockSlab doubleSlab, boolean isDouble) {
        GameRegistry.registerBlock(block, pickup, block.getUnlocalizedName(), singleSlab, doubleSlab, isDouble);

    }

    private static void registerMyBlock(Block block, Class<? extends ItemBlock> pickup, Block blockAgain,
            String[] names) {
        GameRegistry.registerBlock(block, pickup, block.getUnlocalizedName(), blockAgain, names);
    }

    private static void registerMyBlock(Block block, Class<? extends ItemBlock> pickup) {
        GameRegistry.registerBlock(block, pickup, block.getUnlocalizedName());
    }

    private static void registerMyBlock(Block block) {
        GameRegistry.registerBlock(block, ItemBlockTFMeta.class, block.getUnlocalizedName());
    }

}
