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

    public interface OnSpotTimeRequestListener {
        void onRequestTime(Spot spot, SpotTimeCallback callback);
    }

    public interface SpotTimeCallback {
        void onTimeReady(String time);
    }

    private int layout;
    private List<Spot> spotList;
    private boolean heartVisible;
    private String currentUserId;
    private Context context;
    private final OnItemClickListener onItemClickListener;
    private final OnSpotTimeRequestListener onSpotTimeRequestListener;

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView textViewName;
        private final TextView textViewPosizione;
        private final TextView textViewSpotTypeLabel;
        private final TextView textViewSpotTime;
        private final ImageView imageViewSpotTimeIcon;
        private final CheckBox favoriteCheckbox;
        private final ImageView imageView;

        public ViewHolder(View view) {
            super(view);

            textViewName = view.findViewById(R.id.titolo);
            textViewPosizione = view.findViewById(R.id.posizione);
            textViewSpotTypeLabel = view.findViewById(R.id.textViewSpotTypeLabel);
            textViewSpotTime = view.findViewById(R.id.textViewSpotTime);
            imageViewSpotTimeIcon = view.findViewById(R.id.imageViewSpotTimeIcon);
            favoriteCheckbox = view.findViewById(R.id.favoriteButton);
            imageView = view.findViewById(R.id.image_view);

            favoriteCheckbox.setOnClickListener(this);
            view.setOnClickListener(this);
        }

        public TextView getTextViewName() { return textViewName; }
        public TextView getTextViewPosizione() { return textViewPosizione; }
        public TextView getTextViewSpotTypeLabel() { return textViewSpotTypeLabel; }
        public TextView getTextViewSpotTime() { return textViewSpotTime; }
        public ImageView getImageViewSpotTimeIcon() { return imageViewSpotTimeIcon; }
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

    public SpotRecyclerAdapter(int layout, List<Spot> spotList, boolean heartVisible, String currentUserId,
                               OnItemClickListener onItemClickListener,
                               OnSpotTimeRequestListener onSpotTimeRequestListener) {
        this.layout = layout;
        this.spotList = spotList;
        this.heartVisible = heartVisible;
        this.currentUserId = currentUserId;
        this.onItemClickListener = onItemClickListener;
        this.onSpotTimeRequestListener = onSpotTimeRequestListener;
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
        viewHolder.getFavoriteCheckbox().setChecked(currentSpot.isFavoritedBy(currentUserId));

        boolean isSunrise = currentSpot.isSunriseSpot();
        viewHolder.getImageViewSpotTimeIcon().setImageResource(
                isSunrise ? R.drawable.ic_sunrise : R.drawable.ic_sunset);
        viewHolder.getTextViewSpotTypeLabel().setText(
                isSunrise ? R.string.sunrise_label : R.string.sunset_label);

        viewHolder.getTextViewSpotTime().setText("--:--");
        if (onSpotTimeRequestListener != null) {
            onSpotTimeRequestListener.onRequestTime(currentSpot, time -> {
                if (viewHolder.getBindingAdapterPosition() == position) {
                    viewHolder.getTextViewSpotTime().setText(time);
                }
            });
        }

        String firstPhoto = (currentSpot.getPhotoUrls() != null && !currentSpot.getPhotoUrls().isEmpty())
                ? currentSpot.getPhotoUrls().get(0)
                : null;

        if (firstPhoto != null && firstPhoto.startsWith("http")) {
            Glide.with(context)
                    .load(firstPhoto)
                    .placeholder(new ColorDrawable(context.getColor(R.color.md_theme_inverseOnSurface)))
                    .into(viewHolder.getImageView());
        } else if (firstPhoto != null) {
            android.graphics.Bitmap bitmap = com.group3boot.sunspot.util.ImageUtil.decodeBase64(firstPhoto);
            if (bitmap != null) {
                viewHolder.getImageView().setImageBitmap(bitmap);
            } else {
                viewHolder.getImageView().setImageDrawable(new ColorDrawable(context.getColor(R.color.md_theme_inverseOnSurface)));
            }
        } else {
            viewHolder.getImageView().setImageDrawable(new ColorDrawable(context.getColor(R.color.md_theme_inverseOnSurface)));
        }

        if (!heartVisible) {
            viewHolder.getFavoriteCheckbox().setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return spotList.size();
    }
}