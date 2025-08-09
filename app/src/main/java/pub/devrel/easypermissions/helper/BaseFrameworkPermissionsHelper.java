package pub.devrel.easypermissions.helper;

import android.app.FragmentManager;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import pub.devrel.easypermissions.RationaleDialogFragment;

public abstract class BaseFrameworkPermissionsHelper<T> extends PermissionHelper<T> {
    public abstract FragmentManager getFragmentManager();

    public BaseFrameworkPermissionsHelper(@NonNull T host) {
        super(host);
    }

    @RequiresApi(api = 11)
    public void showRequestPermissionRationale(@NonNull String rationale, int positiveButton, int negativeButton, int requestCode, @NonNull String... perms) {
        RationaleDialogFragment.newInstance(positiveButton, negativeButton, rationale, requestCode, perms).show(getFragmentManager(), RationaleDialogFragment.TAG);
    }
}
