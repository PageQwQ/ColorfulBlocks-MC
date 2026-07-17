package platinpython.rgbblocks.util.registries;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import platinpython.rgbblocks.RGBBlocks;
import platinpython.rgbblocks.item.PaintBucketItem;
import platinpython.rgbblocks.item.RGBBlockItem;

import java.util.ArrayList;
import java.util.List;

public class ItemRegistry {
    public static final List<Item> ALL_ITEMS = new ArrayList<>();

    public static final Item PAINT_BUCKET = register(
        "paint_bucket", new PaintBucketItem(new Item.Properties().durability(500)
            .component(DataComponentRegistry.COLOR, -1)
            .component(DataComponentRegistry.RGB_SELECTED, true))
    );

    static void registerBlockItem(String name, Block block) {
        Item item = Registry.register(
            BuiltInRegistries.ITEM,
            ResourceLocation.fromNamespaceAndPath(RGBBlocks.MOD_ID, name),
            new RGBBlockItem(block, new Item.Properties().component(DataComponentRegistry.COLOR, -1))
        );
        ALL_ITEMS.add(item);
    }

    private static Item register(String name, Item item) {
        Item registered = Registry.register(
            BuiltInRegistries.ITEM,
            ResourceLocation.fromNamespaceAndPath(RGBBlocks.MOD_ID, name),
            item
        );
        ALL_ITEMS.add(registered);
        return registered;
    }

    public static void register() {}
}
