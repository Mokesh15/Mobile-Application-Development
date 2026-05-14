package com.tripmate.app.network

import com.google.gson.annotations.SerializedName

data class GeocodingResponse(
    val results: List<GeocodingResult>,
    val status: String
)

data class GeocodingResult(
    @SerializedName("address_components") val addressComponents: List<AddressComponent>,
    val geometry: Geometry
)

data class AddressComponent(
    @SerializedName("long_name") val longName: String,
    val types: List<String>
)

data class Geometry(
    val location: Location
)

data class Location(
    val lat: Double,
    val lng: Double
)

data class PlacesResponse(
    val candidates: List<PlaceCandidate>,
    val status: String
)

data class PlaceCandidate(
    @SerializedName("place_id") val placeId: String,
    val name: String,
    val geometry: Geometry,
    val photos: List<PlacePhoto>?
)

data class PlacePhoto(
    @SerializedName("photo_reference") val photoReference: String
)
