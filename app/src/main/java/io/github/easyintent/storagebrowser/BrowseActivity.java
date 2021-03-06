package io.github.easyintent.storagebrowser;

import android.app.DialogFragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.util.Stack;

import io.github.easyintent.storagebrowser.data.Node;
import io.github.easyintent.storagebrowser.fragment.BrowseFragment;
import io.github.easyintent.storagebrowser.fragment.BrowseFragmentListener;
import io.github.easyintent.storagebrowser.fragment.OperationDoneListener;
import io.github.easyintent.storagebrowser.fragment.TopLevelStorageFragment;

@EActivity
public class BrowseActivity extends AppCompatActivity
        implements OperationDoneListener, BrowseFragmentListener {

    @ViewById protected TextView pathView;

    private TopLevelStorageFragment topLevelStorageFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse);
        initFragment();
    }

    @Override
    public void onBackPressed() {
        BrowseFragment browseFragment = getBrowseFragment();
        if (browseFragment != null) {
            browseFragment.up();
        } else {
            finish();
        }
    }

    @Override
    public void onLocationChanged(Stack<Node> nodes) {
        if (pathView == null) {
            return;
        }
        // should use clickable button
        StringBuilder sb = new StringBuilder();
        for (Node node : nodes) {
            sb.append(node.getName()).append(" / ");
        }
        pathView.setText(sb.length() != 0 ? sb : getString(R.string.lbl_all_storage));
    }

    @Override
    public void onOperationDone(DialogFragment fragment) {
        BrowseFragment browseFragment = getBrowseFragment();
        if (browseFragment != null) {
            browseFragment.reload();
        }
    }

    private BrowseFragment getBrowseFragment() {
        FragmentManager manager = getFragmentManager();
        return (BrowseFragment) manager.findFragmentByTag(BrowseFragment.TAG);
    }

    private void initFragment() {
        FragmentManager manager = getFragmentManager();
        topLevelStorageFragment = (TopLevelStorageFragment) manager.findFragmentByTag(TopLevelStorageFragment.TAG);
        if (topLevelStorageFragment == null) {
            initTopLevelFragment();
        }
    }

    private void initTopLevelFragment() {
        FragmentManager manager = getFragmentManager();
        topLevelStorageFragment = TopLevelStorageFragment.newInstance();
        manager.beginTransaction()
                .replace(R.id.content_view, topLevelStorageFragment, TopLevelStorageFragment.TAG)
                .commit();
    }

}
