package ca.maplenetwork.waitifylock;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;

import java.util.EnumMap;

import ca.maplenetwork.waitifylock.helpers.PhoneHelper;

public class Variables {
    private static final EnumMap<PreferenceCategory, SharedPreferences> preferencesMap = new EnumMap<>(PreferenceCategory.class);

    public static String getMasterKeyAlias(Context context) {
        String cachedAlias = MasterKeyAlias(context);
        if (cachedAlias != null) {
            return cachedAlias;
        }

        try {
            String masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC);
            MasterKeyAlias(context, masterKeyAlias);
            return masterKeyAlias;
        } catch (Exception e) {
            Log.e("MasterKeyManager", "Error getting master key alias", e);

            String masterKeyAlias = PhoneHelper.GetAndroidID(context);
            MasterKeyAlias(context, masterKeyAlias);
            return masterKeyAlias;
        }
    }

    private enum VariableType {
        STRING,
        BOOLEAN,
        INTEGER,
        LONG,
        FLOAT;
    }

    enum PreferenceTypes {
        UNENCRYPTED,
        ENCRYPTED;
    }

    enum PreferenceCategory {
        APP(PreferenceTypes.UNENCRYPTED, "AppPreferences"),
        ENCRYPTED(PreferenceTypes.ENCRYPTED, "EncryptedPreferences");
        private final PreferenceTypes type;
        private final String preferenceName;
        PreferenceCategory(PreferenceTypes type, String preferenceName) {
            this.type = type;
            this.preferenceName = preferenceName;
        }
        public PreferenceTypes getEncryptionType() {
            return type;
        }
        public String getPreferenceName() {
            return preferenceName;
        }
    }
    private static SharedPreferences preferences(Context context, PreferenceCategory preferenceCategory) {
        if (!preferencesMap.containsKey(preferenceCategory)) {
            Context appContext = context.getApplicationContext();

            SharedPreferences sharedPreferences;

            if (preferenceCategory.getEncryptionType() == PreferenceTypes.UNENCRYPTED) {
                sharedPreferences = appContext.getSharedPreferences(preferenceCategory.getPreferenceName(), Context.MODE_PRIVATE);
            } else {
                try {
                    sharedPreferences = EncryptedSharedPreferences.create(
                            preferenceCategory.getPreferenceName(),
                            getMasterKeyAlias(appContext),
                            appContext,
                            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
                    );
                } catch (Exception e) {
                    Log.e("MasterKeyManager", "Error getting master key alias", e);

                    sharedPreferences = appContext.getSharedPreferences(preferenceCategory.getPreferenceName(), Context.MODE_PRIVATE);
                }
            }

            preferencesMap.put(preferenceCategory, sharedPreferences);
        }
        return preferencesMap.get(preferenceCategory);
    }

    private static void setValue(Context context, String key, Object value, VariableType valueType, PreferenceCategory preferenceCategory) {
        SharedPreferences.Editor editor = preferences(context, preferenceCategory).edit();
        switch (valueType) {
            case STRING:
                editor.putString(key, (String) value);
                break;
            case BOOLEAN:
                editor.putBoolean(key, (Boolean) value);
                break;
            case INTEGER:
                editor.putInt(key, (Integer) value);
                break;
            case LONG:
                editor.putLong(key, (Long) value);
                break;
            case FLOAT:
                editor.putFloat(key, (Float) value);
                break;
        }
        editor.apply();
    }

        //Encrypted Preferences
    //ParentalPin
    public static void AppLockPin(Context context, String SetValue) {
        setValue(context, "AppLockPin", SetValue, VariableType.STRING, PreferenceCategory.ENCRYPTED);
    }
    public static String AppLockPin(Context context) {
        return preferences(context, PreferenceCategory.ENCRYPTED).getString("AppLockPin", "");
    }

        //App Preferences
    //AppLockEnabled
    public static void AppLockEnabled(Context context, boolean SetValue) {
        setValue(context, "AppLockEnabled", SetValue, VariableType.BOOLEAN, PreferenceCategory.APP);
    }
    public static boolean AppLockEnabled(Context context) {
        return preferences(context, PreferenceCategory.APP).getBoolean("AppLockEnabled", false);
    }

    //AppLocked
    public static void AppLocked(Context context, boolean SetValue) {
        setValue(context, "AppLocked", SetValue, VariableType.BOOLEAN, PreferenceCategory.APP);
    }
    public static boolean AppLocked(Context context) {
        return preferences(context, PreferenceCategory.APP).getBoolean("AppLocked", false);
    }

    //MasterKeyAlias
    private static void MasterKeyAlias(Context context, String SetValue) {
        setValue(context, "MasterKeyAlias", SetValue, VariableType.STRING, PreferenceCategory.APP);
    }
    private static String MasterKeyAlias(Context context) {
        return preferences(context, PreferenceCategory.APP).getString("MasterKeyAlias", null);
    }

    //ProtectPermissions
    public static void ProtectPermissions(Context context, boolean SetValue) {
        setValue(context, "ProtectPermissions", SetValue, VariableType.BOOLEAN, PreferenceCategory.APP);
    }
    public static boolean ProtectPermissions(Context context) {
        return preferences(context, PreferenceCategory.APP).getBoolean("ProtectPermissions", false);
    }

    //lastAppVersion
    public static void lastAppVersion(Context context, int SetValue) {
        setValue(context, "lastAppVersion", SetValue, VariableType.INTEGER, PreferenceCategory.APP);
    }
    public static int lastAppVersion(Context context) {
        return preferences(context, PreferenceCategory.APP).getInt("lastAppVersion", 0);
    }
}
