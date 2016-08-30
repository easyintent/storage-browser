package io.github.bangun.storagebrowser.data;


import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v4.provider.DocumentFile;

import java.util.List;
import java.util.UnknownFormatConversionException;

import io.github.bangun.storagebrowser.R;

public class RootNode implements Node {

    final private String name;
    final private Uri uri;

    public RootNode(String name, Uri uri) {
        this.name = name;
        this.uri = uri;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public List<Node> list(Context context) {
        if (DocumentFile.isDocumentUri(context, uri)) {
            throw new UnsupportedOperationException("Not implemented for " + uri);
        }
        DocumentFile root = DocumentFile.fromTreeUri(context, uri);
        return FileNode.getChildren(this, root);
    }

    @Override
    public String getSummary(Context context) {
        return context.getString(R.string.lbl_local_storage);
    }

    @Override
    public Drawable getIcon(Context context) {
        return ContextCompat.getDrawable(context, R.drawable.ic_storage);
    }

    @Override
    public Node getParent() throws UnsupportedOperationException {
        throw new UnknownFormatConversionException("Root node has no parent");
    }

    @Override
    public boolean hasParent() {
        return false;
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
    public Uri getUri() {
        return uri;
    }

    @Override
    public String getType() {
        return "*/*";
    }
}
