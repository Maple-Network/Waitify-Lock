package ca.maplenetwork.waitifylock.Helpers;

import android.content.Context;
import android.provider.Settings;

public class PhoneHelper {
    public static String GetAndroidID(Context context) {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }
}
