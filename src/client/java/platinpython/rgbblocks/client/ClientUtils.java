package platinpython.rgbblocks.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import platinpython.rgbblocks.client.gui.screen.ColorSelectScreen;

public class ClientUtils {
    public static void openColorSelectScreen(int color, boolean isRGBSelected) {
        Minecraft.getInstance().setScreen(new ColorSelectScreen(color, isRGBSelected));
    }

    public static boolean hasShiftDown() {
        return Screen.hasShiftDown();
    }
}
