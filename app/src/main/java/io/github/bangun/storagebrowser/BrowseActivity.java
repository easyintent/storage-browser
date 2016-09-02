package io.github.bangun.storagebrowser;

import android.app.DialogFragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Stack;

import io.github.bangun.storagebrowser.data.Node;
import io.github.bangun.storagebrowser.fragment.BrowseFragment;
import io.github.bangun.storagebrowser.fragment.BrowseFragmentListener;
import io.github.bangun.storagebrowser.fragment.OperationDoneListener;
import io.github.bangun.storagebrowser.fragment.TopLevelStorageFragment;

@EActivity
public class BrowseActivity extends AppCompatActivity
        implements OperationDoneListener, BrowseFragmentListener {

    private static final Logger logger = LoggerFactory.getLogger(BrowseActivity.class);

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
        pathView.setText(sb);
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
