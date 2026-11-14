package pageqwq.colorbmc.paint;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import pageqwq.colorbmc.RGBBlocks;
import pageqwq.colorbmc.registry.DataComponentRegistry;

/**
 * Client -> server: "the paint bucket I am holding should now hold this color".
 *
 * <p>Sent when the color picker closes. Colors travel as <b>opaque ARGB</b>: the sender is
 * {@code Color.HSBtoRGB}, which returns {@code 0xFF000000 | rgb}, and that is the same convention
 * {@code RGBBlockEntity} stores ({@code new Color(c).getRGB()}). Do not strip the alpha byte here —
 * {@code PaintBucketItem} and the durability-aware recipe compare the raw component value against the
 * block entity's color, so a 0x00RRGGBB value would compare unequal to the 0xFFRRGGBB it was painted
 * from, and re-painting a block the same color would start draining durability.
 */
public record PaintBucketSyncPayload(int color, boolean isRGBSelected) implements CustomPacketPayload {

    /**
     * Sentinel for "no color picked"; the default on the paint bucket's COLOR component. Normalizing to
     * opaque ARGB leaves it untouched ({@code 0xFF000000 | (-1 & 0xFFFFFF) == -1}), so it needs no
     * special case in {@link #handle}.
     */
    public static final int NO_COLOR = -1;

    /** Colors are stored as opaque ARGB, matching {@code Color.getRGB()}. */
    private static final int OPAQUE = 0xFF000000;
    private static final int RGB_MASK = 0xFFFFFF;

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
        // The server is authoritative for what lands in an item's data components: the client chooses a
        // color, but a modified client must not be able to write an arbitrary int there. Normalizing to
        // opaque ARGB bounds the value to 24 bits plus a fixed alpha, and is a no-op for every legitimate
        // sender (they only ever produce 0xFFRRGGBB, or -1 for "not picked").
        int color = OPAQUE | (payload.color() & RGB_MASK);

        var server = context.player().level().getServer();
        if (server == null) return;
        server.execute(() -> {
            ItemStack stack = context.player().getMainHandItem();
            if (stack.getItem() instanceof PaintBucketItem) {
                stack.set(DataComponentRegistry.COLOR, color);
                stack.set(DataComponentRegistry.RGB_SELECTED, payload.isRGBSelected());
            }
        });
    }
}
