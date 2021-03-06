package io.github.easyintent.storagebrowser.data;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v4.content.ContextCompat;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import io.github.easyintent.storagebrowser.R;

public class DocumentFileRootNode implements TopLevelNode {

    private DocumentFileNode documentFileNode;
    private TopLevelDir topLevelDir;
    private String displayName;

    public DocumentFileRootNode(TopLevelDir topLevelDir, DocumentFileNode documentFileNode, String displayName) {
        this.documentFileNode = documentFileNode;
        this.topLevelDir = topLevelDir;
        this.displayName = displayName;
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
        return displayName;
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
    public InputStream openForRead(Context context) throws IOException {
        return documentFileNode.openForRead(context);
    }

    @Override
    public OutputStream openForWrite(Context context) throws IOException {
        return documentFileNode.openForWrite(context);
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
