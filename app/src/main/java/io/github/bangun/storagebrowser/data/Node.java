package io.github.bangun.storagebrowser.data;


import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;

import java.util.List;

public interface Node {

    boolean hasParent();
    Node getParent() throws UnsupportedOperationException;
    boolean isDirectory();

    Node newFile(Context context, String name, String type);
    String getSummary(Context context);
    Drawable getIcon(Context context);
    List<Node> list(Context context);

    String getName();
    long size();
    Uri getUri();
    String getType();
}
