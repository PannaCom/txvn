package com.quickcar.thuexe.Models;

import java.util.ArrayList;

/**
 * Created by DatNT on 6/28/2016.
 */
public class FilterObject {
    private String keyWord;
    private String[] arrayCandidate;
    public FilterObject(String keyWord, String[] arrayCandidate) {
        this.keyWord = keyWord;
        this.arrayCandidate = arrayCandidate;
    }

    public String getKeyWord() {
        return keyWord;
    }

    public void setKeyWord(String keyWord) {
        this.keyWord = keyWord;
    }

    public String[] getArrayCandidate() {
        return arrayCandidate;
    }

    public void setArrayCandidate(String[] arrayCandidate) {
        this.arrayCandidate = arrayCandidate;
    }
}
