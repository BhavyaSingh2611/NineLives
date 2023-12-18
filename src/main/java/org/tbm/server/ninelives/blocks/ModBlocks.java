package org.tbm.server.ninelives.blocks;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ModBlocks {
    public static final Block AQUA_TEAM_BLOCK = registerBlock("aqua_team_block",
            new AquaTeamBlock(FabricBlockSettings.copyOf(Blocks.WHITE_WOOL)));
    public static final Block BLACK_TEAM_BLOCK = registerBlock("black_team_block",
            new BlackTeamBlock(FabricBlockSettings.copyOf(Blocks.WHITE_WOOL)));
    public static final Block BLUE_TEAM_BLOCK = registerBlock("blue_team_block",
            new BlueTeamBlock(FabricBlockSettings.copyOf(Blocks.WHITE_WOOL)));
    public static final Block DARK_AQUA_TEAM_BLOCK = registerBlock("dark_aqua_team_block",
            new DarkAquaTeamBlock(FabricBlockSettings.copyOf(Blocks.WHITE_WOOL)));
    public static final Block DARK_BLUE_TEAM_BLOCK = registerBlock("dark_blue_team_block",
            new DarkBlueTeamBlock(FabricBlockSettings.copyOf(Blocks.WHITE_WOOL)));
    public static final Block DARK_GRAY_TEAM_BLOCK = registerBlock("dark_gray_team_block",
            new DarkGrayTeamBlock(FabricBlockSettings.copyOf(Blocks.WHITE_WOOL)));
    public static final Block DARK_GREEN_TEAM_BLOCK = registerBlock("dark_green_team_block",
            new DarkGreenTeamBlock(FabricBlockSettings.copyOf(Blocks.WHITE_WOOL)));
    public static final Block DARK_PURPLE_TEAM_BLOCK = registerBlock("dark_purple_team_block",
            new DarkPurpleTeamBlock(FabricBlockSettings.copyOf(Blocks.WHITE_WOOL)));
    public static final Block DARK_RED_TEAM_BLOCK = registerBlock("dark_red_team_block",
            new DarkRedTeamBlock(FabricBlockSettings.copyOf(Blocks.WHITE_WOOL)));
    public static final Block GOLD_TEAM_BLOCK = registerBlock("gold_team_block",
            new GoldTeamBlock(FabricBlockSettings.copyOf(Blocks.WHITE_WOOL)));
    public static final Block GRAY_TEAM_BLOCK = registerBlock("gray_team_block",
            new GrayTeamBlock(FabricBlockSettings.copyOf(Blocks.WHITE_WOOL)));
    public static final Block GREEN_TEAM_BLOCK = registerBlock("green_team_block",
            new GreenTeamBlock(FabricBlockSettings.copyOf(Blocks.WHITE_WOOL)));
    public static final Block LIGHT_PURPLE_TEAM_BLOCK = registerBlock("light_purple_team_block",
            new LightPurpleTeamBlock(FabricBlockSettings.copyOf(Blocks.WHITE_WOOL)));
    public static final Block RED_TEAM_BLOCK = registerBlock("red_team_block",
            new RedTeamBlock(FabricBlockSettings.copyOf(Blocks.WHITE_WOOL)));
    public static final Block WHITE_TEAM_BLOCK = registerBlock("white_team_block",
            new WhiteTeamBlock(FabricBlockSettings.copyOf(Blocks.WHITE_WOOL)));
    public static final Block YELLOW_TEAM_BLOCK = registerBlock("yellow_team_block",
            new YellowTeamBlock(FabricBlockSettings.copyOf(Blocks.WHITE_WOOL)));

    public static final Block PLAYER_LEADERBOARD_BLOCK = registerBlock("player_leaderboard_block",
            new PlayerLeaderboardBlock(FabricBlockSettings.copyOf(Blocks.GOLD_BLOCK)));
    public static final Block TEAM_LEADERBOARD_BLOCK = registerBlock("team_leaderboard_block",
            new TeamLeaderboardBlock(FabricBlockSettings.copyOf(Blocks.GOLD_BLOCK)));
    public static final Block DEATH_LEADERBOARD_BLOCK = registerBlock("death_leaderboard_block",
            new DeathLeaderboardBlock(FabricBlockSettings.copyOf(Blocks.GOLD_BLOCK)));

    private static final ItemGroup ITEM_GROUP = FabricItemGroup.builder()
            .icon(() -> new ItemStack(AQUA_TEAM_BLOCK))
            .displayName(Text.translatable("ninelives.item_group"))
            .entries((context, entries) -> {
                entries.add(AQUA_TEAM_BLOCK.asItem());
                entries.add(BLACK_TEAM_BLOCK.asItem());
                entries.add(BLUE_TEAM_BLOCK.asItem());
                entries.add(DARK_AQUA_TEAM_BLOCK.asItem());
                entries.add(DARK_BLUE_TEAM_BLOCK.asItem());
                entries.add(DARK_GRAY_TEAM_BLOCK.asItem());
                entries.add(DARK_GREEN_TEAM_BLOCK.asItem());
                entries.add(DARK_PURPLE_TEAM_BLOCK.asItem());
                entries.add(DARK_RED_TEAM_BLOCK.asItem());
                entries.add(GOLD_TEAM_BLOCK.asItem());
                entries.add(GRAY_TEAM_BLOCK.asItem());
                entries.add(GREEN_TEAM_BLOCK.asItem());
                entries.add(LIGHT_PURPLE_TEAM_BLOCK.asItem());
                entries.add(RED_TEAM_BLOCK.asItem());
                entries.add(WHITE_TEAM_BLOCK.asItem());
                entries.add(YELLOW_TEAM_BLOCK.asItem());
                entries.add(PLAYER_LEADERBOARD_BLOCK.asItem());
                entries.add(TEAM_LEADERBOARD_BLOCK.asItem());
                entries.add(DEATH_LEADERBOARD_BLOCK.asItem());
            })
            .build();

    private static Block registerBlock(String name, Block block) {
        registerBlockItem(name, block);
        return Registry.register(Registries.BLOCK, new Identifier("ninelives", name), block);
    }
    private static Item registerBlockItem(String name, Block block) {
        return Registry.register(Registries.ITEM, new Identifier("ninelives", name),
                new BlockItem(block, new FabricItemSettings()));
    }
    public static void registerModBlocks() {
        Registry.register(Registries.ITEM_GROUP, new Identifier("ninelives", "group"), ITEM_GROUP);

    }
}
