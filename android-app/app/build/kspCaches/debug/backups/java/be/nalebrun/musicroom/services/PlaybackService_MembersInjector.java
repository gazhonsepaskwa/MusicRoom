package be.nalebrun.musicroom.services;

import be.nalebrun.musicroom.repositories.ICredentialRepository;
import be.nalebrun.musicroom.repositories.IMusicRepository;
import dagger.MembersInjector;
import dagger.internal.DaggerGenerated;
import dagger.internal.InjectedFieldSignature;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import javax.annotation.processing.Generated;

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
public final class PlaybackService_MembersInjector implements MembersInjector<PlaybackService> {
  private final Provider<IMusicRepository> musicRepositoryProvider;

  private final Provider<ICredentialRepository> credentialRepositoryProvider;

  private PlaybackService_MembersInjector(Provider<IMusicRepository> musicRepositoryProvider,
      Provider<ICredentialRepository> credentialRepositoryProvider) {
    this.musicRepositoryProvider = musicRepositoryProvider;
    this.credentialRepositoryProvider = credentialRepositoryProvider;
  }

  @Override
  public void injectMembers(PlaybackService instance) {
    injectMusicRepository(instance, musicRepositoryProvider.get());
    injectCredentialRepository(instance, credentialRepositoryProvider.get());
  }

  public static MembersInjector<PlaybackService> create(
      Provider<IMusicRepository> musicRepositoryProvider,
      Provider<ICredentialRepository> credentialRepositoryProvider) {
    return new PlaybackService_MembersInjector(musicRepositoryProvider, credentialRepositoryProvider);
  }

  @InjectedFieldSignature("be.nalebrun.musicroom.services.PlaybackService.musicRepository")
  public static void injectMusicRepository(PlaybackService instance,
      IMusicRepository musicRepository) {
    instance.musicRepository = musicRepository;
  }

  @InjectedFieldSignature("be.nalebrun.musicroom.services.PlaybackService.credentialRepository")
  public static void injectCredentialRepository(PlaybackService instance,
      ICredentialRepository credentialRepository) {
    instance.credentialRepository = credentialRepository;
  }
}
