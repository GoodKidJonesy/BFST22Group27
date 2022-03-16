package bfst22.vector;

import java.util.ArrayList;
import java.util.Comparator;

public class NodeMap extends ArrayList<OSMNode> {
    boolean sorted;

    public boolean add(OSMNode node) {
        sorted = false;
        return super.add(node);
    }

    public OSMNode get(long ref) {
        if (!sorted) {
            sort(Comparator.comparing(node -> node.id));
            sorted = true;
        }
        int lo = 0;
        int hi = size();
        // I: get(lo).id <= ref < get(hi).id
        while (hi - lo > 1) {
            int mi = (lo + hi) / 2;
            if (get(mi).id <= ref) {
                lo = mi;
            } else {
                hi = mi;
            }
        }
        var node = get(lo);
        return node.id == ref ? node : null;
    }
}
