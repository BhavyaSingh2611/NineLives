package org.tbm.server.ninelives.commands;

import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.command.CommandRegistryAccess;
import org.tbm.server.ninelives.HologramManager;
import org.tbm.server.ninelives.mixinInterfaces.IDisplayEntityMixin;
import org.tbm.server.ninelives.mixinInterfaces.ITextDisplayEntityMixin;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import java.util.Arrays;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

import net.minecraft.util.Formatting;
import net.minecraft.entity.EntityType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.command.CommandManager;
import net.minecraft.command.argument.ColorArgumentType;
import net.minecraft.command.argument.TextArgumentType;
import net.minecraft.command.argument.PosArgument;
import net.minecraft.command.argument.Vec3ArgumentType;
import net.minecraft.text.Text;
import net.minecraft.entity.decoration.DisplayEntity;
import net.minecraft.entity.decoration.DisplayEntity.BillboardMode;
import org.joml.Vector3f;

public class HologramCommand {
    private static final SuggestionProvider<ServerCommandSource> BILLBOARD_SUGGESTION_PROVIDER = (context, builder) -> {
        Stream<String> billboardValues = Arrays.stream(BillboardMode.values()).map(DisplayEntity.BillboardMode::asString);
        Objects.requireNonNull(builder);
        billboardValues.forEach(builder::suggest);
        return builder.buildFuture();
    };

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment) {
        LiteralCommandNode<ServerCommandSource> hologramNode = CommandManager
                .literal("hologram")
                .requires((source) -> source.hasPermissionLevel(2))
                .build();

        LiteralCommandNode<ServerCommandSource> createNode = CommandManager
                .literal("create")
                .then(CommandManager.argument("name", StringArgumentType.word())
                        .then(CommandManager.argument("text", TextArgumentType.text())
                                .executes(HologramCommand::create)))
                .build();

        LiteralCommandNode<ServerCommandSource> editNode = CommandManager
                .literal("edit")
                .then(CommandManager.argument("hologram", HologramCommand.HologramArgumentType.hologram())
                        .then(CommandManager.argument("text", TextArgumentType.text())
                                .executes(HologramCommand::edit)))
                .build();

        LiteralCommandNode<ServerCommandSource> positionNode = CommandManager
                .literal("position")
                .then(CommandManager.argument("hologram", HologramCommand.HologramArgumentType.hologram())
                        .then(CommandManager.argument("position", Vec3ArgumentType.vec3())
                                .executes(HologramCommand::position)))
                .build();

        LiteralCommandNode<ServerCommandSource> backgroundNode = CommandManager
                .literal("background")
                .then(CommandManager.argument("hologram", HologramCommand.HologramArgumentType.hologram())
                        .then(CommandManager.argument("background", ColorArgumentType.color())
                                .executes(HologramCommand::background)))
                .build();

        LiteralCommandNode<ServerCommandSource> billboardNode = CommandManager
                .literal("billboard")
                .then(CommandManager.argument("hologram", HologramCommand.HologramArgumentType.hologram())
                        .then(CommandManager.argument("billboard", StringArgumentType.word()).suggests(BILLBOARD_SUGGESTION_PROVIDER)
                                .executes(HologramCommand::billboard)))
                .build();

        LiteralCommandNode<ServerCommandSource> scaleNode = CommandManager
                .literal("scale")
                .then(CommandManager.argument("hologram", HologramCommand.HologramArgumentType.hologram())
                        .then(CommandManager.argument("scale", FloatArgumentType.floatArg())
                                .executes(HologramCommand::scale)))
                .build();

        LiteralCommandNode<ServerCommandSource> removeNode = CommandManager
                .literal("remove")
                .then(CommandManager.argument("hologram", HologramCommand.HologramArgumentType.hologram())
                        .executes(HologramCommand::remove))
                .build();

        dispatcher.getRoot().addChild(hologramNode);

        hologramNode.addChild(createNode);
        hologramNode.addChild(editNode);
        hologramNode.addChild(positionNode);
        hologramNode.addChild(backgroundNode);
        hologramNode.addChild(billboardNode);
        hologramNode.addChild(scaleNode);
        hologramNode.addChild(removeNode);
    }

    private static int create(CommandContext<ServerCommandSource> context) {
        Text text = TextArgumentType.getTextArgument(context, "text");;
        System.out.println(text);
        return executeCreate(context.getSource(), context.getArgument("name", String.class), Objects.requireNonNullElseGet(text, () -> Text.literal("New hologram")));
    }

    private static int edit(CommandContext<ServerCommandSource> context) {
        return executeEditText(context.getSource(), context.getArgument("hologram", DisplayEntity.TextDisplayEntity.class), context.getArgument("text", Text.class));
    }

    private static int position(CommandContext<ServerCommandSource> context) {
        return executeEditPos(context.getSource(), context.getArgument("hologram", DisplayEntity.TextDisplayEntity.class), context.getArgument("position", PosArgument.class));
    }

    private static int background(CommandContext<ServerCommandSource> context) {
        return executeEditBackground(context.getSource(), context.getArgument("hologram", DisplayEntity.TextDisplayEntity.class), context.getArgument("background", Formatting.class));
    }

    private static int billboard(CommandContext<ServerCommandSource> context) {
        return executeEditBillboard(context.getSource(), context.getArgument("hologram", DisplayEntity.TextDisplayEntity.class), context.getArgument("billboard", String.class));
    }

    private static int scale(CommandContext<ServerCommandSource> context) {
        return executeEditScale(context.getSource(), context.getArgument("hologram", DisplayEntity.TextDisplayEntity.class), context.getArgument("scale", Float.class));
    }

    private static int remove(CommandContext<ServerCommandSource> context) {
        return executeRemove(context.getSource(), context.getArgument("hologram", DisplayEntity.TextDisplayEntity.class));
    }

    private static int executeCreate(ServerCommandSource source, String name, Text text) {
        if (HologramManager.getHologram(name) != null) {
            source.sendError(Text.literal("A hologram with this name already exists: '" + name + "'"));
            return 0;
        } else {
            DisplayEntity.TextDisplayEntity entity = new DisplayEntity.TextDisplayEntity(EntityType.TEXT_DISPLAY, source.getWorld());
            ITextDisplayEntityMixin textDisplayEntityMixin = (ITextDisplayEntityMixin) entity;
            IDisplayEntityMixin displayEntityMixin = (IDisplayEntityMixin) entity;
            entity.setPosition(source.getPosition());
            textDisplayEntityMixin.setHologramTextPlaceholder(text.getString());
            entity.getDataTracker().set(textDisplayEntityMixin.getBackgroundData(), 0);
            entity.getDataTracker().set(displayEntityMixin.getBillboardData(), (byte) 3);
            textDisplayEntityMixin.setIsHologram(true);
            textDisplayEntityMixin.setHologramName(name);
            source.getWorld().spawnEntity(entity);
            source.getPlayer().sendMessage(Text.literal("Created new hologram").formatted(Formatting.GREEN));
            return 1;
        }
    }

    private static int executeEditText(ServerCommandSource source, DisplayEntity.TextDisplayEntity hologram, Text text) {
        ITextDisplayEntityMixin textDisplayEntityMixin = (ITextDisplayEntityMixin) hologram;
        textDisplayEntityMixin.setHologramTextPlaceholder(text.getString());
        source.getPlayer().sendMessage(Text.literal("Edited hologram").formatted(Formatting.GREEN));
        return 1;
    }

    private static int executeEditPos(ServerCommandSource source, DisplayEntity.TextDisplayEntity hologram, PosArgument pos) {
        hologram.setPosition(pos.toAbsolutePos(source));
        hologram.setYaw(source.getPlayer().getYaw());
        source.getPlayer().sendMessage(Text.literal("Edited hologram").formatted(Formatting.GREEN));
        return 1;
    }

    private static int executeEditBackground(ServerCommandSource source, DisplayEntity.TextDisplayEntity hologram, Formatting color) {
        ITextDisplayEntityMixin textDisplayEntityMixin = (ITextDisplayEntityMixin) hologram;
        hologram.getDataTracker().set(textDisplayEntityMixin.getBackgroundData(), color.getColorValue() | -939524096);
        source.getPlayer().sendMessage(Text.literal("Edited hologram").formatted(Formatting.GREEN));
        return 1;
    }

    private static int executeEditBillboard(ServerCommandSource source, DisplayEntity.TextDisplayEntity hologram, String billboardName) {
        IDisplayEntityMixin displayEntityMixin = (IDisplayEntityMixin) hologram;
        byte index = 1;
        switch (billboardName.toLowerCase()) {
            case "fixed":
                index = 0;
                break;
            case "vertical":
                index = 1;
                break;
            case "horizontal":
                index = 2;
                break;
            case "center":
                index = 3;
                break;
            default:
                source.sendError(Text.literal("Unknown billboard: '" + billboardName + "'").formatted(Formatting.RED));
                return 0;
        }

        hologram.getDataTracker().set(displayEntityMixin.getBillboardData(), index);
        source.getPlayer().sendMessage(Text.literal("Edited hologram").formatted(Formatting.GREEN));
        return 1;
    }

    private static int executeEditScale(ServerCommandSource source, DisplayEntity.TextDisplayEntity hologram, Float scale) {
        IDisplayEntityMixin displayEntityMixin = (IDisplayEntityMixin) hologram;
        hologram.getDataTracker().set(displayEntityMixin.getScaleData(), new Vector3f(scale, scale, scale));
        source.getPlayer().sendMessage(Text.literal("Edited hologram").formatted(Formatting.GREEN));
        return 1;
    }

    private static int executeRemove(ServerCommandSource source, DisplayEntity.TextDisplayEntity hologram) {
        ITextDisplayEntityMixin displayEntityMixin = (ITextDisplayEntityMixin) hologram;
        displayEntityMixin.setIsHologram(false);
        hologram.kill();
        HologramManager.removeHologram(displayEntityMixin.getHologramName());
        source.getPlayer().sendMessage(Text.literal("Removed hologram").formatted(Formatting.GREEN));
        return 1;
    }

    public static class HologramArgumentType implements ArgumentType<DisplayEntity.TextDisplayEntity> {

        public static HologramArgumentType hologram() {
            return new HologramArgumentType();
        }

        public DisplayEntity.TextDisplayEntity parse(StringReader reader) throws CommandSyntaxException {
            String name = reader.readUnquotedString();
            DisplayEntity.TextDisplayEntity entity = (DisplayEntity.TextDisplayEntity) HologramManager.getHologram(name);
            if (entity == null) {
                throw (new DynamicCommandExceptionType((nameArg) -> Text.literal("Unknown hologram: '" + nameArg + "'").formatted(Formatting.RED))).create(name);
            } else {
                return entity;
            }
        }

        public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
            Set<String> nameSet = HologramManager.getAllNames();
            Objects.requireNonNull(builder);
            nameSet.forEach(builder::suggest);
            return builder.buildFuture();
        }
    }


}

