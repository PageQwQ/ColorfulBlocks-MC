package pageqwq.colorbmc.client.gui.widget;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import pageqwq.colorbmc.util.Color;

public class HueStripWidget extends AbstractWidget {
    public interface Listener {
        void onChange(float hue);
    }

    private static final int[] POINTS = {0, 17, 34, 50, 66, 82, 100};

    private float hue;
    private final Listener listener;

    public HueStripWidget(int x, int y, int width, int height, Listener listener) {
        super(x, y, width, height, Component.empty());
        this.listener = listener;
    }

    public void setHue(float hue) {
        this.hue = hue;
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput output) {
    }

    @Override
    public void onClick(MouseButtonEvent event, boolean doubleClick) {
        updateFromMouse(event.y());
    }

    @Override
    protected void onDrag(MouseButtonEvent event, double dx, double dy) {
        updateFromMouse(event.y());
        super.onDrag(event, dx, dy);
    }

    private void updateFromMouse(double mouseY) {
        float h = Mth.clamp((float) ((mouseY - this.getY()) / this.height), 0.0F, 1.0F) * 360.0F;
        this.hue = h;
        this.listener.onChange(h);
    }

    @Override
    protected void extractWidgetRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        int x = this.getX();
        int y = this.getY();
        int w = this.width;
        int h = this.height;
        for (int i = 0; i < POINTS.length - 1; i++) {
            int y0 = y + Math.round(Mth.lerp(POINTS[i] / 100.0F, 0.0F, (float) h));
            int y1 = y + Math.round(Mth.lerp(POINTS[i + 1] / 100.0F, 0.0F, (float) h));
            graphics.fillGradient(x, y0, x + w, y1,
                Color.HSBtoRGB(POINTS[i] / 100.0F, 1.0F, 1.0F),
                Color.HSBtoRGB(POINTS[i + 1] / 100.0F, 1.0F, 1.0F));
        }
        int lineY = y + Math.round(this.hue / 360.0F * (h - 1));
        graphics.fill(x, lineY - 1, x + w, lineY + 2, 0xFF000000);
        graphics.fill(x, lineY, x + w, lineY + 1, 0xFFFFFFFF);
    }
}
