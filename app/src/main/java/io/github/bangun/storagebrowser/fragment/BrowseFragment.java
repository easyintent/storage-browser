package io.github.bangun.storagebrowser.fragment;

import android.app.Activity;
import android.app.ListFragment;
import android.content.ActivityNotFoundException;
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

import java.util.List;
import java.util.Stack;

import io.github.bangun.storagebrowser.R;
import io.github.bangun.storagebrowser.data.Node;

@EFragment
@OptionsMenu(R.menu.fragment_browse)
public class BrowseFragment extends ListFragment
        implements NodeActionListener {

    public static final String TAG = "browse_fragment";
    private static final Logger logger = LoggerFactory.getLogger(BrowseFragment.class);

    private static final int COPY_FROM = 0x10c0;
    private static final int COPY_TO   = 0x10c1;

    private Node currentDir;
    private Stack<Node> stack;
    private volatile boolean loading;

    // source file to copy
    private Node sourceFile;

    private BrowseFragmentListener browseFragmentListener;

    public static BrowseFragment newInstance() {
        return new BrowseFragmentEx();
    }

    public BrowseFragment() {
        stack = new Stack<>();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);
    }

    public void setCurrentDir(Node currentDir) {
        this.currentDir = currentDir;
        stack.clear();
        stack.push(currentDir);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        browseFragmentListener = (BrowseFragmentListener) getActivity();
        browseFragmentListener.onLocationChanged(stack);
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
            case COPY_TO:
                copyTo(data);
                break;
        }
    }

    @Override
    public void onView(Node node) {
        onItemSelected(node);
    }

    @Override
    public void onDelete(Node node) {
        DeleteFileFragment deleteFileFragment = DeleteFileFragment.newInstance();
        deleteFileFragment.setTargetFile(node);
        deleteFileFragment.show(getFragmentManager(), "confirm_delete");
    }

    @Override
    public void onRename(Node node) {
        RenameFileFragment renameFileFragment = RenameFileFragment.newInstance();
        renameFileFragment.setTargetFile(node);
        renameFileFragment.show(getFragmentManager(), "rename");
    }

    // copy to ..
    //
    @Override
    public void onCopy(Node node) {
        this.sourceFile = node;
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.putExtra(Intent.EXTRA_TITLE, node.getName());
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        startActivityForResult(intent, COPY_TO);
    }

    private void onDirectoryEnter(Node node) {
        stack.push(node);
        browseFragmentListener.onLocationChanged(stack);
    }

    private void onDirectoryLeave(Node node) {
        stack.pop();
        browseFragmentListener.onLocationChanged(stack);
    }

    private void copyFrom(Intent data) {

        Uri uri = data.getData();
        DocumentFile src = DocumentFile.fromSingleUri(getActivity(), uri);
        Node target = currentDir.newFile(src.getName(), src.getType());

        if (target == null) {
            // can not create new file
            Toast.makeText(getActivity(), R.string.msg_copy_failed, Toast.LENGTH_SHORT).show();
            return;
        }

        CopyUriFragment copyUriFragment = CopyUriFragment.newInstance(src.getUri(), target.getUri());
        copyUriFragment.show(getFragmentManager(), "copy_fragment");
    }


    private void copyTo(Intent data) {
        if (sourceFile == null) {
            // no source file
            Toast.makeText(getActivity(), R.string.msg_copy_failed, Toast.LENGTH_SHORT).show();
            return;
        }

        Uri from = sourceFile.getUri();
        sourceFile = null;  // remove cache

        Uri to = data.getData();
        CopyUriFragment copyUriFragment = CopyUriFragment.newInstance(from, to);
        copyUriFragment.show(getFragmentManager(), "copy_fragment");

    }

    public void up() {

        if (loading) {
            return;
        }

        if (!currentDir.hasParent()) {
            stack.clear();
            browseFragmentListener.onLocationChanged(stack);
            getFragmentManager().popBackStack();
            return;
        }

        onDirectoryLeave(currentDir);
        setListShown(false);
        getChildrenList(currentDir.getParent());
    }

    public void reload() {

        if (loading) {
            return;
        }

        setListShown(false);
        getChildrenList(currentDir);
    }

    public void createNewDir() {
        NewDirFragment newDirFragment = NewDirFragment.newInstance();
        newDirFragment.setCurrentDir(currentDir);
        newDirFragment.show(getFragmentManager(), "new_directory");
    }

    @OptionsItem(R.id.refresh)
    protected void refreshClicked() {
        reload();
    }

    @OptionsItem(R.id.create_dir)
    protected void createDirClicked() {
        createNewDir();
    }

    @OptionsItem(R.id.copy_from)
    protected void copyFromClicked() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, COPY_FROM);
    }

    @Background
    protected void getChildrenList(Node path) {
        loading = true;

        logger.debug("Loading path: {}", path.getUri());
        List<Node> children = path.list();

        onGetChildrenListDone(path, children);
        loading = false;
    }

    @UiThread
    protected void onGetChildrenListDone(Node path, List<Node> children) {
        this.currentDir = path;
        if (!isAdded()) {
            return;
        }
        showList(children);
    }

    private void onItemSelected(Node item) {
        if (item.isDirectory()) {
            setListShown(false);
            getChildrenList(item);
            onDirectoryEnter(item);
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
                    onItemSelected(item);
                }
            }
        });

        if (children.isEmpty()) {
            setEmptyText(getString(R.string.lbl_no_data));
        }

        setListShown(true);
    }

}
