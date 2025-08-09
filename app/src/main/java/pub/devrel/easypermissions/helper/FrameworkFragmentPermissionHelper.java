package pub.devrel.easypermissions.helper;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;

class FrameworkFragmentPermissionHelper extends BaseFrameworkPermissionsHelper<Fragment> {
    public FrameworkFragmentPermissionHelper(@NonNull Fragment host) {
        super(host);
    }

    @RequiresApi(api = 17)
    public FragmentManager getFragmentManager() {
        return ((Fragment) getHost()).getChildFragmentManager();
    }

    @SuppressLint({"NewApi"})
    public void directRequestPermissions(int requestCode, @NonNull String... perms) {
        ((Fragment) getHost()).requestPermissions(perms, requestCode);
    }

    @SuppressLint({"NewApi"})
    public boolean shouldShowRequestPermissionRationale(@NonNull String perm) {
        return ((Fragment) getHost()).shouldShowRequestPermissionRationale(perm);
    }

    @SuppressLint({"NewApi"})
    public Context getContext() {
        return ((Fragment) getHost()).getActivity();
    }
}
