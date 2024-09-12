package com.husph.mobilecomputing;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.husph.mobilecomputing.models.LetterCard;
import com.husph.mobilecomputing.utils.Constants;
import com.husph.mobilecomputing.utils.DetailsActivity;
import com.husph.mobilecomputing.utils.FormValidation;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private final static String TAG = "MainActivity";

    CardGridAdapter cardGridAdapter;

    FirebaseAuth mAuth;

    RecyclerView rv_card_grid;

    private String selectedWord = "Hello World".trim().replaceAll("\\s", "").toUpperCase();
    private List<LetterCard> letterCards;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        setupCardGrid();
        mAuth = FirebaseAuth.getInstance();
        handleUserNotLoggedIn();
    }

    private void setupCardGrid() {
        rv_card_grid = findViewById(R.id.rv_card_grid);
        letterCards = getLetterCards(selectedWord);

        CardGridAdapter.CardClickListener cardClickListener = position -> {
            Log.i(TAG, "Trying card click listener on MainActivity");
            flipLetterCard(position);
        };

        cardGridAdapter = new CardGridAdapter(
                MainActivity.this,
                letterCards,
                cardClickListener);

        // set up the rv_grid
        rv_card_grid.setAdapter(cardGridAdapter);
        rv_card_grid.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, Constants.CARD_GRID_COLUMN);

        rv_card_grid.setLayoutManager(gridLayoutManager);
    }

    private void handleUserNotLoggedIn() {
        if(mAuth.getCurrentUser() == null) {

            Toast.makeText(
                MainActivity.this,
                FormValidation.WarningMessage.USER_NOT_LOGGED_IN_WARNING.getMessage(),
                Toast.LENGTH_LONG
            )
            .show();

            Intent openLoginActivity = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(openLoginActivity);
        }
    }

    private void flipLetterCard(int position) {
        LetterCard letterCard = letterCards.get(position);

        letterCard.setIsFaceUp(!letterCard.getIsFaceUp());
        cardGridAdapter.notifyItemChanged(position);
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

    private List<LetterCard> getLetterCards(String word) {

        final List<LetterCard> letterCards = new ArrayList<>();

        final String reArrangdeLetters = arrangeLetters(word);

        final char[] wordAsCharArr = reArrangdeLetters.toCharArray();

        for (char letter : wordAsCharArr) {
            letterCards.add(new LetterCard(Character.toString(letter)));
        }

        return letterCards;
    }
}