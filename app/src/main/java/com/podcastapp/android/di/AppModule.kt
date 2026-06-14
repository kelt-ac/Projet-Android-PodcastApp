package com.podcastapp.android.di

import android.content.Context
import androidx.room.Room
import com.google.firebase.auth.FirebaseAuth
import com.podcastapp.android.data.local.PodcastDatabase
import com.podcastapp.android.data.local.dao.EpisodeDao
import com.podcastapp.android.data.local.dao.PodcastDao
import com.podcastapp.android.data.remote.PodcastIndexApiService
import com.podcastapp.android.data.repository.DownloadRepository
import com.podcastapp.android.data.repository.PodcastIndexRepository
import com.podcastapp.android.data.repository.SubscriptionRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.security.MessageDigest
import java.time.Instant
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    private const val PODCAST_INDEX_API_KEY    = "9HLJ3QSWRAUBAPAAVJUW"
    private const val PODCAST_INDEX_API_SECRET = "hq\$updFAuC^BXCUBd3qPXrk2CGBFx43V\$bkLXQF6"
    private const val PODCAST_INDEX_BASE_URL   = "https://api.podcastindex.org/api/1.0/"

    // ── Firebase ──────────────────────────────────────────
    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth =
        FirebaseAuth.getInstance()

    // ── OkHttp Podcast Index ──────────────────────────────
    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        return OkHttpClient.Builder()
            .addInterceptor { chain ->
                val epoch   = Instant.now().epochSecond.toString()
                val hash    = sha1("$PODCAST_INDEX_API_KEY$PODCAST_INDEX_API_SECRET$epoch")
                val request = chain.request().newBuilder()
                    .addHeader("X-Auth-Key",    PODCAST_INDEX_API_KEY)
                    .addHeader("X-Auth-Date",   epoch)
                    .addHeader("Authorization", hash)
                    .addHeader("User-Agent",    "PodcastApp/1.0")
                    .build()
                chain.proceed(request)
            }
            .addInterceptor(logging)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    // ── Retrofit Podcast Index ────────────────────────────
    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl(PODCAST_INDEX_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    // ── API Service ───────────────────────────────────────
    @Provides
    @Singleton
    fun providePodcastIndexApiService(retrofit: Retrofit): PodcastIndexApiService =
        retrofit.create(PodcastIndexApiService::class.java)

    // ── Repository Podcast Index ──────────────────────────
    @Provides
    @Singleton
    fun providePodcastIndexRepository(
        api: PodcastIndexApiService
    ): PodcastIndexRepository = PodcastIndexRepository(api)

    // ── Room Database ─────────────────────────────────────
    @Provides
    @Singleton
    fun providePodcastDatabase(
        @ApplicationContext context: Context
    ): PodcastDatabase = Room.databaseBuilder(
        context,
        PodcastDatabase::class.java,
        "podcast_database"
    )
        .fallbackToDestructiveMigration()
        .build()

    @Provides
    @Singleton
    fun providePodcastDao(database: PodcastDatabase): PodcastDao =
        database.podcastDao()

    @Provides
    @Singleton
    fun provideEpisodeDao(database: PodcastDatabase): EpisodeDao =
        database.episodeDao()

    @Provides
    @Singleton
    fun provideSubscriptionRepository(
        dao: PodcastDao
    ): SubscriptionRepository = SubscriptionRepository(dao)

    @Provides
    @Singleton
    fun provideDownloadRepository(
        dao: EpisodeDao
    ): DownloadRepository = DownloadRepository(dao)

    // ── SHA-1 Helper ──────────────────────────────────────
    private fun sha1(input: String): String {
        val digest = MessageDigest.getInstance("SHA-1")
        val result = digest.digest(input.toByteArray())
        return result.joinToString("") { "%02x".format(it) }
    }
}