package pageqwq.colorbmc.item.crafting;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.NormalCraftingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import pageqwq.colorbmc.util.registries.RecipeSerializerRegistry;

import java.util.List;

public class ShapelessNoReturnRecipe extends ShapelessRecipe {
    private final ItemStackTemplate result;
    private final List<Ingredient> ingredients;

    public static final MapCodec<ShapelessNoReturnRecipe> CODEC = RecordCodecBuilder.mapCodec(
        instance -> instance.group(
            Recipe.CommonInfo.MAP_CODEC.forGetter(o -> o.commonInfo),
            CraftingRecipe.CraftingBookInfo.MAP_CODEC.forGetter(o -> o.bookInfo),
            ItemStackTemplate.CODEC.fieldOf("result").forGetter(o -> o.result),
            Ingredient.CODEC.listOf(1, 9).fieldOf("ingredients").forGetter(o -> o.ingredients)
        ).apply(instance, ShapelessNoReturnRecipe::new)
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, ShapelessNoReturnRecipe> STREAM_CODEC =
        StreamCodec.composite(
            Recipe.CommonInfo.STREAM_CODEC, o -> o.commonInfo,
            CraftingRecipe.CraftingBookInfo.STREAM_CODEC, o -> o.bookInfo,
            ItemStackTemplate.STREAM_CODEC, o -> o.result,
            Ingredient.CONTENTS_STREAM_CODEC.apply(ByteBufCodecs.list()),
            o -> o.ingredients,
            ShapelessNoReturnRecipe::new
        );

    public ShapelessNoReturnRecipe(
        Recipe.CommonInfo commonInfo,
        CraftingRecipe.CraftingBookInfo bookInfo,
        ItemStackTemplate result,
        List<Ingredient> ingredients
    ) {
        super(commonInfo, bookInfo, result, ingredients);
        this.result = result;
        this.ingredients = ingredients;
    }

    @SuppressWarnings("unchecked")
    @Override
    public RecipeSerializer<ShapelessRecipe> getSerializer() {
        return (RecipeSerializer<ShapelessRecipe>) (RecipeSerializer<?>) RecipeSerializerRegistry.SHAPELESS_NO_RETURN;
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(CraftingInput input) {
        return NonNullList.withSize(input.size(), ItemStack.EMPTY);
    }
}