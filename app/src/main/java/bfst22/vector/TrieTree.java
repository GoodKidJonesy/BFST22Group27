package bfst22.vector;

<<<<<<< HEAD
=======
import java.util.ArrayList;
//Initial code from geeksforgeeks.org.
>>>>>>> prototype-luczito
public class TrieTree {

    public TrieTree() {

    }

<<<<<<< HEAD
    static final int alphabet_size = 37; // antal symboler der bliver brugt, det danske alfabet+tal+mellemrum.
=======
    static final int alhabet_size = 37; // antal symboler der bliver brugt, det danske alfabet+tal+mellemrum.
>>>>>>> prototype-luczito

    // opretter trienode klassen, hver node har en arraylist af børn samt en bool
    // der afgør om det er en slutnode.
    // der bliver oprettet børn for alle symboler i alfabet og de bliver sat til
    // null.
    static class TrieNode {
<<<<<<< HEAD
        TrieNode[] children = new TrieNode[alphabet_size];
        String cords;
        boolean endOfString;

        TrieNode(String cords) {
            endOfString = false;
            this.cords = cords;
            for (int i = 0; i < alphabet_size; i++) {
=======
        TrieNode[] children = new TrieNode[alhabet_size];
        String cords;
        boolean endOfString;
        char character;
        public TrieNode(){}
        TrieNode(String cords, char c) {
            this.character = c;
            endOfString = false;
            this.cords = cords;
            for (int i = 0; i < alhabet_size; i++) {
>>>>>>> prototype-luczito
                children[i] = null;
            }
        }
    }
    
    // opretter root node som altid vil være null;
<<<<<<< HEAD
    static TrieNode root = new TrieNode("0");
=======
    static TrieNode root = new TrieNode();
>>>>>>> prototype-luczito

    // insert metode der tager en String som argument og indsætter denne i træet.
    // hver char i key bliver indsat efter den forrige og hver node har en parent
    // samt børn.
    public void insert(String key, String cords) {
        key = key.replace("æ", "ae").replace("ø","oe").replace("å", "aa");
        int depth;
        int index;

        TrieNode parent = root;
        for (depth = 0; depth < key.length(); depth++) {
            //System.out.println(key.charAt(depth));
            index = key.charAt(depth) - 'a';
            if(key.charAt(depth) == 'ø')
                index -= 17;
            if(key.charAt(depth) == 'å')
                index += 3;
            if(key.charAt(depth) == ' ')
                index += 101;
            if(index < 0)
                index += 75;
            if (parent.children[index] == null)
<<<<<<< HEAD
                parent.children[index] = new TrieNode(cords);
=======
                parent.children[index] = new TrieNode(cords, key.charAt(depth));
>>>>>>> prototype-luczito

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
            if(key.charAt(depth) == ' ')
                index += 101;
            if(index < 0)
                index += 75;            
            if (parent.children[index] == null){
                found = false;
<<<<<<< HEAD
                System.out.println("not found");
=======
>>>>>>> prototype-luczito
                return "No such address found";
            }

            parent = parent.children[index];
        }
        found = true;
        System.out.println(found);
        System.out.println(parent.cords);
        return parent.cords;
    }

<<<<<<< HEAD
}
=======
    //metode til at søge efter alle ord der indeholder bruger input i trietree.
    //bruger rekursiv dybde først søgning metoden til dette.
    //finder den node som er sidste character i inputtet og kalder derefter DFS metoden med denne node, input og arraylist.
    public ArrayList<String> searchMuliple(String key){
        ArrayList<String> words = new ArrayList<>();
        key = key.replace("æ", "ae").replace("ø","oe").replace("å", "aa");
        TrieNode currentNode = root;
        for(int i = 0; i < key.length(); i++){
            inner : for(TrieNode n : currentNode.children){
                if(n == null){
                }
                else if(n.character == key.charAt(i)){
                    currentNode = n;
                    break inner;
                }
            }
        }
        words = DFS(key, currentNode, words);
        return words;
    }
    //rekursiv dybde først søgning. Søger rekursivt igennem alle børn til currentnode og tilføjer alle ord der matcher input til listen. 
    static ArrayList<String> DFS(String key, TrieNode current, ArrayList<String> words){
        if(current.endOfString){
            words.add(key.replaceAll("ae", "æ").replace("oe", "ø").replace("aa", "å"));
            
        }
        for(TrieNode n : current.children){
            if(n == null){
                
            }else{
                current = n;
                DFS(key + n.character, current, words);
            }
        }
        return words;
    }
}

>>>>>>> prototype-luczito
