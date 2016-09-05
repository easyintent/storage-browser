package io.github.easyintent.storagebrowser.fragment;


import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import org.androidannotations.annotations.EFragment;

import io.github.easyintent.storagebrowser.R;
import io.github.easyintent.storagebrowser.data.Node;

@EFragment
public class ConfirmDeleteFileFragment extends DialogFragment {

    private Node node;

    public static ConfirmDeleteFileFragment newInstance() {
        ConfirmDeleteFileFragment fragment = new ConfirmDeleteFileFragmentEx();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    public void setTargetFile(Node node) {
        this.node = node;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Context context = getActivity();
        if (node == null) {
            throw new IllegalStateException("Target file not set");
        }
        return new AlertDialog.Builder(context)
                .setTitle(R.string.lbl_confirm)
                .setMessage(R.string.msg_confirm_delete)
                .setPositiveButton(R.string.lbl_delete, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        delete(node);
                    }
                })
                .setNegativeButton(R.string.lbl_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .create();
    }

    private void delete(Node node) {
        DeleteFragment deleteFragment = DeleteFragment.newInstance();
        deleteFragment.setTarget(node);
        deleteFragment.show(getFragmentManager(), "delete_file_fragment");
    }

}
