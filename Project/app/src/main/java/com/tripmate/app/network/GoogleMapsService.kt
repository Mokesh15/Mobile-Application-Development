package com.tripmate.app.network

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface GoogleMapsService {
    @GET("maps/api/geocode/json")
    suspend fun reverseGeocode(
        @Query("latlng") latlng: String,
        @Query("key") apiKey: String
    ): Response<GeocodingResponse>

    @GET("maps/api/place/findplacefromtext/json")
    suspend fun findPlaceFromText(
        @Query("input") input: String,
        @Query("inputtype") inputtype: String = "textquery",
        @Query("fields") fields: String = "name,geometry,place_id,photos",
        @Query("key") apiKey: String
    ): Response<PlacesResponse>
}
