package io.github.easyintent.storagebrowser;

import android.app.FragmentManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import io.github.easyintent.storagebrowser.fragment.AddLocalStorageFragment;

public class AddLocalStorageActivity extends AppCompatActivity {

    private AddLocalStorageFragment addLocalStorageFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_local_storage);
        addFragment();
    }

    private void addFragment() {
        FragmentManager fm = getFragmentManager();
        addLocalStorageFragment = (AddLocalStorageFragment) fm.findFragmentByTag("add_local_storage");
        if (addLocalStorageFragment != null) {
            return;
        }

        addLocalStorageFragment = AddLocalStorageFragment.newInstance();
        fm.beginTransaction()
                .replace(R.id.content_view, addLocalStorageFragment, "add_local_storage")
                .commit();


    }
}
