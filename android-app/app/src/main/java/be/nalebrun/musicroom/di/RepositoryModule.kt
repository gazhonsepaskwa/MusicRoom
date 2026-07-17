package be.nalebrun.musicroom.di

import be.nalebrun.musicroom.APIRepository
import be.nalebrun.musicroom.IAPIRepository
import be.nalebrun.musicroom.repositories.CredentialRepository
import be.nalebrun.musicroom.repositories.ICredentialRepository
import be.nalebrun.musicroom.repositories.IMusicRepository
import be.nalebrun.musicroom.repositories.MusicRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAPIRepository(impl: APIRepository) : IAPIRepository

    @Binds
    @Singleton
    abstract fun bindCredentialRepository(impl: CredentialRepository) : ICredentialRepository

    @Binds
    @Singleton
    abstract fun bindMusicRepository(impl: MusicRepository) : IMusicRepository

}