package com.rajchandak.copa.helpers;

import com.rajchandak.copa.data.ClipDetails;
import com.rajchandak.copa.data.DeleteResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Interface for all the retrofit restendpoints.
 */
public interface RestEndpoints {

    @GET("clips/{userID}")
    Call<List<ClipDetails>> getClips(@Header("Authorization") String bearerToken, @Path("userID") String userID);

    @POST("addClip")
    Call<Void> sendClip(@Header("Authorization") String bearerToken, @Body ClipDetails clipDetails);

    @FormUrlEncoded
    @HTTP(method = "DELETE", path = "/deleteClip", hasBody = true)
    Call<DeleteResponse> deleteClip(@Header("Authorization") String bearerToken, @Field("_id") String _id, @Field("fromType") String fromType);

    @GET("/logout")
    Call<Void> executeLogout();

}
