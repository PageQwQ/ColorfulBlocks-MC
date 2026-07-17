package platinpython.rgbblocks.mixin;

import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import platinpython.rgbblocks.item.PaintBucketItem;
import platinpython.rgbblocks.item.RGBBlockItem;
import platinpython.rgbblocks.util.registries.DataComponentRegistry;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

@Pseudo
@Mixin(targets = "icyllis.modernui.mc.TooltipRenderer", remap = false)
public class TooltipRendererMixin {

    private static Field rgbblocks$workStrokeColorField;
    private static Field rgbblocks$useSpectrumField;
    private static Field rgbblocks$sStrokeColorField;
    private static Field rgbblocks$sFillColorField;
    private static Method rgbblocks$hsvToColor;

    @Inject(method = "computeWorkingColor", at = @At("HEAD"), cancellable = true, remap = false)
    private void rgbblocks$overrideWorkingColor(ItemStack item, CallbackInfo ci) {
        if (item.isEmpty()) return;

        int color = -1;
        if (item.getItem() instanceof RGBBlockItem || item.getItem() instanceof PaintBucketItem) {
            color = item.getOrDefault(DataComponentRegistry.COLOR, -1);
        }

        if (color != -1) {
            try {
                int r = (color >> 16) & 0xFF;
                int g = (color >> 8) & 0xFF;
                int b = color & 0xFF;
                float[] hsv = platinpython.rgbblocks.util.Color.RGBtoHSB(r, g, b);

                // Convert H to degrees (0-360) for ModernUI color math
                hsv[0] = hsv[0] * 360f;
                hsv[1] = Math.min(hsv[1], 0.9f);
                hsv[2] = org.joml.Math.clamp(hsv[2], 0.2f, 0.85f);

                int c1 = hsvToColorInt(hsv);
                int c2 = adjustColor(hsv.clone(), false, true, false, false);
                int c3 = adjustColor(hsv.clone(), true, true, true, false);
                int c4 = adjustColor(hsv.clone(), true, false, true, false);

                initFields();

                int[] sStrokeColor = (int[]) rgbblocks$sStrokeColorField.get(null);
                int[] sFillColor = (int[]) rgbblocks$sFillColorField.get(null);

                int[] workStrokeColor = (int[]) rgbblocks$workStrokeColorField.get(this);
                workStrokeColor[0] = (sStrokeColor[0] & 0xFF000000) | c1;
                workStrokeColor[1] = (sStrokeColor[1] & 0xFF000000) | c2;
                workStrokeColor[2] = (sStrokeColor[2] & 0xFF000000) | c3;
                workStrokeColor[3] = (sStrokeColor[3] & 0xFF000000) | c4;

                int fillAlpha = (sFillColor[0] >>> 24);
                for (int i = 0; i < 4; i++) {
                    sFillColor[i] = (c1 & 0x00FFFFFF) | (fillAlpha << 24);
                }

                rgbblocks$useSpectrumField.setBoolean(this, false);
            } catch (Exception ignored) {
            }
        }
    }

    private static void initFields() throws Exception {
        if (rgbblocks$sStrokeColorField == null) {
            Class<?> target = Class.forName("icyllis.modernui.mc.TooltipRenderer");
            rgbblocks$sStrokeColorField = target.getField("sStrokeColor");
            rgbblocks$sFillColorField = target.getField("sFillColor");
            rgbblocks$workStrokeColorField = target.getDeclaredField("mWorkStrokeColor");
            rgbblocks$workStrokeColorField.setAccessible(true);
            rgbblocks$useSpectrumField = target.getDeclaredField("mUseSpectrum");
            rgbblocks$useSpectrumField.setAccessible(true);
            rgbblocks$hsvToColor = Class.forName("icyllis.modernui.graphics.Color")
                .getMethod("HSVToColor", float[].class);
        }
    }

    private static int hsvToColorInt(float[] hsv) {
        try {
            return (int) rgbblocks$hsvToColor.invoke(null, (Object) hsv);
        } catch (Exception e) {
            return platinpython.rgbblocks.util.Color.HSBtoRGB(hsv[0] / 360f, hsv[1], hsv[2]);
        }
    }

    private static int adjustColor(float[] hsv, boolean hue, boolean sat, boolean val, boolean magnified) {
        if (hue) {
            if (hsv[0] >= 60f && hsv[0] <= 240f) {
                hsv[0] += magnified ? 27f : 15f;
            } else {
                hsv[0] -= magnified ? 18f : 10f;
            }
            hsv[0] = (hsv[0] + 360f) % 360f;
        }
        if (sat) {
            if (hsv[1] < 0.6f) {
                hsv[1] += magnified ? 0.18f : 0.12f;
            } else {
                hsv[1] -= magnified ? 0.12f : 0.06f;
            }
        }
        if (val) {
            if (hsv[2] < 0.6f) {
                hsv[2] += magnified ? 0.12f : 0.08f;
            } else {
                hsv[2] -= magnified ? 0.08f : 0.04f;
            }
        }
        return hsvToColorInt(hsv);
    }
}
