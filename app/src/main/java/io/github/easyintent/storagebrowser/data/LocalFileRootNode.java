package io.github.easyintent.storagebrowser.data;


import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

public class LocalFileRootNode implements TopLevelNode {

    private LocalFileTopLevelDir topLevelDir;
    private LocalFileNode fileNode;

    public LocalFileRootNode(LocalFileNode fileNode, LocalFileTopLevelDir topLevelDir) {
        this.topLevelDir = topLevelDir;
        this.fileNode = fileNode;
    }

    @Override
    public TopLevelDir getTopLevelDir() {
        return topLevelDir;
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
    public Node newFile(String name, String type) {
        return fileNode.newFile(name, type);
    }

    @Override
    public Node newDir(String name) {
        return fileNode.newDir(name);
    }

    @Override
    public InputStream openForRead(Context context) throws IOException {
        return fileNode.openForRead(context);
    }

    @Override
    public OutputStream openForWrite(Context context) throws IOException {
        return fileNode.openForWrite(context);
    }

    @Override
    public Drawable getIcon(Context context) {
        return fileNode.getIcon(context);
    }

    @Override
    public List<Node> list() {
        return fileNode.list();
    }

    @Override
    public String getName() {
        return fileNode.getName();
    }

    @Override
    public long size() {
        return fileNode.size();
    }

    @Override
    public Uri getUri() {
        return fileNode.getUri();
    }

    @Override
    public String getType() {
        return fileNode.getType();
    }

    @Override
    public long getModified() {
        return fileNode.getModified();
    }

    @Override
    public boolean delete() {
        return fileNode.delete();
    }

    @Override
    public boolean rename(String newName) {
        return fileNode.rename(newName);
    }
}
