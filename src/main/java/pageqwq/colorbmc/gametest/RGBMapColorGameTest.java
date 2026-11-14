package pageqwq.colorbmc.gametest;

import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import pageqwq.colorbmc.rgb.RGBBlockEntity;
import pageqwq.colorbmc.color.MapColorResolver;
import pageqwq.colorbmc.registry.BlockRegistry;

/** Verifies that the map-color hook sees the block entity's current color. */
public class RGBMapColorGameTest implements FabricGameTest {
    @GameTest(template = FabricGameTest.EMPTY_STRUCTURE)
    public void paintedBlockUsesItsMapColor(GameTestHelper helper) {
        BlockPos pos = helper.absolutePos(new BlockPos(3, 3, 3));
        helper.getLevel().setBlock(pos, BlockRegistry.RGB_CONCRETE.defaultBlockState(), Block.UPDATE_ALL);
        RGBBlockEntity blockEntity = (RGBBlockEntity) helper.getLevel().getBlockEntity(pos);
        helper.assertTrue(blockEntity != null, "painted block has no RGB block entity");

        blockEntity.setColor(0x3366CC);
        MapColor blue = mapColorAt(helper, pos);
        helper.assertTrue(blue == MapColorResolver.forColor(0x3366CC),
            "map color should match the block color, got " + blue);
        helper.assertTrue(blue != MapColor.NONE, "a painted block must not be invisible on the map");

        blockEntity.setColor(0xCC3333);
        MapColor red = mapColorAt(helper, pos);
        helper.assertTrue(red == MapColorResolver.forColor(0xCC3333),
            "repainting should change the map color, got " + red);
        helper.assertTrue(red != blue, "red and blue should not resolve to the same map color");

        helper.succeed();
    }

    @GameTest(template = FabricGameTest.EMPTY_STRUCTURE)
    public void unpaintedBlockKeepsVanillaMapColor(GameTestHelper helper) {
        BlockPos pos = helper.absolutePos(new BlockPos(3, 3, 3));
        helper.getLevel().setBlock(pos, BlockRegistry.RGB_CONCRETE.defaultBlockState(), Block.UPDATE_ALL);

        MapColor actual = mapColorAt(helper, pos);
        helper.assertTrue(actual == BlockRegistry.RGB_CONCRETE.defaultMapColor(),
            "an unpainted block should keep its vanilla map color, got " + actual);

        helper.succeed();
    }

    @GameTest(template = FabricGameTest.EMPTY_STRUCTURE)
    public void mapUpdateStoresThePaletteColor(GameTestHelper helper) {
        int color = 0x3366CC;
        BlockPos origin = helper.absolutePos(new BlockPos(16, 3, 16));

        // createFresh snaps the map center to a 128-block grid.
        MapItemSavedData data = MapItemSavedData.createFresh(
            origin.getX(), origin.getZ(), (byte) 0, true, false, helper.getLevel().dimension());
        BlockPos center = new BlockPos(data.centerX, origin.getY() + 200, data.centerZ);
        for (int dx = -8; dx < 8; dx++) {
            for (int dz = -8; dz < 8; dz++) {
                BlockPos pos = center.offset(dx, 0, dz);
                helper.getLevel().setBlock(pos, BlockRegistry.RGB_CONCRETE.defaultBlockState(), Block.UPDATE_ALL);
                ((RGBBlockEntity) helper.getLevel().getBlockEntity(pos)).setColor(color);
            }
        }
        helper.assertTrue(mapColorAt(helper, center) == MapColorResolver.forColor(color),
            "platform block should expose the expected map color before the map update");

        ServerPlayer player = helper.makeMockServerPlayerInLevel();
        player.setPos(center.getX() + 0.5, center.getY() + 1.5, center.getZ() + 0.5);
        ((MapItem) Items.FILLED_MAP).update(helper.getLevel(), player, data);

        byte expected = MapColorResolver.forColor(color).getPackedId(MapColor.Brightness.LOWEST);
        // Low two bits are brightness; the palette id is what this fix controls.
        int expectedId = (expected & 0xFF) >> 2;
        int matches = 0;
        int nonZero = 0;
        for (byte pixel : data.colors) {
            if (pixel != 0) {
                nonZero++;
            }
            if (((pixel & 0xFF) >> 2) == expectedId) {
                matches++;
            }
        }
        helper.assertTrue(matches > 50,
            "map update should write the painted block's palette color, expected id=" + expectedId
                + " nonZero=" + nonZero + " centerPixel=" + (data.colors[64 + 64 * 128] & 0xFF)
                + " player=" + player.getX() + "," + player.getY() + "," + player.getZ()
                + " center=" + center);

        helper.succeed();
    }

    private static MapColor mapColorAt(GameTestHelper helper, BlockPos pos) {
        return helper.getLevel().getBlockState(pos).getMapColor(helper.getLevel(), pos);
    }
}
