package io.github.bangun.storagebrowser.fragment;

import android.app.Activity;
import android.app.ListFragment;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.provider.DocumentFile;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Stack;

import io.github.bangun.storagebrowser.R;
import io.github.bangun.storagebrowser.data.DocumentFileNode;
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
    private ChildrenListLoader childrenListLoader;

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
                copyFromStorageAccess(data);
                break;
            case COPY_TO:
                copyToStorageAccess(data);
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

        try {
            startActivityForResult(intent, COPY_TO);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(getActivity(), R.string.msg_no_picker, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDetail(Node node) {
        InfoFragment fragment = InfoFragment.newInstance();
        fragment.setTarget(node);
        fragment.show(getFragmentManager(), "info_fragment");
    }

    @Override
    public void onShare(Node node) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_STREAM, node.getUri());
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(getActivity(), R.string.msg_no_app, Toast.LENGTH_SHORT).show();
        }
    }

    private void onDirectoryEnter(Node node) {
        stack.push(node);
        browseFragmentListener.onLocationChanged(stack);
    }

    private void onDirectoryLeave(Node node) {
        stack.pop();
        browseFragmentListener.onLocationChanged(stack);
    }

    private void copyFromStorageAccess(Intent data) {

        Uri uri = data.getData();
        DocumentFile srcDocument = DocumentFile.fromSingleUri(getActivity(), uri);
        Node target = currentDir.newFile(srcDocument.getName(), srcDocument.getType());

        if (target == null) {
            // can not create new file
            Toast.makeText(getActivity(), R.string.msg_copy_failed, Toast.LENGTH_SHORT).show();
            return;
        }

        DocumentFileNode src = new DocumentFileNode(null, srcDocument);
        CopyFragment copyFragment = CopyFragment.newInstance();
        copyFragment.setFileToCopy(src, target);
        copyFragment.show(getFragmentManager(), "copy_from_fragment");
    }

    private void copyToStorageAccess(Intent data) {
        if (sourceFile == null) {
            // no source file
            Toast.makeText(getActivity(), R.string.msg_copy_failed, Toast.LENGTH_SHORT).show();
            return;
        }

        Node src = sourceFile;
        sourceFile = null;  // remove cache

        DocumentFile dstDocument = DocumentFile.fromSingleUri(getActivity(), data.getData());
        DocumentFileNode dst = new DocumentFileNode(null, dstDocument);

        CopyFragment copyFragment = CopyFragment.newInstance();
        copyFragment.setFileToCopy(src, dst);
        copyFragment.show(getFragmentManager(), "copy_to_fragment");
    }

    public void up() {

        if (isLoading()) {
            Toast.makeText(getActivity(), R.string.msg_please_wait, Toast.LENGTH_SHORT).show();
            return;
        }

        if (!currentDir.hasParent()) {
            leave();
            return;
        }

        onDirectoryLeave(currentDir);
        setListShown(false);
        loadChildren(currentDir.getParent());
    }

    private void leave() {
        stack.clear();
        browseFragmentListener.onLocationChanged(stack);
        getFragmentManager().popBackStack();
    }

    @OptionsItem(R.id.refresh)
    public void reload() {

        if (isLoading()) {
            Toast.makeText(getActivity(), R.string.msg_please_wait, Toast.LENGTH_SHORT).show();
            return;
        }

        setListShown(false);
        loadChildren(currentDir);
    }

    public void createNewDir() {
        NewDirFragment newDirFragment = NewDirFragment.newInstance();
        newDirFragment.setCurrentDir(currentDir);
        newDirFragment.show(getFragmentManager(), "new_directory");
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

        try {
            startActivityForResult(intent, COPY_FROM);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(getActivity(), R.string.msg_no_picker, Toast.LENGTH_SHORT).show();
        }
    }

    @OptionsItem(R.id.goto_home)
    protected void gotoHomeClicked() {
        leave();
    }

    private synchronized void loadChildren(Node path) {
        childrenListLoader = new ChildrenListLoader(path);
        childrenListLoader.execute();
    }

    private synchronized boolean isLoading() {
        return childrenListLoader != null && childrenListLoader.isLoading();
    }

    private void onLoadChildrenListDone(Node path, List<Node> children) {
        this.currentDir = path;
        if (!isAdded()) {
            return;
        }
        showList(children);
    }

    private void onItemSelected(Node item) {
        if (item.isDirectory()) {
            openDir(item);
        } else {
            openFile(item);
        }
    }

    private void openDir(Node item) {
        setListShown(false);
        loadChildren(item);
        onDirectoryEnter(item);
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

    private class ChildrenListLoader extends AsyncTask<Object,Object,List<Node>> {

        private boolean loading;
        private Node path;

        public ChildrenListLoader(Node path) {
            this.path = path;
        }

        public boolean isLoading() {
            return loading;
        }

        @Override
        protected List<Node> doInBackground(Object... params) {
            loading = true;
            //logger.debug("Loading children of: {}", path.getUri());
            List<Node> children = path.list();
            loading = false;
            return children;
        }

        @Override
        protected void onPostExecute(List<Node> nodes) {
            onLoadChildrenListDone(path, nodes);
        }
    }
}
