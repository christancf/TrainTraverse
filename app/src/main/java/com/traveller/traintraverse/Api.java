package com.traveller.traintraverse;

import com.traveller.models.AuthRequest;
import com.traveller.models.AuthResponse;
import com.traveller.models.Booking;
import com.traveller.models.BookingRequest;
import com.traveller.models.ChangePasswordRequest;
import com.traveller.models.DeactivateRequest;
import com.traveller.models.GetAllTrainsResponse;
import com.traveller.models.Traveler;
import com.traveller.models.TravellerRegRequest;
import com.traveller.models.UpdateUserRequest;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface Api {
    public static String BASE_URL = "https://api.eadbe.dev.dehemi.com/api/";
    @GET("trains")
    Call<GetAllTrainsResponse> getTrains(@Query("departure") String departure, @Query("arrival") String arrival);

    /**
     * Reference - https://stackoverflow.com/questions/33815882/retrofit-using-more-than-one-variable-in-path
     * @param id The traveller id
     * @param withTrains Should the API resolve the train details
     * @return List of bookings
     */
    @GET("traveller/{id}/bookings")
    Call<List<Booking>> getBookings(@Path("id") String id, @Query("trains") Boolean withTrains);

    @GET("booking/{id}")
    Call<Map<String, Booking>> getBookingById(@Path("id") String id, @Query("trains") Boolean withTrains);

    @POST("booking")
    Call<Booking> bookTrain(@Body BookingRequest body);

    @POST("auth/login")
    Call<AuthResponse> login(@Body AuthRequest body);

    @POST("traveller")
    Call<Traveler> register(@Body TravellerRegRequest body);

    @POST("traveller/{id}/bookings/{bid}/cancel")
    Call<Void> cancelBooking(@Path("id") String id, @Path("bid") String bid);

    @PATCH("traveller/{id}")
    Call<Void> updateUser(@Path("id") String id, @Body UpdateUserRequest update);

    @POST("traveller/deactivate")
    Call<Void> deactivate(@Body DeactivateRequest deactivateRequest);

    @POST("traveller/changePass")
    Call<Void> changePassword(@Body ChangePasswordRequest changePassword);
}
