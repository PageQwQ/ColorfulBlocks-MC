package pageqwq.colorbmc.command;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.level.block.Block;
import pageqwq.colorbmc.util.registries.BlockRegistry;
import pageqwq.colorbmc.util.registries.DataComponentRegistry;

public class GiveColorCommand {
    private static final DynamicCommandExceptionType INVALID_COLOR =
        new DynamicCommandExceptionType(value -> Component.translatable("commands.colorblockmc.givec.invalid_color", value));

    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
            dispatcher.register(Commands.literal("givec")
                .requires(source -> source.hasPermission(2))
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
        int color = 0xFF000000 | parseHex(hex);
        ServerPlayer player = source.getPlayerOrException();
        Block block = glowing ? BlockRegistry.RGB_GLOWING_CONCRETE : BlockRegistry.RGB_CONCRETE;
        Item item = block.asItem();

        int remaining = count;
        while (remaining > 0) {
            int stackSize = Math.min(item.getDefaultMaxStackSize(), remaining);
            remaining -= stackSize;
            ItemStack stack = new ItemStack(item, stackSize);
            stack.set(DataComponentRegistry.COLOR, color);
            stack.set(DataComponents.DYED_COLOR, new DyedItemColor(color, true));
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

    private static int parseHex(String hex) throws CommandSyntaxException {
        String s = hex.startsWith("#") ? hex.substring(1) : hex;
        if (s.length() != 6) {
            throw INVALID_COLOR.create(hex);
        }
        try {
            return Integer.parseInt(s, 16);
        } catch (NumberFormatException e) {
            throw INVALID_COLOR.create(hex);
        }
    }
}
