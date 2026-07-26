package pageqwq.colorbmc.client.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiGraphicsExtractor;

public class ScreenUtils {
    public static void fillGradient(GuiGraphicsExtractor guiGraphics, int x1, int y1, int x2, int y2, int colorFrom, int colorTo) {
        // GuiGraphicsExtractor.fillGradient() is vertical (top→bottom).
        // For horizontal (left→right) gradient, draw thin vertical strips.
        int steps = Math.max(x2 - x1, 1);
        for (int i = 0; i < steps; i++) {
            float ratio = (float) i / steps;
            int color = lerpColor(colorFrom, colorTo, ratio);
            guiGraphics.fill(x1 + i, y1, x1 + i + 1, y2, color);
        }
    }

    private static int lerpColor(int colorA, int colorB, float ratio) {
        int a = (int) (((colorA >> 24) & 0xFF) * (1 - ratio) + ((colorB >> 24) & 0xFF) * ratio);
        int r = (int) (((colorA >> 16) & 0xFF) * (1 - ratio) + ((colorB >> 16) & 0xFF) * ratio);
        int g = (int) (((colorA >> 8) & 0xFF) * (1 - ratio) + ((colorB >> 8) & 0xFF) * ratio);
        int b = (int) ((colorA & 0xFF) * (1 - ratio) + (colorB & 0xFF) * ratio);
        return (a << 24) | (r << 16) | (g << 8) | b;
    }
}