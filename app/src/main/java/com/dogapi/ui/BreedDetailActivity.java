package com.dogapi.ui;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.dogapi.R;
import com.dogapi.database.DogDatabaseHelper;
import com.google.android.material.progressindicator.LinearProgressIndicator;

public class BreedDetailActivity extends AppCompatActivity {

    private ImageView imageViewDog;
    private TextView textViewBreedName;
    private ImageButton btnFavorite;
    private LinearProgressIndicator progressIndicator;
    private ConstraintLayout detailContainer;

    private boolean isFavorite = false;
    private String currentImageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_breed_detail);

        // Initialize views
        initializeViews();

        // Get image URL from intent
        currentImageUrl = getIntent().getStringExtra("imageUrl");

        // Load and display image
        loadDogImage();

        // Setup listeners
        setupListeners();

        // Apply entrance animations
        applyEntranceAnimations();
    }

    private void initializeViews() {
        imageViewDog = findViewById(R.id.imageViewDog);
        textViewBreedName = findViewById(R.id.textViewBreedName);
        btnFavorite = findViewById(R.id.btnFavorite);
        progressIndicator = findViewById(R.id.progressIndicator);
        detailContainer = findViewById(R.id.detailContainer);
    }

    private void loadDogImage() {
        // Show progress
        progressIndicator.setVisibility(View.VISIBLE);

        // Extract breed name from URL
        String breedName = extractBreedName(currentImageUrl);
        textViewBreedName.setText(breedName);

        // Load image with Glide
        Glide.with(this)
                .load(currentImageUrl)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model,
                                                Target<Drawable> target, boolean isFirstResource) {
                        progressIndicator.setVisibility(View.GONE);
                        Toast.makeText(BreedDetailActivity.this,
                                "Failed to load image", Toast.LENGTH_SHORT).show();
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model,
                                                   Target<Drawable> target,
                                                   DataSource dataSource,
                                                   boolean isFirstResource) {
                        progressIndicator.setVisibility(View.GONE);
                        return false;
                    }
                })
                .into(imageViewDog);
    }

    private void setupListeners() {
        // Favorite button click
        btnFavorite.setOnClickListener(v -> {
            toggleFavoriteWithAnimation();
        });
    }

    private void toggleFavoriteWithAnimation() {
        // Toggle favorite state
        isFavorite = !isFavorite;

        // Create pulse animation
        Animation pulseAnimation = AnimationUtils.loadAnimation(this, R.anim.pulse_animation);

        if (isFavorite) {
            // Change favorite icon to filled
            btnFavorite.setImageResource(R.drawable.ic_favorite_filled);

            // Save to favorites
            saveImageToFavorites();

            // Apply pulse animation
            btnFavorite.startAnimation(pulseAnimation);
        } else {
            // Change favorite icon to outline
            btnFavorite.setImageResource(R.drawable.ic_favorite_border);
        }
    }

    private void saveImageToFavorites() {
        DogDatabaseHelper dbHelper = new DogDatabaseHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("imageUrl", currentImageUrl);

        long newRowId = db.insert("favorites", null, values);

        if (newRowId != -1) {
            showCustomToast("Image added to favorites");
        } else {
            showCustomToast("Failed to add image");
        }

        db.close();
    }

    private void applyEntranceAnimations() {
        // Fade in and slide up animation for the detail container
        Animation entranceAnimation = AnimationUtils.loadAnimation(this, R.anim.entrance_animation);
        detailContainer.startAnimation(entranceAnimation);
    }

    private void showCustomToast(String message) {
        // Custom toast with animation
        Toast toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        View toastView = toast.getView();

        // Optional: Customize toast appearance
        // toastView.setBackgroundResource(R.drawable.custom_toast_background);
        toast.show();
    }

    private String extractBreedName(String url) {
        // Extract breed name from URL
        if (url != null) {
            String[] parts = url.split("/");
            if (parts.length > 4) {
                return parts[4].substring(0, 1).toUpperCase() +
                        parts[4].substring(1).replace("-", " ");
            }
        }
        return "Unknown Breed";
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // Add exit transition
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}