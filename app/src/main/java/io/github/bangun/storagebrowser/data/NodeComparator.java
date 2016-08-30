package io.github.bangun.storagebrowser.data;


import java.util.Comparator;

public class NodeComparator implements Comparator<Node> {
    @Override
    public int compare(Node t1, Node t2) {
        boolean is1Dir = t1.isDirectory();
        boolean is2Dir = t2.isDirectory();

        if (is1Dir && !is2Dir) {
            return -1;
        }

        if (!is1Dir && is2Dir) {
            return 1;
        }

        return t1.getName().compareTo(t2.getName());
    }
}
