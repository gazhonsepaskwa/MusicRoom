package be.nalebrun.musicroom.viewmodel;

import be.nalebrun.musicroom.IAPIRepository;
import be.nalebrun.musicroom.repositories.ICredentialRepository;
import be.nalebrun.musicroom.repositories.MusicRepository;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

@ScopeMetadata
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava",
    "cast",
    "deprecation",
    "nullness:initialization.field.uninitialized"
})
public final class PlaylistViewModel_Factory implements Factory<PlaylistViewModel> {
  private final Provider<IAPIRepository> apiRepositoryProvider;

  private final Provider<ICredentialRepository> credentialRepositoryProvider;

  private final Provider<MusicRepository> musicRepositoryProvider;

  private PlaylistViewModel_Factory(Provider<IAPIRepository> apiRepositoryProvider,
      Provider<ICredentialRepository> credentialRepositoryProvider,
      Provider<MusicRepository> musicRepositoryProvider) {
    this.apiRepositoryProvider = apiRepositoryProvider;
    this.credentialRepositoryProvider = credentialRepositoryProvider;
    this.musicRepositoryProvider = musicRepositoryProvider;
  }

  @Override
  public PlaylistViewModel get() {
    return newInstance(apiRepositoryProvider.get(), credentialRepositoryProvider.get(), musicRepositoryProvider.get());
  }

  public static PlaylistViewModel_Factory create(Provider<IAPIRepository> apiRepositoryProvider,
      Provider<ICredentialRepository> credentialRepositoryProvider,
      Provider<MusicRepository> musicRepositoryProvider) {
    return new PlaylistViewModel_Factory(apiRepositoryProvider, credentialRepositoryProvider, musicRepositoryProvider);
  }

  public static PlaylistViewModel newInstance(IAPIRepository apiRepository,
      ICredentialRepository credentialRepository, MusicRepository musicRepository) {
    return new PlaylistViewModel(apiRepository, credentialRepository, musicRepository);
  }
}
