package io.github.easyintent.storagebrowser.data;


import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v4.provider.DocumentFile;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.github.easyintent.storagebrowser.R;

public class DocumentFileNode implements Node {

    private Node parent;
    private DocumentFile file;

    public DocumentFileNode(Node parent, DocumentFile file) {
        this.parent = parent;
        this.file = file;
    }

    @Override
    public boolean hasParent() {
        return parent != null;
    }

    @Override
    public boolean isDirectory() {
        return file.isDirectory();
    }

    @Override
    public long size() {
        return file.length();
    }

    @Override
    public Uri getUri() {
        return file.getUri();
    }

    @Override
    public Node newFile(String name, String type) {
        return new DocumentFileNode(this, file.createFile(type, name));
    }

    @Override
    public Node newDir(String name) {
        DocumentFile newDir = file.createDirectory(name);
        if (newDir == null) {
            return null;
        }
        return new DocumentFileNode(this, newDir);
    }

    @Override
    public InputStream openForRead(Context context) throws IOException {
        return context.getContentResolver().openInputStream(getUri());
    }

    @Override
    public OutputStream openForWrite(Context context) throws IOException {
        return context.getContentResolver().openOutputStream(getUri());
    }

    @Override
    public String getType() {
        return file.getType();
    }

    @Override
    public long getModified() {
        return file.lastModified();
    }

    @Override
    public boolean delete() {
        return file.delete();
    }

    @Override
    public boolean rename(String newName) {
        return file.renameTo(newName);
    }

    @Override
    public String getName() {
        return file.getName();
    }

    @Override
    public List<Node> list() {
        return getChildren(this, file);
    }

    @Override
    public Drawable getIcon(Context context) {
        if (isDirectory()) {
            return ContextCompat.getDrawable(context, R.drawable.ic_folder);
        } else {
            return ContextCompat.getDrawable(context, R.drawable.ic_file);
        }
    }

    @Override
    public Node getParent() throws UnsupportedOperationException {
        return parent;
    }

    public static List<Node> getChildren(Node parent, DocumentFile documentFile) {
        DocumentFile[] files = documentFile.listFiles();
        List<Node> nodeList = new ArrayList<>();
        for (DocumentFile file : files) {
            nodeList.add(new DocumentFileNode(parent, file));
        }
        Collections.sort(nodeList, new NodeComparator());
        return nodeList;
    }
}
