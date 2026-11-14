package pageqwq.colorbmc.command;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import pageqwq.colorbmc.color.ColoredItems;
import pageqwq.colorbmc.registry.BlockRegistry;
import pageqwq.colorbmc.registry.DataComponentRegistry;

public class GiveColorCommand {
    private static final DynamicCommandExceptionType INVALID_COLOR =
        new DynamicCommandExceptionType(value -> Component.translatable("commands.colorblockmc.givec.invalid_color", value));

    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
            dispatcher.register(Commands.literal("givec")
                .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                .then(Commands.argument("color", StringArgumentType.word())
                    .executes(context -> give(context.getSource(), StringArgumentType.getString(context, "color"), 1, false))
                    .then(Commands.argument("count", IntegerArgumentType.integer(1))
                        .executes(context -> give(context.getSource(), StringArgumentType.getString(context, "color"),
                            IntegerArgumentType.getInteger(context, "count"), false))
                        .then(Commands.argument("glowing", BoolArgumentType.bool())
                            .executes(context -> give(context.getSource(), StringArgumentType.getString(context, "color"),
                                IntegerArgumentType.getInteger(context, "count"), BoolArgumentType.getBool(context, "glowing"))))))));
    }

    private static int give(CommandSourceStack source, String hex, int count, boolean glowing) throws CommandSyntaxException {
        int color = parseHex(hex);
        ServerPlayer player = source.getPlayerOrException();
        Block block = glowing ? BlockRegistry.RGB_GLOWING_CONCRETE : BlockRegistry.RGB_CONCRETE;
        Item item = block.asItem();

        int remaining = count;
        while (remaining > 0) {
            int stackSize = Math.min(item.getDefaultMaxStackSize(), remaining);
            remaining -= stackSize;
            ItemStack stack = new ItemStack(item, stackSize);
            applyColor(stack, color);
            if (!player.getInventory().add(stack) || !stack.isEmpty()) {
                ItemEntity itemEntity = player.drop(stack, false);
                if (itemEntity != null) {
                    itemEntity.setNoPickUpDelay();
                    itemEntity.setTarget(player.getUUID());
                }
            }
        }

        String hexString = String.format("#%06X", color & 0xFFFFFF);
        source.sendSuccess(() -> Component.translatable("commands.colorblockmc.givec.success", count, block.getName(), hexString), true);
        return count;
    }

    /**
     * Marks a stack as carrying {@code color}, using the same two components a mined drop gets.
     *
     * <p>{@code color} must be opaque ARGB. A bare 24-bit value here would compare unequal to the
     * {@code 0xFFRRGGBB} a block entity stores, which is exactly the comparison
     * {@link pageqwq.colorbmc.paint.PaintBucketItem#useOn} and the durability-aware recipe rely on.
     */
    public static void applyColor(ItemStack stack, int color) {
        ColoredItems.applyColor(stack, color);
    }

    /** Parses {@code RRGGBB} (with or without the leading {@code #}) into opaque ARGB. */
    public static int parseHex(String hex) throws CommandSyntaxException {
        String s = hex.startsWith("#") ? hex.substring(1) : hex;
        if (s.length() != 6) {
            throw INVALID_COLOR.create(hex);
        }
        try {
            // Opaque ARGB, matching Color(int) / Color.getRGB() and everything the GUI produces.
            return Integer.parseInt(s, 16) | 0xFF000000;
        } catch (NumberFormatException e) {
            throw INVALID_COLOR.create(hex);
        }
    }
}
