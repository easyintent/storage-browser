package io.github.bangun.storagebrowser.data;

import android.content.Context;
import android.net.Uri;
import android.support.v4.provider.DocumentFile;

import java.util.Collections;
import java.util.Map;

public class DocumentFileTopLevelDir extends TopLevelDir {

    public DocumentFileTopLevelDir(String uri) {
        super(StorageLocation.DOCUMENT_FILE, uri, Collections.<String, String>emptyMap());
    }

    @Override
    public Node createNode(Context context) {
        DocumentFile file = DocumentFile.fromTreeUri(context, Uri.parse(getUri()));
        return new DocumentFileRootNode(new DocumentFileNode(null, file));
    }
}
