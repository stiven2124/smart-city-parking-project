package com.example.smart_city;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ApiService {
    @GET("/api/getSensorStatus")
    Call<List<ParkingSlots>> getParkingSlots();

    @POST("api/register")
    Call<AuthResponse> registerUser(@Body User user);

    @POST("api/login")
    Call<AuthResponse> loginUser(@Body User user);
}

