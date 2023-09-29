package ca.maplenetwork.waitifylock.Helpers;

import android.content.Context;
import android.content.Intent;
import android.provider.Settings;

import ca.maplenetwork.waitifylock.MainActivity;

public class NavigationHelper {
    public static void OpenMainMenu(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        context.startActivity(intent);
    }

    public static void OpenHomeScreen(Context context) {
        Intent homeIntent = new Intent(Intent.ACTION_MAIN);
        homeIntent.addCategory(Intent.CATEGORY_HOME);
        context.startActivity(homeIntent);
    }

    public static void OpenAndroidSettings(Context context) {
        Intent settingsIntent = new Intent(Settings.ACTION_SETTINGS);
        settingsIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        settingsIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(settingsIntent);
    }
}
