package psycho.euphoria.autoclicker;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;
import android.util.Pair;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static psycho.euphoria.autoclicker.Shared.requestAccessibilityPermission;

public class Utils {

    // adb shell am start -a android.settings.ACCESSIBILITY_SETTINGS
    // https://developer.android.com/guide/topics/ui/accessibility/service
    public static long getDirectorySize(File directory) {
        if (directory.isDirectory()) {
            long size = 0;
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    size += getDirectorySize(file);
                }
            }
            return size;
        } else if (directory.isFile()) {
            return directory.length();
        } else {
            return 0;
        }
    }

    public static void startScreenRecorder(Context context, final int resultCode, final Intent data) {
        final Intent intent = new Intent(context, AutoService.class);
        intent.setAction(AutoService.ACTION_START);
        intent.putExtra(AutoService.EXTRA_RESULT_CODE, resultCode);
        intent.putExtras(data);
        context.startService(intent);

    }

    private void listLargestDirectories() {
        new Thread(() -> {
            File[] directories = new File(Environment.getExternalStorageDirectory(), "").listFiles(file -> file.isDirectory());
            List<Pair<String, Long>> data = new ArrayList<>();
            for (File dir : directories) {
                try {
                    data.add(Pair.create(dir.getName(), getDirectorySize(dir)));
                } catch (Exception ignored) {
                }
            }
            data.sort((s1, s2) -> {
                long dif = s1.second - s2.second;
                if (dif > 0)
                    return -1;
                else if (dif < 0)
                    return 1;
                return 0;
            });
            for (Pair<String, Long> p : data) {
                Log.e("B5aOx2", String.format("onCreate, %s == %s", p.first, p.second));
            }
        }).start();
    }

    public static boolean compareColor(int color, int red, int redOffset, int green, int greenOffset, int blue, int blueOffset) {
        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int b = color & 0xFF;
        Log.e("B5aOx2", String.format("compareColor, %s %s %s %s", color, r, g, b));
        if (redOffset == 0 && r != red) {
            return false;
        }
        if (greenOffset == 0 && g != green) {
            return false;
        }
        if (blueOffset == 0 && b != blue) {
            return false;
        }
        if (r < red - redOffset || r > red + redOffset) {
            return false;
        }
        if (g < green - greenOffset || g > green + greenOffset) {
            return false;
        }
        if (b < blue - blueOffset || b > blue + blueOffset) {
            return false;
        }
        return true;
    }

    public static boolean compareColor(Bitmap bitmap, int x, int y, int red, int redOffset, int green, int greenOffset, int blue, int blueOffset) {
        return compareColor(bitmap.getPixel(x, y), red, redOffset, green, greenOffset, blue, blueOffset);
    }

    public static boolean compareColor(Bitmap bitmap, int x, int y, int color) {
        int j = bitmap.getPixel(x, y);
        return j == color;
    }

    public static void dumpColor(Bitmap bitmap, int x, int y) {
        int color = bitmap.getPixel(x, y);
        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int b = color & 0xFF;
        Log.e("B5aOx2", String.format("dumpColor, %sx%s = %s = %s,%s,%s", x, y, color, r, g, b));
    }

    public static boolean compareColor(Bitmap bitmap, int... values) {
        for (int i = 0; i < values.length; i += 3) {
            if (!compareColor(bitmap, values[i], values[i + 1], values[i + 2])) {
                return false;
            }
        }
        return true;
    }

    public static void dumpColor(Bitmap bitmap, int... values) {
        for (int i = 0; i < values.length; i += 2) {
            dumpColor(bitmap, values[i], values[i + 1]);
        }
    }
}