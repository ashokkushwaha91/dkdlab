package com.agro.dkdlab.network.base

import android.content.Context
import androidx.room.Room
import com.agro.dkdlab.app.MyApp
import com.agro.dkdlab.network.config.UrlConfig
import com.agro.dkdlab.network.tool.ErrorInterceptor
import com.agro.dkdlab.ui.database.room.AppDatabase
import com.agro.dkdlab.ui.database.daos.ReportDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Singleton
    @Provides
    fun getOkHttpClient(errorInterceptor: ErrorInterceptor, logging: HttpLoggingInterceptor): OkHttpClient {
//    fun getOkHttpClient(logging: HttpLoggingInterceptor): OkHttpClient {
        return OkHttpClient
            .Builder()
            .addInterceptor(errorInterceptor)
            .addInterceptor(logging)
            .addInterceptor { chain ->
//                val authToken = App.get().getAuthToken()
                val original = chain.request()
                val requestBuilder = original.newBuilder()
                    .method(original.method, original.body)
//                    .addHeader("Authorization", "Bearer $authToken")
//                    .addHeader("X-DeviceId", App.get().getDeviceId())
                val request = requestBuilder.build()
                chain.proceed(request)
            }.connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS).build()
    }

    @Singleton
    @Provides
    fun getGsonFactory(): GsonConverterFactory = GsonConverterFactory.create()

    @Singleton
    @Provides
    fun getAppSession(@ApplicationContext appContext: Context): MyApp = MyApp()

    @Singleton
    @Provides
    fun getRetroClient(
        okHttpClient: OkHttpClient,
        gsonConverterFactory: GsonConverterFactory
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(UrlConfig.get(UrlConfig.PRODUCTION).BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(gsonConverterFactory)
            .build()
    }

    @Singleton
    @Provides
    fun getLogger(): HttpLoggingInterceptor{
        val logging  = HttpLoggingInterceptor()
        logging .setLevel(HttpLoggingInterceptor.Level.BASIC)
        return logging
    }

    @Singleton
    @Provides
    fun getErrorInterceptor() = ErrorInterceptor()

    @Singleton
    @Provides
    fun getApiService(retrofit: Retrofit): ApiEndPoint = retrofit.create(ApiEndPoint::class.java)


    @Singleton // Tell Dagger-Hilt to create a singleton accessible everywhere in ApplicationCompenent (i.e. everywhere in the application)
    @Provides
    fun provideYourDatabase(@ApplicationContext app: Context) = Room.databaseBuilder(app,
        AppDatabase::class.java,
        "userDB"
    ).build() // The reason we can construct a database for the repo


    @Provides
    @Singleton
    fun provideUserDao(database: AppDatabase): ReportDao =
        database.userDao()

}