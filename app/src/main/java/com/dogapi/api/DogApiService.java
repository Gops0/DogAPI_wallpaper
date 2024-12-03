package com.dogapi.api;

import com.dogapi.model.DogImage;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface DogApiService {

//    @GET("breeds/image/random/10")
//    Call<List<String>> getAllDogImages();

    @GET("breeds/image/random/10")
    Call<Map<String, Object>> getAllDogImages();


    @GET("breed/{breed}/images")
    Call<Map<String, Object>> getBreedImages(@Path("breed") String breed);


    @GET("v1/images/search")
    Call<List<DogImage>> getDogImages(@Query("limit") int limit);

    @GET("v1/images/search")
    Call<List<DogImage>> searchDogByBreed(@Query("breed_ids") String breedId, @Query("limit") int limit);
}
