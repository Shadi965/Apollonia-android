package songbird.apollo.data.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import songbird.apollo.data.repository.AlbumRepositoryImpl
import songbird.apollo.data.repository.PlaylistRepositoryImpl
import songbird.apollo.data.repository.SongRepositoryImpl
import songbird.apollo.data.repository.SyncRepositoryImpl
import songbird.apollo.domain.repository.AlbumRepository
import songbird.apollo.domain.repository.PlaylistRepository
import songbird.apollo.domain.repository.SongRepository
import songbird.apollo.domain.repository.SyncRepository

@Module
@InstallIn(SingletonComponent::class)
interface RepositoryModule {

    @Binds
    fun bindSongRepository(
        impl: SongRepositoryImpl
    ): SongRepository

    @Binds
    fun bindAlbumRepository(
        impl: AlbumRepositoryImpl
    ): AlbumRepository

    @Binds
    fun bindPlaylistRepository(
        impl: PlaylistRepositoryImpl
    ): PlaylistRepository

    @Binds
    fun bindSyncRepository(
        impl: SyncRepositoryImpl
    ): SyncRepository


}