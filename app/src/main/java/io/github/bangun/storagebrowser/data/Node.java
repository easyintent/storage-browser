package io.github.bangun.storagebrowser.data;


import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;

import java.util.List;

public interface Node {

    boolean hasParent();
    Node getParent() throws UnsupportedOperationException;
    boolean isDirectory();

    long size();
    String getName();

    List<Node> list(Context context);
    String getSummary(Context context);
    Drawable getIcon(Context context);

    Uri getUri();

    String getType();
}
