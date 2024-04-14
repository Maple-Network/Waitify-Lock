package ca.maplenetwork.waitifylock;

import android.app.Dialog;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.checkbox.MaterialCheckBox;

import ca.maplenetwork.waitifylock.helpers.DownloadHelper;

public class PinDialogFragment extends DialogFragment {
    private EditText pinEditText;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.dialog_parental_unlock, null);
        pinEditText = view.findViewById(R.id.pinEditText);
        MaterialCheckBox showPinCheckBox = view.findViewById(R.id.showPinCheckBox);

        builder.setView(view)
            .setPositiveButton("OK", null);

        AlertDialog dialog = builder.create();

        dialog.setOnShowListener(dialogInterface -> {
            Button button = ((AlertDialog) dialogInterface).getButton(AlertDialog.BUTTON_POSITIVE);
            button.setOnClickListener(view1 -> {
                if (!checkPin()) {
                    Toast.makeText(getContext(), "Invalid PIN", Toast.LENGTH_SHORT).show();
                    pinEditText.setText("");
                    return;
                }
                Variables.AppLocked(getContext(), false);
                DownloadHelper.INSTANCE.downloadAndPromptInstall(requireContext());
                dismiss();
            });
        });

        showPinCheckBox.addOnCheckedStateChangedListener((checkBox, isChecked) -> {
            if (isChecked == MaterialCheckBox.STATE_CHECKED) {
                pinEditText.setTransformationMethod(null);
                return;
            }

            pinEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
        });

        return dialog;
    }

    private boolean checkPin() {
        String enteredPin = pinEditText.getText().toString();
        String savedPin = Variables.AppLockPin(getContext());
        if (savedPin.equals("")) {
            Variables.AppLockEnabled(requireContext(), false);
            return true;
        }

        return savedPin.equals(enteredPin);
    }
}