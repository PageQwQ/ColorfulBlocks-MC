package pageqwq.colorbmc.util.registries;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import pageqwq.colorbmc.RGBBlocks;
import pageqwq.colorbmc.item.crafting.ShapelessDurabilityAwarePaintBucketRecipe;
import pageqwq.colorbmc.item.crafting.ShapelessNoReturnRecipe;

public class RecipeSerializerRegistry {
    public static final RecipeSerializer<ShapelessDurabilityAwarePaintBucketRecipe> SHAPELESS_DURABILITY_AWARE = Registry.register(
        BuiltInRegistries.RECIPE_SERIALIZER,
        ResourceLocation.fromNamespaceAndPath(RGBBlocks.MOD_ID, "crafting_shapeless_durability_aware"),
        new RecipeSerializer<ShapelessDurabilityAwarePaintBucketRecipe>() {
            @Override
            public MapCodec<ShapelessDurabilityAwarePaintBucketRecipe> codec() {
                return ShapelessDurabilityAwarePaintBucketRecipe.CODEC;
            }

            @Override
            public StreamCodec<RegistryFriendlyByteBuf, ShapelessDurabilityAwarePaintBucketRecipe> streamCodec() {
                return ShapelessDurabilityAwarePaintBucketRecipe.STREAM_CODEC;
            }
        }
    );

    public static final RecipeSerializer<ShapelessNoReturnRecipe> SHAPELESS_NO_RETURN = Registry.register(
        BuiltInRegistries.RECIPE_SERIALIZER,
        ResourceLocation.fromNamespaceAndPath(RGBBlocks.MOD_ID, "crafting_shapeless_no_return"),
        new RecipeSerializer<ShapelessNoReturnRecipe>() {
            @Override
            public MapCodec<ShapelessNoReturnRecipe> codec() {
                return ShapelessNoReturnRecipe.CODEC;
            }

            @Override
            public StreamCodec<RegistryFriendlyByteBuf, ShapelessNoReturnRecipe> streamCodec() {
                return ShapelessNoReturnRecipe.STREAM_CODEC;
            }
        }
    );

    public static void register() {}
}
