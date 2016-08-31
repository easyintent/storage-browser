package io.github.bangun.storagebrowser.data.repository;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.github.bangun.storagebrowser.data.DocumentFileTopLevelDir;
import io.github.bangun.storagebrowser.data.StorageLocation;
import io.github.bangun.storagebrowser.data.TopLevelDir;

//
// todo: currently backed by shared pref
// should use SQLite database, or file
//
public class DefaultTopLevelRepository implements TopLevelDirRepository {

    private static final String ROOT_LIST = "root_list";
    private Context context;
    private Gson gson;

    public DefaultTopLevelRepository(Context context) {
        this.context = context;
        gson = new Gson();
    }

    @Override
    public void add(TopLevelDir topLevelDir) {

        String serialized = gson.toJson(topLevelDir);

        SharedPreferences pref = getTopLevelStorageSetting();
        pref.edit()
                .putString(topLevelDir.getUri().toString(), serialized)
                .apply();
    }

    @Override
    public void remove(TopLevelDir topLevelDir) {
        getTopLevelStorageSetting().edit()
                .remove(topLevelDir.getUri().toString())
                .apply();
    }

    @Override
    public List<TopLevelDir> listAll() {
        List<TopLevelDir> roots = new ArrayList<>();

        SharedPreferences pref = getTopLevelStorageSetting();
        Map<String, ?> all = pref.getAll();
        for (String uri: all.keySet()) {
            String serialized = (String) all.get(uri);
            TopLevelDir topLevelDir = deserialize(serialized);
            roots.add(topLevelDir);
        }
        return roots;
    }

    private TopLevelDir deserialize(String serialized) {
        JsonObject jsonObject = gson.fromJson(serialized, JsonObject.class);
        StorageLocation location = gson.fromJson(jsonObject.get("location"), StorageLocation.class);
        TopLevelDir dir = null;
        switch (location) {
            case DOCUMENT_FILE:
                dir = gson.fromJson(jsonObject, DocumentFileTopLevelDir.class);
                break;
            default:
                throw new UnsupportedOperationException("Location is not supported yet:" + location);
        }
        return dir;
    }

    private SharedPreferences getTopLevelStorageSetting() {
        return context.getSharedPreferences(ROOT_LIST, Context.MODE_PRIVATE);
    }
}
