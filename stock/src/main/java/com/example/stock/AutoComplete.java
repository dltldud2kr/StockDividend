package com.example.stock;

import org.apache.commons.collections4.Trie;
import org.apache.commons.collections4.trie.PatriciaTrie;

public class AutoComplete {

    private Trie trie = new PatriciaTrie();

    public AutoComplete(Trie trie){
        this.trie = trie;
    }
    public void add(String s) {
        this.trie.put(s, "world");
    }

    public Object get(String s) {
        return this.trie.get(s);
    }
}
