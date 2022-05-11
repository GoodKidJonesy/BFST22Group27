package bfst22.vector;

import java.lang.annotation.Repeatable;
import java.util.ArrayList;
import java.util.Locale;

//Initial code from geeksforgeeks.org.
public class TrieTree {

    public TrieTree() {

    }

    static final int alhabet_size = 37; // antal symboler der bliver brugt, det danske alfabet+tal+mellemrum.

    // opretter trienode klassen, hver node har en arraylist af børn samt en bool
    // der afgør om det er en slutnode.
    // der bliver oprettet børn for alle symboler i alfabet og de bliver sat til
    // null.
    static class TrieNode {
        TrieNode[] children = new TrieNode[alhabet_size];
        String cords;
        boolean endOfString;
        char character;
        long id;
        public TrieNode(){}
        TrieNode(String cords, char c, long id) {
            this.character = c;
            this.id = id;
            endOfString = false;
            this.cords = cords;
            for (int i = 0; i < alhabet_size; i++) {
                children[i] = null;
            }
        }
    }
    
    // opretter root node som altid vil være null;
    static TrieNode root = new TrieNode();

    // insert metode der tager en String som argument og indsætter denne i træet.
    // hver char i key bliver indsat efter den forrige og hver node har en parent
    // samt børn.
    public void insert(String key, String cords, long id) {
        key = replaceKey(key);
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
                parent.children[index] = new TrieNode(cords, key.charAt(depth), id);

            parent = parent.children[index];
        }
        parent.endOfString = true;
    }

    // search metode, fungerer ligesom insert. metode bare hvor den tjekker hver
    // node og sammenligner med input.
    public long searchForID(String key) {
        key = replaceKey(key);
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
                return Integer.MAX_VALUE;
            }

            parent = parent.children[index];
        }
        found = true;
        return parent.id;
    }

    //metode til at søge efter alle ord der indeholder bruger input i trietree.
    //bruger rekursiv dybde først søgning metoden til dette.
    //finder den node som er sidste character i inputtet og kalder derefter DFS metoden med denne node, input og arraylist.
    public ArrayList<String> searchMuliple(String key){
        ArrayList<String> words = new ArrayList<>();
        key = replaceKey(key);
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
            String output = "";
            for (String word : key.split("\\s+")){
                output += word.replaceFirst(".", word.substring(0, 1).toUpperCase()) + " ";
            }
            words.add(output.replaceAll("ae", "æ").replace("oe", "ø").replace("aa", "å"));
            
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
    private String replaceKey(String key){
        key = key.replace("æ", "ae").replace("ø","oe").replace("å", "aa").replace("é","e").replace("ü","u").replace("ö", "oe").replace("õ","oe").replace("ä","ae"); 

        return key.toLowerCase().trim();
    }
}

