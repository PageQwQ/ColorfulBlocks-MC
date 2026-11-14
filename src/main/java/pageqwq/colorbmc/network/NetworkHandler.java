package pageqwq.colorbmc.network;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import pageqwq.colorbmc.paint.PaintBucketSyncPayload;

public class NetworkHandler {
    public static void register() {
        PayloadTypeRegistry.playC2S().register(
            PaintBucketSyncPayload.TYPE,
            PaintBucketSyncPayload.STREAM_CODEC
        );

        ServerPlayNetworking.registerGlobalReceiver(PaintBucketSyncPayload.TYPE, PaintBucketSyncPayload::handle);
    }
}
