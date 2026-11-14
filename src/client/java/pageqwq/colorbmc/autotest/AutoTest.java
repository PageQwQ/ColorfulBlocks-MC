package pageqwq.colorbmc.autotest;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Screenshot;
import net.minecraft.core.BlockPos;
import pageqwq.colorbmc.rgb.RGBBlockEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/** Runs the CTM glass check when the autotest run config is used. */
public final class AutoTest {
    private static final Logger LOGGER = LoggerFactory.getLogger("ColorfulBlocks-AutoTest");
    private static int ticks;

    private AutoTest() {
    }

    public static void init() {
        ClientTickEvents.END_CLIENT_TICK.register(AutoTest::tick);
    }

    private static void tick(Minecraft client) {
        if (client.level == null || client.player == null) {
            return;
        }
        ticks++;
        if (ticks == 40) {
            client.options.pauseOnLostFocus = false;
            client.options.hideGui = true;
            client.pauseGame(false);
            client.setScreen(null);
            client.player.connection.sendCommand("tp @s 179.5 66 100 180 10");
        } else if (ticks == 100) {
            grab(client, "ctm_wall.png");
        } else if (ticks == 120) {
            client.player.connection.sendCommand("tp @s 179.5 66 97 180 10");
        } else if (ticks == 180) {
            grab(client, "ctm_wall_close.png");
        } else if (ticks == 200) {
            client.stop();
        }
    }

    private static void grab(Minecraft client, String name) {
        client.pauseGame(false);
        client.setScreen(null);
        client.options.hideGui = true;
        LOGGER.info("player pos={} {} {} yaw={} pitch={}", client.player.getX(), client.player.getY(), client.player.getZ(), client.player.getYRot(), client.player.getXRot());
        LOGGER.info("wall block={} color={}", client.level.getBlockState(new BlockPos(175, 65, 91)),
            client.level.getBlockEntity(new BlockPos(175, 65, 91)) instanceof RGBBlockEntity be ? be.getColor() : "none");
        for (int x = 174; x <= 187; x++) {
            LOGGER.info("row x={} state={}", x, client.level.getBlockState(new BlockPos(x, 65, 91)));
        }
        Screenshot.grab(client.gameDirectory, name, client.getMainRenderTarget(), message -> {
        });
        LOGGER.info("Saved CTM screenshot {}", name);
    }
}
