package com.husph.mobilecomputing;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.husph.mobilecomputing.models.LetterCard;
import com.husph.mobilecomputing.utils.Constants;

import java.util.List;

public class CardGridAdapter extends RecyclerView.Adapter<CardGridAdapter.ViewHolder> {

    private static final int MARGIN_SIZE = 10;
    private final List<LetterCard> letterCards;
    private final Context context;
    private final CardClickListener cardClickListener;


    public CardGridAdapter(Context context, List<LetterCard> letterCards, CardClickListener cardClickListener) {
        this.context = context;
        this.letterCards = letterCards;
        this.cardClickListener = cardClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //render item

        final int cardWidth = parent.getWidth() / Constants.CARD_GRID_COLUMN;
        final int cardHeight = parent.getHeight() / (letterCards.size() / Constants.CARD_GRID_COLUMN);
        final int cardSideLength = Math.min(cardWidth, cardHeight) - (2 * MARGIN_SIZE);

        View itemView = LayoutInflater.from(context)
                .inflate(R.layout.memory_card, parent, false);

        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams)
                itemView.findViewById(R.id.cardView).getLayoutParams();

        layoutParams.height = cardSideLength;
        layoutParams.width = cardSideLength;
        layoutParams.setMargins(MARGIN_SIZE, MARGIN_SIZE, MARGIN_SIZE, MARGIN_SIZE);

        return new ViewHolder(itemView, this.cardClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(letterCards.get(position));
    }

    @Override
    public int getItemCount() {
        return letterCards.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final ImageButton imageButton;
        private final TextView tv_letter;

        ViewHolder(@NonNull View itemView, final CardClickListener cardClickListener) {
            super(itemView);
            imageButton = itemView.findViewById(R.id.imageButton);
            imageButton.setOnClickListener(view -> cardClickListener.onCardClick(getAdapterPosition()));
            tv_letter = itemView.findViewById(R.id.tv_letter_card);
        }

        void bind(LetterCard letterCard) {
            imageButton.setImageResource(letterCard.getIsFaceUp() ? R.color.white : R.drawable.ic_launcher_background );
            tv_letter.setText(letterCard.getIsFaceUp() ? letterCard.getIdentifier(): "");
        }
    }

    public interface CardClickListener {
        void onCardClick(int position);
    }
}
