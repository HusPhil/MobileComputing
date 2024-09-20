package com.husph.mobilecomputing.models;

import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.husph.mobilecomputing.CardGridAdapter;
import com.husph.mobilecomputing.MainActivity;
import com.husph.mobilecomputing.utils.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class FlipCardManager {

    public final static int FOUND_SELECTED_WORD = 0;
    public final static int NOT_FOUND_SELECTED_WORD = 1;
    public final static int FINDING_SELECTED_WORD = 2;
    private final static String TAG = "FlipCardManager";

    private final String selectedWord;

    private String constructedWord = "";



    public List<LetterCard> getLetterCards() {
        return letterCards;
    }

    private final List<LetterCard> letterCards;

    public FlipCardManager(String selectedWord) {
        this.selectedWord = selectedWord;
        letterCards = getLetterCards(selectedWord);
    }

    @NonNull
    private List<LetterCard> getLetterCards(String word) {

        final List<LetterCard> letterCards = new ArrayList<>();

        final String reArrangedLetters = arrangeLetters(word);

        final char[] wordAsCharArr = reArrangedLetters.toCharArray();

        for (char letter : wordAsCharArr) {
            letterCards.add(new LetterCard(Character.toString(letter)));
        }

        return letterCards;
    }

    private String arrangeLetters(String word) {
        //to arrnge letter for grid layout
        // HELLOWORLD -> HWEOLRLLOD

        int length = word.length();
        int mid = length / Constants.CARD_GRID_COLUMN;


        String firstHalf = word.substring(0, mid);
        String secondHalf = word.substring(mid);

        StringBuilder rearrangedLetters = new StringBuilder();

        for (int i = 0; i < mid; i++) {
            rearrangedLetters.append(firstHalf.charAt(i));
            rearrangedLetters.append(secondHalf.charAt(i));
        }


        return rearrangedLetters.toString();
    }

    public int flipLetterCardForResult(int position, CardGridAdapter cardGridAdapter) {
        LetterCard letterCard = letterCards.get(position);


        if(letterCard.getIsFaceUp()) {
            flipCardsToReset(cardGridAdapter);
            return NOT_FOUND_SELECTED_WORD;
        }

        letterCard.setIsFaceUp(!letterCard.getIsFaceUp());
        cardGridAdapter.notifyItemChanged(position);

        constructedWord += letterCard.getIdentifier();

        if(constructedWord.length() != this.selectedWord.length()) {
            return FINDING_SELECTED_WORD;
        }

        if(constructedWord.equals(this.selectedWord)) {
            return FOUND_SELECTED_WORD;
        }

        flipCardsToReset(cardGridAdapter);
        return NOT_FOUND_SELECTED_WORD;
    }

    private void flipCardsToReset(CardGridAdapter cardGridAdapter) {
        for (int i = 0; i < letterCards.size(); i++) {
            LetterCard letterCard = letterCards.get(i);
            if(letterCard.getIsFaceUp()) {
                letterCard.setIsFaceUp(false);
                cardGridAdapter.notifyItemChanged(i);
            }
        }
        constructedWord = "";
    }

}
