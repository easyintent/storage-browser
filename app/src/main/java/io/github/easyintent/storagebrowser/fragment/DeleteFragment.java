package io.github.easyintent.storagebrowser.fragment;


import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.UiThread;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.easyintent.storagebrowser.R;
import io.github.easyintent.storagebrowser.data.Node;

@EFragment
public class DeleteFragment extends DialogFragment {

    private static final Logger logger = LoggerFactory.getLogger(DeleteFragment.class);

    private boolean deleting;
    private OperationDoneListener listener;

    private Node target;

    public static DeleteFragment newInstance() {
        return new DeleteFragmentEx();
    }

    public void setTarget(Node target) {
        this.target = target;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCancelable(false);
        setRetainInstance(true);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        listener = (OperationDoneListener) getActivity();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (target == null) {
            throw new IllegalStateException("Target not set");
        }
        if (!deleting) {
            deleting = true;
            startDelete(target);
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final View view = getActivity().getLayoutInflater().inflate(R.layout.fragment_indeterminate, null);
        final Context context = getActivity();
        return new AlertDialog.Builder(context)
                .setTitle(R.string.lbl_deleting)
                .setView(view)
                .create();
    }

    @Background
    protected void startDelete(Node target) {
        onDeleteDone(target.delete());
    }

    @UiThread
    protected void onDeleteDone(boolean success) {
        if (!isAdded()) {
            return;
        }
        Toast.makeText(getActivity(),
                success ? R.string.msg_delete_ok : R.string.msg_delete_failed,
                Toast.LENGTH_SHORT).show();
        listener.onOperationDone(this);
        dismiss();
    }

}
