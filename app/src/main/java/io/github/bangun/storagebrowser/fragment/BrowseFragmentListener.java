package io.github.bangun.storagebrowser.fragment;

import java.util.Stack;

import io.github.bangun.storagebrowser.data.Node;

public interface BrowseFragmentListener {
    void onLocationChanged(Stack<Node> nodes);
}
