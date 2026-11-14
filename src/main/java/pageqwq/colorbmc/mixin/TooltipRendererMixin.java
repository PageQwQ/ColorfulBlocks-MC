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
import pageqwq.colorbmc.registry.DataComponentRegistry;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Tints ModernUI's item tooltips with the RGB color of a stained block / paint bucket.
 *
 * <h2>What this adapts, and why it is fragile by nature</h2>
 * ModernUI is not a compile-time dependency, so this cannot use its API: it reaches into
 * {@code icyllis.modernui.mc.TooltipRenderer} and its {@code icyllis.modernui.graphics.Color} helper by
 * name. Three members are read reflectively — {@code mWorkStrokeColor}, {@code mUseSpectrum} and the
 * static {@code sFillColor} — plus the static {@code Color.HSVToColor(float[])} method, and the injected
 * method is named {@code computeWorkingColor}. A ModernUI update that renames any of them turns this
 * feature off; it must never take the game down with it. That is what every guard below is for.
 *
 * <h2>Failure modes and guard for each</h2>
 * <ul>
 *   <li><b>ModernUI absent</b> — {@code @Pseudo} + {@code required: false} in colorblockmc.mixins.json,
 *       so the mixin is simply skipped.</li>
 *   <li><b>{@code computeWorkingColor} renamed</b> — the config's {@code injectors.defaultRequire} is 0
 *       and the injection carries {@code require = 0}, so a miss is silent instead of an InjectionError
 *       thrown out of ModernUI's render path. Do not raise either to 1: the target is a moving
 *       third-party mod, and a missed injection here means "no tint", not "broken game".</li>
 *   <li><b>A reflected member renamed</b> — {@link #rgbblocks$init()} reports once and gives up for the
 *       rest of the session, instead of retrying (and logging) on every tooltip render.</li>
 *   <li><b>Linkage errors</b> — {@code Class.forName} can throw {@link LinkageError}, not just an
 *       exception, if ModernUI is present but one of its own dependencies is not.</li>
 *   <li><b>{@code sFillColor} is static</b> — writing it leaks colors into every later tooltip, so the
 *       previous contents are snapshotted and restored at the start of the next call. See
 *       {@link #rgbblocks$restoreFillColor()}.</li>
 * </ul>
 *
 * <h2>Revisit when</h2>
 * ModernUI exposes a public tooltip-color API, or drops these members outright. The member names above
 * are the whole contract; when one changes, the fix is a rename here, not a redesign.
 */
@Pseudo
@Mixin(targets = "icyllis.modernui.mc.TooltipRenderer", remap = false)
public class TooltipRendererMixin {

    @Unique
    private static final Logger rgbblocks$LOGGER = LoggerFactory.getLogger("RGBBlocks-MUI");

    /** Reflected members resolved once; guarded by {@link #rgbblocks$initialized}. */
    @Unique
    private static volatile boolean rgbblocks$initialized = false;

    /** Set when resolution failed, so the failure is reported once rather than once per tooltip. */
    @Unique
    private static volatile boolean rgbblocks$initFailed = false;

    @Unique
    private static Field rgbblocks$mWorkStrokeColor;

    @Unique
    private static Field rgbblocks$mUseSpectrum;

    @Unique
    private static Field rgbblocks$sFillColor;

    @Unique
    private static Method rgbblocks$HSVToColor;

    /** What {@code sFillColor} held before this mixin overwrote it, or null when nothing is live. */
    @Unique
    private static volatile int[] rgbblocks$savedFill;

    @Inject(method = "computeWorkingColor", at = @At("RETURN"), require = 0, remap = false)
    private void rgbblocks$afterComputeWorkingColor(ItemStack item, CallbackInfo ci) {
        // Undo the previous call's write first. sFillColor is static and shared, so leaving our four
        // corner colors in it would tint the next ordinary (non-RGB) tooltip that does not overwrite it.
        // This runs before ModernUI computes the current item's colors, so it cannot affect this frame's
        // result -- our own values go back in at the end of this method.
        rgbblocks$restoreFillColor();

        if (item.isEmpty()) return;

        int color = -1;
        if (item.getItem() instanceof RGBBlockItem || item.getItem() instanceof PaintBucketItem) {
            color = item.getOrDefault(DataComponentRegistry.COLOR, -1);
        }

        if (color == -1) return;

        try {
            if (!rgbblocks$init()) return;

            // Convert RGB to HSV (H: 0-360, S: 0-1, V: 0-1) for ModernUI
            int r = (color >> 16) & 0xFF;
            int g = (color >> 8) & 0xFF;
            int b = color & 0xFF;
            float[] hsv = pageqwq.colorbmc.color.Color.RGBtoHSB(r, g, b);
            hsv[0] = hsv[0] * 360f; // Convert to degrees
            hsv[1] = Math.min(hsv[1], 0.9f);
            hsv[2] = org.joml.Math.clamp(hsv[2], 0.2f, 0.85f);

            // Generate 4 corner colors using ModernUI's own HSVToColor
            int c1 = rgbblocks$hsvToColor(hsv);
            int c2 = rgbblocks$adjustHsv(hsv.clone(), false, true, false, false);
            int c3 = rgbblocks$adjustHsv(hsv.clone(), true, true, true, false);
            int c4 = rgbblocks$adjustHsv(hsv.clone(), true, false, true, false);

            // Override mWorkStrokeColor. This one is an instance field, so it is naturally scoped to the
            // tooltip being built and needs no restore.
            int[] workStroke = (int[]) rgbblocks$mWorkStrokeColor.get(this);
            int[] sFill = (int[]) rgbblocks$sFillColor.get(null);
            if (workStroke == null || sFill == null) return;

            workStroke[0] = (0xFF000000) | c1;
            workStroke[1] = (0xFF000000) | c2;
            workStroke[2] = (0xFF000000) | c3;
            workStroke[3] = (0xFF000000) | c4;

            // Set fill color to match (with transparency), reusing the alpha ModernUI computed.
            // Snapshot the static array before touching it so the next call can put it back.
            rgbblocks$savedFill = sFill.clone();
            int fillAlpha = (sFill[0] >>> 24);
            for (int i = 0; i < 4 && i < sFill.length; i++) {
                sFill[i] = (c1 & 0x00FFFFFF) | (fillAlpha << 24);
            }

            rgbblocks$mUseSpectrum.setBoolean(this, false);

            rgbblocks$LOGGER.debug("Overrode tooltip colors for #{}", Integer.toHexString(color).substring(2));
        } catch (Exception | LinkageError e) {
            // Never let a tooltip coloring failure escape into ModernUI's render path.
            rgbblocks$LOGGER.warn("Failed to override tooltip colors", e);
            rgbblocks$restoreFillColor();
        }
    }

    /**
     * Restores {@code sFillColor} to what it held before this mixin overwrote it, and clears the
     * snapshot. Safe to call when nothing was written.
     */
    @Unique
    private static void rgbblocks$restoreFillColor() {
        int[] saved = rgbblocks$savedFill;
        if (saved == null) return;
        rgbblocks$savedFill = null;
        try {
            if (rgbblocks$sFillColor == null) return;
            int[] live = (int[]) rgbblocks$sFillColor.get(null);
            if (live == null) return;
            System.arraycopy(saved, 0, live, 0, Math.min(saved.length, live.length));
        } catch (Exception | LinkageError e) {
            rgbblocks$LOGGER.debug("Could not restore ModernUI fill color", e);
        }
    }

    /**
     * Resolves the reflected members, once. Returns false when the target shape is not what this mixin
     * was written against; that is reported a single time and then latched, because this runs on the
     * render path and a per-tooltip retry would flood the log.
     */
    @Unique
    private static boolean rgbblocks$init() {
        if (rgbblocks$initFailed) return false;
        if (rgbblocks$initialized) return true;
        try {
            Class<?> target = Class.forName("icyllis.modernui.mc.TooltipRenderer");
            rgbblocks$mWorkStrokeColor = target.getDeclaredField("mWorkStrokeColor");
            rgbblocks$mWorkStrokeColor.setAccessible(true);
            rgbblocks$mUseSpectrum = target.getDeclaredField("mUseSpectrum");
            rgbblocks$mUseSpectrum.setAccessible(true);
            rgbblocks$sFillColor = target.getField("sFillColor");

            Class<?> colorClass = Class.forName("icyllis.modernui.graphics.Color");
            rgbblocks$HSVToColor = colorClass.getMethod("HSVToColor", float[].class);

            rgbblocks$initialized = true;
            return true;
        } catch (Exception | LinkageError e) {
            rgbblocks$initFailed = true;
            rgbblocks$LOGGER.warn("ModernUI tooltip tinting is OFF for this session: {}. This mod reflects "
                + "ModernUI's mWorkStrokeColor / mUseSpectrum / sFillColor and Color.HSVToColor; a ModernUI "
                + "update that renames them costs the tint, nothing else. Colors still work everywhere "
                + "except this tooltip.", e.toString());
            return false;
        }
    }

    @Unique
    private static int rgbblocks$hsvToColor(float[] hsv) {
        try {
            if (rgbblocks$HSVToColor == null) throw new IllegalStateException("not initialized");
            return (int) rgbblocks$HSVToColor.invoke(null, (Object) hsv);
        } catch (Exception | LinkageError e) {
            return pageqwq.colorbmc.color.Color.HSBtoRGB(hsv[0] / 360f, hsv[1], hsv[2]);
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
