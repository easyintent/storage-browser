package io.github.easyintent.storagebrowser.data;


import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v4.content.ContextCompat;

import java.io.File;
import java.util.Map;

import io.github.easyintent.storagebrowser.R;

public class LocalFileTopLevelDir extends TopLevelDir {

    public static final String DISPLAY_NAME = "display_name";

    public LocalFileTopLevelDir(String uri, Map<String,String> map) {
        super(StorageLocation.LOCAL_STORAGE, uri, map);
    }

    @Override
    public String getDescription(Context context) {
        return context.getString(R.string.lbl_local_storage);
    }

    @Override
    public String getName(Context context) {
        return getDisplayName();
    }

    @Override
    public Drawable getIcon(Context context) {
        return ContextCompat.getDrawable(context, R.drawable.ic_local_storage);
    }

    @Override
    public Node createNode(Context context) {
        File file = new File(Uri.parse(getUri()).getPath());
        return new LocalFileRootNode(this, new LocalFileNode(null, file), getDisplayName());
    }

    private String getDisplayName() {
        String name = getParams().get(DISPLAY_NAME);
        if (name != null) {
            return name;
        }
        return Uri.parse(getUri()).getLastPathSegment();
    }
}
