package pageqwq.colorbmc.mixin;

import net.minecraft.world.item.ItemStack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import pageqwq.colorbmc.item.PaintBucketItem;
import pageqwq.colorbmc.item.RGBBlockItem;
import pageqwq.colorbmc.util.registries.DataComponentRegistry;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

@Pseudo
@Mixin(targets = "icyllis.modernui.mc.TooltipRenderer", remap = false)
public class TooltipRendererMixin {

    @Unique
    private static final Logger rgbblocks$LOGGER = LoggerFactory.getLogger("RGBBlocks-MUI");

    @Unique
    private static boolean rgbblocks$initialized = false;

    @Unique
    private static Field rgbblocks$mWorkStrokeColor;

    @Unique
    private static Field rgbblocks$mUseSpectrum;

    @Unique
    private static Field rgbblocks$sFillColor;

    @Unique
    private static Method rgbblocks$HSVToColor;

    @Inject(method = "computeWorkingColor", at = @At("RETURN"), remap = false)
    private void rgbblocks$afterComputeWorkingColor(ItemStack item, CallbackInfo ci) {
        if (item.isEmpty()) return;

        int color = -1;
        if (item.getItem() instanceof RGBBlockItem || item.getItem() instanceof PaintBucketItem) {
            color = item.getOrDefault(DataComponentRegistry.COLOR, -1);
        }

        if (color == -1) return;

        try {
            if (!rgbblocks$initialized) {
                rgbblocks$init();
            }

            // Convert RGB to HSV (H: 0-360, S: 0-1, V: 0-1) for ModernUI
            int r = (color >> 16) & 0xFF;
            int g = (color >> 8) & 0xFF;
            int b = color & 0xFF;
            float[] hsv = pageqwq.colorbmc.util.Color.RGBtoHSB(r, g, b);
            hsv[0] = hsv[0] * 360f; // Convert to degrees
            hsv[1] = Math.min(hsv[1], 0.9f);
            hsv[2] = org.joml.Math.clamp(hsv[2], 0.2f, 0.85f);

            // Generate 4 corner colors using ModernUI's own HSVToColor
            int c1 = rgbblocks$hsvToColor(hsv);
            int c2 = rgbblocks$adjustHsv(hsv.clone(), false, true, false, false);
            int c3 = rgbblocks$adjustHsv(hsv.clone(), true, true, true, false);
            int c4 = rgbblocks$adjustHsv(hsv.clone(), true, false, true, false);

            // Override mWorkStrokeColor
            int[] workStroke = (int[]) rgbblocks$mWorkStrokeColor.get(this);
            int[] sStroke = (int[]) rgbblocks$sFillColor.get(null); // reuse alpha from sFillColor
            workStroke[0] = (0xFF000000) | c1;
            workStroke[1] = (0xFF000000) | c2;
            workStroke[2] = (0xFF000000) | c3;
            workStroke[3] = (0xFF000000) | c4;

            // Set fill color to match (with transparency)
            int[] sFill = (int[]) rgbblocks$sFillColor.get(null);
            int fillAlpha = (sFill[0] >>> 24);
            for (int i = 0; i < 4; i++) {
                sFill[i] = (c1 & 0x00FFFFFF) | (fillAlpha << 24);
            }

            rgbblocks$mUseSpectrum.setBoolean(this, false);

            rgbblocks$LOGGER.debug("Overrode tooltip colors for #{}", Integer.toHexString(color).substring(2));
        } catch (Exception e) {
            rgbblocks$LOGGER.warn("Failed to override tooltip colors", e);
        }
    }

    @Unique
    private static void rgbblocks$init() throws Exception {
        Class<?> target = Class.forName("icyllis.modernui.mc.TooltipRenderer");
        rgbblocks$mWorkStrokeColor = target.getDeclaredField("mWorkStrokeColor");
        rgbblocks$mWorkStrokeColor.setAccessible(true);
        rgbblocks$mUseSpectrum = target.getDeclaredField("mUseSpectrum");
        rgbblocks$mUseSpectrum.setAccessible(true);
        rgbblocks$sFillColor = target.getField("sFillColor");

        Class<?> colorClass = Class.forName("icyllis.modernui.graphics.Color");
        rgbblocks$HSVToColor = colorClass.getMethod("HSVToColor", float[].class);

        rgbblocks$initialized = true;
    }

    @Unique
    private static int rgbblocks$hsvToColor(float[] hsv) {
        try {
            return (int) rgbblocks$HSVToColor.invoke(null, (Object) hsv);
        } catch (Exception e) {
            return pageqwq.colorbmc.util.Color.HSBtoRGB(hsv[0] / 360f, hsv[1], hsv[2]);
        }
    }

    @Unique
    private static int rgbblocks$adjustHsv(float[] hsv, boolean hue, boolean sat, boolean val, boolean magnified) {
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
        return rgbblocks$hsvToColor(hsv);
    }
}
