package io.github.bangun.storagebrowser.fragment;


import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Toast;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.UiThread;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.bangun.storagebrowser.R;
import io.github.bangun.storagebrowser.data.Node;

@EFragment
public class DeleteFileFragment extends DialogFragment {

    private static final Logger logger = LoggerFactory.getLogger(DeleteFileFragment.class);
    private OperationDoneListener listener;

    private Node node;

    public static DeleteFileFragment newInstance() {
        DeleteFileFragment fragment = new DeleteFileFragmentEx();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCancelable(false);
        setRetainInstance(true);
    }

    public void setTargetFile(Node node) {
        this.node = node;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        listener = (OperationDoneListener) getActivity();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Context context = getActivity();
        return new AlertDialog.Builder(context)
                .setTitle(R.string.lbl_confirm)
                .setMessage(R.string.msg_confirm_delete)
                .setPositiveButton(R.string.lbl_delete, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (node == null) {
                            Toast.makeText(getActivity(), R.string.msg_file_not_available, Toast.LENGTH_SHORT).show();
                            return;
                        }
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

    @Background
    protected void delete(Node node) {
        onDeleteDone(node.delete());
    }

    @UiThread
    protected void onDeleteDone(boolean success) {
        Context context = getActivity();
        if (!isAdded() || context == null) {
            return;
        }
        Toast.makeText(context,
                success ? R.string.msg_delete_ok : R.string.msg_delete_failed,
                Toast.LENGTH_SHORT).show();
        listener.onOperationDone(this);
        dismiss();
    }

}
