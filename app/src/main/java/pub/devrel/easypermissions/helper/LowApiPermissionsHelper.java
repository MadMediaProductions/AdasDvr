package pub.devrel.easypermissions.helper;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;

class LowApiPermissionsHelper extends PermissionHelper<Object> {
    public LowApiPermissionsHelper(@NonNull Object host) {
        super(host);
    }

    public void directRequestPermissions(int requestCode, @NonNull String... perms) {
        throw new IllegalStateException("Should never be requesting permissions on API < 23!");
    }

    public boolean shouldShowRequestPermissionRationale(@NonNull String perm) {
        return false;
    }

    @SuppressLint({"NewApi"})
    public void showRequestPermissionRationale(@NonNull String rationale, int positiveButton, int negativeButton, int requestCode, @NonNull String... perms) {
        throw new IllegalStateException("Should never be requesting permissions on API < 23!");
    }

    public Context getContext() {
        return null;
    }
}
