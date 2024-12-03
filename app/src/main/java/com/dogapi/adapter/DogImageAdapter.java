package com.dogapi.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.dogapi.R;
import com.dogapi.model.DogImage;
import java.util.List;

public class DogImageAdapter extends RecyclerView.Adapter<DogImageAdapter.ViewHolder> {

    public interface OnDogImageClickListener {
        void onDogImageClick(DogImage dogImage);
    }

    public interface OnDogImageLongClickListener {
        void onDogImageLongClick(View view, DogImage dogImage, int position);
    }

    private List<DogImage> dogImages;
    private OnDogImageClickListener clickListener;
    private OnDogImageLongClickListener longClickListener;

    public DogImageAdapter(List<DogImage> dogImages, OnDogImageClickListener clickListener, OnDogImageLongClickListener longClickListener) {
        this.dogImages = dogImages;
        this.clickListener = clickListener;
        this.longClickListener = longClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_dog_image, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DogImage dogImage = dogImages.get(position);
        Glide.with(holder.itemView.getContext())
                .load(dogImage.getUrl())
                .placeholder(R.drawable.placeholder_image)
                .into(holder.dogImageView);

        holder.itemView.setOnClickListener(v -> {
            if (clickListener != null) {
                clickListener.onDogImageClick(dogImage);
            }
        });

        holder.itemView.setOnLongClickListener(v -> {
            if (longClickListener != null) {
                longClickListener.onDogImageLongClick(holder.itemView, dogImage, position);
                return true;
            }
            return false;
        });
    }

    @Override
    public int getItemCount() {
        return dogImages.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView dogImageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            dogImageView = itemView.findViewById(R.id.dogImageView);
        }
    }
}
