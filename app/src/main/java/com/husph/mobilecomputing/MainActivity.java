package com.husph.mobilecomputing;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.usage.NetworkStats;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.SignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.husph.mobilecomputing.models.LetterCard;
import com.husph.mobilecomputing.models.UserProfile;
import com.husph.mobilecomputing.utils.Constants;
import com.husph.mobilecomputing.utils.FormValidation;

import java.util.ArrayList;
import java.util.List;
public class MainActivity extends AppCompatActivity {

    private final static String TAG = "MainActivity";

    CardGridAdapter cardGridAdapter;

    FirebaseAuth mAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference usersRef;
    private UserProfile userProfile;
    private Gson gson;
    private SignInAccount oneTapClient;

    RecyclerView rv_card_grid;
    TextView tv_currentUser;
    MaterialToolbar tb_mainAct;

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
        InitializeComponents();

    }

    @Override
    protected void onStart() {
        super.onStart();
        handleUserNotLoggedIn();
        setupCardGrid();
        showCurrentUserName();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();

        menuInflater.inflate(R.menu.main_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId() == R.id.mi_logout) {
            showLogOutDialog();
        }



        return super.onOptionsItemSelected(item);
    }

    private void tbNavigation_OnClickEvent() {
        Intent openUserProfileActivity = new Intent(MainActivity.this, UserProfileActivity.class);

        if(userProfile != null) {
            openUserProfileActivity.putExtra(Constants.PASSKEY_UseProfile, gson.toJson(userProfile));
        }

        startActivity(openUserProfileActivity);
    }

    private void showLogOutDialog() {
        AlertDialog.Builder logOutDialogBuilder = new AlertDialog.Builder(this);

        logOutDialogBuilder.setTitle("You are about to log out");
        logOutDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                logOutUser();
            }
        });
        logOutDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });

        AlertDialog logOutDialog =  logOutDialogBuilder.create();

        logOutDialog.show();
    }

    private void logOutUser() {
        mAuth.signOut();
        handleUserNotLoggedIn();
    }

    private void showCurrentUserName() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser == null) return;

        String userId = currentUser.getUid();
        usersRef.child(userId).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {

                try {
                    if (!task.isSuccessful()) {
                        Log.e("firebase", "Error getting data", task.getException());
                    }
                    else {
                        Log.d("firebase", String.valueOf(task.getResult().getValue()));
                        userProfile = task.getResult().getValue(UserProfile.class);


                        String toolBarTitle = "Welcome back, " + userProfile.getUsername() + "!";
                        tb_mainAct.setTitle(toolBarTitle);
                    }
                } catch (Exception e) {
                    Log.e("Firebase Error: MainAct", e.toString());
                    String toolBarTitle = "Welcome back!";
                    tb_mainAct.setTitle(toolBarTitle);
                    Toast.makeText(MainActivity.this, "An error occurred while loading data", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void InitializeComponents() {
        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        usersRef = firebaseDatabase.getReference("users");
        gson = new Gson();

        tv_currentUser = findViewById(R.id.tv_currentUser);
        tb_mainAct = findViewById((R.id.tb_mainAct));
        setSupportActionBar(tb_mainAct);

        tb_mainAct.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tbNavigation_OnClickEvent();
            }
        });

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