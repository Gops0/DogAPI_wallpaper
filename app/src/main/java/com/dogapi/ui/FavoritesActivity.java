package com.dogapi.ui;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.Toast;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dogapi.R;
import com.dogapi.adapter.DogImageAdapter;
import com.dogapi.database.DogDatabaseHelper;
import com.dogapi.model.DogImage;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.progressindicator.CircularProgressIndicator;

import android.app.WallpaperManager;
import android.graphics.Bitmap;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import java.io.IOException;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FavoritesActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private DogImageAdapter adapter;
    private List<DogImage> favoriteDogs;
    private DogDatabaseHelper dbHelper;
    private LinearLayout emptyStateLayout;
    private MaterialToolbar toolbarFavorites;
    private CircularProgressIndicator progressIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        // Initialize views
        initializeViews();

        // Setup Toolbar
        setupToolbar();

        // Setup RecyclerView
        setupRecyclerView();

        // Load favorites
        loadFavorites();
    }

    private void initializeViews() {
        recyclerView = findViewById(R.id.recyclerViewFavorites);
        emptyStateLayout = findViewById(R.id.emptyStateLayout);
        toolbarFavorites = findViewById(R.id.toolbarFavorites);
        progressIndicator = findViewById(R.id.progressIndicatorFavorites);
        dbHelper = new DogDatabaseHelper(this);
    }

    private void setupToolbar() {
        toolbarFavorites.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbarFavorites.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void setupRecyclerView() {
        // Use GridLayoutManager with 2 columns
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        favoriteDogs = new ArrayList<>();
        adapter = new DogImageAdapter(favoriteDogs, null, this::onDogImageLongPress);
        recyclerView.setAdapter(adapter);
    }

    private void loadFavorites()
    {
        progressIndicator.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        emptyStateLayout.setVisibility(View.GONE);

        // Clear previous data
        favoriteDogs.clear();

        new Thread(() -> {
            try {
                SQLiteDatabase db = dbHelper.getReadableDatabase();
                Cursor cursor = db.query("favorites",
                        new String[]{"imageUrl"},
                        null, null, null, null, null);

                // Use a Set to track unique URLs
                Set<String> uniqueUrls = new HashSet<>();

                while (cursor.moveToNext()) {
                    int urlColumnIndex = cursor.getColumnIndex("imageUrl");
                    if (urlColumnIndex != -1) {
                        String imageUrl = cursor.getString(urlColumnIndex);

                        // Add only if URL is unique
                        if (imageUrl != null && !imageUrl.isEmpty() && !uniqueUrls.contains(imageUrl)) {
                            uniqueUrls.add(imageUrl);
                            favoriteDogs.add(new DogImage(imageUrl));
                        }
                    }
                }
                cursor.close();
                db.close();

                runOnUiThread(() -> {
                    progressIndicator.setVisibility(View.GONE);

                    if (favoriteDogs.isEmpty()) {
                        recyclerView.setVisibility(View.GONE);
                        emptyStateLayout.setVisibility(View.VISIBLE);
                    } else {
                        adapter.notifyDataSetChanged();
                        recyclerView.setVisibility(View.VISIBLE);
                        emptyStateLayout.setVisibility(View.GONE);
                    }
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    progressIndicator.setVisibility(View.GONE);
                    Toast.makeText(this, "Error loading favorites: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        }).start();
    }

    private void onDogImageLongPress(View view, DogImage dogImage, int position) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        popupMenu.inflate(R.menu.favorites_popup_menu);

        popupMenu.setOnMenuItemClickListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.action_remove_favorite) {
                removeFavorite(dogImage, position);
                return true;
            } else if (itemId == R.id.action_set_wallpaper) {
                setWallpaper(dogImage);
                return true;
            }
            return false;
        });

        popupMenu.show();
    }

    private void setWallpaper(DogImage dogImage) {
        try {
            Glide.with(this)
                    .asBitmap()
                    .load(dogImage.getUrl())
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            try {
                                WallpaperManager wallpaperManager = WallpaperManager.getInstance(FavoritesActivity.this);
                                wallpaperManager.setBitmap(resource);
                                Toast.makeText(FavoritesActivity.this, "Wallpaper set successfully!", Toast.LENGTH_SHORT).show();
                            } catch (IOException e) {
                                Toast.makeText(FavoritesActivity.this, "Failed to set wallpaper", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        } catch (Exception e) {
            Toast.makeText(this, "Error loading image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void removeFavorite(DogImage dogImage, int position) {
        try {
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            int rowsDeleted = db.delete("favorites", "imageUrl = ?", new String[]{dogImage.getUrl()});
            db.close();

            if (rowsDeleted > 0) {
                favoriteDogs.remove(position);
                adapter.notifyItemRemoved(position);

                // Update UI
                if (favoriteDogs.isEmpty()) {
                    recyclerView.setVisibility(View.GONE);
                    emptyStateLayout.setVisibility(View.VISIBLE);
                }

                Toast.makeText(this, "Removed from favorites", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Failed to remove favorite", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error removing favorite: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}