package io.github.bangun.storagebrowser.fragment;

import io.github.bangun.storagebrowser.data.Node;

public interface NodeActionListener {
    void onView(Node node);
    void onDelete(Node node);
    void onRename(Node node);
    void onRemoveFromList(Node node);
}
