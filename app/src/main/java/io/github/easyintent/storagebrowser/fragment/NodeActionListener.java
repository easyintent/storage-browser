package io.github.easyintent.storagebrowser.fragment;

import io.github.easyintent.storagebrowser.data.Node;

public interface NodeActionListener {
    void onView(Node node);
    void onDelete(Node node);
    void onRename(Node node);
    void onCopy(Node node);
    void onDetail(Node node);
    void onShare(Node node);
}
