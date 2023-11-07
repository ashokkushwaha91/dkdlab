package com.agro.dkdlab.network.base

import com.agro.dkdlab.network.model.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import com.agro.dkdlab.network.model.SoilSampleModel
import retrofit2.Response
import retrofit2.http.*


interface ApiEndPoint {

    @GET("sendDKDOtp")
    suspend fun sendOtp(@Query("mobileNumber") mobileNumber: String,@Query("languageName") languageName: String
    ): Response<LoginModel>

    @POST("verifyDKDOtp")
    suspend fun verifyOTP(@Body body: HashMap<String, String>): Response<UserModel>

    @POST("createUser")
    @JvmSuppressWildcards
    suspend fun createUser(@Body body: Map<String, Any>): Response<UserModel>

    @GET("findUserAddressByUserId/{userId}")
    suspend fun findUserAddressByUserId(@Path("userId") userId: String): Response<List<UserAddressModel>>

    @POST("createUserAddress")
    @JvmSuppressWildcards
    suspend fun createUserAddress(@Body body: Map<String, String>): Response<UserAddressModel>

    @GET("soilSampleByBarCode")
    suspend fun soilSampleByBarCode(@Query("sampleBarCode") barcode: String): Response<SoilSampleModel>
    @PUT("updateSoilSampleByBarCode")
    @JvmSuppressWildcards
    suspend fun updateSoilSampleByBarCode(@Body body: Map<String, Any>): Response<CreateSampleModel>

    @GET("soilHealthCardManual")
    suspend fun storeFertCalcManual(
        @Query("farmSize") farmSize: String,
        @Query("nRangName") nRangName: String,
        @Query("pRangName") pRangName: String,
        @Query("kRangName") kRangName: String,
        @Query("ocRangName") ocRangName: String,
        @Query("phRangName") phRangName: String,
        @Query("language") language: String,
    ): Response<FertCalcManualModel>

    @GET("soilSampleByPrimaryPhoneV1/{primaryPhone}")
    suspend fun soilSampleByPrimaryPhone(@Path("primaryPhone") primaryPhone:String): Response<List<SoilSampleModel>>

    @Multipart
    @POST("createSoilSampleV1")
    @JvmSuppressWildcards
    suspend fun createSoilSample(@Part("soilSampleRequestDTO") soilSampleRequestDTO: RequestBody?,
                           @Part file: ArrayList<MultipartBody.Part>): Response<CreateSampleModel>
    @PUT("updateSampleByIdV1")
    suspend fun updateSampleById(@Body body: HashMap<String, Any>): Response<SoilSampleModel>


    @GET("checkCollectionByKhasraVillageCodeNew")
    suspend fun checkCollectionByKhasraVillageCode(@Query("villageCode") villageCode: String,@Query("khasraNumber") khasraNumber: String): Response<CheckCollectionModel>

    @GET("getCropMasterList")
    suspend fun getCropMasterList(@Query("stateCode") stateCode: String): Response<List<CropModel>>

    @GET("states")
    suspend fun getStates(): Response<List<Address>>

    @GET("districts")
    suspend fun getDistricts(@Query("stateId") stateId: String): Response<List<Address>>

    @GET("tehsils")
    suspend fun getBlock(@Query("districtId") districtId: String): Response<List<Address>>

    //  admin
    @POST("totalSamples/v1")
    suspend fun getSampleCount(@Body body: HashMap<String, String>): Response<SampleCount>

    @POST("villageList/v1")
    suspend fun getVillageList(@Body body: HashMap<String, String>): Response<VillageList>

    @POST("soilSampleAverage/v1")
    suspend fun getSoilSampleAverage(@Body body: HashMap<String, String>): Response<NPKAverageValue>

    @GET("getUserList")
    suspend fun getUserList(@Query("blockCode") villageCode: String): Response<List<UserListModel>>

    @GET("soilSampleByVillageNameV1/{villageName}")
    suspend fun soilSampleByVillageName(@Path("villageName") villageName:String): Response<List<SampleList>>

}