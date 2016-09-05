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
import org.androidannotations.annotations.UiThread;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.easyintent.storagebrowser.R;
import io.github.easyintent.storagebrowser.data.Node;

@EFragment
public class NewDirFragment extends DialogFragment {

    private static final Logger logger = LoggerFactory.getLogger(NewDirFragment.class);
    private OperationDoneListener listener;

    private Node currentDir;

    public static NewDirFragment newInstance() {
        NewDirFragment fragment = new NewDirFragmentEx();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCancelable(false);
        setRetainInstance(true);
    }

    public void setCurrentDir(Node node) {
        this.currentDir = node;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        listener = (OperationDoneListener) getActivity();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        if (currentDir == null) {
            throw new IllegalStateException("Current directory is not set");
        }
        final View view = getActivity().getLayoutInflater().inflate(R.layout.fragment_new_dir, null);
        final EditText editText = (EditText) view.findViewById(R.id.name_view);
        final Context context = getActivity();
        return new AlertDialog.Builder(context)
                .setTitle(R.string.lbl_new_dir)
                .setPositiveButton(R.string.lbl_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String name = editText.getText().toString();
                        if (name.length() == 0) {
                            Toast.makeText(getActivity(), R.string.msg_no_name, Toast.LENGTH_SHORT).show();
                            return;
                        }
                        create(currentDir, name);
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
    protected void create(Node node, String name) {
        Node newDir = node.newDir(name);
        onCreateDirDone(newDir != null);
    }

    @UiThread
    protected void onCreateDirDone(boolean success) {
        Toast.makeText(getActivity(),
                success ? R.string.msg_create_dir_ok : R.string.msg_create_dir_failed,
                Toast.LENGTH_SHORT).show();
        listener.onOperationDone(this);
        dismiss();
    }

}
