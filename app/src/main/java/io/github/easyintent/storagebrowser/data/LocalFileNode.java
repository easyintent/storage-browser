package io.github.easyintent.storagebrowser.data;


import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v4.content.ContextCompat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.github.easyintent.storagebrowser.R;

public class LocalFileNode implements Node {

    private static final Logger logger = LoggerFactory.getLogger(LocalFileNode.class);

    private LocalFileNode parent;
    private File file;

    public LocalFileNode(LocalFileNode parent, File file) {
        this.parent = parent;
        this.file = file;
    }

    @Override
    public boolean hasParent() {
        return parent != null;
    }

    @Override
    public Node getParent() throws UnsupportedOperationException {
        return parent;
    }

    @Override
    public boolean isDirectory() {
        return file.isDirectory();
    }

    @Override
    public Node newFile(String name, String type) {
        File newDir = new File(file, name);
        if (newDir.exists()) {
            logger.debug("File already exists: {}", name);
            return null;
        }
        return new LocalFileNode(this, newDir);
    }

    @Override
    public Node newDir(String name) {
        File newDir = new File(file, name);
        if (newDir.exists()) {
            logger.debug("Directory already exists: {}", name);
            return null;
        }
        return new LocalFileNode(this, newDir);
    }

    @Override
    public InputStream openForRead(Context context) throws IOException {
        return new FileInputStream(file);
    }

    @Override
    public OutputStream openForWrite(Context context) throws IOException {
        return new FileOutputStream(file);
    }

    @Override
    public Drawable getIcon(Context context) {
        return ContextCompat.getDrawable(context, file.isDirectory() ? R.drawable.ic_folder : R.drawable.ic_file);
    }

    @Override
    public List<Node> list() {
        if (!file.isDirectory()) {
            return Collections.emptyList();
        }
        File[] files = file.listFiles();
        if (files == null || files.length == 0) {
            return Collections.emptyList();
        }
        List<Node> list = new ArrayList<>();
        for (File f: files) {
            list.add(new LocalFileNode(this, f));
        }
        return list;
    }

    @Override
    public String getName() {
        return file.getName();
    }

    @Override
    public long size() {
        return file.length();
    }

    @Override
    public Uri getUri() {
        return Uri.fromFile(file);
    }

    @Override
    public String getType() {
        // fixme
        return "application/octet-stream";
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
        File parent = file.getParentFile();
        if (parent == null) {
            logger.debug("Can not rename root directory");
            return false;
        }
        File target = new File(parent, newName);
        if (target.exists()) {
            logger.debug("Can not rename, file exists");
            return false;
        }
        return file.renameTo(target);
    }
}
