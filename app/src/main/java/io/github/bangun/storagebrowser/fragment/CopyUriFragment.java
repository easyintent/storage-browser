package io.github.bangun.storagebrowser.fragment;


import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.UiThread;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import io.github.bangun.storagebrowser.R;

@EFragment
public class CopyUriFragment extends DialogFragment {

    private static final Logger logger = LoggerFactory.getLogger(CopyUriFragment.class);

    private static final int MAX = 100;
    @FragmentArg protected Uri source;
    @FragmentArg protected Uri target;

    private boolean copying;
    private CommonOperationListener listener;

    public static CopyUriFragment newInstance(Uri source, Uri target) {
        CopyUriFragment fragment = new CopyUriFragmentEx();
        Bundle args = new Bundle();
        args.putParcelable("source", source);
        args.putParcelable("target", target);
        fragment.setArguments(args);
        return fragment;
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
        listener = (CommonOperationListener) getActivity();
        if (!copying) {
            copying = true;
            startCopy(getActivity().getContentResolver(), source, target);
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
    protected void startCopy(ContentResolver resolver, Uri src, Uri dst) {
        InputStream is = null;
        OutputStream os = null;
        try {
            is = resolver.openInputStream(src);
            os = resolver.openOutputStream(dst);
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
