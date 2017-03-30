package io.github.easyintent.storagebrowser.fragment;

import io.github.easyintent.storagebrowser.data.TopLevelDir;

public interface TopLevelDirActionListener {
    void onView(TopLevelDir topLevelDir);
    void onRemoveFromList(TopLevelDir topLevelDir);
}
