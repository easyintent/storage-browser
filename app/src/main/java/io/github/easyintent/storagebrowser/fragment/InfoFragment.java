package io.github.easyintent.storagebrowser.fragment;


import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import io.github.easyintent.storagebrowser.R;
import io.github.easyintent.storagebrowser.data.Node;

public class InfoFragment extends DialogFragment {

    private static final long ONE_KB = 1024;
    private static final long ONE_MB = ONE_KB * ONE_KB;

    private Node target;

    public static InfoFragment newInstance() {
        InfoFragment fragment = new InfoFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    public void setTarget(Node node) {
        this.target = node;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final View view = getActivity().getLayoutInflater().inflate(R.layout.fragment_info, null);

        if (target == null) {
            throw new IllegalStateException("Target file is not set yet");
        }

        final EditText nameView = (EditText) view.findViewById(R.id.name_view);
        final EditText locationView = (EditText) view.findViewById(R.id.location_view);
        final EditText sizeView = (EditText) view.findViewById(R.id.size_view);
        final EditText typeView = (EditText) view.findViewById(R.id.type_view);

        nameView.setText(target.getName());
        locationView.setText(target.getUri().toString());
        sizeView.setText(getPrintableSize(target.size()));
        typeView.setText(target.getType());

        final Context context = getActivity();
        return new AlertDialog.Builder(context)
                .setTitle(R.string.lbl_info)
                .setPositiveButton(R.string.lbl_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                })
                .setView(view)
                .create();
    }


    private String getPrintableSize(long size) {
        long printSize = size;
        String suffix = "";
        Context context = getActivity();
        if (size < ONE_KB) {
            suffix = context.getString(R.string.lbl_bytes);
        } else if (size < ONE_MB) {
            suffix = context.getString(R.string.lbl_kb);
            printSize = size / ONE_KB;
        } else {
            suffix = context.getString(R.string.lbl_mb);
            printSize = size / ONE_MB;
        }
        return String.format("%,d %s", printSize, suffix);
    }

}
