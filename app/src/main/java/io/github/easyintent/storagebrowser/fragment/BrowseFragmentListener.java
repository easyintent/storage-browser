package io.github.easyintent.storagebrowser.fragment;

import java.util.Stack;

import io.github.easyintent.storagebrowser.data.Node;

public interface BrowseFragmentListener {
    void onLocationChanged(Stack<Node> nodes);
}
