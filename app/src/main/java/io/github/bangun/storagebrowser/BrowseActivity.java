package io.github.bangun.storagebrowser;

import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.provider.DocumentFile;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Stack;

import io.github.bangun.storagebrowser.data.DocumentFileTopLevelDir;
import io.github.bangun.storagebrowser.data.Node;
import io.github.bangun.storagebrowser.data.TopLevelDir;
import io.github.bangun.storagebrowser.data.repository.DefaultTopLevelRepository;
import io.github.bangun.storagebrowser.data.repository.TopLevelDirRepository;
import io.github.bangun.storagebrowser.fragment.BrowseFragment;
import io.github.bangun.storagebrowser.fragment.BrowseFragmentListener;
import io.github.bangun.storagebrowser.fragment.CommonOperationListener;

@EActivity
public class BrowseActivity extends AppCompatActivity
        implements CommonOperationListener, BrowseFragmentListener {

    private static final Logger logger = LoggerFactory.getLogger(BrowseActivity.class);
    private static final int PICK_ROOT_DOCUMENT = 0x00d0;

    @ViewById protected TextView pathView;

    private BrowseFragment browseFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse);

        initBrowseFragment();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.add_button);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addDocument();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            // do not care if cancelled
            return;
        }

        switch (requestCode) {
            case PICK_ROOT_DOCUMENT:
                documentPicked(data);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (!browseFragment.up()) {
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
        browseFragment.reload();
    }

    @Click(R.id.all_storage_view)
    protected void allStorageViewClicked() {
        browseFragment.showRoot();
    }

    private void documentPicked(Intent data) {
        Uri root = data.getData();

        logger.debug("Picked root uri: {}", root);

        ContentResolver resolver = getContentResolver();
        resolver.takePersistableUriPermission(root, Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

        DocumentFile document = DocumentFile.fromTreeUri(this, root);
        TopLevelDir topLevelDir = new DocumentFileTopLevelDir(document.getUri().toString());
        addRoot(this, topLevelDir);

        browseFragment.reload();
    }

    @Background
    protected void addRoot(Context context, TopLevelDir topLevelDir) {
        TopLevelDirRepository repo = new DefaultTopLevelRepository(context);
        repo.add(topLevelDir);
        onAddTopLevelDirFinished();
    }

    @UiThread
    protected void onAddTopLevelDirFinished() {
        browseFragment.reload();
    }

    private void initBrowseFragment() {
        FragmentManager manager = getFragmentManager();
        browseFragment = (BrowseFragment) manager.findFragmentByTag("browse_fragment");
        if (browseFragment != null) {
            return;
        }

        browseFragment = BrowseFragment.newInstance();
        manager.beginTransaction()
                .replace(R.id.content_view, browseFragment, "browse_fragment")
                .commit();
    }

    private void addDocument() {
        if (browseFragment.isRoot()) {
            addRootDocument();
        } else {
            browseFragment.createNewDir();
        }
    }

    private void addRootDocument() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
        startActivityForResult(intent, PICK_ROOT_DOCUMENT);
    }

}
