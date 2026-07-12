package com.group3boot.sunspot.adapter;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.group3boot.sunspot.R;
import com.group3boot.sunspot.models.Spot;

import java.util.List;

public class SpotRecyclerAdapter extends RecyclerView.Adapter<SpotRecyclerAdapter.ViewHolder> {

    public interface OnItemClickListener {
        void onSpotItemClick(Spot spot);
        void onFavoriteButtonPressed(int position);
    }

    private int layout;
    private List<Spot> spotList;
    private boolean heartVisible;
    private Context context;
    private final OnItemClickListener onItemClickListener;

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView textViewName;
        private final TextView textViewPosizione;
        private final CheckBox favoriteCheckbox;
        private final ImageView imageView;

        public ViewHolder(View view) {
            super(view);

            textViewName = view.findViewById(R.id.titolo);
            textViewPosizione = view.findViewById(R.id.posizione);
            favoriteCheckbox = view.findViewById(R.id.favoriteButton);
            imageView = view.findViewById(R.id.image_view);

            favoriteCheckbox.setOnClickListener(this);
            view.setOnClickListener(this);
        }

        public TextView getTextViewName() { return textViewName; }
        public TextView getTextViewPosizione() { return textViewPosizione; }
        public CheckBox getFavoriteCheckbox() { return favoriteCheckbox; }
        public ImageView getImageView() { return imageView; }

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.favoriteButton) {
                onItemClickListener.onFavoriteButtonPressed(getBindingAdapterPosition());
            } else {
                onItemClickListener.onSpotItemClick(spotList.get(getBindingAdapterPosition()));
            }
        }
    }

    public SpotRecyclerAdapter(int layout, List<Spot> spotList, boolean heartVisible,
                               OnItemClickListener onItemClickListener) {
        this.layout = layout;
        this.spotList = spotList;
        this.heartVisible = heartVisible;
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(layout, viewGroup, false);

        if (this.context == null) this.context = viewGroup.getContext();

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        Spot currentSpot = spotList.get(position);

        viewHolder.getTextViewName().setText(currentSpot.getName());
        viewHolder.getTextViewPosizione().setText(currentSpot.getPosizione());
        viewHolder.getFavoriteCheckbox().setChecked(currentSpot.isLiked());

        String firstPhoto = (currentSpot.getPhotoUrls() != null && !currentSpot.getPhotoUrls().isEmpty())
                ? currentSpot.getPhotoUrls().get(0)
                : null;

        Glide.with(context)
                .load(firstPhoto)
                .placeholder(new ColorDrawable(context.getColor(R.color.md_theme_inverseOnSurface)))
                .into(viewHolder.getImageView());

        if (!heartVisible) {
            viewHolder.getFavoriteCheckbox().setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return spotList.size();
    }
}