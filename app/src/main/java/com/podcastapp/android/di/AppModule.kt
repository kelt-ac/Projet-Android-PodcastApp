package com.podcastapp.android.di

import com.google.firebase.auth.FirebaseAuth
import com.podcastapp.android.data.remote.PodcastApiService
import com.podcastapp.android.data.repository.PodcastRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    private const val BASE_URL = "https://itunes.apple.com/"

    // ── Firebase ──────────────────────────────────────────
    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth =
        FirebaseAuth.getInstance()

    // ── OkHttp ────────────────────────────────────────────
    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        return OkHttpClient.Builder()
            .addInterceptor(logging)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    // ── Retrofit ──────────────────────────────────────────
    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    // ── API Service ───────────────────────────────────────
    @Provides
    @Singleton
    fun providePodcastApiService(retrofit: Retrofit): PodcastApiService =
        retrofit.create(PodcastApiService::class.java)

    // ── Repository ────────────────────────────────────────
    @Provides
    @Singleton
    fun providePodcastRepository(
        api: PodcastApiService
    ): PodcastRepository = PodcastRepository(api)
}