package io.github.easyintent.storagebrowser.fragment;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.io.File;

import io.github.easyintent.storagebrowser.R;

@EFragment(R.layout.fragment_add_local_storage)
public class AddLocalStorageFragment extends DialogFragment {

    @ViewById protected EditText nameView;
    @ViewById protected EditText anyDirView;

    @ViewById protected RadioButton advancedView;
    @ViewById protected RadioButton sharedStorageView;
    @ViewById protected View advancedGroup;

    public static AddLocalStorageFragment newInstance() {
        return new AddLocalStorageFragmentEx();
    }

    @AfterViews
    protected void afterViews() {
        if (anyDirView.getText().length() == 0) {
            anyDirView.setText(Environment.getExternalStorageDirectory().getAbsolutePath());
        }
        advancedView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                advancedGroup.setVisibility(advancedView.isChecked() ? View.VISIBLE : View.GONE);
            }
        });
    }

    @Click
    protected void saveButtonClicked() {
        String name = nameView.getText().toString();
        String subDir = anyDirView.getText().toString();
        if (name.isEmpty()) {
            nameView.setError(getString(R.string.lbl_required));
            return;
        }

        if (sharedStorageView.isChecked()) {
            File root = Environment.getExternalStorageDirectory();
            useLocation(name, root);
        }

        if (advancedView.isChecked()) {
            addAnyPath(name, subDir);
        }
    }

    private void addAnyPath(String name, String subDir) {
        File target = new File(subDir);
        if ((target.exists() && target.isDirectory())) {
            useLocation(name, target);
            return;
        }
        anyDirView.setError(getString(R.string.msg_location_does_not_exist));
    }

    private void useLocation(String name, File target) {
        Intent intent = new Intent();
        intent.setData(Uri.fromFile(target));
        intent.putExtra(Intent.EXTRA_TITLE, name);
        getActivity().setResult(Activity.RESULT_OK, intent);
        getActivity().finish();
    }

}
