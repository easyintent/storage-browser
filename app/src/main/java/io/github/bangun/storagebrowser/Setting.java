package io.github.bangun.storagebrowser;


import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.github.bangun.storagebrowser.data.RootNode;

public class Setting {

    private static final String ROOT_LIST = "root";

    private Context context;

    public Setting(Context context) {
        this.context = context;
    }

    public List<RootNode> listRoot() {
        List<RootNode> roots = new ArrayList<>();

        SharedPreferences pref = getRootSetting();
        Map<String, ?> all = pref.getAll();
        for (String uri: all.keySet()) {
            roots.add(new RootNode((String) all.get(uri), Uri.parse(uri)));
        }
        return roots;
    }

    public void addRoot(RootNode rootDocument) {
        // should use db
        SharedPreferences pref = getRootSetting();
        pref.edit()
                .putString(rootDocument.getUri().toString(), rootDocument.getName())
                .apply();
    }

    private SharedPreferences getRootSetting() {
        return context.getSharedPreferences(ROOT_LIST, Context.MODE_PRIVATE);
    }

}
