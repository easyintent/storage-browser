package io.github.easyintent.storagebrowser.data.repository;

import java.util.List;

import io.github.easyintent.storagebrowser.data.TopLevelDir;

public interface TopLevelDirRepository {
    void add(TopLevelDir topLevelDir);
    void remove(TopLevelDir topLevelDir);
    List<TopLevelDir> listAll();
}
