package pageqwq.colorbmc.color;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.DyedItemColor;
import pageqwq.colorbmc.registry.DataComponentRegistry;

/**
 * The one place that knows how a colored item stack is built on this branch.
 *
 * <p>It takes <b>two</b> components, and they are not interchangeable:
 * <ul>
 *   <li>{@code colorblockmc:color} — what the mod reads back (tooltips, the paint bucket, comparisons
 *       against a block entity's color).</li>
 *   <li>{@code minecraft:dyed_color} — what the item model's {@code minecraft:dye} tint actually
 *       samples (see {@code assets/colorblockmc/items/*.json}).</li>
 * </ul>
 * Setting only the first produces an item that renders untinted. That has now been missed three times
 * — by the color-picker preview, by mined drops, and by {@code /givec} — so every caller goes through
 * here instead of remembering both.
 *
 * <p>{@code color} must be <b>opaque ARGB</b> ({@code 0xFFRRGGBB}), which is what {@link Color} and
 * {@link Color#HSBtoRGB} produce and what a block entity stores. {@link #NO_COLOR} means "nothing picked
 * yet" and clears the dye component, so an uncolored stack is genuinely uncolored rather than tinted
 * black or left showing a stale color.
 */
public final class ColoredItems {

    /** "No color picked"; the default on the paint bucket's COLOR component. */
    public static final int NO_COLOR = -1;

    private ColoredItems() {
    }

    public static void applyColor(ItemStack stack, int color) {
        stack.set(DataComponentRegistry.COLOR, color);
        if (color == NO_COLOR) {
            stack.remove(DataComponents.DYED_COLOR);
        } else {
            stack.set(DataComponents.DYED_COLOR, new DyedItemColor(color));
        }
    }
}
