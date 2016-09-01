package io.github.bangun.storagebrowser.data;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v4.content.ContextCompat;

import java.util.List;

import io.github.bangun.storagebrowser.R;

public class DocumentFileRootNode implements TopLevelNode {

    private DocumentFileNode documentFileNode;
    private TopLevelDir topLevelDir;

    public DocumentFileRootNode(TopLevelDir topLevelDir, DocumentFileNode documentFileNode) {
        this.documentFileNode = documentFileNode;
        this.topLevelDir = topLevelDir;
    }

    @Override
    public boolean hasParent() {
        return false;
    }

    @Override
    public Node getParent() throws UnsupportedOperationException {
        return null;
    }

    @Override
    public boolean isDirectory() {
        return true;
    }

    @Override
    public long size() {
        return 0;
    }

    @Override
    public String getName() {
        return documentFileNode.getName();
    }

    @Override
    public List<Node> list() {
        return documentFileNode.list();
    }

    @Override
    public Drawable getIcon(Context context) {
        return ContextCompat.getDrawable(context, R.drawable.ic_storage_access);
    }

    @Override
    public Uri getUri() {
        return documentFileNode.getUri();
    }

    @Override
    public Node newFile(String name, String type) {
        return documentFileNode.newFile(name, type);
    }

    @Override
    public Node newDir(String name) {
        return documentFileNode.newDir(name);
    }

    @Override
    public String getType() {
        return documentFileNode.getType();
    }

    @Override
    public long getModified() {
        return documentFileNode.getModified();
    }

    @Override
    public boolean delete() {
        return documentFileNode.delete();
    }

    @Override
    public boolean rename(String newName) {
        return documentFileNode.rename(newName);
    }

    @Override
    public TopLevelDir getTopLevelDir() {
        return topLevelDir;
    }
}
