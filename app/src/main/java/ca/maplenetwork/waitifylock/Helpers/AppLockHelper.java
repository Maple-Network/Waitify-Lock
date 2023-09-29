package ca.maplenetwork.waitifylock.Helpers;

import android.content.Context;
import android.util.Log;

import androidx.fragment.app.FragmentManager;

import ca.maplenetwork.waitifylock.PinDialogFragment;
import ca.maplenetwork.waitifylock.Variables;

public class AppLockHelper {
    private static final String TAG = "AppLockHelper";
    private static PinDialogFragment pinDialog;

    public static void showAppLock(Context context, FragmentManager supportFragmentManager) {

            if (!Variables.AppLocked(context) || !Variables.AppLockEnabled(context)) {
                return;
            }
            if (pinDialog != null) {
                pinDialog.dismiss();
            }

            pinDialog = new PinDialogFragment();
            pinDialog.setCancelable(false);
            pinDialog.show(supportFragmentManager, "pinDialog");
    }
}
