package io.github.bangun.storagebrowser.fragment;

import android.app.Activity;
import android.app.ListFragment;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.provider.DocumentFile;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.UiThread;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import io.github.bangun.storagebrowser.R;
import io.github.bangun.storagebrowser.data.Node;
import io.github.bangun.storagebrowser.data.TopLevelDir;
import io.github.bangun.storagebrowser.data.TopLevelNode;
import io.github.bangun.storagebrowser.data.repository.DefaultTopLevelRepository;
import io.github.bangun.storagebrowser.data.repository.TopLevelDirRepository;

@EFragment
@OptionsMenu(R.menu.fragment_browse)
public class BrowseFragment extends ListFragment
        implements NodeActionListener {

    private static final Logger logger = LoggerFactory.getLogger(BrowseFragment.class);
    private static final int COPY_FROM = 0x10c0;

    private Node current;

    public static BrowseFragment newInstance() {
        return new BrowseFragmentEx();
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        switch (requestCode) {
            case COPY_FROM:
                copyFrom(data);
                break;
        }
    }

    @Override
    public void onView(Node node) {
        showItem(node);
    }

    @Override
    public void onDelete(Node node) {
        DeleteFileFragment deleteFileFragment = DeleteFileFragment.newInstance();
        deleteFileFragment.setTargetFile(node);
        deleteFileFragment.show(getFragmentManager(), "confirm_delete");
    }

    @Override
    public void onRename(Node node) {

    }

    @Override
    public void onRemoveFromList(Node node) {
        removeTopLevelFromList(getActivity(), (TopLevelNode) node);
    }

    @Background
    protected void removeTopLevelFromList(Context context, TopLevelNode node) {
        TopLevelDirRepository repo = new DefaultTopLevelRepository(context);
        repo.remove(node.getTopLevelDir());
        removeTopLevelDone();
    }

    @UiThread
    protected void removeTopLevelDone() {
        reload();
    }

    private void copyFrom(Intent data) {

        Uri uri = data.getData();
        DocumentFile src = DocumentFile.fromSingleUri(getActivity(), uri);
        Node target = current.newFile(src.getName(), src.getType());

        if (target == null) {
            // can not create new file
            Toast.makeText(getActivity(), R.string.msg_copy_failed, Toast.LENGTH_SHORT).show();
            return;
        }

        CopyUriFragment copyUriFragment = CopyUriFragment.newInstance(src.getUri(), target.getUri());
        copyUriFragment.show(getFragmentManager(), "copy_fragment");
    }

    public boolean isRoot() {
        return current == null;
    }

    public boolean up() {
        if (current == null) {
            return false;
        }

        // reload root
        if (!current.hasParent()) {
            current = null;
            reload();
            return true;
        }

        getChildrenList(current.getParent());
        return true;
    }

    public void reload() {

        setListShown(false);

        if (current == null) {
            getTopLevelList(getActivity());
        } else {
            getChildrenList(current);
        }
    }

    public void createNewDir() {
        NewDirFragment newDirFragment = NewDirFragment.newInstance();
        newDirFragment.setCurrentDir(current);
        newDirFragment.show(getFragmentManager(), "new_directory");
    }

    @OptionsItem(R.id.refresh)
    protected void refreshClicked() {
        reload();
    }

    @OptionsItem(R.id.copy_from)
    protected void copyFromClicked() {

        if (current == null) {
            Toast.makeText(getActivity(), R.string.msg_not_applicable_here, Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, COPY_FROM);
    }

    @Background
    protected void getChildrenList(Node path) {

        logger.debug("Loading path: {}", path.getUri());
        List<Node> children = path.list();

        onGetChildrenListDone(path, children);
    }

    @UiThread
    protected void onGetChildrenListDone(Node path, List<Node> children) {
        this.current = path;
        if (!isAdded()) {
            return;
        }
        showList(children);
    }

    @Background
    protected void getTopLevelList(Context context) {
        TopLevelDirRepository repo = new DefaultTopLevelRepository(context);
        List<TopLevelDir> rootDocuments = repo.listAll();
        List<Node> topLevels = new ArrayList<>();
        for (TopLevelDir root : rootDocuments) {
            topLevels.add(root.createNode(context));
        }
        onGetChildrenListDone(null, topLevels);
    }

    private void showItem(Node item) {
        if (item.isDirectory()) {
            setListShown(false);
            getChildrenList(item);
        } else {
            openFile(item);
        }
    }

    private void openFile(Node item) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(item.getUri(), item.getType());
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION|Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(getActivity(), R.string.msg_no_app, Toast.LENGTH_SHORT).show();
        }
    }

    private void showList(List<? extends Node> children) {
        final NodeListAdapter adapter = new NodeListAdapter(getActivity(), children, this);
        setListAdapter(adapter);

        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Node item = (Node) adapterView.getItemAtPosition(i);
                if (item != null) {
                    showItem(item);
                }
            }
        });

        if (children.isEmpty()) {
            int msg = current == null ? R.string.msg_add_root : R.string.lbl_no_data;
            setEmptyText(getString(msg));
        }

        setListShown(true);
    }


}
