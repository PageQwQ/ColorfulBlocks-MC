package pageqwq.colorbmc;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.fabric.api.client.rendering.v1.BlockColorRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import pageqwq.colorbmc.paint.ClientPlayHelperImpl;
import pageqwq.colorbmc.client.colorhandlers.RGBBlockColor;
import pageqwq.colorbmc.autotest.AutoTest;
import pageqwq.colorbmc.client.render.ConcreteReplicatorRenderer;
import pageqwq.colorbmc.client.render.ConcreteWorkshopRenderer;
import pageqwq.colorbmc.paint.ClientProxy;
import pageqwq.colorbmc.registry.BlockEntityRegistry;
import pageqwq.colorbmc.registry.BlockRegistry;
import pageqwq.colorbmc.registry.EntityRegistry;

import java.util.List;

public class RGBBlocksClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientProxy.clientPlayHelper = new ClientPlayHelperImpl();

        FabricLoader.getInstance().getModContainer(RGBBlocks.MOD_ID).ifPresent(container ->
            ResourceManagerHelper.registerBuiltinResourcePack(
                Identifier.fromNamespaceAndPath(RGBBlocks.MOD_ID, "ctm"),
                container,
                Component.translatable("resourcepack.colorblockmc.ctm"),
                ResourcePackActivationType.NORMAL
            )
        );

        BlockColorRegistry.register(
            List.of(new RGBBlockColor()),
            BlockRegistry.ALL_BLOCKS.toArray(new net.minecraft.world.level.block.Block[0])
        );

        BlockEntityRendererRegistry.register(
            BlockEntityRegistry.WORKSHOP,
            context -> new ConcreteWorkshopRenderer()
        );

        BlockEntityRendererRegistry.register(
            BlockEntityRegistry.REPLICATOR,
            context -> new ConcreteReplicatorRenderer()
        );

        EntityRendererRegistry.register(
            EntityRegistry.THROWN_PAINT_BUCKET,
            context -> new ThrownItemRenderer<>(context)
        );

        if (Boolean.getBoolean("colorblockmc.autotest")) {
            AutoTest.init();
        }
    }
}