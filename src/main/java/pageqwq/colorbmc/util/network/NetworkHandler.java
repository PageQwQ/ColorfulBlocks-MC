package pageqwq.colorbmc.util.network;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import pageqwq.colorbmc.util.network.packets.PaintBucketSyncPayload;

public class NetworkHandler {
    public static void register() {
        PayloadTypeRegistry.playC2S().register(
            PaintBucketSyncPayload.TYPE,
            PaintBucketSyncPayload.STREAM_CODEC
        );

        ServerPlayNetworking.registerGlobalReceiver(PaintBucketSyncPayload.TYPE, PaintBucketSyncPayload::handle);
    }
}
