package io.github.easyintent.storagebrowser.fragment;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.widget.EditText;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.io.File;

import io.github.easyintent.storagebrowser.R;

@EFragment(R.layout.fragment_add_local_storage)
public class AddLocalStorageFragment extends DialogFragment {

    @ViewById protected EditText nameView;
    @ViewById protected EditText subDirView;

    public static AddLocalStorageFragment newInstance() {
        return new AddLocalStorageFragmentEx();
    }

    @Click
    protected void saveButtonClicked() {
        String name = nameView.getText().toString();
        String subDir = subDirView.getText().toString();
        if (name.isEmpty()) {
            nameView.setError(getString(R.string.lbl_required));
            return;
        }

        File root = Environment.getExternalStorageDirectory();
        File target = new File(root, subDir);
        if (!(target.exists() && target.isDirectory())) {
            subDirView.setError(getString(R.string.msg_location_does_not_exist));
            return;
        }

        Intent intent = new Intent();
        intent.setData(Uri.fromFile(target));
        intent.putExtra(Intent.EXTRA_TITLE, name);
        getActivity().setResult(Activity.RESULT_OK, intent);
        getActivity().finish();
    }

}
