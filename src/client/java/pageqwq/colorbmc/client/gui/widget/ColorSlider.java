package pageqwq.colorbmc.client.gui.widget;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.network.chat.Component;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import pageqwq.colorbmc.client.gui.ScreenUtils;
import pageqwq.colorbmc.client.gui.screen.ColorSelectScreen;
import pageqwq.colorbmc.util.Color;

import java.util.Locale;
import java.util.function.Function;

public class ColorSlider extends AbstractSliderButton {
    private final SliderType type;

    private final double minValue;
    private final double maxValue;

    private final Component displayText;

    private static final Identifier SLIDER_HANDLE_SPRITE = Identifier.withDefaultNamespace("widget/slider_handle");
    private static final Identifier SLIDER_HANDLE_HIGHLIGHTED_SPRITE = Identifier.withDefaultNamespace("widget/slider_handle_highlighted");

    public ColorSlider(
        int x,
        int y,
        int width,
        int height,
        Component displayText,
        double minValue,
        double maxValue,
        double currentValue,
        SliderType type
    ) {
        super(x, y, width, height, displayText, (currentValue - minValue) / (maxValue - minValue));
        this.type = type;
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.value = (currentValue - this.minValue) / (this.maxValue - this.minValue);
        this.displayText = displayText;
        setMessage(
            Component.empty()
                .append(displayText)
                .append(Integer.toString((int) Math.round(this.value * (maxValue - minValue) + minValue)))
        );
    }

    @Override
    protected void updateMessage() {
        setMessage(Component.empty().append(this.displayText).append(Integer.toString(getValueInt())));
    }

    @Override
    protected void applyValue() {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.screen instanceof ColorSelectScreen screen && screen.hexBox != null && screen.redSlider != null
            && screen.greenSlider != null && screen.blueSlider != null) {
            screen.hexBox.setValue(
                "#" + Integer
                    .toHexString(
                        new Color(
                            screen.redSlider.getValueInt(), screen.greenSlider.getValueInt(),
                            screen.blueSlider.getValueInt()
                        ).getRGB()
                    )
                    .substring(2)
                    .toUpperCase(Locale.ENGLISH)
            );
        }
    }

    public int getValueInt() {
        return (int) Math.round(this.value * (maxValue - minValue) + minValue);
    }

    public double getValue() {
        return this.value * (maxValue - minValue) + minValue;
    }

    public void setValueInt(int value) {
        this.value = (value - this.minValue) / (this.maxValue - this.minValue);
        this.updateMessage();
    }

    @Override
    public void extractWidgetRenderState(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float a) {
        if (this.visible) {
            // Draw black background
            guiGraphics.fill(this.getX(), this.getY(), this.getX() + this.width, this.getY() + this.height, 0xFF000000);

            // Draw gradient background
            Minecraft minecraft = Minecraft.getInstance();
            if (minecraft.screen instanceof ColorSelectScreen screen) {
                switch (type) {
                    case RED: renderRedBackground(guiGraphics, screen); break;
                    case GREEN: renderGreenBackground(guiGraphics, screen); break;
                    case BLUE: renderBlueBackground(guiGraphics, screen); break;
                    case HUE: renderHueBackground(guiGraphics, screen); break;
                    case SATURATION: renderSaturationBackground(guiGraphics, screen); break;
                    case BRIGHTNESS: renderBrightnessBackground(guiGraphics, screen); break;
                }
            }

            // Draw handle sprite (skip parent's background sprite)
            Identifier handleSprite = this.isHovered() && this.active
                ? SLIDER_HANDLE_HIGHLIGHTED_SPRITE : SLIDER_HANDLE_SPRITE;
            guiGraphics.blitSprite(
                RenderPipelines.GUI, handleSprite,
                this.getX() + (int)(this.value * (this.width - 8)),
                this.getY(), 8, this.getHeight()
            );

            // Draw centered text label
            int textX = this.getX() + this.width / 2 - minecraft.font.width(this.getMessage()) / 2;
            guiGraphics.text(
                minecraft.font, this.getMessage(), textX, this.getY() + 2,
                0xFFFFFF, true
            );
        }
    }

    private void renderRedBackground(GuiGraphicsExtractor guiGraphics, ColorSelectScreen screen) {
        if (screen.greenSlider == null || screen.blueSlider == null) return;
        int leftColor = new Color(0x00, screen.greenSlider.getValueInt(), screen.blueSlider.getValueInt()).getRGB();
        int rightColor = new Color(0xFF, screen.greenSlider.getValueInt(), screen.blueSlider.getValueInt()).getRGB();
        ScreenUtils.fillGradient(guiGraphics, this.getX() + 1, this.getY() + 1, this.getX() + this.width - 1, this.getY() + this.height - 1, leftColor, rightColor);
    }

    private void renderGreenBackground(GuiGraphicsExtractor guiGraphics, ColorSelectScreen screen) {
        if (screen.redSlider == null || screen.blueSlider == null) return;
        int leftColor = new Color(screen.redSlider.getValueInt(), 0x00, screen.blueSlider.getValueInt()).getRGB();
        int rightColor = new Color(screen.redSlider.getValueInt(), 0xFF, screen.blueSlider.getValueInt()).getRGB();
        ScreenUtils.fillGradient(guiGraphics, this.getX() + 1, this.getY() + 1, this.getX() + this.width - 1, this.getY() + this.height - 1, leftColor, rightColor);
    }

    private void renderBlueBackground(GuiGraphicsExtractor guiGraphics, ColorSelectScreen screen) {
        if (screen.redSlider == null || screen.greenSlider == null) return;
        int leftColor = new Color(screen.redSlider.getValueInt(), screen.greenSlider.getValueInt(), 0x00).getRGB();
        int rightColor = new Color(screen.redSlider.getValueInt(), screen.greenSlider.getValueInt(), 0xFF).getRGB();
        ScreenUtils.fillGradient(guiGraphics, this.getX() + 1, this.getY() + 1, this.getX() + this.width - 1, this.getY() + this.height - 1, leftColor, rightColor);
    }

    private void renderHueBackground(GuiGraphicsExtractor guiGraphics, ColorSelectScreen screen) {
        if (screen.saturationSlider == null || screen.brightnessSlider == null) return;
        Function<Integer, Integer> lerp = (pct) -> (int) Math.floor(Mth.lerp(pct / 100f, this.getX() + 1, this.getX() + this.width - 1));
        Function<Integer, Integer> color = (pct) -> Color.HSBtoRGB(
            (pct / 100f), (float) (screen.saturationSlider.getValueInt() / ColorSelectScreen.MAX_VALUE_SB),
            (float) (screen.brightnessSlider.getValueInt() / ColorSelectScreen.MAX_VALUE_SB));
        int[] pts = {0, 17, 34, 50, 66, 82, 100};
        for (int i = 0; i < pts.length - 1; i++) {
            ScreenUtils.fillGradient(guiGraphics, lerp.apply(pts[i]), this.getY() + 1, lerp.apply(pts[i + 1]), this.getY() + this.height - 1, color.apply(pts[i]), color.apply(pts[i + 1]));
        }
    }

    private void renderSaturationBackground(GuiGraphicsExtractor guiGraphics, ColorSelectScreen screen) {
        if (screen.hueSlider == null || screen.brightnessSlider == null) return;
        int leftColor = Color.HSBtoRGB(
            (float) (screen.hueSlider.getValue() / ColorSelectScreen.MAX_VALUE_HUE), 0.0f,
            (float) (screen.brightnessSlider.getValue() / ColorSelectScreen.MAX_VALUE_SB));
        int rightColor = Color.HSBtoRGB(
            (float) (screen.hueSlider.getValue() / ColorSelectScreen.MAX_VALUE_HUE), 1.0f,
            (float) (screen.brightnessSlider.getValue() / ColorSelectScreen.MAX_VALUE_SB));
        ScreenUtils.fillGradient(guiGraphics, this.getX() + 1, this.getY() + 1, this.getX() + this.width - 1, this.getY() + this.height - 1, leftColor, rightColor);
    }

    private void renderBrightnessBackground(GuiGraphicsExtractor guiGraphics, ColorSelectScreen screen) {
        if (screen.hueSlider == null || screen.saturationSlider == null) return;
        int leftColor = Color.HSBtoRGB(
            (float) (screen.hueSlider.getValue() / ColorSelectScreen.MAX_VALUE_HUE),
            (float) (screen.saturationSlider.getValue() / ColorSelectScreen.MAX_VALUE_SB), 0.0f);
        int rightColor = Color.HSBtoRGB(
            (float) (screen.hueSlider.getValue() / ColorSelectScreen.MAX_VALUE_HUE),
            (float) (screen.saturationSlider.getValue() / ColorSelectScreen.MAX_VALUE_SB), 1.0f);
        ScreenUtils.fillGradient(guiGraphics, this.getX() + 1, this.getY() + 1, this.getX() + this.width - 1, this.getY() + this.height - 1, leftColor, rightColor);
    }
}