package com.nullsolutions.mlxapp.api

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

interface APIInterface {

    @Headers("Content-Type: application/json")
    @POST("/api/index.php")
    fun register(
        @Body payload: ApiRequest,
    ): Call<RegistrationResponse>

    @Headers("Content-Type: application/json")
    @POST("/api/index.php")
    fun login(
        @Body payload: ApiRequest,
    ): Call<LoginResponse>

    @Headers("Content-Type: application/json")
    @POST("/api/index.php")
    fun forget(
        @Body payload: ApiRequest,
    ): Call<ForgetResponse>

    @Headers("Content-Type: application/json")
    @POST("/api/index.php")
    fun updateProfile(
        @Body payload: ApiRequest,
    ): Call<UpdateResponse>

    @Headers("Content-Type: application/json")
    @POST("/api/index.php")
    fun getBalanceUpdate(
        @Body payload: ApiRequest,
    ): Call<GetBalanceResponse>


    @Headers("Content-Type: application/json")
    @POST("/api/index.php")
    fun getReferralCode(
        @Body payload: ApiRequest,
    ): Call<ReferralResponse>

    @Headers("Content-Type: application/json")
    @POST("/api/index.php")
    fun withdrawRequest(
        @Body payload: ApiRequest,
    ): Call<UpdateResponse>

    @Headers("Content-Type: application/json")
    @POST("/api/index.php")
    fun withdrawRequestLog(
        @Body payload: ApiRequest,
    ): Call<WithdrawResponse>

    @Headers("Content-Type: application/json")
    @POST("/api/index.php")
    fun getLog(
        @Body payload: ApiRequest,
    ): Call<LogResponse>

    @Headers("Content-Type: application/json")
    @POST("/api/index.php")
    fun addLog(
        @Body payload: ApiRequest,
    ): Call<UpdateResponse>
}