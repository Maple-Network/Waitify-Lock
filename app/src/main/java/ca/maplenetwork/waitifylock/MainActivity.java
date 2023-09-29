package ca.maplenetwork.waitifylock;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.switchmaterial.SwitchMaterial;

import ca.maplenetwork.waitifylock.Helpers.AppLockHelper;
import ca.maplenetwork.waitifylock.Helpers.NavigationHelper;
import ca.maplenetwork.waitifylock.Helpers.PermissionHelper;

public class MainActivity extends AppCompatActivity {
    private static Button allowAccessibilityButton;
    SwitchMaterial protectPermissionsSwitch;
    private static SwitchMaterial preventUninstallSwitch;
    private static SwitchMaterial appLockEnabledSwitch;

    @Override
    protected void onCreate(android.os.Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        allowAccessibilityButton = findViewById(R.id.allowAccessibilityButton);
        protectPermissionsSwitch = findViewById(R.id.protectPermissionsSwitch);
        preventUninstallSwitch = findViewById(R.id.preventUninstallSwitch);
        appLockEnabledSwitch = findViewById(R.id.appLockEnabledSwitch);

        loadAllOptions();

        Button setAppLockPinButton = findViewById(R.id.setAppLockPinButton);

        allowAccessibilityButton.setOnClickListener((view) -> {
            Intent intent = new Intent(android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        });

        protectPermissionsSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (AppLocked()) {
                return;
            }

            Variables.ProtectPermissions(this, isChecked);
        });

        preventUninstallSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (AppLocked()) {
                return;
            }

            DevicePolicyManager mDPM = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
            ComponentName mAdminName = new ComponentName(this, DeviceAdminSample.class);
            if (isChecked) {
                if (!mDPM.isAdminActive(mAdminName)) {
                    Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
                    intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, mAdminName);
                    intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, getString(R.string.device_admin_description));
                    deviceAdminResultLauncher.launch(intent);
                }
            } else if (mDPM.isAdminActive(mAdminName)) {
                mDPM.removeActiveAdmin(mAdminName);
            }
        });

        appLockEnabledSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (AppLocked()) {
                return;
            }

            Variables.AppLockEnabled(this, isChecked);
            Variables.AppLocked(this, false);
        });

        setAppLockPinButton.setOnClickListener(v -> {
            if (AppLocked()) {
                return;
            }

            // Inflate the custom layout
            LayoutInflater inflater = getLayoutInflater();
            View view = inflater.inflate(R.layout.pin_dialog_layout, null);
            EditText pinEditText = view.findViewById(R.id.pinEditText);
            EditText confirmPinEditText = view.findViewById(R.id.confirmPinEditText);
            MaterialCheckBox showPinCheckBox = view.findViewById(R.id.showPinCheckBox);

            AlertDialog alertDialog = new AlertDialog.Builder(this)

                    .setTitle("Set PIN")
                    .setView(view)
                    .setPositiveButton("Confirm", null)
                    .setNegativeButton("Cancel", null)
                    .create();

            alertDialog.setOnShowListener(dialog -> {
                Button positiveButton = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                positiveButton.setOnClickListener(view1 -> {
                    String pin = pinEditText.getText().toString();
                    String confirmPin = confirmPinEditText.getText().toString();

                    if (pin.isEmpty()) {
                        Toast.makeText(getApplicationContext(), "PINs can't be empty!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (!pin.equals(confirmPin)) {
                        Toast.makeText(getApplicationContext(), "PINs don't match!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Variables.AppLockPin(this, pin);
                    dialog.dismiss();
                });
            });

            showPinCheckBox.addOnCheckedStateChangedListener((checkBox, isChecked) -> {
                if (isChecked == MaterialCheckBox.STATE_CHECKED) {
                    pinEditText.setTransformationMethod(null);
                    confirmPinEditText.setTransformationMethod(null);
                    return;
                }

                pinEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
                confirmPinEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
            });

            alertDialog.show();
        });
    }

    private boolean AppLocked() {
        return Variables.AppLocked(this) && Variables.AppLockEnabled(this);
    }

    private void loadAllOptions() {
        refreshAccessibilityButton(this);
        boolean isAdminActive = ((DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE)).isAdminActive(new ComponentName(this, DeviceAdminSample.class));
        preventUninstallSwitch.setChecked(isAdminActive);
        protectPermissionsSwitch.setChecked(Variables.ProtectPermissions(this));
        appLockEnabledSwitch.setChecked(Variables.AppLockEnabled(this));
    }

    public static void refreshAccessibilityButton(Context context) {
        if (allowAccessibilityButton == null) {
            return;
        }

        if (PermissionHelper.IsAccessibilityServiceGranted(context)) {
            allowAccessibilityButton.setEnabled(false);
        } else {
            allowAccessibilityButton.setEnabled(true);
        }
    }

    private final ActivityResultLauncher<Intent> deviceAdminResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() != RESULT_OK) {
                    preventUninstallSwitch.setChecked(false);
                }
            }
    );

    public static void disablePin(Context context) {
        if (appLockEnabledSwitch == null) {
            Variables.AppLockEnabled(context, false);
            return;
        }
        appLockEnabledSwitch.setChecked(false);
    }

    public static void disableAdmin(Context context) {
        if (preventUninstallSwitch == null) {
            DevicePolicyManager mDPM = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
            ComponentName mAdminName = new ComponentName(context, DeviceAdminSample.class);
            mDPM.removeActiveAdmin(mAdminName);
            return;
        }
        preventUninstallSwitch.setChecked(false);
    }

    @Override
    protected void onPause() {
        Variables.AppLocked(this, true);
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            AppLockHelper.showAppLock(this, getSupportFragmentManager());
        } catch (Exception e) {
            NavigationHelper.OpenHomeScreen(this);
        }
    }
}
