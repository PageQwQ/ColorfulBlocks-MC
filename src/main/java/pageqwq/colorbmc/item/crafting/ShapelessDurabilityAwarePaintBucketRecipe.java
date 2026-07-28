package pageqwq.colorbmc.item.crafting;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import pageqwq.colorbmc.item.PaintBucketItem;
import pageqwq.colorbmc.item.RGBBlockItem;
import pageqwq.colorbmc.util.registries.DataComponentRegistry;
import pageqwq.colorbmc.util.registries.RecipeSerializerRegistry;

import java.util.List;

public class ShapelessDurabilityAwarePaintBucketRecipe extends ShapelessRecipe {
    private final ItemStack result;
    private final List<Ingredient> ingredients;

    public static final MapCodec<ShapelessDurabilityAwarePaintBucketRecipe> CODEC = RecordCodecBuilder.mapCodec(
        instance -> instance.group(
            CraftingBookCategory.CODEC.fieldOf("category").orElse(CraftingBookCategory.MISC).forGetter(o -> o.category()),
            ItemStack.CODEC.fieldOf("result").forGetter(o -> o.result),
            Ingredient.CODEC.listOf(1, 9).fieldOf("ingredients").forGetter(o -> o.ingredients)
        ).apply(instance, ShapelessDurabilityAwarePaintBucketRecipe::new)
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, ShapelessDurabilityAwarePaintBucketRecipe> STREAM_CODEC =
        StreamCodec.composite(
            CraftingBookCategory.STREAM_CODEC, o -> o.category(),
            ItemStack.STREAM_CODEC, o -> o.result,
            Ingredient.CONTENTS_STREAM_CODEC.apply(ByteBufCodecs.list()), o -> o.ingredients,
            ShapelessDurabilityAwarePaintBucketRecipe::new
        );

    public ShapelessDurabilityAwarePaintBucketRecipe(CraftingBookCategory category, ItemStack result, List<Ingredient> ingredients) {
        super("", category, result, NonNullList.of(Ingredient.EMPTY, ingredients.toArray(new Ingredient[0])));
        this.result = result;
        this.ingredients = ingredients;
    }

    @SuppressWarnings("unchecked")
    @Override
    public RecipeSerializer<ShapelessRecipe> getSerializer() {
        return (RecipeSerializer<ShapelessRecipe>) (RecipeSerializer<?>) RecipeSerializerRegistry.SHAPELESS_DURABILITY_AWARE;
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(CraftingInput input) {
        NonNullList<ItemStack> nonnulllist = NonNullList.withSize(input.size(), ItemStack.EMPTY);
        ItemStack blockStack = ItemStack.EMPTY;

        for (int i = 0; i < nonnulllist.size(); i++) {
            ItemStack item = input.getItem(i);
            if (item.getItem() instanceof RGBBlockItem) {
                blockStack = item;
                break;
            }
        }

        for (int i = 0; i < nonnulllist.size(); i++) {
            ItemStack item = input.getItem(i);
            if (item.getItem() instanceof PaintBucketItem) {
                if (item.getOrDefault(DataComponentRegistry.COLOR, -1)
                    .equals(blockStack.getOrDefault(DataComponentRegistry.COLOR, -1))) {
                    nonnulllist.set(i, item.copy());
                } else if (item.getDamageValue() == item.getMaxDamage() - 1) {
                    nonnulllist.set(i, new ItemStack(Items.BUCKET));
                } else {
                    ItemStack remainder = item.copy();
                    remainder.setDamageValue(item.getDamageValue() + 1);
                    nonnulllist.set(i, remainder);
                }
            }
        }

        return nonnulllist;
    }

    @Override
    public ItemStack assemble(CraftingInput input, HolderLookup.Provider provider) {
        int color = 0;
        for (int i = 0; i < input.size(); i++) {
            if (input.getItem(i).getItem() instanceof PaintBucketItem) {
                color = input.getItem(i).getOrDefault(DataComponentRegistry.COLOR, -1);
                break;
            }
        }
        ItemStack result = super.assemble(input, provider);
        result.set(DataComponentRegistry.COLOR, color);
        result.set(net.minecraft.core.component.DataComponents.DYED_COLOR, new DyedItemColor(color, true));
        return result;
    }
}