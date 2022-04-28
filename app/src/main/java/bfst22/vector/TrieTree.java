package bfst22.vector;

public class TrieTree {

    public TrieTree() {

    }

    static final int alhabet_size = 40; // antal symboler der bliver brugt, det danske alfabet+tal+mellemrum.

    // opretter trienode klassen, hver node har en arraylist af børn samt en bool
    // der afgør om det er en slutnode.
    // der bliver oprettet børn for alle symboler i alfabet og de bliver sat til
    // null.
    static class TrieNode {
        TrieNode[] children = new TrieNode[alhabet_size];

        boolean endOfString;

        TrieNode() {
            endOfString = false;
            for (int i = 0; i < alhabet_size; i++) {
                children[i] = null;
            }
        }
    }

    // opretter root node som altid vil være null;
    static TrieNode root;

    // insert metode der tager en String som argument og indsætter denne i træet.
    // hver char i key bliver indsat efter den forrige og hver node har en parent
    // samt børn.
    public void insert(String key) {
        int depth;
        int index;

        TrieNode parent = root;
        for (depth = 0; depth < key.length(); depth++) {
            index = key.charAt(depth) - 'a';
            if (parent.children[index] == null)
                parent.children[index] = new TrieNode();

            parent = parent.children[index];
        }
        parent.endOfString = true;
    }

    // search metode, fungerer ligesom insert. metode bare hvor den tjekker hver
    // node og sammenligner med input.
    public static boolean search(String key) {
        int depth;
        int index;
        TrieNode parent = root;

        for (depth = 0; depth < key.length(); depth++) {
            index = key.charAt(depth) - 'a';
            if (parent.children[index] == null)
                return false;

            parent = parent.children[index];
        }
        return parent.endOfString;
    }

    public void testAdd() {
        insert("aha 13");
        System.out.println("inserted 13");
        insert("æble 1b");
        System.out.println("inserted æ");
        insert("årøgade 51");
        System.out.println("inserted åø ");

    }
}
