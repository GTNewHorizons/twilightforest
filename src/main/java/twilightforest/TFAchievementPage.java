package twilightforest;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.Achievement;

import twilightforest.block.TFBlocks;
import twilightforest.item.TFItems;

public class TFAchievementPage extends net.minecraftforge.common.AchievementPage {

    public static final Achievement twilightPortal = new Achievement(
            TwilightForestMod.ID + 1,
            "twilightPortal",
            -2,
            1,
            TFBlocks.portal,
            null).setSpecial().registerStat();
    public static final Achievement twilightArrival = new Achievement(
            TwilightForestMod.ID + 2,
            "twilightArrival",
            0,
            0,
            new ItemStack(TFBlocks.log, 1, 9),
            twilightPortal).registerStat();
    public static final Achievement twilightHunter = new Achievement(
            TwilightForestMod.ID + 3,
            "twilightHunter",
            2,
            2,
            TFItems.feather,
            twilightArrival).registerStat();
    public static final Achievement twilightMagicMapFocus = new Achievement(
            TwilightForestMod.ID + 5,
            "twilightMagicMapFocus",
            2,
            0,
            TFItems.magicMapFocus,
            twilightHunter).registerStat();
    public static final Achievement twilightHill1 = new Achievement(
            TwilightForestMod.ID + 10,
            "twilightHill1",
            -2,
            -1,
            Blocks.iron_ore,
            twilightArrival).registerStat();
    public static final Achievement twilightHill2 = new Achievement(
            TwilightForestMod.ID + 11,
            "twilightHill2",
            -3,
            -2,
            Blocks.gold_ore,
            twilightArrival).registerStat();
    public static final Achievement twilightHill3 = new Achievement(
            TwilightForestMod.ID + 12,
            "twilightHill3",
            -1,
            -3,
            Blocks.diamond_ore,
            twilightArrival).registerStat();
    public static final Achievement twilightHedge = new Achievement(
            TwilightForestMod.ID + 13,
            "twilightHedge",
            2,
            -3,
            TFBlocks.hedge,
            twilightArrival).registerStat();
    public static final Achievement twilightMagicMap = new Achievement(
            TwilightForestMod.ID + 14,
            "twilightMagicMap",
            4,
            -1,
            TFItems.magicMap,
            twilightMagicMapFocus).registerStat();

    public static final Achievement twilightKillNaga = new Achievement(
            TwilightForestMod.ID + 6,
            "twilightKillNaga",
            4,
            3,
            new ItemStack(TFItems.trophy, 1, 1),
            twilightHunter).registerStat();
    public static final Achievement twilightProgressNaga = new Achievement(
            TwilightForestMod.ID + 17,
            "twilightProgressNaga",
            4,
            5,
            TFItems.nagaScale,
            twilightKillNaga).registerStat();
    public static final Achievement twilightKillLich = new Achievement(
            TwilightForestMod.ID + 8,
            "twilightKillLich",
            2,
            5,
            new ItemStack(TFItems.trophy, 1, 2),
            twilightProgressNaga).registerStat();
    public static final Achievement twilightProgressLich = new Achievement(
            TwilightForestMod.ID + 18,
            "twilightProgressLich",
            -1,
            4,
            TFItems.scepterLifeDrain,
            twilightKillLich).registerStat();
    public static final Achievement twilightProgressLabyrinth = new Achievement(
            TwilightForestMod.ID + 28,
            "twilightProgressLabyrinth",
            -4,
            6,
            TFItems.meefStroganoff,
            twilightProgressLich).registerStat();
    public static final Achievement twilightKillHydra = new Achievement(
            TwilightForestMod.ID + 30,
            "twilightKillHydra",
            -6,
            4,
            new ItemStack(TFItems.trophy, 1, 0),
            twilightProgressLabyrinth).registerStat();
    public static final Achievement twilightProgressHydra = new Achievement(
            TwilightForestMod.ID + 20,
            "twilightProgressHydra",
            -8,
            4,
            TFItems.fieryBlood,
            twilightKillHydra).registerStat();
    public static final Achievement twilightProgressTrophyPedestal = new Achievement(
            TwilightForestMod.ID + 29,
            "twilightProgressTrophyPedestal",
            -5,
            2,
            TFBlocks.trophyPedestal,
            twilightProgressHydra).registerStat();
    public static final Achievement twilightProgressKnights = new Achievement(
            TwilightForestMod.ID + 21,
            "twilightProgressKnights",
            -5,
            -1,
            TFItems.phantomHelm,
            twilightProgressTrophyPedestal).registerStat();
    public static final Achievement twilightProgressUrghast = new Achievement(
            TwilightForestMod.ID + 22,
            "twilightProgressUrghast",
            -7,
            -1,
            new ItemStack(TFItems.trophy, 1, 3),
            twilightProgressKnights).registerStat();
    public static final Achievement twilightProgressYeti = new Achievement(
            TwilightForestMod.ID + 23,
            "twilightProgressYeti",
            -7,
            -3,
            TFItems.alphaFur,
            twilightProgressUrghast).registerStat();
    public static final Achievement twilightProgressGlacier = new Achievement(
            TwilightForestMod.ID + 24,
            "twilightProgressGlacier",
            -5,
            -5,
            new ItemStack(TFItems.trophy, 1, 4),
            twilightProgressYeti).registerStat();
    public static final Achievement twilightProgressTroll = new Achievement(
            TwilightForestMod.ID + 25,
            "twilightProgressTroll",
            -5,
            -7,
            TFItems.lampOfCinders,
            twilightProgressGlacier).registerStat();
    public static final Achievement twilightProgressThorns = new Achievement(
            TwilightForestMod.ID + 26,
            "twilightProgressThorns",
            -3,
            -7,
            TFBlocks.thorns,
            twilightProgressTroll).registerStat();
    public static final Achievement twilightProgressCastle = new Achievement(
            TwilightForestMod.ID + 27,
            "twilightProgressCastle",
            -1,
            -7,
            Blocks.stonebrick,
            twilightProgressThorns).registerStat();

    public static final Achievement twilightNagaArmors = new Achievement(
            TwilightForestMod.ID + 7,
            "twilightNagaArmors",
            5,
            1,
            TFItems.plateNaga,
            twilightKillNaga).setSpecial().registerStat();
    public static final Achievement twilightLichScepters = new Achievement(
            TwilightForestMod.ID + 9,
            "twilightLichScepters",
            3,
            7,
            TFItems.scepterZombie,
            twilightKillLich).setSpecial().registerStat();

    public static final Achievement twilightMazeMap = new Achievement(
            TwilightForestMod.ID + 15,
            "twilightMazeMap",
            1,
            7,
            TFItems.mazeMap,
            twilightProgressLich).registerStat();
    public static final Achievement twilightOreMap = new Achievement(
            TwilightForestMod.ID + 16,
            "twilightOreMap",
            1,
            9,
            TFItems.oreMap,
            twilightMazeMap).setSpecial().registerStat();

    public static final Achievement twilightHydraChop = new Achievement(
            TwilightForestMod.ID + 31,
            "twilightHydraChop",
            -6,
            6,
            TFItems.hydraChop,
            twilightKillHydra).registerStat();
    public static final Achievement twilightMazebreaker = new Achievement(
            TwilightForestMod.ID + 32,
            "twilightMazebreaker",
            -3,
            4,
            TFItems.mazebreakerPick,
            twilightProgressLich).setSpecial().registerStat();
    public static final Achievement twilightFierySet = new Achievement(
            TwilightForestMod.ID + 33,
            "twilightFierySet",
            -8,
            7,
            TFItems.fierySword,
            twilightProgressHydra).setSpecial().registerStat();
    public static final Achievement twilightQuestRam = new Achievement(
            TwilightForestMod.ID + 34,
            "twilightQuestRam",
            1,
            -5,
            TFItems.crumbleHorn,
            twilightArrival).setSpecial().registerStat();

    public TFAchievementPage() {
        super(
                "Twilight Forest",
                twilightPortal,
                twilightArrival,
                twilightHunter,
                twilightMagicMapFocus,
                twilightKillNaga,
                twilightNagaArmors,
                twilightKillLich,
                twilightLichScepters,
                twilightHill1,
                twilightHill2,
                twilightHill3,
                twilightHedge,
                twilightMagicMap,
                twilightMazeMap,
                twilightOreMap,
                twilightProgressNaga,
                twilightProgressLich,
                twilightProgressLabyrinth,
                twilightHydraChop,
                twilightProgressKnights,
                twilightProgressUrghast,
                twilightProgressYeti,
                twilightProgressGlacier,
                twilightProgressTroll,
                twilightProgressThorns,
                twilightProgressCastle,
                twilightKillHydra,
                twilightProgressTrophyPedestal,
                twilightProgressHydra,
                twilightMazebreaker,
                twilightFierySet,
                twilightQuestRam);
    }

}
