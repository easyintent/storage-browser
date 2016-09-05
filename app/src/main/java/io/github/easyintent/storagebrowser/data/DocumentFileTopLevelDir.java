package io.github.easyintent.storagebrowser.data;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v4.provider.DocumentFile;

import java.util.Collections;

import io.github.easyintent.storagebrowser.R;

public class DocumentFileTopLevelDir extends TopLevelDir {

    public DocumentFileTopLevelDir(String uri) {
        super(StorageLocation.DOCUMENT_FILE, uri, Collections.<String, String>emptyMap());
    }

    @Override
    public String getDescription(Context context) {
        return context.getString(R.string.lbl_storage_access_framework);
    }

    @Override
    public String getName(Context context) {
        return Uri.parse(getUri()).getLastPathSegment();
    }

    @Override
    public Drawable getIcon(Context context) {
        return ContextCompat.getDrawable(context, R.drawable.ic_storage_access);
    }

    @Override
    public Node createNode(Context context) {
        DocumentFile file = DocumentFile.fromTreeUri(context, Uri.parse(getUri()));
        return new DocumentFileRootNode(this, new DocumentFileNode(null, file));
    }
}
