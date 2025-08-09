package pub.devrel.easypermissions;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v7.app.AlertDialog;

class RationaleDialogConfig {
    private static final String KEY_NEGATIVE_BUTTON = "negativeButton";
    private static final String KEY_PERMISSIONS = "permissions";
    private static final String KEY_POSITIVE_BUTTON = "positiveButton";
    private static final String KEY_RATIONALE_MESSAGE = "rationaleMsg";
    private static final String KEY_REQUEST_CODE = "requestCode";
    int negativeButton;
    String[] permissions;
    int positiveButton;
    String rationaleMsg;
    int requestCode;

    RationaleDialogConfig(@StringRes int positiveButton2, @StringRes int negativeButton2, @NonNull String rationaleMsg2, int requestCode2, @NonNull String[] permissions2) {
        this.positiveButton = positiveButton2;
        this.negativeButton = negativeButton2;
        this.rationaleMsg = rationaleMsg2;
        this.requestCode = requestCode2;
        this.permissions = permissions2;
    }

    RationaleDialogConfig(Bundle bundle) {
        this.positiveButton = bundle.getInt(KEY_POSITIVE_BUTTON);
        this.negativeButton = bundle.getInt(KEY_NEGATIVE_BUTTON);
        this.rationaleMsg = bundle.getString(KEY_RATIONALE_MESSAGE);
        this.requestCode = bundle.getInt(KEY_REQUEST_CODE);
        this.permissions = bundle.getStringArray(KEY_PERMISSIONS);
    }

    /* access modifiers changed from: package-private */
    public Bundle toBundle() {
        Bundle bundle = new Bundle();
        bundle.putInt(KEY_POSITIVE_BUTTON, this.positiveButton);
        bundle.putInt(KEY_NEGATIVE_BUTTON, this.negativeButton);
        bundle.putString(KEY_RATIONALE_MESSAGE, this.rationaleMsg);
        bundle.putInt(KEY_REQUEST_CODE, this.requestCode);
        bundle.putStringArray(KEY_PERMISSIONS, this.permissions);
        return bundle;
    }

    /* access modifiers changed from: package-private */
    public AlertDialog createDialog(Context context, DialogInterface.OnClickListener listener) {
        return new AlertDialog.Builder(context).setCancelable(false).setPositiveButton(this.positiveButton, listener).setNegativeButton(this.negativeButton, listener).setMessage((CharSequence) this.rationaleMsg).create();
    }
}
