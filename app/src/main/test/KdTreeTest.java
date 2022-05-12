package tests;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.BeforeEach;
import org.junit.After;
import org.junit.Test;

import bfst22.vector.KDTree;
import bfst22.vector.OSMNode;
import bfst22.vector.OSMWay;

public class kdTreeTest {
    KDTree kd = new KDTree();

    // Runs before each Test
    @BeforeEach
    public void setUp() {
        OSMNode node1 = new OSMNode("test1", "test1", 1, 1);
        OSMNode node2 = new OSMNode("test2", "test2", 1, 1);
        OSMNode node3 = new OSMNode("test3", "test3", 1, 1);
        OSMNode node4 = new OSMNode("test4", "test4", 1, 1, "street1");
        OSMNode node5 = new OSMNode("test5", "test5", 1, 1, "street1");
        OSMNode node6 = new OSMNode("test5", "test5", 1, 1, "street1");

        ArrayList<Node> nodeList = new ArrayList<>(node1, node2, node3, node4, node5, node6);

        kd.fillTree(nodeList, 3);
    }

    @Test
    public void KDNearestNeighbor() {

    }
    // 
}
