package io.github.bangun.storagebrowser;

import android.app.FragmentManager;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.provider.DocumentFile;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.widget.Toast;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.bangun.storagebrowser.data.RootNode;
import io.github.bangun.storagebrowser.fragment.BrowseFragment;

public class BrowseActivity extends AppCompatActivity {

    private static final Logger logger = LoggerFactory.getLogger(BrowseActivity.class);

    private static final int PICK_ROOT_DOCUMENT = 0x00d0;

    private Setting setting;
    private BrowseFragment browseFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setting = new Setting(this);
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

    private void documentPicked(Intent data) {
        Uri root = data.getData();

        logger.debug("Picked root uri: {}", root);

        ContentResolver resolver = getContentResolver();
        resolver.takePersistableUriPermission(root, Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

        DocumentFile document = DocumentFile.fromTreeUri(this, root);
        String name = document.getName();
        RootNode rootDocument = new RootNode(name, root);
        setting.addRoot(rootDocument);

        browseFragment.reload();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.fragment_browse, menu);
        return true;
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
            // add file or dir
            Toast.makeText(this, R.string.msg_can_not_create_dir, Toast.LENGTH_SHORT).show();
        }
    }

    private void addRootDocument() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
        startActivityForResult(intent, PICK_ROOT_DOCUMENT);
    }
}
