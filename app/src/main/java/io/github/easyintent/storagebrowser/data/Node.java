package io.github.easyintent.storagebrowser.data;


import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

public interface Node {

    boolean hasParent();
    Node getParent() throws UnsupportedOperationException;
    boolean isDirectory();

    Node newFile(String name, String type);
    Node newDir(String name);

    InputStream openForRead(Context context) throws IOException;
    OutputStream openForWrite(Context context) throws IOException;
    Drawable getIcon(Context context);

    List<Node> list();
    String getName();
    long size();
    Uri getUri();
    String getType();
    long getModified();

    boolean delete();
    boolean rename(String newName);
}
