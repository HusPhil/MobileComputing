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
        mAuth = FirebaseAuth.getInstance();

        handleUserNotLoggedIn();

        setupCardGrid();

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
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);

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

            Intent openMainActivity = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(openMainActivity);
        }
    }

    private void flipLetterCard(int position) {
        LetterCard letterCard = letterCards.get(position);

        letterCard.setIsFaceUp(!letterCard.getIsFaceUp());
        cardGridAdapter.notifyItemChanged(position);

    }

    private List<LetterCard> getLetterCards(String word) {

        final List<LetterCard> letterCards = new ArrayList<>();
        final char[] wordAsCharArr = word.toCharArray();
        final int column = 2;
        for (char letter : wordAsCharArr) {
            letterCards.add(new LetterCard(Character.toString(letter)));
        }

//        for (int i = 0; i < wordAsCharArr.length; i++) {
//            String letter = Character.toString(wordAsCharArr[i]);
//            if((i + 1) % 2 == 0) {
//                // [0,1,2,3]
//                int halfIndex = wordAsCharArr.length / 2;
//                letter = Character.toString(wordAsCharArr[halfIndex + i - 1]);
//                letterCards.add(new LetterCard(letter));
//                continue;
//            }
//
//            letterCards.add(new LetterCard(letter));
//        }
//
////        List<LetterCard> rearrangedLetterCards = new ArrayList<>();

        return letterCards;
    }
}