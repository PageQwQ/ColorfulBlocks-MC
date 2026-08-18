package pageqwq.colorbmc.client.util;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;

import java.io.BufferedReader;
import java.util.Map;

public final class ColorNames {
    private static int[] enRgb;
    private static String[] enNames;
    private static int[] zhRgb;
    private static String[] zhNames;

    private ColorNames() {}

    private static void load() {
        if (enRgb != null) {
            return;
        }
        enRgb = new int[0];
        enNames = new String[0];
        zhRgb = new int[0];
        zhNames = new String[0];
        try {
            Map.Entry<int[], String[]> en = parse("colornames/en.json");
            Map.Entry<int[], String[]> zh = parse("colornames/zh.json");
            enRgb = en.getKey();
            enNames = en.getValue();
            zhRgb = zh.getKey();
            zhNames = zh.getValue();
        } catch (Exception ignored) {
        }
    }

    private static Map.Entry<int[], String[]> parse(String path) throws Exception {
        Map<String, String> map;
        try (BufferedReader reader = Minecraft.getInstance().getResourceManager()
            .openAsReader(Identifier.fromNamespaceAndPath("colorblockmc", path))) {
            map = new Gson().fromJson(reader, new TypeToken<Map<String, String>>() {
            }.getType());
        }
        int[] rgb = new int[map.size()];
        String[] names = new String[map.size()];
        int i = 0;
        for (Map.Entry<String, String> entry : map.entrySet()) {
            try {
                rgb[i] = Integer.parseInt(entry.getKey(), 16);
                names[i] = entry.getValue();
                i++;
            } catch (NumberFormatException ignored) {
            }
        }
        int[] trimmedRgb = new int[i];
        String[] trimmedNames = new String[i];
        System.arraycopy(rgb, 0, trimmedRgb, 0, i);
        System.arraycopy(names, 0, trimmedNames, 0, i);
        return new java.util.AbstractMap.SimpleEntry<>(trimmedRgb, trimmedNames);
    }

    public static String nearestEnglish(int r, int g, int b) {
        load();
        return nearest(enRgb, enNames, r, g, b);
    }

    public static String nearestChinese(int r, int g, int b) {
        load();
        return nearest(zhRgb, zhNames, r, g, b);
    }

    private static String nearest(int[] rgb, String[] names, int r, int g, int b) {
        String best = "";
        int bestDist = Integer.MAX_VALUE;
        for (int i = 0; i < rgb.length; i++) {
            int cr = (rgb[i] >> 16) & 0xFF;
            int cg = (rgb[i] >> 8) & 0xFF;
            int cb = rgb[i] & 0xFF;
            int dr = r - cr;
            int dg = g - cg;
            int db = b - cb;
            int dist = dr * dr + dg * dg + db * db;
            if (dist < bestDist) {
                bestDist = dist;
                best = names[i];
            }
        }
        return best;
    }
}