package com.husph.mobilecomputing.models;

public class LetterCard {
    private final String letterValue;
    private boolean isFaceUp;
    private boolean isMatched;

    public String getIdentifier() {
        return letterValue;
    }

    public boolean getIsFaceUp() {
        return isFaceUp;
    }

    public void setIsFaceUp(boolean isFaceUp) {
        this.isFaceUp = isFaceUp;
    }

    public boolean getIsMatched() {
        return isMatched;
    }

    public void setIsMatched(boolean matched) {
        isMatched = matched;
    }

    public LetterCard(String letterValue) {
        this.letterValue = letterValue;
    }
}
