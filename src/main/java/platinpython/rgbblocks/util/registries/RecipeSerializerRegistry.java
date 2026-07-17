package platinpython.rgbblocks.util.registries;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import platinpython.rgbblocks.RGBBlocks;
import platinpython.rgbblocks.item.crafting.ShapelessDurabilityAwarePaintBucketRecipe;
import platinpython.rgbblocks.item.crafting.ShapelessNoReturnRecipe;

public class RecipeSerializerRegistry {
    public static final RecipeSerializer<ShapelessDurabilityAwarePaintBucketRecipe> SHAPELESS_DURABILITY_AWARE = Registry.register(
        BuiltInRegistries.RECIPE_SERIALIZER,
        ResourceLocation.fromNamespaceAndPath(RGBBlocks.MOD_ID, "crafting_shapeless_durability_aware"),
        new ShapelessDurabilityAwarePaintBucketRecipe.Serializer()
    );

    public static final RecipeSerializer<ShapelessNoReturnRecipe> SHAPELESS_NO_RETURN = Registry.register(
        BuiltInRegistries.RECIPE_SERIALIZER,
        ResourceLocation.fromNamespaceAndPath(RGBBlocks.MOD_ID, "crafting_shapeless_no_return"),
        new ShapelessNoReturnRecipe.Serializer()
    );

    public static void register() {}
}
