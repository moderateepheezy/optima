package ru.max64.myappstime.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class InstalledDialogFragment extends DialogFragment implements DialogInterface.OnClickListener {

    private ChooserFragmentInterface mInterface;
    private String title;
    private CharSequence[] options;
    private Object dataObject;

    public interface ChooserFragmentInterface {
        public void onChooserFragmentResult(int choice, Object dataObject);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mInterface = (ChooserFragmentInterface) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement ChooserFragmentInterface");
        }
    }

    public InstalledDialogFragment() {
    }

    public InstalledDialogFragment(String title, CharSequence[] options, Object dataObject) {
        this.title = title;
        this.options = options;
        this.dataObject = dataObject;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setRetainInstance(true);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(title);
        builder.setItems(options, this);
        builder.setCancelable(true);
        AlertDialog alert = builder.create();
        return alert;
    }

    @Override
    public void onClick(DialogInterface dialog, int choice) {
        mInterface.onChooserFragmentResult(choice, dataObject);
    }

    @Override
    public void onDestroyView() {
        // Work around bug:
        // http://code.google.com/p/android/issues/detail?id=17423
        Dialog dialog = getDialog();
        if ((dialog != null) && getRetainInstance()) {
            dialog.setDismissMessage(null);
        }
        super.onDestroyView();
    }

}
