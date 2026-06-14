package com.podcastapp.android.data.repository

import com.podcastapp.android.data.local.dao.EpisodeDao
import com.podcastapp.android.data.local.entity.EpisodeEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DownloadRepository @Inject constructor(
    private val dao: EpisodeDao
) {
    fun getAllDownloads(): Flow<List<EpisodeEntity>> =
        dao.getAllDownloads()

    suspend fun download(episode: EpisodeEntity) {
        dao.insertDownload(episode)
    }

    suspend fun deleteDownload(id: String) {
        dao.deleteDownload(id)
    }

    suspend fun isDownloaded(id: String): Boolean =
        dao.isDownloaded(id)
}