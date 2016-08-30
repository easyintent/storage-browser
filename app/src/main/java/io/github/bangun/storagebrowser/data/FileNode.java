package io.github.bangun.storagebrowser.data;


import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v4.provider.DocumentFile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.github.bangun.storagebrowser.R;

public class FileNode implements Node {

    private Node parent;
    private DocumentFile file;

    public FileNode(Node parent, DocumentFile file) {
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
    public Node newFile(Context context, String name, String type) {
        return new FileNode(this, file.createFile(type, name));
    }

    @Override
    public String getType() {
        return file.getType();
    }

    @Override
    public String getName() {
        return file.getName();
    }

    @Override
    public List<Node> list(Context context) {
        return getChildren(this, file);
    }

    @Override
    public String getSummary(Context context) {
        if (isDirectory()) {
            return context.getString(R.string.lbl_na);
        }
        String size = String.format("%,d", size());
        return context.getString(R.string.lbl_bytes_ex, size);
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
            nodeList.add(new FileNode(parent, file));
        }
        Collections.sort(nodeList, new NodeComparator());
        return nodeList;
    }
}
