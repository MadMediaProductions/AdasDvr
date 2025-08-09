package v4;

import android.content.Context;
import android.os.Build;
import android.os.Process;
import java.io.File;

public class ContextCompat {
    protected ContextCompat() {
    }

    public static File[] getExternalFilesDirs(Context context, String type) {
        if (Build.VERSION.SDK_INT >= 19) {
            return ContextCompatKitKat.getExternalFilesDirs(context, type);
        }
        return new File[]{context.getExternalFilesDir(type)};
    }

    public static File[] getExternalCacheDirs(Context context) {
        if (Build.VERSION.SDK_INT >= 19) {
            return ContextCompatKitKat.getExternalCacheDirs(context);
        }
        return new File[]{context.getExternalCacheDir()};
    }

    private static File buildPath(File base, String... segments) {
        File cur;
        int length = segments.length;
        int i = 0;
        File cur2 = base;
        while (i < length) {
            String segment = segments[i];
            if (cur2 == null) {
                cur = new File(segment);
            } else if (segment != null) {
                cur = new File(cur2, segment);
            } else {
                cur = cur2;
            }
            i++;
            cur2 = cur;
        }
        return cur2;
    }

    public static int checkSelfPermission(Context context, String permission) {
        if (permission != null) {
            return context.checkPermission(permission, Process.myPid(), Process.myUid());
        }
        throw new IllegalArgumentException("permission is null");
    }
}
