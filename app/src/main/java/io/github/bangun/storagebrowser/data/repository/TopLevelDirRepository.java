package io.github.bangun.storagebrowser.data.repository;

import java.util.List;

import io.github.bangun.storagebrowser.data.TopLevelDir;

public interface TopLevelDirRepository {
    void add(TopLevelDir topLevelDir);
    void remove(TopLevelDir topLevelDir);
    List<TopLevelDir> listAll();
}
