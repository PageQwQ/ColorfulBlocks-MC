package pageqwq.colorbmc.util.network.packets;

import io.netty.buffer.ByteBuf;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.DyedItemColor;
import pageqwq.colorbmc.RGBBlocks;
import pageqwq.colorbmc.item.PaintBucketItem;
import pageqwq.colorbmc.util.registries.DataComponentRegistry;

public record PaintBucketSyncPayload(int color, boolean isRGBSelected) implements CustomPacketPayload {
    public static final Type<PaintBucketSyncPayload> TYPE =
        new Type<>(ResourceLocation.fromNamespaceAndPath(RGBBlocks.MOD_ID, "paint_bucket_sync"));

    public static final StreamCodec<ByteBuf, PaintBucketSyncPayload> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.INT, PaintBucketSyncPayload::color,
        ByteBufCodecs.BOOL, PaintBucketSyncPayload::isRGBSelected,
        PaintBucketSyncPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(PaintBucketSyncPayload payload, net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking.Context context) {
        context.player().getServer().execute(() -> {
            ItemStack stack = context.player().getMainHandItem();
            if (stack.getItem() instanceof PaintBucketItem) {
                stack.set(DataComponentRegistry.COLOR, payload.color());
                stack.set(DataComponents.DYED_COLOR, new DyedItemColor(payload.color(), false));
                stack.set(DataComponentRegistry.RGB_SELECTED, payload.isRGBSelected());
            }
        });
    }
}
