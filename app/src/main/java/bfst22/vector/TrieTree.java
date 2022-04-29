package bfst22.vector;

public class TrieTree {

    public TrieTree() {

    }

    static final int alhabet_size = 36; // antal symboler der bliver brugt, det danske alfabet+tal+mellemrum.

    // opretter trienode klassen, hver node har en arraylist af børn samt en bool
    // der afgør om det er en slutnode.
    // der bliver oprettet børn for alle symboler i alfabet og de bliver sat til
    // null.
    static class TrieNode {
        TrieNode[] children = new TrieNode[alhabet_size];
        String cords;
        boolean endOfString;

        TrieNode(String cords) {
            endOfString = false;
            this.cords = cords;
            for (int i = 0; i < alhabet_size; i++) {
                children[i] = null;
            }
        }
    }
    
    // opretter root node som altid vil være null;
    static TrieNode root = new TrieNode("0");

    // insert metode der tager en String som argument og indsætter denne i træet.
    // hver char i key bliver indsat efter den forrige og hver node har en parent
    // samt børn.
    public void insert(String key, String cords) {
        key = key.replace("æ", "ae").replace("ø","oe").replace("å", "aa");
        int depth;
        int index;

        TrieNode parent = root;
        for (depth = 0; depth < key.length(); depth++) {
            index = key.charAt(depth) - 'a';
            if(key.charAt(depth) == 'ø')
                index -= 17;
            if(key.charAt(depth) == 'å')
                index += 3;
            if(index < 0)
                index += 75;
            if (parent.children[index] == null)
                parent.children[index] = new TrieNode(cords);

            parent = parent.children[index];
        }
        parent.endOfString = true;
    }

    // search metode, fungerer ligesom insert. metode bare hvor den tjekker hver
    // node og sammenligner med input.
    public static String search(String key) {
        key = key.replace("æ", "ae").replace("ø","oe").replace("å", "aa");
        int depth;
        int index;
        TrieNode parent = root;
        boolean found;
        for (depth = 0; depth < key.length(); depth++) {
            index = key.charAt(depth) - 'a';
            if(index < 0)
                index += 75;
            if (parent.children[index] == null){
                found = false;
                return "No such address found";
            }

            parent = parent.children[index];
        }
        found = true;
        System.out.println(found);
        System.out.println(parent.cords);
        return parent.cords;
    }
}
