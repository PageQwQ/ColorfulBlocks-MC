package pageqwq.colorbmc;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import pageqwq.colorbmc.paint.ClientPlayHelperImpl;
import pageqwq.colorbmc.color.PaintBucketItemColor;
import pageqwq.colorbmc.color.RGBBlockColor;
import pageqwq.colorbmc.color.RGBBlockItemColor;
import pageqwq.colorbmc.machine.replicator.ConcreteReplicatorRenderer;
import pageqwq.colorbmc.machine.workshop.ConcreteWorkshopRenderer;
import pageqwq.colorbmc.dev.AutoTest;
import pageqwq.colorbmc.compat.continuity.ContinuityGlassCompat;
import pageqwq.colorbmc.paint.ClientProxy;
import pageqwq.colorbmc.registry.BlockEntityRegistry;
import pageqwq.colorbmc.registry.BlockRegistry;
import pageqwq.colorbmc.registry.EntityRegistry;
import pageqwq.colorbmc.registry.ItemRegistry;

public class RGBBlocksClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientProxy.clientPlayHelper = new ClientPlayHelperImpl();

        FabricLoader.getInstance().getModContainer(RGBBlocks.MOD_ID).ifPresent(container ->
            ResourceManagerHelper.registerBuiltinResourcePack(
                ResourceLocation.fromNamespaceAndPath(RGBBlocks.MOD_ID, "ctm"),
                container,
                Component.translatable("resourcepack.colorblockmc.ctm"),
                ResourcePackActivationType.NORMAL
            )
        );

        BlockRenderLayerMap.INSTANCE.putBlock(BlockRegistry.RGB_GLASS, RenderType.translucent());

        ColorProviderRegistry.BLOCK.register(
            new RGBBlockColor(),
            BlockRegistry.ALL_BLOCKS.toArray(new net.minecraft.world.level.block.Block[0])
        );

        ColorProviderRegistry.ITEM.register(
            new RGBBlockItemColor(),
            BlockRegistry.ALL_BLOCKS.toArray(new net.minecraft.world.level.block.Block[0])
        );

        ColorProviderRegistry.ITEM.register(
            new PaintBucketItemColor(),
            ItemRegistry.PAINT_BUCKET
        );

        BlockEntityRendererRegistry.register(
            BlockEntityRegistry.REPLICATOR,
            context -> new ConcreteReplicatorRenderer()
        );

        BlockEntityRendererRegistry.register(
            BlockEntityRegistry.WORKSHOP,
            context -> new ConcreteWorkshopRenderer()
        );

        EntityRendererRegistry.register(
            EntityRegistry.THROWN_PAINT_BUCKET,
            context -> new ThrownItemRenderer<>(context)
        );

        if (FabricLoader.getInstance().isModLoaded("continuity")) {
            ContinuityGlassCompat.register();
        }

        if (Boolean.getBoolean("colorblockmc.autotest")) {
            AutoTest.init();
        }
    }
}