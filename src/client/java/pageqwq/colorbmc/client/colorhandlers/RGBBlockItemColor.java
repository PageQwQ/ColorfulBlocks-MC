package pageqwq.colorbmc.client.colorhandlers;

import com.mojang.serialization.MapCodec;
import net.minecraft.client.color.item.ItemTintSource;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import pageqwq.colorbmc.item.RGBBlockItem;
import pageqwq.colorbmc.util.registries.DataComponentRegistry;
import org.jspecify.annotations.Nullable;

public class RGBBlockItemColor implements ItemTintSource {
    @Override
    public int calculate(ItemStack stack, @Nullable ClientLevel level, @Nullable LivingEntity owner) {
        if (stack.getItem() instanceof RGBBlockItem) {
            return stack.getOrDefault(DataComponentRegistry.COLOR, -1);
        }
        return -1;
    }

    @Override
    public MapCodec<? extends ItemTintSource> type() {
        return null; // Not used for programmatic registration
    }
}
