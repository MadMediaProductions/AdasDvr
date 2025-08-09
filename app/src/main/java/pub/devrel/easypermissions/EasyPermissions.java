package pub.devrel.easypermissions;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import pub.devrel.easypermissions.helper.PermissionHelper;

public class EasyPermissions {
    private static final String TAG = "EasyPermissions";

    public interface PermissionCallbacks extends ActivityCompat.OnRequestPermissionsResultCallback {
        void onPermissionsDenied(int i, List<String> list);

        void onPermissionsGranted(int i, List<String> list);
    }

    public static boolean hasPermissions(Context context, @NonNull String... perms) {
        if (Build.VERSION.SDK_INT < 23) {
            Log.w(TAG, "hasPermissions: API version < M, returning true by default");
            return true;
        } else if (context == null) {
            throw new IllegalArgumentException("Can't check permissions for null context");
        } else {
            for (String perm : perms) {
                if (ContextCompat.checkSelfPermission(context, perm) != 0) {
                    return false;
                }
            }
            return true;
        }
    }

    public static void requestPermissions(@NonNull Activity host, @NonNull String rationale, int requestCode, @NonNull String... perms) {
        requestPermissions(host, rationale, 17039370, 17039360, requestCode, perms);
    }

    public static void requestPermissions(@NonNull Fragment host, @NonNull String rationale, int requestCode, @NonNull String... perms) {
        requestPermissions(host, rationale, 17039370, 17039360, requestCode, perms);
    }

    public static void requestPermissions(@NonNull android.app.Fragment host, @NonNull String rationale, int requestCode, @NonNull String... perms) {
        requestPermissions(host, rationale, 17039370, 17039360, requestCode, perms);
    }

    public static void requestPermissions(@NonNull Activity host, @NonNull String rationale, @StringRes int positiveButton, @StringRes int negativeButton, int requestCode, @NonNull String... perms) {
        requestPermissions(PermissionHelper.newInstance(host), rationale, positiveButton, negativeButton, requestCode, perms);
    }

    public static void requestPermissions(@NonNull Fragment host, @NonNull String rationale, @StringRes int positiveButton, @StringRes int negativeButton, int requestCode, @NonNull String... perms) {
        requestPermissions(PermissionHelper.newInstance(host), rationale, positiveButton, negativeButton, requestCode, perms);
    }

    public static void requestPermissions(@NonNull android.app.Fragment host, @NonNull String rationale, @StringRes int positiveButton, @StringRes int negativeButton, int requestCode, @NonNull String... perms) {
        requestPermissions(PermissionHelper.newInstance(host), rationale, positiveButton, negativeButton, requestCode, perms);
    }

    private static void requestPermissions(@NonNull PermissionHelper helper, @NonNull String rationale, @StringRes int positiveButton, @StringRes int negativeButton, int requestCode, @NonNull String... perms) {
        if (hasPermissions(helper.getContext(), perms)) {
            notifyAlreadyHasPermissions(helper.getHost(), requestCode, perms);
        } else {
            helper.requestPermissions(rationale, positiveButton, negativeButton, requestCode, perms);
        }
    }

    public static void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults, @NonNull Object... receivers) {
        List<String> granted = new ArrayList<>();
        List<String> denied = new ArrayList<>();
        for (int i = 0; i < permissions.length; i++) {
            String perm = permissions[i];
            if (grantResults[i] == 0) {
                granted.add(perm);
            } else {
                denied.add(perm);
            }
        }
        for (PermissionCallbacks permissionCallbacks : receivers) {
            if (!granted.isEmpty() && (permissionCallbacks instanceof PermissionCallbacks)) {
                permissionCallbacks.onPermissionsGranted(requestCode, granted);
            }
            if (!denied.isEmpty() && (permissionCallbacks instanceof PermissionCallbacks)) {
                permissionCallbacks.onPermissionsDenied(requestCode, denied);
            }
            if (!granted.isEmpty() && denied.isEmpty()) {
                runAnnotatedMethods(permissionCallbacks, requestCode);
            }
        }
    }

    public static boolean somePermissionPermanentlyDenied(@NonNull Activity host, @NonNull List<String> deniedPermissions) {
        return PermissionHelper.newInstance(host).somePermissionPermanentlyDenied(deniedPermissions);
    }

    public static boolean somePermissionPermanentlyDenied(@NonNull Fragment host, @NonNull List<String> deniedPermissions) {
        return PermissionHelper.newInstance(host).somePermissionPermanentlyDenied(deniedPermissions);
    }

    public static boolean somePermissionPermanentlyDenied(@NonNull android.app.Fragment host, @NonNull List<String> deniedPermissions) {
        return PermissionHelper.newInstance(host).somePermissionPermanentlyDenied(deniedPermissions);
    }

    public static boolean permissionPermanentlyDenied(@NonNull Activity host, @NonNull String deniedPermission) {
        return PermissionHelper.newInstance(host).permissionPermanentlyDenied(deniedPermission);
    }

    public static boolean permissionPermanentlyDenied(@NonNull Fragment host, @NonNull String deniedPermission) {
        return PermissionHelper.newInstance(host).permissionPermanentlyDenied(deniedPermission);
    }

    public static boolean permissionPermanentlyDenied(@NonNull android.app.Fragment host, @NonNull String deniedPermission) {
        return PermissionHelper.newInstance(host).permissionPermanentlyDenied(deniedPermission);
    }

    public static boolean somePermissionDenied(@NonNull Activity host, @NonNull String... perms) {
        return PermissionHelper.newInstance(host).somePermissionDenied(perms);
    }

    public static boolean somePermissionDenied(@NonNull Fragment host, @NonNull String... perms) {
        return PermissionHelper.newInstance(host).somePermissionDenied(perms);
    }

    public static boolean somePermissionDenied(@NonNull android.app.Fragment host, @NonNull String... perms) {
        return PermissionHelper.newInstance(host).somePermissionDenied(perms);
    }

    private static void notifyAlreadyHasPermissions(@NonNull Object object, int requestCode, @NonNull String[] perms) {
        int[] grantResults = new int[perms.length];
        for (int i = 0; i < perms.length; i++) {
            grantResults[i] = 0;
        }
        onRequestPermissionsResult(requestCode, perms, grantResults, object);
    }

    private static void runAnnotatedMethods(@NonNull Object object, int requestCode) {
        Class clazz = object.getClass();
        if (isUsingAndroidAnnotations(object)) {
            clazz = clazz.getSuperclass();
        }
        while (clazz != null) {
            for (Method method : clazz.getDeclaredMethods()) {
                if (method.isAnnotationPresent(AfterPermissionGranted.class) && ((AfterPermissionGranted) method.getAnnotation(AfterPermissionGranted.class)).value() == requestCode) {
                    if (method.getParameterTypes().length > 0) {
                        throw new RuntimeException("Cannot execute method " + method.getName() + " because it is non-void method and/or has input parameters.");
                    }
                    try {
                        if (!method.isAccessible()) {
                            method.setAccessible(true);
                        }
                        method.invoke(object, new Object[0]);
                    } catch (IllegalAccessException e) {
                        Log.e(TAG, "runDefaultMethod:IllegalAccessException", e);
                    } catch (InvocationTargetException e2) {
                        Log.e(TAG, "runDefaultMethod:InvocationTargetException", e2);
                    }
                }
            }
            clazz = clazz.getSuperclass();
        }
    }

    private static boolean isUsingAndroidAnnotations(@NonNull Object object) {
        if (!object.getClass().getSimpleName().endsWith("_")) {
            return false;
        }
        try {
            return Class.forName("org.androidannotations.api.view.HasViews").isInstance(object);
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
}
