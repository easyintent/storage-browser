package io.github.bangun.storagebrowser.data;


import android.content.Context;
import android.graphics.drawable.Drawable;

import java.util.Map;

public abstract class TopLevelDir {

    final private Map<String,String> params;
    final private String uri;

    // required for serialization
    final StorageLocation location;

    public TopLevelDir(StorageLocation location, String uri, Map<String, String> params) {
        this.params = params;
        this.uri = uri;
        this.location = location;
    }

    public Map<String, String> getParams() {
        return params;
    }

    public String getUri() {
        return uri;
    }

    public StorageLocation getLocation() {
        return location;
    }

    public abstract String getDescription(Context context);

    public abstract String getName(Context context);

    public abstract Drawable getIcon(Context context);

    public abstract Node createNode(Context context);

}
