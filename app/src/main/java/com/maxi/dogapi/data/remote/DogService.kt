package com.maxi.dogapi.data.remote

import com.maxi.dogapi.model.DogResponse
import com.maxi.dogapi.utils.Constants
import retrofit2.Response
import retrofit2.http.GET

interface DogService {

    @GET(Constants.RANDOM_URL)
    suspend fun getDog(): Response<DogResponse>
}
