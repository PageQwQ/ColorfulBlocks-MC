package pageqwq.colorbmc.client.gui.screen;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import pageqwq.colorbmc.client.gui.ScreenUtils;
import pageqwq.colorbmc.client.gui.widget.ColorSlider;
import pageqwq.colorbmc.client.gui.widget.SliderType;
import pageqwq.colorbmc.util.Color;
import pageqwq.colorbmc.util.network.packets.PaintBucketSyncPayload;

import java.util.Locale;

public class ColorSelectScreen extends Screen {
    private final double red, green, blue;
    public ColorSlider redSlider, greenSlider, blueSlider;
    private final double hue, saturation, brightness;
    public ColorSlider hueSlider, saturationSlider, brightnessSlider;
    public EditBox hexBox;

    private static final int WIDGET_HEIGHT = 18;
    private static final int PANEL_WIDTH = 280;
    private static final int LABEL_WIDTH = 44;
    private static final int VALUE_WIDTH = 32;
    private static final int SLIDER_WIDTH = PANEL_WIDTH - LABEL_WIDTH - VALUE_WIDTH;
    private static final int PANEL_HEIGHT = 36;
    private static final int SPACING = 22;

    public static final double MIN_VALUE = 0.0D;
    public static final double MAX_VALUE_RGB = 255.0D;
    public static final double MAX_VALUE_HUE = 360.0D;
    public static final double MAX_VALUE_SB = 100.0D;

    private boolean isRGBSelected;
    private final Component useRGBText, useHSBText;
    private boolean valuesInitialized = false;

    private final Component redText, greenText, blueText;
    private final Component hueText, saturationText, brightnessText;

    public ColorSelectScreen(int colorIn, boolean isRGBSelected) {
        super(Component.empty());
        Color color = new Color(colorIn);
        this.red = color.getRed();
        this.green = color.getGreen();
        this.blue = color.getBlue();

        float[] hsb = Color.RGBtoHSB((int) red, (int) green, (int) blue);
        this.hue = hsb[0] * MAX_VALUE_HUE;
        this.saturation = hsb[1] * MAX_VALUE_SB;
        this.brightness = hsb[2] * MAX_VALUE_SB;

        this.isRGBSelected = isRGBSelected;
        this.useRGBText = Component.translatable("gui.colorblockmc.useRGB");
        this.useHSBText = Component.translatable("gui.colorblockmc.useHSB");

        this.redText = Component.translatable("gui.colorblockmc.red");
        this.greenText = Component.translatable("gui.colorblockmc.green");
        this.blueText = Component.translatable("gui.colorblockmc.blue");
        this.hueText = Component.translatable("gui.colorblockmc.hue");
        this.saturationText = Component.translatable("gui.colorblockmc.saturation");
        this.brightnessText = Component.translatable("gui.colorblockmc.brightness");
    }

    public int getColor() {
        if (isRGBSelected && redSlider != null && greenSlider != null && blueSlider != null) {
            return new Color(redSlider.getValueInt(), greenSlider.getValueInt(), blueSlider.getValueInt()).getRGB();
        } else if (hueSlider != null && saturationSlider != null && brightnessSlider != null) {
            return Color.getHSBColor(
                (float) (hueSlider.getValueInt() / MAX_VALUE_HUE),
                (float) (saturationSlider.getValueInt() / MAX_VALUE_SB),
                (float) (brightnessSlider.getValueInt() / MAX_VALUE_SB)
            ).getRGB();
        }
        return 0;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    protected void init() {
        int baseX = this.width / 2 - PANEL_WIDTH / 2;
        int sliderX = baseX + LABEL_WIDTH;
        int y = this.height / 2 - 80;

        y += PANEL_HEIGHT + 10;

        // RGB Sliders
        if (redSlider == null) {
            redSlider = new ColorSlider(sliderX, y, SLIDER_WIDTH, WIDGET_HEIGHT,
                Component.empty(), MIN_VALUE, MAX_VALUE_RGB, this.red, SliderType.RED);
        } else { redSlider.setX(sliderX); redSlider.setY(y); }
        y += SPACING;

        if (greenSlider == null) {
            greenSlider = new ColorSlider(sliderX, y, SLIDER_WIDTH, WIDGET_HEIGHT,
                Component.empty(), MIN_VALUE, MAX_VALUE_RGB, this.green, SliderType.GREEN);
        } else { greenSlider.setX(sliderX); greenSlider.setY(y); }
        y += SPACING;

        if (blueSlider == null) {
            blueSlider = new ColorSlider(sliderX, y, SLIDER_WIDTH, WIDGET_HEIGHT,
                Component.empty(), MIN_VALUE, MAX_VALUE_RGB, this.blue, SliderType.BLUE);
        } else { blueSlider.setX(sliderX); blueSlider.setY(y); }

        // HSB Sliders
        int hsbY = this.height / 2 - 80 + PANEL_HEIGHT + 10;
        if (hueSlider == null) {
            hueSlider = new ColorSlider(sliderX, hsbY, SLIDER_WIDTH, WIDGET_HEIGHT,
                Component.empty(), MIN_VALUE, MAX_VALUE_HUE, this.hue, SliderType.HUE);
        } else { hueSlider.setX(sliderX); hueSlider.setY(hsbY); }
        hsbY += SPACING;

        if (saturationSlider == null) {
            saturationSlider = new ColorSlider(sliderX, hsbY, SLIDER_WIDTH, WIDGET_HEIGHT,
                Component.empty(), MIN_VALUE, MAX_VALUE_SB, this.saturation, SliderType.SATURATION);
        } else { saturationSlider.setX(sliderX); saturationSlider.setY(hsbY); }
        hsbY += SPACING;

        if (brightnessSlider == null) {
            brightnessSlider = new ColorSlider(sliderX, hsbY, SLIDER_WIDTH, WIDGET_HEIGHT,
                Component.empty(), MIN_VALUE, MAX_VALUE_SB, this.brightness, SliderType.BRIGHTNESS);
        } else { brightnessSlider.setX(sliderX); brightnessSlider.setY(hsbY); }

        // Hex input + toggle button row
        int rowY = this.height / 2 - 80 + PANEL_HEIGHT + 10 + 3 * SPACING + 8;

        int hexBoxW = 80;
        int toggleW = 120;
        int totalRowW = hexBoxW + 8 + toggleW;
        int rowX = this.width / 2 - totalRowW / 2;

        if (hexBox == null) {
            hexBox = new EditBox(this.font, rowX, rowY, hexBoxW, WIDGET_HEIGHT, Component.literal("Hex")) {
                @Override
                public void insertText(String textToWrite) {
                    textToWrite = textToWrite.contains("#") ? textToWrite.substring(1) : textToWrite;
                    textToWrite = textToWrite.toUpperCase(Locale.ENGLISH);
                    super.insertText(textToWrite);
                    if (valuesInitialized) updateFromHex();
                }

                @Override
                public void deleteChars(int pNum) {
                    super.deleteChars(pNum);
                    if (valuesInitialized) updateFromHex();
                }

                private void updateFromHex() {
                    try {
                        String raw = getValue();
                        if (raw.contains("#")) raw = raw.substring(1);
                        if (raw.length() < 6) return;
                        Color color = new Color(Integer.parseInt(raw, 16));
                        redSlider.setValueInt(color.getRed());
                        greenSlider.setValueInt(color.getGreen());
                        blueSlider.setValueInt(color.getBlue());
                    } catch (NumberFormatException ignored) {}
                }
            };
            hexBox.setMaxLength(7);
            hexBox.setValue("#" + Integer.toHexString(
                new Color((int) red, (int) green, (int) blue).getRGB()
            ).substring(2).toUpperCase(Locale.ENGLISH));
        } else { hexBox.setX(rowX); hexBox.setY(rowY); }

        Button toggleButton = new Button.Builder(isRGBSelected ? useHSBText : useRGBText, button -> {
            isRGBSelected = !isRGBSelected;
            redSlider.visible = isRGBSelected;
            greenSlider.visible = isRGBSelected;
            blueSlider.visible = isRGBSelected;
            hueSlider.visible = !isRGBSelected;
            saturationSlider.visible = !isRGBSelected;
            brightnessSlider.visible = !isRGBSelected;

            if (isRGBSelected) {
                Color c = Color.getHSBColor(
                    (float) (hueSlider.getValueInt() / MAX_VALUE_HUE),
                    (float) (saturationSlider.getValueInt() / MAX_VALUE_SB),
                    (float) (brightnessSlider.getValueInt() / MAX_VALUE_SB));
                redSlider.setValueInt(c.getRed());
                greenSlider.setValueInt(c.getGreen());
                blueSlider.setValueInt(c.getBlue());
                hexBox.setValue("#" + Integer.toHexString(c.getRGB()).substring(2).toUpperCase(Locale.ENGLISH));
                button.setMessage(useHSBText);
            } else {
                float[] hsb = Color.RGBtoHSB(redSlider.getValueInt(), greenSlider.getValueInt(), blueSlider.getValueInt());
                hueSlider.setValueInt((int) (hsb[0] * MAX_VALUE_HUE));
                saturationSlider.setValueInt((int) (hsb[1] * MAX_VALUE_SB));
                brightnessSlider.setValueInt((int) (hsb[2] * MAX_VALUE_SB));
                button.setMessage(useRGBText);
            }
        }).bounds(rowX + hexBoxW + 8, rowY, toggleW, WIDGET_HEIGHT).build();

        if (!isRGBSelected) {
            redSlider.visible = false; greenSlider.visible = false; blueSlider.visible = false;
        } else {
            hueSlider.visible = false; saturationSlider.visible = false; brightnessSlider.visible = false;
        }

        addRenderableWidget(redSlider); addRenderableWidget(greenSlider); addRenderableWidget(blueSlider);
        addRenderableWidget(hueSlider); addRenderableWidget(saturationSlider); addRenderableWidget(brightnessSlider);
        addRenderableWidget(hexBox);
        addRenderableWidget(toggleButton);

        valuesInitialized = true;
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float a) {
        super.extractRenderState(guiGraphics, mouseX, mouseY, a);

        int baseX = this.width / 2 - PANEL_WIDTH / 2;
        int panelY = this.height / 2 - 80;

        // Color preview panel
        guiGraphics.fill(baseX - 1, panelY - 1, baseX + PANEL_WIDTH + 1, panelY + PANEL_HEIGHT + 1, 0xFF444444);
        guiGraphics.fill(baseX, panelY, baseX + PANEL_WIDTH, panelY + PANEL_HEIGHT, 0xFF000000);
        guiGraphics.fill(baseX + 1, panelY + 1, baseX + PANEL_WIDTH - 1, panelY + PANEL_HEIGHT - 1, getColor());

        // Draw labels and values for visible sliders
        int y = panelY + PANEL_HEIGHT + 10;
        if (isRGBSelected) {
            drawLabel(guiGraphics, baseX, y, redText); y += SPACING;
            drawLabel(guiGraphics, baseX, y, greenText); y += SPACING;
            drawLabel(guiGraphics, baseX, y, blueText);
        } else {
            drawLabel(guiGraphics, baseX, y, hueText); y += SPACING;
            drawLabel(guiGraphics, baseX, y, saturationText); y += SPACING;
            drawLabel(guiGraphics, baseX, y, brightnessText);
        }
    }

    private void drawLabel(GuiGraphicsExtractor guiGraphics, int baseX, int y, Component label) {
        int labelY = y + (WIDGET_HEIGHT - 9) / 2;
        guiGraphics.text(font, label, baseX + 2, labelY, 0xFFFFFFFF, true);
    }

    @Override
    public void onClose() {
        ClientPlayNetworking.send(new PaintBucketSyncPayload(getColor(), isRGBSelected));
        super.onClose();
    }
}
