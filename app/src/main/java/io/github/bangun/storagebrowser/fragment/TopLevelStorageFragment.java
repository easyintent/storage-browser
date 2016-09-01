package io.github.bangun.storagebrowser.fragment;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.ListFragment;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.provider.DocumentFile;
import android.view.View;
import android.widget.AdapterView;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.UiThread;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import io.github.bangun.storagebrowser.R;
import io.github.bangun.storagebrowser.data.DocumentFileTopLevelDir;
import io.github.bangun.storagebrowser.data.TopLevelDir;
import io.github.bangun.storagebrowser.data.repository.DefaultTopLevelRepository;
import io.github.bangun.storagebrowser.data.repository.TopLevelDirRepository;

@EFragment
@OptionsMenu(R.menu.fragment_top_level_storage)
public class TopLevelStorageFragment extends ListFragment
        implements TopLevelDirActionListener {

    public static final String TAG = "top_level_storage_fragment";
    private static final Logger logger = LoggerFactory.getLogger(TopLevelStorageFragment.class);
    private static final int PICK_ROOT_DOCUMENT = 0x00d0;

    private volatile boolean loading;

    public static TopLevelStorageFragment newInstance() {
        return new TopLevelStorageFragmentEx();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        reload();
    }

    public void reload() {
        if (loading) {
            return;
        }
        setListShown(false);
        getTopLevelList(getActivity());
    }

    @Override
    public void onRemoveFromList(TopLevelDir topLevelDir) {
        removeTopLevelFromList(getActivity(), topLevelDir);
    }

    @OptionsItem(R.id.add_saf)
    protected void addStorageAccessDocument() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
        startActivityForResult(intent, PICK_ROOT_DOCUMENT);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            // do not care if cancelled
            return;
        }

        switch (requestCode) {
            case PICK_ROOT_DOCUMENT:
                documentPicked(data);
                break;
        }
    }

    private void documentPicked(Intent data) {

        Context context = getActivity();
        Uri root = data.getData();

        logger.debug("Picked root uri: {}", root);

        ContentResolver resolver = context.getContentResolver();
        resolver.takePersistableUriPermission(root, Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

        DocumentFile document = DocumentFile.fromTreeUri(context, root);
        TopLevelDir topLevelDir = new DocumentFileTopLevelDir(document.getUri().toString());
        addRoot(context, topLevelDir);

        reload();
    }

    @Background
    protected void addRoot(Context context, TopLevelDir topLevelDir) {
        loading = true;
        TopLevelDirRepository repo = new DefaultTopLevelRepository(context);
        repo.add(topLevelDir);
        reloadLater();
        loading = false;
    }

    @Background
    protected void removeTopLevelFromList(Context context, TopLevelDir topLevelDir) {
        loading = true;
        TopLevelDirRepository repo = new DefaultTopLevelRepository(context);
        repo.remove(topLevelDir);
        reloadLater();
        loading = false;
    }

    @UiThread
    protected void reloadLater() {
        reload();
    }

    @OptionsItem(R.id.refresh)
    protected void refreshClicked() {
        reload();
    }

    @Background
    protected void getTopLevelList(Context context) {
        loading = true;
        TopLevelDirRepository repo = new DefaultTopLevelRepository(context);
        List<TopLevelDir> topLevelDirs = repo.listAll();
        onGetChildrenListDone(topLevelDirs);
        loading = false;
    }

    @UiThread
    protected void onGetChildrenListDone(List<TopLevelDir> topLevelDirs) {
        if (!isAdded()) {
            return;
        }
        showList(topLevelDirs);
    }

    private void onItemSelected(TopLevelDir topLevelDir) {
        FragmentManager manager = getFragmentManager();
        BrowseFragment browseFragment = BrowseFragment.newInstance();
        browseFragment.setCurrentDir(topLevelDir.createNode(getActivity()));
        manager.beginTransaction()
                .replace(R.id.content_view, browseFragment, BrowseFragment.TAG)
                .addToBackStack(null)
                .commit();
    }

    private void showList(List<TopLevelDir> topLevelDirs) {
        final TopLevelDirListAdapter adapter = new TopLevelDirListAdapter(getActivity(), topLevelDirs, this);
        setListAdapter(adapter);

        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                TopLevelDir item = (TopLevelDir) adapterView.getItemAtPosition(i);
                if (item != null) {
                    onItemSelected(item);
                }
            }
        });

        if (topLevelDirs.isEmpty()) {
            setEmptyText(getString(R.string.msg_add_root));
        }

        setListShown(true);
    }

}
