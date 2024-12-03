package com.dogapi.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.dogapi.R;
import com.dogapi.adapter.DogImageAdapter;
import com.dogapi.model.DogImage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONException;


import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class MainActivity extends AppCompatActivity implements DogImageAdapter.OnDogImageClickListener {

    private RecyclerView recyclerView;
    private DogImageAdapter dogImageAdapter;
    private Button btnFavorites;
    private final List<DogImage> dogImages = new ArrayList<>();
    private static final String DOG_API_URL = "https://dog.ceo/api/breeds/image/random/10";
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        btnFavorites = findViewById(R.id.btnFavorites);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout); // Initialize SwipeRefreshLayout

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        dogImageAdapter = new DogImageAdapter(
                dogImages,
                this, // Existing click listener
                (view, dogImage, position) -> { // New long-click listener
                    Toast.makeText(MainActivity.this, "Long pressed on: " + dogImage.getUrl(), Toast.LENGTH_SHORT).show();
                }
        );

        recyclerView.setAdapter(dogImageAdapter);

        fetchRandomDogImages();

        btnFavorites.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, FavoritesActivity.class);
            startActivity(intent);
        });

        // Set up the refresh listener
        swipeRefreshLayout.setOnRefreshListener(() -> {
            dogImages.clear(); // Clear the existing dog images
            fetchRandomDogImages(); // Fetch new dog images
        });
    }

    private void fetchRandomDogImages() {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(DOG_API_URL)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> {
                    Toast.makeText(MainActivity.this, "Failed to load images. Please try again.", Toast.LENGTH_SHORT).show();
                    swipeRefreshLayout.setRefreshing(false); // Stop the refresh animation
                });
                Log.e("DogAPI", "Error: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String jsonResponse = response.body().string();
                    parseDogImages(jsonResponse);
                } else {
                    runOnUiThread(() -> {
                        Toast.makeText(MainActivity.this, "Failed to load images. Please try again.", Toast.LENGTH_SHORT).show();
                        swipeRefreshLayout.setRefreshing(false); // Stop the refresh animation
                    });
                }
            }
        });
    }

    private void parseDogImages(String jsonResponse) {
        try {
            JSONArray images = new JSONArray(new org.json.JSONObject(jsonResponse).getString("message"));
            for (int i = 0; i < images.length(); i++) {
                String imageUrl = images.getString(i);
                dogImages.add(new DogImage(imageUrl));
            }
            runOnUiThread(() -> {
                dogImageAdapter.notifyDataSetChanged();
                swipeRefreshLayout.setRefreshing(false); // Stop the refresh animation
            });
        } catch (JSONException e) {
            Log.e("DogAPI", "Error parsing JSON: " + e.getMessage());
            runOnUiThread(() -> swipeRefreshLayout.setRefreshing(false)); // Stop the refresh animation
        }
    }

    @Override
    public void onDogImageClick(DogImage dogImage) {
        Intent intent = new Intent(this, BreedDetailActivity.class);
        intent.putExtra("imageUrl", dogImage.getUrl());
        startActivity(intent);
    }
}
