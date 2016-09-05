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
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import io.github.easyintent.storagebrowser.R;
import io.github.easyintent.storagebrowser.data.Node;

@EFragment
public class CopyFragment extends DialogFragment {

    private static final Logger logger = LoggerFactory.getLogger(CopyFragment.class);

    private boolean copying;
    private OperationDoneListener listener;

    private Node source;
    private Node target;

    public static CopyFragment newInstance() {
        CopyFragment fragment = new CopyFragmentEx();
        return fragment;
    }

    public void setFileToCopy(Node source, Node target) {
        this.source = source;
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
        if (source == null || target == null) {
            throw new IllegalStateException("Source and target not set");
        }
        if (!copying) {
            copying = true;
            startCopy(getActivity(), source, target);
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final View view = getActivity().getLayoutInflater().inflate(R.layout.fragment_copy, null);
        final Context context = getActivity();
        return new AlertDialog.Builder(context)
                .setTitle(R.string.lbl_copying)
                .setView(view)
                .create();
    }

    @Background
    protected void startCopy(Context context, Node src, Node dst) {
        InputStream is = null;
        OutputStream os = null;
        try {
            is = src.openForRead(context);
            os = dst.openForWrite(context);
            IOUtils.copy(is, os);
            onCopyDone(true, null);
        } catch (IOException e) {
            logger.debug("Failed to copy", e);
            onCopyDone(false, e.getMessage());
        } finally {
            IOUtils.closeQuietly(is);
            IOUtils.closeQuietly(os);
        }
    }

    @UiThread
    protected void onCopyDone(boolean success, String message) {

        if (!isAdded() || getActivity() == null) {
            // fragment not attached
            return;
        }

        if (message != null) {
            Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
        } else {
            finishNoMessage(success);
        }
        listener.onOperationDone(this);
        dismiss();
    }

    private void finishNoMessage(boolean success) {
        if (success) {
            Toast.makeText(getActivity(), R.string.msg_copy_done, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getActivity(), R.string.msg_copy_failed, Toast.LENGTH_SHORT).show();
        }
    }
}
