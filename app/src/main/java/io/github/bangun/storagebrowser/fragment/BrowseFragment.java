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

import java.util.List;

import io.github.bangun.storagebrowser.R;
import io.github.bangun.storagebrowser.Setting;
import io.github.bangun.storagebrowser.data.Node;
import io.github.bangun.storagebrowser.data.RootNode;

@EFragment
@OptionsMenu(R.menu.fragment_browse)
public class BrowseFragment extends ListFragment {

    private static final Logger logger = LoggerFactory.getLogger(BrowseFragment.class);
    private static final int COPY_FROM = 0x10c0;

    private Node current;
    private Setting setting;

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
        setting = new Setting(getActivity());
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

    private void copyFrom(Intent data) {

        //logger.debug("copy from : {}", data);

        Uri uri = data.getData();
        DocumentFile src = DocumentFile.fromSingleUri(getActivity(), uri);
        Node target = current.newFile(getActivity(), src.getName(), src.getType());

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

        listChildren(getActivity(), current.getParent());
        return true;
    }

    public void reload() {

        setListShown(false);

        if (current == null) {
            listRoot();
        } else {
            listChildren(getActivity(), current);
        }
    }

    @OptionsItem(R.id.refresh)
    protected void refreshClicked() {
        reload();
    }

    @OptionsItem(R.id.copy_from)
    protected void copyFromClicked() {

        if (current == null) {
            // not applicable on root
            return;
        }

        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, COPY_FROM);
    }

    @Background
    protected void listChildren(Context context, Node path) {

        logger.debug("Loading path: {}", path.getUri());
        List<Node> children = path.list(context);

        onListPathDone(path, children);
    }

    @UiThread
    protected void onListPathDone(Node path, List<Node> children) {
        this.current = path;
        if (!isAdded()) {
            return;
        }
        showList(children);
    }

    private void listRoot() {
        List<RootNode> rootDocuments = setting.listRoot();
        showList(rootDocuments);
    }


    private void showItem(Node item) {
        if (item.isDirectory()) {
            setListShown(false);
            listChildren(getActivity(), item);
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
        final DocumentListAdapter adapter = new DocumentListAdapter(getActivity(), children);
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
