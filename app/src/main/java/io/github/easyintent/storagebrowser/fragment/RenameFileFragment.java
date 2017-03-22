package io.github.easyintent.storagebrowser.fragment;


import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.IgnoreWhen;
import org.androidannotations.annotations.UiThread;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.easyintent.storagebrowser.R;
import io.github.easyintent.storagebrowser.data.Node;

@EFragment
public class RenameFileFragment extends DialogFragment {

    private static final Logger logger = LoggerFactory.getLogger(RenameFileFragment.class);
    private OperationDoneListener listener;

    private Node target;

    public static RenameFileFragment newInstance() {
        RenameFileFragment fragment = new RenameFileFragmentEx();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCancelable(false);
        setRetainInstance(true);
    }

    public void setTargetFile(Node node) {
        this.target = node;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        listener = (OperationDoneListener) getActivity();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        if (target == null) {
            throw new IllegalStateException("Target file is not set");
        }

        final View view = getActivity().getLayoutInflater().inflate(R.layout.fragment_rename, null);
        final EditText editText = (EditText) view.findViewById(R.id.name_view);
        final Context context = getActivity();

        editText.setText(target.getName());

        return new AlertDialog.Builder(context)
                .setTitle(R.string.lbl_rename)
                .setPositiveButton(R.string.lbl_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String name = editText.getText().toString();
                        if (name.length() == 0) {
                            Toast.makeText(getActivity(), R.string.msg_no_name, Toast.LENGTH_SHORT).show();
                            return;
                        }
                        rename(target, name);
                    }
                })
                .setNegativeButton(R.string.lbl_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setView(view)
                .create();
    }


    @Background
    protected void rename(Node node, String newName) {
        onRenameDone(node.rename(newName));
    }

    @UiThread
    @IgnoreWhen(IgnoreWhen.State.DETACHED)
    protected void onRenameDone(boolean success) {
        Context context = getActivity();
        Toast.makeText(context,
                success ? R.string.msg_rename_ok : R.string.msg_rename_failed,
                Toast.LENGTH_SHORT).show();
        listener.onOperationDone(this);
        dismiss();
    }

}
