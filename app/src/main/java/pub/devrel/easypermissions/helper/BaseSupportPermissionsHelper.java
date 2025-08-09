package pub.devrel.easypermissions.helper;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import pub.devrel.easypermissions.RationaleDialogFragmentCompat;

public abstract class BaseSupportPermissionsHelper<T> extends PermissionHelper<T> {
    public abstract FragmentManager getSupportFragmentManager();

    public BaseSupportPermissionsHelper(@NonNull T host) {
        super(host);
    }

    @SuppressLint({"NewApi"})
    public void showRequestPermissionRationale(@NonNull String rationale, int positiveButton, int negativeButton, int requestCode, @NonNull String... perms) {
        RationaleDialogFragmentCompat.newInstance(positiveButton, negativeButton, rationale, requestCode, perms).show(getSupportFragmentManager(), RationaleDialogFragmentCompat.TAG);
    }
}
