package com.dogapi.ui;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.dogapi.R;
import com.dogapi.adapter.DogImageAdapter;
import com.dogapi.database.DogDatabaseHelper;
import com.dogapi.model.DogImage;
import java.util.ArrayList;
import java.util.List;

import android.app.WallpaperManager;
import android.graphics.Bitmap;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import java.io.IOException;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


public class FavoritesActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private DogImageAdapter adapter;
    private List<DogImage> favoriteDogs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        favoriteDogs = loadFavorites();
        adapter = new DogImageAdapter(favoriteDogs, null, this::onDogImageLongPress);
        recyclerView.setAdapter(adapter);
    }

    private List<DogImage> loadFavorites() {
        List<DogImage> favorites = new ArrayList<>();

        DogDatabaseHelper dbHelper = new DogDatabaseHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.query("favorites", new String[]{"imageUrl"}, null, null, null, null, null);
        while (cursor.moveToNext()) {
            String imageUrl = cursor.getString(cursor.getColumnIndexOrThrow("imageUrl"));
            favorites.add(new DogImage(imageUrl));
        }
        cursor.close();
        db.close();

        return favorites;
    }

    private void onDogImageLongPress(View view, DogImage dogImage, int position) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        popupMenu.inflate(R.menu.favorites_popup_menu);

        popupMenu.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_remove_favorite) {
                removeFavorite(dogImage, position);
                return true;
            } else if (item.getItemId() == R.id.action_set_wallpaper) {
                setWallpaper(dogImage);
                return true;
            } else if (item.getItemId() == R.id.action_cancel) {
                return true; // Do nothing, dismiss menu
            }
            return false;
        });

        popupMenu.show();
    }

    private void setWallpaper(DogImage dogImage) {
        try {
            // Get the image URL from the DogImage object
            String imageUrl = dogImage.getUrl();

            // Use Glide to load the image into a Bitmap
            Glide.with(this)
                    .asBitmap()
                    .load(imageUrl)
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            // Set the image as wallpaper
                            WallpaperManager wallpaperManager = WallpaperManager.getInstance(FavoritesActivity.this);
                            try {
                                wallpaperManager.setBitmap(resource);
                                Toast.makeText(FavoritesActivity.this, "Wallpaper set successfully!", Toast.LENGTH_SHORT).show();
                            } catch (IOException e) {
                                Toast.makeText(FavoritesActivity.this, "Failed to set wallpaper", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        } catch (Exception e) {
            Toast.makeText(this, "Error setting wallpaper: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }



    private void removeFavorite(DogImage dogImage, int position) {
        DogDatabaseHelper dbHelper = new DogDatabaseHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        int rowsDeleted = db.delete("favorites", "imageUrl = ?", new String[]{dogImage.getUrl()});
        db.close();

        if (rowsDeleted > 0) {
            favoriteDogs.remove(position);
            adapter.notifyItemRemoved(position);
            Toast.makeText(this, "Removed from favorites", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Failed to remove favorite", Toast.LENGTH_SHORT).show();
        }
    }
}
