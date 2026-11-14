package pageqwq.colorbmc.color;

import net.minecraft.world.level.material.MapColor;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/** Resolves an RGB color to the nearest vanilla map color. */
public final class MapColorResolver {
    private static final MapColor[] PALETTE = new MapColor[63];

    static {
        for (int id = 1; id < 64; id++) {
            PALETTE[id - 1] = MapColor.byId(id);
        }
    }

    // Bounded so a server handing out many distinct colors cannot grow this forever.
    private static final int MAX_CACHE_SIZE = 4096;
    private static final Map<Integer, MapColor> CACHE = new ConcurrentHashMap<>();

    private MapColorResolver() {
    }

    public static MapColor forColor(int argb) {
        int rgb = argb & 0xFFFFFF;
        MapColor cached = CACHE.get(rgb);
        if (cached != null) {
            return cached;
        }
        if (CACHE.size() >= MAX_CACHE_SIZE) {
            CACHE.clear();
        }
        MapColor resolved = nearest(rgb);
        CACHE.put(rgb, resolved);
        return resolved;
    }

    private static MapColor nearest(int rgb) {
        int red = (rgb >> 16) & 0xFF;
        int green = (rgb >> 8) & 0xFF;
        int blue = rgb & 0xFF;

        MapColor best = MapColor.NONE;
        double bestDistance = Double.MAX_VALUE;
        for (MapColor candidate : PALETTE) {
            int color = candidate.col;
            int dr = red - ((color >> 16) & 0xFF);
            int dg = green - ((color >> 8) & 0xFF);
            int db = blue - (color & 0xFF);
            // Weighted RGB distance: the eye is most sensitive to green and least to blue.
            double distance = 0.30 * dr * dr + 0.59 * dg * dg + 0.11 * db * db;
            if (distance < bestDistance) {
                bestDistance = distance;
                best = candidate;
            }
        }
        return best;
    }
}
