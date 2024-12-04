package com.dogapi.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.AnimationUtils;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.dogapi.R;
import com.dogapi.adapter.DogImageAdapter;
import com.dogapi.model.DogImage;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

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
import org.json.JSONObject;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class MainActivity extends AppCompatActivity implements DogImageAdapter.OnDogImageClickListener {

    private RecyclerView recyclerView;
    private DogImageAdapter dogImageAdapter;
    private FloatingActionButton fabFavorites;
    private final List<DogImage> dogImages = new ArrayList<>();
    private static final String DOG_API_URL = "https://dog.ceo/api/breeds/image/random/20";
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize views
        initializeViews();

        // Setup RecyclerView
        setupRecyclerView();

        // Fetch initial dog images
        fetchRandomDogImages();

        // Setup listeners
        setupListeners();
    }

    private void initializeViews() {
        recyclerView = findViewById(R.id.recyclerView);
        fabFavorites = findViewById(R.id.fabFavorites);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);

        // Apply entrance animation to RecyclerView
        recyclerView.setAnimation(AnimationUtils.loadAnimation(this, R.anim.recycler_view_entrance));
    }

    private void setupRecyclerView() {
        // Use GridLayoutManager for a more interesting layout
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(layoutManager);

        dogImageAdapter = new DogImageAdapter(
                dogImages,
                this, // Click listener
                this::onDogImageLongPress // Long-press listener
        );

        recyclerView.setAdapter(dogImageAdapter);
    }

    private void setupListeners() {
        // Swipe refresh listener
        swipeRefreshLayout.setOnRefreshListener(this::refreshDogImages);

        // Favorites FAB listener
        fabFavorites.setOnClickListener(v -> {
            // Add scale animation to FAB
            fabFavorites.animate()
                    .scaleX(0.7f)
                    .scaleY(0.7f)
                    .setDuration(100)
                    .withEndAction(() -> {
                        fabFavorites.animate()
                                .scaleX(1f)
                                .scaleY(1f)
                                .setDuration(100)
                                .start();
                        startActivity(new Intent(this, FavoritesActivity.class));
                    })
                    .start();
        });
    }

    private void refreshDogImages() {
        dogImages.clear();
        fetchRandomDogImages();
    }

    private void fetchRandomDogImages() {
        swipeRefreshLayout.setRefreshing(true);

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(DOG_API_URL)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> {
                    handleFetchError(e);
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        String jsonResponse = response.body().string();
                        parseDogImages(jsonResponse);
                    } catch (Exception e) {
                        runOnUiThread(() -> handleFetchError(e));
                    }
                } else {
                    runOnUiThread(() -> handleFetchError(new Exception("Network error")));
                }
            }
        });
    }

    private void parseDogImages(String jsonResponse) {
        try {
            JSONObject jsonObject = new JSONObject(jsonResponse);
            JSONArray images = jsonObject.getJSONArray("message");

            for (int i = 0; i < images.length(); i++) {
                String imageUrl = images.getString(i);
                dogImages.add(new DogImage(imageUrl));
            }

            runOnUiThread(() -> {
                dogImageAdapter.notifyDataSetChanged();
                swipeRefreshLayout.setRefreshing(false);

                // Animate RecyclerView
                recyclerView.scheduleLayoutAnimation();
            });
        } catch (JSONException e) {
            runOnUiThread(() -> handleFetchError(e));
        }
    }

    private void handleFetchError(Throwable e) {
        Log.e("DogAPI", "Error: " + e.getMessage());
        swipeRefreshLayout.setRefreshing(false);
        Toast.makeText(this, "Failed to load images. Please try again.", Toast.LENGTH_SHORT).show();
    }

    private void onDogImageLongPress(android.view.View view, DogImage dogImage, int position) {
        // Add a subtle animation on long press
        view.animate()
                .scaleX(0.9f)
                .scaleY(0.9f)
                .setDuration(100)
                .withEndAction(() -> {
                    view.animate()
                            .scaleX(1f)
                            .scaleY(1f)
                            .setDuration(100)
                            .start();
                })
                .start();
    }

    @Override
    public void onDogImageClick(DogImage dogImage) {
        Intent intent = new Intent(this, BreedDetailActivity.class);
        intent.putExtra("imageUrl", dogImage.getUrl());
        startActivity(intent);

        // Add transition animation
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }
}