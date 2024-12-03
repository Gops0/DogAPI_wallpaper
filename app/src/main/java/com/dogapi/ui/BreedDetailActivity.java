package com.dogapi.ui;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.dogapi.R;
import com.dogapi.database.DogDatabaseHelper;

public class BreedDetailActivity extends AppCompatActivity {

    private ImageView breedImageView;
    private ImageView heartButton;
    private String imageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_breed_detail);

        breedImageView = findViewById(R.id.breedImageView);
        heartButton = findViewById(R.id.heartButton);

        imageUrl = getIntent().getStringExtra("imageUrl");

        Glide.with(this)
                .load(imageUrl)
                .placeholder(R.drawable.placeholder_image)
                .into(breedImageView);

        heartButton.setOnClickListener(v -> saveImageToFavorites());
    }

    private void saveImageToFavorites() {
        DogDatabaseHelper dbHelper = new DogDatabaseHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("imageUrl", imageUrl);

        long newRowId = db.insert("favorites", null, values);

        if (newRowId != -1) {
            Toast.makeText(this, "Image added to favorites", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Failed to add image", Toast.LENGTH_SHORT).show();
        }

        db.close();
    }
}
