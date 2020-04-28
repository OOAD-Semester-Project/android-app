package com.rajchandak.copa.helpers;

import com.rajchandak.copa.data.ClipDetails;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface RestEndpoints {

    @GET("clips/{userID}")
    Call<List<ClipDetails>> getClips(@Header("Authorization") String bearerToken, @Path("userID") String userID);

    @POST("addClip")
    Call<Void> sendClip(@Header("Authorization") String bearerToken, @Body ClipDetails clipDetails);

}
