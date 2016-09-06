package io.github.easyintent.storagebrowser.fragment;


import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import io.github.easyintent.storagebrowser.R;
import io.github.easyintent.storagebrowser.data.Node;

public class ViewAsDialogFragment extends DialogFragment {

    private Node target;

    public static ViewAsDialogFragment newInstance() {
        return new ViewAsDialogFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    public void setTarget(Node target) {
        this.target = target;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        if (target == null) {
            throw new IllegalStateException("Target not set");
        }
        AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setTitle(R.string.lbl_view_as)
                .setItems(getResources().getStringArray(R.array.open_as),
                        new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String[] types = getResources().getStringArray(R.array.open_as_type);
                        String type = types[i];
                        openFile(target, type);
                    }
                })
                .create();
        return dialog;
    }

    private void openFile(Node target, String type) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(target.getUri(), type);
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION|Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(getActivity(), R.string.msg_no_app, Toast.LENGTH_SHORT).show();
        }
    }
}
