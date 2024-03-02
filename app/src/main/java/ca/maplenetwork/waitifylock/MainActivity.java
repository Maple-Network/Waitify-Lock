package ca.maplenetwork.waitifylock;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
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

import ca.maplenetwork.waitifylock.Helpers.AppLockHelper;
import ca.maplenetwork.waitifylock.Helpers.NavigationHelper;
import ca.maplenetwork.waitifylock.Helpers.PermissionHelper;
import ca.maplenetwork.waitifylock.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(android.os.Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        loadAllOptions();

        setListeners();
    }

    private void setListeners() {
        binding.allowAccessibilityButton.setOnClickListener((view) -> {
            NavigationHelper.INSTANCE.openAccessibilitySettings(this);
        });

        binding.usageAccessButton.setOnClickListener((view) -> {
            NavigationHelper.INSTANCE.openUsageAccessSettings(this);
        });

        binding.protectPermissionsSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (AppLocked()) {
                return;
            }

            Variables.ProtectPermissions(this, isChecked);
        });

        binding.preventUninstallSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
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

        binding.appLockEnabledSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (AppLocked()) {
                return;
            }

            Variables.AppLockEnabled(this, isChecked);
            Variables.AppLocked(this, false);
        });

        binding.setAppLockPinButton.setOnClickListener(v -> {
            if (AppLocked()) {
                return;
            }

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
        boolean isAdminActive = ((DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE)).isAdminActive(new ComponentName(this, DeviceAdminSample.class));

        binding.allowAccessibilityButton.setEnabled(!PermissionHelper.INSTANCE.isAccessibilityServiceGranted(this));
        binding.usageAccessButton.setEnabled(!PermissionHelper.INSTANCE.isUsageAccessGranted(this));

        binding.protectPermissionsSwitch.setChecked(Variables.ProtectPermissions(this));
        binding.preventUninstallSwitch.setChecked(isAdminActive);

        binding.appLockEnabledSwitch.setChecked(Variables.AppLockEnabled(this));
    }

    private final ActivityResultLauncher<Intent> deviceAdminResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() != RESULT_OK) {
                    binding.preventUninstallSwitch.setChecked(false);
                }
            }
    );

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
            NavigationHelper.INSTANCE.openHomeScreen(this);
        }
        loadAllOptions();
    }
}
